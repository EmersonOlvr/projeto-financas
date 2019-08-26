package com.jolteam.financas.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.enums.TipoRelatorio;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;
import com.jolteam.financas.model.dto.RelatorioForm;
import com.jolteam.financas.util.Util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Controller
public class RelatorioController {

	@Autowired private TransacaoDAO transacoes;
	
	@GetMapping("/relatorio")
	public ModelAndView redirecionaParaMovimentos(HttpSession session, 
			@RequestParam(required = false) String erro, 
			@RequestParam(required = false) Integer mesErro, @RequestParam(required = false) Integer anoErro) 
	{
		ModelAndView mv = new ModelAndView("usuario/relatorio");
		
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		
		int mesAtual = Util.obterValorMesAtual();
		int anoAtual = Util.obterAnoAtual();
		
		RelatorioForm relatorioForm = new RelatorioForm(mesAtual, anoAtual);
		
		if (erro != null && erro.equals("sem_dados")) {
			mv.addObject("msgErro", "Nenhuma movimentação encontrada para o mês/ano informados.");
			relatorioForm = new RelatorioForm(mesErro, anoErro);
		}
		
		mv.addObject("relatorioForm", relatorioForm);
		mv.addObject("anos", Util.obterAnosDeCadastradoDoUsuario(usuario));
		
		return mv;
	}
	
	@PostMapping("/relatorio")
	public void testeRelatorio(@ModelAttribute RelatorioForm relatorioForm, @RequestParam String acao, 
			HttpSession session, HttpServletResponse response, Model model, RedirectAttributes ra) throws JRException, IOException 
	{
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		
		// Define os parâmetros
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("nome", usuario.getNome() +" "+ usuario.getSobrenome());
		parametros.put("mes", Util.obterMesDe(relatorioForm.getMes()));
		
		// Consulta no banco dependendo do tipo (RECEITA, DESPESA, AMBOS)
		List<Transacao> transacoes = null;
		String relatorioNome = "extrato";
		if (relatorioForm.getTipoRelatorio().equals(TipoRelatorio.RECEITA)) {
			transacoes = this.transacoes.listarPorDataEUsuario(relatorioForm.getMes(), 
																relatorioForm.getAno(), 
																usuario, 
																TipoTransacao.RECEITA);
			parametros.put("tipoExtrato", "Receitas");
		} else if (relatorioForm.getTipoRelatorio().equals(TipoRelatorio.DESPESA)) {
			transacoes = this.transacoes.listarPorDataEUsuario(relatorioForm.getMes(), 
																relatorioForm.getAno(), 
																usuario, 
																TipoTransacao.DESPESA);
			parametros.put("tipoExtrato", "Despesas");
		} else {
			relatorioNome = "movimentos";
			transacoes = this.transacoes.listarPorDataEUsuario(relatorioForm.getMes(), relatorioForm.getAno(), usuario);
		}
		
		
		// Verifica se existem transações para o mês/ano informados
		if (transacoes.size() > 0) {
			// Define a fonte dos dados
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transacoes, false);
			
			// inline: abre o arquivo no navegador, attachment: baixa o arquivo
			if (!Strings.isBlank(acao)) {
				if (acao.equals("view")) {
					acao = "inline";
				} else if (acao.equals("download")) {
					acao = "attachment";
				} else {
					acao = "inline";
				}
			} else {
				acao = "inline";
			}
			
			
			// ===== Compila o template quando ele ainda não está compilado (ou seja, com extensão .jrxml) ===== //
//			InputStream inputStream = this.getClass().getResourceAsStream("/jasper/movimentos.jrxml");
//			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			
			// ===== Usa o template que ja está compilado (ou seja, com extensão .jasper) ===== //
			InputStream inputStream = this.getClass().getResourceAsStream("/jasper/"+relatorioNome+".jasper");
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
			
			// Passa para o JasperPrint o relatório, os parâmetros e a fonte dos dados, no caso uma lista
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
			
			
			// Configura a resposta para o tipo PDF
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", acao+"; filename=relatorio-"+LocalDate.now().toString()+".pdf");

			// Faz a exportação do relatório para o HttpServletResponse
			final OutputStream outStream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
		} else {
			response.sendRedirect("/relatorio?erro=sem_dados&mesErro="+relatorioForm.getMes()+"&anoErro="+relatorioForm.getAno());
		}
	}
	
	@GetMapping("/relatorio/compilar")
	public String compilarRelatorio() {
		InputStream inputStream = this.getClass().getResourceAsStream("/jasper/extrato.jrxml");
		OutputStream outputStream = null;
		
		try {
			outputStream = new FileOutputStream("src/main/resources/jasper/extrato.jasper");
			JasperCompileManager.compileReportToStream(inputStream, outputStream);
			outputStream.close();
		} catch (Exception e) {
			System.out.println(e.getClass());
			System.out.println(e.getMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					System.out.println("erro ao fechar");
				}
			}
		}
		
		return "index";
	}
	
}
