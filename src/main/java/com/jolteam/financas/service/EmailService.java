package com.jolteam.financas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.jolteam.financas.exceptions.EmailException;

@Service
public class EmailService {

	@Autowired JavaMailSender mailSender;
	
	public void enviar(String destinatario, String assunto, String corpo) throws EmailException {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setSubject(assunto);
		message.setText(corpo);
		message.setTo(destinatario);
		message.setFrom("Jolteam <jolteam.ifpe@gmail.com>");
		
		try {
			this.mailSender.send(message);
		} catch (Exception e) {
			throw new EmailException("Erro ao enviar e-mail: "+e.getMessage());
		}
	}
	
}
