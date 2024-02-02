package com.sinensia.pollosalegres.backend.presentation.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinensia.pollosalegres.backend.business.model.Camarero;
import com.sinensia.pollosalegres.backend.business.model.Cliente;
import com.sinensia.pollosalegres.backend.business.model.Establecimiento;
import com.sinensia.pollosalegres.backend.business.model.EstadoPedido;
import com.sinensia.pollosalegres.backend.business.model.LineaPedido;
import com.sinensia.pollosalegres.backend.business.model.Pedido;
import com.sinensia.pollosalegres.backend.business.model.Producto;
import com.sinensia.pollosalegres.backend.business.services.PedidoServices;
import com.sinensia.pollosalegres.backend.presentation.controllers.PedidoController;

@WebMvcTest(value=PedidoController.class)
class PedidosControllerTest {
	
	@Autowired
	private MockMvc miniPostman;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private PedidoServices pedidoServices;
	
	private Pedido pedido1;
	private Pedido pedido2;
	
	@BeforeEach
	void init() {
		initObjects();
	}
	
	@Test
	void solicitamos_todos_los_pedidos() throws Exception {
		
		List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
		
		when(pedidoServices.getAll()).thenReturn(pedidos);
		
		MvcResult respuesta = miniPostman.perform(get("/pedidos"))
											.andExpect(status().isOk())
											.andReturn();
		
		String responseBody = respuesta.getResponse().getContentAsString(StandardCharsets.UTF_8);
		String responseBodyEsperada = objectMapper.writeValueAsString(pedidos);
				
		assertThat(responseBody).isEqualToIgnoringWhitespace(responseBodyEsperada);
	}
	
	
	// *************************************************
	//
	// Private Methods
	//
	// *************************************************
	
	private void initObjects() {
	
		pedido1 = new Pedido();
		pedido2 = new Pedido();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Date fecha1 = null;
		Date fecha2 = null;
		
		try {
			fecha1 = sdf.parse("14/04/2005");
			fecha2 = sdf.parse("24/11/1999");
		} catch (ParseException e) {
			
		}
		
		pedido1.setNumero(1L);
		pedido1.setFecha(fecha1);
		
		Camarero camarero1 = new Camarero();
		camarero1.setId(1L);
		pedido1.setCamarero(camarero1);
		
		Cliente cliente1 = new Cliente();
		cliente1.setId(3L);
		pedido1.setCliente(cliente1);
		
		Establecimiento establecimiento1 = new Establecimiento();
		establecimiento1.setCodigo(5L);
		pedido1.setEstablecimiento(establecimiento1);
		
		pedido1.setEstado(EstadoPedido.EN_PROCESO);
		
		LineaPedido linea1 = new LineaPedido();
		linea1.setCantidad(5);
		
		Producto producto1 = new Producto();
		producto1.setCodigo(1L);
		Producto producto2 = new Producto();
		producto1.setCodigo(2L);
		
		List<Producto> lista1 = new ArrayList<>();
		lista1.add(producto1);
		lista1.add(producto2);
		
		List<LineaPedido> listaPedido1 = new ArrayList<>();
		listaPedido1.add(linea1);
		
		pedido1.setLineas(listaPedido1);
		
		//*****
		
		pedido2.setNumero(2L);
		pedido2.setFecha(fecha2);
		
		Camarero camarero2 = new Camarero();
		camarero2.setId(2L);
		pedido2.setCamarero(camarero2);
		
		Cliente cliente2 = new Cliente();
		cliente2.setId(4L);
		pedido2.setCliente(cliente2);
		
		Establecimiento establecimiento2 = new Establecimiento();
		establecimiento2.setCodigo(6L);
		pedido2.setEstablecimiento(establecimiento2);

		pedido2.setEstado(EstadoPedido.PENDIENTE_ENTREGA);
		
		LineaPedido linea2 = new LineaPedido();
		linea2.setCantidad(6);
		
		Producto producto3 = new Producto();
		producto3.setCodigo(3L);
		Producto producto4 = new Producto();
		producto4.setCodigo(4L);
		
		List<Producto> lista2 = new ArrayList<>();
		lista2.add(producto3);
		lista2.add(producto4);
		
		List<LineaPedido> listaPedido2 = new ArrayList<>();
		listaPedido2.add(linea2);
		
		pedido2.setLineas(listaPedido2);
	}
}
	

