package com.jolteam.financas.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Usuario;

public interface CofreDAO extends JpaRepository<Cofre, Integer> {
	
	public Optional<Cofre> findByIdAndUsuario(Integer id,Usuario usuario);
	public List<Cofre> findAll();
	public boolean existsByFinalidade(String finalidade);
	public List<Cofre> findAllByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
	public void deleteById(Integer id);

}
