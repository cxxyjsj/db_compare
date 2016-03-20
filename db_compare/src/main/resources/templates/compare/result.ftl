<div class="row">
	<div class="col-md-4">
		<div class="panel panel-default">
		  <div class="panel-heading">主版本比目标版本多出的表名</div>
		  <div class="panel-body">
		    <#if moreTables?size gt 0>
		    <#list moreTables as t>
		     <button type="button" class="btn btn-primary btn-xs">${t}</button>
		    </#list>
		    </#if>
		  </div>
		</div>
	</div>
	<div class="col-md-4">
		<div class="panel panel-default">
		  <div class="panel-heading">主版本比目标版本缺少的表名</div>
		  <div class="panel-body">
		    <#if lessTables?size gt 0>
		    <#list lessTables as t>
		    <button type="button" class="btn btn-info btn-xs">${t}</button>
		    </#list>
		    </#if>
		  </div>
		</div>
	</div>
	<div class="col-md-4">
		<div class="panel panel-default">
		  <div class="panel-heading">主版本比目标版本差异的表名</div>
		  <div class="panel-body">
		    <#if diffTables?size gt 0>
		    <#list diffTables as t>
		    <button type="button" class="btn btn-danger btn-xs">${t}</button>
		    </#list>
		    </#if>
		  </div>
		</div>
	</div>
</div>
<div class="row" id="diff">
</div>