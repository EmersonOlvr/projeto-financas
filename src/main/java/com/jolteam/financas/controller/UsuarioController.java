package com.jolteam.financas.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

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

import com.jolteam.financas.dao.LogDAO;
import com.jolteam.financas.enums.TiposCodigos;
import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.exceptions.UsuarioInexistenteException;
import com.jolteam.financas.exceptions.UsuarioInvalidoException;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.UsuarioService;

@Controller
public class UsuarioController {
	
	@Autowired private LogDAO logs;
	
	@Autowired private UsuarioService usuarioService;

	@GetMapping("/")
	public String viewIndex() {
		return "/deslogado/index";
	}
	
	@GetMapping("/cadastrar")
	public ModelAndView viewCadastrar(Model model) {
		ModelAndView mv = new ModelAndView("/deslogado/cadastrar");
		
		// se receber (através do método addFlashAttribute do RedirectAttributes) o objeto usuario (com os atributos que foram
		// preenchidos pelo usuário), ele é adicionado na view senão, é adicionado um objeto com atributos vazios. 
		// isso serve pra quando ocorrer erros no cadastro o usuário não ter que digitar todos os dados de novo, 
		// bastando apenas ele corrigir o que está errado e submeter de novo o formulário
		Usuario usuario = model.asMap().get("usuario") != null ? (Usuario) model.asMap().get("usuario") : new Usuario();
		mv.addObject("usuario", usuario);
		
		return mv;
	}
	
	@PostMapping("/cadastrar")
	public ModelAndView cadastrarUsuario(HttpServletRequest request, Model model, 
								   @ModelAttribute Usuario usuario, 
								   BindingResult result) 
	{
		usuario.setRegistroData(LocalDateTime.now());
		usuario.setRegistroIp(request.getRemoteAddr());
		
		try {
			// valida o usuário
			usuarioService.validar(usuario);
			
			// salva o usuário no banco de dados
			this.usuarioService.save(usuario);
			
			// envia o código de ativação para o e-mail do usuário
			this.usuarioService.enviarCodigo(usuario, TiposCodigos.ATIVACAO_CONTA);
			
			// adiciona a mensagem de sucesso na view
			model.addAttribute("msgSucesso", "Registrado com sucesso!");
			
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
	public String autenticarUsuario(Model model, @RequestParam String email, String senha, HttpServletRequest request) {
		try {
			Usuario usuario = this.usuarioService.entrar(email, senha);
			
			// aqui salva o usuário na sessão
			
			// salva um log de login no banco
			this.logs.save(new Log(usuario, TiposLogs.LOGIN, LocalDateTime.now(), request.getRemoteAddr()));
			
			model.addAttribute("msgSucesso", "Logado com sucesso!");
		} catch (UsuarioInexistenteException ui) {
			model.addAttribute("msgErro", ui.getMessage());
		} catch (UsuarioInvalidoException ue) {
			model.addAttribute("msgErro", ue.getMessage());
		}
		
		return "/deslogado/entrar";
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
