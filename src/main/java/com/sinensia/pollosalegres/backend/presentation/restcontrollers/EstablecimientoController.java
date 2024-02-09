package com.sinensia.pollosalegres.backend.presentation.restcontrollers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sinensia.pollosalegres.backend.business.model.Establecimiento;
import com.sinensia.pollosalegres.backend.business.services.EstablecimientoServices;
import com.sinensia.pollosalegres.backend.presentation.config.PresentationException;

@RestController
@CrossOrigin
@RequestMapping("/establecimientos")
public class EstablecimientoController {
	
	private EstablecimientoServices establecimientoServices;
	
	public EstablecimientoController(EstablecimientoServices establecimientoServices) {
		this.establecimientoServices = establecimientoServices;
	}
	
	@GetMapping
	public List<Establecimiento> getAll(){
		
		return establecimientoServices.getAll();
	}
	
	@GetMapping("/{codigo}")
	public Establecimiento read(@PathVariable Long codigo) {
		
		Optional<Establecimiento> optional = establecimientoServices.read(codigo);
		
		if(optional.isEmpty()) {
			throw new PresentationException("No existe el establecimiento " + codigo, HttpStatus.NOT_FOUND);	
		}
		
		return optional.get();
	}
	
	@PostMapping
	public ResponseEntity<Object> create(@RequestBody Establecimiento establecimiento, UriComponentsBuilder ucb) {
		
		Long nuevoCodigo = null;
		
		try {
			nuevoCodigo = establecimientoServices.create(establecimiento);
		} catch(IllegalStateException e) {
			throw new PresentationException(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		URI uri = ucb.path("/establecimientos/{codigo}").build(nuevoCodigo);
		
		return ResponseEntity.created(uri).build();
	}

}
