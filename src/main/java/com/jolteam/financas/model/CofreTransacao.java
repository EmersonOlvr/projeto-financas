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
@Table(name="cofre_transacoes")
public class CofreTransacao {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Cofre cofre;
	
	@Column(nullable = false)
	private BigDecimal valor;
	
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime data;

	
	public CofreTransacao() {}
	public CofreTransacao(Cofre cofre, BigDecimal valor, LocalDateTime data) {
		this.cofre = cofre;
		this.valor = valor;
		this.data = data;
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Cofre getCofre() {
		return cofre;
	}
	public void setCofre(Cofre cofre) {
		this.cofre = cofre;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime dataCriacao) {
		this.data = dataCriacao;
	}
	
	
	@Override
	public String toString() {
		return "CofreTransacao [id=" + id + ", cofre=" + cofre + ", valor=" + valor + ", data=" + data + "]";
	}
	
}
