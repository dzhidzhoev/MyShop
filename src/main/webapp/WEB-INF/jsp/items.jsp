<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ include file="header.jspf" %>

<div class="container-fluid">
<div class="row my-3">
<h2>Товары</h2>
</div>
<div class="row">
<div class="col-sm-3 bg-light">
<c:if test="${not empty category }">
<h4>Фильтр</h4>

<form>
<div class="form-group characteristics">
<label><b>Цена</b></label><br>
<input id="min-price" type="number" placeholder="От" class="form-control-range" value="${param.minPrice }" />
<input id="max-price" type="number" placeholder="До" class="form-control-range" value="${param.maxPrice }" />
</div>
</form>

<c:forEach items="${catTraits }" var="trait">
	<div class="form-group characteristics">
		<label>
			<c:out value="${trait.getName() }">
			</c:out>&nbsp;<b>
			<span id="slider-val-${trait.getId() }"><c:if test="${trait.getType() eq 'IntType' && searchTerms.containsKey(trait.getId())}"><c:out value="${searchTerms.get(trait.getId()).minSegmentVal}" /> - <c:out value="${searchTerms.get(trait.getId()).maxSegmentVal}" /></c:if></span></b> <c:if test="${trait.getType() eq 'IntType'}"><a href="javascript:;" onclick="$('#slider-val-${trait.getId() }').text(''); $('#slider-${trait.getId() }').slider('refresh');" >x</a></c:if>
		</label><br>
		<c:choose>
		<c:when test="${trait.getType() eq 'IntType' }">
			<div class="form-row">
			<div class="col">
			<c:out value="${trait.getMinValue() }"></c:out>
			</div>
			<div class="col">
			<input id="slider-${trait.getId() }" class="filter-slider" type="text" class="span2" value="" data-slider-min="${trait.getMinValue() }" data-slider-max="${trait.getMaxValue() }" data-slider-step="1" data-slider-value="[${searchTerms.containsKey(trait.getId()) ? searchTerms.get(trait.getId()).minSegmentVal : trait.getMinValue() },${searchTerms.containsKey(trait.getId()) ? searchTerms.get(trait.getId()).maxSegmentVal : trait.getMaxValue() }]"/>
			</div>
			<div class="col text-right">
			<c:out value="${trait.getMaxValue() }"></c:out>
			</div>
			</div>
			<script type="text/javascript">
				$("#slider-" + ${trait.getId()}).slider({});
				$("#slider-" + ${trait.getId()}).on("change", function (evt) {
					var minmax = $('#slider-' + ${trait.getId()}).val().toString().split(',');
					$('#slider-val-' + ${trait.getId()}).text(minmax[0] + ' - ' + minmax[1]);
				});
			</script>
		</c:when>
		<c:when test="${trait.getType() eq 'EnumType' }">
			<c:forEach items="${trait.getValues() }" varStatus="i" var="traitVal">
				<div class="form-check">
				  <input class="form-check-input filter-checkbox" type="checkbox" ${(searchTerms.containsKey(trait.getId()) && searchTerms.get(trait.getId()).oneOfValues.contains(traitVal)) ? 'checked' : '' } value="" id="check-${trait.getId() }-${i.index }">
				  <label class="form-check-label" for="check-${trait.getId() }-${i.index }" id="check-label-${trait.getId() }-${i.index }">
				    <c:out value="${traitVal }"></c:out>
				  </label>
				</div>
			</c:forEach>
		</c:when>
		<c:when test="${trait.getType() eq 'StringType'}">
			<c:forEach items="${stringTraitValues.get(trait.getId()) }" varStatus="i" var="traitVal">
				<div class="form-check">
				  <input class="form-check-input filter-checkbox" type="checkbox" ${(searchTerms.containsKey(trait.getId()) && searchTerms.get(trait.getId()).oneOfValues.contains(traitVal)) ? 'checked' : '' } value="" id="check-${trait.getId() }-${i.index }">
				  <label class="form-check-label" for="check-${trait.getId() }-${i.index }" id="check-label-${trait.getId() }-${i.index }">
				    <c:out value="${traitVal }"></c:out>
				  </label>
				</div>
			</c:forEach>
		</c:when>
		</c:choose>
	</div>
</c:forEach>

