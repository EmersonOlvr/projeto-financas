package com.jolteam.financas.model;

public enum TiposTransacoes {
	
	RECEITA("Receita"), DESPESA("Despesa"), COFRE("Cofre");
	
	public String valor;
	TiposTransacoes(String valor) {
		this.valor = valor;
	}
	
}
