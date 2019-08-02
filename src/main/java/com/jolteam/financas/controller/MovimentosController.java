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

import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CategoriaService;
import com.jolteam.financas.service.MovimentosService;

@Controller
public class MovimentosController {
	
	@Autowired private CategoriaService categorias;
	@Autowired private MovimentosService movimentosService;
	
	@GetMapping("/movimentos")
	public ModelAndView viewMovimentos(HttpSession session) {
		ModelAndView mv = new ModelAndView("movimentos");
		
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		BigDecimal totalReceitas = this.movimentosService.totalReceitaAcumuladaDe(usuario);
		BigDecimal totalDespesas = this.movimentosService.totalDespesaAcumuladaDe(usuario);
		
		mv.addObject("transacao", new Transacao());
		mv.addObject("listatransacao", this.movimentosService.listarTodasPorUsuario(usuario));
		mv.addObject("categoria", this.categorias.listarTodasPorUsuario(usuario));
		
		mv.addObject("totalReceitas", totalReceitas);
		mv.addObject("totalDespesas", totalDespesas);
		mv.addObject("saldoAtual", totalReceitas.subtract(totalDespesas));

		return mv;
	}
	
	
	@GetMapping("/movimentos/excluir")
	@Transactional
	public String excluirTransacao(@RequestParam Integer id, @RequestParam(name = "l", required = false) String local, 
			HttpSession session, RedirectAttributes ra) 
	{
		try {
			Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
			if (this.movimentosService.buscarPorIdEUsuario(id, usuario).isPresent()) {
				this.movimentosService.deletarPorIdEUsuario(id, usuario);
				ra.addFlashAttribute("msgSucessoExcluir", "Transação excluída com sucesso!");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		if (local != null && local.equals("RH")) {
			return "redirect:/receitas/historico";
		} else if (local != null && local.equals("DH")) {
			return "redirect:/despesas/historico";
		}
		
		return "redirect:/movimentos";
	}
	
	

}
