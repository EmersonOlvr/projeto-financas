package com.jolteam.financas.model.dto;

import com.jolteam.financas.enums.TipoRelatorio;

public class RelatorioForm {

	private int mes;
	private int ano;
	private TipoRelatorio tipoRelatorio;
	
	public RelatorioForm() {}
	public RelatorioForm(int mes, int ano) {
		this.mes = mes;
		this.ano = ano;
	}
	
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public TipoRelatorio getTipoRelatorio() {
		return tipoRelatorio;
	}
	public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
	
}
