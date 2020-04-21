<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="header.jspf" %>
<div class="container">
<div class="row">
<div class="col text-center">
<h1>Авторизация</h1>
</div>
</div>
<div class="row">
<div class="col">
<form action="perform_login" method="post">
  <div class="form-group">
    <label for="username">Email</label>
    <input type="email" class="form-control" id="username" name="username" aria-describedby="emailHelp" placeholder="Введите адрес почтового ящика">
  </div>
  <div class="form-group">
    <label for="password">Пароль</label>
    <input type="password" class="form-control" id="password" name="password" placeholder="Введите пароль">
    <small id="passwordHelp" class="form-text text-muted"><a href="forgot">Забыли пароль?</a></small>
    
	  <c:if test="${param.error != null }">
	  	<div class="text-danger">Пользователь не найден!</div>
	  </c:if>
  </div>
  <button type="submit" class="btn m-2 btn-primary">Войти</button>
</form>
<form action="register">
	<button type="submit" class="btn m-2 btn-danger">Регистрация нового пользователя</button>
</form>
</div>
</div>
</div>

<c:if test="${param.restored != null || param.pwdchanged != null }">
<div id="restored-toast" data-delay="20000" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
  <div class="toast-header">
    <strong class="mr-auto">MyShop</strong>
    <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
      <span aria-hidden="true">&times;</span>
    </button>
  </div>
  <div class="toast-body">
<c:if test="${param.restored != null}">
   	Вам отправлено письмо с инструкциями
</c:if>
<c:if test="${param.pwdchanged != null}">
	Пароль изменён
</c:if>
  </div>
</div>
<script type="text/javascript">
$('.toast').toast();
$('#restored-toast').toast('show');
</script>
</c:if>

<%@ include file="footer.jspf" %>