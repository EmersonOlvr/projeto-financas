package com.jolteam.financas.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
	
	public static List<String> obterUltimosSeisMeses(int mesAtual) throws Exception {
		List<String> meses = new ArrayList<>(Arrays.asList(
					"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", 
					"Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
				));
		
		if (mesAtual == 1) {
			return Arrays.asList(meses.get(7), meses.get(8), meses.get(9), meses.get(10), meses.get(11), meses.get(0));
		} else if (mesAtual == 2) {
			return Arrays.asList(meses.get(8), meses.get(9), meses.get(10), meses.get(11), meses.get(0), meses.get(1));
		} else if (mesAtual == 3) {
			return Arrays.asList(meses.get(9), meses.get(10), meses.get(11), meses.get(0), meses.get(1), meses.get(2));
		} else if (mesAtual == 4) {
			return Arrays.asList(meses.get(10), meses.get(11), meses.get(0), meses.get(1), meses.get(2), meses.get(3));
		} else if (mesAtual == 5) {
			return Arrays.asList(meses.get(11), meses.get(0), meses.get(1), meses.get(2), meses.get(3), meses.get(4));
		} else if (mesAtual == 6) {
			return Arrays.asList(meses.get(0), meses.get(1), meses.get(2), meses.get(3), meses.get(4), meses.get(5));
		} else if (mesAtual == 7) {
			return Arrays.asList(meses.get(1), meses.get(2), meses.get(3), meses.get(4), meses.get(5), meses.get(6));
		} else if (mesAtual == 8) {
			return Arrays.asList(meses.get(2), meses.get(3), meses.get(4), meses.get(5), meses.get(6), meses.get(7));
		} else if (mesAtual == 9) {
			return Arrays.asList(meses.get(3), meses.get(4), meses.get(5), meses.get(6), meses.get(7), meses.get(8));
		} else if (mesAtual == 10) {
			return Arrays.asList(meses.get(4), meses.get(5), meses.get(6), meses.get(7), meses.get(8), meses.get(9));
		} else if (mesAtual == 11) {
			return Arrays.asList(meses.get(5), meses.get(6), meses.get(7), meses.get(8), meses.get(9), meses.get(10));
		} else if (mesAtual == 12) {
			return Arrays.asList(meses.get(6), meses.get(7), meses.get(8), meses.get(9), meses.get(10), meses.get(11));
		} else {
			throw new Exception("O mês atual informado por parâmetro é inválido.");
		}
	}
	
	public static Map<Integer, Integer> obterUltimosSeisMesesEAno(int mesAtual, int anoAtual) {
		Map<Integer, Integer> ultimosSeisMeses = new LinkedHashMap<Integer, Integer>();
		
		int anoAnterior = anoAtual - 1;
		
		if (mesAtual == 1) {
			ultimosSeisMeses.put(8, anoAnterior);
			ultimosSeisMeses.put(9, anoAnterior);
			ultimosSeisMeses.put(10, anoAnterior);
			ultimosSeisMeses.put(11, anoAnterior);
			ultimosSeisMeses.put(12, anoAnterior);
			ultimosSeisMeses.put(1, anoAtual);
		} else if (mesAtual == 2) {
			ultimosSeisMeses.put(9, anoAnterior);
			ultimosSeisMeses.put(10, anoAnterior);
			ultimosSeisMeses.put(11, anoAnterior);
			ultimosSeisMeses.put(12, anoAnterior);
			ultimosSeisMeses.put(1, anoAtual);
			ultimosSeisMeses.put(2, anoAtual);
		} else if (mesAtual == 3) {
			ultimosSeisMeses.put(10, anoAnterior);
			ultimosSeisMeses.put(11, anoAnterior);
			ultimosSeisMeses.put(12, anoAnterior);
			ultimosSeisMeses.put(1, anoAtual);
			ultimosSeisMeses.put(2, anoAtual);
			ultimosSeisMeses.put(3, anoAtual);
		} else if (mesAtual == 4) {
			ultimosSeisMeses.put(11, anoAnterior);
			ultimosSeisMeses.put(12, anoAnterior);
			ultimosSeisMeses.put(1, anoAtual);
			ultimosSeisMeses.put(2, anoAtual);
			ultimosSeisMeses.put(3, anoAtual);
			ultimosSeisMeses.put(4, anoAtual);
		} else if (mesAtual == 5) {
			ultimosSeisMeses.put(12, anoAnterior);
			ultimosSeisMeses.put(1, anoAtual);
			ultimosSeisMeses.put(2, anoAtual);
			ultimosSeisMeses.put(3, anoAtual);
			ultimosSeisMeses.put(4, anoAtual);
			ultimosSeisMeses.put(5, anoAtual);
		} else if (mesAtual == 6) {
			ultimosSeisMeses.put(1, anoAtual);
			ultimosSeisMeses.put(2, anoAtual);
			ultimosSeisMeses.put(3, anoAtual);
			ultimosSeisMeses.put(4, anoAtual);
			ultimosSeisMeses.put(5, anoAtual);
			ultimosSeisMeses.put(6, anoAtual);
		} else if (mesAtual == 7) {
			ultimosSeisMeses.put(2, anoAtual);
			ultimosSeisMeses.put(3, anoAtual);
			ultimosSeisMeses.put(4, anoAtual);
			ultimosSeisMeses.put(5, anoAtual);
			ultimosSeisMeses.put(6, anoAtual);
			ultimosSeisMeses.put(7, anoAtual);
		} else if (mesAtual == 8) {
			ultimosSeisMeses.put(3, anoAtual);
			ultimosSeisMeses.put(4, anoAtual);
			ultimosSeisMeses.put(5, anoAtual);
			ultimosSeisMeses.put(6, anoAtual);
			ultimosSeisMeses.put(7, anoAtual);
			ultimosSeisMeses.put(8, anoAtual);
		} else if (mesAtual == 9) {
			ultimosSeisMeses.put(4, anoAtual);
			ultimosSeisMeses.put(5, anoAtual);
			ultimosSeisMeses.put(6, anoAtual);
			ultimosSeisMeses.put(7, anoAtual);
			ultimosSeisMeses.put(8, anoAtual);
			ultimosSeisMeses.put(9, anoAtual);
		} else if (mesAtual == 10) {
			ultimosSeisMeses.put(5, anoAtual);
			ultimosSeisMeses.put(6, anoAtual);
			ultimosSeisMeses.put(7, anoAtual);
			ultimosSeisMeses.put(8, anoAtual);
			ultimosSeisMeses.put(9, anoAtual);
			ultimosSeisMeses.put(10, anoAtual);
		} else if (mesAtual == 11) {
			ultimosSeisMeses.put(6, anoAtual);
			ultimosSeisMeses.put(7, anoAtual);
			ultimosSeisMeses.put(8, anoAtual);
			ultimosSeisMeses.put(9, anoAtual);
			ultimosSeisMeses.put(10, anoAtual);
			ultimosSeisMeses.put(11, anoAtual);
		} else if (mesAtual == 12) {
			ultimosSeisMeses.put(7, anoAtual);
			ultimosSeisMeses.put(8, anoAtual);
			ultimosSeisMeses.put(9, anoAtual);
			ultimosSeisMeses.put(10, anoAtual);
			ultimosSeisMeses.put(11, anoAtual);
			ultimosSeisMeses.put(12, anoAtual);
		}
		
		return ultimosSeisMeses;
	}
	
	public static boolean isEmailValido(String email) {
		return email.matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[A-z]{2,})$");
	}
	
}
