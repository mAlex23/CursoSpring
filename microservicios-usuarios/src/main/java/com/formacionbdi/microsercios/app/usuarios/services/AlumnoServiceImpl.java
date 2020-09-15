package com.formacionbdi.microsercios.app.usuarios.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionbdi.microsercios.app.usuarios.models.repository.AlumnoRepository;
import com.formacionbdi.microsercios.commons.alumnos.model.entity.Alumno;
import com.formacionbdi.microsercios.commons.services.CommonServiceImpl;

@Service
public class AlumnoServiceImpl extends CommonServiceImpl<Alumno, AlumnoRepository> implements AlumnoService {

	@Override
	@Transactional(readOnly = true)
	public List<Alumno> findByNobreOrApellido(String term) {
		return repository.findByNobreOrApellido(term);
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<Alumno> findAllById(Iterable<Long> ids) {
		return repository.findAllById(ids);
	}

}
