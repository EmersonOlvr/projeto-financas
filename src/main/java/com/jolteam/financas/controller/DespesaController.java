package com.jolteam.financas.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.enums.TiposTransacoes;
import com.jolteam.financas.exceptions.DespesaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Despesa;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.service.DespesaService;
import com.jolteam.financas.service.LogService;
import com.jolteam.financas.service.UsuarioService;

@Controller
public class DespesaController {
	
	@Autowired private DespesaService despesaService;
	@Autowired private LogService logService;
	@Autowired private CategoriaDAO categorias;
	@Autowired private UsuarioService usuarioService;
	
	@GetMapping("/despesas/adicionar")
	public ModelAndView viewDespesaAdicionar() {
			ModelAndView mv=new ModelAndView("/despesas-adicionar");
			mv.addObject("despesa", new Despesa());
			mv.addObject("categorias", this.categorias.findByUsuarioAndTipoTransacao(this.usuarioService.getOne(1), TiposTransacoes.DESPESA));
			return mv;
	}
	@PostMapping("/despesas/adicionar")
	public ModelAndView adicionarDespesa(@ModelAttribute Despesa despesa,HttpServletRequest request) {
		try {
			this.despesaService.salvar(despesa);
			
			// salva um log de sucesso no banco
			this.logService.save(new Log(despesa.getUsuario(), TiposLogs.CADASTRO_DESPESA, LocalDateTime.now(), request.getRemoteAddr()));
		} catch (DespesaException de) {
			// salva um log de erro no banco
			this.logService.save(new Log(despesa.getUsuario(), TiposLogs.ERRO_CADASTRO_DESPESA, LocalDateTime.now(), request.getRemoteAddr()));
			
			return this.viewDespesaAdicionar().addObject("erro", de.getMessage());
		}
		return this.viewDespesaAdicionar().addObject("sucesso", "Despesa salva com sucesso.");
	}
	
	
	@GetMapping("/despesas/categorias")
	public ModelAndView viewCategoriasDespesas() {
		ModelAndView mv= new ModelAndView("despesas-categorias");
		mv.addObject("catDespesa", new Categoria());
		mv.addObject("listacatDespesa", this.categorias.findAllByTipoTransacao(TiposTransacoes.DESPESA));
		return mv;
	}
	
	@PostMapping("/despesas/categorias/adicionar")
	public ModelAndView adicionarCatDespesa(@ModelAttribute Categoria catDespesa,HttpServletRequest request) {
		
		try {
			this.despesaService.salvarCategoriaDespesa(catDespesa);
			
			this.logService.save(new Log(catDespesa.getUsuario(),TiposLogs.CADASTRO_CATEGORIA_DESPESA,LocalDateTime.now(),request.getRemoteAddr()));
		} catch (DespesaException de) {
			this.logService.save(new Log(catDespesa.getUsuario(), TiposLogs.ERRO_CADASTRO_CATEGORIA_DESPESA, LocalDateTime.now(), request.getRemoteAddr()));
			
			return this.viewCategoriasDespesas().addObject("erro", de.getMessage());

		}
		return this.viewCategoriasDespesas().addObject("sucesso", "Categoria salva com sucesso.");
	}
	@GetMapping("/despesas/categorias/excluir")
	public String excluirCatDespesa(@RequestParam Integer id) {
		this.categorias.deleteById(id);
		return "redirect:/despesas/categorias";
	}
	
	@GetMapping("/despesas/historico")
	public String viewHistoricoDespesas() {
		return "/despesas-historico";
	}
	
}