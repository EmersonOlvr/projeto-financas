package com.jolteam.financas.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.thymeleaf.expression.Numbers;

import com.jolteam.financas.exceptions.BigDecimalInvalidoException;

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
	
	public static boolean isEmailValido(String email) {
		return email.matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[A-z]{2,})$");
	}
	
}
