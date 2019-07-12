package com.jolteam.financas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.UsuarioService;

@Controller
public class HomeController {
	
	@Autowired private UsuarioService usuarioService;
	
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
	
	@GetMapping("/movimentos")
	public String viewMovimentos() {
		return "/movimentos";
	}
	
}
