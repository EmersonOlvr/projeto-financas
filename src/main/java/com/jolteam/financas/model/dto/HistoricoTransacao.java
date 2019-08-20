package com.jolteam.financas.model.dto;

import java.util.List;

import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Transacao;

public class HistoricoTransacao {

	private Categoria categoria;
	private List<Transacao> transacoes;
	
	public HistoricoTransacao() {}
	public HistoricoTransacao(Categoria categoria, List<Transacao> transacoes) {
		this.categoria = categoria;
		this.transacoes = transacoes;
	}
	
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public List<Transacao> getTransacoes() {
		return transacoes;
	}
	public void setTransacoes(List<Transacao> transacoes) {
		this.transacoes = transacoes;
	}
	
}
