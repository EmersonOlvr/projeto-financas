package com.jolteam.financas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.CofreTransacao;

@Repository
public interface CofreTransacaoDAO extends JpaRepository<CofreTransacao, Integer> {
	
	public List<CofreTransacao> findByid(Integer id);
	public List<CofreTransacao> findAllByCofre(Cofre cofre);
	
	public void deleteAllByCofre(Cofre cofre);
	
}
