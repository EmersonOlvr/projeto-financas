package com.jolteam.financas.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.MovimentoService;

@Controller
public class MovimentosController {
	
	@Autowired private TransacaoDAO transacaodao;
	@Autowired private CategoriaDAO categoriadao;
	@Autowired private MovimentoService movimentos;

	
	@GetMapping("/movimentos")
	public ModelAndView viewMovimentos(HttpSession session) {
		ModelAndView mv = new ModelAndView("movimentos");
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		BigDecimal totalReceitas = this.movimentos.totalReceitaAcumuladaDe(usuario);
		BigDecimal totalDespesas = this.movimentos.totalDespesaAcumuladaDe(usuario);
		
		mv.addObject("transacao", new Transacao());
		mv.addObject("listatransacao", transacaodao.findAll());
		mv.addObject("categoria", categoriadao.findAll());
		mv.addObject("totalReceitas", totalReceitas);
		mv.addObject("totalDespesas", totalDespesas);
		mv.addObject("saldoAtual", totalReceitas.subtract(totalDespesas));

		return mv;
	}
	
	
	@GetMapping("/movimentos/excluir")
	@Transactional
	public String excluirTransacao(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
		try {
			Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
			if (this.transacaodao.findByIdAndUsuario(id, usuario).isPresent()) {
				transacaodao.deleteByIdAndUsuario(id, usuario);				
				ra.addFlashAttribute("msgSucessoExcluir", "Transação excluída com sucesso!");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return "redirect:/movimentos";
	}
	
	

}
