package com.jolteam.financas.model.dto;

import java.util.List;

import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.CofreTransacao;

public class HistoricoCofreTransacao {

	private Cofre cofre;
	private List<CofreTransacao> transacoes;
	
	public HistoricoCofreTransacao() {}
	public HistoricoCofreTransacao(Cofre cofre, List<CofreTransacao> transacoes) {
		this.cofre = cofre;
		this.transacoes = transacoes;
	}
	
	public Cofre getCofre() {
		return cofre;
	}
	public void setCofre(Cofre cofre) {
		this.cofre = cofre;
	}
	public List<CofreTransacao> getTransacoes() {
		return transacoes;
	}
	public void setTransacoes(List<CofreTransacao> transacoes) {
		this.transacoes = transacoes;
	}
	
}
