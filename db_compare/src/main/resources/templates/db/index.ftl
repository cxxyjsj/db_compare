<div class="btn-tools">
	<a href="javascript:;" class="btn btn-primary" name="add">新增</a>
</div>
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
      <td>${db.CODE}</th>
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