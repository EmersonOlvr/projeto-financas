package com.jolteam.financas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cofres_transacoes")
public class CofreTransacao {

	// atributos/colunas
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Cofre cofre;
	
	@ManyToOne(optional = false)
	private Categoria categoria;
	
	@Column(nullable = false)
	private BigDecimal valor;
	
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime data;
	
	@Column(nullable = false, length = 150)
	private String descricao;

	
	// construtores
	public CofreTransacao() {}
	public CofreTransacao(Cofre cofre, Categoria categoria, BigDecimal valor, LocalDateTime data, String descricao) {
		this.cofre = cofre;
		this.categoria = categoria;
		this.valor = valor;
		this.data = data;
		this.descricao = descricao;
	}
	
	
	// getters e setters
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Cofre getCofre() {
		return cofre;
	}
	public void setCofre(Cofre cofre) {
		this.cofre = cofre;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
	@Override
	public String toString() {
		return "TransacaoCofre [id=" + id + ", cofre=" + cofre + ", categoria=" + categoria + ", valor=" + valor
				+ ", data=" + data + ", descricao=" + descricao + "]";
	}
	
}
