package com.jolteam.financas.enums;

public enum Provedor {

	LOCAL("Local"), GOOGLE("Google");
	
	private String valor;
	Provedor(String valor) {
		this.valor = valor;
	}
	
	public String getValor() {
		return valor;
	}
    
}
