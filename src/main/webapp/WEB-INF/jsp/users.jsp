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

<div class="container-fluid">
<div class="row">
<div class="col m-2 text-center">
<h1>Управление пользователями</h1>
</div>
</div>
<div class="row">
<div class="col m-2 text-center">
<c:if test="${param.errorMessage eq 'selfdelete'}">
<div class="text-danger">Невозможно удалить самого себя!</div><br>
</c:if>

<table class="table">
  <thead>
    <tr>
      <th scope="col">#</th>
      <th scope="col">Email</th>
      <th scope="col">Администратор?</th>
      <th scope="col"></th>
    </tr>
  </thead>
  <tbody>
	<c:forEach items="${users}" var="user">
	    <tr>
	      <th scope="row">${user.id}</th>
	      <td>${user.isDeleted() ? '<strike>' : '<a href="user-admin.php">'}${user.email}${user.isDeleted() ? '</strike>' : '</a>'}</a></td> <!-- TODO -->
	      <td><input type="checkbox" disabled="disabled" ${user.isAdmin() ? 'checked="checked"' : ''}/></td>
	      <td>
	      	<c:if test="${user.deleted }">
	      		<form action="/admin/restore_user" method="post">
		      		<input type="hidden" name="id" value="${user.id}">
		      		<input type="submit" value="Восстановить" class="btn btn-warning">
		      	</form>
	      	</c:if>
	      	<c:if test="${not user.deleted }">
		      	<form action="/admin/delete_user" method="post">
		      		<input type="hidden" name="id" value="${user.id}">
		      		<input type="submit" value="Удалить" class="btn btn-danger">
		      	</form>
	      	</c:if>
	      </td>
	    </tr>
    </c:forEach>
  </tbody>
</table>

</div>
</div>
</div>

<%@ include file="footer.jspf" %>