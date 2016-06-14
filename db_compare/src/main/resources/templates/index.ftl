<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link href="/bs/css/bootstrap.min.css" rel="stylesheet">
	<link href="/ve/css/ve.css" rel="stylesheet">
	<link href="/showLoading/css/showLoading.css" rel="stylesheet">
	<link href="/custom.css" rel="stylesheet">
	<link href="/admin/css/sb-admin-2.css" rel="stylesheet">
	<link href="/fa/css/font-awesome.min.css" rel="stylesheet">
	<link href="/uploadify/uploadify.css" rel="stylesheet">
	<link href="/jstree/themes/default/style.min.css" rel="stylesheet">
    <title>数据库结构对比工具V1.0</title>
  </head>
  <body>
  	<div id="wrapper">
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="javascript:;">数据库结构对比工具V1.0</a>
            </div>

            <div class="navbar-default sidebar" role="navigation">
                <div class="sidebar-nav navbar-collapse">
                    <ul class="nav in" id="side-menu">
                    	<li>
                            <a href="#db"><i class="fa fa-wrench fa-fw"></i>数据库配置</a>
                        </li>
                        <li>
                            <a href="#version"><i class="fa fa-dashboard fa-fw"></i>版本生成</a>
                        </li>
                        <li>
                            <a href="#compare"><i class="fa fa-bar-chart-o fa-fw"></i>数据库比较</a>
                        </li>
                        <li>
                            <a href="#app"><i class="fa fa-windows fa-fw"></i>应用维护</a>
                        </li>
                        <li>
                            <a href="#data"><i class="fa fa-save fa-fw"></i>数据监控</a>
                            <ul class="nav nav-second-level">
                                <li>
                                    <a href="#escape">脚本转义</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <div id="page-wrapper" style="min-height: 404px;">
            
        </div>
    </div>
  </body>
  <script>
  	var basePath = "${basePath}";
  </script>
  <script src="/jquery/jquery.min.js"></script>
  <script src="/bs/js/bootstrap.min.js"></script>
  <script src="/admin/js/sb-admin-2.js"></script>
  <script src="/layer/layer.js"></script>
  <script src="/ve/ve.js"></script>
  <script src="/ve/ve-zh_CN.js"></script>
  <script src="/showLoading/js/jquery.showLoading.js"></script>
  <script src="/uploadify/jquery.uploadify.js"></script>
  <script src="/jstree/jstree.min.js"></script>
  <script src="/plugins/director.min.js"></script>
  <script src="/plugins/jquery.cookie.js"></script>
  <script src="/plugins/jquery.md5.js"></script>
  <script src="/plugins/json2.js"></script>
  <script src="/custom.js"></script>
  <script src="/index.js"></script>
</html>