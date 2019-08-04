package com.jolteam.financas.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jolteam.financas.util.Util;

@Controller
public class ErroController implements ErrorController {

	@RequestMapping(value = "/error")
	public String handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		Object urlRequisitada = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		String ip = Util.getUserIp(request);
		String metodo = request.getMethod();
		String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		
		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());
			
			System.out.println(dataAtual+"  Erro "+statusCode+" ("+metodo+"): "+urlRequisitada+" (IP: "+ip+")");
			return "error/"+statusCode;
		}
		
		System.out.println(dataAtual+"  Erro desconhecido: "+urlRequisitada+" (IP: "+ip+")");
		return "error/500";
	}

	@Override
	public String getErrorPath() {
		System.out.println("getErrorPath()");
		return "/error";
	}

}
