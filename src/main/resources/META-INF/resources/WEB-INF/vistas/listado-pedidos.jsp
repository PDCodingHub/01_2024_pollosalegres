<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<jsp:include page="common-in-head.jsp"/>
</head>
<body>
	<div class="container-fluid">
		<jsp:include page="header.jsp"/>
		<h2>Listado de Pedidos</h2>
		<table class="table">
			<caption>Listado de Pedidos</caption>
			<thead>
				<tr>
					<th>Numero</th>
					<th>Establecimiento</th>
					<th>Fecha</th>
					<th>Hora</th>
					<th>Camarero</th>
					<th>Estado</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="pedido" items="${pedidos}">
				<tr>
					<td>${pedido.numero}</td>
					<td>${pedido.establecimiento.nombreComercial}</td>
					<td><fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy"/></td>
					
					<td><fmt:formatDate value="${pedido.fecha}" pattern="HH:mm"/></td>
					<td>${pedido.camarero.nombre} ${pedido.camarero.apellido1} ${pedido.camarero.apellido2}</td>
					<td>${pedido.estado}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>