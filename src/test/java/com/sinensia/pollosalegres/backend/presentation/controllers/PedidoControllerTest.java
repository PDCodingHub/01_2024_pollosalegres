package com.sinensia.pollosalegres.backend.presentation.controllers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

import com.sinensia.pollosalegres.backend.business.model.Camarero;
import com.sinensia.pollosalegres.backend.business.model.Cliente;
import com.sinensia.pollosalegres.backend.business.model.DatosContacto;
import com.sinensia.pollosalegres.backend.business.model.Direccion;
import com.sinensia.pollosalegres.backend.business.model.Establecimiento;
import com.sinensia.pollosalegres.backend.business.model.EstadoPedido;
import com.sinensia.pollosalegres.backend.business.model.LineaPedido;
import com.sinensia.pollosalegres.backend.business.model.Pedido;
import com.sinensia.pollosalegres.backend.business.model.Producto;
import com.sinensia.pollosalegres.backend.business.services.PedidoServices;
import com.sinensia.pollosalegres.backend.presentation.config.RespuestaError;
import com.sinensia.pollosalegres.backend.presentation.restcontrollers.PedidoController;

@WebMvcTest(value = PedidoController.class)
class PedidoControllerTest extends AbstractControllerTest{

	@MockBean
	private PedidoServices pedidoServices;

	private Pedido pedido1;
	private Pedido pedido2;
	private Pedido pedido3;

	@BeforeEach
	void init() {
		initObjects();
	}

	@Test
	void solicitamos_todos_los_pedidos() throws Exception {
		
		List<Pedido> pedidos = Arrays.asList(pedido1, pedido2, pedido3);

		when(pedidoServices.getAll()).thenReturn(pedidos);

		MvcResult respuesta = mockMvc.perform(get("/pedidos"))
				 						 .andExpect(status().isOk())
				 						 .andReturn();

		checkResponseBody(pedidos, respuesta);
	}
	
	@Test
	void solicitamos_pedido_EXISTENTE_a_partir_de_su_numero() throws Exception {
		
		when(pedidoServices.read(1011L)).thenReturn(Optional.of(pedido1));
		
		MvcResult respuesta = mockMvc.perform(get("/pedidos/1012"))
											.andExpect(status().isOk())
											.andReturn();
		
		checkResponseBody(pedido1, respuesta);
	}
	
	@Test
	void solicitamos_pedido_NO_EXISTINTE_a_partir_de_su_numero() throws Exception{
		
		when(pedidoServices.read(101L)).thenReturn(Optional.empty());
		
		MvcResult respuesta = mockMvc.perform(get("/pedidos/101"))
										 .andExpect(status().isNotFound())
										 .andReturn();
		
		checkResponseBody(new RespuestaError("No existe el pedido 101"), respuesta);
	}
	
	@Test
	void creamos_pedido_ok() throws Exception {
		
		pedido1.setNumero(null);
		
		when(pedidoServices.create(pedido1)).thenReturn(10L);
		
		String requestBody = objectMapper.writeValueAsString(pedido1);
		
		mockMvc.perform(post("/pedidos").content(requestBody).contentType("application/json"))
				   .andExpect(status().isCreated())
				   .andExpect(header().string("Location","http://localhost/pedidos/10"));					
	}
	
	
	@Test
	void creamos_pedido_con_numero_NO_null() throws Exception {
		
		when(pedidoServices.create(pedido1)).thenThrow(new IllegalStateException("**** EL MENSAJE ****"));
		
		String requestBody = objectMapper.writeValueAsString(pedido1);
		
		MvcResult respuesta = mockMvc.perform(post("/pedidos").content(requestBody).contentType("application/json"))
											.andExpect(status().isBadRequest())
											.andReturn();
		
		checkResponseBody(new RespuestaError("**** EL MENSAJE ****"), respuesta);
	}
	
	@Test
	void actualizamos_pedido_ok() throws Exception {
		
		String requestBody = objectMapper.writeValueAsString(pedido1);
		
		mockMvc.perform(put("/pedidos/10").content(requestBody).contentType("application/json"))
				   .andExpect(status().isNoContent());
		
		verify(pedidoServices, times(1)).update(pedido1);
	}
	
	@Test
	void actualizamos_pedido_con_numero_null() throws Exception {
		
		doThrow(new IllegalStateException("EL MENSAJE")).when(pedidoServices).update(pedido1);
		
		String requestBody = objectMapper.writeValueAsString(pedido1);
		
		MvcResult respuesta = mockMvc.perform(put("/pedidos/10").content(requestBody).contentType("application/json"))
				 							.andExpect(status().isNotFound())
				 							.andReturn();

		verify(pedidoServices, times(1)).update(pedido1);

		checkResponseBody(new RespuestaError("EL MENSAJE"), respuesta);
	}
		
	// *************************************************
	//
	// Private Methods
	//
	// *************************************************
	
