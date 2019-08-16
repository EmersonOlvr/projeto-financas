package com.jolteam.financas.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Usuario;

@Repository
public interface CategoriaDAO extends JpaRepository<Categoria, Integer> {

	Optional<Categoria> findByIdAndUsuarioAndTipoTransacao(Integer id, Usuario usuario, TipoTransacao tipoTransacao);
	
	List<Categoria> findAllByUsuario(Usuario usuario);
	List<Categoria> findByUsuarioAndTipoTransacao(Usuario usuario, TipoTransacao tipoTransacao);
	List<Categoria> findByUsuarioAndTipoTransacaoOrderByDataCriacaoDesc(Usuario usuario, TipoTransacao tipoTransacao);
	List<Categoria> findAllByTipoTransacao(TipoTransacao transacao);
	
	boolean existsByNomeAndTipoTransacao(String nome, TipoTransacao tipoTransacao);
	
	
}
