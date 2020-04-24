<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.beans.XMLEncoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.myshop.model.TypeEnum"%>
<%@page import="com.myshop.repository.ItemTraitRepository"%>
<%@page import="com.myshop.repository.TraitRepository"%>
<%@page import="com.myshop.model.Trait"%>
<%@page import="java.util.Set"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ include file="header.jspf" %>

<div class="container-fluid">
<div class="row">
<div class="col m-2 text-center">
<h1>Управление свойствами товаров</h1>
</div>
</div>
<div class="row">
<div class="col m-2 text-center">
<c:if test="${param.errorMessage != ull}">
<span class="text-danger">
Ошибка! ${fn:escapeXml(param.errorMessage)}
</span>
</c:if>
<table class="table">
  <thead>
    <tr>
      <th scope="col">#</th>
      <th scope="col">Название</th>
      <th scope="col">Видно при поиске?</th>
      <th scope="col">Тип</th>
      <th scope="col">Min знач.</th>
      <th scope="col">Max знач.</th>
      <th scope="col">Возможные значения</th>
      <th scope="col">Еденица измерения</th>
      <th scope="col">Действие</th>
    </tr>
  </thead>
  <tbody>
  <%
	String number = "Число";
	String str = "Произвольная строка";
	String en = "Значение из набора";
	Map<String, String> allTypes = Map.of(
			TypeEnum.EnumType.toString(), en,
			TypeEnum.IntType.toString(), number,
			TypeEnum.StringType.toString(), str);
	Map<String, String> colors = Map.of(
			TypeEnum.EnumType.toString(), "text-info",
			TypeEnum.IntType.toString(), "text-success",
			TypeEnum.StringType.toString(), "text-danger"
			);
	pageContext.setAttribute("allTypesKey", allTypes.keySet());
	pageContext.setAttribute("allTypes", allTypes);
	pageContext.setAttribute("colors", colors);
  %>
	<%
	long traitsCount = ((Long) request.getAttribute("traitsCount"));
	for (long i = 0; i < traitsCount; i++) {
		pageContext.setAttribute("i", i);
	%>
		<form:form modelAttribute="trait_${i}" method="post" action="/admin/add_update_trait">
		<tr>
			<form:input type="hidden" path="id" />
	      <th scope="row"><%=((Trait) request.getAttribute("trait_" + i)).getId() %></th>
	      <td><form:input path="name" type="text" placeholder="Название"/></td>
	      <td><form:checkbox path="searchable" /></td>
	      <td>
	          <form:select path="type" class="custom-select-sm">
	          <c:forEach items="${allTypesKey }" var="type">
	          	<form:option value="${type}" label="${allTypes[type] }" class="${colors[type] }" />
	          </c:forEach>
	          
	          </form:select>
	      </td>
	      <td>
	      	<form:input path="minValue" type="number" placeholder="Для чисел" />
	      </td>
	      <td><form:input path="maxValue" type="number" placeholder="Для чисел" /></td>
	      <td><form:input path="values" type="text" placeholder="Значения через запятую"/></td>
	      <td><form:input path="unit" type="text" placeholder="Для чисел"/></td>
	      <td><input type="submit" class="btn btn-success" value="Обновить" />
	      		<button type="button" class="btn btn-danger" onclick="deleteTrait(${requestScope['trait_'.concat(i)].getId()})">Удалить</button>
	      </td>
	    </tr>
	    </form:form>
	<%
	}
	%>
	<form:form modelAttribute="newTrait" method="post" action="/admin/add_update_trait">
	<tr>
      <th scope="row">*</th>
      <td><form:input path="name" type="text" placeholder="Название"/></td>
      <td><form:checkbox path="searchable" /></td>
      <td>
          <form:select path="type" class="custom-select-sm">
          <%
          for (String type: allTypes.keySet()) {
	      %>
	      	<option value="<%=type %>" class="<%=colors.get(type)%>">
            	<%=allTypes.get(type) %>
            </option>
          <%
          }
          %>
          </form:select>
      </td>
      <td>
      	<form:input path="minValue" type="number" placeholder="Для чисел" />
      </td>
      <td><form:input path="maxValue" type="number" placeholder="Для чисел" /></td>
      <td><form:input path="values" type="text" placeholder="Значения через запятую"/></td>
      <td><form:input path="unit" type="text" placeholder="Для чисел"/></td>
      <td><input type="submit" class="btn btn-success" value="Добавить" /></td>
    </tr>
    </form:form>
  </tbody>
</table>
</div>
</div>
</div>

<form id="delete-form" action="/admin/delete_trait" method="post">
	<input id="delete-form-id" type="hidden" name="id"> 
</form>
<script type="text/javascript">
function deleteTrait(id) {
	$('#delete-form-id').val(id);
	$('#delete-form').submit();
}
</script>

<%@ include file="footer.jspf" %>