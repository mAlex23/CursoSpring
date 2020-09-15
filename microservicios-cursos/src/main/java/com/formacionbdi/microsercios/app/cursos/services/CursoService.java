package com.formacionbdi.microsercios.app.cursos.services;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.formacionbdi.microsercios.app.cursos.models.entity.Curso;
import com.formacionbdi.microsercios.commons.alumnos.model.entity.Alumno;
import com.formacionbdi.microsercios.commons.services.CommonService;

public interface CursoService extends CommonService<Curso> {
	
	public Curso findCursoByAlumnoId(Long id);
	
	public Iterable<Long> obtenerExamenesIdsConRespuestasAlumno(Long alumnoId);
	
	public Iterable<Alumno> obtnerAlumnosPorCurso(@RequestBody List<Long> ids);
}
