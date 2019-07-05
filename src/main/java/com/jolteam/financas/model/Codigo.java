package com.jolteam.financas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.jolteam.financas.enums.TiposCodigos;

@Entity
@Table(name = "codigos")
public class Codigo {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private String codigo;
	
	@ManyToOne(optional = false)
	private Usuario usuario;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TiposCodigos tipo;
	
	
	public Codigo() {}
	public Codigo(Usuario usuario, TiposCodigos tipo) {
		this.usuario = usuario;
		this.tipo = tipo;
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
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
}
