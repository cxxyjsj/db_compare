<div id="app_container">
	<div class="col-md-6" style="padding:0;">
		<div class="panel panel-default">
			<div class="panel-heading">
				应用目录
			</div>
			<div class="panel-body">
				<div class="input-group" style="margin-bottom:10px;">
                    <input id="appSearch" type="text" class="form-control" placeholder="输入关键字回车键过滤">
                    <span class="input-group-btn">
	                    <button class="btn btn-default" type="button">
	                        <i class="fa fa-search"></i>
	                    </button>
	                </span>
                </div>
				<div id="app_tree">
				
				</div>
			</div>
		</div>
	</div>
	<div id="app_form" style="display:none;">
	<form>
		<div class="form-group">
	        <label>应用ID</label>
	        <input type="text" class="form-control validate[required]" name="NAME">
	    </div>
	    <div class="form-group">
	        <label>应用名称</label>
	        <input type="text" class="form-control validate[required]" name="TITLE">
	    </div>
    </form>
    </div>
</div>
<script>
	app.init();
</script>