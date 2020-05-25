<%@page import="com.myshop.controller.UserController"%>
<%@page import="com.myshop.controller.TraitController"%>
<%@page import="com.myshop.controller.CategoryController"%>
<%@page import="java.time.ZoneId"%>
<%@page import="java.time.format.FormatStyle"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="com.myshop.model.Order"%>
<%@page import="com.myshop.controller.CommonController"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="header.jspf" %>

<div class="container">
	<div class="row m-3">
		<div class="col-sm-2 my-auto">
			<span class="align-middle">
				<a href="<%=UserController.ADMIN_USERS_PATH%>">&lt; Назад</a>
			</span>
		</div>

		<div class="col-sm-10">
			<h1><c:out value="${user.email}"></c:out></h1>
			&nbsp;<a href="orders.php">Его заказы</a> <!-- TODO -->
		</div>
	</div>
	<div class="row">
		<div class="col">
			<c:set var="action_url" value="/update_user_info"></c:set>
			<c:set var="edit_admin" value="true"></c:set>
			<c:set var="action" value="Обновить данные"></c:set>
			<c:set var="redirectPath" value="/admin/user?id=${user.id}"></c:set>
			<%@ include file="admin/userdata.jspf" %>
		</div>
	</div>
</div>

<%@ include file="footer.jspf" %>