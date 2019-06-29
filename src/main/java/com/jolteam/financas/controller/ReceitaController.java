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
import com.jolteam.financas.exceptions.ReceitaException;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Receita;
import com.jolteam.financas.model.TipoLog;
import com.jolteam.financas.model.TipoTransacao;
import com.jolteam.financas.service.LogService;
import com.jolteam.financas.service.ReceitaService;

@Controller
public class ReceitaController {
	
	@Autowired private ReceitaService receitaService;
	@Autowired private LogService logService;
	
	@Autowired private CategoriaDAO categorias;
	@Autowired private UsuarioDAO usuarios;
	
	@GetMapping("/receitas/adicionar")
	public ModelAndView viewAdicionarReceita() {
		ModelAndView mv = new ModelAndView("/receitas-adicionar");
		mv.addObject("receita", new Receita());
		mv.addObject("categorias", this.categorias.findByUsuarioAndTipo(this.usuarios.getOne(1), TipoTransacao.RECEITA));
		return mv;
	}
	@PostMapping("/receitas/adicionar")
	public ModelAndView adicionarReceita(@ModelAttribute Receita receita, HttpServletRequest request) {
		try {
			this.receitaService.salvar(receita);
			
			try {
				// salva um log de sucesso no banco
				this.logService.salvar(new Log(receita.getUsuario(), TipoLog.CADASTRO_RECEITA, LocalDateTime.now(), request.getRemoteAddr()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} catch (ReceitaException re) {
			try {
				// salva um log de erro no banco
				this.logService.salvar(new Log(receita.getUsuario(), TipoLog.ERRO_CADASTRO_RECEITA, LocalDateTime.now(), request.getRemoteAddr()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			return this.viewAdicionarReceita().addObject("erro", re.getMessage());
		}
		
		return this.viewAdicionarReceita().addObject("sucesso", "Receita salva com sucesso.");
	}
	
	@GetMapping("/receitas/categorias")
	public String viewCategoriasReceitas() {
		return "/receitas-categorias";
	}
	
	@GetMapping("/receitas/historico")
	public String viewHistoricoReceitas() {
		return "/receitas-historico";
	}
	
}
