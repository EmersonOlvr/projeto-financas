package com.jolteam.financas.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.enums.TipoLog;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.exceptions.BigDecimalInvalidoException;
import com.jolteam.financas.exceptions.CofreException;
import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.CofreTransacao;
import com.jolteam.financas.model.HistoricoCofreTransacao;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.service.CofreService;
import com.jolteam.financas.service.LogService;
import com.jolteam.financas.util.Util;

@Controller
@RequestMapping("/cofres")
public class CofreController {
	
	@Autowired private CofreService cofreService;
	@Autowired private LogService logService;
	
	@GetMapping("/listar")
	public ModelAndView viewCofres(HttpSession session) {
		ModelAndView mv = new ModelAndView("cofres");
		mv.addObject("listaCofres", this.cofreService.listarPorUsuario((Usuario)session.getAttribute("usuarioLogado")));
		return mv;
	}
	
	@GetMapping("/adicionar")
	public ModelAndView viewCadastrarCofre() {
		ModelAndView mv=new ModelAndView("cofres-adicionar");
		mv.addObject("cofre", new Cofre());
		return mv;
	}
	
	@PostMapping("/adicionar")
	public ModelAndView cadastrarCofre(@ModelAttribute Cofre cofre, 
			@RequestParam String str_valorInicial, @RequestParam String str_totalDesejado, 
			HttpServletRequest request, HttpSession session) 
	{
		ModelAndView mv = this.viewCadastrarCofre();
		
		// se não passar nas validações abaixo joga os valores informados de volta aos inputs
		mv.addObject("cofre", cofre);
		mv.addObject("str_valorInicial", str_valorInicial);
		mv.addObject("str_totalDesejado", str_totalDesejado);
		
		// converte os valores informados (que estão como String) para BigDecimal
		BigDecimal valorInicial;
		BigDecimal totalDesejado;
		try {
			valorInicial = Util.getBigDecimalOf(str_valorInicial);
		} catch (BigDecimalInvalidoException e) {
			return mv.addObject("msgErro", "O valor inicial informado é inválido.");
		}
		try {
			totalDesejado = Util.getBigDecimalOf(str_totalDesejado);
		} catch (BigDecimalInvalidoException e) {
			return mv.addObject("msgErro", "O valor total desejado informado é inválido.");
		}
		
		// define os atributos obrigatórios do cofre que vieram nulos do formulário
		cofre.setUsuario((Usuario) session.getAttribute("usuarioLogado"));
		cofre.setTotalDesejado(totalDesejado);
		cofre.setDataCriacao(LocalDateTime.now());
		
		try {
			// salva o cofre no banco (ou não, pode lançar uma excessão)
			cofre = this.cofreService.salvar(cofre);
			
			// salva um log de sucesso no banco
			this.logService.save(new Log(cofre.getUsuario(), TipoLog.CADASTRO_COFRE,LocalDateTime.now(), request.getRemoteAddr()));
			
			// verifica se o usuário deseja inserir um valor inicial no cofre.
			// esse valor vai para as transações do cofre e não para o cofre em si
			int result = valorInicial.compareTo(new BigDecimal("0"));
			// se o valor inicial informado for maior que R$ 0,00
			if (result > 0) {
				// adiciona um valor inicial ao cofre (isto é, nas transações do cofre)
				this.cofreService.adicionarTransacao(cofre, valorInicial, TipoTransacao.RECEITA);
			// se o valor inicial informado for menor que R$ 0,00
			} else if (result < 0) {
				return mv.addObject("msgErro", "O Valor inicial deve ser maior que R$ 0,00");
			}
			
			return this.viewCadastrarCofre().addObject("msgSucesso", "Cofre cadastrado com sucesso.");
		} catch (CofreException ce) {
			return mv.addObject("msgErro", ce.getMessage());
		}
	}
	
	@GetMapping("/editar")
	public ModelAndView viewEditarCofre(@RequestParam Integer id, HttpSession session) {
		ModelAndView mv = new ModelAndView("cofres-editar");
		
		try {
			Cofre cofre = this.cofreService.obterPorIdEUsuario(id, (Usuario) session.getAttribute("usuarioLogado"))
					.orElseThrow(() -> new CofreException("Cofre inexistente."));
			mv.addObject("cofre", cofre);
		} catch (CofreException ce) {
			mv.setViewName("redirect:/cofres/listar");
		}
		
		return mv;
	}
	
