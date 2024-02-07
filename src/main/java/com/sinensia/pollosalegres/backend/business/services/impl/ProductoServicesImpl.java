package com.sinensia.pollosalegres.backend.business.services.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinensia.pollosalegres.backend.business.model.Categoria;
import com.sinensia.pollosalegres.backend.business.model.Producto;
import com.sinensia.pollosalegres.backend.business.model.dtos.EstadisticaDTO1;
import com.sinensia.pollosalegres.backend.business.model.dtos.EstadisticaDTO2;
import com.sinensia.pollosalegres.backend.business.services.ProductoServices;
import com.sinensia.pollosalegres.backend.integration.model.CategoriaPL;
import com.sinensia.pollosalegres.backend.integration.model.ProductoPL;
import com.sinensia.pollosalegres.backend.integration.repositories.ProductoPLRepository;

@Service
public class ProductoServicesImpl extends AbstractServices implements ProductoServices {

	private ProductoPLRepository productoPLRepository;

	public ProductoServicesImpl(ProductoPLRepository productoPLRepository, DozerBeanMapper mapper) {
		super(mapper);
		this.productoPLRepository = productoPLRepository;
	}

	@Transactional
	@Override
	public Long create(Producto producto) {

		if (producto.getCodigo() != null) {
			throw new IllegalStateException("Para crear un producto el codigo ha de ser null");
		}

		ProductoPL productoPL = mapper.map(producto, ProductoPL.class);

		return productoPLRepository.save(productoPL).getCodigo();
	}

	@Override
	public Optional<Producto> read(Long codigo) {

		Optional<ProductoPL> optional = productoPLRepository.findById(codigo);

		Producto producto = null;

		if (optional.isPresent()) {
			producto = mapper.map(optional.get(), Producto.class);
		}

		return Optional.ofNullable(producto);
	}

	@Transactional
	@Override
	public void update(Producto producto) {

		Long codigo = producto.getCodigo();

		if (codigo == null) {
			throw new IllegalStateException("No se puede actualizar un producto con codigo null");
		}

		boolean existe = productoPLRepository.existsById(codigo);

		if (!existe) {
			throw new IllegalStateException("El producto con codigo " + codigo + " no existe. No se puede actualizar");
		}

		productoPLRepository.save(mapper.map(producto, ProductoPL.class));

	}

	@Override
	public List<Producto> getAll() {
		return convertProductoPLList(productoPLRepository.findAll());
	}

	@Override
	public List<Producto> getBetweenPriceRange(double min, double max) {
		return convertProductoPLList(productoPLRepository.findByPrecioBetween(min, max));
	}

	@Override
	public List<Producto> getBetweenDates(Date desde, Date hasta) {
		return convertProductoPLList(productoPLRepository.findByFechaAltaBetween(desde, hasta));
	}

	@Override
	public List<Producto> getDescatalogados() {
		return convertProductoPLList(productoPLRepository.findByDescatalogadoTrue());
	}

	@Override
	public List<Producto> getByCategoria(Categoria categoria) {
		return convertProductoPLList(productoPLRepository.findByCategoriaId(categoria.getId()));
	}

	@Override
	public int getNumeroTotalProductos() {
		return (int) productoPLRepository.count();
	}

	@Override
	@Transactional
	public void variarPrecio(List<Producto> productos, double porcentaje) {
		List<ProductoPL> productosPL = productos.stream().map(x -> mapper.map(x, ProductoPL.class)).toList();

		productoPLRepository.variarPrecio(productosPL, porcentaje);
	}

	@Override
	@Transactional
	public void variarPrecio(long[] codigos, double porcentaje) {
		productoPLRepository.variarPrecio(codigos, porcentaje);
	}

	@Override
	public Map<Categoria, Integer> getEstadisticaNumeroProductoPorCategoria() {

		// TODO Deben aparecer las categorias que no tienen ningun producto

		Map<Categoria, Integer> mapaResultados = new HashMap<>();

		productoPLRepository.getEstadisticaNumeroProductoPorCategoria().stream().forEach(x -> {
			Categoria categoria = mapper.map((CategoriaPL) x[0], Categoria.class);
			Integer cantidad = ((Long) x[1]).intValue();
			mapaResultados.put(categoria, cantidad);
		});

		return mapaResultados;
	}

	@Override
	public Map<Categoria, Double> getEstadisticaPrecioMedioProductosPorCategoria() {

		// TODO Deben aparecer las categorias que no tienen ningun producto

		Map<Categoria, Double> mapaResultados = new HashMap<>();

		productoPLRepository.getEstadisticaPrecioMedioProductoPorCategoria().stream().forEach(x -> {
			Categoria categoria = mapper.map((CategoriaPL) x[0], Categoria.class);
			Double precioMedio = (Double) x[1];
			mapaResultados.put(categoria, precioMedio);
		});

		return mapaResultados;
	}

	@Override
	public List<EstadisticaDTO1> getEstadisticasDTO1() {

		// TODO Deben aparecer las categorias que no tienen ningun producto

		return productoPLRepository.getEstadisticaNumeroProductoPorCategoria().stream().map(x -> {
			CategoriaPL categoriaPL = (CategoriaPL) x[0];
			int cantidad = ((Long) x[1]).intValue();
			return new EstadisticaDTO1(categoriaPL.getId(), categoriaPL.getName(), cantidad);
		}).toList();
	}

	@Override
	public List<EstadisticaDTO2> getEstadisticasDTO2() {

		// TODO Deben aparecer las categorias que no tienen ningun producto

		return productoPLRepository.getEstadisticaNumeroProductoPorCategoria().stream().map(x -> {
			Categoria categoria = mapper.map((CategoriaPL) x[0], Categoria.class);
			int cantidad = ((Long) x[1]).intValue();
			return new EstadisticaDTO2(categoria, cantidad);
		}).toList();
	}

	// *************************************************
	//
	// Private Methods
	//
	// *************************************************

	private List<Producto> convertProductoPLList(List<ProductoPL> productosPL) {
		return convertList(productosPL, Producto.class);
	}
}
