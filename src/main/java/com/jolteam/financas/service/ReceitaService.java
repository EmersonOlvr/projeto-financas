package com.jolteam.financas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.exceptions.ReceitaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Receita;
import com.jolteam.financas.model.Transacao;

@Service
public class ReceitaService {

	@Autowired private TransacaoDAO transacoes;
	@Autowired private CategoriaDAO categorias;

	public boolean existsByCategoria(Categoria categoria) {
		return this.transacoes.existsByCategoria(categoria);
	}

	public void salvar(Receita receita) throws ReceitaException {
		// validações
		if (receita.getCategoria() == null) {
			throw new ReceitaException("Selecione uma categoria.");
		}
		receita.setDescricao(receita.getDescricao().trim());
		if (!Strings.isBlank(receita.getDescricao()) && receita.getDescricao().length() < 2) {
			throw new ReceitaException("A descrição deve ter no mínimo 2 caracteres.");
		}
		if (receita.getValor().compareTo(new BigDecimal("0.05")) == -1) {
			throw new ReceitaException("O valor da receita deve ser igual ou maior que R$ 0,05.");
		}

		// tenta salvar no banco...
		try {
			this.transacoes.save(new Transacao(receita.getUsuario(), TipoTransacao.RECEITA, receita.getCategoria(),
					receita.getDescricao(), receita.getValor()));
		} catch(DataIntegrityViolationException e) {
			throw new ReceitaException("Valor inválido: máximo de 19 números.");
		} catch(Exception e) {
			throw new ReceitaException("Desculpe, algo deu errado.");
		}
	}
	public void salvarCategoriaReceita(Categoria catReceitas) throws ReceitaException {
		//define data de criação
		catReceitas.setDataCriacao(LocalDateTime.now());
		
		//define tipo de transação da categoria 
		catReceitas.setTipoTransacao(TipoTransacao.RECEITA);
		
		//Validação nome e tratamento de dados
		
		if(Strings.isBlank(catReceitas.getNome())) {
			throw new ReceitaException("Insira nome da categoria.");
		}
		if(!catReceitas.getNome().matches("^[A-zÀ-ú ]*$")) {
			throw new ReceitaException("Nome inválido: somente letras e espaços são permitidos.");
		}
		catReceitas.setNome(catReceitas.getNome().trim());
		if(catReceitas.getNome().length()<2) {
			throw new ReceitaException("O nome precisa ter no mínimo 2 caracteres.");
		}
		catReceitas.setNome(catReceitas.getNome().replaceAll("\\s+", " "));
		
		this.categorias.save(catReceitas);
		
	}
}
