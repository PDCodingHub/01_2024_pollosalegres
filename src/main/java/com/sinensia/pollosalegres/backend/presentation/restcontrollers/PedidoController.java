package com.sinensia.pollosalegres.backend.presentation.restcontrollers;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.sinensia.pollosalegres.backend.business.model.EstadoPedido;
import com.sinensia.pollosalegres.backend.business.model.Pedido;
import com.sinensia.pollosalegres.backend.business.services.PedidoServices;
import com.sinensia.pollosalegres.backend.presentation.config.PresentationException;

@RestController
@CrossOrigin
@RequestMapping("/pedidos")
public class PedidoController {

	private PedidoServices pedidoServices;
	
	public PedidoController(PedidoServices pedidoServices) {
		this.pedidoServices = pedidoServices;
	}
	
	@GetMapping
	public List<Pedido> getPedidos(){
		return pedidoServices.getAll();
	}
	
	@GetMapping("/{numero}")
	public Pedido read(@PathVariable Long numero) {
	
		Optional<Pedido> optional = pedidoServices.read(numero);
		
		if(optional.isEmpty()) {
			throw new PresentationException("No existe el pedido " + numero, HttpStatus.NOT_FOUND);	
		}
		
		return optional.get();
	}
	
	@PostMapping
	public ResponseEntity<Object> create(@RequestBody Pedido pedido, UriComponentsBuilder ucb) {
		
		Long nuevoNumero = null;
		
		try {
			nuevoNumero = pedidoServices.create(pedido);
		} catch(IllegalStateException e) {
			throw new PresentationException(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		URI uri = ucb.path("/pedidos/{numero}").build(nuevoNumero);
		
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping("/{numero}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@RequestBody Pedido pedido, @PathVariable Long numero) {
		
		pedido.setNumero(numero);
		
		try {
			pedidoServices.update(pedido);
		} catch(IllegalStateException e) {
			throw new PresentationException(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	@PatchMapping("/{numero}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateEstado(@RequestBody Map<String, String> attributes, @PathVariable Long numero) {
		
		try {
			String estado = (attributes.get("estado")).toUpperCase();
			EstadoPedido estadoPedio = EstadoPedido.valueOf(estado);
			switch(estadoPedio) {
				case CANCELADO: pedidoServices.cancelar(numero); break;
				case SERVIDO: pedidoServices.servir(numero); break;
				case EN_PROCESO: pedidoServices.procesar(numero); break;
				case PENDIENTE_ENTREGA: pedidoServices.entregar(numero); break;
				case NUEVO: break;
			}
		} catch(IllegalStateException | IllegalArgumentException e) {
			throw new PresentationException(e.getMessage(), HttpStatus.BAD_REQUEST);
		} 
	}
	
}
