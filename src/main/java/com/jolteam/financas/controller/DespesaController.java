package com.jolteam.financas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DespesaController {

	@GetMapping("/despesas/adicionar")
	public String viewAdicionarDespesa() {
		return "/despesas-adicionar";
	}
	
	@GetMapping("/despesas/categorias")
	public String viewCategoriasDespesas() {
		return "/despesas-categorias";
	}
	
	@GetMapping("/despesas/historico")
	public String viewHistoricoDespesas() {
		return "/despesas-historico";
	}
	
}
