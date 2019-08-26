package com.jolteam.financas.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.enums.TipoLog;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CategoriaService;
import com.jolteam.financas.service.LogService;
import com.jolteam.financas.service.MovimentosService;
import com.jolteam.financas.util.Util;

@Controller
public class MovimentosController {
	
	@Autowired private CategoriaService categorias;
	@Autowired private MovimentosService movimentosService;
	@Autowired private LogService logService;
	
	@GetMapping("/movimentos")
	public ModelAndView viewMovimentos(HttpSession session, RedirectAttributes ra, 
			@RequestParam(required=false,defaultValue = "1") Integer page) 
	{
		ModelAndView mv = new ModelAndView("usuario/movimentos");
		
		if (page < 1) {
			return new ModelAndView("redirect:/movimentos");
		}
		
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		BigDecimal totalReceitas = this.movimentosService.totalReceitaAcumuladaDe(usuario);
		BigDecimal totalDespesas = this.movimentosService.totalDespesaAcumuladaDe(usuario);
		Page<Transacao> pagina=this.movimentosService.findAllByUsuario((Usuario) session.getAttribute("usuarioLogado"),PageRequest.of(page - 1, 15, Sort.by("data").descending()));
		
		if (page > pagina.getTotalPages()) {
			return new ModelAndView("redirect:/movimentos");
		}
		
		mv.addObject("transacao", new Transacao());
		mv.addObject("transacoes", pagina);
		mv.addObject("categoria", this.categorias.listarTodasPorUsuario(usuario));
		
		mv.addObject("totalReceitas", totalReceitas);
		mv.addObject("totalDespesas", totalDespesas);
		mv.addObject("saldoAtual", totalReceitas.subtract(totalDespesas));

		return mv;
	}
	
	
	@GetMapping("/movimentos/excluir")
	@Transactional
	public String excluirTransacao(@RequestParam Integer id, @RequestParam(name = "l", required = false) String local, 
			HttpSession session, HttpServletRequest request, RedirectAttributes ra) 
	{
		try {
			Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
			Optional<Transacao> transacao = this.movimentosService.buscarPorIdEUsuario(id, usuario);
			if (transacao.isPresent()) {
				this.movimentosService.deletarPorIdEUsuario(id, usuario);
				ra.addFlashAttribute("msgSucessoExcluir", "Transação excluída com sucesso!");
				
				if (transacao.get().getTipo().equals(TipoTransacao.RECEITA)) {
					this.logService.save(new Log(usuario, TipoLog.EXCLUSAO_RECEITA, LocalDateTime.now(), 
							Util.getUserIp(request)));
				} else if (transacao.get().getTipo().equals(TipoTransacao.DESPESA)) {
					this.logService.save(new Log(usuario, TipoLog.EXCLUSAO_DESPESA, LocalDateTime.now(), Util.getUserIp(request)));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		if (local != null && local.equals("RH")) {
			return "redirect:/receitas/extrato";
		} else if (local != null && local.equals("DH")) {
			return "redirect:/despesas/extrato";
		}
		
		return "redirect:/movimentos";
	}

}
