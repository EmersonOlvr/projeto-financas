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
import com.jolteam.financas.exceptions.ReceitaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.model.dto.HistoricoTransacao;
import com.jolteam.financas.model.dto.Receita;
import com.jolteam.financas.service.CategoriaService;
import com.jolteam.financas.service.LogService;
import com.jolteam.financas.service.ReceitaService;
import com.jolteam.financas.util.Util;

@Controller
@RequestMapping("/receitas")
public class ReceitaController {
	
	@Autowired private LogService logService;
	@Autowired private ReceitaService receitaService;
	@Autowired private CategoriaService categoriaService;
	@Autowired private TransacaoDAO transacoes;
	
	@GetMapping("/adicionar")
	public ModelAndView viewAdicionarReceita(HttpSession session) {
		ModelAndView mv = new ModelAndView("usuario/receita/adicionar");
		mv.addObject("receita", new Receita());
		mv.addObject("categorias", this.categoriaService.listarTodasPorUsuarioETipoTransacao((Usuario) session.getAttribute("usuarioLogado"), TipoTransacao.RECEITA));
		return mv;
	}
	
	@PostMapping("/adicionar")
	public ModelAndView adicionarReceita(@ModelAttribute Receita receita, @RequestParam String str_valor, 
			HttpServletRequest request, HttpSession session) 
	{
		receita.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		
		BigDecimal valor;
		try {
			valor = Util.getBigDecimalOf(str_valor);
			receita.setValor(valor);
		} catch (BigDecimalInvalidoException e) {
			return this.viewAdicionarReceita(session)
					.addObject("msgErroAdd", "O valor informado é inválido.")
					.addObject("receita", receita)
					.addObject("str_valor", str_valor);
		}
		
		try {
			this.receitaService.salvar(receita);
			
			this.logService.save(new Log(receita.getUsuario(), TipoLog.CADASTRO_RECEITA, LocalDateTime.now(), request.getRemoteAddr()));
		} catch (ReceitaException re) {
			return this.viewAdicionarReceita(session)
					.addObject("msgErroAdd", re.getMessage())
					.addObject("receita", receita)
					.addObject("str_valor", str_valor);
		}
		
		return this.viewAdicionarReceita(session)
				.addObject("msgSucessoAdd", "Receita salva com sucesso.");
	}
	
	@GetMapping("/categorias")
	public ModelAndView viewCategoriasReceitas(HttpSession session) {
		ModelAndView mv= new ModelAndView("usuario/receita/categorias");
		mv.addObject("catReceita", new Categoria());
		mv.addObject("listaCatReceita", this.categoriaService.listarTodasPorUsuarioETipoTransacao((Usuario) session.getAttribute("usuarioLogado"), TipoTransacao.RECEITA));
		return mv;
	}
	
	@PostMapping("/categorias")
	public ModelAndView adicionarCatReceita(@ModelAttribute Categoria catReceita, HttpServletRequest request, HttpSession session) {
		catReceita.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		
		try {
			this.receitaService.salvarCategoriaReceita(catReceita);
			
			this.logService.save(new Log(catReceita.getUsuario(),TipoLog.CADASTRO_CATEGORIA_RECEITA,LocalDateTime.now(),request.getRemoteAddr()));
		} catch (ReceitaException de) {
			return this.viewCategoriasReceitas(session).addObject("msgErroAdd", de.getMessage());
		}
		
		return this.viewCategoriasReceitas(session).addObject("msgSucessoAdd", "Categoria salva com sucesso.");
	}
	
	@GetMapping("/categorias/excluir")
	public String excluirCatReceita(@RequestParam Integer id, HttpSession session, HttpServletRequest request, 
			RedirectAttributes ra) 
	{
		try {
			Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
			Categoria catExistente = this.categoriaService.obterPorIdEUsuarioETipoTransacao(id, usuario, TipoTransacao.RECEITA)
													.orElseThrow(() -> new Exception("Categoria inexistente."));
			
			if (this.receitaService.existsByCategoria(catExistente)) {
				ra.addFlashAttribute("msgErroExcluir", "A categoria selecionada contém vínculos com outras informações.");
			} else {
				ra.addFlashAttribute("msgSucessoExcluir", "Categoria excluída com sucesso!");
				this.categoriaService.deletarPorId(id);
				
				this.logService.save(new Log(usuario, TipoLog.EXCLUSAO_CATEGORIA_RECEITA, LocalDateTime.now(), 
						Util.getUserIp(request)));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return "redirect:/receitas/categorias";
	}
	
	@GetMapping("/extrato")
	public ModelAndView viewHistoricoReceitas(HttpSession session) {
		ModelAndView mv = new ModelAndView("usuario/receita/extrato");
		
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		
		List<HistoricoTransacao> historicoReceitas = new ArrayList<>();
		
		List<Categoria> categoriasUsuario = this.categoriaService.listarTodasPorUsuarioETipoTransacao(usuario, TipoTransacao.RECEITA);
		for (Categoria categoria : categoriasUsuario) {
			List<Transacao> transacoesPorCategoria = this.transacoes.findAllByCategoriaOrderByDataDesc(categoria);
			historicoReceitas.add(new HistoricoTransacao(categoria, transacoesPorCategoria));
		}
		
		mv.addObject("historicoReceitas", historicoReceitas);
		
		return mv;
	}
	
}
