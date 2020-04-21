<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jspf" %>
<div class="container">
<div class="row">
<div class="col text-center">
<h1>Восстановление пароля</h1>
</div>
</div>
<div class="row">
<div class="col">
<form action="forgot" method="post">
  <div class="form-group">
    <label for="exampleInputEmail1">Email</label>
    <input name="email" type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp" placeholder="Введите адрес почтового ящика">
  </div>
  <c:if test="${isEmailEmpty != null }">
  	<div class="text-danger">Введите email!</div>
  </c:if>
  <button type="submit" class="btn m-2 btn-primary">Отправить заявку на восстановление</button>
 <br>
</form>
</div>
</div>
</div>
<%@ include file="footer.jspf" %>