<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="container">
<div class="row">
<div class="col text-center">
<h1>Смена пароля</h1>
</div>
</div>
<div class="row">
<div class="col">

<div class="form-group">
	<form action="reset" method="post">
		<input type="hidden" name="email" value="${fn:escapeXml(param.email)}" />
		<input type="hidden" name="token" value="${fn:escapeXml(param.token)}" />
		<label for="exampleInputPassword1">Новый пароль (минимум 8 символов) *</label>
		<input name="password" type="password" required="required" class="form-control" id="exampleInputPassword1" placeholder="Введите пароль">
		<input name="password2" type="password" required="required" class="form-control" id="exampleInputPassword2" placeholder="Повторите пароль">
		<c:if test="${samePwd != null }">
			<div class="text-danger">Введены разные пароли!</div>
		</c:if>
		<button type="submit" class="btn m-2 btn-success">Обновить</button>
		<c:if test="${errorMessage != null }">
		  	<div class="text-danger">Ошибка! ${errorMessage }</div>
		</c:if>
	</form>
</div>

</div>
</div>
</div>

<%@ include file="footer.jspf" %>