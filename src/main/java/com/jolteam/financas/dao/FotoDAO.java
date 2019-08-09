package com.jolteam.financas.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jolteam.financas.model.Foto;
@Repository
public interface FotoDAO extends JpaRepository<Foto, Integer> {

	Optional<Foto> findById(String id);
	

}
