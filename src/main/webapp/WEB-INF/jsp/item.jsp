<%@page import="com.myshop.repository.ItemTraitRepository"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ include file="header.jspf" %>
<style>
.form-control-lg {
	font-size: 2.25rem;
}
</style>
<div class="container">
	<div class="row m-3">
		<div class="col-sm-2 my-auto">
		</div>

		<div class="col-sm-10">
			<h1>
			<input type="text" class="form-control-plaintext form-control-lg" value="${fn:escapeXml(item.getName()) }" placeholder="Название товара" />
			</h1>

		</div>
	</div>
	
	<div class="row">
	<div class="col-sm">
	<img class="img-fluid"src="/item_image?id=${item.getId() }" />
	</div>
	<div class="col-sm">
	Категория: <c:out value="${item.getCategory().getName() }" />
	<p>
	
	<c:forEach items="${traits }" var="trait">
		<c:out value="${trait.getTrait().getName() }" /> - <c:out value="${itRepo.getValue(trait, tRepo) }"></c:out><p>
	</c:forEach>
	
	Наличие: <c:choose><c:when test="${item.getCount() > 0 }">есть</c:when><c:otherwise>нет</c:otherwise> </c:choose>
<p>
	<h4>${item.getPrice() } руб.
	</h4>
	<div class="my-auto">
     		<span class="btn-link" onclick="$('#item-counter').text(parseInt($('#item-counter').text()) + 1)">+</span>  	
        	<b>
        	<span id="item-counter">
        	1
    		</span>
    		</b>
    		<span class="btn-link" onclick="if (parseInt($('#item-counter').text()) > 1) {$('#item-counter').text(parseInt($('#item-counter').text()) - 1);}">-</span>
        </div>
	<p>

	<c:if test="${not empty param.added }">
		<a href="/user/cart" class="btn btn-success">Добавлено в корзину</a><br>
	</c:if>
	<c:if test="${empty param.added}">
		<form id="add-to-cart-form" method="post" action="/user/add_to_cart">
			<input type="hidden" name="count" id="count-form-input">
			<input type="hidden" name="itemId" value="${item.id }" >
		</form>
		<button onclick="$('#count-form-input').val(parseInt($('#item-counter').text())); $('#add-to-cart-form').submit();" class="btn btn-primary">В корзину!</button><br> <!-- TODO -->
	</c:if>
	
	<c:if test="${isUserAdmin }">
	<a id="item-edit-link" href="/admin/item?id=${item.id }">Редактировать</a><br>
	</c:if>
	<p><p>
	<c:out value="${item.getDescription()}" />
	</div>
	</div>
</div>

<%@ include file="footer.jspf" %>