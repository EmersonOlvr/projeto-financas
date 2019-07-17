package com.jolteam.financas.util;

import java.math.BigDecimal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.thymeleaf.expression.Numbers;

import com.jolteam.financas.enums.BigDecimalInvalidoException;

public class Util {

	// método para obter o IP do usuário
	public String getUserIp(HttpServletRequest request) {
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
	
}
