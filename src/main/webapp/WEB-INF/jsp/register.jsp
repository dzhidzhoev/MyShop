<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>

<div class="container">
<div class="row">
<div class="col text-center">
<h1>Регистрация</h1>
</div>
</div>
<div class="row">
<div class="col">
<c:set var="edit_admin" value="false"></c:set>
<c:set var="action_url" value="register"></c:set>
<c:set var="action" value="Зарегистрироваться"></c:set>
<%@ include file="admin/userdata.jspf" %>
</div>
</div>
</div>

<%@ include file="footer.jspf" %>