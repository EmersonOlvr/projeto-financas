package com.jolteam.financas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.TiposTransacoes;
import com.jolteam.financas.model.Usuario;

public interface CategoriaDAO extends JpaRepository<Categoria, Integer> {

	List<Categoria> findAllByUsuario(Usuario usuario);
	List<Categoria> findByUsuarioAndTipoTransacao(Usuario usuario, TiposTransacoes tipoTransacao);
	
}
