package com.jolteam.financas.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.exceptions.DespesaException;
import com.jolteam.financas.model.Despesa;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.TipoLog;
import com.jolteam.financas.model.TipoTransacao;
import com.jolteam.financas.service.DespesaService;
import com.jolteam.financas.service.LogService;

@Controller
public class DespesaController {
	
	@Autowired private DespesaService despesaService;
	@Autowired private LogService logService;
	@Autowired private CategoriaDAO categorias;
	@Autowired private UsuarioDAO usuarios;
	
	@GetMapping("/despesas/adicionar")
	public ModelAndView viewDespesaAdicionar() {
			ModelAndView mv=new ModelAndView("/despesas-adicionar");
			mv.addObject("despesa", new Despesa());
			mv.addObject("categorias", this.categorias.findByUsuarioAndTipo(this.usuarios.getOne(1), TipoTransacao.DESPESA));
			return mv;
	}
	@PostMapping("/despesas/adicionar")
	public ModelAndView adicionarDespesa(@ModelAttribute Despesa despesa,HttpServletRequest request) {
		try {
			this.despesaService.salvar(despesa);
			try {
				// salva um log de sucesso no banco
				this.logService.salvar(new Log(despesa.getUsuario(), TipoLog.CADASTRO_DESPESA, LocalDateTime.now(), request.getRemoteAddr()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}catch (DespesaException de) {
			try {
				// salva um log de erro no banco
				this.logService.salvar(new Log(despesa.getUsuario(), TipoLog.ERRO_CADASTRO_DESPESA, LocalDateTime.now(), request.getRemoteAddr()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return this.viewDespesaAdicionar().addObject("erro", de.getMessage());
		}
		return this.viewDespesaAdicionar().addObject("sucesso", "Despesa salva com sucesso.");
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
