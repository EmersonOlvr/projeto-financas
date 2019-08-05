package com.jolteam.financas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jolteam.financas.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired private UsuarioService usuarioService;
	
	@GetMapping("/usuarios")
	public ModelAndView viewUsuarios() {
		ModelAndView mv = new ModelAndView("admin/usuarios");
		mv.addObject("usuarios", this.usuarioService.findAll());
		return mv;
	}
	
}
