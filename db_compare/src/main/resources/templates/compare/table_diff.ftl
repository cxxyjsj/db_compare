<table class="table table-bordered" id="dbTable">
  <thead>
    <tr>
      <th>主表</th>
      <th>从表</th>
    </tr>
  </thead>
  <tbody>
  	<#if cols?? && cols?size gt 0>
  	<#list cols as col>
  	<tr>
  	  <td>
  	  	<#if col.SRC_NAME??>
  	  	${col.SRC_NAME} - ${col.SRC_TYPE} - ${col.SRC_SIZE}
  	  	<#else>
  	  	无
  	  	</#if>
  	  </td>
  	  <td>
  	  	<#if col.TAR_NAME??>
  	  	${col.TAR_NAME} - ${col.TAR_TYPE} - ${col.TAR_SIZE}
  	  	<#else>
  	  	无
  	  	</#if>
  	  </td>
  	</tr>
  	</#list>
  	<#else>
  	<tr>
  	  <td colspan="2" style="text-align:center;">未查询到数据...</td>
  	</tr>
  	</#if>
  </tbody>
</table>