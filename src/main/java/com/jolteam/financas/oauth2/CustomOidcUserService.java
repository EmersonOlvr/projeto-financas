package com.jolteam.financas.oauth2;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.dto.GoogleOAuth2UserInfo;
import com.jolteam.financas.dto.OAuth2UserInfo;
import com.jolteam.financas.enums.Provedor;
import com.jolteam.financas.model.Usuario;

@Service
public class CustomOidcUserService extends OidcUserService {

	@Autowired private UsuarioDAO usuarios;

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("loadUser()");
		
		OidcUser oidcUser = super.loadUser(userRequest);
		Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();
		GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(attributes);
		
		//System.out.println(userInfo);
		this.updateUser(userInfo);
		
		return oidcUser;
	}

	private void updateUser(OAuth2UserInfo userInfo) {
		System.out.println("updateUser()");
		
		// obtém o usuário do banco, se existir, senão, cria um novo
		Optional<Usuario> u = this.usuarios.findByEmail(userInfo.getEmail());
		boolean usuarioExiste = u.isPresent() ? true : false;
		Usuario usuario = usuarioExiste ? u.get() : new Usuario();
		
		// se o e-mail da conta externa do usuário 
		// não tiver sido cadastrado previamente como uma conta local
		if (usuario.getProvedor() == null || !usuario.getProvedor().equals(Provedor.LOCAL)) {
			// se o OAuth2UserInfo for um usuário do Google
			if (userInfo instanceof GoogleOAuth2UserInfo) {
				userInfo = (GoogleOAuth2UserInfo) userInfo;
				
				// os atributos nome, sobrenome e e-mail são definidos/atualizados independentemente de
				// o usuário já ser cadastrado ou não
				usuario.setNome(userInfo.getName());
				usuario.setSobrenome(((GoogleOAuth2UserInfo) userInfo).getFamilyName());
				usuario.setEmail(userInfo.getEmail());
				
				// já os atributos a seguir só são definidos se o usuário ainda não for cadastrado
				if (!usuarioExiste) {
					usuario.setProvedor(Provedor.GOOGLE);
					usuario.setProvedorId(userInfo.getId());
					
					usuario.setRegistroData(LocalDateTime.now());
					// para pegar o IP do usuário precisamos da requisição (request)
					// porém, neste método não há um objeto HttpServletRequest como parâmetro
					usuario.setRegistroIp("indefinido");
					usuario.setAtivado(true);
					usuario.setPermissao((short) 1);
				}
			}
		}
		
		// adiciona/atualiza o usuário no banco
		this.usuarios.save(usuario);
	}
	
}
