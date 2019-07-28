package com.jolteam.financas.enums;

public enum TipoCodigo {

	ATIVACAO_CONTA("Ativacão de Conta"), ALTERACAO_EMAIL("Alteração de E-mail"), REDEFINICAO_SENHA("Redefinição de Senha");
	
	public String valor;
	TipoCodigo(String valor) {
		this.valor = valor;
	}
	
}
