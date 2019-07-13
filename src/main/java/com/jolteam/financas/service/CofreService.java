package com.jolteam.financas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CofreDAO;
import com.jolteam.financas.dao.CofreTransacaoDAO;
import com.jolteam.financas.exceptions.CofreException;
import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.Usuario;

@Service
public class CofreService {
	
	@Autowired private CofreDAO cofres;
	@Autowired private CofreTransacaoDAO transacaoCofre;
	
	public List<Cofre> findAll(){
		return this.cofres.findAll();
	}
	public void deleteById(Integer id) {
		this.cofres.deleteById(id);
	}
	public boolean existsByDescricao(String descricao) {
		return this.cofres.existsByDescricao(descricao);
	}
	public List<Cofre> findByIdAndUsuario(Integer id,Usuario usuario){
		return this.cofres.findByIdAndUsuario(id, usuario);
	}
	
	public List<Cofre> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario){
		return this.cofres.findByUsuarioOrderByDataCriacaoDesc(usuario);
	}
	
	public void salvaCofrer(Cofre cofre) throws CofreException {
		//Definindo data criação
		cofre.setDataCriacao(LocalDateTime.now());
		//valida se descricao ja existe
		if(this.cofres.existsByDescricao(cofre.getDescricao())) {
			throw new CofreException("Já existe um cofre com essa descrição.");
		}
		//Validação de Descrição
		cofre.setDescricao(cofre.getDescricao().trim());
		if(cofre.getDescricao().isEmpty()) {
			throw new CofreException("Insira descrição");
		}else if(cofre.getDescricao().length()<2) {
			throw new CofreException("A descrição deve ter no mínimo 2 caracteres.");
		}
		cofre.setDescricao(cofre.getDescricao().replaceAll("\\s+", " "));
		
		//Validação do valor
		if(cofre.getTotalDesejado().compareTo(new BigDecimal("0.05"))== -1) {
			throw new CofreException("O valor do cofre deve ser igual ou maior que 5 centavos (0.05).");
		}
		
		try {
			this.cofres.save(cofre);
			
		}catch(Exception e) {
			throw new CofreException("Desculpe, algo deu errado.");
		}
		
	}
	
}
