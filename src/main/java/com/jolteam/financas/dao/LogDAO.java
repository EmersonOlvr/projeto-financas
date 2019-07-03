package com.jolteam.financas.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.model.Log;

@Repository
public interface LogDAO extends JpaRepository<Log, Integer> {

	
	
}
