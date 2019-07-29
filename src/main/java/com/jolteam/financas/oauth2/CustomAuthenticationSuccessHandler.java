package com.jolteam.financas.oauth2;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.jolteam.financas.dao.LogDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.enums.Provedor;
import com.jolteam.financas.enums.TipoLog;
import com.jolteam.financas.exceptions.UsuarioInexistenteException;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired private LogDAO logs;
	@Autowired private UsuarioDAO usuarios;

	// este método é chamado após o usuário oauth ser inserido/atualizado no banco
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
			HttpServletResponse response, 
			Authentication authentication) throws IOException, ServletException 
	{
		System.out.println("onAuthenticationSuccess()");
		
		if (response.isCommitted()) {
			return;
		}
		
		// obtém os atributos da conta do usuário do provedor oauth
		DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
		Map<String, Object> attributes = oauth2User.getAttributes();
		
		String email = (String) attributes.get("email");
		try {
			Usuario usuario = this.usuarios.findByEmail(email).orElseThrow(() -> 
				new UsuarioInexistenteException("E-mail inexistente."));
			
			// se o provedor não for local, ou seja, é um conta do Google ou do Facebook
			if (!usuario.getProvedor().equals(Provedor.LOCAL)) {
				// define o IP de registro do usuário caso esteja indefinido
				if (usuario.getRegistroIp().equals("indefinido")) {
					usuario.setRegistroIp(request.getRemoteAddr());
					this.usuarios.save(usuario);
				}
				
				// salva o usuário na sessão
				request.getSession().setAttribute("usuarioLogado", usuario);
				
				// salva um log de login no banco
				this.logs.save(new Log(usuario, TipoLog.LOGIN, LocalDateTime.now(), request.getRemoteAddr()));
				
				// redireciona para /home
				this.getRedirectStrategy().sendRedirect(request, response, "/home");
				System.out.println("Redirecionando para /home...");
			} else {
				this.getRedirectStrategy().sendRedirect(request, response, "/entrar?erro=email_em_uso");
			}
		} catch (UsuarioInexistenteException e) {
			// isso é estranho mas o e-mail informado não existe, portanto, não dá pra realizar login.
			// sendo assim, redireciona para /entrar
			this.getRedirectStrategy().sendRedirect(request, response, "/entrar?erro=email_inexistente");
		}
		
	}

}
