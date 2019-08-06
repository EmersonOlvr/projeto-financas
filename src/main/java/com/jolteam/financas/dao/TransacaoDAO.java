package com.jolteam.financas.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.Usuario;

@Repository
public interface TransacaoDAO extends JpaRepository<Transacao, Integer> {

	List<Transacao> findAllByTipo(TipoTransacao tipo);
	
	@Query("SELECT t FROM Transacao t WHERE t.usuario = :usuario AND t.tipo = :tipo")
	List<Transacao> findAllByUsuarioAndTipo(@Param("usuario") Usuario usuario, @Param("tipo") TipoTransacao tipo);

	List<Transacao> findAllByCategoria(Categoria categoria);
	
	@Query("SELECT t FROM Transacao t WHERE month(t.data) = month(:data) AND year(t.data) = year(:data)"
			+ "AND t.tipo = :tipo AND t.usuario = :usuario")
	List<Transacao> findAllByMes(LocalDate data, TipoTransacao tipo, Usuario usuario);
	
	boolean existsByCategoria(Categoria categoria);
	
	List<Transacao> findAllByUsuario(Usuario usuario);
	
	Optional<Transacao> findByIdAndUsuario(@Param("id") Integer id, @Param("usuario") Usuario usuario);
	
	void deleteByIdAndUsuario(Integer id, Usuario usuario);
	
}