<button type="button" onclick="doFilter()" class="btn btn-primary">Применить</button>
</c:if>
<c:if test="${empty category }">
<p><h4>Категории</h4>

<form class="bg-light">
<c:forEach items="${menuCategories}" var="cat">
	<c:if test="${cat.isActive()}">
		<p><a href="/?categoryId=${cat.getId()}"><c:out value="${cat.getName()}"></c:out> </a>
	</c:if>
</c:forEach>
</form>
</c:if>
</div>


<form action="/" method="get" id="update-filter-form">
<c:if test="${not empty category }">
	<input type="hidden" name="categoryId" value="${category.getId() }" />
</c:if>
</form>
<script type="text/javascript">
function doFilter() {
	var terms = [];
	$('.filter-slider').each(function () {
		var data = $(this).attr('id').split('-');
		if ($('#slider-val-' + data[1]).text().split(' - ').length >= 2) {
			var minmax = $('#slider-val-' + data[1]).text().split(' - ');
			var term = {};
			term.type = "BETWEEN";
			term.traitID = data[1];
			term.minSegmentVal = minmax[0];
			term.maxSegmentVal = minmax[1];
			terms.push(term);
		}
	});
	var valChecks = {};
	$('.filter-checkbox').each(function () {
		if ($(this).prop('checked')) {
			var data = $(this).attr('id').split('-');
			if (valChecks[data[1]] == null) {
				valChecks[data[1]] = [];
			}
			valChecks[data[1]].push($('#check-label-' + data[1] + '-' + data[2]).text().trim());
		}
	});
	for (var id in valChecks) {
		var term = {}
		term['type'] = 'ONE_OF';
		term['traitID'] = id;
		term['oneOfValues'] = valChecks[id];
		terms.push(term);
	}
	$('.update-filter-term').remove();
	if ($('#min-price').val() !== '') {
		var item = $('<input type="hidden" name="minPrice">');
		item.val($('#min-price').val());
		$('#update-filter-form').append(item);
	}
	if ($('#max-price').val() !== '') {
		var item = $('<input type="hidden" name="maxPrice">');
		item.val($('#max-price').val());
		$('#update-filter-form').append(item);
	}
	for (var term in terms) {
		var item = $('<input type="hidden" name="searchTerms">');
		item.val(JSON.stringify(terms[term]));
		item.addClass('update-filter-term');
		$('#update-filter-form').append(item);
	}
	$('#update-filter-form').submit()
}
</script>

<div class="col-lg">
<c:choose>
<c:when test="${category != null }">
<h4>
<c:out value="${category.getName() }"></c:out>&nbsp;
<c:if test="${searchTerms != null}">(найдено <c:out value="${items.size() }"/> товаров)</c:if>
</h4>
</c:when>
<c:otherwise>
<h4>Все товары</h4>
</c:otherwise>
</c:choose>
<br>

<div class="row">

<c:forEach items="${items }" var="item">
    <div class="card m-2 col-sm-3">
    	<a href="/item?id=${item.getId() }">
    	<img class="card-img-top" width="200" height="200" src="/item_image?id=${item.getId() }" />
    	</a>
    	<div class="card-body">
    		<a href="/item?id=${item.getId() }"><h5 class="card-title"><c:out value="${item.getName() }"></c:out></h5></a>
    		<p class="card-text"><c:out value="${item.getPrice() }"></c:out>&nbsp;руб.</p>
    		<c:choose>
    		<c:when test="${param.addedId == item.id}">
    			<a href="/user/cart" class="btn btn-success">Добавлено</a>
    		</c:when>
    		<c:otherwise>
	    		<form method="post" action="/user/add_to_cart">
					<input type="hidden" name="count" value="1">
					<input type="hidden" name="itemId" value="${item.id }" >
					<input type="hidden" name="redir" value="${requestScope['javax.servlet.forward.request_uri'].concat('?').concat(requestScope['javax.servlet.forward.query_string']).concat('&addedId=').concat(item.id)}" >
					<input type="submit" value="В корзину!" class="btn btn-primary">
				</form>
    		</c:otherwise>
    		</c:choose>
    	</div>
    </div>
</c:forEach>

</div>
</div>
</div>
</div>

<%@ include file="footer.jspf" %>