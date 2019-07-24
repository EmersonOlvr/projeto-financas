package com.jolteam.financas.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.jolteam.financas.util.Util;

@Controller
public class ErroController implements ErrorController {

	@GetMapping("/error")
	public String handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		Object urlRequisitada = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		String ip = Util.getUserIp(request);
		
		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());
			
			if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
				System.out.println("Erro 403: "+urlRequisitada+" (IP: "+ip+")");
				return "error/403";
			} else if(statusCode == HttpStatus.NOT_FOUND.value()) {
				System.out.println("Erro 404: "+urlRequisitada+" (IP: "+ip+")");
				return "error/404";
			} else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				System.out.println("Erro 500: "+urlRequisitada+" (IP: "+ip+")");
				return "error/500";
			}
		}
		
		return "error";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}
