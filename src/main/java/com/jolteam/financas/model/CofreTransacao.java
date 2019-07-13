package com.jolteam.financas.model;

import java.math.BigDecimal;
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

import com.jolteam.financas.enums.TiposTransacoes;

@Entity
@Table(name="cofre_transacao")
public class CofreTransacao {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(optional = false)
	private Cofre cofre;
	
	@Column(nullable=false) @Enumerated(EnumType.STRING)
	private TiposTransacoes tipoTransacoes;
	
	@Column(nullable = false)
	private BigDecimal valor;
	
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime dataCriacao;
	
	@Column(nullable = false,length = 150)
	private String descricao;

	public CofreTransacao() {}
	
	public CofreTransacao(Integer id, Cofre cofre, TiposTransacoes tipoTransacoes, BigDecimal valor,
			LocalDateTime dataCriacao, String descricao) {
		super();
		this.id = id;
		this.cofre = cofre;
		this.tipoTransacoes = tipoTransacoes;
		this.valor = valor;
		this.dataCriacao = dataCriacao;
		this.descricao = descricao;
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

	public TiposTransacoes getTipoTransacoes() {
		return tipoTransacoes;
	}

	public void setTipoTransacoes(TiposTransacoes tipoTransacoes) {
		this.tipoTransacoes = tipoTransacoes;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return "CofreTransacao [id=" + id + ", cofre=" + cofre + ", tipoTransacoes=" + tipoTransacoes + ", valor="
				+ valor + ", dataCriacao=" + dataCriacao + ", descricao=" + descricao + "]";
	}
	
	
	

}
