package com.jolteam.financas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jolteam.financas.enums.TiposCodigos;

@Entity
@Table(name = "codigos")
public class Codigo {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TiposCodigos tipo;
	
	@Column(nullable = false, length = 7)
	private int codigo;
	
	
	public Codigo() {}
	public Codigo(Usuario usuario, TiposCodigos tipo, int codigo) {
		this.usuario = usuario;
		this.tipo = tipo;
		this.codigo = codigo;
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
	public TiposCodigos getTipo() {
		return tipo;
	}
	public void setTipo(TiposCodigos tipo) {
		this.tipo = tipo;
	}
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	
}
