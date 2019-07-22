package com.jolteam.financas.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
	public String atualizarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes ra, HttpSession session) {
		ModelAndView mv = new ModelAndView("configuracoes");
		mv.addObject("usuario", usuario);
		
		try {
			Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

			// Validar campos atualizados
			try {
				this.usuarioService.atualizarUsuario(usuarioLogado, usuario);

				// salva usuario com valores atualizados
				this.usuarioService.save(usuarioLogado);
			} catch (UsuarioInvalidoException ui) {
				ra.addFlashAttribute("msgErroConfig", ui.getMessage());
				return "redirect:/configuracoes";
			}
		} catch (Exception e) {
			ra.addFlashAttribute("msgErroConfig", e.getMessage());
			return "redirect:/configuracoes";
		}
		ra.addFlashAttribute("msgSucessoConfig", "Configurações salvas!");
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
