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
import com.jolteam.financas.enums.TiposCodigos;
import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.exceptions.UsuarioDesativadoException;
import com.jolteam.financas.exceptions.UsuarioInexistenteException;
import com.jolteam.financas.exceptions.UsuarioInvalidoException;
import com.jolteam.financas.model.Codigo;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CodigoService;
import com.jolteam.financas.service.UsuarioService;

@Controller
public class IndexController {

	@Autowired private LogDAO logs;
	@Autowired private UsuarioService usuarioService;
	@Autowired private CodigoService codigoService;

	@GetMapping("/")
	public String viewIndex() {
		return "deslogado/index";
	}

	@GetMapping("/cadastrar")
	public ModelAndView viewCadastrar(Model model) {
		ModelAndView mv = new ModelAndView("deslogado/cadastrar");
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

			// envia o código de ativação para o e-mail do usuário
			this.usuarioService.enviarCodigoAtivacao(usuarioSalvo);

			// adiciona a mensagem de sucesso na view
			model.addAttribute("msgSucesso", "Cadastrado! Verifique seu e-mail para ativar sua conta.");

			// adiciona um novo objeto Usuario na variável 'usuario' que será jogada de volta no formulário
			usuario = new Usuario();
		} catch (UsuarioInvalidoException e) {
			result.addError(new ObjectError("erroValidacao", e.getMessage()));
		}

		return new ModelAndView("deslogado/cadastrar").addObject("usuario", usuario);
	}

	@GetMapping("/entrar")
	public ModelAndView viewEntrar() {
		ModelAndView mv = new ModelAndView("deslogado/entrar");
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
			this.logs.save(new Log(usuario, TiposLogs.LOGIN, LocalDateTime.now(), request.getRemoteAddr()));
			
			if (!Strings.isBlank(destino)) {
				System.out.println("Redirecionando para "+destino+"...");
				return "redirect:"+destino;
			} else {
				System.out.println("Redirecionando para /home...");
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

		return "deslogado/entrar";
	}

	@GetMapping("/ativarConta")
	public String ativarConta(@RequestParam(required = false) Integer id, @RequestParam(required = false) String codigo,
			Model model, HttpSession session) {
		String msgErro = "Este link é inválido ou já foi usado antes.";
		if (id == null || Strings.isEmpty(codigo)) {
			return "redirect:/";
		} else {
			Optional<Usuario> usuario = this.usuarioService.findById(id);
			if (usuario.isPresent()) {
				Optional<Codigo> codigoExistente = this.codigoService.findByUsuarioAndTipo(usuario.get(), TiposCodigos.ATIVACAO_CONTA);
				if (codigoExistente.isPresent()) {
					String codigoConfirmacao = codigoExistente.get().getCodigo().replaceAll("[\\-]+", "");
					if (codigo.equals(codigoConfirmacao)) {
						// ativa a conta do usuário e atualiza no banco
						usuario.get().setAtivado(true);
						this.usuarioService.save(usuario.get());
						
						// exclui o código que foi usado do banco
						this.codigoService.delete(codigoExistente.get());
						
						// excluir o id do usuário da sessão
						// estava sendo usado para o sistema saber para qual usuário reenviar o codigo
						session.removeAttribute("usuarioId");
						
						model.addAttribute("sucesso", true);
					} else {
						model.addAttribute("msgErro", msgErro);
					}
				} else {
					model.addAttribute("msgErro", msgErro);
				}
			} else {
				model.addAttribute("msgErro", msgErro);
			}
		}
		
		return "deslogado/ativacao-conta";
	}
	
	@PostMapping("/reenviar-link-ativacao")
	public String reenviarLinkAtivacao(HttpSession session, RedirectAttributes ra) {
		Integer id = (Integer) session.getAttribute("usuarioId");
		if (id != null) {
			Optional<Usuario> usuario = this.usuarioService.findById(id);
			if (usuario.isPresent() && !usuario.get().isAtivado()) {
				this.usuarioService.enviarCodigoAtivacao(usuario.get());
				ra.addFlashAttribute("msgSucesso", "Link de ativação reenviado para o seu e-mail.");
				return "redirect:/entrar";
			} else {
				return "redirect:/";
			}
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/ativacao-conta")
	public String viewAtivacaoConta() {
		return "deslogado/ativacao-conta";
	}

	@GetMapping("/recuperar-senha")
	public String viewRecuperarSenha() {
		return "deslogado/recuperar-senha";
	}

	@GetMapping("recuperar-senha/2")
	public String viewRecuperarSenhaMsg() {
		return "deslogado/recuperar-senha-2";
	}

	@GetMapping("/redefinir-senha")
	public String viewRedefinirSenha() {
		return "deslogado/redefinir-senha";
	}
	
	@GetMapping("/teste")
	public String teste(HttpSession session) {
		System.out.println((Integer) session.getAttribute("usuarioId"));
		return "redirect:/entrar";
	}

}
