package com.jolteam.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.LogDAO;
import com.jolteam.financas.model.Log;

@Service
public class LogService {

	@Autowired private LogDAO logs;
	
	public Log save(Log log) {
		return this.logs.save(log);
	}
	
}
