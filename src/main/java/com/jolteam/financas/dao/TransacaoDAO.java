package com.jolteam.financas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.enums.TiposTransacoes;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;

@Repository
public interface TransacaoDAO extends JpaRepository<Transacao, Integer> {

	List<Transacao> findAllByTipo(TiposTransacoes tipo);
	
	@Query("SELECT t FROM Transacao t WHERE t.usuario = :usuario AND t.tipo = :tipo")
	List<Transacao> findAllByUsuarioAndTipo(Usuario usuario, TiposTransacoes tipo);

	boolean existsByCategoria(Categoria categoria);
	
}
