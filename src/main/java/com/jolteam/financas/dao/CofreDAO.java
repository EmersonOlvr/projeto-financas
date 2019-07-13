package com.jolteam.financas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Usuario;

public interface CofreDAO extends JpaRepository<Cofre, Integer> {
	
	List<Cofre> findByIdAndUsuario(Integer id,Usuario usuario);
	List<Cofre> findAll();
	boolean existsByDescricao(String descricao);
	List<Cofre> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
	void deleteById(Integer id);

}
