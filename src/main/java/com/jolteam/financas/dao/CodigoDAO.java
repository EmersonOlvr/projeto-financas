package com.jolteam.financas.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.enums.TipoCodigo;
import com.jolteam.financas.model.Codigo;
import com.jolteam.financas.model.Usuario;

@Repository
public interface CodigoDAO extends JpaRepository<Codigo, Integer> {

	Optional<Codigo> findByUsuario(Usuario usuario);
	Optional<Codigo> findByUsuarioAndTipo(Usuario usuario, TipoCodigo tipo);
	
}
