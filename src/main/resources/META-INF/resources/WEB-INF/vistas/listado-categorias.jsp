<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>Pollos Primos</title>
	<jsp:include page="common-in-head.jsp"/>
</head>
<body>
	<div class="container-fluid">
		<jsp:include page="header.jsp"/>
		<h2>Listado de Categorias</h2>
		<table class="table">
			<caption>Listado de Categorias</caption>
			<thead>
				<tr>
					<th>Id</th>
					<th>nombre</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="categoria" items="${categorias}">
				<tr>
					<td>${categoria.id}</td>
					<td>${categoria.nombre}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>