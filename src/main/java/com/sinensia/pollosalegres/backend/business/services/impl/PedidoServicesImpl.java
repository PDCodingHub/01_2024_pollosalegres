package com.sinensia.pollosalegres.backend.business.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import com.sinensia.pollosalegres.backend.business.model.EstadoPedido;
import com.sinensia.pollosalegres.backend.business.model.Pedido;
import com.sinensia.pollosalegres.backend.business.services.PedidoServices;
import com.sinensia.pollosalegres.backend.integration.model.EstadoPedidoPL;
import com.sinensia.pollosalegres.backend.integration.model.PedidoPL;
import com.sinensia.pollosalegres.backend.integration.repositories.PedidoPLRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoServicesImpl implements PedidoServices {

	private PedidoPLRepository pedidoPLRepository;
	private DozerBeanMapper mapper;

	public PedidoServicesImpl(PedidoPLRepository pedidoPLRepository, DozerBeanMapper mapper) {
		this.pedidoPLRepository = pedidoPLRepository;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public Long create(Pedido pedido) {

		if (pedido.getNumero() != null) {
			throw new IllegalStateException("Para crear un pedido el número ha de ser null");
		}

		PedidoPL pedidoPL = mapper.map(pedido, PedidoPL.class);
		pedidoPL.setNumero(System.currentTimeMillis());

		return pedidoPLRepository.save(pedidoPL).getNumero();
	}

	@Override
	public Optional<Pedido> read(Long numero) {

		Optional<PedidoPL> optional = pedidoPLRepository.findById(numero);

		Pedido pedido = null;

		if (optional.isPresent()) {
			pedido = mapper.map(optional.get(), Pedido.class);
		}

		return Optional.ofNullable(pedido);
	}

	@Transactional
	@Override
	public void update(Long numerPedido, Map<String, Object> atributos) {

	}

	@Transactional
	@Override
	public void update(Pedido pedido) {

		Long numero = pedido.getNumero();

		if (numero == null) {
			throw new IllegalStateException("No se puede actualizar un pedido con número null");
		}

		boolean existe = pedidoPLRepository.existsById(numero);

		if (!existe) {
			throw new IllegalStateException("El número del pedido no existe");
		}

		pedidoPLRepository.save(mapper.map(pedido, PedidoPL.class));
	}

	@Override
	public List<Pedido> getAll() {
		
		return pedidoPLRepository.findAll().stream()
				.map(x -> mapper.map(x, Pedido.class))
				.toList();
	}

	@Transactional
	@Override
	public void procesar(Long numero) {
		
		Optional<PedidoPL> optionalPL = pedidoPLRepository.findById(numero);
		
		if (optionalPL.isPresent()) {
			optionalPL.get().setEstado(EstadoPedidoPL.EN_PROCESO);
		}
	}

	@Override
	public void entregar(Long numero) {
		
		Optional<PedidoPL> optionalPL = pedidoPLRepository.findById(numero);
		
		if (optionalPL.isPresent()) {
			optionalPL.get().setEstado(EstadoPedidoPL.PENDIENTE_ENTREGA);
		}
	}

	@Override
	public void servir(Long numero) {

		Optional<PedidoPL> optionalPL = pedidoPLRepository.findById(numero);
		
		if (optionalPL.isPresent()) {
			optionalPL.get().setEstado(EstadoPedidoPL.SERVIDO);
		}
	}

	@Transactional
	@Override
	public void cancelar(Long numero) {
		
		Optional<PedidoPL> optionalPL = pedidoPLRepository.findById(numero);
		
		if (optionalPL.isPresent()) {
			optionalPL.get().setEstado(EstadoPedidoPL.CANCELADO);
		}
	}

}
