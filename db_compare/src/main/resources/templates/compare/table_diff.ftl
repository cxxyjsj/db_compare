<table class="table table-bordered" id="dbTable">
  <thead>
    <tr>
      <th>字段名称(主|从)</th>
      <th>字段类型(主|从)</th>
      <th>字段大小(主|从)</th>
    </tr>
  </thead>
  <tbody>
  	<#if cols?? && cols?size gt 0>
  	<#list cols as col>
  	<tr>
  	  <td>${col.SRC_NAME!}|${col.TAR_NAME!}</td>
  	  <td>${col.SRC_TYPE!}|${col.TAR_TYPE!}</td>
  	  <td>${col.SRC_SIZE!}|${col.TAR_SIZE!}</td>
  	</tr>
  	</#list>
  	<#else>
  	<tr>
  	  <td colspan="3" style="text-align:center;">未查询到数据...</td>
  	</tr>
  	</#if>
  </tbody>
</table>