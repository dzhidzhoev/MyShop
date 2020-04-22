<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="header.jspf" %>

<div class="container-fluid">
<div class="row">
<div class="col m-2 text-center">
<h1>Управление категориями товаров</h1>
</div>
</div>
<div class="row">
<div class="col m-2 text-center">
<c:if test="${param.errorMessage eq 'noname'}">
<div class="text-danger">Название категории не указано!</div><br>
</c:if>
<table class="table">
  <thead>
    <tr>
      <th scope="col">#</th>
      <th scope="col">Название</th>
      <th scope="col">Видна для пользователей?</th>
    </tr>
  </thead>
  <tbody>
	<c:forEach items="${categories}" var="cat">
	    <tr>
	      <th scope="row">${cat.getId()}</th>
	      <td><a href="category-admin.php">${fn:escapeXml(cat.getName())}</a></td>
	      <td>
	      <c:choose>
	      <c:when test="${cat.isActive()}">
	      <input type="checkbox" onclick="toggleCatActive(${cat.getId()})" checked></input>
	      </c:when>
	      <c:otherwise>
	      <input type="checkbox" onclick="toggleCatActive(${cat.getId()})"></input>
	      </c:otherwise>
	      </c:choose>
	      </td>
	    </tr>
	</c:forEach>
	<tr>
      <td><button class="btn btn-success" onclick="newCategory()">Добавить</button></td>
      <td><input type="text" id="new_cat_name" placeholder="Название"/></td>
      <td><input type="checkbox" id="new_cat_active" checked></input></td>
    </tr>
  </tbody>
</table>
</div>
</div>
</div>

<form class="d-none" id="cat_active_form" action="/admin/toggle_cat_is_active" method="post"><input id="category_id_field" type="hidden" name="cat" value=""/></form>
<form class="d-none" id="cat_add_form" action="/admin/add_cat" method="post">
<input id="cat_name_field" type="hidden" name="name" value=""/>
<input id="cat_active_field" type="hidden" name="active" value=""/>
</form>
<script type="text/javascript">
	function toggleCatActive(categoryId) {
		$('#category_id_field').val(categoryId);
		$('#cat_active_form').submit();
	}
	function newCategory() {
		$('#cat_name_field').val($('#new_cat_name').val());
		$('#cat_active_field').val($('#new_cat_active').prop('checked'));
		$('#cat_add_form').submit();
	}
</script>

<%@ include file="footer.jspf" %>