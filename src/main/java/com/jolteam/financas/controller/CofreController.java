package com.jolteam.financas.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.exceptions.CofreException;
import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CofreService;
import com.jolteam.financas.service.LogService;

@Controller
public class CofreController {
	
	@Autowired private CofreService cofreService;
	@Autowired private LogService logService;
	
	@GetMapping("/cofres")
	public ModelAndView viewCofres(HttpSession session) {
		ModelAndView mv=new ModelAndView("cofres");
		mv.addObject("cofre", new Cofre());
		mv.addObject("listaCofres", this.cofreService.findAllByUsuarioOrderByDataCriacaoDesc((Usuario)session.getAttribute("usuarioLogado")));
		return mv;
	}
	
	@PostMapping("/cofres")
	public ModelAndView cadastrarCofre(@ModelAttribute Cofre cofre, @RequestParam BigDecimal valorInicial, 
			HttpServletRequest request, HttpSession session) 
	{
		cofre.setUsuario((Usuario)session.getAttribute("usuarioLogado"));
		
		try {
			cofre = this.cofreService.salvar(cofre);
			
			// salva um log de sucesso no banco
			this.logService.save(new Log(cofre.getUsuario(),TiposLogs.CADASTRO_COFRE,LocalDateTime.now(), request.getRemoteAddr()));
			
			if (valorInicial != null) {
				int result = valorInicial.compareTo(new BigDecimal("0"));
				if (result > 0) {
					this.cofreService.adicionarTransacao(cofre, valorInicial);
				}
			}
		}catch(CofreException ce) {
			
			this.logService.save(new Log(cofre.getUsuario(),TiposLogs.ERRO_CADASTRO_COFRE,LocalDateTime.now(), request.getRemoteAddr()));
			
			return this.viewCofres(session).addObject("msgErroAdd", ce.getMessage());
		}
		
		return this.viewCofres(session).addObject("msgSucessoAdd", "Cofre cadastrado com sucesso.");
	}
	
	@GetMapping("/cofres/editar")
	public ModelAndView viewEditarCofre(@RequestParam Integer id) {
		ModelAndView mv = new ModelAndView("cofres-editar");
		
		try {
			Cofre cofre = this.cofreService.findById(id).orElseThrow(() -> new CofreException("Cofre inexistente."));
			
			mv.addObject("cofre", cofre);
		} catch (CofreException ce) {
			mv.setViewName("redirect:/cofres");
		}
		
		return mv;
	}
	
	@PostMapping("/cofres/editar")
	public String editarCofre(@ModelAttribute Cofre cofre, @RequestParam(required = false) BigDecimal valor, 
			RedirectAttributes ra) {
		try {
			Cofre cofreExistente = this.cofreService.findById(cofre.getId()).orElseThrow(() -> new Exception("Cofre inexistente."));
			
			cofre.setUsuario(cofreExistente.getUsuario());
			cofre.setDataCriacao(cofreExistente.getDataCriacao());
			cofre = this.cofreService.salvar(cofre);
			
			if (valor != null) {
				int result = valor.compareTo(new BigDecimal("0"));
				if (result > 0) {
					this.cofreService.adicionarTransacao(cofre, valor);
					ra.addFlashAttribute("msgSucessoValor", "R$ "+valor+" adicionado(s) ao cofre.");
				} else if (result < 0) {
					this.cofreService.adicionarTransacao(cofre, valor);
					ra.addFlashAttribute("msgSucessoValor", "R$ "+valor.negate()+" retirado(s) do cofre.");
				}
			}
			
			ra.addFlashAttribute("msgSucessoEditar", "Cofre atualizado com sucesso!");
		}  catch (CofreException ce) {
			ra.addFlashAttribute("msgErroEditar", ce.getMessage());
		} catch (Exception e) {
			return "redirect:/cofres";
		}
		
		return "redirect:/cofres/editar?id="+cofre.getId();
	}
	
	@GetMapping("/cofres/excluir")
	public String excluirCofre(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
		try {
			Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
			Cofre cofre = this.cofreService.findByIdAndUsuario(id, usuario).orElseThrow(() -> new Exception("Cofre inexistente."));
			
			this.cofreService.delete(cofre);
		} catch (Exception e) {
			return "redirect:/cofres";
		}
		
		ra.addFlashAttribute("msgSucessoExcluir", "Cofre excluído com sucesso!");
		return "redirect:/cofres";
	}
	
}
