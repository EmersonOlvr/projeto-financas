package com.jolteam.financas.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.dao.LogDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.errorgroups.usuario.UsuarioValidationSequence;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.TiposLogs;
import com.jolteam.financas.model.Usuario;

@Controller
public class UsuarioController {

	@Autowired private UsuarioDAO usuarios;
	@Autowired private LogDAO logs;

	@GetMapping("/")
	public String viewIndex() {
		return "/deslogado/index";
	}
	
	@GetMapping("/cadastrar")
	public ModelAndView viewCadastrar(Model model) {
		ModelAndView mv = new ModelAndView("/deslogado/cadastrar");
		
		// se receber (através do método addFlashAttribute do RedirectAttributes) o objeto usuario (com os atributos que foram
		// preenchidos pelo usuário), ele é adicionado na view senão, é adicionado um objeto com atributos vazios. 
		// isso serve pra quando ocorrer erros no cadastro o usuário não ter que digitar todos os dados de novo, 
		// bastando apenas ele corrigir o que está errado e submeter de novo o formulário
		Usuario usuario = model.asMap().get("usuario") != null ? (Usuario) model.asMap().get("usuario") : new Usuario();
		mv.addObject("usuario", usuario);
		
		return mv;
	}
	
	@PostMapping("/cadastrar")
	public ModelAndView cadastrarUsuario(HttpServletRequest request, Model model, 
								   @ModelAttribute @Validated(UsuarioValidationSequence.class) Usuario usuario, 
								   BindingResult result) 
	{
		
		// checa se houve algum erro na validação dos campos do usuário
		if (!result.hasFieldErrors()) {
			if (!usuario.getSenha().equals(usuario.getSenhaRepetida())) {
				result.addError(new ObjectError("senhasNaoConferem", "As senhas não conferem."));
			} else if (usuarios.findByEmail(usuario.getEmail()).isPresent()) {
				result.addError(new ObjectError("emailEmUso", "O e-mail inserido já está em uso."));
			} else {
				// remove os espaços do começo e do final do nome e do sobrenome
				usuario.setNome(usuario.getNome().trim());
				usuario.setSobrenome(usuario.getSobrenome().trim());
				
				// remove os espaços duplicados do nome e do sobrenome
				// \s é uma expressão regular (regex) que corresponde a espaços, tabs e quebras de linhas
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
				usuario.setContaAtivada(false);
				usuario.setPermissao((short) 1);
				usuario.setRegistroData(LocalDateTime.now());
				usuario.setRegistroIp(request.getRemoteAddr());
				
				// tenta...
				try {
					// ...salvar o usuário no banco de dados
					this.usuarios.save(usuario);
					
					// adiciona o atributo "msgSucesso" na view
					model.addAttribute("msgSucesso", "Registrado com sucesso!");
					
					System.out.println("Novo registro: "+usuario+".");
					
					// adiciona um novo objeto Usuario na variável 'usuario' que será jogada de volta no formulário
					usuario = new Usuario();
				} catch (Exception e) {
					result.addError(new ObjectError("algoDeuErrado", "Desculpe, algo deu errado."));
				}
			}
		}

		return new ModelAndView("/deslogado/cadastrar").addObject("usuario", usuario);
	}
	
	@GetMapping("/entrar")
	public ModelAndView viewEntrar() {
		ModelAndView mv = new ModelAndView("/deslogado/entrar");
		return mv;
	}
	
	@PostMapping("/entrar")
	public String autenticarUsuario(Model model, @RequestParam String email, String senha, HttpServletRequest request) {
		// busca no banco o usuário com o e-mail fornecido
		// se não existir nenhum, retorna null
		Optional<Usuario> usuario = this.usuarios.findByEmail(email);
		
		if (usuario.isPresent() && BCrypt.checkpw(senha, usuario.get().getSenha())) {
			// insere um novo log de login no banco
			String ip = request.getRemoteAddr();
			LocalDateTime dataAtual = LocalDateTime.now();
			System.out.println(dataAtual);
			logs.save(new Log(usuario.get(), TiposLogs.LOGIN, dataAtual, ip));
			
			// atualiza o usuário no banco
			this.usuarios.save(usuario.get());
			
			System.out.println("Logou! "+usuario);
		} else {
			model.addAttribute("msgErro", "E-mail e/ou senha inválidos.");
		}
		
		return "/deslogado/entrar";
	}
	
	@GetMapping("/ativacao-conta")
	public String viewAtivacaoConta() {
		return "/deslogado/ativacao-conta";
	}
	
	@GetMapping("/recuperar-senha")
	public String viewRecuperarSenha() {
		return "/deslogado/recuperar-senha";
	}
	
	@GetMapping("recuperar-senha/2")
	public String viewRecuperarSenhaMsg() {
		return "/deslogado/recuperar-senha-2";
	}
	
	@GetMapping("/redefinir-senha")
	public String viewRedefinirSenha() {
		return "/deslogado/redefinir-senha";
	}
	
	@GetMapping("/configuracoes")
	public String viewConfiguracoes() {
		return "/configuracoes";
	}
	@PostMapping("/configuracoes")
	public String atualizarUsuario(Model model, @ModelAttribute Usuario usuario) {
		this.usuarios.save(usuario);
		model.addAttribute("msgSucesso", "Configurações salvas!");
		return "/configuracoes";
	}
	
	@GetMapping("/home")
	public String viewHome() {
		return "/home";
	}
	
	@GetMapping("/movimentos")
	public String viewMovimentos() {
		return "/movimentos";
	}
	
	@GetMapping("/receitas/adicionar")
	public String viewAdicionarReceitas() {
		return "/receitas-adicionar";
	}
	
	@GetMapping("/receitas/categorias")
	public String viewCategoriasReceita() {
		return "/receitas-categorias";
	}
	
	@GetMapping("/receitas/historico")
	public String viewHistoricoReceitas() {
		return "/receitas-historico";
	}
	
	@GetMapping("/despesas/adicionar")
	public String viewAdicionarDespesas() {
		return "/despesas-adicionar";
	}
	
	@GetMapping("/despesas/categorias")
	public String viewCategoriasDespesa() {
		return "/despesas-categorias";
	}
	
	@GetMapping("/despesas/historico")
	public String viewHistoricoDespesas() {
		return "/despesas-historico";
	}
	
	@GetMapping("/cofres")
	public String viewCofres(Model model) {
		return "/cofres";
	}
	
	@GetMapping("/cofres/editar/{id}")
	public String viewEditarCofre(@PathVariable Integer id) {
		System.out.println("Editando o cofre: "+id+".");
		return "/cofres-editar";
	}
	
	@GetMapping("/cofres/excluir/{id}")
	public String excluirCofre(@PathVariable Integer id, RedirectAttributes ra) {
		System.out.println("Excluindo o cofre: "+id+"...");
		
		ra.addFlashAttribute("msgSucesso", "Cofre excluído!");
		return "redirect:/cofres";
	}
	
}