	@PostMapping("/editar")
	public String editarCofre(@ModelAttribute Cofre cofre, @RequestParam String str_totalDesejado, 
			RedirectAttributes ra, HttpSession session) {
		try {
			// obtém o cofre do banco que contém o ID fornecido na URL
			Cofre cofreExistente = this.cofreService.obterPorIdEUsuario(
					cofre.getId(), (Usuario) session.getAttribute("usuarioLogado"))
					.orElseThrow(() -> new Exception("Cofre inexistente."));
			
			// converte o valor do Total Desejado (que está como String) para BigDecimal
			BigDecimal totalDesejado;
			try {
				totalDesejado = Util.getBigDecimalOf(str_totalDesejado);
			} catch (BigDecimalInvalidoException e) {
				ra.addFlashAttribute("msgErro", "O valor total desejado informado é inválido.");
				return "redirect:/cofres/editar?id="+cofre.getId();
			}

			// define os atributos obrigatórios do cofre que vieram nulos do formulário
			cofre.setUsuario(cofreExistente.getUsuario());
			cofre.setTotalDesejado(totalDesejado);
			cofre.setDataCriacao(cofreExistente.getDataCriacao());
			
			// só salva se os dados informados forem diferentes dos dados do cofre que está no banco
			if (!cofre.getFinalidade().equals(cofreExistente.getFinalidade()) 
					|| cofre.getTotalDesejado().compareTo(cofreExistente.getTotalDesejado()) != 0) 
			{
				cofre = this.cofreService.salvar(cofre);
				ra.addFlashAttribute("msgSucesso", "Cofre atualizado com sucesso!");
			}
		}  catch (CofreException ce) {
			ra.addFlashAttribute("msgErro", ce.getMessage());
		} catch (Exception e) {
			return "redirect:/cofres/listar";
		}
		
		return "redirect:/cofres/editar?id="+cofre.getId();
	}
	
	@GetMapping("/movimentar")
	public ModelAndView viewMovimentos(HttpSession session) {
		ModelAndView mv=new ModelAndView("cofres-movimentar");
		mv.addObject("listaCofres", this.cofreService.listarPorUsuario((Usuario)session.getAttribute("usuarioLogado")));
		return mv;
	}
	
	@PostMapping("/movimentar")
	public ModelAndView cofreMovimentos(@RequestParam String str_valor, @RequestParam Integer cofreId,
			HttpSession session) 
	{
		ModelAndView mv = this.viewMovimentos(session);
		
		Cofre cofre = null;
		try {
			if (cofreId == null || cofreId == 0) {
				throw new Exception("Selecione um cofre.");
			}
			cofre = this.cofreService.obterPorIdEUsuario(cofreId, (Usuario) session.getAttribute("usuarioLogado"))
					.orElseThrow(() -> new CofreException("O Cofre selecionado não existe."));
		} catch (Exception e) {
			mv.addObject("msgErro", e.getMessage());
			return mv;
		}
		
		BigDecimal valor;
		try {
			valor = Util.getBigDecimalOf(str_valor);
		} catch (BigDecimalInvalidoException e) {
			mv.addObject("msgErro", "O valor informado é inválido.");
			return mv;
		}
		
		int result = valor.compareTo(new BigDecimal("0"));
		if (result > 0) {
			this.cofreService.adicionarTransacao(cofre, valor, TipoTransacao.RECEITA);
			return mv.addObject("msgSucesso", "R$ "+Util.getStringOf(valor)+" adicionados ao cofre.");
		} else if (result < 0) {
			this.cofreService.adicionarTransacao(cofre, valor, TipoTransacao.DESPESA);
			return mv.addObject("msgSucesso", "R$ "+Util.getStringOf(valor.negate())+" retirados do cofre.");
		} else {
			return mv.addObject("msgErro", "Informe um valor diferente de R$ 0,00.");
		}
		
	}
	
	@GetMapping("/historico")
	public ModelAndView viewHistorico(HttpSession session) {
		ModelAndView mv = new ModelAndView("cofres-historico");
		
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		
		List<HistoricoCofreTransacao> historico = new ArrayList<>();
		
		List<Cofre> cofresUsuario = this.cofreService.listarPorUsuario(usuario);
		for (Cofre cofre : cofresUsuario) {
			List<CofreTransacao> transacoesPorCofre = this.cofreService.listarTransacoesPorCofre(cofre);
			historico.add(new HistoricoCofreTransacao(cofre, transacoesPorCofre));
		}
		
		mv.addObject("historico", historico);
		
		return mv;
	}
	
	@GetMapping("/historico/excluir")
	public String excluirTransacao(HttpSession session, 
			@RequestParam(value = "id", required = false) Integer id, 
			RedirectAttributes ra) 
	{
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		
		if (id != null) {
			// deletarTransacaoPorId() retorna true somente se realmente excluir a transação
			if (this.cofreService.deletarTransacaoPorId(id, usuario)) {
				ra.addFlashAttribute("msgSucessoExcluir", "Movimentação excluída com sucesso!");
			}
		}
		
		return "redirect:/cofres/historico";
	}
	
	@GetMapping("/excluir")
	public String excluirCofre(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
		try {
			Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
			Cofre cofre = this.cofreService.obterPorIdEUsuario(id, usuario).orElseThrow(() -> new Exception("Cofre inexistente."));
			
			this.cofreService.delete(cofre);
		} catch (Exception e) {
			return "redirect:/cofres/listar";
		}
		
		ra.addFlashAttribute("msgSucessoExcluir", "Cofre excluído com sucesso!");
		return "redirect:/cofres/listar";
	}
	
}