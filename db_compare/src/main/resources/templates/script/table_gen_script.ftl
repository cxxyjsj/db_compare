<?xml version="1.0" encoding="GBK"?>
<dbVersion>
	<#list tables as table>
	<table name="${table.NAME}" desc="${table.DESC!''}">
		<#if table.cols??>
		<#list table.cols as col>
		<column name="${col.NAME}" type="${col.TYPE}" desc="${col.DESC!''}" />
		</#list>
		</#if>
	</table>
	<index name="PK_${table.NAME}" type="key" tableName="${table.NAME}">
		<column name="WID"/>
	</index>
	</#list>
</dbVersion>