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

import com.jolteam.financas.enums.BigDecimalInvalidoException;
import com.jolteam.financas.enums.TiposLogs;
import com.jolteam.financas.exceptions.CofreException;
import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CofreService;
import com.jolteam.financas.service.LogService;
import com.jolteam.financas.util.Util;

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
	public ModelAndView cadastrarCofre(@ModelAttribute Cofre cofre, 
			@RequestParam String str_valorInicial, @RequestParam String str_totalDesejado, 
			HttpServletRequest request, HttpSession session) 
	{
		BigDecimal valorInicial;
		BigDecimal totalDesejado;
		try {
			valorInicial = Util.getBigDecimalOf(str_valorInicial);
		} catch (BigDecimalInvalidoException e) {
			return this.viewCofres(session).addObject("msgErroAdd", "O valor inicial informado é inválido.");
		}
		try {
			totalDesejado = Util.getBigDecimalOf(str_totalDesejado);
		} catch (BigDecimalInvalidoException e) {
			return this.viewCofres(session).addObject("msgErroAdd", "O valor total desejado informado é inválido.");
		}
		
		cofre.setTotalDesejado(totalDesejado);
		cofre.setUsuario((Usuario)session.getAttribute("usuarioLogado"));
		
		try {
			cofre = this.cofreService.salvar(cofre);
			
			// salva um log de sucesso no banco
			this.logService.save(new Log(cofre.getUsuario(),TiposLogs.CADASTRO_COFRE,LocalDateTime.now(), request.getRemoteAddr()));
			
			int result = valorInicial.compareTo(new BigDecimal("0"));
			if (result > 0) {
				this.cofreService.adicionarTransacao(cofre, valorInicial);
			}
		}catch(CofreException ce) {
			
			this.logService.save(new Log(cofre.getUsuario(),TiposLogs.ERRO_CADASTRO_COFRE,LocalDateTime.now(), request.getRemoteAddr()));
			
			return this.viewCofres(session).addObject("msgSucessoAdd", "Cofre cadastrado com sucesso.");
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
	public String editarCofre(@ModelAttribute Cofre cofre, 
			@RequestParam("valor") String str_valorIncrDecr, @RequestParam String str_totalDesejado, 
			RedirectAttributes ra) {
		try {
			Cofre cofreExistente = this.cofreService.findById(cofre.getId()).orElseThrow(() -> new Exception("Cofre inexistente."));
			
			BigDecimal valorIncrDecr;
			BigDecimal totalDesejado;
			try {
				valorIncrDecr = Util.getBigDecimalOf(str_valorIncrDecr);
			} catch (BigDecimalInvalidoException e) {
				ra.addFlashAttribute("msgErroEditar", "O valor para incrementar/decrementar informado é inválido.");
				return "redirect:/cofres/editar?id="+cofre.getId();
			}
			try {
				totalDesejado = Util.getBigDecimalOf(str_totalDesejado);
			} catch (BigDecimalInvalidoException e) {
				ra.addFlashAttribute("msgErroEditar", "O valor total desejado informado é inválido.");
				return "redirect:/cofres/editar?id="+cofre.getId();
			}
			
			
			cofre.setUsuario(cofreExistente.getUsuario());
			cofre.setTotalDesejado(totalDesejado);
			cofre.setDataCriacao(cofreExistente.getDataCriacao());
			
			if (!cofre.getFinalidade().equals(cofreExistente.getFinalidade()) 
					|| cofre.getTotalDesejado().compareTo(cofreExistente.getTotalDesejado()) != 0) 
			{
				cofre = this.cofreService.salvar(cofre);
				ra.addFlashAttribute("msgSucessoEditar", "Cofre atualizado com sucesso!");
			}
			
			int result = valorIncrDecr.compareTo(new BigDecimal("0"));
			if (result > 0) {
				this.cofreService.adicionarTransacao(cofre, valorIncrDecr);
				ra.addFlashAttribute("msgSucessoValor", "R$ "+Util.getStringOf(valorIncrDecr)+" adicionado(s) ao cofre.");
			} else if (result < 0) {
				this.cofreService.adicionarTransacao(cofre, valorIncrDecr);
				ra.addFlashAttribute("msgSucessoValor", "R$ "+Util.getStringOf(valorIncrDecr.negate())+" retirado(s) do cofre.");
			}
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
	
	@GetMapping("/testee")
	public String teste() {
		String str = "1.000.000,75";
		str = str.replace(".", "").replace(",", ".");
		System.out.println("  a  b  c".replace("  ", ""));
		System.out.println(str);
		return "deslogado/index";
	}
	
}
