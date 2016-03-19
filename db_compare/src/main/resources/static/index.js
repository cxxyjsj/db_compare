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

/**
 * 版本配置脚本
 */
var version = {
	init : function(){
		$("#version_container").on("click","[op^='version_']",function(){
			var op = $(this).attr("op");
			var mid = $(this).closest("tr").attr("mid");
			switch(op){
			case 'version_view' : 
				version.view(mid);
				break;
			case 'version_del' : 
				version.del(mid);
				break;
			case 'version_add' : 
				version.add();
				break;
			}
		});
	},
	add : function(){
		$.dialog({
			title : '新增数据库版本',
			content : $("#version_template").html(),
			beforeShow : function(){
				this.find("form").validationEngine();
			},
			callback : function(op){
				if(op == "ok"){
					var $form = this.find("form");
					if(!$form.validationEngine("validate")){
						return false;
					}
					$("body").showLoading();
					$.post(basePath + "/version/add",$form.serialize(),function(resp){
						$("body").hideLoading();
						if(resp.success){
							$.msg("创建成功",function(){
								location.reload();
							});
						}else{
							$.alert(resp.msg || "创建失败");
						}
					});
					return false;
				}
			}
		});
	},
	del : function(id){
		
	},
	view : function(id){
		
	}
}

$(function(){
	$.initRouter($("#page-wrapper"));
});