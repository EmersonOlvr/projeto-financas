package com.jolteam.financas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jolteam.financas.dao.CofreDAO;
import com.jolteam.financas.dao.CofreTransacaoDAO;
import com.jolteam.financas.enums.TipoTransacao;
import com.jolteam.financas.exceptions.CofreException;
import com.jolteam.financas.model.Cofre;
import com.jolteam.financas.model.CofreTransacao;
import com.jolteam.financas.model.Usuario;

@Service
public class CofreService {
	
	@Autowired private CofreDAO cofres;
	@Autowired private CofreTransacaoDAO cofreTransacao;
	
	public Optional<Cofre> obterPorId(Integer id) {
		Optional<Cofre> cofre = this.cofres.findById(id);
		if (cofre.isPresent()) {
			cofre.get().setTotalAcumulado(this.totalAcumuladoDe(cofre.get()));
		}
		return cofre;
	}
	public Optional<Cofre> obterPorIdEUsuario(Integer id,Usuario usuario) {
		Optional<Cofre> cofre = this.cofres.findByIdAndUsuario(id, usuario);
		if (cofre.isPresent()) {
			cofre.get().setTotalAcumulado(this.totalAcumuladoDe(cofre.get()));
		}
		return cofre;
	}
	public List<Cofre> listarPorUsuario(Usuario usuario) {
		List<Cofre> cofres = this.cofres.findAllByUsuarioOrderByDataCriacaoDesc(usuario);
		
		for (Cofre cofre : cofres) {
			cofre.setTotalAcumulado(this.totalAcumuladoDe(cofre));
			cofre.setTotalRestante(cofre.getTotalDesejado().subtract(cofre.getTotalAcumulado()));
		}
		
		return cofres;
	}
	public List<Cofre> listarTodos() {
		List<Cofre> cofres = this.cofres.findAll();
		
		for (Cofre cofre : cofres) {
			cofre.setTotalAcumulado(this.totalAcumuladoDe(cofre));
		}
		
		return cofres;
	}
	
	public Optional<CofreTransacao> obterTransacaoPorId(Integer id) {
		return this.cofreTransacao.findById(id);
	}
	
	public List<CofreTransacao> listarTransacoesPorCofre(Cofre cofre) {
		return this.cofreTransacao.findAllByCofre(cofre);
	}
	public void deletarTransacao(CofreTransacao cofreTransacao) {
		this.cofreTransacao.delete(cofreTransacao);
	}
	public boolean deletarTransacaoPorId(Integer id, Usuario usuario) {
		Optional<CofreTransacao> transacao = this.cofreTransacao.findById(id);
		if (transacao.isPresent()) {
			// só excluir a transação se o dono do cofre desta transação for o mesmo que está logado
			// por exemplo, se um usuário souber o ID da transação de um cofre de um outro usuário
			// e acessar a URL para excluir passando este ID, nada será feito, pois o cofre não pertence a ele
			if (transacao.get().getCofre().getUsuario().equals(usuario)) {
				this.cofreTransacao.deleteById(id);
				return true;
			}
		}
		return false;
	}
	
	public boolean existsByDescricao(String descricao) {
		return this.cofres.existsByFinalidade(descricao);
	}
	
	
	public Cofre salvar(Cofre cofre) throws CofreException {
		// Validação da Finalidade
		if (Strings.isBlank(cofre.getFinalidade())) {
			throw new CofreException("Informe a finalidade.");
		}
		cofre.setFinalidade(cofre.getFinalidade().trim());
		if (cofre.getFinalidade().length() < 2) {
			throw new CofreException("A finalidade deve ter no mínimo 2 caracteres.");
		}
		cofre.setFinalidade(cofre.getFinalidade().replaceAll("\\s+", " "));
		
		// se o cofre ainda não estiver cadastrado
		if (cofre.getId() == null) {
			// valida se finalidade ja existe
			if (this.cofres.existsByUsuarioAndFinalidade(cofre.getUsuario(), cofre.getFinalidade())) {
				throw new CofreException("Já existe cofre com a finalidade informada.");
			}
			
			// Definindo data criação
			cofre.setDataCriacao(LocalDateTime.now());
		}
		
		// se o valor desejado for nulo ou igual à 0
		if (cofre.getTotalDesejado() == null || cofre.getTotalDesejado().compareTo(new BigDecimal("0")) == 0) {
			throw new CofreException("Informe o total desejado.");
		}
		if (cofre.getTotalDesejado().compareTo(new BigDecimal("0.05")) == -1) {
			throw new CofreException("O total desejado deve ser igual ou maior que R$ 0,05.");
		}
		
		return this.cofres.save(cofre);
		
	}
	
	public void deleteById(Integer id) {
		this.cofres.deleteById(id);
	}
	@Transactional
	public void delete(Cofre cofre) {
		this.cofreTransacao.deleteAllByCofre(cofre);
		this.cofres.delete(cofre);
	}
	
	public void adicionarTransacao(Cofre cofre, BigDecimal valor, TipoTransacao tipoTransacao) {
		if (cofre != null && cofre.getId() != null) {
			if (valor != null) {
				int result = valor.compareTo(new BigDecimal("0"));
				if (result != 0) {
					this.cofreTransacao.save(new CofreTransacao(cofre, valor, LocalDateTime.now()));
				}
			}
		}
	}
	
	public BigDecimal totalAcumuladoDe(Cofre cofre) {
		BigDecimal totalAcumulado = new BigDecimal("0");
		List<CofreTransacao> transacoes = this.cofreTransacao.findAllByCofre(cofre);
		for (CofreTransacao transacao : transacoes) {
			totalAcumulado = totalAcumulado.add(transacao.getValor());
		}
		return totalAcumulado;
	}
	
}
