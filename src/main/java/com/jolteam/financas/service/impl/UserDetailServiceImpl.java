package com.jolteam.financas.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.model.Usuario;

@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired private UsuarioDAO usuarios;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	Optional<Usuario> usuario = usuarios.findByEmail(email);
        if (!usuario.isPresent()){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(
        		usuario.get().getEmail(), usuario.get().getSenha(), this.getAuthority());
    }
    
	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
	}
    
}
