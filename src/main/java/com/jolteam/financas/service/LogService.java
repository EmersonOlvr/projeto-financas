package com.jolteam.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.LogDAO;
import com.jolteam.financas.model.Log;
import com.jolteam.financas.model.Usuario;

@Service
public class LogService {

	@Autowired private LogDAO logs;
	
	public Log save(Log log) {
		return this.logs.save(log);
	}
	
	public Page<Log> findAllByUsuario(Usuario usuario, Pageable page) {
		return this.logs.findAllByUsuario(usuario, page);
	}
	
}
