package com.jolteam.financas.enums;

public enum TipoLog {
	
	LOGIN("Login"), 
	
	CADASTRO_RECEITA("Cadastro de Receita"), 
	CADASTRO_DESPESA("Cadastro de Despesa"), 
	CADASTRO_COFRE("Cadastro de Cofre"), 
	
	CADASTRO_CATEGORIA_RECEITA("Cadastro de Categoria de Receita"), 
	CADASTRO_CATEGORIA_DESPESA("Cadastro de Categoria de Despesa"), 
	CADASTRO_CATEGORIA_COFRE("Cadastro de Categoria de Cofre"), 
	
	EXCLUSAO_RECEITA("Exclusão de Receita"), 
	EXCLUSAO_DESPESA("Exclusão de Despesa"), 
	EXCLUSAO_COFRE("Exclusão de Cofre"), 
	
	EXCLUSAO_CATEGORIA_RECEITA("Exclusão de Categoria de Receita"), 
	EXCLUSAO_CATEGORIA_DESPESA("Exclusão de Categoria de Despesa"), 
	EXCLUSAO_CATEGORIA_COFRE("Exclusão de Categoria de Cofre"), 
	
	CADASTRO_TRANSACAO_POSITIVA_COFRE("Cadastro de Transação Positiva de Cofre"), 
	CADASTRO_TRANSACAO_NEGATIVA_COFRE("Cadastro de Transação Negativa de Cofre"), 
	
	EXCLUSAO_TRANSACAO_POSITIVA_COFRE("Exclusão de Transação com Valor Positivo de Cofre"), 
	EXCLUSAO_TRANSACAO_NEGATIVA_COFRE("Exclusão de Transação com Valor Negativo de Cofre");
	
	public String valor;
	TipoLog(String valor) {
		this.valor = valor;
	}
	
}
