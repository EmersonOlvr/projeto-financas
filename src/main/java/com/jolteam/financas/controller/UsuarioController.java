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
public class UsuarioController {

	@Autowired private LogDAO logs;
	@Autowired private UsuarioService usuarioService;
	@Autowired private CodigoService codigoService;

	@GetMapping("/")
	public String viewIndex() {
		return "/deslogado/index";
	}

	@GetMapping("/cadastrar")
	public ModelAndView viewCadastrar(Model model) {
		ModelAndView mv = new ModelAndView("/deslogado/cadastrar");

		// se receber (através do método addFlashAttribute do RedirectAttributes) o
		// objeto usuario (com os atributos que foram
		// preenchidos pelo usuário), ele é adicionado na view senão, é adicionado um
		// objeto com atributos vazios.
		// isso serve pra quando ocorrer erros no cadastro o usuário não ter que digitar
		// todos os dados de novo,
		// bastando apenas ele corrigir o que está errado e submeter de novo o
		// formulário
		Usuario usuario = model.asMap().get("usuario") != null ? (Usuario) model.asMap().get("usuario") : new Usuario();
		mv.addObject("usuario", usuario);

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
			Usuario usuario2 = this.usuarioService.save(usuario);

			System.out.println(usuario2.getId());

			// envia o código de ativação para o e-mail do usuário
			this.usuarioService.enviarCodigoAtivacao(usuario2);

			// adiciona a mensagem de sucesso na view
			model.addAttribute("msgSucesso", "Cadastrado! Verifique seu e-mail para ativar sua conta.");

			// adiciona um novo objeto Usuario na variável 'usuario' que será jogada de volta no formulário
			usuario = new Usuario();
		} catch (UsuarioInvalidoException e) {
			result.addError(new ObjectError("erroValidacao", e.getMessage()));
		}

		return new ModelAndView("/deslogado/cadastrar").addObject("usuario", usuario);
	}

	@GetMapping("/entrar")
	public ModelAndView viewEntrar() {
		ModelAndView mv = new ModelAndView("/deslogado/entrar");
		return mv;
	}

	@PostMapping("/entrar")
	public String autenticarUsuario(Model model, @RequestParam String email, @RequestParam String senha,
			HttpServletRequest request, HttpSession session) {
		try {
			Usuario usuario = this.usuarioService.entrar(email, senha);

			// aqui salva o usuário na sessão

			// salva um log de login no banco
			this.logs.save(new Log(usuario, TiposLogs.LOGIN, LocalDateTime.now(), request.getRemoteAddr()));

			model.addAttribute("msgSucesso", "Logado com sucesso!");
		} catch (UsuarioInexistenteException e) {
			model.addAttribute("msgErro", e.getMessage());
		} catch (UsuarioInvalidoException e) {
			model.addAttribute("msgErro", e.getMessage());
		} catch (UsuarioDesativadoException e) {
			session.setAttribute("usuarioId", this.usuarioService.findByEmail(email).get().getId());
			model.addAttribute("isDesativado", true);
			model.addAttribute("email", email);
		}

		return "/deslogado/entrar";
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
					if (codigo.equals(codigoExistente.get().getCodigo())) {
						// ativa a conta do usuário e atualiza no banco
						usuario.get().setAtivado(true);
						this.usuarioService.save(usuario.get());
						
						// exclui o código que foi usado do banco
						this.codigoService.delete(codigoExistente.get());
						
						// remove o id do usuário da sessão 
						// (estava sendo usado para o sistema saber para qual usuário reenviar o código)
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
		
		return "/deslogado/ativacao-conta";
	}
	
	@PostMapping("/reenviar-link-ativacao")
	public String reenviarLinkAtivacao(HttpSession session, RedirectAttributes ra) {
		Integer id = (Integer) session.getAttribute("usuarioId");
		if (id != null) {
			Optional<Usuario> usuario = this.usuarioService.findById(id);
			if (usuario.isPresent() && !usuario.get().isAtivado()) {
				this.usuarioService.enviarCodigoAtivacao(usuario.get());
				ra.addFlashAttribute("reenviado", true);
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
		return "/deslogado/ativacao-conta";
	}

	@GetMapping("/recuperar-senha")
	public String viewRecuperarSenha() {
		return "/deslogado/recuperar-senha";
	}

	@GetMapping("recuperar-senha/2")
	public String viewRecuperarSenhaMsg() {
		return "/deslogado/recuperar-senha-2";
	}

	@GetMapping("/redefinir-senha")
	public String viewRedefinirSenha() {
		return "/deslogado/redefinir-senha";
	}

	@GetMapping("/configuracoes")
	public String viewConfiguracoes() {
		return "/configuracoes";
	}

	@PostMapping("/configuracoes")
	public String atualizarUsuario(Model model, @ModelAttribute Usuario usuario) {
		this.usuarioService.save(usuario);
		model.addAttribute("msgSucesso", "Configurações salvas!");
		return "/configuracoes";
	}

	@GetMapping("/home")
	public String viewHome() {
		return "/home";
	}

}
