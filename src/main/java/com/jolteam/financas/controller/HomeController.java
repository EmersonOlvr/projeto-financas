package com.jolteam.financas.controller;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.enums.Provedor;
import com.jolteam.financas.exceptions.FotoInvalidaException;
import com.jolteam.financas.exceptions.UsuarioInvalidoException;
import com.jolteam.financas.model.Foto;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.FotoStorageService;
import com.jolteam.financas.service.MovimentosService;
import com.jolteam.financas.service.UsuarioService;
import com.jolteam.financas.util.Util;




@Controller
public class HomeController {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired MovimentosService movimentosService;
	@Autowired FotoStorageService fotoService;
	
	@GetMapping("/home")
	public ModelAndView viewHome(HttpSession session) {
		ModelAndView mv=new ModelAndView("home");
		
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		
		int mesAtual = LocalDate.now().getMonthValue();
		int mesAtualMenosSeis = LocalDate.now().getMonthValue() - 5;
		
		List<BigDecimal> receitas = new ArrayList<>();
		List<BigDecimal> despesas = new ArrayList<>();
		
		for (int i = mesAtualMenosSeis; i <= mesAtual; i++) {
			Month mes = Month.of(i);
			
			List<Transacao> receitasPorMes = this.movimentosService.obterReceitasPorMes(mes, usuario);
			List<Transacao> despesasPorMes = this.movimentosService.obterDespesasPorMes(mes, usuario);
			
			BigDecimal totalReceitasPorMes = Util.somarTransacoes(receitasPorMes);
			BigDecimal totalDespesasPorMes = Util.somarTransacoes(despesasPorMes);
			
			receitas.add(totalReceitasPorMes);
			despesas.add(totalDespesasPorMes);
		}
		
		mv.addObject("meses", Util.obterUltimosSeisMeses());
		mv.addObject("receitas", receitas);
		mv.addObject("despesas", despesas);
		
		return mv;
	}

	// ====== Configurações ====== //
	@GetMapping("/configuracoes")
	public ModelAndView viewConfiguracoes(HttpSession session) {
		ModelAndView mv = new ModelAndView("configuracoes");
		Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
		mv.addObject("usuario", usuarioLogado);
		mv.addObject("isProvedorLocal", usuarioLogado.getProvedor().equals(Provedor.LOCAL) ? true : false);
		return mv;
	}

	@PostMapping("/configuracoes/perfil")
	@Transient
	public String atualizarPerfilUsuario(@ModelAttribute Usuario usuario,@RequestParam(name="fotoUser",required = false)MultipartFile file, RedirectAttributes ra, HttpSession session) {
		ModelAndView mv = new ModelAndView("configuracoes");
		mv.addObject("usuario", usuario);
		
		Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
		
		if (!usuarioLogado.getProvedor().equals(Provedor.LOCAL)) {
			return "redirect:/configuracoes";
		}
		
		
		// Validar campos atualizados
		try {
			ArrayList<String> erros = new ArrayList<>();
			if (file != null && !file.isEmpty()) {
				try {
					Foto fotoNova = this.fotoService.validar(file);
					
					if (usuarioLogado.getFoto() != null) {
						Foto fotoExistente = usuarioLogado.getFoto();
						fotoExistente.setConteudo(fotoNova.getConteudo());
					} else {
						usuarioLogado.setFoto(fotoNova);
					}
				} catch (FotoInvalidaException e) {
					System.out.println(e.getMessage());
					erros.add(e.getMessage());
				}
			}
			if (erros.size() > 0) {
				throw new FotoInvalidaException(erros.get(0));
			}
			
			this.usuarioService.atualizarUsuario(usuarioLogado, usuario);

			// salva usuario com valores atualizados
			usuarioLogado = this.usuarioService.save(usuarioLogado);
			
			session.setAttribute("usuarioLogado", usuarioLogado);
			
			ra.addFlashAttribute("msgSucessoConfig", "Perfil atualizado com sucesso!");
		} catch (UsuarioInvalidoException ui) {
			ra.addFlashAttribute("msgErroConfig", ui.getMessage());
		} catch (FotoInvalidaException e) {
			ra.addFlashAttribute("msgErroConfig", e.getMessage());
		}
		
		ra.addFlashAttribute("alvoLista", "perfil");
		return "redirect:/configuracoes";
	}
	
	@PostMapping("/configuracoes/senha")
	public String atualizarSenhaUsuario(@RequestParam String senhaAtual, 
			@RequestParam String novaSenha, @RequestParam String novaSenhaRepetida, 
			RedirectAttributes ra, HttpSession session) 
	{
		Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
		
		if (!usuarioLogado.getProvedor().equals(Provedor.LOCAL)) {
			return "redirect:/configuracoes";
		}
		
		if (Strings.isEmpty(senhaAtual)) {
			ra.addFlashAttribute("msgErroConfig", "Insira a senha atual.");
		} else if (!this.usuarioService.checarSenha(senhaAtual, usuarioLogado.getSenha())) {
			ra.addFlashAttribute("msgErroConfig", "A senha atual informada está incorreta.");
		} else if (Strings.isEmpty(novaSenha)) {
			ra.addFlashAttribute("msgErroConfig", "Insira a nova senha.");
		} else if (novaSenha.length() < 6) {
			ra.addFlashAttribute("msgErroConfig", "Senha muito curta. Mínimo de 6 caracteres.");
		} else if (novaSenha.length() > 255) {
			ra.addFlashAttribute("msgErroConfig", "Senha muito grande. Máximo de 255 caracteres.");
		} else if (Strings.isEmpty(novaSenhaRepetida)) {
			ra.addFlashAttribute("msgErroConfig", "Por favor, repita a nova senha.");
		} else if (!novaSenha.equals(novaSenhaRepetida)) {
			ra.addFlashAttribute("msgErroConfig", "As senhas não conferem.");
		} else {
			usuarioLogado.setSenha(this.usuarioService.criptografarSenha(novaSenha));
			
			// salva usuario com valores atualizados
			this.usuarioService.save(usuarioLogado);
			
			ra.addFlashAttribute("msgSucessoConfig", "Senha atualizada com sucesso!");
		}

		ra.addFlashAttribute("alvoLista", "senha");
		return "redirect:/configuracoes";
	}

	

	@GetMapping("/sair")
	public String sair(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/acesso-negado")
	public String viewAcessoNegado() {
		return "acesso-negado";
	}

}
