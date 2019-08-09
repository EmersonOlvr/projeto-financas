package com.jolteam.financas.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.jolteam.financas.dao.FotoDAO;
import com.jolteam.financas.exceptions.FotoInvalidaException;
import com.jolteam.financas.exceptions.FotoNotFoundException;
import com.jolteam.financas.model.Foto;

@Service
public class FotoStorageService {

	@Autowired FotoDAO fotos;
	
    public Foto salvar(Foto foto) {
    	return fotos.save(foto);
    }
    
    public Foto obterPorId(String id) throws FotoNotFoundException {
        return fotos.findById(id).orElseThrow(() -> new FotoNotFoundException("Foto não encontrada: " + id));
    }
    
    public Foto validar(MultipartFile file) throws FotoInvalidaException {
    	if (!(file.getContentType().equals("image/jpeg") 
		       	|| file.getContentType().equals("image/gif")
		       	|| file.getContentType().equals("image/png"))) 
    	{
			throw new FotoInvalidaException("Foto em formato inválido. Formatos permitidos: JPG, GIF, PNG.");
		} else if (file.getSize() > 5242880) {
			throw new FotoInvalidaException("O tamanho máximo da foto não deve exceder 5 MB.");
		} else {
			try {
				String nomeFoto = StringUtils.cleanPath(file.getOriginalFilename());
				String tipoFoto = file.getContentType();
				byte[] conteudoFoto = file.getBytes();
				
				return new Foto(nomeFoto, tipoFoto, conteudoFoto,null);
			} catch (IOException ioe) {
				throw new FotoInvalidaException("Desculpe, algo deu errado.");
			}
		}
    }
    
}
