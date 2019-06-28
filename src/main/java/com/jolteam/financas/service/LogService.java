package com.jolteam.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.LogDAO;
import com.jolteam.financas.model.Log;

@Service
public class LogService {

	@Autowired private LogDAO logs;
	
	public void salvar(Log log) throws Exception {
		try {
			this.logs.save(log);
		} catch (Exception e) {
			throw new Exception("Erro ao salvar log no banco.");
		}
	}
	
}
