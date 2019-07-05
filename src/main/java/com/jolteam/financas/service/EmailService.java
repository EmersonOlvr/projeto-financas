package com.jolteam.financas.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.jolteam.financas.enums.TiposEmails;
import com.jolteam.financas.exceptions.EmailException;

@Service
public class EmailService {

	@Autowired JavaMailSender mailSender;
	
	public void enviar(String destinatario, String assunto, String corpo, TiposEmails tipo) throws EmailException {
		if (tipo.equals(TiposEmails.TEXTO)) {
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
		} else {
			MimeMessage mail = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper( mail );
            try {
            	helper.setSubject(assunto);
            	helper.setText(corpo, true);
				helper.setTo(destinatario);
				helper.setFrom("Jolteam <jolteam.ifpe@gmail.com>");
			} catch (MessagingException e) {
				throw new EmailException("Erro ao enviar e-mail: "+e.getMessage());
			}
            mailSender.send(mail);
		}
	}
	
}
