<%@page import="com.myshop.controller.OrderController"%>
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
<div class="row">
<div class="col m-2 text-center">
<h1>Здравствуйте, ${fn:escapeXml(user.getFirstName())} ${fn:escapeXml(user.getMiddleName())}!</h1>&nbsp;<a href="/logout">Выйти</a>
</div>
</div>

<c:if test="${isUserAdmin}">
<div class="row bg-light">
<div class="col m-1 text-right">
<a href="<%=OrderController.ADMIN_ORDERS_PATH%>">Заказы</a>&nbsp;
</div>
<div class="col m-1 text-center">
<a href="<%=UserController.ADMIN_USERS_PATH%>">Пользователи</a>
</div>
<div class="col m-1 text-center">
<a href="<%=CategoryController.ADMIN_CATEGORIES_PATH%>">Категории товаров</a>
</div>
<div class="col m-1 text-center">
<a href="<%=TraitController.ADMIN_TRAITS_PATH%>">Свойства товаров</a>
</div>
<div class="col m-2 text-center">
<button type="button" id="add-item-dialog-button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal">
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
            <div class="col-sm-6"><a href="/user/order?id=${o.getId() }">Заказ #${o.getId()}</a></div>
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
        <c:set var="action_url" value="update_user_info"></c:set>
		<c:set var="edit_admin" value="false"></c:set>
		<c:set var="action" value="Обновить данные"></c:set>
		<c:set var="redirectPath" value="/profile"></c:set>
		<%@ include file="admin/userdata.jspf" %>
    </div>
</div>
</div>

<c:if test="${isUserAdmin}">
<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
    <form action="/admin/add_item" method="post">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">Выберите категорию</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body form-group">
  		<select name="categoryId">
	       	<c:forEach items="${allCategories}" var="cat">
	       		<option value="${cat.id}">${fn:escapeXml(cat.name)}</option>
	       	</c:forEach>
  		</select>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
        <button type="submit" class="btn btn-success" id="add-item-button">Создать</button>
      </div>
    </form>
    </div>
  </div>
</div>
</c:if>


<%@ include file="footer.jspf" %>