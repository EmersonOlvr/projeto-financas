package com.jolteam.financas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.dao.CodigoDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.enums.Provedor;
import com.jolteam.financas.enums.TipoCodigo;
import com.jolteam.financas.enums.TipoEmail;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.exceptions.UsuarioDesativadoException;
import com.jolteam.financas.exceptions.UsuarioInexistenteException;
import com.jolteam.financas.exceptions.UsuarioInvalidoException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Codigo;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.util.Util;

@Service
public class UsuarioService {

	@Autowired private EmailService emailService;

	@Autowired private UsuarioDAO usuarios;
	@Autowired private CategoriaDAO categorias;
	@Autowired private CodigoDAO codigos;

	// vai de 4 à 31 (o padrão do gensalt() é 10)
	private static final int complexidadeSenha = 10;
	
	@Transient
	public Usuario save(Usuario usuario) {
		return this.usuarios.save(usuario);
	}
	
	public void adicionarCategorias(Usuario usuario) {
		List<String> nomesCategoriasReceitas = Arrays.asList("Bolsa Família", "Décimo Terceiro", "Empréstimo", "FGTS", "Herança", 
																"Mesada", "PASEP", "PIS", "Salário", "Vendas");
		List<String> nomesCategoriasDespesas = Arrays.asList("Bebidas", "Diversão", "Educação", "Gasolina", "Hotel", 
																"Mascotes", "Restaurante", "Alimentação", "Saúde", "Transporte");
		
		List<Categoria> categorias = new ArrayList<>();
		for (String nomeCategoriaReceita : nomesCategoriasReceitas) {
			Categoria categoriaAtual = new Categoria();
			categoriaAtual.setNome(nomeCategoriaReceita);
			categoriaAtual.setUsuario(usuario);
			categoriaAtual.setTipoTransacao(TipoTransacao.RECEITA);
			categoriaAtual.setDataCriacao(LocalDateTime.now());
			
			categorias.add(categoriaAtual);
		}
		for (String nomeCategoriaDespesa : nomesCategoriasDespesas) {
			Categoria categoriaAtual = new Categoria();
			categoriaAtual.setNome(nomeCategoriaDespesa);
			categoriaAtual.setUsuario(usuario);
			categoriaAtual.setTipoTransacao(TipoTransacao.DESPESA);
			categoriaAtual.setDataCriacao(LocalDateTime.now());
			
			categorias.add(categoriaAtual);
		}
		
		// salva as categorias do usuário
		this.categorias.saveAll(categorias);
	}

	public Usuario getOne(Integer id) {
		return this.usuarios.getOne(id);
	}

	public List<Usuario> findAll() {
		return usuarios.findAll();
	}

	public List<Usuario> findAllByEmail(String email) {
		return this.usuarios.findAllByEmail(email);
	}

	public Optional<Usuario> findById(Integer id) {
		return this.usuarios.findById(id);
	}

	public Optional<Usuario> findByEmail(String email) {
		return this.usuarios.findByEmail(email);
	}

	public boolean existsByEmail(String email) {
		return usuarios.existsByEmail(email);
	}

	public String criptografarSenha(String senha) {
		return BCrypt.hashpw(senha, BCrypt.gensalt(complexidadeSenha));
	}
	// retorna true se a senha for compatível com o hash informado
	public boolean checarSenha(String senha, String hash) {
		return BCrypt.checkpw(senha, hash);
	}

	// ==== Validação do Nome ==== //
	private void validarNome(String nome) throws UsuarioInvalidoException {
		if (Strings.isBlank(nome)) {
			throw new UsuarioInvalidoException("Insira um nome.");
		}
		if (!nome.matches("^[a-zA-ZÀ-ú ]*$")) {
			throw new UsuarioInvalidoException("Nome inválido: somente letras e espaços são permitidos.");
		}
		if (nome.length() < 2 || nome.length() > 50) {
			throw new UsuarioInvalidoException("Nome inválido: mínimo 2 e máximo de 50 letras.");
		}
	}

	// ==== Validação do Sobrenome ==== //
	private void validarSobrenome(String sobrenome) throws UsuarioInvalidoException {
		if (Strings.isBlank(sobrenome)) {
			throw new UsuarioInvalidoException("Insira um sobrenome.");
		}
		if (!sobrenome.matches("^[a-zA-ZÀ-ú ]*$")) {
			throw new UsuarioInvalidoException("Sobrenome inválido: somente letras e espaços são permitidos.");
		}
		if (sobrenome.length() < 2 || sobrenome.length() > 50) {
			throw new UsuarioInvalidoException("Sobrenome inválido: mínimo 2 e máximo de 50 letras.");
		}
	}

