<form>
	<input type="hidden" name="ID" value="${(data.ID)!}">
	<div class="form-group">
        <label>数据库编码</label>
        <input class="form-control validate[required]" name="CODE" value="${(data.CODE)!}">
    </div>
    <div class="form-group">
        <label>数据库名称</label>
        <input class="form-control validate[required]" name="NAME" value="${(data.NAME)!}">
    </div>
    <div class="form-group">
        <label>数据库类型</label>
        <select class="form-control" name="TYPE">
        	<option value="oracle">Oracle</option>
        </select>
    </div>
    <div class="form-group">
        <label>数据库驱动</label>
        <input class="form-control validate[required]" name="DRIVER" value="${(data.DRIVER)!}">
    </div>
    <div class="form-group">
        <label>数据库URL</label>
        <input class="form-control validate[required]" name="URL" value="${(data.URL)!}">
    </div>
    <div class="form-group">
        <label>数据库账号</label>
        <input class="form-control" name="USERNAME" value="${(data.USERNAME)!}">
    </div>
    <div class="form-group">
        <label>数据库密码</label>
        <input class="form-control" type="password" name="PASSWORD" value="${(data.PASSWORD)!}">
    </div>
</form>