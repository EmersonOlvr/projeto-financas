package com.jolteam.financas.enums;

public enum TipoTransacao {
	
	RECEITA("Receita"), DESPESA("Despesa");
	
	public String valor;
	TipoTransacao(String valor) {
		this.valor = valor;
	}
	
}