	// ==== Validação do E-mail ==== //
	private void validarEmail(String email) throws UsuarioInvalidoException {
		if (Strings.isBlank(email)) {
			throw new UsuarioInvalidoException("Insira um e-mail.");
		}
		if (!Util.isEmailValido(email)) {
			throw new UsuarioInvalidoException("E-mail em formato inválido.");
		}
		if (email.length() > 50) {
			throw new UsuarioInvalidoException("E-mail inválido: máximo de 50 caracteres.");
		}
		if (this.usuarios.findByEmail(email).isPresent()) {
			throw new UsuarioInvalidoException("O e-mail inserido já está em uso.");
		}
	}

	private void validarSenhas(String senha, String senhaRepetida) throws UsuarioInvalidoException {
		// ==== Validação da Senha ==== //
		if (Strings.isEmpty(senha)) {
			throw new UsuarioInvalidoException("Insira uma senha.");
		}
		if (senha.length() < 6) {
			throw new UsuarioInvalidoException("Senha muito curta. Mínimo de 6 caracteres.");
		}
		if (senha.length() > 255) {
			throw new UsuarioInvalidoException("Senha muito grande. Máximo de 255 caracteres.");
		}

		// ==== Validação da Senha Repetida ==== //
		if (Strings.isBlank(senhaRepetida)) {
			throw new UsuarioInvalidoException("Por favor, repita a senha.");
		}
		if (!senha.equals(senhaRepetida)) {
			throw new UsuarioInvalidoException("As senhas não conferem.");
		}
	}

	public void validar(Usuario usuario) throws UsuarioInvalidoException {
		this.validarNome(usuario.getNome());
		this.validarSobrenome(usuario.getSobrenome());
		this.validarEmail(usuario.getEmail());
		this.validarSenhas(usuario.getSenha(), usuario.getSenhaRepetida());

		// ==== Tratamento dos Dados ==== //
		// remove os espaços do começo e do final do nome e do sobrenome
		usuario.setNome(usuario.getNome().trim());
		usuario.setSobrenome(usuario.getSobrenome().trim());

		// remove os espaços duplicados do nome e do sobrenome
		// \s é uma expressão regular (regex) que corresponde a espaços, tabs e quebras
		// de linhas
		// o + corresponde a 1 ou mais caracteres da expressão precedente
		usuario.setNome(usuario.getNome().replaceAll("\\s+", " "));
		usuario.setSobrenome(usuario.getSobrenome().replaceAll("\\s+", " "));

		// coloca o email em letras minúsculas
		usuario.setEmail(usuario.getEmail().toLowerCase());

		// criptografa a senha do usuário
		usuario.setSenha(this.criptografarSenha(usuario.getSenha()));

		// atributos que vieram do formulário nulos mas que não podem ser nulos no banco
		usuario.setAtivado(false);
		usuario.setPermissao((short) 1);
		usuario.setProvedor(Provedor.LOCAL);
	}
	
	public void atualizarUsuario(Usuario usuarioAntigo, Usuario usuarioNovo) throws UsuarioInvalidoException {
		// verifica se o usuário alterou o nome, se sim, valida o novo nome
		if (usuarioNovo.getNome() != null && !usuarioNovo.getNome().equals(usuarioAntigo.getNome())) {
			this.validarNome(usuarioNovo.getNome());
			// remove os espaços do começo e do final do nome
			usuarioAntigo.setNome(usuarioNovo.getNome().trim());
			// remove os espaços duplicados do nome
			// \s é uma expressão regular (regex) que corresponde a espaços, tabs e quebras
			// de linhas
			// o + corresponde a 1 ou mais caracteres da expressão precedente
			usuarioAntigo.setNome(usuarioNovo.getNome().replaceAll("\\s+", " "));
		}
		
		// verifica se o usuário alterou o sobrenome, se sim, valida o novo sobrenome
		if (usuarioNovo.getSobrenome() != null && !usuarioNovo.getSobrenome().equals(usuarioAntigo.getSobrenome())) {
			this.validarSobrenome(usuarioNovo.getSobrenome());
			// remove os espaços do começo e do final do sobrenome
			usuarioAntigo.setSobrenome(usuarioNovo.getSobrenome().trim());
			// remove os espaços duplicados sobrenome
			// \s é uma expressão regular (regex) que corresponde a espaços, tabs e quebras
			// de linhas
			// o + corresponde a 1 ou mais caracteres da expressão precedente
			usuarioAntigo.setSobrenome(usuarioNovo.getSobrenome().replaceAll("\\s+", " "));
		}
		
		// verifica se o usuário alterou o e-mail, se sim, valida o novo e-mail
		if (usuarioNovo.getEmail() != null && !usuarioNovo.getEmail().equals(usuarioAntigo.getEmail())) {
			this.validarEmail(usuarioNovo.getEmail());
			// coloca o email em letras minúsculas
			usuarioAntigo.setEmail(usuarioNovo.getEmail().toLowerCase());
		}
		
		// verifica se o usuário alterou a senha, se sim, valida a nova senha
		if (usuarioNovo.getSenha() != null || usuarioNovo.getSenhaRepetida() != null) {
			this.validarSenhas(usuarioNovo.getSenha(), usuarioNovo.getSenhaRepetida());
			// criptografa a senha do usuário
			usuarioAntigo.setSenha(this.criptografarSenha(usuarioNovo.getSenha()));
		}
	}

