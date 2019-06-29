package com.jolteam.financas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.TransacaoPrincipalDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.exceptions.ReceitaException;
import com.jolteam.financas.model.Receita;
import com.jolteam.financas.model.TransacaoPrincipal;

@Service
public class ReceitaService {

	@Autowired private TransacaoPrincipalDAO transacoes;
	@Autowired private UsuarioDAO usuarios;

	public void salvar(Receita receita) throws ReceitaException {
		// usuário que está na sessão
		receita.setUsuario(usuarios.getOne(1));
		
		// validações
		if (receita.getCategoria() == null) {
			throw new ReceitaException("Selecione uma categoria.");
		}
		receita.setDescricao(receita.getDescricao().trim());
		if (receita.getDescricao().isEmpty()) {
			throw new ReceitaException("Insira uma descrição.");
		} else if (receita.getDescricao().length() < 2) {
			throw new ReceitaException("A descrição deve ter no mínimo 2 caracteres.");
		}
		if (receita.getValor().compareTo(new BigDecimal("0.05")) == -1) {
			throw new ReceitaException("O valor da receita deve ser igual ou maior que 5 centavos (0.05)");
		}

		// tenta salvar no banco...
		try {
			TransacaoPrincipal transacaoReceita = new TransacaoPrincipal(receita.getUsuario(), receita.getCategoria(), 
					receita.getDescricao(), receita.getValor(), LocalDateTime.now());
			this.transacoes.save(transacaoReceita);
		} catch (Exception e) {
			throw new ReceitaException("Desculpe, algo deu errado.");
		}
	}

}
