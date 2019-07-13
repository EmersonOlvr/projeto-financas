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
@Table(name="cofres")
public class Cofre {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;
	
	@Column(nullable = false,length = 150)
	private String descricao;
	
	@Column(nullable = false,columnDefinition="datetime")
	private LocalDateTime dataCriacao;
	
	@Column(nullable=false)
	private BigDecimal totalDesejado;
	
	public Cofre() {}
	
	public Cofre(Integer id, Usuario usuario, String descricao, LocalDateTime dataCriacao,BigDecimal totalDesejado) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.descricao = descricao;
		this.dataCriacao = dataCriacao;
		this.totalDesejado= totalDesejado;
	}
	
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
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public BigDecimal getTotalDesejado() {
		return totalDesejado;
	}

	public void setTotalDesejado(BigDecimal totalDesejado) {
		this.totalDesejado = totalDesejado;
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
