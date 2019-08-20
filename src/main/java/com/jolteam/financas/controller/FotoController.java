package com.jolteam.financas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.jolteam.financas.model.Foto;
import com.jolteam.financas.service.FotoStorageService;

@Controller
public class FotoController {
	
@Autowired private FotoStorageService fotoStorageService;
	
	@GetMapping("/download/{fotoId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String fotoId) {
        try {
        	// Load file from database
        	Foto foto = fotoStorageService.obterPorId(fotoId);

        	return ResponseEntity.ok()
        		.contentType(MediaType.parseMediaType(foto.getTipo()))
        		.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + foto.getNome() + "\"")
        		.body(new ByteArrayResource(foto.getConteudo()));
        } catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
    }
	
}
