package com.jolteam.financas.dao;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.model.Usuario;

@Repository
public interface UsuarioDAO extends JpaRepository<Usuario, Integer> {
	
	Optional<Usuario> findByEmail(String email);
	List<Usuario> findAllByEmail(String email);
	boolean existsByEmail(String email);
	
	
}
