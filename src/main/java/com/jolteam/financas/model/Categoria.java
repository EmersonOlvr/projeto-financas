package com.jolteam.financas.model;

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

@Entity
@Table(name = "categorias")
public class Categoria {

	// atributos/colunas
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;

	@Column(nullable = false) @Enumerated(EnumType.ORDINAL)
	private TipoTransacao tipo;
	
	@Column(length = 50, nullable = false)
	private String nome;
	
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime dataCriacao;

	
	// construtores
	public Categoria () {}
	public Categoria(Usuario usuario, TipoTransacao tipo, String nome, LocalDateTime dataCriacao) {
		this.usuario = usuario;
		this.tipo = tipo;
		this.nome = nome;
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
	public TipoTransacao getTipo() {
		return tipo;
	}
	public void setTipo(TipoTransacao tipo) {
		this.tipo = tipo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	
	@Override
	public String toString() {
		return "Categoria [id=" + id + ", usuario=" + usuario + ", tipo=" + tipo + ", nome=" + nome
				+ ", dataCriacao=" + dataCriacao + "]";
	}
	
}
