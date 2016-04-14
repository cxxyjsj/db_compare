<form>
	<input type="hidden" name="ID" value="${(data.ID)!}">
	<#if db_conf??>
	<input type="hidden" id="db_conf" value='${db_conf!}'>
	</#if>
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
        	<option></option>
        	<option value="oracle" <#if ((data.TYPE)! == 'oracle')>selected="selected"</#if>>Oracle</option>
        	<option value="mysql" <#if ((data.TYPE)! == 'mysql')>selected="selected"</#if>>MySQL</option>
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