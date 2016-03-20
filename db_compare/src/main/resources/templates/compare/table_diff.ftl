<div class="col-md-12">
	<div class="panel panel-default">
	  <div class="panel-heading">主版本与目标版本差异</div>
	  <div class="panel-body">
	    <table class="table table-bordered" id="dbTable">
		  <thead>
		    <tr>
		      <th>字段名称(主|从)</th>
		      <th>字段类型(主|从)</th>
		      <th>字段大小(主|从)</th>
		    </tr>
		  </thead>
		  <tbody>
		  	<#if cols?size gt 0>
		  	<#list cols as col>
		  	<tr>
		  	  <td <#if col.SRC_NAME ne col.TAR_NAME>class="red"</#if>>${col.SRC_NAME}|${col.TAR_NAME}</td>
		  	  <td <#if col.SRC_TYPE ne col.TAR_TYPE>class="red"</#if>>${col.SRC_TYPE}|${col.TAR_TYPE}</td>
		  	  <td <#if col.SRC_SIZE ne col.TAR_SIZE>class="red"</#if>>${col.SRC_SIZE}|${col.TAR_SIZE}</td>
		  	</tr>
		  	</#list>
		  	</#if>
		  </tbody>
		</table>
	  </div>
	</div>
</div>