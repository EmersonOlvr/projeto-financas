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
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.dto.Receita;

@Service
public class ReceitaService {

	@Autowired private TransacaoDAO transacoes;
	@Autowired private CategoriaDAO categorias;

	public boolean existsByCategoria(Categoria categoria) {
		return this.transacoes.existsByCategoria(categoria);
	}

	public void salvar(Receita receita) throws ReceitaException {
		//Validação de categoria
		if (receita.getCategoria() == null) {
			throw new ReceitaException("Selecione uma categoria.");
		}
		
		//Validação da descrição 
		receita.setDescricao(receita.getDescricao().trim());
		if (!Strings.isBlank(receita.getDescricao()) && receita.getDescricao().length() < 2) {
			throw new ReceitaException("A descrição deve ter no mínimo 2 caracteres.");
		}
		if (receita.getDescricao().length() > 50) {
			throw new ReceitaException("A descrição deve ter no máximo 50 caracteres.");
		}
		if (!receita.getDescricao().matches("^[a-zA-ZÀ-ú0-9 ]*$")) {
			throw new ReceitaException("Descrição inválida: somente letras, espaços e números são permitidos.");
		}
		receita.setDescricao(receita.getDescricao().replaceAll("\\s+", " "));

		//Validação do valor
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
	
	public void salvarCategoriaReceita(Categoria catReceita) throws ReceitaException {
		//define data de criação
		catReceita.setDataCriacao(LocalDateTime.now());
		
		//define tipo de transação da categoria 
		catReceita.setTipoTransacao(TipoTransacao.RECEITA);
		
		//Validação nome e tratamento de dados
		if(Strings.isBlank(catReceita.getNome())) {
			throw new ReceitaException("Insira nome da categoria.");
		}
		catReceita.setNome(catReceita.getNome().trim());
		if(catReceita.getNome().length() < 2) {
			throw new ReceitaException("O nome precisa ter no mínimo 2 caracteres.");
		}
		if (catReceita.getNome().length() > 50) {
			throw new ReceitaException("O nome precisa ter no máximo 50 caracteres.");
		}
		if (!catReceita.getNome().matches("^[a-zA-ZÀ-ú0-9 ]*$")) {
			throw new ReceitaException("Nome inválido: somente letras, espaços e números são permitidos.");
		}
		if (this.categorias.existsByNomeAndTipoTransacaoAndUsuario(catReceita.getNome(), TipoTransacao.RECEITA, catReceita.getUsuario())) {
			throw new ReceitaException("Já existe uma Categoria de Receita com este nome: "+catReceita.getNome());
		}
		catReceita.setNome(catReceita.getNome().replaceAll("\\s+", " "));
		
		this.categorias.save(catReceita);
		
	}
}
