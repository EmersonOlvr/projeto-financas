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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jolteam.financas.dao.TransacaoDAO;
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
	public String redirecionaParaMovimentos() {
		return "redirect:/movimentos";
	}
	
	@PostMapping("/relatorio")
	public void testeRelatorio(@ModelAttribute RelatorioForm relatorioForm, @RequestParam String acao, 
			HttpSession session, HttpServletResponse response, Model model, RedirectAttributes ra) throws JRException, IOException 
	{
		// Dados necessários para a consulta
		LocalDate dataConsulta = LocalDate.of(relatorioForm.getAno(), relatorioForm.getMes(), 1);
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		
		// Consulta no banco
		List<Transacao> transacoes = this.transacoes.findAllByMesAndAnoAndUsuario(dataConsulta, usuario);
		
		// Verifica se existem transações para o mês/ano informados
		if (transacoes.size() > 0) {
			// Define a fonte dos dados (a Lista acima)
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
			InputStream inputStream = this.getClass().getResourceAsStream("/jasper/movimentos.jasper");
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
			
			
			// Define os parâmetros
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("nome", usuario.getNome() +" "+ usuario.getSobrenome());
			parametros.put("mes", Util.obterMesDe(relatorioForm.getMes()));
			
			// Passa para o JasperPrint o relatório, os parâmetros e a fonte dos dados, no caso uma lista
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
			
			
			// Configura a resposta para o tipo PDF
			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", acao+"; filename=relatorio-"+LocalDate.now().toString()+".pdf");

			// Faz a exportação do relatório para o HttpServletResponse
			final OutputStream outStream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
		} else {
			response.sendRedirect("/movimentos?erro=sem_dados&mesErro="+relatorioForm.getMes()+"&anoErro="+relatorioForm.getAno());
		}
	}
	
//	@GetMapping("/relatorio/compilar")
	public String compilarRelatorio() {
		InputStream inputStream = this.getClass().getResourceAsStream("/jasper/movimentos.jrxml");
		OutputStream outputStream = null;
		
		try {
			outputStream = new FileOutputStream("src/main/resources/jasper/movimentos.jasper");
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
