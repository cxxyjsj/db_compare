<div class="alert alert-success" role="alert">
  <strong>主版本: ${srcDb.NAME}(${srcDb.CODE})</strong><br />
  <strong>目标版本: ${tarDb.NAME}(${tarDb.CODE})</strong>
</div>
<div class="row">
	<div class="col-md-4">
		<div class="panel panel-default">
		  <div class="panel-heading">
		  	主版本比目标版本多出的表
		  	<div class="pull-right">
		  		<a href="javascript:;" class="btn btn-info" op="compare_export_add" style="position:absolute;right:20px;top:4px;">导出新增表</a>
		  	</div>
		  </div>
		  <div class="panel-body">
		    <#if moreTables?size gt 0>
		    <table class="table table-condensed">
		    	<thead>
		    		<tr>
		    			<th width="30px">#</th>
		    			<th>表名</th>
		    		</tr>
		    	</thead>
		    	<tbody>
		    		<#list moreTables as t>
		    		<tr>
		    		<td>${t_index+1}</td>
		    		<td>${t}</td>
		    		</tr>
				    </#list>
		    	</tbody>
		    </table>
		    </#if>
		  </div>
		</div>
	</div>
	<div class="col-md-4">
		<div class="panel panel-default">
		  <div class="panel-heading">主版本比目标版本缺少的表</div>
		  <div class="panel-body">
		    <#if lessTables?size gt 0>
		    <table class="table table-condensed">
		    	<thead>
		    		<tr>
		    			<th width="30px">#</th>
		    			<th>表名</th>
		    		</tr>
		    	</thead>
		    	<tbody>
		    		<#list lessTables as t>
		    		<tr>
		    		<td>${t_index+1}</td>
		    		<td>${t}</td>
		    		</tr>
				    </#list>
		    	</tbody>
		    </table>
		    </#if>
		  </div>
		</div>
	</div>
	<div class="col-md-4">
		<div class="panel panel-default">
		  <div class="panel-heading">
		  	差异的表
		  	<div class="pull-right">
		  		<a href="javascript:;" class="btn btn-info" op="compare_export_modify" style="position:absolute;right:20px;top:4px;">导出变更表</a>
		  	</div>
		  </div>
		  <div class="panel-body">
		    <#if diffTables?size gt 0>
		    <table class="table table-condensed" id="diffTable" srcId="${srcId}" tarId="${tarId}">
		    	<thead>
		    		<tr>
		    			<th width="30px">#</th>
		    			<th>表名</th>
		    		</tr>
		    	</thead>
		    	<tbody>
		    		<#list diffTables as t>
		    		<tr>
		    		<td>${t_index+1}</td>
		    		<td>${t}</td>
		    		</tr>
				    </#list>
		    	</tbody>
		    </table>
		    </#if>
		  </div>
		</div>
	</div>
</div>