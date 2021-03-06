package com.jolteam.financas.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "fotos")
public class Foto {
	
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private String id;
	
	private String nome;
	
	private String tipo;
	
	@Lob
	private byte[] conteudo;
	
	private String link;

	
	public Foto() {}
	public Foto( String nome, String tipo, byte[] conteudo, String link) {
		this.nome = nome;
		this.tipo = tipo;
		this.conteudo = conteudo;
		this.link=link;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public byte[] getConteudo() {
		return conteudo;
	}
	public void setConteudo(byte[] conteudo) {
		this.conteudo = conteudo;
	}
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	@Override
	public String toString() {
		return "Foto [id=" + id +", nome=" + nome + ", tipo=" + tipo + ", link=" + link + "]";
	}
	
	
	
	
}
