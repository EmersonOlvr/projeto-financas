package com.jolteam.financas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.TransacaoPrincipalDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.exceptions.DespesaException;
import com.jolteam.financas.model.Despesa;
import com.jolteam.financas.model.TransacaoPrincipal;

@Service
public class DespesaService {

	@Autowired private TransacaoPrincipalDAO transacoes;
	@Autowired private UsuarioDAO usuarios;
	
	public void salvar(Despesa despesa) throws DespesaException{
		
		// pegar usuário que está na sessãos
		despesa.setUsuario(usuarios.getOne(1));
		
		//Validação de categoria
		if(despesa.getCategoria() == null) {
			throw new DespesaException("Selecione uma categoria.");
		}
		//Validação da descrição
		despesa.setDescricao(despesa.getDescricao().trim());
		if(despesa.getDescricao().isEmpty()) {
			throw new DespesaException("Insira uma descrição.");
		}else if(despesa.getDescricao().length() < 2){
			throw new DespesaException("A descrição deve ter no mínimo 2 caracteres.");
		}
		//Validação do valor
		if(despesa.getValor().compareTo(new BigDecimal("0.05"))== -1) {
			throw new DespesaException("O valor da despesa deve ser igual ou maior que 5 centavos (0.05).");
		}
		
		//tentativa de salvar no banco
		try {
			TransacaoPrincipal transacaoDespesa = new TransacaoPrincipal(despesa.getUsuario(), despesa.getCategoria(), 
					despesa.getDescricao(), despesa.getValor(), LocalDateTime.now());
			this.transacoes.save(transacaoDespesa);
		}catch(Exception e){
			throw new DespesaException("Desculpe, algo deu erro");
		}
	}
}
