package com.jolteam.financas.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.model.Transacao;

@Controller
public class MovimentosController {
	
	@Autowired private TransacaoDAO transacaodao;
	@Autowired private CategoriaDAO categoriadao;


	
	@GetMapping("/movimentos")
	public ModelAndView viewMovimentos(HttpSession session) {
		ModelAndView mv = new ModelAndView("movimentos");
		mv.addObject("transacao", new Transacao());
		mv.addObject("listatransacao", transacaodao.findAll());
		mv.addObject("categoria", categoriadao.findAll());

		return mv;
	}
	
		

}
