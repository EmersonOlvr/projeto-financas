package com.jolteam.financas.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.dao.LogDAO;
import com.jolteam.financas.dao.TransacaoPrincipalDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.TipoLog;
import com.jolteam.financas.model.TipoTransacao;
import com.jolteam.financas.model.TransacaoPrincipal;
import com.jolteam.financas.model.Usuario;

@Controller
@RequestMapping("/testes")
public class TesteController {

	@Autowired private UsuarioDAO usuarios;
	@Autowired private LogDAO logs;
	@Autowired private TransacaoPrincipalDAO transacoes;
	@Autowired private CategoriaDAO categorias;

	// ====================== Testes ======================
	/*
	 * Cofres
	 */
	@GetMapping("/cofres/{usuarioId}")
	public ModelAndView testeCofres(@PathVariable int usuarioId) {
		ModelAndView mv = new ModelAndView("/testes/cofres");
		
		List<Cofre> cofres = new ArrayList<>();
		/*Usuario usuario = this.usuarios.getOne(usuarioId);
		List<TransacaoPrincipal> transacoesComTipoCofre = this.transacoes.findAllByTipo(TipoTransacao.COFRE);
		List<TransacaoPrincipal> transacoesDoUsuarioComTipoCofre = this.transacoes.findAllByUsuarioAndTipo(
				usuario, TipoTransacao.COFRE);
		
		for (TransacaoPrincipal transacao : transacoesDoUsuarioComTipoCofre) {
			Cofre cofreAtual = new Cofre(usuario, transacao.getDescricao(), transacao.getValor(), transacao.getCategoria(), transacao.getData());
			if (cofres.contains(cofreAtual)) {
				int indexOfCofreAtual = cofres.indexOf(cofreAtual);
				
				Cofre cofreExistente = cofres.get(indexOfCofreAtual);
				cofreExistente.setTotalAcumulado(cofreExistente.getTotalAcumulado().add(transacao.getValor()));
				
				cofres.set(indexOfCofreAtual, cofreExistente);
			} else {
				cofres.add(cofreAtual);
			}
		}
		
		mv.addObject("extratoCofres", transacoesComTipoCofre);
		mv.addObject("extratoCofresDoUsuario", transacoesDoUsuarioComTipoCofre);
		mv.addObject("cofres", cofres);*/
		
		return mv;
	}
	
	/*
	 * Categorias
	 */
	@GetMapping("/categorias/{usuarioId}")
	public ModelAndView testeCategorias(@PathVariable int usuarioId) {
		ModelAndView mv = new ModelAndView("/testes/categorias");
		mv.addObject("categorias", categorias.findAll());
		mv.addObject("categoriasDoUsuario", categorias.findAllByUsuario(this.usuarios.getOne(usuarioId)));
		return mv;
	}
	@GetMapping("/categorias/adicionar/{usuarioId}/{tipoTransacao}/{nome}")
	public String testeAdicionarCategoria(@PathVariable int usuarioId, 
			@PathVariable TipoTransacao tipoTransacao, 
			@PathVariable String nome, 
			HttpServletRequest request) 
	{
		TipoLog tipoLog = null;
		if (tipoTransacao.equals(TipoTransacao.RECEITA)) {
			tipoLog = TipoLog.CADASTRO_CATEGORIA_RECEITA;
		} else if (tipoTransacao.equals(TipoTransacao.DESPESA)) {
			tipoLog = TipoLog.CADASTRO_CATEGORIA_DESPESA;
		} else if (tipoTransacao.equals(TipoTransacao.COFRE)) {
			tipoLog = TipoLog.CADASTRO_CATEGORIA_COFRE;
		}
		
		Categoria categoria = new Categoria(this.usuarios.getOne(usuarioId), tipoTransacao, nome, LocalDateTime.now());
		Log log = new Log(this.usuarios.getOne(usuarioId), tipoLog, LocalDateTime.now(), request.getRemoteAddr());
		
		this.categorias.save(categoria);
		this.logs.save(log);
		
		return "redirect:/testes/categorias/"+usuarioId;
	}
	/*
	 * Datas e horas
	 */
	@GetMapping("/data")
	public ModelAndView testeDatasEHoras() {
		ModelAndView mv = new ModelAndView("/testes/data");
		mv.addObject("dataAtual", LocalDateTime.now());
		return mv;
	}
	@GetMapping("/ip")
	public ModelAndView testeIPs(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/testes/ip");
		mv.addObject("userIP", request.getRemoteAddr());
		return mv;
	}
	
}
