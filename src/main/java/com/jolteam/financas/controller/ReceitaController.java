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
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.enums.TiposTransacoes;
import com.jolteam.financas.exceptions.ReceitaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Receita;
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
		mv.addObject("categorias", this.categorias.findByUsuarioAndTipoTransacao(this.usuarios.getOne(1), TiposTransacoes.RECEITA));
		return mv;
	}
	@PostMapping("/receitas/adicionar")
	public ModelAndView adicionarReceita(@ModelAttribute Receita receita, HttpServletRequest request) {
		try {
			this.receitaService.salvar(receita);
			
			try {
				// salva um log de sucesso no banco
				this.logService.save(new Log(receita.getUsuario(), TiposLogs.CADASTRO_RECEITA, LocalDateTime.now(), request.getRemoteAddr()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} catch (ReceitaException re) {
			try {
				// salva um log de erro no banco
				this.logService.save(new Log(receita.getUsuario(), TiposLogs.ERRO_CADASTRO_RECEITA, LocalDateTime.now(), request.getRemoteAddr()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			return this.viewAdicionarReceita().addObject("erro", re.getMessage());
		}
		
		return this.viewAdicionarReceita().addObject("sucesso", "Receita salva com sucesso.");
	}
	
	
	
	
	@GetMapping("/receitas/categorias")
	public ModelAndView viewCategoriasReceitas() {
		ModelAndView mv= new ModelAndView("receitas-categorias");
		mv.addObject("catReceita", new Categoria());
		mv.addObject("listacatReceita", this.categorias.findAllByTipoTransacao(TiposTransacoes.RECEITA));
		return mv;
	}
	
	@PostMapping("/receitas/categorias/adicionar")
	public ModelAndView adicionarCatReceita(@ModelAttribute Categoria catReceita,HttpServletRequest request) {
		
		try {
			this.receitaService.salvarCategoriaReceita(catReceita);
			
			this.logService.save(new Log(catReceita.getUsuario(),TiposLogs.CADASTRO_CATEGORIA_RECEITA,LocalDateTime.now(),request.getRemoteAddr()));
		} catch (ReceitaException de) {
			this.logService.save(new Log(catReceita.getUsuario(), TiposLogs.ERRO_CADASTRO_CATEGORIA_RECEITA, LocalDateTime.now(), request.getRemoteAddr()));
			
			return this.viewCategoriasReceitas().addObject("erro", de.getMessage());

		}
		return this.viewCategoriasReceitas().addObject("sucesso", "Categoria salva com sucesso.");
	}
	@GetMapping("/receitas/categorias/excluir")
	public String excluirCatReceita(@RequestParam Integer id) {
		this.categorias.deleteById(id);
		return "redirect:/receitas/categorias";
	}
	
	
	
	@GetMapping("/receitas/historico")
	public String viewHistoricoReceitas() {
		return "/receitas-historico";
	}
	
}
