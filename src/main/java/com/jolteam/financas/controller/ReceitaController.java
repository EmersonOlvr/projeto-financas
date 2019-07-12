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

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.enums.TiposTransacoes;
import com.jolteam.financas.exceptions.ReceitaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Receita;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.LogService;
import com.jolteam.financas.service.ReceitaService;

@Controller
public class ReceitaController {
	
	@Autowired private LogService logService;
	@Autowired private ReceitaService receitaService;
	
	@Autowired private CategoriaDAO categorias;
	
	@GetMapping("/receitas/adicionar")
	public ModelAndView viewAdicionarReceita(HttpSession session) {
		ModelAndView mv = new ModelAndView("/receitas-adicionar");
		mv.addObject("receita", new Receita());
		mv.addObject("categorias", this.categorias.findByUsuarioAndTipoTransacaoOrderByDataCriacaoDesc((Usuario) session.getAttribute("usuarioLogado"), TiposTransacoes.RECEITA));
		return mv;
	}
	
	@PostMapping("/receitas/adicionar")
	public ModelAndView adicionarReceita(@ModelAttribute Receita receita, HttpServletRequest request, HttpSession session) {
		receita.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		
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
			
			return this.viewAdicionarReceita(session).addObject("erro", re.getMessage());
		}
		
		return this.viewAdicionarReceita(session).addObject("sucesso", "Receita salva com sucesso.");
	}
	
	@GetMapping("/receitas/categorias")
	public ModelAndView viewCategoriasReceitas(HttpSession session) {
		ModelAndView mv= new ModelAndView("receitas-categorias");
		mv.addObject("catReceita", new Categoria());
		mv.addObject("listaCatReceita", this.categorias.findByUsuarioAndTipoTransacaoOrderByDataCriacaoDesc((Usuario) session.getAttribute("usuarioLogado"), TiposTransacoes.RECEITA));
		return mv;
	}
	
	@PostMapping("/receitas/categorias")
	public ModelAndView adicionarCatReceita(@ModelAttribute Categoria catReceita, HttpServletRequest request, HttpSession session) {
		catReceita.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		
		try {
			this.receitaService.salvarCategoriaReceita(catReceita);
			
			this.logService.save(new Log(catReceita.getUsuario(),TiposLogs.CADASTRO_CATEGORIA_RECEITA,LocalDateTime.now(),request.getRemoteAddr()));
		} catch (ReceitaException de) {
			this.logService.save(new Log(catReceita.getUsuario(), TiposLogs.ERRO_CADASTRO_CATEGORIA_RECEITA, LocalDateTime.now(), request.getRemoteAddr()));
			
			return this.viewCategoriasReceitas(session).addObject("msgErroAdd", de.getMessage());

		}
		return this.viewCategoriasReceitas(session).addObject("msgSucessoAdd", "Categoria salva com sucesso.");
	}
	
	@GetMapping("/receitas/categorias/excluir")
	public String excluirCatReceita(@RequestParam Integer id, RedirectAttributes ra) {
		try {
			Categoria catExistente = this.categorias.findById(id).orElseThrow(() -> new Exception("Categoria inexistente."));
			
			if (this.receitaService.existsByCategoria(catExistente)) {
				ra.addFlashAttribute("msgErroListagem", "A categoria selecionada contém vínculos com outras informações.");
			} else {
				ra.addFlashAttribute("msgSucessoListagem", "Categoria excluída com sucesso!");
				this.categorias.deleteById(id);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return "redirect:/receitas/categorias";
	}
	
	@GetMapping("/receitas/historico")
	public String viewHistoricoReceitas() {
		return "/receitas-historico";
	}
	
}
