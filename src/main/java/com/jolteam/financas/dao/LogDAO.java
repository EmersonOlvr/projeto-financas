package com.jolteam.financas.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jolteam.financas.model.Log;

public interface LogDAO extends JpaRepository<Log, Integer> {

	
	
}
