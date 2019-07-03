package com.jolteam.financas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioLogadoController {
	
	@GetMapping("/movimentos")
	public String viewMovimentos() {
		return "/movimentos";
	}
	
}
