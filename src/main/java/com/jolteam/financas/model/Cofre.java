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
@Table(name="cofres")
public class Cofre {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Usuario usuario;
	
	@Column(nullable = false,length = 150)
	private String finalidade;
	
	@Column(nullable=false)
	private BigDecimal totalDesejado;
	
	@Transient
	private BigDecimal totalAcumulado;
	
	@Column(nullable = false,columnDefinition="datetime")
	private LocalDateTime dataCriacao;
	
	public Cofre() {}
	public Cofre(Usuario usuario, String finalidade, BigDecimal totalDesejado, LocalDateTime dataCriacao) {
		this.usuario = usuario;
		this.finalidade = finalidade;
		this.totalDesejado = totalDesejado;
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
	public String getFinalidade() {
		return finalidade;
	}
	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}
	public BigDecimal getTotalDesejado() {
		return totalDesejado;
	}
	public void setTotalDesejado(BigDecimal totalDesejado) {
		this.totalDesejado = totalDesejado;
	}
	public BigDecimal getTotalAcumulado() {
		return totalAcumulado;
	}
	public void setTotalAcumulado(BigDecimal totalAcumulado) {
		this.totalAcumulado = totalAcumulado;
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
			if (this.finalidade.equals(((Cofre) outro).getFinalidade())) {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		return "Cofre [id=" + id + ", usuario=" + (usuario != null ? usuario.getId() : null) 
				+ ", finalidade=" + finalidade + ", totalDesejado=" + totalDesejado 
				+ ", totalAcumulado=" + totalAcumulado + ", dataCriacao=" + dataCriacao + "]";
	}
	
}
