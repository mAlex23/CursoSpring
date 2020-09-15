package com.formacionbdi.microsercios.app.cursos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionbdi.microsercios.app.cursos.clients.AlumnoFeignClient;
import com.formacionbdi.microsercios.app.cursos.clients.RespuestaFeignClient;
import com.formacionbdi.microsercios.app.cursos.models.entity.Curso;
import com.formacionbdi.microsercios.app.cursos.models.repository.CursoRepository;
import com.formacionbdi.microsercios.commons.alumnos.model.entity.Alumno;
import com.formacionbdi.microsercios.commons.services.CommonServiceImpl;

@Service
public class CursoServiceImpl extends CommonServiceImpl<Curso, CursoRepository> implements CursoService {
	
	@Autowired
	private RespuestaFeignClient client;
	
	@Autowired
	private AlumnoFeignClient clientAlumno;

	@Override
	@Transactional(readOnly=true)
	public Curso findCursoByAlumnoId(Long id) { 
		return repository.findCursoByAlumnoId(id);
	}

	@Override
	public Iterable<Long> obtenerExamenesIdsConRespuestasAlumno(Long alumnoId) {
		return client.obtenerExamenesIdsConRespuestasAlumno(alumnoId);
	}

	@Override
	public Iterable<Alumno> obtnerAlumnosPorCurso(List<Long> ids) {
		return clientAlumno.obtnerAlumnosPorCurso(ids);
	}
}
