<div class="row">
	<div class="col-md-4">
		<div class="panel panel-default">
		  <div class="panel-heading">主版本比目标版本多出的表名</div>
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
		  <div class="panel-heading">主版本比目标版本缺少的表名</div>
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
		  <div class="panel-heading">主版本比目标版本差异的表名</div>
		  <div class="panel-body" srcId="${srcId}" tarId="${tarId}">
		    <#if diffTables?size gt 0>
		    <#list diffTables as t>
		    <table class="table table-condensed">
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
		    </#list>
		    </#if>
		  </div>
		</div>
	</div>
</div>
<div class="row" id="diff">
</div>