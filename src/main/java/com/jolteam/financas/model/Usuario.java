package com.jolteam.financas.model;

import java.time.LocalDateTime;

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
	@Size(min=2, max=50, message="Nome inválido: mínimo {min} e máximo de {max} letras.", groups=NomeSizeGroup.class)
	private String nome;
	
	@Column(length=50, nullable=false)
	@NotBlank(message="Insira um sobrenome.", groups=SobrenomeNotBlankGroup.class)
	@Pattern(regexp="^[A-zÀ-ú ]*$", message="Sobrenome inválido: somente letras e espaços são permitidos.", groups=SobrenomePatternGroup.class)
	@Size(min=2, max=50, message="Sobrenome inválido: mínimo {min} e máximo de {max} letras.", groups=SobrenomeSizeGroup.class)
	private String sobrenome;
	
	@Column(length=64, unique=true, nullable=false)
	@NotBlank(message="Insira um e-mail.", groups=EmailNotBlankGroup.class)
	@Pattern(regexp="^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[A-z]{2,})$", message="E-mail em formato inválido.", groups=EmailPatternGroup.class)
	@Size(max=64, message="E-mail inválido: máximo de {max} caracteres.", groups=EmailSizeMaxGroup.class)
	private String email;
	
	@Column(columnDefinition="bit(1)", nullable=false)
	private Boolean contaAtivada;
	
	@Column(nullable=false)
	@Size(min=6, message="Senha muito curta. Mínimo de 6 caracteres.", groups=SenhaSizeMinGroup.class)
	@NotBlank(message="A senha não pode conter apenas espaços.", groups=SenhaNotBlankGroup.class)
	@Size(max=255, message="Senha muito grande. Máximo de 255 caracteres.", groups=SenhaSizeMaxGroup.class)
	private String senha;
	
	@Transient
	@NotBlank(message="Por favor, repita a senha.", groups=SenhaRepetidaNotBlankGroup.class)
	private String senhaRepetida;
	
	@Column(nullable = false)
	private Short permissao;
	
	@Column(nullable=false, columnDefinition = "datetime")
	private LocalDateTime registroData;
	
	@Column(length=72, nullable=false)
	private String registroIp;
	
	
	// construtores
	public Usuario() {}
	public Usuario(@NotBlank(message = "Insira um nome.", groups = NomeNotBlankGroup.class) @Pattern(regexp = "^[A-zÀ-ú ]*$", message = "Nome inválido: somente letras e espaços são permitidos.", groups = NomePatternGroup.class) @Size(min = 2, max = 50, message = "Nome inválido: mínimo {min} e máximo de {max} letras.", groups = NomeSizeGroup.class) String nome,
			@NotBlank(message = "Insira um sobrenome.", groups = SobrenomeNotBlankGroup.class) @Pattern(regexp = "^[A-zÀ-ú ]*$", message = "Sobrenome inválido: somente letras e espaços são permitidos.", groups = SobrenomePatternGroup.class) @Size(min = 2, max = 50, message = "Sobrenome inválido: mínimo {min} e máximo de {max} letras.", groups = SobrenomeSizeGroup.class) String sobrenome,
			@NotBlank(message = "Insira um e-mail.", groups = EmailNotBlankGroup.class) @Pattern(regexp = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[A-z]{2,})$", message = "E-mail em formato inválido.", groups = EmailPatternGroup.class) @Size(max = 64, message = "E-mail inválido: máximo de {max} caracteres.", groups = EmailSizeMaxGroup.class) String email,
			Boolean contaAtivada,
			@Size(min = 6, message = "Senha muito curta. Mínimo de 6 caracteres.", groups = SenhaSizeMinGroup.class) @NotBlank(message = "A senha não pode conter apenas espaços.", groups = SenhaNotBlankGroup.class) @Size(max = 255, message = "Senha muito grande. Máximo de 255 caracteres.", groups = SenhaSizeMaxGroup.class) String senha,
			@NotBlank(message = "Por favor, repita a senha.", groups = SenhaRepetidaNotBlankGroup.class) String senhaRepetida,
			Short permissao, LocalDateTime registroData, String registroIp) {
		this.nome = nome;
		this.sobrenome = sobrenome;
		this.email = email;
		this.contaAtivada = contaAtivada;
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
	public Boolean isContaAtivada() {
		return contaAtivada;
	}
	public void setContaAtivada(Boolean ativado) {
		this.contaAtivada = ativado;
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
				+ ", contaAtivada=" + contaAtivada + ", senha=" + senha + ", senhaRepetida=" + senhaRepetida
				+ ", permissao=" + permissao + ", registroData=" + registroData + ", registroIp=" + registroIp + "]";
	}
	
}
