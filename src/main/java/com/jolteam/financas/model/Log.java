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

import com.jolteam.financas.enums.TiposLogs;

@Entity
@Table(name = "logs")
public class Log {

	// atributos/colunas
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;

	@Column(nullable = false) @Enumerated(EnumType.STRING)
	private TiposLogs tipo;
	
	@Column(nullable=false, columnDefinition = "datetime")
	private LocalDateTime data;
	
	@Column(length = 72, nullable = false)
	private String ip;

	
	// construtores
	public Log() {}
	public Log(Usuario usuario, TiposLogs tipo, LocalDateTime data, String ip) {
		this.usuario = usuario;
		this.tipo = tipo;
		this.data = data;
		this.ip = ip;
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
	public TiposLogs getTipo() {
		return tipo;
	}
	public void setTipo(TiposLogs tipo) {
		this.tipo = tipo;
	}
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
