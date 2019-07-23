package com.jolteam.financas.controller;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.exceptions.UsuarioInvalidoException;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.UsuarioService;

@Controller
public class HomeController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/home")
	public String viewHome() {
		return "home";
	}

	// ====== Configurações ====== //
	@GetMapping("/configuracoes")
	public ModelAndView viewConfiguracoes(HttpSession session) {
		ModelAndView mv = new ModelAndView("configuracoes");
		mv.addObject("usuario", (Usuario) session.getAttribute("usuarioLogado"));
		return mv;
	}

	@PostMapping("/configuracoes/perfil")
	public String atualizarPerfilUsuario(@ModelAttribute Usuario usuario, RedirectAttributes ra, HttpSession session) {
		ModelAndView mv = new ModelAndView("configuracoes");
		mv.addObject("usuario", usuario);
		
		Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

		// Validar campos atualizados
		try {
			this.usuarioService.atualizarUsuario(usuarioLogado, usuario);

			// salva usuario com valores atualizados
			this.usuarioService.save(usuarioLogado);
			
			ra.addFlashAttribute("msgSucessoConfig", "Perfil atualizado com sucesso!");
		} catch (UsuarioInvalidoException ui) {
			ra.addFlashAttribute("msgErroConfig", ui.getMessage());
		}
		
		ra.addFlashAttribute("alvoLista", "perfil");
		return "redirect:/configuracoes";
	}
	
	@PostMapping("/configuracoes/senha")
	public String atualizarSenhaUsuario(@RequestParam String senhaAtual, 
			@RequestParam String novaSenha, @RequestParam String novaSenhaRepetida, 
			RedirectAttributes ra, HttpSession session) 
	{
		Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
		
		if (Strings.isEmpty(senhaAtual)) {
			ra.addFlashAttribute("msgErroConfig", "Insira a senha atual.");
		} else if (!this.usuarioService.checarSenha(senhaAtual, usuarioLogado.getSenha())) {
			ra.addFlashAttribute("msgErroConfig", "A senha atual informada está incorreta.");
		} else if (Strings.isEmpty(novaSenha)) {
			ra.addFlashAttribute("msgErroConfig", "Insira a nova senha.");
		} else if (novaSenha.length() < 6) {
			ra.addFlashAttribute("msgErroConfig", "Senha muito curta. Mínimo de 6 caracteres.");
		} else if (novaSenha.length() > 255) {
			ra.addFlashAttribute("msgErroConfig", "Senha muito grande. Máximo de 255 caracteres.");
		} else if (Strings.isEmpty(novaSenhaRepetida)) {
			ra.addFlashAttribute("msgErroConfig", "Por favor, repita a nova senha.");
		} else if (!novaSenha.equals(novaSenhaRepetida)) {
			ra.addFlashAttribute("msgErroConfig", "As senhas não conferem.");
		} else {
			usuarioLogado.setSenha(this.usuarioService.criptografarSenha(novaSenha));
			
			// salva usuario com valores atualizados
			this.usuarioService.save(usuarioLogado);
			
			ra.addFlashAttribute("msgSucessoConfig", "Senha atualizada com sucesso!");
		}

		ra.addFlashAttribute("alvoLista", "senha");
		return "redirect:/configuracoes";
	}

	@GetMapping("/movimentos")
	public String viewMovimentos() {
		return "movimentos";
	}

	@GetMapping("/sair")
	public String sair(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/acesso-negado")
	public String viewAcessoNegado() {
		return "acesso-negado";
	}

}
