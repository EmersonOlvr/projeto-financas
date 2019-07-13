package com.jolteam.financas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.service.CofreService;

@Controller
public class CofreController {
	
	@Autowired private CofreService cofreService;
	
	
	@GetMapping("/cofres")
	public ModelAndView viewCofres() {
		ModelAndView mv=new ModelAndView("cofres");
		mv.addObject("cofre", new Cofre());
		mv.addObject("listaCofres", this.cofreService.findAll());
		return mv;
	}
	@GetMapping("/cofres/cadastrar")
	public ModelAndView viewCadastrarCofres() {
		ModelAndView mv=new ModelAndView("cofres");
		return mv;
	}
	
	@GetMapping("/cofres/editar")
	public String viewEditarCofre(@RequestParam Integer id) {
		System.out.println("Editando o cofre: "+id+".");
		return "cofres-editar";
	}
	@GetMapping("/cofres/excluir")
	public String excluirCofre(@RequestParam Integer id, RedirectAttributes ra) {
		System.out.println("Excluindo o cofre: "+id+"...");
		
		ra.addFlashAttribute("msgSucesso", "Cofre excluído!");
		return "redirect:/cofres";
	}
	
}