	public Usuario entrar(String email, String senha)
			throws UsuarioInvalidoException, UsuarioDesativadoException, UsuarioInexistenteException {
		if (Strings.isBlank(email)) {
			throw new UsuarioInvalidoException("Insira o e-mail.");
		}
		if (Strings.isBlank(senha)) {
			throw new UsuarioInvalidoException("Insira a senha.");
		}

		// busca no banco o usuário com o e-mail fornecido
		Optional<Usuario> usuario = this.usuarios.findByEmail(email);
		
		if (usuario.isPresent()) {
			if (usuario.get().getProvedor().equals(Provedor.LOCAL)) {
				// se a senha estiver correta...
				if (BCrypt.checkpw(senha, usuario.get().getSenha())) {
					if (usuario.get().isAtivado()) {
						return usuario.get();
					} else {
						throw new UsuarioDesativadoException("A conta não está ativa.");
					}
				} else {
					throw new UsuarioInexistenteException("E-mail e/ou senha inválidos.");
				}
			} else {
				throw new UsuarioInvalidoException("Este e-mail foi cadastrado com o "
						+ usuario.get().getProvedor().getValor() + ".");
			}
		} else {
			throw new UsuarioInexistenteException("E-mail e/ou senha inválidos.");
		}
	}

//	private int gerarCodigoConfirmacao(int min, int max) {
//		Random r = new Random();
//		return r.nextInt((max - min) + 1) + min;
//	}

	private String getServerUrl(HttpServletRequest request) {
		String host = request.getServerName();
		String port = Integer.toString(request.getServerPort());
		String url = !port.equals("80") ? "http://" + host + ":" + port : "http://" + host;
		return url;
	}

	public void enviarCodigoAtivacao(Usuario usuario, HttpServletRequest request) {
		String url = this.getServerUrl(request);

		// obtém o código de ativação do banco, se não existir cria um novo
		Optional<Codigo> cod = this.codigos.findByUsuarioAndTipo(usuario, TipoCodigo.ATIVACAO_CONTA);
		Codigo codigo = cod.isPresent() ? cod.get()
				: this.codigos.save(new Codigo(usuario, TipoCodigo.ATIVACAO_CONTA));

		String codigoAtivacao = codigo.getCodigo().replaceAll("[\\-]+", "");

		String assunto = "Ativação da Conta";
		String destinatario = usuario.getEmail();
		String botaoCorpo = "<a href='" + url + "/ativarConta?id=" + usuario.getId() + "&codigo=" + codigoAtivacao
				+ "' target='_blank'><button>Ativar Conta</button></a>";
		String corpo = "<div style=\"color: black\">Olá " + usuario.getNome()
				+ ", bem-vindo(a) ao Projeto Finanças, é um prazer ter você conosco!</div>" + "<br>"
				+ "<div style=\"color: black\">Clique no botão a seguir para ativar sua conta: " + botaoCorpo
				+ "</div>";

		// envia o código para o e-mail do usuário
		this.emailService.enviar(destinatario, assunto, corpo, TipoEmail.HTML);
	}

	public void enviarLinkRedefinicaoSenha(Usuario usuario, HttpServletRequest request) {
		String url = this.getServerUrl(request);

		// obtém o código de ativação do banco, se não existir cria um novo
		Optional<Codigo> cod = this.codigos.findByUsuarioAndTipo(usuario, TipoCodigo.REDEFINICAO_SENHA);
		Codigo codigo = cod.isPresent() ? cod.get()
				: this.codigos.save(new Codigo(usuario, TipoCodigo.REDEFINICAO_SENHA));

		String codigoRedefinicao = codigo.getCodigo().replaceAll("[\\-]+", "");

		String assunto = "Redefinição de Senha";
		String destinatario = usuario.getEmail();
		String botaoCorpo = "<a href='" + url + "/redefinirSenha?id=" + usuario.getId() + "&codigo=" + codigoRedefinicao
				+ "' target='_blank'><button>Redefinir Senha</button></a>";
		String corpo = "<div style=\"color: black\">Olá " + usuario.getNome()
				+ ", foi socilitada a redefinição de senha da sua conta.</div>" + "<br>"
				+ "<div style=\"color: black\">Clique no botão a seguir para redefinir sua senha: " + botaoCorpo
				+ "</div>";

		// envia o link para o e-mail do usuário
		this.emailService.enviar(destinatario, assunto, corpo, TipoEmail.HTML);
	}

}
