package com.jolteam.financas.util;

import javax.servlet.http.HttpServletRequest;

public class Util {

	// método para obter o IP do usuário
	public String getUserIp(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
	
}
