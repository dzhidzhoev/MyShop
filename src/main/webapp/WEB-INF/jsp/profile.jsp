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
<div class="row">
<div class="col m-2 text-center">
<h1>Здравствуйте, ${fn:escapeXml(user.getFirstName())} ${fn:escapeXml(user.getMiddleName())}!</h1>&nbsp;<a href="/logout">Выйти</a>
</div>
</div>

<c:if test="${isUserAdmin}">
<div class="row bg-light">
<div class="col m-2 text-right">
<a href="orders.php">Заказы</a>&nbsp;
</div>
<div class="col m-2 text-center">
<a href="users.php">Пользователи</a>
</div>
<div class="col m-2 text-left">
<a href="categories.php">Категории товаров</a>
</div>
<div class="col m-2 text-left">
<a href="traits.php">Свойства товаров</a>
</div>
<div class="col m-2 text-left">
<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal">
  Добавить товар
</button>
</div>
</div>
</c:if>

<div class="row m-2">
    <div class="col-md-6">
    	<b>Мои заказы</b>
    	<div class="conatiner">
    	<% DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.of("UTC+3"));
    	%>
    	<c:forEach items="${orders}" var="o">
    		<% Order o = (Order) pageContext.getAttribute("o"); %>
    		<div class="row m-1 p-2 bg-light">
            <div class="col-sm-6"><a href="order-details.php">Заказ #${o.getId()}</a></div>
            <div class="col-sm-3">
            	<%=dtf.format(o.getOrderTime().toInstant()) %>
            </div>
            <%=CommonController.getOrderStatusHTML(o.getStatus()) %>
            </div>
    	</c:forEach>
    	<c:if test="${orders.isEmpty()}">
	    	<div class="row m-1 p-2 bg-light text-center">
	    	Заказов нет!
	    	</div>
    	</c:if>
    	</div>
	</div>
    
    <div class="col-md-6">
        <b>Мои данные</b><br>
<?php 
include 'userdata.php';
userdata_form('Обновить данные');
?>
    </div>
</div>
</div>

<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">Выберите категорию</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body form-group">
        <input list="categories" name="browser" width="1000" placeholder="Кликните дважды" autocomplete="off">
          <datalist id="categories">
            <option value="Холодильники">
            <option value="Холодильники">
            <option value="Холодильники">
            <option value="Холодильники">
          </datalist>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
        <a href="item.php?edit=1" type="submit" class="btn btn-primary">Создать</a>
      </div>
    </div>
  </div>
</div>


<%@ include file="footer.jspf" %>