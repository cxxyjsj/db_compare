<div id="escape_container">
	<div class="row">
		<div class="panel panel-default">
	        <div class="panel-heading">
	            <font color="red">
	            <#if db??>
	            当前数据库: ${db.NAME}(${db.CODE})
	            <#else>
	            请先选择数据库
	            </#if>
	            </font>
	            <div class="pull-right">
	            	<a href="javascript:;" class="btn btn-info" id="dbBtn" style="position:relative;bottom:7px;">切换数据库</a>
			  		<a href="javascript:;" class="btn btn-primary" id="genBtn" style="position:relative;bottom:7px;">生成</a>
			  	</div>
	        </div>
	        <div class="panel-body">
	            <div class="col-md-12">
	            	<form>
					    <div class="form-group">
					        <label>请输入表名匹配模式(例如：T_JZG_%)</label>
					        <input class="form-control" id="tableNameStr">
					    </div>
					    <div class="form-group">
					        <label>输出脚本</label>
					        <textarea id="target" class="form-control" rows="30"></textarea>
					    </div>
					</form>
	            </div>
	        </div>
	    </div>
    </div>
</div>
<div class="template" id="db_template">
	<div class="form-group">
        <label>请选择数据库</label>
        <select class="form-control" name="ID">
        <#list dbs as d>
        <option value="${d.ID}" <#if (db??) && (db.ID == d.ID)>selected="selected"</#if> >${d.NAME}(${d.CODE})</option>
        </#list>
        </select>
    </div>
</div>
<script>
	escape.init();
</script>