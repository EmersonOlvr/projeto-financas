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
import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.TiposLogs;
import com.jolteam.financas.model.TiposTransacoes;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;

@Controller
@RequestMapping("/testes")
public class TesteController {

	@Autowired private UsuarioDAO usuarios;
	@Autowired private LogDAO logs;
	@Autowired private TransacaoDAO transacoes;
	@Autowired private CategoriaDAO categorias;

	// ====================== Testes ======================
	/*
	 * Transações
	 */
	@GetMapping("/transacao/adicionar/{usuarioId}/{tipo}/{categoriaId}/{descricao}/{valor}")
	public String testeAdicionarTransacao(@PathVariable int usuarioId, @PathVariable int categoriaId, 
			@PathVariable TiposTransacoes tipo, @PathVariable String descricao, @PathVariable BigDecimal valor) 
	{
		this.transacoes.save(new Transacao(
				this.usuarios.getOne(usuarioId), 
				tipo, 
				this.categorias.getOne(categoriaId), 
				descricao, 
				valor
				));
		
		return "redirect:/testes/cofres/"+usuarioId;
	}
	
	/*
	 * Cofres
	 */
	@GetMapping("/cofres/{usuarioId}")
	public ModelAndView testeCofres(@PathVariable int usuarioId) {
		ModelAndView mv = new ModelAndView("/testes/cofres");
		
		List<Cofre> cofres = new ArrayList<>();
		List<Transacao> transacoesComTipoCofre = this.transacoes.findAllByTipo(TiposTransacoes.COFRE);
		List<Transacao> transacoesDoUsuarioComTipoCofre = this.transacoes.findAllByUsuarioAndTipo(
				this.usuarios.getOne(usuarioId), TiposTransacoes.COFRE);
		
		for (Transacao transacao : transacoesDoUsuarioComTipoCofre) {
			Cofre cofreAtual = new Cofre(transacao.getDescricao(), transacao.getValor(), transacao.getCategoria(), transacao.getData());
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
		mv.addObject("cofres", cofres);
		
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
			@PathVariable TiposTransacoes tipoTransacao, 
			@PathVariable String nome, 
			HttpServletRequest request) 
	{
		TiposLogs tipoLog = null;
		if (tipoTransacao.equals(TiposTransacoes.RECEITA)) {
			tipoLog = TiposLogs.CADASTRO_CATEGORIA_RECEITA;
		} else if (tipoTransacao.equals(TiposTransacoes.DESPESA)) {
			tipoLog = TiposLogs.CADASTRO_CATEGORIA_DESPESA;
		} else if (tipoTransacao.equals(TiposTransacoes.COFRE)) {
			tipoLog = TiposLogs.CADASTRO_CATEGORIA_COFRE;
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
	/*
	 * Adicionar Transação
	 */
	@GetMapping("/add-transacao/{usuarioId}")
	public ModelAndView testeViewAdicionarTransacao(@PathVariable int usuarioId) {
		ModelAndView mv = new ModelAndView("/testes/add-transacao");
		Optional<Usuario> usuario = this.usuarios.findById(usuarioId);
		
		if (usuario.isPresent()) {
			mv.addObject("categorias", this.categorias.findAllByUsuario(usuario.get()));
		} else {
			List<Categoria> listaCategorias = this.categorias.findAll();
			mv.addObject("categorias", listaCategorias);
		}
		
		return mv;
	}
	@PostMapping("/add-transacao/{usuarioId}")
	public String testeAddTransacao(@PathVariable int usuarioId, @RequestParam int categoria_id, @RequestParam TiposTransacoes tipo, 
			@RequestParam String descricao, @RequestParam BigDecimal valor) 
	{
		Transacao transacao = new Transacao(this.usuarios.getOne(usuarioId), tipo, this.categorias.getOne(categoria_id), descricao, valor);
		this.transacoes.save(transacao);
		return "redirect:/testes/add-transacao/"+usuarioId;
	}
	
}
