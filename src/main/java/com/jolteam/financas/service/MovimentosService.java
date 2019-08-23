package com.jolteam.financas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;

@Service
public class MovimentosService {

	@Autowired private TransacaoDAO transacoes;
	
	public List<Transacao> listarTodas() {
		return this.transacoes.findAll();
	}
	public List<Transacao> listarPorUsuarioeTipo(Usuario usuario,TipoTransacao tipo){
	
		return this.transacoes.findAllByUsuarioAndTipo(usuario, tipo);
	}
	public List<Transacao> listarTodasPorUsuario(Usuario usuario) {
		return this.transacoes.findAllByUsuario(usuario);
	}
	
	public List<Transacao> obterReceitasPorMes(Month mes, Usuario usuario) {
		LocalDate data = LocalDate.of(LocalDate.now().getYear(), mes, 1);
		
		return this.transacoes.findAllByMesAndTipoAndUsuario(data, TipoTransacao.RECEITA, usuario);
	}
	public List<Transacao> obterDespesasPorMes(Month mes, Usuario usuario) {
		LocalDate data = LocalDate.of(LocalDate.now().getYear(), mes, 1);
		
		return this.transacoes.findAllByMesAndTipoAndUsuario(data, TipoTransacao.DESPESA, usuario);
	}
	
	public Optional<Transacao> buscarPorIdEUsuario(Integer id, Usuario usuario) {
		return this.transacoes.findByIdAndUsuario(id, usuario);
	}
	
	public void deletarPorIdEUsuario(Integer id, Usuario usuario) {
		this.transacoes.deleteByIdAndUsuario(id, usuario);
	}
	
	public BigDecimal totalReceitaAcumuladaDe(Usuario usuario) {
		BigDecimal totalAcumulado = new BigDecimal("0");
		List<Transacao> transacoes = this.transacoes.findAllByUsuarioAndTipo(usuario, TipoTransacao.RECEITA);
		for (Transacao transacao : transacoes) {
			totalAcumulado = totalAcumulado.add(transacao.getValor());
		}
		return totalAcumulado;
	}
	
	public BigDecimal totalDespesaAcumuladaDe(Usuario usuario) {
		BigDecimal totalAcumulado = new BigDecimal("0");
		List<Transacao> transacoes = this.transacoes.findAllByUsuarioAndTipo(usuario, TipoTransacao.DESPESA);
		for (Transacao transacao : transacoes) {
			totalAcumulado = totalAcumulado.add(transacao.getValor());
		}
		return totalAcumulado;
	}
	
	public Page<Transacao> findAllByUsuario(Usuario usuario,Pageable page) {
		return transacoes.findAllByUsuario(usuario,page);
	}
	
}
