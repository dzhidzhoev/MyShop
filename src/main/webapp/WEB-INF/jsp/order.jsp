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

<c:set var="order" value="${order }"></c:set>
<br>
<div class="container my-1">
<h2>Заказ #${order.id }</h2>
	<h4>Статус: 
	<c:if test="${isUserAdmin }">
		<div class="dropdown">
			<button class="btn dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				<%=CommonController.getOrderStatusHTML(((Order) pageContext.getAttribute("order")).getStatus()) %>
			</button>
			
			<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
			<%

      		for (OrderStatus status: OrderStatus.values()) {
      			out.print("<form  method=\"post\" action=\"/admin/set_order_status\">");
      			out.print("<input type=\"hidden\" name=\"id\" value=" + ((Order) pageContext.getAttribute("order")).getId() + "></input>");
      			out.print("<input type=\"hidden\" name=\"status\" value=" + status + "></input>");
      			out.print("<button class=\"dropdown-item\" type=\"submit\">");
      			out.print(CommonController.getOrderStatusHTML(status));
      			out.print("</button>");
      			out.print("</form>");
      		}
			%>
            
          </div>
		</div>
	</c:if>
	<c:if test="${not isUserAdmin }">
	<%=CommonController.getOrderStatusHTML(((Order) pageContext.getAttribute("order")).getStatus()) %>
	</c:if>
	</h4><br>
<form>
		<div class="form-group">
			<label for="exampleInputEmail1">Дата заказа</label>
			<br>${order.orderTime }
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Получатель</label> <input
				type="text" class="order-fc form-control" id="exampleInputEmail1"
				aria-describedby="emailHelp" placeholder="Введите ФИО" value="${fn:escapeXml(order.name) }"> <small
				id="emailHelp" class="form-text text-muted"><a class="order-fc" href="#">Использовать
					данные аккаунта</a></small>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Номер телефона получателя</label> <input
				type="text" class="order-fc form-control" id="exampleInputEmail1"
				aria-describedby="emailHelp" placeholder="Введите номер" value="${fn:escapeXml(order.phone) }"> <small
				id="tel" class="form-text text-muted"><a class="order-fc" href="#">Использовать
					данные аккаунта</a></small>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Почта получателя</label> <input
				type="email" class="order-fc form-control" id="exampleInputEmail1"
				aria-describedby="emailHelp" placeholder="Введите email-адрес" value="${fn:escapeXml(order.email) }"> <small
				id="emailHelp" class="form-text text-muted"><a class="order-fc" href="#">Использовать
					данные аккаунта</a></small>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Время доставки</label> <input
				type="text" class="order-fc form-control" id="exampleInputEmail1"
				aria-describedby="emailHelp"
				placeholder="Введите предпочитаемое время и дату" value="${fn:escapeXml(order.deliveryTime) }"> <small class="order-fc"
				id="emailHelp" class="form-text text-muted">Оставьте пустым, если
				заказ должен быть доставлен Вам как можно скорее</small>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Адрес доставки</label>
			<textarea type="text" class="order-fc form-control" id="exampleInputEmail1"
				aria-describedby="emailHelp"
				placeholder="Введите полный почтовый адрес и индекс" cols="" rows=""><c:out value="${fn:escapeXml(order.address) }"></c:out></textarea>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Комментарий</label>
			<textarea type="text" class="order-fc form-control" id="exampleInputEmail1"
				aria-describedby="emailHelp" placeholder="Любые Ваши пожелания"
				cols="" rows=""><c:out value="${fn:escapeXml(order.comment) }"></c:out> </textarea>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Сумма</label>
			<h3>${order.total } руб.</h3>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Товары</label>
			<table class="table">
				<thead>
					<tr>
						<td>Название товара</td>
						<td>Количество</td>
						<td>Цена товара</td>
						<td>Стоимость</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${items }">
					<tr>
						<td><c:out value="${item.item.name }"></c:out></td>
						<td>${item.count }</td>
						<td>${item.price }</td>
						<td>${item.price * item.count }</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

	<script>
		$('.order-fc').attr('readonly', true);
		$('.order-fc').attr('placeholder', '');
		$('a.order-fc').remove();
		$('small.order-fc').remove();
	</script>
	</form>
	
	<form method="post" action="/user/cancel">
		<input type="hidden" name="id" value="${order.id }" />
		<button class="btn btn-danger" type="submit">Отменить заказ</button>
	</form>
</div>
<br>

<%@ include file="footer.jspf" %>