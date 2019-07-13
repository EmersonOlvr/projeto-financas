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
	
	@ManyToOne(optional = false)
	private Categoria categoria;
	
	@Column(nullable = false,columnDefinition="datetime")
	private LocalDateTime dataCriacao;
	
	
	private BigDecimal valorInicial;
	
	public Cofre() {}
	
	public Cofre(Integer id, Usuario usuario, String descricao, Categoria categoria, LocalDateTime dataCriacao,BigDecimal valorInicial) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.descricao = descricao;
		this.categoria = categoria;
		this.dataCriacao = dataCriacao;
		this.valorInicial= valorInicial;
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
	public BigDecimal getValorInicial() {
		return valorInicial;
	}

	public void setValorInicial(BigDecimal valorInicial) {
		this.valorInicial = valorInicial;
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
