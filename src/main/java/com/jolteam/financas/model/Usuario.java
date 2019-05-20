package com.jolteam.financas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.jolteam.financas.errorgroups.usuario.*;

@Entity
@Table(name="usuarios")
public class Usuario {

	// atributos/colunas
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=50, nullable=false)
	@NotBlank(message="Insira um nome.", groups=NomeNotBlankGroup.class)
	@Pattern(regexp="^[A-zÀ-ú ]*$", message="Nome inválido: somente letras e espaços são permitidos.", groups=NomePatternGroup.class)
	@Size(min=2, message="Nome inválido: mínimo de {min} letras.", groups=NomeSizeMinGroup.class)
	@Size(max=50, message="Nome inválido: máximo de {max} letras.", groups=NomeSizeMaxGroup.class)
	private String nome;
	
	@Column(length=50, nullable=false)
	@NotBlank(message="Insira um sobrenome.", groups=SobrenomeNotBlankGroup.class)
	@Pattern(regexp="^[A-zÀ-ú ]*$", message="Sobrenome inválido: somente letras e espaços são permitidos.", groups=SobrenomePatternGroup.class)
	@Size(min=2, message="Sobrenome inválido: mínimo de {min} letras.", groups=SobrenomeSizeMinGroup.class)
	@Size(max=50, message="Sobrenome inválido: máximo de {max} letras.", groups=SobrenomeSizeMaxGroup.class)
	private String sobrenome;
	
	@Column(length=64, unique=true, nullable=false)
	@NotBlank(message="Insira um e-mail.", groups=EmailNotBlankGroup.class)
	@Pattern(regexp="^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[A-z]{2,})$", message="E-mail em formato inválido.", groups=EmailPatternGroup.class)
	@Size(max=64, message="E-mail inválido: máximo de {max} caracteres.", groups=EmailSizeMaxGroup.class)
	private String email;
	
	@Column(columnDefinition="bit(1)", nullable=false)
	private Boolean emailAtivado;
	
	@Column(length=64)
	private String emailPendente;
	
	@Column(length=4)
	private String codEmail;
	
	@Column(length=4)
	private String codEmailPendente;
	
	@Column(nullable=false)
	@Size(min=6, message="Senha muito curta. Mínimo de 6 caracteres.", groups=SenhaSizeMinGroup.class)
	@NotBlank(message="A senha não pode conter apenas espaços.", groups=SenhaNotBlankGroup.class)
	@Size(max=255, message="Senha muito grande. Máximo de 255 caracteres.", groups=SenhaSizeMaxGroup.class)
	private String senha;
	
	@Transient
	@NotBlank(message="Por favor, repita a senha.", groups=SenhaRepetidaNotBlankGroup.class)
	private String senhaRepetida;
	
	@Column(columnDefinition="tinyint(1)", nullable=false)
	private Integer permissao;
	
	@Column(columnDefinition="datetime", nullable=false)
	private String registroData;
	
	@Column(length=72)
	private String registroIp;
	
	@Column(columnDefinition="datetime")
	private String ultimoAcesso;
	
	@Column(length=72)
	private String ultimoIp;

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
	public Boolean getEmailAtivado() {
		return emailAtivado;
	}
	public void setEmailAtivado(Boolean emailAtivado) {
		this.emailAtivado = emailAtivado;
	}
	public String getEmailPendente() {
		return emailPendente;
	}
	public void setEmailPendente(String emailPendente) {
		this.emailPendente = emailPendente;
	}
	public String getCodEmail() {
		return codEmail;
	}
	public void setCodEmail(String codEmail) {
		this.codEmail = codEmail;
	}
	public String getCodEmailPendente() {
		return codEmailPendente;
	}
	public void setCodEmailPendente(String codEmailPendente) {
		this.codEmailPendente = codEmailPendente;
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
	public Integer getPermissao() {
		return permissao;
	}
	public void setPermissao(Integer permissao) {
		this.permissao = permissao;
	}
	public String getRegistroData() {
		return registroData;
	}
	public void setRegistroData(String registroData) {
		this.registroData = registroData;
	}
	public String getRegistroIp() {
		return registroIp;
	}
	public void setRegistroIp(String registroIp) {
		this.registroIp = registroIp;
	}
	public String getUltimoAcesso() {
		return ultimoAcesso;
	}
	public void setUltimoAcesso(String ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}
	public String getUltimoIp() {
		return ultimoIp;
	}
	public void setUltimoIp(String ultimoIp) {
		this.ultimoIp = ultimoIp;
	}
	
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nome=" + nome + ", sobrenome=" + sobrenome + ", email=" + email
				+ ", emailAtivado=" + emailAtivado + ", emailPendente=" + emailPendente + ", codEmail=" + codEmail
				+ ", codEmailPendente=" + codEmailPendente + ", senha=" + senha + ", senhaRepetida=" + senhaRepetida
				+ ", permissao=" + permissao + ", registroData=" + registroData + ", registroIp=" + registroIp
				+ ", ultimoAcesso=" + ultimoAcesso + ", ultimoIp=" + ultimoIp + "]";
	}
	
}
