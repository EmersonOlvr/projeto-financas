package com.jolteam.financas.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CodigoDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.enums.TiposCodigos;
import com.jolteam.financas.enums.TiposEmails;
import com.jolteam.financas.exceptions.UsuarioDesativadoException;
import com.jolteam.financas.exceptions.UsuarioInexistenteException;
import com.jolteam.financas.exceptions.UsuarioInvalidoException;
import com.jolteam.financas.model.Codigo;
import com.jolteam.financas.model.Usuario;

@Service
public class UsuarioService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UsuarioDAO usuarios;
	@Autowired
	private CodigoDAO codigosConfirmacao;

	public Usuario save(Usuario usuario) {
		return this.usuarios.save(usuario);
	}

	public Usuario getOne(Integer id) {
		return this.usuarios.getOne(id);
	}
	
	public List<Usuario> findAll() {
		return usuarios.findAll();
	}

	public Optional<Usuario> findById(Integer id) {
		return this.usuarios.findById(id);
	}

	public Optional<Usuario> findByEmail(String email) {
		return this.usuarios.findByEmail(email);
	}

	public List<Usuario> findAllByEmail(String email) {
		return this.usuarios.findAllByEmail(email);
	}

	public void validar(Usuario usuario) throws UsuarioInvalidoException {
		// ==== Validação do Nome ==== //
		if (Strings.isBlank(usuario.getNome())) {
			throw new UsuarioInvalidoException("Insira um nome.");
		}
		if (!usuario.getNome().matches("^[A-zÀ-ú ]*$")) {
			throw new UsuarioInvalidoException("Nome inválido: somente letras e espaços são permitidos.");
		}
		if (usuario.getNome().length() < 2 || usuario.getNome().length() > 50) {
			throw new UsuarioInvalidoException("Nome inválido: mínimo 2 e máximo de 50 letras.");
		}

		// ==== Validação do Sobrenome ==== //
		if (Strings.isBlank(usuario.getSobrenome())) {
			throw new UsuarioInvalidoException("Insira um sobrenome.");
		}
		if (!usuario.getSobrenome().matches("^[A-zÀ-ú ]*$")) {
			throw new UsuarioInvalidoException("Sobrenome inválido: somente letras e espaços são permitidos.");
		}
		if (usuario.getSobrenome().length() < 2 || usuario.getSobrenome().length() > 50) {
			throw new UsuarioInvalidoException("Sobrenome inválido: mínimo 2 e máximo de 50 letras.");
		}

		// ==== Validação do E-mail ==== //
		if (Strings.isBlank(usuario.getEmail())) {
			throw new UsuarioInvalidoException("Insira um e-mail.");
		}
		if (!usuario.getEmail().matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[A-z]{2,})$")) {
			throw new UsuarioInvalidoException("E-mail em formato inválido.");
		}
		if (usuario.getEmail().length() > 50) {
			throw new UsuarioInvalidoException("E-mail inválido: máximo de 50 caracteres.");
		}
		if (usuarios.findByEmail(usuario.getEmail()).isPresent()) {
			throw new UsuarioInvalidoException("O e-mail inserido já está em uso.");
		}

		// ==== Validação da Senha ==== //
		if (Strings.isBlank(usuario.getSenha())) {
			throw new UsuarioInvalidoException("Insira uma senha.");
		}
		if (usuario.getSenha().length() < 6) {
			throw new UsuarioInvalidoException("Senha muito curta. Mínimo de 6 caracteres.");
		}
		if (usuario.getSenha().length() > 255) {
			throw new UsuarioInvalidoException("Senha muito grande. Máximo de 255 caracteres.");
		}

		// ==== Validação da Senha Repetida ==== //
		if (Strings.isBlank(usuario.getSenhaRepetida())) {
			throw new UsuarioInvalidoException("Por favor, repita a senha.");
		}
		if (!usuario.getSenha().equals(usuario.getSenhaRepetida())) {
			throw new UsuarioInvalidoException("As senhas não conferem.");
		}

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
		int complexidade = 10; // vai de 4 à 31 (o padrão do gensalt() é 10)
		String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt(complexidade));
		usuario.setSenha(senhaHash);

		// atributos que vieram do formulário nulos mas que não podem ser nulos no banco
		usuario.setAtivado(false);
		usuario.setPermissao((short) 1);
	}

	public Usuario entrar(String email, String senha) 
		throws UsuarioInvalidoException, UsuarioDesativadoException, UsuarioInexistenteException 
	{
		if (Strings.isBlank(email)) {
			throw new UsuarioInvalidoException("Insira o e-mail.");
		}
		if (Strings.isBlank(senha)) {
			throw new UsuarioInvalidoException("Insira a senha.");
		}

		// busca no banco o usuário com o e-mail fornecido
		Optional<Usuario> usuario = this.usuarios.findByEmail(email);

		// se existir e a senha estiver correta...
		if (usuario.isPresent() && BCrypt.checkpw(senha, usuario.get().getSenha())) {
			if (usuario.get().isAtivado()) {
				return usuario.get();
			} else {
				throw new UsuarioDesativadoException("A conta não está ativa.");
			}
		} else {
			throw new UsuarioInexistenteException("E-mail e/ou senha inválidos.");
		}
	}

//	private int gerarCodigoConfirmacao(int min, int max) {
//		Random r = new Random();
//		return r.nextInt((max - min) + 1) + min;
//	}

	public void enviarCodigoAtivacao(Usuario usuario, HttpServletRequest request) {
		String host = request.getServerName();
		String port = Integer.toString(request.getServerPort());
		String url = !port.equals("80") ? "http://"+host+":"+port : "http://"+host;
		
		// obtém o código do banco, se existir
		Optional<Codigo> cod = this.codigosConfirmacao.findByUsuarioAndTipo(usuario, TiposCodigos.ATIVACAO_CONTA);
		Codigo codigo = cod.isPresent() ? cod.get() : null;
		codigo = codigo != null ? codigo : this.codigosConfirmacao.save(new Codigo(usuario, TiposCodigos.ATIVACAO_CONTA));
		
		String codigoConfirmacao = codigo.getCodigo().replaceAll("[\\-]+", "");
		
		String assunto = "Ativação da Conta";
		String destinatario = usuario.getEmail();
		String botaoCorpo = "<a href='"+url+"/ativarConta?id="+usuario.getId()+"&codigo="+codigoConfirmacao+"' target='_blank'><button>Ativar Conta</button></a>";
		String corpo = "<div style=\"color: black\">Olá "+ usuario.getNome()+", bem-vindo(a) ao Projeto Finanças, "
				+ "é um prazer ter você conosco!</div> <br> <div style=\"color: black\">Clique no botão a seguir para ativar sua conta: "
				+ botaoCorpo + "</div>";
		
		// envia o código para o e-mail do usuário
		this.emailService.enviar(destinatario, assunto, corpo, TiposEmails.HTML);
	}

}
