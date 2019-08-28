package com.jolteam.financas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Usuario;

@Service
public class CategoriaService {

	@Autowired private CategoriaDAO categorias;
	
	public List<Categoria> listarTodas() {
		return this.categorias.findAll();
	}
	
	public List<Categoria> listarTodasPorUsuario(Usuario usuario) {
		return this.categorias.findAllByUsuario(usuario);
	}
	
	public List<Categoria> listarTodasPorUsuarioETipoTransacao(Usuario usuario, TipoTransacao tipoTransacao) {
		return this.categorias.findAllByUsuarioAndTipoTransacao(usuario, tipoTransacao, Sort.by("dataCriacao").descending());
	}
	
	public Optional<Categoria> obterPorIdEUsuarioETipoTransacao(Integer id, Usuario usuario, TipoTransacao tipoTransacao) {
		return this.categorias.findByIdAndUsuarioAndTipoTransacao(id, usuario, tipoTransacao);
	}
	
	public void deletarPorId(Integer id) {
		this.categorias.deleteById(id);
	}
	
}
