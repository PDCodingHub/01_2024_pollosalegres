package com.sinensia.pollosalegres.backend.presentation.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sinensia.pollosalegres.backend.business.model.Pedido;
import com.sinensia.pollosalegres.backend.business.services.PedidoServices;

@Controller
@RequestMapping("/app")
public class AppPedidoController {

	private PedidoServices pedidoServices;
	
	public AppPedidoController(PedidoServices pedidoServices) {
		this.pedidoServices = pedidoServices;
	}
	
	@GetMapping("/listado-pedidos")
	public ModelAndView getListaCamareros(ModelAndView mav) {
		
		List<Pedido> pedidos = pedidoServices.getAll();
		
		mav.setViewName("listado-pedidos");
		mav.addObject("pedidos", pedidos);
		
		return mav;
	}
}
