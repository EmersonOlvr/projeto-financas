package com.jolteam.financas.model;

import java.math.BigDecimal;

public class Receita {

	private Usuario usuario;
	private Categoria categoria;
	private String descricao;
	private BigDecimal valor;
	
	
	// construtores
	public Receita() {}
	public Receita(Usuario usuario, Categoria categoria, BigDecimal valor, String descricao) {
		this.usuario = usuario;
		this.valor = valor;
		this.categoria = categoria;
		this.descricao = descricao;
	}
	
	
	// getters e setters
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	
	@Override
	public String toString() {
		return "Receita [usuario=" + usuario + ", categoria=" + categoria + ", descricao=" + descricao + ", valor="
				+ valor + "]";
	}
	
}
