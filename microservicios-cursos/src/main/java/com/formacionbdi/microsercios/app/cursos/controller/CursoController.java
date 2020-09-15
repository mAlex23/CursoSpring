package com.formacionbdi.microsercios.app.cursos.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.microsercios.app.cursos.models.entity.Curso;
import com.formacionbdi.microsercios.app.cursos.models.entity.CursoAlumno;
import com.formacionbdi.microsercios.app.cursos.services.CursoService;
import com.formacionbdi.microsercios.commons.alumnos.model.entity.Alumno;
import com.formacionbdi.microsercios.commons.controllers.CommonController;
import com.formacionbdi.microsercios.commons.examenes.models.entity.Examen;

@RestController
public class CursoController extends CommonController<Curso, CursoService> {
	
	@Value("${config.balanceador.test}")
	private String balanceadorTest;
	
	@Override
	@GetMapping
	public ResponseEntity<?> listar(){
		List<Curso> cursos = ((List<Curso>) service.findAll()).stream().map( c -> {
			c.getCursoAlumnos().forEach(ca -> {
				Alumno alumno = new Alumno();
				alumno.setId(ca.getAlumnoId());
				c.addAlumno(alumno);
			});
			return c;
		}).collect(Collectors.toList());
		return ResponseEntity.ok().body(cursos);
	}
	
	@GetMapping("/pagina")
	@Override
	public ResponseEntity<?> listar(Pageable pageable){
		Page<Curso> cursos = service.findAll(pageable).map( curso -> {
			curso.getCursoAlumnos().forEach(ca -> {
				Alumno alumno = new Alumno();
				alumno.setId(ca.getId());
				curso.addAlumno(alumno);
			});
			return curso;
		});
		return ResponseEntity.ok().body(cursos);
	}
	
	@Override
	@GetMapping("/{id}")
	public ResponseEntity<?> ver(@PathVariable Long id){
		Optional<Curso> o = service.findById(id);
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Curso curso = o.get();
		if(curso.getCursoAlumnos().isEmpty() == false) {
			List<Long> ids = curso.getCursoAlumnos().stream().map( ca ->  ca.getAlumnoId())
					.collect(Collectors.toList());
			
			List<Alumno> alumnos = (List<Alumno>) service.obtnerAlumnosPorCurso(ids);
			
			curso.setAlumnos(alumnos);
		}
		return ResponseEntity.ok().body(curso);
	}
	
	@GetMapping("/balanceador-test")
	public ResponseEntity<?> balanceadorTest() {

		Map<String,Object> response = new HashMap<String,Object>();
		response.put("balanceador", balanceadorTest);
		response.put("cursos", service.findAll());
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id){
		
		if(result.hasErrors()) {
			return this.validar(result);
		}
		
		Optional<Curso> o = this.service.findById(id);
		
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Curso cursoDb = o.get();
		cursoDb.setNombre(curso.getNombre());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDb));
	}
	
	@PutMapping("/{id}/asignar-alumnos")
	public ResponseEntity<?> asignarAlumnos(@RequestBody List<Alumno> alumnos,@PathVariable Long id){
		
		Optional<Curso> o = this.service.findById(id);
		
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Curso cursoDb = o.get();
		
		alumnos.forEach(a -> {
			CursoAlumno cursoAlumno = new CursoAlumno();
			cursoAlumno.setAlumnoId(a.getId());
			cursoAlumno.setCurso(cursoDb);
			cursoDb.addCursoAlumno(cursoAlumno);
		});
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDb));
	}
	
	@PutMapping("/{id}/eliminar-alumno")
	public ResponseEntity<?> eliminarAlumno(@RequestBody Alumno alumno,@PathVariable Long id){
		
		Optional<Curso> o = this.service.findById(id);
		
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Curso cursoDb = o.get();
		CursoAlumno cursoAlumno = new CursoAlumno();
		cursoAlumno.setAlumnoId(alumno.getId());
		cursoDb.removeCursoAlumno(cursoAlumno);
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDb));
	}
	
	@GetMapping("/alumno/{id}")
	public ResponseEntity<?> buscarPorAlumnoId(@PathVariable Long id){
		Curso curso = service.findCursoByAlumnoId(id);
		if(curso != null) {
			List<Long> examenesIds = (List<Long>) service.obtenerExamenesIdsConRespuestasAlumno(id);
			
			List<Examen> examenes = curso.getExamenes().stream().map(examen -> {
				if(examenesIds.contains(examen.getId())) {
					examen.setRespondido(true);
				}
				return examen;
			}).collect(Collectors.toList());
			curso.setExamenes(examenes);
		}
		
		return ResponseEntity.ok(curso);
	}
	
	@PutMapping("/{id}/asignar-examenes")
	public ResponseEntity<?> asignarExamenes(@RequestBody List<Examen> examenes,@PathVariable Long id){
		
		Optional<Curso> o = this.service.findById(id);
		
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Curso cursoDb = o.get();
		
		examenes.forEach(e -> {
			cursoDb.addExamen(e);
		});
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDb));
	}
	
	@PutMapping("/{id}/eliminar-examen")
	public ResponseEntity<?> eliminarExamen(@RequestBody Examen examen,@PathVariable Long id){
		
		Optional<Curso> o = this.service.findById(id);
		
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		Curso cursoDb = o.get();
		
		cursoDb.removeExamen(examen);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(cursoDb));
	}

}
