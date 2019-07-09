package com.jolteam.financas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CategoriaDAO;
import com.jolteam.financas.dao.TransacaoDAO;
import com.jolteam.financas.dao.UsuarioDAO;
import com.jolteam.financas.enums.TiposTransacoes;
import com.jolteam.financas.exceptions.ReceitaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Receita;
import com.jolteam.financas.model.Transacao;

@Service
public class ReceitaService {

	@Autowired private TransacaoDAO transacoes;
	@Autowired private UsuarioDAO usuarios;
	@Autowired private CategoriaDAO categorias;


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
			this.transacoes.save(new Transacao(receita.getUsuario(), TiposTransacoes.RECEITA, receita.getCategoria(),
					receita.getDescricao(), receita.getValor()));
		} catch (Exception e) {
			throw new ReceitaException("Desculpe, algo deu errado.");
		}
	}
	public void salvarCategoriaReceita(Categoria catReceitas) throws ReceitaException {
		//pegar usuario que esta na sessão
		catReceitas.setUsuario(usuarios.getOne(1));
		//define data de criação
		catReceitas.setDataCriacao(LocalDateTime.now());
		
		//define tipo de transação da categoria 
		catReceitas.setTipoTransacao(TiposTransacoes.RECEITA);
		
		//Validação nome e tratamento de dados
		
		if(Strings.isBlank(catReceitas.getNome())) {
			throw new ReceitaException("Insira nome da categoria");
		}
		catReceitas.setNome(catReceitas.getNome().trim());
		if(catReceitas.getNome().length()<2) {
			throw new ReceitaException("O nome precisa ter no mínimo 2 caracteres");
		}
		catReceitas.setNome(catReceitas.getNome().replaceAll("\\s+", " "));
		
		this.categorias.save(catReceitas);
		
	}
}
