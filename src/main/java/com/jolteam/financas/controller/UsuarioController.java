package com.jolteam.financas.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.errorgroups.usuario.UsuarioValidationSequence;
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
	public ModelAndView registrarUsuario(HttpServletRequest request, Model model, 
								   @ModelAttribute @Validated(UsuarioValidationSequence.class) Usuario usuario, 
								   BindingResult result) 
	{
		ModelAndView mv = new ModelAndView("registrar");
		
		// checa se houve algum erro na validação dos campos
		if (!result.hasFieldErrors()) {
			if (!usuario.getSenha().equals(usuario.getSenhaRepetida())) {
				result.addError(new ObjectError("senhasNaoConferem", "As senhas não conferem."));
			} else if (usuarioRep.findAllByEmail(usuario.getEmail()).size() > 0) {
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
				
				// data e hora atual
				String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				
				// atributos que vieram do formulário nulos mas que não podem ser nulos no banco
				usuario.setEmailAtivado(false);
				usuario.setPermissao(1);
				usuario.setRegistroData(datetime);
				usuario.setRegistroIp(this.getUserIp(request));
				
				// tenta...
				try {
					// ...salvar o usuário no banco de dados
					this.usuarioRep.save(usuario);
					
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

		return new ModelAndView("registrar").addObject("usuario", usuario);
	}
	
	@GetMapping("/entrar")
	public ModelAndView viewEntrar() {
		ModelAndView mv = new ModelAndView("entrar");
		return mv;
	}
	
	@PostMapping("/entrar")
	public String autenticarUsuario(Model model, @RequestParam String email, String senha, HttpServletRequest request) {
		// busca no banco o usuário com o e-mail fornecido
		// se não existir nenhum, retorna null
		Usuario usuario = this.usuarioRep.findByEmail(email);
		
		if (usuario == null || !BCrypt.checkpw(senha, usuario.getSenha())) {
			model.addAttribute("msgErro", "E-mail ou senha inválidos.");
		} else {
			// atualiza o último acesso e o último IP
			usuario.setUltimoAcesso(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			usuario.setUltimoIp(this.getUserIp(request));
			
			// atualiza o usuário no banco
			this.usuarioRep.save(usuario);
			
			System.out.println("Logou! "+usuario);
		}
		
		return "entrar";
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
