package com.jolteam.financas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CofreDAO;
import com.jolteam.financas.model.Cofre;

@Service
public class CofreService {
	
	@Autowired private CofreDAO cofres;
	
	public List<Cofre> findAll(){
		return this.cofres.findAll();
	}
	
}
