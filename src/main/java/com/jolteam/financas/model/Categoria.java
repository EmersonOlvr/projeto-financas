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

import com.jolteam.financas.enums.TipoTransacao;

@Entity
@Table(name = "categorias")
public class Categoria {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;

	@Column(nullable = false) @Enumerated(EnumType.STRING)
	private TipoTransacao tipoTransacao;
	
	@Column(length = 50, nullable = false)
	private String nome;
	
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime dataCriacao;

	
	// construtores
	public Categoria () {}
	public Categoria(Usuario usuario, TipoTransacao tipoTransacao, String nome,
			LocalDateTime dataCriacao) {
		this.usuario = usuario;
		this.tipoTransacao = tipoTransacao;
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
	public TipoTransacao getTipoTransacao() {
		return tipoTransacao;
	}
	public void setTipoTransacao(TipoTransacao tipoTransacao) {
		this.tipoTransacao = tipoTransacao;
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
		return "Categoria [id=" + id + ", usuario=" + usuario + ", tipoTransacao=" + tipoTransacao + ", nome=" + nome
				+ ", dataCriacao=" + dataCriacao + "]";
	}
	
}
