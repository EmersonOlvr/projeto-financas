package com.jolteam.financas.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="usuarios")
public class Usuario {

	// atributos/colunas
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=50, nullable=false)
	private String nome;
	
	@Column(length=50, nullable=false)
	private String sobrenome;
	
	@Column(length=64, unique=true, nullable=false)
	private String email;
	
	@Column(columnDefinition="bit(1)", nullable=false)
	private Boolean ativado;
	
	@Column(nullable=false, length = 70)
	private String senha;
	
	@Transient
	private String senhaRepetida;
	
	@Column(nullable = false)
	private Short permissao;
	
	@Column(nullable=false, columnDefinition = "datetime")
	private LocalDateTime registroData;
	
	@Column(length=72, nullable=false)
	private String registroIp;
	
	
	// construtores
	public Usuario() {}
	public Usuario(String nome, String sobrenome, String email, Boolean contaAtivada, 
			String senha, String senhaRepetida, Short permissao, LocalDateTime registroData, String registroIp) {
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.email = email;
		this.ativado = contaAtivada;
		this.senha = senha;
		this.senhaRepetida = senhaRepetida;
		this.permissao = permissao;
		this.registroData = registroData;
		this.registroIp = registroIp;
	}
	
	
	// getters e setters
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSobrenome() {
		return sobrenome;
	}
	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Boolean isAtivado() {
		return ativado;
	}
	public void setAtivado(Boolean ativado) {
		this.ativado = ativado;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getSenhaRepetida() {
		return senhaRepetida;
	}
	public void setSenhaRepetida(String senhaRepetida) {
		this.senhaRepetida = senhaRepetida;
	}
	public Short getPermissao() {
		return permissao;
	}
	public void setPermissao(Short permissao) {
		this.permissao = permissao;
	}
	public LocalDateTime getRegistroData() {
		return registroData;
	}
	public void setRegistroData(LocalDateTime registroData) {
		this.registroData = registroData;
	}
	public String getRegistroIp() {
		return registroIp;
	}
	public void setRegistroIp(String registroIp) {
		this.registroIp = registroIp;
	}
	
	
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nome=" + nome + ", sobrenome=" + sobrenome + ", email=" + email
				+ ", contaAtivada=" + ativado + ", senha=" + senha + ", senhaRepetida=" + senhaRepetida
				+ ", permissao=" + permissao + ", registroData=" + registroData + ", registroIp=" + registroIp + "]";
	}
	
}
