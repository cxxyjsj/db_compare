/**
 * 数据库配置脚本
 */
var db = {
	init : function(){
		$("#db_container").on("click","[op^='db_']",function(){
			var op = $(this).attr("op");
			var mid = $(this).closest("tr").attr("mid");
			switch(op){
			case 'db_edit' : 
				db.edit(mid);
				break;
			case 'db_del' : 
				db.del(mid);
				break;
			case 'db_add' : 
				db.edit();
				break;
			}
		});
	},
	del : function(id){
		if(!id){
			return;
		}
		$.confirm("确定删除吗?",function(){
			$.post(basePath + "/db/del/" + id, function(resp){
				if(resp.success){
					$.msg("删除成功",function(){
						location.reload();
					});
				}else{
					$.alert(resp.msg || "删除失败");
				}
			})
		});
	},
	edit : function(id){
		$.post(basePath + "/db/edit",{ID : id || ''},function(html){
			if(html){
				$.dialog({
					title : '数据库信息',
					content : html,
					beforeShow : function(){
						this.find("form").validationEngine();
					},
					callback : function(op){
						var $dialog = this;
						var $form = this.find("form");
						if(op == 'ok'){
							if(!$form.validationEngine("validate")){
								return false;
							}
							$.post(basePath + "/db/save",$form.serialize(),function(resp){
								if(resp.success){
									$.msg("保存成功",function(){
										location.reload();
									});
								}else{
									$.alert(resp.msg || "保存失败");
								}
							});
							return false;
						}
					}
				});
			}
		});
	}
}

$(function(){
	$.initRouter($("#page-wrapper"));
});