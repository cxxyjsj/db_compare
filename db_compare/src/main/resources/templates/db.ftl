<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Bootstrap 101 Template</title>
    <link href="/bs/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body>
  	<div class="container">
  		<div class="row">
  			<div class="col-md-12">
  				<table class="table table-bordered">
			      <thead>
			        <tr>
			          <th>数据库编码</th>
			          <th>数据库名称</th>
			          <th>数据库类型</th>
			          <th>操作</th>
			        </tr>
			      </thead>
			      <tbody>
			      	<#list dbs as db>
			      	<tr>
			          <th>${db.CODE}</th>
			          <td>${db.NAME}</td>
			          <td>${db.TYPE}</td>
			          <td>
			          	<a href="javascript:;" class="btn btn-sm btn-info" name="edit">修改</a>
			          	<a href="javascript:;" class="btn btn-sm btn-danger" name="del">删除</a>
			          </td>
			        </tr>
			      	</#list>
			      </tbody>
			    </table>
  			</div>
  		</div>
  	</div>

    <script src="/jquery/jquery.min.js"></script>
    <script src="/bs/js/bootstrap.min.js"></script>
  </body>
</html>