<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<form method="post" id="edit-item-traits-form" action="/admin/edit_item_traits">
<div class="modal-body form-group">
<c:if test="${param.errorMessage != null }">
	<span class="text-danger"><c:out value="${param.errorMessage }"></c:out> </span>
</c:if>
	<c:forEach items="${catTraits }" var="trait" varStatus="i">
	<div class="traitItem" class="form-group">
		<input type="hidden" class="itemID" name="itemTraits[${i.index }].id.itemID" value="${itemId }" />
		<input type="hidden" class="traitID" name="itemTraits[${i.index }].id.traitID" value="${trait.trait.id }" />
		<input type="hidden" class="traitType" value="${trait.trait.type }" />
		<label for="itemTraits[${i.index }]"><c:out value="${trait.trait.name }"></c:out></label>
		<c:choose>
		<c:when test="${trait.trait.type eq 'StringType' }">
			<input type="text" class="value" placeholder="Строка" id="itemTraits[${i.index }]" name="itemTraits[${i.index }].value" value="${itemTraits.containsKey(trait.trait.id) ? itRepo.getValue(itemTraits.get(trait.trait.id), tRepo) : ''}" />
		</c:when>
		<c:when test="${trait.trait.type eq 'IntType' }">
			<input type="number" class="valueInt" placeholder="Число от ${trait.trait.minValue} до ${trait.trait.maxValue}" name="itemTraits[${i.index }].valueInt" value="${itemTraits.containsKey(trait.trait.id) ? itemTraits.get(trait.trait.id).valueInt : ''}" />
		</c:when>
		<c:when test="${trait.trait.type eq 'EnumType' }">
			<c:forEach items="${trait.trait.values }" var="enumVal" varStatus="j">
				<div class="form-check">
				<input class="form-check-input valueEnum" type="radio" name="itemTraits[${i.index }].value" id="itemTraits[${i.index }].value-${j.index}" value="${fn:escapeXml(enumVal)}" ${itemTraits.containsKey(trait.trait.id) ? (itemTraits.get(trait.trait.id).value eq enumVal ? 'checked' : '') : ''} />
				<label class="form-check-label" for="itemTraits[${i.index }].value-${j.index}">
					<c:out value="${enumVal }"></c:out>
				</label>
				</div>
			</c:forEach>
		</c:when>
		</c:choose>
	</div>
	</c:forEach>
</div>
<div class="modal-footer">
	<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
	<input type="button" class="btn btn-primary" onclick="updateTraitValues()" value="Обновить" />
</div>
</form>
<form id="submit-form" method="post" action="/admin/edit_item_traits">
	<input type="hidden" id="item-traits-array" name="itemTraits" />
</form>
<script type="text/javascript">
function updateTraitValues() {
	var res = [];
	$('.traitItem').each(function (index, item) {
		var trait = {};
		trait.itemTrait = {};
		trait.itemTrait.itemID = $(item).find('.itemID').val();
		trait.itemTrait.traitID = $(item).find('.traitID').val();
		var traitType = $(item).find('.traitType').val();
		if (traitType === 'EnumType') {
			trait.value = $(item).find('.valueEnum:checked').val();
		} else if (traitType == 'StringType') {
			trait.value = $(item).find('.value').val();
		} else if (traitType === 'IntType') {
			valInt = $(item).find('.valueInt').val();
			trait.valueInt = (valInt == '' ? null : valInt);
		}
		res.push(trait);
	});
	$('#item-traits-array').val(JSON.stringify(res));
	$('#submit-form').submit();
}
</script>