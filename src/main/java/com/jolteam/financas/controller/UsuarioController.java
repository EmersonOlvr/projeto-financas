package com.jolteam.financas.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.errorgroups.ValidationSequence;
import com.jolteam.financas.model.Usuario;

@Controller
public class UsuarioController {

	@Autowired
	private UsuarioDAO usuarioRep;
	
	// método para obter o IP do usuário
	private String getUserIp(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}

	@GetMapping("/")
	public String viewIndex() {
		return "index";
	}
	
	@GetMapping("/registrar")
	public ModelAndView viewRegistrar(Model model) {
		ModelAndView mv = new ModelAndView("registrar");
		
		// se receber (através do método addFlashAttribute do RedirectAttributes) o objeto usuario (com os atributos que foram
		// preenchidos pelo usuário), ele é adicionado na view senão, é adicionado um objeto com atributos vazios. 
		// isso serve pra quando ocorrer erros no cadastro o usuário não ter que digitar todos os dados de novo, 
		// bastando apenas ele corrigir o que está errado e submeter de novo o formulário
		Usuario usuario = model.asMap().get("usuario") != null ? (Usuario) model.asMap().get("usuario") : new Usuario();
		mv.addObject("usuario", usuario);
		
		return mv;
	}
	
	@PostMapping("/registrar")
	public String registrarUsuario(HttpServletRequest request, Model model, 
								   @ModelAttribute @Validated(ValidationSequence.class) Usuario usuario, 
								   BindingResult result, 
								   RedirectAttributes redirectAttr) 
	{
		String msgErro = null;
		String msgSucesso = "Registrado com sucesso!";
		
		// checa se houve algum erro na validação
		if (result.hasFieldErrors()) {
			// se sim, pega a primeira mensagem de erro
			msgErro = result.getFieldErrors().get(0).getDefaultMessage();
		// checa se a senha e a senha repetida são iguais
		} else if (!usuario.getSenha().equals(usuario.getSenhaRepetida())) {
			msgErro = "As senhas não conferem.";
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
			
			// criptografa a senha do usuário antes de inserir no banco de dados
			int complexidade = 10; // vai de 4 à 31 (o padrão do gensalt() é 10)
			String senhaHash = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt(complexidade));
			usuario.setSenha(senhaHash);
			
			// data e hora atual
			String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			
			// atributos que vieram da view nulos mas que não podem ser nulos no banco
			usuario.setEmailAtivado(false);
			usuario.setPermissao(1);
			usuario.setRegistroData(datetime);
			usuario.setRegistroIp(this.getUserIp(request));
			
			// tenta...
			try {
				// ...salvar o usuário no banco de dados
				this.usuarioRep.save(usuario);
				
				// adiciona o atributo "msgSucesso" na view
				redirectAttr.addFlashAttribute("msgSucesso", msgSucesso);
				
				System.out.println("Novo registro: "+usuario+".");
				
				// adiciona um objeto vazio na variável 'usuario' para ser jogada de volta no formulário
				usuario = new Usuario();
			} catch(DataIntegrityViolationException dive) {
				// se o código de erro for igual à 1062 
				// (1062 é um código de erro exibido quando há os mesmos dados na coluna definida como única)
				if (((JDBCException) dive.getCause()).getSQLException().getErrorCode() == 1062) {
					// já que dos atributos recebidos no registro, o e-mail é o único que corresponde à uma coluna
					// definida como única no BD, então a mensagem já diz o erro ao usuário.
					msgErro = "O e-mail inserido já está em uso.";
					System.out.println("Erro no registro: "+msgErro.replace(".", "")+" ("+usuario.getEmail()+").");
				} else {
					msgErro = "Erro desconhecido.";
					System.out.println("Erro no registro: "+msgErro);
				}
			} catch (Exception e) {
				msgErro = "Desculpe, algo deu errado.";
				System.out.println("Erro no registro: "+msgErro);
			}
		}
		
		// adiciona a mensagem de erro na view
		redirectAttr.addFlashAttribute("msgErro", msgErro);
		// adiciona o objeto Usuario de volta no form
		redirectAttr.addFlashAttribute("usuario", usuario);
		
		return "redirect:/registrar";
	}
	
	@GetMapping("/entrar")
	public ModelAndView viewEntrar() {
		ModelAndView mv = new ModelAndView("entrar");
		return mv;
	}
	
	@PostMapping("/entrar")
	public String autenticarUsuario(@RequestParam String email, String senha, RedirectAttributes redirectAttr) {
		// SE as credenciais forem corretas cria a sessão do usuário...
		return "redirect:/entrar";
	}
	
	@GetMapping("/home")
	public String viewHome() {
		return "home";
	}
	
	@GetMapping("/ajustes")
	public String viewAjustes() {
		return "ajustes";
	}

	@GetMapping("/teste/{num}/{str}")
	public String testePathVariables(Model model, @PathVariable Long num, @PathVariable String str) {
		model.addAttribute("num", num);
		model.addAttribute("str", str);
		return "testes";
	}
	
}
