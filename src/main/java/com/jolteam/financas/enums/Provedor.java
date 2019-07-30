package com.jolteam.financas.enums;

public enum Provedor {

	LOCAL("Local"), GOOGLE("Google"), FACEBOOK("Facebook"), GITHUB("GitHub");
	
	private String valor;
	Provedor(String valor) {
		this.valor = valor;
	}
	
	public String getValor() {
		return valor;
	}
    
}
