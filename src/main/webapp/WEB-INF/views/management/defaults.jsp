<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<div id="defaultEditMatrix" style="display: none">
	
	<table>
		<tr>
			<td>	
				<div class="dialogscroller" style="max-width: 100%; width: 100%; max-height: 100%; overflow: auto;">
					<table class="matrixtable">
						<tr>
							<td></td>
							<td class="matrix-header" onclick="editText(this);">Answer</td>
							<td class="matrix-header" onclick="editText(this);">Answer</td>
							<td class="matrix-header" onclick="editText(this);">Answer</td>
						</tr>
						<tr>
							<td onclick="editText(this);">Question</td>
							<td class="matrix-cell"><input type="checkbox" /></td>
							<td class="matrix-cell"><input type="checkbox" /></td>
							<td class="matrix-cell"><input type="checkbox" /></td>
						</tr>
						<tr>
							<td onclick="editText(this);">Question</td>
							<td class="matrix-cell"><input type="checkbox" /></td>
							<td class="matrix-cell"><input type="checkbox" /></td>
							<td class="matrix-cell"><input type="checkbox" /></td>
						</tr>
						<tr>
							<td onclick="editText(this);">Question</td>
							<td class="matrix-cell"><input type="checkbox" /></td>
							<td class="matrix-cell"><input type="checkbox" /></td>
							<td class="matrix-cell"><input type="checkbox" /></td>
						</tr>
					</table>	
				</div>			
			</td>
			<td>
				<a id="btnAddColumnMatrix" class="btn btn-sm btn-default" style="margin-bottom: 5px;" onclick="addMatrixColumn();"><span class="glyphicon glyphicon-plus-sign"></span></a><br />
				<a id="btnRemoveColumnMatrix" class="btn btn-sm btn-default" onclick="removeLastMatrixColumn()"><span class="glyphicon glyphicon-minus-sign"></span></a>
			</td>
		</tr>
		<tr>
			<td colspan="2" style="text-align: center">
				<a id="btnAddRowMatrix" class="btn btn-sm btn-default" onclick="addMatrixRow();"><span class="glyphicon glyphicon-plus-sign"></span></a>
				<a id="btnRemoveRowMatrix" class="btn btn-sm btn-default" onclick="removeLastMatrixRow()"><span class="glyphicon glyphicon-minus-sign"></span></a>
			</td>
		</tr>
	</table>	

</div>

<div id="defaultEditTable" style="display: none">
	
	<table>
		<tr>
			<td>	
				<table class="tabletable" style="width: 900px;">
					<tr style="background-color: #eee">
						<td></td>
						<td class="editabletableCell">A</td>
						<td class="editabletableCell">B</td>
						<td class="editabletableCell">C</td>
						<td class="editabletableCell">D</td>
						<td class="editabletableCell">E</td>
					</tr>
					<tr>
						<td class="editabletableCell" style="background-color: #eee">1</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
					</tr>
					<tr>
						<td class="editabletableCell" style="background-color: #eee">2</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
					</tr>
					<tr>
						<td class="editabletableCell" style="background-color: #eee">3</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
						<td>&#160;</td>
					</tr>
				</table>		
			</td>
			<td>
				<a class="btn btn-sm btn-default" style="margin-bottom: 5px;" onclick="addTableColumn();"><span class="glyphicon glyphicon-plus-sign"></span></a><br />
				<a class="btn btn-sm btn-default" onclick="removeLastTableColumn()"><span class="glyphicon glyphicon-minus-sign"></span></a>
			</td>
		</tr>
		<tr>
			<td colspan="2" style="text-align: center">
				<a class="btn btn-sm btn-default" onclick="addTableRow();"><span class="glyphicon glyphicon-plus-sign"></span></a>
				<a class="btn btn-sm btn-default" onclick="removeLastTableRow()"><span class="glyphicon glyphicon-minus-sign"></span></a>
			</td>
		</tr>
	</table>	

</div>

<div id="default-gallery-table" style="display: none">
	<table class="gallery-table default">				
		<tbody>
			<tr>
				<td>			
					<img src="${contextpath}/resources/images/gallery1.jpg" data-width="500" width="246px" style="width:246px">													
				</td>		
				<td>			
					<img src="${contextpath}/resources/images/gallery2.jpg" data-width="500" width="246px" style="width:246px">													
				</td>
				<td>			
					<img src="${contextpath}/resources/images/gallery3.jpg" data-width="500" width="246px" style="width:246px">													
				</td>				
			</tr>
			<tr>
				<td>			
					<img src="${contextpath}/resources/images/gallery4.jpg" data-width="500" width="246px" style="width:246px">													
				</td>		
				<td>			
					<img src="${contextpath}/resources/images/gallery5.jpg" data-width="500" width="246px" style="width:246px">													
				</td>
				<td>			
					<img src="${contextpath}/resources/images/gallery6.jpg" data-width="500" width="246px" style="width:246px">													
				</td>				
			</tr>
		</tbody>
	</table>
</div>

<div class="modal" id="editTextDialog" data-backdrop="static" style="width: auto;">
	<div class="modal-dialog">
    <div class="modal-content">
	<div class="modal-header">
		<span id="defaulttext"><spring:message code="label.EditText" /></span>
		<span id="matrixheader"><spring:message code="label.EditHeaderText" /></span>
	</div>
	<div class="modal-body">
		<ul id="editTextDialogTabs" class="nav nav-tabs">
			<li class="active"><a href="#edittextgeneral" data-toggle="tab"><spring:message code="label.General" /></a></li>
			<li><a href="#edittextadvanced" data-toggle="tab"><spring:message code="label.Advanced" /></a></li>
		</ul>
		
		<div class="tab-content">
			<div class="tab-pane active" id="edittextgeneral">
				<span class="mandatory">*</span><spring:message code="label.Text" /><br />
				<div id="editTextDialog-dialog-title-invalid" class="validation-error-keep hideme"><spring:message code="label.InvalidXHTML" /></div>
				<div id="editTextDialog-dialog-title-empty" class="validation-error-keep hideme"><spring:message code="validation.required" /></div>
				<textarea id="edit-text-dialog-text" class="tinymcealign"></textarea>
				<div id="editTextDialogLongdesc" class="hideme" style="margin-top: 10px">
					<spring:message code="label.LongDesc" /><br />
					<input type="text" id="edit-text-dialog-longdesc"></input>
					<div id="editTextDialog-dialog-longdesc-invalid" class="validation-error-keep hideme"><spring:message code="validation.invalidURL" /></div>
				</div>	
			</div>
			<div class="tab-pane" id="edittextadvanced" style="width: 510px; height: 150px;">
				<div id="editTextDialogIdentifier" class="hideme">
					<span class="mandatory">*</span><spring:message code="label.Identifier" /><br />
					<input type="text" id="edit-text-dialog-shortname"></input>			
					<span id="edit-text-dialog-shortname-empty" class="validation-error-keep hideme"><spring:message code="validation.required" /></span>
				</div>	
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a id="btnOkFromEditTextDialog" onclick="updateTextElement();"  class="btn btn-primary"><spring:message code="label.OK" /></a>
		<a  class="btn btn-default" onclick="cancelTextElement()"><spring:message code="label.Cancel" /></a>
	 </div>
	 </div>
	 </div>
</div>