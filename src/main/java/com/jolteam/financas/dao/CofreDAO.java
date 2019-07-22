package com.jolteam.financas.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Usuario;

public interface CofreDAO extends JpaRepository<Cofre, Integer> {
	
	Optional<Cofre> findByIdAndUsuario(Integer id,Usuario usuario);
	List<Cofre> findAll();
	boolean existsByFinalidade(String finalidade);
	boolean existsByUsuarioAndFinalidade(Usuario usuario, String finalidade);
	List<Cofre> findAllByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
	void deleteById(Integer id);

}