	private void initObjects() {
		
		Camarero camarero1 = new Camarero();
		Camarero camarero2 = new Camarero();
		Cliente cliente1 = new Cliente();

		Direccion direccion1 = new Direccion();
		Direccion direccion2 = new Direccion();
		Direccion direccion3 = new Direccion();

		DatosContacto datosContacto1 = new DatosContacto();
		DatosContacto datosContacto2 = new DatosContacto();
		DatosContacto datosContacto3 = new DatosContacto();

		direccion1.setDireccion("c/ Padilla, 230 ático 2");
		direccion1.setPoblacion("Barcelona");
		direccion1.setCodigoPostal("80934");
		direccion1.setProvincia("Barcelona");
		direccion1.setPais("España");
		datosContacto1.setTelefono("932218772");
		datosContacto1.setFax(null);
		datosContacto1.setEmail("pablofer334@hotmail.com");

		direccion2.setDireccion("Avda. Pintor Garriño, 230-232");
		direccion2.setPoblacion("Móstoles");
		direccion2.setCodigoPostal("91002");
		direccion2.setProvincia("Madrid");
		direccion2.setPais("España");
		datosContacto2.setTelefono("912293444");
		datosContacto2.setFax(null);
		datosContacto2.setEmail("annabado@gmail.com");

		direccion3.setDireccion("c/ Pez Volador, 2 4º 2ª");
		direccion3.setPoblacion("Madrid");
		direccion3.setCodigoPostal("91240");
		direccion3.setProvincia("Madrid");
		direccion3.setPais("España");
		datosContacto3.setTelefono("912547821");
		datosContacto3.setFax(null);
		datosContacto3.setEmail("pacoort@gmail.com");

		camarero1.setId(100L);
		camarero1.setDni("27884178R");
		camarero1.setNombre("Pablo");
		camarero1.setApellido1("Fernández");
		camarero1.setApellido2("Borlán");
		camarero1.setDireccion(direccion1);
		camarero1.setDatosContacto(datosContacto1);
		camarero1.setLicenciaManipuladorAlimentos("LMA4998111253R");

		camarero2.setId(101L);
		camarero2.setDni("30092123H");
		camarero2.setNombre("Ana");
		camarero2.setApellido1("Badosa");
		camarero2.setApellido2("Domingo");
		camarero2.setDireccion(direccion2);
		camarero2.setDatosContacto(datosContacto2);
		camarero2.setLicenciaManipuladorAlimentos("LMA9000238712F");

		cliente1.setId(102L);
		cliente1.setDni("45099812W");
		cliente1.setNombre("Francisco Javier");
		cliente1.setApellido1("Ort");
		cliente1.setApellido2("Montcunill");
		cliente1.setDireccion(direccion3);
		cliente1.setDatosContacto(datosContacto3);
		cliente1.setTarjetaGold(true);
		
		Establecimiento establecimiento1 = new Establecimiento();
		Establecimiento establecimiento2 = new Establecimiento();
		
		establecimiento1.setCodigo(100L);
		establecimiento2.setCodigo(101L);
		
		establecimiento1.setDatosContacto(datosContacto1);	
		establecimiento2.setDatosContacto(datosContacto2);
		establecimiento1.setDireccion(direccion1);
		establecimiento2.setDireccion(direccion2);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Date fecha1 = null;
		Date fecha2 = null;
		
		try {
			fecha1 = sdf.parse("14/04/2005");
			fecha2 = sdf.parse("24/11/1999");
		} catch (Exception e) {
			// no code here
		}
	
		establecimiento1.setFechaInauguracion(fecha1);
		establecimiento2.setFechaInauguracion(fecha2);
		
		establecimiento1.setNombreComercial("Pollos Felices - La Vaguada");
		establecimiento2.setNombreComercial("Pollos Felices - Granvia  2");
		
		Producto producto1 = new Producto();
		Producto producto2 = new Producto();
		
		producto1.setCodigo(100L);
		producto2.setCodigo(101L);
		
		LineaPedido lineaPedido1 = new LineaPedido();
		LineaPedido lineaPedido2 = new LineaPedido();
		
		lineaPedido1.setCantidad(1);
		lineaPedido1.setCantidad(2);
		
		lineaPedido1.setProducto(producto1);
		lineaPedido1.setProducto(producto2);
		
		pedido1 = new Pedido();
		pedido2 = new Pedido();
		pedido3 = new Pedido();
		
		pedido1.setNumero(10L);
		pedido2.setNumero(11L);
		pedido3.setNumero(12L);
		
		pedido1.setFecha(fecha1);
		pedido2.setFecha(fecha1);
		pedido3.setFecha(fecha2);
		
		pedido1.setCliente(cliente1);
		pedido2.setCliente(cliente1);
		pedido3.setCliente(cliente1);
		
		pedido1.setCamarero(camarero1);
		pedido2.setCamarero(camarero1);
		pedido3.setCamarero(camarero2);
		
		pedido1.setEstablecimiento(establecimiento1);
		pedido2.setEstablecimiento(establecimiento1);
		pedido3.setEstablecimiento(establecimiento2);
		
		pedido1.setEstado(EstadoPedido.SERVIDO);
		pedido2.setEstado(EstadoPedido.NUEVO);
		pedido3.setEstado(EstadoPedido.CANCELADO);
		
		pedido1.setLineas(Arrays.asList(lineaPedido1));
		pedido2.setLineas(Arrays.asList(lineaPedido2));
		pedido3.setLineas(Arrays.asList(lineaPedido1, lineaPedido2));
	}

}
