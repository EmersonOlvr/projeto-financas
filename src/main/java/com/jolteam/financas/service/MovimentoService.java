package com.jolteam.financas.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;

@Service
public class MovimentoService {

	@Autowired private TransacaoDAO transacoes;
	
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
	
}
