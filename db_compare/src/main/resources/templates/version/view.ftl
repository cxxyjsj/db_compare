<div id="version_view" mid="${version.ID}">
	<div class="col-md-6" style="padding:0;">
		<div class="panel panel-default">
			<div class="panel-heading">
				${version.NAME}(${version.CREATE_DATE})
				<div class="pull-right">
			  		<a href="javascript:;" class="btn btn-info" op="view_import" style="position:absolute;right:20px;top:4px;">导入表</a>
			  	</div>
			</div>
			<div class="panel-body">
				<div class="input-group" style="margin-bottom:10px;">
                    <input id="txtSearch" type="text" class="form-control" placeholder="输入关键字回车键过滤">
                    <span class="input-group-btn">
	                    <button class="btn btn-default" type="button">
	                        <i class="fa fa-search"></i>
	                    </button>
	                </span>
                </div>
				<div id="tree">
				
				</div>
			</div>
		</div>
	</div>
	<div class="col-md-6">
		<div class="panel panel-default">
			<div class="panel-heading">
				检测备份表
				<div class="pull-right">
			  		<a href="javascript:;" class="btn btn-info" op="view_bak" style="position:absolute;right:20px;top:4px;">导出备份表</a>
			  	</div>
			</div>
			<div class="panel-body">
				<div class="input-group" style="margin-bottom:10px;">
                    <input id="txtSearch" type="text" class="form-control" placeholder="输入关键字回车键过滤">
                    <span class="input-group-btn">
	                    <button class="btn btn-default" type="button">
	                        <i class="fa fa-search"></i>
	                    </button>
	                </span>
                </div>
				
			</div>
		</div>
	</div>
</div>
<div class="template" id="import_template">
	<form>
	    <div class="form-group">
	        <label>导入策略</label>
	        <select class="form-control validate[required]" name="TYPE">
	        	<option value="1">覆盖</option>
	        	<option value="2">合并</option>
	        </select>
	    </div>
	    <div class="form-group" id="fileArea">
	    	<label>选择文件</label>
	    </div>
    </form>
    <div class="alert alert-success" role="alert">
    <strong>PLSQL导出的CSV格式文件:</strong><br/>
      	Oracle查询语句: SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE,NVL(DATA_PRECISION,DATA_LENGTH) AS DATA_LENGTH FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ?;
 <br />
    </div>
</div>
<script>
	version.viewInit();
</script>