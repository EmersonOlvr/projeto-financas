package com.jolteam.financas.model;

public class Teste {

	private String nome;
	private Integer idade;
	
	public Teste(String nome, Integer idade) {
		super();
		this.nome = nome;
		this.idade = idade;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	@Override
	public String toString() {
		return "Teste [nome=" + nome + ", idade=" + idade + "]";
	}
	
}
