<div id="escape_container">
	<div class="row">
		<div class="panel panel-default">
	        <div class="panel-heading">
	            SQL脚本转义
	            <div class="pull-right">
			  		<a href="javascript:;" class="btn btn-info" id="escapeBtn" style="position:relative;bottom:7px;">转换</a>
			  	</div>
	        </div>
	        <div class="panel-body">
	            <div class="col-md-12">
	            	<p>请输入insert SQL脚本,分号结尾</p>
	            	<textarea id="src" class="form-control" rows="15"></textarea>
	            </div>
	            <div class="col-md-12">
	            	<p>转换后脚本</p>
	            	<textarea id="target" class="form-control" rows="15"></textarea>
	            </div>
	        </div>
	    </div>
    </div>
</div>
<script>
	escape.init();
</script>