package com.jolteam.financas.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
@Entity
public class Cofre {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;
	@Column(nullable = false,length = 150)
	private String descricao;
	
	@ManyToOne(optional = false)
	private Categoria categoria;
	
	@Column(nullable = false)
	private LocalDateTime dataCriacao;
	
	public Cofre() {}
	
	public Cofre(Integer id, Usuario usuario, String descricao, Categoria categoria, LocalDateTime dataCriacao) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.descricao = descricao;
		this.categoria = categoria;
		this.dataCriacao = dataCriacao;
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
	public boolean equals(Object outro) {
		if (outro instanceof Cofre) {
			if (this.descricao.equals(((Cofre) outro).getDescricao())) {
				return true;
			}
		}
		return false;
	}
	
}
