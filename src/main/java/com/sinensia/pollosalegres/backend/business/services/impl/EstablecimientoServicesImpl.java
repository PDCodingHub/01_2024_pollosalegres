package com.sinensia.pollosalegres.backend.business.services.impl;

import java.util.List;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinensia.pollosalegres.backend.business.model.Establecimiento;
import com.sinensia.pollosalegres.backend.business.services.EstablecimientoServices;
import com.sinensia.pollosalegres.backend.integration.model.EstablecimientoPL;
import com.sinensia.pollosalegres.backend.integration.repositories.EstablecimientoPLRepository;

@Service
public class EstablecimientoServicesImpl extends AbstractServices implements EstablecimientoServices {

	private EstablecimientoPLRepository establecimientoPLRepository;

	public EstablecimientoServicesImpl(EstablecimientoPLRepository establecimientoPLRepository, DozerBeanMapper mapper) {
		super(mapper);
		this.establecimientoPLRepository = establecimientoPLRepository;
	}

	@Transactional
	@Override
	public Long create(Establecimiento establecimiento) {

		if (establecimiento.getCodigo() != null) {
			throw new IllegalStateException("Para crear un establecimiento el codigo ha de ser null");
		}

		EstablecimientoPL establecimientoPL = mapper.map(establecimiento, EstablecimientoPL.class);

		EstablecimientoPL createdEstablecimientoPL = establecimientoPLRepository.save(establecimientoPL);

		return createdEstablecimientoPL.getCodigo();
	}

	@Override
	public Optional<Establecimiento> read(Long codigo) {

		Optional<EstablecimientoPL> optional = establecimientoPLRepository.findById(codigo);

		Establecimiento establecimiento = null;

		if (optional.isPresent()) {
			establecimiento = mapper.map(optional.get(), Establecimiento.class);
		}

		return Optional.ofNullable(establecimiento);
	}

	@Override
	public List<Establecimiento> getAll() {
		return convertList(establecimientoPLRepository.findAll(), Establecimiento.class);
	}

}
