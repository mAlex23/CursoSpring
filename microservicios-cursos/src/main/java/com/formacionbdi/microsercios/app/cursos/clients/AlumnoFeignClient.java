package com.formacionbdi.microsercios.app.cursos.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.formacionbdi.microsercios.commons.alumnos.model.entity.Alumno;

@FeignClient(name="microservicio-usuarios")
public interface AlumnoFeignClient {
	
	@GetMapping("/alumnos-por-curso")
	public Iterable<Alumno> obtnerAlumnosPorCurso(@RequestBody List<Long> ids);
}
