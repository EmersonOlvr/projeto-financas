package com.jolteam.financas.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.dao.LogDAO;
import com.jolteam.financas.enums.Provedor;
import com.jolteam.financas.enums.TipoCodigo;
import com.jolteam.financas.enums.TipoLog;
import com.jolteam.financas.exceptions.UsuarioDesativadoException;
import com.jolteam.financas.exceptions.UsuarioInexistenteException;
import com.jolteam.financas.exceptions.UsuarioInvalidoException;
import com.jolteam.financas.model.Codigo;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CodigoService;
import com.jolteam.financas.service.UsuarioService;
import com.jolteam.financas.util.Util;

@Controller
public class IndexController {

	@Autowired private LogDAO logs;
	@Autowired private UsuarioService usuarioService;
	@Autowired private CodigoService codigoService;

	@GetMapping("/")
	public String viewIndex() {
		return "index";
	}

	@GetMapping("/cadastrar")
	public ModelAndView viewCadastrar() {
		ModelAndView mv = new ModelAndView("cadastrar");
		mv.addObject("usuario", new Usuario());

		return mv;
	}

	@PostMapping("/cadastrar")
	public ModelAndView cadastrarUsuario(HttpServletRequest request, Model model, @ModelAttribute Usuario usuario,
			BindingResult result) {
		usuario.setRegistroData(LocalDateTime.now());
		usuario.setRegistroIp(request.getRemoteAddr());

		try {
			// valida o usuário
			usuarioService.validar(usuario);

			// salva o usuário no banco de dados
			Usuario usuarioSalvo = this.usuarioService.save(usuario);
			
			// adiciona algumas categorias para este usuário recém cadastrado
			this.usuarioService.adicionarCategorias(usuarioSalvo);

			// envia o código de ativação para o e-mail do usuário
			this.usuarioService.enviarCodigoAtivacao(usuarioSalvo, request);

			// adiciona a mensagem de sucesso na view
			model.addAttribute("msgSucesso", "Cadastrado! Verifique seu e-mail para ativar sua conta.");

			// adiciona um novo objeto Usuario na variável 'usuario' que será jogada de volta no formulário
			usuario = new Usuario();
		} catch (UsuarioInvalidoException e) {
			result.addError(new ObjectError("erroValidacao", e.getMessage()));
		}

		return new ModelAndView("cadastrar").addObject("usuario", usuario);
	}

	@GetMapping("/entrar")
	public ModelAndView viewEntrar(@RequestParam(required = false) String erro, RedirectAttributes ra) {
		ModelAndView mv = new ModelAndView("entrar");
		
		if (erro != null) {
			mv.setViewName("redirect:/entrar");
			
			if (erro.equals("email_em_uso")) {
				ra.addFlashAttribute("msgErro", "Este e-mail já está em uso.");
			} else if (erro.equals("authorization_request_not_found")) {
				ra.addFlashAttribute("msgErro", "Erro ao entrar. Por favor, tente novamente.");
			} else if (erro.equals("email_inexistente")) {
				ra.addFlashAttribute("msgErro", "E-mail não encontrado em nossa base de dados.");
			} else if (erro.equals("access_denied")) {
				ra.addFlashAttribute("msgErro", "Por favor, aceite as permissões necessárias.");
			} else if (erro.equals("nome_vazio_github")) {
				ra.addFlashAttribute("msgErro", "Parece que você não tem um nome definido no GitHub. Por favor, defina um antes de continuar.");
			} else if (erro.equals("provedor_invalido_google")) {
				ra.addFlashAttribute("msgErro", "Aparentemente você está cadastrado com a conta do Google. Por favor, use sua conta do Google para entrar.");
			} else if (erro.equals("provedor_invalido_facebook")) {
				ra.addFlashAttribute("msgErro", "Aparentemente você está cadastrado com a conta do Facebook. Por favor, use sua conta do Facebook para entrar.");
			} else if (erro.equals("provedor_invalido_github")) {
				ra.addFlashAttribute("msgErro", "Aparentemente você está cadastrado com a conta do GitHub. Por favor, use sua conta do GitHub para entrar.");
			} else {
				ra.addFlashAttribute("msgErro", "Desculpe, algo deu errado. Tente novamente.");
			}
		}
		
		return mv;
	}

	@PostMapping("/entrar")
	public String autenticarUsuario(Model model, @RequestParam String email, @RequestParam String senha, 
			@RequestParam(required = false) String destino, HttpServletRequest request, HttpSession session) {
		try {
			Usuario usuario = this.usuarioService.entrar(email, senha);

			// salva o usuário na sessão
			session.setAttribute("usuarioLogado", usuario);

			// salva um log de login no banco
			this.logs.save(new Log(usuario, TipoLog.LOGIN, LocalDateTime.now(), request.getRemoteAddr()));
			
			if (!Strings.isBlank(destino)) {
				System.out.println("Redirecionando para "+destino+"...");
				return "redirect:"+destino;
			} else {
				return "redirect:/home";
			}
		} catch (UsuarioInexistenteException e) {
			model.addAttribute("msgErro", e.getMessage());
		} catch (UsuarioInvalidoException e) {
			model.addAttribute("msgErro", e.getMessage());
		} catch (UsuarioDesativadoException e) {
			session.setAttribute("usuarioId", this.usuarioService.findByEmail(email).get().getId());
			model.addAttribute("isDesativado", true);
		}

		return "entrar";
	}
	
