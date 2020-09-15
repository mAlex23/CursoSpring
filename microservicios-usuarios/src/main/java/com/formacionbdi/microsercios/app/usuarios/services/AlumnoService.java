package com.formacionbdi.microsercios.app.usuarios.services;

import java.util.List;

import com.formacionbdi.microsercios.commons.alumnos.model.entity.Alumno;
import com.formacionbdi.microsercios.commons.services.CommonService;

public interface AlumnoService extends CommonService<Alumno>  {
	public List<Alumno> findByNobreOrApellido(String term);
	
	public Iterable<Alumno> findAllById(Iterable<Long> ids);
}
