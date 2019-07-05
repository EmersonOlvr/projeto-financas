package com.jolteam.financas.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CodigoDAO;
import com.jolteam.financas.enums.TiposCodigos;
import com.jolteam.financas.model.Codigo;
import com.jolteam.financas.model.Usuario;

@Service
public class CodigoService {

	@Autowired CodigoDAO codigos;
	
	public Optional<Codigo> findByUsuario(Usuario usuario) {
		return this.codigos.findByUsuario(usuario);
	}
	public Optional<Codigo> findByUsuarioAndTipo(Usuario usuario, TiposCodigos tipo) {
		return this.codigos.findByUsuarioAndTipo(usuario, tipo);
	}
	public void delete(Codigo codigo) {
		this.codigos.delete(codigo);
	}
	
}
