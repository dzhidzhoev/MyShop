<%@page import="com.myshop.controller.OrderController"%>
<%@page import="com.myshop.controller.CommonController"%>
<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.beans.XMLEncoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.myshop.model.TypeEnum"%>
<%@page import="com.myshop.repository.ItemTraitRepository"%>
<%@page import="com.myshop.repository.TraitRepository"%>
<%@page import="com.myshop.model.Trait"%>
<%@page import="com.myshop.model.Order"%>
<%@page import="java.util.Set"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ include file="header.jspf" %>

<div class="container">
<div class="row">
<div class="col m-2 text-center">
<h1>Управление заказами</h1>
</div>
</div>
<div class="row">
    <div class="col m-1 text-center">
        
        <form method="get" action="<%=OrderController.ADMIN_ORDERS_PATH %>" autocomplete="off">
        <div class="input-group mb-3">
          <input name="email" value="${fn:escapeXml(param.email) }" type="text" class="form-control" placeholder="Имя пользователя" aria-label="Имя пользователя" aria-describedby="basic-addon2">
          <div class="input-group-append">
            <button class="btn btn-outline-secondary" type="submit">Фильтр</button>
          </div>
        </div>
        </form>
    </div>
</div>
<div class="row">
<div class="col m-2 text-center">
<table class="table">
  <thead>
    <tr>
      <th scope="col">#</th>
      <th scope="col">Пользователь</th>
      <th scope="col">Время заказа</th>
      <th scope="col">ФИО получателя</th>
      <th scope="col">Сумма</th>
      <th scope="col">Статус</th>
    </tr>
  </thead>
  <tbody>
	<c:forEach var="order" items="${orders }">
    <tr>
      <th scope="row"><a href="/user/order?id=${order.id }">${order.id }</a></th>
      <td><a href="user?id=${order.user.id }"><c:out value="${order.user.email }"></c:out> </a></td>
      <td><c:out value="${order.orderTime }"></c:out></td>
      <td><c:out value="${order.user.firstName }"></c:out> <c:out value="${order.user.middleName }"></c:out> <c:out value="${order.user.lastName}"></c:out> </td>
      <td><c:out value="${order.total }"></c:out> руб.</td>
      <td>
      	<div class="dropdown">
            <%=CommonController.getOrderStatusHTML(((Order) pageContext.getAttribute("order")).getStatus()) %>
          
        </div>
      </td>
    </tr>
	</c:forEach>
  </tbody>
</table>
<br><a href="<%=OrderController.ADMIN_ORDERS_PATH %>?page=${page - 2 < 0 ? 0 : page - 2}${not empty param.email ? '&email='.concat(param.email) : ''}" class="btn btn-secondary">&lt;&lt;</a>&nbsp;Страница: <c:out value="${page }"></c:out>&nbsp;<a href="<%=OrderController.ADMIN_ORDERS_PATH %>?page=${page}${not empty param.email ? '&email='.concat(param.email) : ''}" class="btn btn-secondary">&gt;&gt;</a>
</div>
</div>
</div>

<%@ include file="footer.jspf" %>