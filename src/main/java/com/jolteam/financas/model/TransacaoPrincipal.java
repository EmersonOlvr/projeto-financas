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
@Table(name = "transacoes_principais")
public class TransacaoPrincipal {

	// atributos/colunas
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;

	@ManyToOne(optional = false)
	private Categoria categoria;

	@Column(nullable = false)
	private BigDecimal valor;

	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime data;
	
	@Column(length = 50, nullable = false)
	private String descricao;
	
	
	// construtores
	public TransacaoPrincipal() {}
	public TransacaoPrincipal(Usuario usuario, Categoria categoria, String descricao, BigDecimal valor, LocalDateTime data) {
		this.usuario = usuario;
		this.categoria = categoria;
		this.descricao = descricao;
		this.valor = valor;
		this.data = data;
	}


	// getters e setters
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
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
		return "TransacaoPrincipal [id=" + id + ", usuario=" + usuario + ", categoria=" + categoria + ", descricao="
				+ descricao + ", valor=" + valor + ", data=" + data + "]";
	}
	
}
