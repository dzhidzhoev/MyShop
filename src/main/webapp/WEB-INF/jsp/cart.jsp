<%@page import="com.myshop.model.Cart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ include file="header.jspf" %>

<div class="container m-3">
<div class="row">
<div class="col-sm">
<h1>Корзина</h1>
</div>
</div>

<div class="row">

<div class="col-md-8">

<div class="conatiner">
<%int price = 0; %>
<c:forEach items="${cart }" var="cartItem">
        <div class="row m-2 bg-light">
        <div class="col-sm-2">
        	<img class="card-img-top" src="/item_image?id=${cartItem.item.id}" />
        </div>
        <div class="col-sm-6 py-1">
        	<a href="/item?id=${cartItem.item.id }"><c:out value="${cartItem.item.name }"></c:out> </a><br>
        	Тип: <c:out value="${cartItem.item.category.name }"></c:out> <br>
        	Наличие: <c:choose><c:when test="${cartItem.item.count > 0 }">есть</c:when><c:otherwise><span class="text-danger">нет</span></c:otherwise></c:choose>
        </div>
        <div class="col-sm-1 vcenter px-0 py-0 my-auto">
        	<c:out value="${cartItem.item.price }"></c:out> руб.
        </div>
        <div class="col-sm-1 vcenter px-0">
        <div class="my-auto text-center">
        	<form method="post" id="form-inc-item-count-${cartItem.item.id }" action="/user/add_count_item_cart">
        		<input type="hidden" name="userId" value="${cartItem.user.id }" />
        		<input type="hidden" name="itemId" value="${cartItem.item.id}"/>
        		<input type="hidden" name="count" value="1"/>
        	</form>
        	<span class="btn-link" onclick="$('#form-inc-item-count-${cartItem.item.id }').submit()">+</span>
        	<b>
			${cartItem.count }
    		</b>
        	<span class="btn-link" onclick="$('#form-dec-item-count-${cartItem.item.id }').submit()">-</span>
    		<br>
 <%
 	Cart cart = ((Cart) pageContext.findAttribute("cartItem"));
 	price += cart.getItem().getPrice() * cart.getCount(); 
 %>
<c:out value="${cartItem.count * cartItem.item.price }"></c:out> руб.
        </div>
        </div>
        <div class="col-sm-2 vcenter">
        	<div class="my-auto mx-1">
        	<form action="/user/delete_item_cart" method="post">
        		<input type="hidden" name="itemId" value="${cartItem.item.id}">
        		<input type="submit" value="Удалить" class="btn btn-danger">
        	</form>
        	</div>
        </div>
        </div>
        
        
 		<form method="post" id="form-dec-item-count-${cartItem.item.id }" action="/user/add_count_item_cart">
     		<input type="hidden" name="userId" value="${cartItem.user.id }" />
     		<input type="hidden" name="itemId" value="${cartItem.item.id}"/>
     		<input type="hidden" name="count" value="-1"/>
     	</form>
</c:forEach>
</div>
</div>

<div class="col-md-4">
<h2>Итого:<br>
<%=price %>₽<br>
</h2>
<form action="/user/place_order" method="post">
<button class="btn btn-success" type="submit"><h3>Заказать!</h3></button>
</form>
</div>
</div>

</div>

<%@ include file="footer.jspf" %>