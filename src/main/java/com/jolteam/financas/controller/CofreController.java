package com.jolteam.financas.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.exceptions.CofreException;
import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CofreService;
import com.jolteam.financas.service.LogService;

@Controller
public class CofreController {
	
	@Autowired private CofreService cofreService;
	@Autowired private LogService logService;
	
	@GetMapping("/cofres")
	public ModelAndView viewCofres(HttpSession session) {
		ModelAndView mv=new ModelAndView("cofres");
		mv.addObject("cofre", new Cofre());
		mv.addObject("listaCofres", this.cofreService.findByUsuarioOrderByDataCriacaoDesc((Usuario)session.getAttribute("usuarioLogado")));
		return mv;
	}
	
	@PostMapping("/cofres/cadastrar")
	public ModelAndView cadastrarCofres(@ModelAttribute Cofre cofre,HttpServletRequest request, HttpSession session) {
		cofre.setUsuario((Usuario)session.getAttribute("usuarioLogado"));
		
		try {
			this.cofreService.salvaCofrer(cofre);
			
			// salva um log de sucesso no banco
			this.logService.save(new Log(cofre.getUsuario(),TiposLogs.CADASTRO_COFRE,LocalDateTime.now(), request.getRemoteAddr()));
		}catch(CofreException ce) {
			
			this.logService.save(new Log(cofre.getUsuario(),TiposLogs.ERRO_CADASTRO_COFRE,LocalDateTime.now(), request.getRemoteAddr()));
			
			return this.viewCofres(session).addObject("msgErroAdd", ce.getMessage());
		}
		return this.viewCofres(session).addObject("msgSucessoAdd", "Cofre salvo com sucesso.");
	}
	
	@GetMapping("/cofres/editar")
	public String viewEditarCofre(@RequestParam Integer id) {
		System.out.println("Editando o cofre: "+id+".");
		return "cofres-editar";
	}
	@GetMapping("/cofres/excluir")
	public String excluirCofre(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
		
		this.cofreService.deleteById(id);
		
		ra.addFlashAttribute("msgSucessoExcluir", "Cofre excluído!");
		return "redirect:/cofres";
	}
	
}
