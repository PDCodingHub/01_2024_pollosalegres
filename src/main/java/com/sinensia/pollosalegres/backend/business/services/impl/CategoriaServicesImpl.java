package com.sinensia.pollosalegres.backend.business.services.impl;

import java.util.List;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import com.sinensia.pollosalegres.backend.business.model.Categoria;
import com.sinensia.pollosalegres.backend.business.services.CategoriaServices;
import com.sinensia.pollosalegres.backend.integration.model.CategoriaPL;
import com.sinensia.pollosalegres.backend.integration.repositories.CategoriaPLRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoriaServicesImpl extends AbstractServices implements CategoriaServices {

	private CategoriaPLRepository categoriaPLRepository;

	public CategoriaServicesImpl(CategoriaPLRepository categoriaRepository, DozerBeanMapper mapper) {
		super(mapper);
		this.categoriaPLRepository = categoriaRepository;
	}

	@Transactional
	@Override
	public Long create(Categoria categoria) {

		if (categoria.getId() != null) {
			throw new IllegalStateException("Para crear una categoria el id ha de ser null");
		}

		CategoriaPL categoriaPL = mapper.map(categoria, CategoriaPL.class);

		return categoriaPLRepository.save(categoriaPL).getId();
	}

	@Override
	public Optional<Categoria> read(Long id) {

		Optional<CategoriaPL> optional = categoriaPLRepository.findById(id);

		Categoria categoria = null;

		if (optional.isPresent()) {
			categoria = mapper.map(optional.get(), Categoria.class);
		}

		return Optional.ofNullable(categoria);
	}

	@Override
	public List<Categoria> getAll() {
		return convertList(categoriaPLRepository.findAll(), Categoria.class);
	}

}
