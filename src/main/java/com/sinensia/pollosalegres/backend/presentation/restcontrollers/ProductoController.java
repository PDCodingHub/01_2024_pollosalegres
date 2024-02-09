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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sinensia.pollosalegres.backend.business.model.Producto;
import com.sinensia.pollosalegres.backend.business.services.ProductoServices;
import com.sinensia.pollosalegres.backend.presentation.config.PresentationException;

@RestController
@CrossOrigin
@RequestMapping("/productos")
public class ProductoController {

	private ProductoServices productoServices;
	
	public ProductoController(ProductoServices productoServices) {
		this.productoServices = productoServices;
	}
	
	@GetMapping
	public List<Producto> getProductos(){
		return productoServices.getAll();
	}
	
	@GetMapping("/{codigo}")
	public Producto read(@PathVariable Long codigo) {
	
		Optional<Producto> optional = productoServices.read(codigo);
		
		if(optional.isEmpty()) {
			throw new PresentationException("No existe el producto " + codigo, HttpStatus.NOT_FOUND);	
		}
		
		return optional.get();
	}
	
	@PostMapping
	public ResponseEntity<Object> create(@RequestBody Producto producto, UriComponentsBuilder ucb) {
		
		Long nuevoCodigo = null;
		
		try {
			nuevoCodigo = productoServices.create(producto);
		} catch(IllegalStateException e) {
			throw new PresentationException(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		URI uri = ucb.path("/productos/{codigo}").build(nuevoCodigo);
		
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@RequestBody Producto producto, @PathVariable Long codigo) {
		
		producto.setCodigo(codigo);
		
		try {
			productoServices.update(producto);
		} catch(IllegalStateException e) {
			throw new PresentationException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

}
