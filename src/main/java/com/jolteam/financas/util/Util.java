package com.jolteam.financas.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.thymeleaf.expression.Numbers;

import com.jolteam.financas.exceptions.BigDecimalInvalidoException;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;

public abstract class Util {

	// método para obter o IP do usuário
	public static String getUserIp(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
	
	// método para transformar uma String em um BigDecimal
	public static BigDecimal getBigDecimalOf(String str_valor) throws BigDecimalInvalidoException {
		str_valor = !Strings.isBlank(str_valor) ? str_valor.replace("R$ ", "").replace(".", "").replace(",", ".") : "0";
		try {
			return new BigDecimal(str_valor);
		} catch (Exception e) {
			throw new BigDecimalInvalidoException("Valor inválido: "+str_valor);
		}
	}
	
	// método para transformar um BigDecimal numa String formatada
	public static String getStringOf(BigDecimal valor) {
		String str_valor = new Numbers(Locale.ITALY).formatDecimal(valor, 1, "POINT", 2, "COMMA");
		return str_valor;
	}
	
	public static String format(BigDecimal valor) {
		DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("pt", "BR"));
		df.applyPattern("R$ #,##0.00;-R$ #,##0.00");
		return df.format(valor);
	}
	
	public static String formatDate(LocalDate data) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		return data.format(formatter);
	}
	public static String formatDateTime(LocalDateTime data) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		
		return data.format(formatter);
	}
	
	public static String obterMesDe(int mes) {
		List<String> meses = Arrays.asList(
				"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", 
				"Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
			);
		
		if (mes >= 0 && mes <= 12) {
			return meses.get(mes - 1);
		}
		
		return null;
	}
	public static int obterValorMesAtual() {
		return LocalDate.now().getMonthValue();
	}
	public static int obterAnoAtual() {
		return LocalDate.now().getYear();
	}
	
	/**
	 * @param usuario Usuário desejado para obter os anos de cadastro
	 * @return Lista de inteiros contendo anos, incluindo o ano de cadastro do usuário até o ano atual
	 */
	public static List<Integer> obterAnosDeCadastradoDoUsuario(Usuario usuario) {
		int anoCadastro = usuario.getRegistroData().getYear();
		int anoAtual = LocalDate.now().getYear();
		
		List<Integer> anos = new ArrayList<>();
		anos.add(anoCadastro);
		
		int ano = anoCadastro;
		while (ano < anoAtual) {
			ano++;
			anos.add(ano);
		}
		
		return anos;
	}
	
	public static BigDecimal somarTransacoes(List<Transacao> transacoes) {
		BigDecimal total = new BigDecimal("0");
		
		for (Transacao tr : transacoes) {
			total = total.add(tr.getValor());
		}
		
		return total;
	}
	
	public static List<String> obterUltimosSeisMeses() {
		List<String> meses = new ArrayList<>(Arrays.asList(
					"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", 
					"Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
				));
		
		int mesAtual = LocalDate.now().getMonthValue();
		int primeiroMes = mesAtual - 6;
		
		return meses.subList(primeiroMes, mesAtual);
	}
	
	public static boolean isEmailValido(String email) {
		return email.matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[A-z]{2,})$");
	}
	
}
