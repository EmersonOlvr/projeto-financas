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
	
	@Autowired private UsuarioService usuarioService;
	
	@GetMapping("/home")
	public String viewHome() {
		return "home";
	}
	
	@GetMapping("/configuracoes")
	public ModelAndView viewConfiguracoes(HttpSession session) {
		ModelAndView mv=new ModelAndView("configuracoes");
		mv.addObject("usuario", session.getAttribute("usuarioLogado"));
		return mv;
	}

	@PostMapping("/configuracoes")
	public ModelAndView atualizarUsuario(@ModelAttribute Usuario usuario,RedirectAttributes ra,HttpSession session) {
		ModelAndView mv=new ModelAndView("configuracoes");
		mv.addObject("usuario",usuario);
		try {
			Usuario usuarioExiste= (Usuario) session.getAttribute("usuarioLogado");
			
				//Validar campos atualizados
				try {
					
				this.usuarioService.validarAtualizarUsuario(usuario);	
					
				if(!usuarioExiste.getNome().contentEquals(usuario.getNome())) {
					usuarioExiste.setNome(usuario.getNome());
				}
				if(!usuarioExiste.getSobrenome().contentEquals(usuario.getSobrenome())) {
					usuarioExiste.setSobrenome(usuario.getSobrenome());
				}
				if(usuarioExiste.getEmail().contentEquals(usuario.getEmail())) {
					usuarioExiste.setEmail(usuario.getEmail());
				}
				
				//salva usuario com valores atualizados
				this.usuarioService.save(usuarioExiste);
				
				}catch(UsuarioInvalidoException ui){
					return this.viewConfiguracoes(session).addObject("msgErroConfig", ui.getMessage());
				}
		} catch (Exception e) {
			return this.viewConfiguracoes(session).addObject("msgErroConfig", e.getMessage());
		}
		return this.viewConfiguracoes(session).addObject("msgSucessoConfig", "Configurações salvas!");
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
