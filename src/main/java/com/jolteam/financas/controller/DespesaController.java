package com.jolteam.financas.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.enums.TiposTransacoes;
import com.jolteam.financas.exceptions.DespesaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Despesa;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.DespesaService;
import com.jolteam.financas.service.LogService;

@Controller
@RequestMapping("/despesas")
public class DespesaController {
	
	@Autowired private DespesaService despesaService;
	@Autowired private LogService logService;
	@Autowired private CategoriaDAO categorias;
	
	@GetMapping("/adicionar")
	public ModelAndView viewDespesaAdicionar(HttpSession session) {
			ModelAndView mv=new ModelAndView("despesas-adicionar");
			mv.addObject("despesa", new Despesa());
			mv.addObject("categorias", this.categorias.findByUsuarioAndTipoTransacaoOrderByDataCriacaoDesc((Usuario) session.getAttribute("usuarioLogado"), TiposTransacoes.DESPESA));
			return mv;
	}
	
	@PostMapping("/adicionar")
	public ModelAndView adicionarDespesa(@ModelAttribute Despesa despesa,HttpServletRequest request, HttpSession session) {
		despesa.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		
		try {
			this.despesaService.salvar(despesa);
			
			// salva um log de sucesso no banco
			this.logService.save(new Log(despesa.getUsuario(), TiposLogs.CADASTRO_DESPESA, LocalDateTime.now(), request.getRemoteAddr()));
		} catch (DespesaException de) {
			return this.viewDespesaAdicionar(session).addObject("msgErroAdd", de.getMessage());
		}
		
		return this.viewDespesaAdicionar(session).addObject("msgSucessoAdd", "Despesa salva com sucesso.");
	}
	
	@GetMapping("/categorias")
	public ModelAndView viewCategoriasDespesas(HttpSession session) {
		ModelAndView mv= new ModelAndView("despesas-categorias");
		mv.addObject("catDespesa", new Categoria());
		mv.addObject("listaCatDespesa", this.categorias.findByUsuarioAndTipoTransacaoOrderByDataCriacaoDesc((Usuario) session.getAttribute("usuarioLogado"), TiposTransacoes.DESPESA));
		return mv;
	}
	
	@PostMapping("/categorias")
	public ModelAndView adicionarCatDespesa(@ModelAttribute Categoria catDespesa,HttpServletRequest request, HttpSession session) {
		catDespesa.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		
		try {
			this.despesaService.salvarCategoriaDespesa(catDespesa);
			
			this.logService.save(new Log(catDespesa.getUsuario(),TiposLogs.CADASTRO_CATEGORIA_DESPESA,LocalDateTime.now(),request.getRemoteAddr()));
		} catch (DespesaException de) {
			return this.viewCategoriasDespesas(session).addObject("msgErroAdd", de.getMessage());
		}
		
		return this.viewCategoriasDespesas(session).addObject("msgSucessoAdd", "Categoria salva com sucesso.");
	}
	
	@GetMapping("/categorias/excluir")
	public String excluirCatDespesa(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
		try {
			Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
			Categoria catExistente = this.categorias.findByIdAndUsuarioAndTipoTransacao(id, usuario, TiposTransacoes.DESPESA)
													.orElseThrow(() -> new Exception("Categoria inexistente."));
			
			if (this.despesaService.existsByCategoria(catExistente)) {
				ra.addFlashAttribute("msgErroExcluir", "A categoria selecionada contém vínculos com outras informações.");
			} else {
				ra.addFlashAttribute("msgSucessoExcluir", "Categoria excluída com sucesso!");
				this.categorias.deleteById(id);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return "redirect:/despesas/categorias";
	}
	
	@GetMapping("/historico")
	public String viewHistoricoDespesas() {
		return "despesas-historico";
	}
	
}
