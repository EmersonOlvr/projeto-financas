package com.jolteam.financas.exceptions;

public class UsuarioDesativadoException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public UsuarioDesativadoException(String mensagem) {
		super(mensagem);
	}

}
