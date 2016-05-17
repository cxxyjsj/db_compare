<div id="version_view" mid="${version.ID}">
	<div class="col-md-6" style="padding:0;">
		<div class="panel panel-default">
			<div class="panel-heading">
				${version.NAME}(${version.CREATE_DATE})
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
<script>
	version.viewInit();
</script>