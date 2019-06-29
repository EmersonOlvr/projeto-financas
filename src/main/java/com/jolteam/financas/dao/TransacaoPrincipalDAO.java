package com.jolteam.financas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jolteam.financas.model.TipoTransacao;
import com.jolteam.financas.model.TransacaoPrincipal;
import com.jolteam.financas.model.Usuario;

public interface TransacaoPrincipalDAO extends JpaRepository<TransacaoPrincipal, Integer> {

	@Query("SELECT tp FROM TransacaoPrincipal tp WHERE tp.categoria IN (SELECT c FROM Categoria c WHERE c.tipo = :tipo)")
	List<TransacaoPrincipal> findAllByTipo(TipoTransacao tipo);
	
	@Query("SELECT tp FROM TransacaoPrincipal tp WHERE tp.usuario = :usuario AND "
			+ "tp.categoria IN (SELECT c FROM Categoria c WHERE c.tipo = :tipo)")
	List<TransacaoPrincipal> findAllByUsuarioAndTipo(Usuario usuario, TipoTransacao tipo);
	
}
