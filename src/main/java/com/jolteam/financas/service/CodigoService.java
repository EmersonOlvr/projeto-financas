package com.jolteam.financas.service;

import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CodigoDAO;
import com.jolteam.financas.enums.TipoCodigo;
import com.jolteam.financas.model.Codigo;
import com.jolteam.financas.model.Usuario;

@Service
public class CodigoService {

	@Autowired private UsuarioService usuarioService;
	
	@Autowired private CodigoDAO codigos;
	
	public Optional<Codigo> findByUsuario(Usuario usuario) {
		return this.codigos.findByUsuario(usuario);
	}
	public Optional<Codigo> findByUsuarioAndTipo(Usuario usuario, TipoCodigo tipo) {
		return this.codigos.findByUsuarioAndTipo(usuario, tipo);
	}
	public void delete(Codigo codigo) {
		this.codigos.delete(codigo);
	}
	
	public boolean isCodigoValido(Integer id, String codigo, TipoCodigo tipoCodigo) {
		if (id == null || Strings.isEmpty(codigo)) {
			return false;
		} else {
			Optional<Usuario> usuario = this.usuarioService.findById(id);
			if (usuario.isPresent()) {
				Optional<Codigo> codigoExistente = this.findByUsuarioAndTipo(usuario.get(), tipoCodigo);
				if (codigoExistente.isPresent()) {
					String codigoConfirmacao = codigoExistente.get().getCodigo().replaceAll("[\\-]+", "");
					if (codigo.equals(codigoConfirmacao)) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
}
