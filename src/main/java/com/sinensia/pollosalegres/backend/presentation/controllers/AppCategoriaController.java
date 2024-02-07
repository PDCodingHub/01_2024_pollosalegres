package com.sinensia.pollosalegres.backend.presentation.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.sinensia.pollosalegres.backend.business.model.Categoria;
import com.sinensia.pollosalegres.backend.business.services.CategoriaServices;

@Controller
@RequestMapping("/app")
public class AppCategoriaController {

	private CategoriaServices categoriaServices;
	
	public AppCategoriaController(CategoriaServices categoriaServices) {
		this.categoriaServices = categoriaServices;
	}
	
	@GetMapping("/listado-categorias")
	public ModelAndView getListaCategorias(ModelAndView mav) {
		
		List<Categoria> categorias = categoriaServices.getAll();
		
		mav.setViewName("listado-categorias");
		mav.addObject("categorias", categorias);
		
		return mav;
	}
	
	// 1.- Necesitamos un end-poin para devolver el formulario (GET)
	// Vamos a provecharnos de las prestaciones de Spring Web para instanciar una nueva categoría y pasársela al formulario
		
	@GetMapping("/alta-categoria")
	public ModelAndView getFormularioAlta(ModelAndView mav) {
		mav.addObject("categoria", new Categoria());
		mav.setViewName("formulario-alta-categoria");
		return mav;
	}
	
	// 2.- Necesitamos un end-point para recoger la Categoria enviada por el
	//     formulario. Este end-point se encargará de la validación de datos!
	//     La respuesta será una redirección para que el cliente (navegador)
	//     solicite la página con el listado de categoria.
		
	@PostMapping("/alta-categoria")
	public RedirectView crearCategoria(@ModelAttribute Categoria categoria) {
			
		try {
			
			if(categoria.getNombre() == null || categoria.getNombre().equals("")) {
			// TODO
			}
			
			categoriaServices.create(categoria);
				
		} catch(Exception e) {
			// TODO
		}
			
		return new RedirectView("listado-categorias");
	}
	
}
