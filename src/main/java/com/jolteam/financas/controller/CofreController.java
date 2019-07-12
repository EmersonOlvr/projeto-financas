package com.jolteam.financas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CofreController {

	@GetMapping("/cofres")
	public String viewCofres(Model model) {
		return "/cofres";
	}
	@GetMapping("/cofres/editar")
	public String viewEditarCofre(@RequestParam Integer id) {
		System.out.println("Editando o cofre: "+id+".");
		return "/cofres-editar";
	}
	@GetMapping("/cofres/excluir")
	public String excluirCofre(@RequestParam Integer id, RedirectAttributes ra) {
		System.out.println("Excluindo o cofre: "+id+"...");
		
		ra.addFlashAttribute("msgSucesso", "Cofre excluído!");
		return "redirect:/cofres";
	}
	
}
