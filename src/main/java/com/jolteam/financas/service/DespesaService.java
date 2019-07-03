package com.jolteam.financas.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.enums.TiposTransacoes;
import com.jolteam.financas.exceptions.DespesaException;
import com.jolteam.financas.model.Despesa;
import com.jolteam.financas.model.Transacao;

@Service
public class DespesaService {

	@Autowired private TransacaoDAO transacoes;
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
			throw new DespesaException("O valor da receita deve ser igual ou maior que 5 centavos (0.05).");
		}
		
		//tentativa de salvar no banco
		
		try {
			this.transacoes.save(new Transacao(despesa.getUsuario(), TiposTransacoes.DESPESA, despesa.getCategoria(),
					despesa.getDescricao(), despesa.getValor()));
		}catch(Exception e){
			throw new DespesaException("Desculpe, algo deu erro");
		}
	}
}
