<%@page import="com.myshop.controller.CategoryController"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="header.jspf" %>

<div class="container">
	<div class="row m-3">
		<div class="col-sm-2 my-auto">
			<span class="align-middle">
				<a href="<%=CategoryController.ADMIN_CATEGORIES_PATH%>">&lt; Назад</a>
			</span>
		</div>

		<div class="col-sm-10">
			<h1>Категория: ${category.getName()}</h1>
		</div>
	</div>
	<div class="row">
		<div class="col">
			<c:if test="${param.errorMessage eq 'noname'}">
				<div class="text-danger">Название категории не указано!</div><br>
			</c:if>
			<table class="table">
              <thead>
                <tr>
                  <th scope="col">Название</th>
                  <th scope="col">Видна для пользователей?</th>
                  <th scope="col">Действие</th>
                </tr>
              </thead>
              <tbody>
			    <tr>
			      <td>
			      	<input type="text" id="category_name_input" value="${fn:escapeXml(category.getName())}"></input>
			   	  </td>
			      <td>
			      <c:choose>
			      <c:when test="${category.isActive()}">
			      <input id="cat_active_input" type="checkbox" checked></input>
			      </c:when>
			      <c:otherwise>
			      <input id="cat_active_input" type="checkbox"></input>
			      </c:otherwise>
			      </c:choose>
			      </td>
			      <td><button class="btn btn-success" onclick="updateCat()">Обновить</button></td>
			    </tr>
			
              </tbody>
            </table>
		</div>
	</div>
	<div class="row">
		<div class="col m-1">Свойства категории</div>
	</div>
	<div class="row">
		<table>
		<tbody>
			<c:forEach var="trait" items="${traits}">
				<tr>
				<td><span class="m-1">${fn:escapeXml(trait.getName())}</span></td>
				<td><button class="btn btn-danger" onclick="removeCatTrait(${trait.getId()})">x</button></td>
				</tr>
			</c:forEach>
			<tr>
			<td>
				<div class="dropdown">
                  <button class="btn dropdown-toggle btn-success" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span>Добавить</span>
                  </button>
                  <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                  	<c:forEach var="trait" items="${allTraits}">
                  		<a onclick="addCatTrait(${trait.getId()})" class="dropdown-item" href="#">${fn:escapeXml(trait.getName())}</a>
                  	</c:forEach>
                  </div>
                </div>
			</td>
			</tr>
		</tbody>
		</table>
	</div>
</div>

<form class="d-none" id="upd-cat" method="post" action="/admin/update_cat">
	<input type="hidden" name="id" value="${category.getId()}" />
	<input type="hidden" name="name" id="name-field" />
	<input type="hidden" name="active" id="active-field" />
</form>
<form class="d-none" id="cat-trait" method="post" action="">
	<input type="hidden" name="traitId" id="trait-id-field" />
	<input type="hidden" name="categoryId" id="category-id-field" />
</form>
<script type="text/javascript">
	function updateCat() {
		$('#name-field').val($('#category_name_input').val());
		$('#active-field').val($('#cat_active_input').prop('checked'));
		$('#upd-cat').submit();
	}
	function addCatTrait(traitId) {
		$('#cat-trait').attr('action', '/admin/add_cat_trait');
		$('#trait-id-field').val(traitId);
		$('#category-id-field').val(${category.getId()});
		$('#cat-trait').submit();
	}
	function removeCatTrait(traitId) {
		$('#cat-trait').attr('action', '/admin/remove_cat_trait');
		$('#trait-id-field').val(traitId);
		$('#category-id-field').val(${category.getId()});
		$('#cat-trait').submit();
	}
</script>

<%@ include file="footer.jspf" %>