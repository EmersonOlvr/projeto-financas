package com.jolteam.financas.model.dto;

import java.math.BigDecimal;

import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Usuario;

public class Despesa {

	private Usuario usuario;
	private Categoria categoria;
	private String descricao;
	private BigDecimal valor;
	
	//Construtores
	public Despesa() {}
	
	public Despesa(Usuario usuario,Categoria categoria,BigDecimal valor,String descricao) {
		this.usuario=usuario;
		this.categoria=categoria;
		this.valor=valor;
		this.descricao=descricao;
	}
	//Getters e Setters
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
		return "Despesa [usuario=" + usuario + ", categoria=" + categoria + ", descricao=" + descricao + ", valor="
				+ valor + "]";
	}
	
}
