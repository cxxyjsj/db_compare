<div class="row">
	<div class="col-md-4">
		<div class="panel panel-default">
		  <div class="panel-heading">主版本比目标版本多出的表名</div>
		  <div class="panel-body">
		    <#if moreTables?size gt 0>
		    <#list moreTables as t>
		    <span class="label label-primary">${t}</span>
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
		    <span class="label label-info">${t}</span>
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
		    <span class="label label-danger">${t}</span>
		    </#list>
		    </#if>
		  </div>
		</div>
	</div>
</div>
<div class="row">
	<div class="col-md-12">
		<div class="panel panel-default">
		  <div class="panel-heading">主版本与目标版本差异</div>
		  <div class="panel-body">
		    
		  </div>
		</div>
	</div>
</div>