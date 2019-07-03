package com.jolteam.financas.enums;

public enum TiposTransacoes {
	
	RECEITA("Receita"), DESPESA("Despesa");
	
	public String valor;
	TiposTransacoes(String valor) {
		this.valor = valor;
	}
	
}
