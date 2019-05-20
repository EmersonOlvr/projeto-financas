package com.jolteam.financas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jolteam.financas.model.Usuario;

public interface UsuarioDAO extends JpaRepository<Usuario, Integer> {
	
	List<Usuario> findByEmail(String email);
	
}
