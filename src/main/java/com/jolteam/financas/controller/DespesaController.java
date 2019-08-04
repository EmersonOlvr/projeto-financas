package com.jolteam.financas.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.enums.TipoLog;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.exceptions.BigDecimalInvalidoException;
import com.jolteam.financas.exceptions.DespesaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Despesa;
import com.jolteam.financas.model.HistoricoTransacao;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CategoriaService;
import com.jolteam.financas.service.DespesaService;
import com.jolteam.financas.service.LogService;
import com.jolteam.financas.util.Util;

@Controller
@RequestMapping("/despesas")
public class DespesaController {
	
	@Autowired private DespesaService despesaService;
	@Autowired private LogService logService;
	@Autowired private CategoriaService categoriaService;
	@Autowired private TransacaoDAO transacoes;
	
	@GetMapping("/adicionar")
	public ModelAndView viewAdicionarDespesa(HttpSession session) {
		ModelAndView mv=new ModelAndView("despesas-adicionar");
		mv.addObject("despesa", new Despesa());
		mv.addObject("categorias", this.categoriaService.listarTodasPorUsuarioETipoTransacao((Usuario) session.getAttribute("usuarioLogado"), TipoTransacao.DESPESA));
		return mv;
	}
	
	@PostMapping("/adicionar")
	public ModelAndView adicionarDespesa(@ModelAttribute Despesa despesa, @RequestParam String str_valor, 
			HttpServletRequest request, HttpSession session) 
	{
		despesa.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		
		BigDecimal valor;
		try {
			valor = Util.getBigDecimalOf(str_valor);
			despesa.setValor(valor);
		} catch (BigDecimalInvalidoException e) {
			return this.viewAdicionarDespesa(session)
					.addObject("msgErroAdd", "O valor informado é inválido.")
					.addObject("despesa", despesa)
					.addObject("str_valor", str_valor);
		}
		
		try {
			this.despesaService.salvar(despesa);
			
			// salva um log de sucesso no banco
			this.logService.save(new Log(despesa.getUsuario(), TipoLog.CADASTRO_DESPESA, LocalDateTime.now(), request.getRemoteAddr()));
		} catch (DespesaException de) {
			return this.viewAdicionarDespesa(session)
					.addObject("msgErroAdd", de.getMessage())
					.addObject("despesa", despesa)
					.addObject("str_valor", str_valor);
		}
		
		return this.viewAdicionarDespesa(session)
				.addObject("msgSucessoAdd", "Despesa salva com sucesso.");
	}
	
	@GetMapping("/categorias")
	public ModelAndView viewCategoriasDespesas(HttpSession session) {
		ModelAndView mv= new ModelAndView("despesas-categorias");
		mv.addObject("catDespesa", new Categoria());
		mv.addObject("listaCatDespesa", this.categoriaService.listarTodasPorUsuarioETipoTransacao((Usuario) session.getAttribute("usuarioLogado"), TipoTransacao.DESPESA));
		return mv;
	}
	
	@PostMapping("/categorias")
	public ModelAndView adicionarCatDespesa(@ModelAttribute Categoria catDespesa,HttpServletRequest request, HttpSession session) {
		catDespesa.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		
		try {
			this.despesaService.salvarCategoriaDespesa(catDespesa);
			
			this.logService.save(new Log(catDespesa.getUsuario(),TipoLog.CADASTRO_CATEGORIA_DESPESA,LocalDateTime.now(),request.getRemoteAddr()));
		} catch (DespesaException de) {
			return this.viewCategoriasDespesas(session).addObject("msgErroAdd", de.getMessage());
		}
		
		return this.viewCategoriasDespesas(session).addObject("msgSucessoAdd", "Categoria salva com sucesso.");
	}
	
	@GetMapping("/categorias/excluir")
	public String excluirCatDespesa(@RequestParam Integer id, HttpSession session, HttpServletRequest request,
			RedirectAttributes ra) 
	{
		try {
			Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
			Categoria catExistente = this.categoriaService.obterPorIdEUsuarioETipoTransacao(id, usuario, TipoTransacao.DESPESA)
													.orElseThrow(() -> new Exception("Categoria inexistente."));
			
			if (this.despesaService.existsByCategoria(catExistente)) {
				ra.addFlashAttribute("msgErroExcluir", "A categoria selecionada contém vínculos com outras informações.");
			} else {
				ra.addFlashAttribute("msgSucessoExcluir", "Categoria excluída com sucesso!");
				this.categoriaService.deletarPorId(id);
				
				this.logService.save(new Log(usuario, TipoLog.EXCLUSAO_CATEGORIA_DESPESA, LocalDateTime.now(), 
						Util.getUserIp(request)));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return "redirect:/despesas/categorias";
	}
	
	@GetMapping("/historico")
	public ModelAndView viewHistoricoDespesas(HttpSession session) {
		ModelAndView mv = new ModelAndView("despesas-historico");
		
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		
		List<HistoricoTransacao> historicoDespesas = new ArrayList<>();
		
		List<Categoria> categoriasUsuario = this.categoriaService.listarTodasPorUsuarioETipoTransacao(usuario, TipoTransacao.DESPESA);
		for (Categoria categoria : categoriasUsuario) {
			List<Transacao> transacoesPorCategoria = this.transacoes.findAllByCategoria(categoria);
			historicoDespesas.add(new HistoricoTransacao(categoria, transacoesPorCategoria));
		}
		
		mv.addObject("historicoDespesas", historicoDespesas);
		
		return mv;
	}
	
}
