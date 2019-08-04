package com.jolteam.financas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.CofreTransacao;
import com.jolteam.financas.model.Usuario;

@Repository
public interface CofreTransacaoDAO extends JpaRepository<CofreTransacao, Integer> {
	
	List<CofreTransacao> findByid(Integer id);
	List<CofreTransacao> findAllByCofre(Cofre cofre);
	
	void deleteAllByCofre(Cofre cofre);
	
}
