package com.jolteam.financas.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

//	@Autowired private UserDAO users;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, 
			HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException 
	{
		System.out.println("onAuthenticationFailure()");
		System.out.println("Redirecionando para /entrar...");
		this.getRedirectStrategy().sendRedirect(request, response, "/entrar");
	}

}
