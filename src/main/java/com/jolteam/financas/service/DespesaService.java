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
import com.jolteam.financas.exceptions.DespesaException;
import com.jolteam.financas.model.Categoria;
import com.jolteam.financas.model.Transacao;
import com.jolteam.financas.model.dto.Despesa;

@Service
public class DespesaService {

	@Autowired private TransacaoDAO transacoes;
	@Autowired private CategoriaDAO categorias;
	
	public boolean existsByCategoria(Categoria categoria) {
		return this.transacoes.existsByCategoria(categoria);
	}
	
	public void salvar(Despesa despesa) throws DespesaException{
		//Validação de categoria
		if(despesa.getCategoria() == null) {
			throw new DespesaException("Selecione uma categoria.");
		}
		
		//Validação da descrição 
		despesa.setDescricao(despesa.getDescricao().trim());
		if (!Strings.isBlank(despesa.getDescricao()) && despesa.getDescricao().length() < 2){
			throw new DespesaException("A descrição deve ter no mínimo 2 caracteres.");
		}
		if (despesa.getDescricao().length() > 50) {
			throw new DespesaException("A descrição deve ter no máximo 50 caracteres.");
		}
		if (!despesa.getDescricao().matches("^[a-zA-ZÀ-ú0-9 ]*$")) {
			throw new DespesaException("Descrição inválida: somente letras, espaços e números são permitidos.");
		}
		despesa.setDescricao(despesa.getDescricao().replaceAll("\\s+", " "));
		
		//Validação do valor
		if(despesa.getValor().compareTo(new BigDecimal("0.05"))== -1) {
			throw new DespesaException("O valor da despesa deve ser igual ou maior que R$ 0,05.");
		}
		
		// tenta salvar no banco...
		try {
			this.transacoes.save(new Transacao(despesa.getUsuario(), TipoTransacao.DESPESA, despesa.getCategoria(),
					despesa.getDescricao(), despesa.getValor()));
		} catch(DataIntegrityViolationException e) {
			throw new DespesaException("Valor inválido: máximo de 19 números.");
		} catch(Exception e) {
			throw new DespesaException("Desculpe, algo deu errado.");
		}
	}
	
	public void salvarCategoriaDespesa(Categoria catDespesa) throws DespesaException {
		//define data de criação
		catDespesa.setDataCriacao(LocalDateTime.now());
		
		//define tipo de transação da categoria 
		catDespesa.setTipoTransacao(TipoTransacao.DESPESA);
		
		//Validação nome e tratamento de dados
		if(Strings.isBlank(catDespesa.getNome())) {
			throw new DespesaException("Insira nome da categoria.");
		}
		catDespesa.setNome(catDespesa.getNome().trim());
		if(catDespesa.getNome().length() < 2) {
			throw new DespesaException("O nome precisa ter no mínimo 2 caracteres.");
		}
		if (catDespesa.getNome().length() > 50) {
			throw new DespesaException("O nome precisa ter no máximo 50 caracteres.");
		}
		if (!catDespesa.getNome().matches("^[a-zA-ZÀ-ú0-9 ]*$")) {
			throw new DespesaException("Nome inválido: somente letras, espaços e números são permitidos.");
		}
		if (this.categorias.existsByNomeAndTipoTransacao(catDespesa.getNome(), TipoTransacao.DESPESA)) {
			throw new DespesaException("Já existe uma Categoria de Despesa com este nome: "+catDespesa.getNome());
		}
		catDespesa.setNome(catDespesa.getNome().replaceAll("\\s+", " "));
		
		this.categorias.save(catDespesa);
		
	}
}
