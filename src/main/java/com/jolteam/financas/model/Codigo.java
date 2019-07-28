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

import com.jolteam.financas.enums.TipoCodigo;

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
	private TipoCodigo tipo;
	
	
	public Codigo() {}
	public Codigo(Usuario usuario, TipoCodigo tipo) {
		this.usuario = usuario;
		this.tipo = tipo;
	}
	
	
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public TipoCodigo getTipo() {
		return tipo;
	}
	public void setTipo(TipoCodigo tipo) {
		this.tipo = tipo;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
}
