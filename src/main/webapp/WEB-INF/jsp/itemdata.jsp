<%@page import="com.myshop.model.Item"%>
<%@page import="java.util.Base64"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ include file="header.jspf" %>
<style>
.form-control-lg {
	font-size: 2.25rem;
}
</style>
<script type="text/javascript">
//Converts an ArrayBuffer directly to base64, without any intermediate 'convert to string then
//use window.btoa' step. According to my tests, this appears to be a faster approach:
//http://jsperf.com/encoding-xhr-image-data/5

/*
MIT LICENSE
Copyright 2011 Jon Leighton
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

function base64ArrayBuffer(arrayBuffer) {
var base64    = ''
var encodings = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'

var bytes         = new Uint8Array(arrayBuffer)
var byteLength    = bytes.byteLength
var byteRemainder = byteLength % 3
var mainLength    = byteLength - byteRemainder

var a, b, c, d
var chunk

// Main loop deals with bytes in chunks of 3
for (var i = 0; i < mainLength; i = i + 3) {
 // Combine the three bytes into a single integer
 chunk = (bytes[i] << 16) | (bytes[i + 1] << 8) | bytes[i + 2]

 // Use bitmasks to extract 6-bit segments from the triplet
 a = (chunk & 16515072) >> 18 // 16515072 = (2^6 - 1) << 18
 b = (chunk & 258048)   >> 12 // 258048   = (2^6 - 1) << 12
 c = (chunk & 4032)     >>  6 // 4032     = (2^6 - 1) << 6
 d = chunk & 63               // 63       = 2^6 - 1

 // Convert the raw binary segments to the appropriate ASCII encoding
 base64 += encodings[a] + encodings[b] + encodings[c] + encodings[d]
}

// Deal with the remaining bytes and padding
if (byteRemainder == 1) {
 chunk = bytes[mainLength]

 a = (chunk & 252) >> 2 // 252 = (2^6 - 1) << 2

 // Set the 4 least significant bits to zero
 b = (chunk & 3)   << 4 // 3   = 2^2 - 1

 base64 += encodings[a] + encodings[b] + '=='
} else if (byteRemainder == 2) {
 chunk = (bytes[mainLength] << 8) | bytes[mainLength + 1]

 a = (chunk & 64512) >> 10 // 64512 = (2^6 - 1) << 10
 b = (chunk & 1008)  >>  4 // 1008  = (2^6 - 1) << 4

 // Set the 2 least significant bits to zero
 c = (chunk & 15)    <<  2 // 15    = 2^4 - 1

 base64 += encodings[a] + encodings[b] + encodings[c] + '='
}

return base64
}
</script>
<form:form modelAttribute="item" action="/admin/update_item" method="post">
<div class="container">
	<div class="row m-3">
		<div class="col-sm-2 my-auto">
		</div>

		<div class="col-sm-10">
			<h1>
			<form:input path="name" type="text" class="form-control-lg" placeholder="Название товара" />
			</h1>
		</div>
	</div>
	<form:hidden path="id"/>
	<div class="row">
	<div class="col-sm">
	<div id="error-message-text-item" class="text-danger"><c:if test="${param.errorMessage != null}"><c:out value="${param.errorMessage }"></c:out></c:if></div><br>
	<img class="img-fluid" id="item-image-view" src="data:image/png;base64,${imageString }" />
	<input type="hidden" id="item-image" name="itemImage" value="${imageString }" />
	<div class="custom-file">
	  <input type="file" class="custom-file-input" id="customFile">
	  <label class="custom-file-label" for="customFile">Выбрать файл (PNG)</label>
	</div>
	<script>
		$('#customFile').on('change', function (evt) {
			var reader = new FileReader();
			reader.onload = function (loadedEvt) {
				var arr = loadedEvt.target.result;
				var data = base64ArrayBuffer(arr);
				$('#item-image-view').attr('src', 'data:image/png;base64,' + data);
				$('#item-image').val(data);
			}
			if (evt.target.files[0].type === 'image/png') {
				reader.readAsArrayBuffer(evt.target.files[0]);
			}
		});
	</script>
	</div>
	<div class="col-sm">
	<div class="form-group">
	Категория:
	<form:select path="category.id">
		<form:options items="${menuCategories }" itemValue="id" itemLabel="name"/>
	</form:select>
	</div>
	
	<div class="form-group">
		<label for="count-input">Количество</label>
		<form:input id="count-input" path="count" type="number" />
	</div>
	<div class="form-group">
		<label for="price-input">Цена</label>
		<form:input id="price-input" path="price" type="number" />
	</div>
	<div class="form-group">
		<label for="price-checkbox">Доступен в магазине</label>
		<form:checkbox path="active" id="price-checkbox"/>
	</div>
	<div class="form-group">
		<form:textarea id="desc-area" path="description" placeholder="Описание"/>
	</div>
	<div class="form-group">
		<form:button class="btn btn-success" id="update-item-button">Обновить</form:button>
	</div>
	<div class="form-group">
		<button type="button" class="btn btn-primary" onclick="editTraits()">Редактировать свойства</button>
	</div>
	<div class="form-group">
		<a id="view-item-link" href="/item?id=${item.id }">Просмотр</a>
	</div>
	</div>
	</div>
</div>
</form:form>

<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">Свойства товара</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div id="modal-content"></div>
    </div>
  </div>
</div>
<script type="text/javascript">
function editTraits() {
	$('#modal-content').load('/admin/edit_item_traits?id=' + ${item.id}, null, 
	function(data, status, jqXGR) {
		if (status == "success") {
			$('#exampleModal').modal('show');
		}
	});
}
</script>

<%@ include file="footer.jspf" %>