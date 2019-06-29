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
import javax.persistence.Transient;

@Entity
@Table(name = "cofres")
public class Cofre {

	// atributos/colunas
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(optional = false)
	private Usuario usuario;

	@Column(nullable = false, length = 50)
	private String nome;

	@Column(nullable = false, length = 150)
	private String descricao;

	@Transient
	private BigDecimal totalAcumulado;

	@Column(nullable = false)
	private LocalDateTime dataCriacao;

	@Transient
	private Categoria categoria;

	
	// construtores
	public Cofre() {}
	public Cofre(Usuario usuario, String nome, String descricao, LocalDateTime dataCriacao, 
			BigDecimal totalAcumulado, Categoria categoria) 
	{
		this.usuario = usuario;
		this.nome = nome;
		this.descricao = descricao;
		this.totalAcumulado = totalAcumulado;
		this.categoria = categoria;
		this.dataCriacao = dataCriacao;
	}
	public Cofre(Usuario usuario, String nome, String descricao, LocalDateTime dataCriacao) {
		this.usuario = usuario;
		this.nome = nome;
		this.descricao = descricao;
		this.dataCriacao = dataCriacao;
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
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
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

	
	@Override
	public String toString() {
		return "Cofre [id=" + id + ", usuario=" + usuario + ", nome=" + nome + ", descricao=" + descricao
				+ ", totalAcumulado=" + totalAcumulado + "]";
	}

	
	@Override
	public boolean equals(Object outro) {
		if (outro instanceof Cofre) {
			if (this.descricao.equals(((Cofre) outro).getNome())) {
				return true;
			}
		}
		return false;
	}

}
