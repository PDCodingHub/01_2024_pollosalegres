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
public class PedidoServicesImpl extends AbstractServices implements PedidoServices {

	private PedidoPLRepository pedidoPLRepository;

	public PedidoServicesImpl(PedidoPLRepository pedidoPLRepository, DozerBeanMapper mapper) {
		super(mapper);
		this.pedidoPLRepository = pedidoPLRepository;
	}

	@Transactional
	@Override
	public Long create(Pedido pedido) {

		if (pedido.getNumero() != null) {
			throw new IllegalStateException("Para crear un pedido el codigo ha de ser null");
		}

		pedido.setEstado(EstadoPedido.NUEVO);

		PedidoPL pedidoPL = mapper.map(pedido, PedidoPL.class);

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

	@Override
	public void update(Long numeroPedido, Map<String, Object> atributos) {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void update(Pedido pedido) {
		Long numero = pedido.getNumero();

		if (numero == null) {
			throw new IllegalStateException("No se puede actualizar un pedido con codigo null");
		}

		boolean existe = pedidoPLRepository.existsById(numero);

		if (!existe) {
			throw new IllegalStateException("El pedido con numero " + numero + " no existe. No se puede actualizar");
		}

		pedidoPLRepository.save(mapper.map(pedido, PedidoPL.class));
	}

	@Override
	public List<Pedido> getAll() {
		return convertList(pedidoPLRepository.findAll(), Pedido.class);
	}

	@Transactional
	@Override
	public void procesar(Long numero) {

		Optional<EstadoPedidoPL> optional = pedidoPLRepository.getEstadoByCodigo(numero);

		if (optional.isEmpty()) {
			throw new IllegalArgumentException("No existe el pedido numero " + numero);
		}

		if (!optional.get().equals(EstadoPedidoPL.NUEVO)) {
			throw new IllegalStateException(
					"No se puede pasar a estado EN_PROCESO un pedido en estado " + optional.get());
		}

		pedidoPLRepository.updateEstado(EstadoPedidoPL.EN_PROCESO, numero);

	}

	@Transactional
	@Override
	public void entregar(Long numero) {

		Optional<EstadoPedidoPL> optional = pedidoPLRepository.getEstadoByCodigo(numero);

		if (optional.isEmpty()) {
			throw new IllegalArgumentException("No existe el pedido numero " + numero);
		}

		if (!optional.get().equals(EstadoPedidoPL.EN_PROCESO)) {
			throw new IllegalStateException(
					"No se puede pasar a estado PENDIENTE_ENTREGA un pedido en estado " + optional.get());
		}

		pedidoPLRepository.updateEstado(EstadoPedidoPL.PENDIENTE_ENTREGA, numero);

	}

	@Transactional
	@Override
	public void servir(Long numero) {

		Optional<EstadoPedidoPL> optional = pedidoPLRepository.getEstadoByCodigo(numero);

		if (optional.isEmpty()) {
			throw new IllegalArgumentException("No existe el pedido numero " + numero);
		}

		if (!optional.get().equals(EstadoPedidoPL.PENDIENTE_ENTREGA)) {
			throw new IllegalStateException("No se puede pasar a estado SERVIDO un pedido en estado " + optional.get());
		}

		pedidoPLRepository.updateEstado(EstadoPedidoPL.SERVIDO, numero);

	}

	@Transactional
	@Override
	public void cancelar(Long numero) {

		Optional<EstadoPedidoPL> optional = pedidoPLRepository.getEstadoByCodigo(numero);

		if (optional.isEmpty()) {
			throw new IllegalArgumentException("No existe el pedido numero " + numero);
		}

		if (optional.get().equals(EstadoPedidoPL.SERVIDO) || optional.get().equals(EstadoPedidoPL.CANCELADO)) {
			throw new IllegalStateException(
					"No se puede pasar a estado CANCELADO un pedido en estado " + optional.get());
		}

		pedidoPLRepository.updateEstado(EstadoPedidoPL.SERVIDO, numero);

	}

}