	// ====== Ativação da Conta ====== //
	@PostMapping("/reenviar-link-ativacao")
	public String reenviarLinkAtivacao(HttpSession session, HttpServletRequest request, RedirectAttributes ra) {
		Integer id = (Integer) session.getAttribute("usuarioId");
		if (id != null) {
			Optional<Usuario> usuario = this.usuarioService.findById(id);
			if (usuario.isPresent() && !usuario.get().isAtivado()) {
				this.usuarioService.enviarCodigoAtivacao(usuario.get(), request);
				ra.addFlashAttribute("msgSucesso", "Link de ativação reenviado para o seu e-mail.");
				return "redirect:/entrar";
			} else {
				return "redirect:/";
			}
		} else {
			return "redirect:/";
		}
	}
	
	@GetMapping("/ativarConta")
	public String ativarConta(@RequestParam(required = false) Integer id, @RequestParam(required = false) String codigo,
			Model model, HttpSession session) 
	{
		if (id == null || Strings.isEmpty(codigo)) {
			return "redirect:/";
		} else if (this.codigoService.isCodigoValido(id, codigo, TipoCodigo.ATIVACAO_CONTA)) {
			Usuario usuario = this.usuarioService.findById(id).get();
			Codigo codigoExistente = this.codigoService.findByUsuarioAndTipo(usuario, TipoCodigo.ATIVACAO_CONTA).get();
			
			// ativa a conta do usuário e atualiza no banco
			usuario.setAtivado(true);
			this.usuarioService.save(usuario);
			
			// exclui o código que foi usado do banco
			this.codigoService.delete(codigoExistente);
			
			// excluir o id do usuário da sessão
			// (estava sendo usado para o sistema saber para qual usuário reenviar o codigo)
			session.removeAttribute("usuarioId");
			
			model.addAttribute("sucesso", true);
		} else {
			model.addAttribute("msgErro", "Este link é inválido ou já foi usado antes.");
		}
		
		return "ativacao-conta";
	}

	
	// ====== Recuperação de Senha ====== //
	@GetMapping("/recuperar-senha")
	public String viewRecuperarSenha() {
		return "recuperar-senha";
	}

	@PostMapping("recuperar-senha")
	public ModelAndView recuperarSenha(@RequestParam String email, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("recuperar-senha");
		
		if (Strings.isBlank(email) || !Util.isEmailValido(email)) {
			return mv.addObject("msgErro", "Insira um e-mail em formato válido.");
		} else {
			Optional<Usuario> usuario = this.usuarioService.findByEmail(email);
			if (usuario.isPresent()) {
				if (usuario.get().getProvedor() == Provedor.LOCAL) {
					this.usuarioService.enviarLinkRedefinicaoSenha(usuario.get(), request);
				} else {
					return mv.addObject("msgErro", "Apenas senhas de contas locais podem ser recuperadas.");
				}
			}
			return new ModelAndView("recuperar-senha-2").addObject("email", email);
		}
	}

	@GetMapping("/redefinirSenha")
	public String viewRedefinirSenha(@RequestParam(required = false) Integer id, @RequestParam(required = false) String codigo,
			Model model) 
	{
		if (id == null || Strings.isEmpty(codigo)) {
			return "redirect:/";
		} else if (!this.codigoService.isCodigoValido(id, codigo, TipoCodigo.REDEFINICAO_SENHA)) {
			model.addAttribute("msgErro", "Este link é inválido ou já foi usado antes.");
		}
		
		return "redefinir-senha";
	}
	
	@PostMapping("/redefinirSenha")
	public String redefinirSenha(@RequestParam(required = false) Integer id, @RequestParam(required = false) String codigo, 
					@RequestParam(required = false) String novaSenha, @RequestParam(required = false) String novaSenhaRepetida, 
					Model model) 
	{
		if (id == null || Strings.isEmpty(codigo)) {
			return "redirect:/";
		} else if (Strings.isEmpty(novaSenha)) {
			model.addAttribute("msgErroCampos", "Insira a nova senha.");
		} else if (novaSenha.length() < 6) {
			model.addAttribute("msgErroCampos", "Senha muito curta. Mínimo de 6 caracteres.");
		} else if (novaSenha.length() > 255) {
			model.addAttribute("Senha muito grande. Máximo de 255 caracteres.");
		} else if (Strings.isEmpty(novaSenhaRepetida)) {
			model.addAttribute("msgErroCampos", "Por favor, repita a senha.");
		} else if (!novaSenhaRepetida.equals(novaSenha)) {
			model.addAttribute("msgErroCampos", "As senhas não conferem.");
		} else {
			if (this.codigoService.isCodigoValido(id, codigo, TipoCodigo.REDEFINICAO_SENHA)) {
				Usuario usuario = this.usuarioService.findById(id).get();
				Codigo codigoExistente = this.codigoService.findByUsuarioAndTipo(usuario, TipoCodigo.REDEFINICAO_SENHA).get();
				
				// define a nova senha do usuário e atualiza no banco
				usuario.setSenha(this.usuarioService.criptografarSenha(novaSenha));
				this.usuarioService.save(usuario);
				
				// exclui o código que foi usado do banco
				this.codigoService.delete(codigoExistente);
				
				model.addAttribute("sucesso", true);
			} else {
				model.addAttribute("msgErro", "Este link é inválido ou já foi usado antes.");
			}
		}
		
		return "redefinir-senha";
	}

}
