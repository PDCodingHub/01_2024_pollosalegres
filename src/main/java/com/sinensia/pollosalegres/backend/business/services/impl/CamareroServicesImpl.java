package com.sinensia.pollosalegres.backend.business.services.impl;

import java.util.List;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinensia.pollosalegres.backend.business.model.Camarero;
import com.sinensia.pollosalegres.backend.business.model.dtos.CamareroDTO1;
import com.sinensia.pollosalegres.backend.business.services.CamareroServices;
import com.sinensia.pollosalegres.backend.integration.model.CamareroPL;
import com.sinensia.pollosalegres.backend.integration.repositories.CamareroPLRepository;

@Service
public class CamareroServicesImpl extends AbstractServices implements CamareroServices {

	private CamareroPLRepository camareroPLRepository;

	public CamareroServicesImpl(CamareroPLRepository camareroPLRepository, DozerBeanMapper mapper) {
		super(mapper);
		this.camareroPLRepository = camareroPLRepository;
	}

	@Transactional
	@Override
	public Long create(Camarero camarero) {

		if (camarero.getId() != null) {
			throw new IllegalStateException("Para crear un camarero el id ha de ser null");
		}

		boolean existeDni = camareroPLRepository.existsByDni(camarero.getDni());

		if (existeDni) {
			throw new IllegalStateException("Ya existe un camarero con el mismo dni");
		}

		CamareroPL camareroPL = mapper.map(camarero, CamareroPL.class);

		return camareroPLRepository.save(camareroPL).getId();
	}

	@Override
	public Optional<Camarero> read(Long id) {

		Optional<CamareroPL> optional = camareroPLRepository.findById(id);

		Camarero camarero = null;

		if (optional.isPresent()) {
			camarero = mapper.map(optional.get(), Camarero.class);
		}

		return Optional.ofNullable(camarero);
	}

	@Override
	public Optional<Camarero> read(String dni) {

		Optional<CamareroPL> optional = camareroPLRepository.findByDni(dni);

		Camarero camarero = null;

		if (optional.isPresent()) {
			camarero = mapper.map(optional.get(), Camarero.class);
		}

		return Optional.ofNullable(camarero);
	}

	@Transactional
	@Override
	public void update(Camarero camarero) {

		Long id = camarero.getId();

		if (id == null) {
			throw new IllegalStateException("No se puede actualizar un camarero con id null");
		}

		boolean existe = camareroPLRepository.existsById(id);

		if (!existe) {
			throw new IllegalStateException("El camarero con id " + id + " no existe. No se puede actualizar");
		}

		camareroPLRepository.save(mapper.map(camarero, CamareroPL.class));
	}

	@Transactional
	@Override
	public void delete(Long id) {
		camareroPLRepository.deleteById(id);
	}

	@Override
	public List<Camarero> getAll() {
		return convertList(camareroPLRepository.findAll(), Camarero.class);
	}

	@Override
	public List<Camarero> getByNombreLikeIgnoreCase(String texto) {
		return convertList(camareroPLRepository.findByNombreLikeIgnoreCase(texto), Camarero.class);
	}

	@Override
	public int getNumeroTotalCamareros() {
		return (int) camareroPLRepository.count();
	}

	@Override
	public List<CamareroDTO1> getAllCamareroDTO1() {
		return convertList(camareroPLRepository.findAllDTO1(), CamareroDTO1.class);
	}

}
