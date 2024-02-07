package com.sinensia.pollosalegres.backend.presentation.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ModelAndView getListaPedidos(ModelAndView mav) {
		
		List<Pedido> pedidos = pedidoServices.getAll();
		
		mav.setViewName("listado-pedidos");
		mav.addObject("pedidos", pedidos);
		
		return mav;
	}
	
	@GetMapping("/ficha-pedido")
	public ModelAndView getFichaPedido(ModelAndView mav, @RequestParam Long numero) {
		
		Optional<Pedido> optional = pedidoServices.read(numero);
		Pedido pedido = optional.get();
		
		mav.setViewName("ficha-pedido");
		mav.addObject("pedido", pedido);
		
		return mav;
	}
}
