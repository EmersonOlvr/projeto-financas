package com.jolteam.financas.enums;

public enum TipoRelatorio {

	RECEITA("Receita"), DESPESA("Despesa"), AMBOS("Receita e Despesa");
	
	public String valor;
	TipoRelatorio(String valor) {
		this.valor = valor;
	}
	
}
