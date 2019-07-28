package com.jolteam.financas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jolteam.financas.enums.TipoTransacao;

@Entity
@Table(name = "transacoes")
public class Transacao {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;

	@Column(nullable = false) @Enumerated(EnumType.STRING)
	private TipoTransacao tipo;
	
	@ManyToOne(optional = false)
	private Categoria categoria;
	
	@Column(length = 50, nullable = false)
	private String descricao;
	
	@Column(nullable = false)
	private BigDecimal valor;
	
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime data;
	
	
	// construtores
	public Transacao() {}
	public Transacao(Usuario usuario, TipoTransacao tipo, Categoria categoria, String descricao,
			BigDecimal valor) {
		this.usuario = usuario;
		this.tipo = tipo;
		this.categoria = categoria;
		this.descricao = descricao;
		this.valor = valor;
		this.data = LocalDateTime.now();
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
	public TipoTransacao getTipo() {
		return tipo;
	}
	public void setTipoTransacao(TipoTransacao tipo) {
		this.tipo = tipo;
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
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
	
	@Override
	public String toString() {
		return "Transacao [id=" + id + ", usuario=" + usuario + ", tipo=" + tipo + ", categoria="
				+ categoria + ", descricao=" + descricao + ", valor=" + valor + ", data=" + data + "]";
	}
	
	
	@Override
	public boolean equals(Object outro) {
		if (outro instanceof Transacao) {
			if (this.descricao.equals(((Transacao) outro).getDescricao())) {
				return true;
			}
		}
		return false;
	}
	
}
