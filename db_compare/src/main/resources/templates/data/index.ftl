<div id="data_container">
	<div class="btn-tools">
		<a href="javascript:;" class="btn btn-primary" op="data_add">新增</a>
	</div>
	<table class="table table-bordered" id="dbTable">
	  <thead>
	    <tr>
	      <th>应用名称</th>
	      <th>数据表名</th>
	      <th>操作</th>
	    </tr>
	  </thead>
	  <tbody>
	  	<#if tables?size gt 0>
	  	<#list tables as table>
	  	<tr mid="${table.TABLE_NAME}">
	      <td>${table.APP_NAME}</th>
	      <td>${table.TABLE_NAME}</td>
	      <td>
	      	<a href="javascript:;" class="btn btn-sm btn-primary" op="db_test">测试连接</a>
	      	<a href="javascript:;" class="btn btn-sm btn-info" op="db_edit">修改</a>
	      	<a href="javascript:;" class="btn btn-sm btn-danger" op="db_del">删除</a>
	      </td>
	    </tr>
	  	</#list>
	  	<#else>
	  	<tr>
	  		<td colspan="4" style="text-align:center;">暂无数据...</td>
	  	</tr>
	  	</#if>
	  </tbody>
	</table>
</div>