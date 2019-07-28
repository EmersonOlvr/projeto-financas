package com.jolteam.financas.oauth2;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.enums.Provedor;
import com.jolteam.financas.exceptions.UsuarioInexistenteException;
import com.jolteam.financas.model.Usuario;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired private UsuarioDAO usuarios;

//	@Autowired private JwtTokenUtil jwtTokenUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
			HttpServletResponse response, 
			Authentication authentication) throws IOException, ServletException 
	{
		System.out.println("onAuthenticationSuccess()");
		
		if (response.isCommitted()) {
			return;
		}
		
		DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
		Map<String, Object> attributes = oidcUser.getAttributes();
		String email = (String) attributes.get("email");
		try {
			// o correto seria não lançar uma excessão, pois, antes de chegar neste método
			// o usuário foi inserido no banco (no método updateUser()), mas é melhor prevenir do que remediar 
			// (por isso o uso do try/catch).
			Usuario usuario = this.usuarios.findByEmail(email).orElseThrow(() -> 
				new UsuarioInexistenteException("Erro no Login. E-mail inexistente."));
			
			if (!usuario.getProvedor().equals(Provedor.LOCAL)) {
//				String token = this.jwtTokenUtil.generateToken(usuario);
//				String redirectionUrl = UriComponentsBuilder.fromUriString("/home")
//						.queryParam("auth_token", token)
//						.build()
//						.toUriString();
				
				// define o IP de registro do usuário caso esteja indefinido
				if (usuario.getRegistroIp().equals("indefinido")) {
					usuario.setRegistroIp(request.getRemoteAddr());
					this.usuarios.save(usuario);
				}
				
				// salva o usuário na sessão
				request.getSession().setAttribute("usuarioLogado", usuario);
				
				// redireciona para /home
				this.getRedirectStrategy().sendRedirect(request, response, "/home");
				System.out.println("Redirecionando para /home...");
			} else {
				this.getRedirectStrategy().sendRedirect(request, response, "/entrar?erro=email_em_uso");
			}
		} catch (UsuarioInexistenteException e) {
			System.out.println(e.getMessage());
			
			// isso é estranho mas o e-mail informado não existe, portanto, não dá pra realizar login.
			// sendo assim, redireciona para /entrar
			this.getRedirectStrategy().sendRedirect(request, response, "/entrar");
		}
		
	}

}
