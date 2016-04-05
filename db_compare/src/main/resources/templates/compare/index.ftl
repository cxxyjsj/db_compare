<div id="compare_container">
<div class="btn-tools">
	<a href="javascript:;" class="btn btn-primary" op="compare_start">开始比较</a>
</div>
<div id="result">

</div>
<div class="template" id="compare_template">
	<form>
		<div class="form-group">
	        <label>选择主版本(一般为开发数据库)</label>
	        <select class="form-control validate[required]" name="SRC_ID">
	        	<#if versions?size gt 0>
	        	<#list versions as v>
	        	<option value="${v.ID}">${v.CODE}(${v.NAME}) - [${v.CREATE_DATE}]</option>
	        	</#list>
	        	</#if>
	        </select>
	    </div>
	    <div class="form-group">
	        <label>选择目标版本(一般为现场数据库)</label>
	        <select class="form-control validate[required]" name="TAR_ID">
	        	<#if versions?size gt 0>
	        	<#list versions as v>
	        	<option value="${v.ID}" <#if v_index == 1>selected="selected"</#if>>${v.CODE}(${v.NAME}) - [${v.CREATE_DATE}]</option>
	        	</#list>
	        	</#if>
	        </select>
	    </div>
	    <div class="form-group">
	    	<label>过滤条件(支持表名的右模糊匹配,区分大小写)</label>
	    	<input type="text" class="form-control" name="CONDITION" placeholder="例如: @tableName NOT LIKE 'T_ZXBZ_%'">
	    </div>
    </form>
</div>
<script>
	compare.init();
</script>