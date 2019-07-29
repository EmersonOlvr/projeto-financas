package com.jolteam.financas.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	// este método é chamado automaticamente quando ocorre algum erro na hora que
	// o usuário faz login no provedor (Google/Facebook), por exemplo, quando o usuário nega
	// as permissões que precisamos
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, 
			HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException 
	{
		System.out.println("onAuthenticationFailure()");
		
		String eMsg = exception.getMessage();
		String erro = !Strings.isEmpty(eMsg) ? eMsg.replace("[", "").replace("]", "") : eMsg;
		
		System.out.println("erro: "+erro);
		
		this.getRedirectStrategy().sendRedirect(request, response, "/entrar?erro="+erro);
	}

}
