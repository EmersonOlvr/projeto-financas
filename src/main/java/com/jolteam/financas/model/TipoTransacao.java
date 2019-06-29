package com.jolteam.financas.model;

public enum TipoTransacao {
	
	RECEITA("Receita"), DESPESA("Despesa"), COFRE("Cofre");
	
	public String texto;
	TipoTransacao(String texto) {
		this.texto = texto;
	}
	
}
