package com.jolteam.financas.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

//@Entity
//@Table(name = "operacoes_automaticas")
@SuppressWarnings("unused")
public class OperacaoAutomatica {

	//@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	//@OneToOne
	private Transacao transacao;
	
}
