package com.jolteam.financas.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;

@Repository
public interface LogDAO extends JpaRepository<Log, Integer> {

	Page<Log> findAllByUsuario(Usuario usuario, Pageable page);
	
}
