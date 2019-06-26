package com.jolteam.financas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cofre {

	private String descricao;
	private BigDecimal totalAcumulado;
	private Categoria categoria;
	private LocalDateTime dataCriacao;
	
	
	// construtores
	public Cofre() {}
	public Cofre(String descricao, BigDecimal totalAcumulado, Categoria categoria, LocalDateTime dataCriacao) {
		this.descricao = descricao;
		this.totalAcumulado = totalAcumulado;
		this.categoria = categoria;
		this.dataCriacao = dataCriacao;
	}

	
	// getters e setters
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public BigDecimal getTotalAcumulado() {
		return totalAcumulado;
	}
	public void setTotalAcumulado(BigDecimal totalAcumulado) {
		this.totalAcumulado = totalAcumulado;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	
	@Override
	public String toString() {
		return "Cofre [descricao=" + descricao + ", totalAcumulado=" + totalAcumulado + ", categoriaNome=" + categoria.getNome()
				+ ", dataCriacao=" + dataCriacao + "]\n";
	}
	
	
	@Override
	public boolean equals(Object outro) {
		if (outro instanceof Cofre) {
			if (this.descricao.equals(((Cofre) outro).getDescricao())) {
				return true;
			}
		}
		return false;
	}
	
}
