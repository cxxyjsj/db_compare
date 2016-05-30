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
			case 'db_test' :
				db.test(mid);
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
						if(!id){
							var mapping = $.toJson($("#db_conf",this).val());
							var $dialog = this;
							$("[name='TYPE']",this).change(function(){
								var type = $(this).val();
								var $driver = $("[name='DRIVER']",$dialog);
								var $url = $("[name='URL']",$dialog);
								$driver.val(mapping[type + ".driver"] || '');
								$url.val(mapping[type + ".url"] || '');
							});
						}
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
	},
	test : function(id){
		$.post(basePath + "/db/test/" + id,function(resp){
			if(resp.success){
				$.alert("连接成功");
			}else{
				$.alert("连接失败");
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
			case 'version_import' :
				version.import();
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
		$.confirm("确定删除该版本吗?",function(){
			$.post(basePath + "/version/del/" + id,function(resp){
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
	view : function(id){
		location.href = "#" + basePath + "/version/view/" + id;
	},
	viewInit : function(){
		var vId = $("#version_view").attr("mid");
		$('#tree').jstree({
			core : {
				data : function(node,cb){
					$.post(basePath + "/version/view/" + vId + "/tree",{id : node.id},function(resp){
						cb(resp);
					});
				}
			},
			plugins : ["contextmenu"],
			contextmenu : {
				items : function(node,cb){
					var actions = [];
					if(node.id == "_ROOT"){
						
					}else if(node.parents.length < 3){
						actions.push({
							label : "生成脚本",
							icon : "fa fa-leaf",
							action : version.genAppScript
						});
					}else{
						actions.push({
							label : "生成脚本",
							icon : "fa fa-leaf",
							action : version.genTableScript
						});
						actions.push({
							label : "更改分组",
							icon : "fa fa-cogs",
							action : version.changeGroup
						});
					}
					cb(actions);
				}
			}
		});
		$("#txtSearch").keydown(function(e){
			if(e.keyCode==13){
			   var $tree = $("#tree");
			   var text = $(this).val();
			   if(!text){
				   // 显示所有节点
				   $tree.jstree("show_all");
				   var tmps = $tree.jstree("get_text","_ROOT").split("(");
				   $tree.jstree("set_text","_ROOT",tmps[0]);
			   }else{
				   // 显示匹配的节点
				   var data = $tree.data("jstree")._model.data;
				   text = text.toLowerCase();
				   for(var name in data){
					   if(name == "#" || name == "_ROOT"){
						   continue;
					   }
					   var node = data[name];
					   if(name.toLowerCase().indexOf(text) >= 0){
						   $tree.jstree("show_node",name,true);
						   if(node.parent){
							   $tree.jstree("show_node",node.parent,true);
						   }
					   }else{
						   $tree.jstree("hide_node",name,true);
					   }
				   }
				   $tree.jstree("redraw",true);
			   }
			}
		});
		$("[op='view_import']").click(function(){
			// 导入表
			var vId = $("#version_view").attr("mid");
			$.dialog({
				title : '导入数据表',
				large : true,
				content : $("#import_template").html(),
				beforeShow : function(){
					var $dialog = this;
					var $file = $("<input type='file' name='file' id='file'>");
					$dialog.find("#fileArea").append($file);
					$file.uploadify({
						auto          : false,
						multi         : false,
						uploadLimit   : 1,
						fileTypeExts  : "*.txt;*.csv",
						fileTypeDesc  : "文本文件",
						height        : 30,
						buttonText    : '选择文件',
						fileObjName   : 'file',
						swf           : '/uploadify/uploadify.swf',
						uploader      : basePath + '/version/uploadTable',
						width         : 120,
						onUploadStart : function(file){
							var data = {
								'vId' : vId,
								'type' : $dialog.find("[name='TYPE']").val()
							}
							$file.uploadify("settings", "formData", data);
						},
						onUploadSuccess : function(){
							$.msg("上传成功",function(){
								location.reload();
							});
						}
					});
				},
				callback : function(op){
					if(op == "ok"){
						var $file = this.find("#file");
						var $uploadify = $file.data("uploadify");
						if($uploadify.queueData.filesQueued < 1){
							$.alert("请选择文件");
							return false;
						}
						$("body").showLoading();
						this.find("#file").uploadify("upload");
						return false;
					}
				}
			});
		});
	},
	genAppScript : function(menu){
		var $tree = $("#tree");
		var node = $tree.jstree("get_node",menu.reference);
		if(node){
			var appId = node.id;
			var vid = $("#version_view").attr("mid");
			window.open(basePath + "/gen_app_script/" + vid + "/" + appId);
		}
	},
	genTableScript : function(menu){
		var $tree = $("#tree");
		var node = $tree.jstree("get_node",menu.reference);
		if(node){
			var tableName = node.id;
			var vid = $("#version_view").attr("mid");
			window.open(basePath + "/gen_table_script/" + vid + "/" + tableName);
		}
	},
	changeGroup : function(menu){
		var $tree = $("#tree");
		var node = $tree.jstree("get_node",menu.reference);
		if(node){
			var tableName = node.id;
			var curAppId = node.parent;
			var appIds = $tree.jstree("get_node","_ROOT").children;
			var html = '<select class="form-control">';
			for(var i=0;i<appIds.length;i++){
				if(appIds[i] == curAppId){
					continue;
				}
				html += '<option value="' + appIds[i] + '">' + $tree.jstree("get_text",appIds[i]) + '</option>';
			}
			html += '</select>';
			$.dialog({
				title : '选择应用',
				small : true,
				content : html,
				callback : function(op){
					if(op == "ok"){
						var appId = this.find("select").val();
						$.post(basePath + "/app/change/" + appId + "/" + tableName, function(resp){
							if(resp.success){
								$.msg("更改成功",function(){
									location.reload();
								});
							}else{
								$.alert(resp.msg || "更改失败");
							}
						});
					}
				}
			});
		}
	},
	import : function(){
		$.dialog({
			title : '导入数据库版本',
			large : true,
			content : $("#import_template").html(),
			beforeShow : function(){
				var $dialog = this;
				var $file = $("<input type='file' name='file' id='file'>");
				$dialog.find("#fileArea").append($file);
				$file.uploadify({
					auto          : false,
					multi         : false,
					uploadLimit   : 1,
					fileTypeExts  : "*.txt;*.csv",
					fileTypeDesc  : "文本文件",
					height        : 30,
					buttonText    : '选择文件',
					fileObjName   : 'file',
					swf           : '/uploadify/uploadify.swf',
					uploader      : basePath + '/version/upload',
					width         : 120,
					onUploadStart : function(file){
						var data = {
							'DB_ID' : $dialog.find("[name='DB_ID']").val(),
							'DESCR' : $dialog.find("[name='DESCR']").val()
						}
						$file.uploadify("settings", "formData", data);
					},
					onUploadSuccess : function(){
						$.msg("上传成功",function(){
							location.reload();
						});
					}
				});
			},
			callback : function(op){
				if(op == "ok"){
					var $file = this.find("#file");
					var $uploadify = $file.data("uploadify");
					if($uploadify.queueData.filesQueued < 1){
						$.alert("请选择文件");
						return false;
					}
					$("body").showLoading();
					this.find("#file").uploadify("upload");
					return false;
				}
			}
		});
	}
}

var compare = {
	init : function(){
		$("#compare_container").on("click","[op^='compare_']",function(){
			var op = $(this).attr("op");
			switch(op){
				case 'compare_start' :
					compare.start();
					break;
				case 'compare_export_modify' : 
					compare.exportModify();
					break;
				case 'compare_export_add' : 
					compare.exportAdd();
					break;
			}
		}).on("click","#diffTable tr",function(){
			var tableName = $(this).find("td:eq(1)").text();
			var $parent = $(this).closest("table");
			var srcId = $parent.attr("srcId");
			var tarId = $parent.attr("tarId");
			$.post(basePath + "/compare/diff/" + srcId + "_" + tarId + "/" + tableName,function(html){
				$.dialog({
					title : '表结构对比',
					large : true,
					content : html
				});
			});
		});
	},
	start : function(){
		$.dialog({
			title : "数据库比较",
			content : $("#compare_template").html(),
			callback : function(op){
				if(op == "ok"){
					var srcId = this.find("[name='SRC_ID']").val();
					var tarId = this.find("[name='TAR_ID']").val();
					if(srcId == tarId){
						$.alert("主数据库与从数据库相同,不需要比较");
						return false;
					}
					$.post(basePath + "/compare/start",{SRC_ID : srcId, TAR_ID : tarId},function(html){
						$("#result").html(html);
					});
				}
			}
		});
	},
	exportModify : function(){
		// 导出变更SQL脚本
		var $diffTable = $("#diffTable");
		if($diffTable.size() < 1){
			$.alert("请先比较数据库");
			return;
		}
		var srcId = $diffTable.attr("srcId");
		var tarId = $diffTable.attr("tarId");
		if(!srcId || !tarId){
			$.alert("必要参数丢失");
			return;
		}
		window.open(basePath + "/export_modify/" + srcId + "-" + tarId);
	},
	exportAdd : function(){
		// 导出新增SQL脚本
		var $diffTable = $("#diffTable");
		if($diffTable.size() < 1){
			$.alert("请先比较数据库");
			return;
		}
		var srcId = $diffTable.attr("srcId");
		var tarId = $diffTable.attr("tarId");
		if(!srcId || !tarId){
			$.alert("必要参数丢失");
			return;
		}
		window.open(basePath + "/export_add/" + srcId + "-" + tarId);
	}
}

var app = {
	init : function(){
		$('#app_tree').jstree({
			core : {
				data : function(node,cb){
					$.post(basePath + "/app/tree",{id : node.id},function(resp){
						cb(resp);
					});
				}
			},
			plugins : ["contextmenu"],
			contextmenu : {
				items : function(node,cb){
					var actions = [];
					if(node.id == "_ROOT"){
						// 点击根节点
						actions.push({
							label : "添加应用",
							icon : "fa fa-plus",
							action : app.addApp
						});
					}else if(node.parents.length < 3){
						actions.push({
							label : "添加",
							icon : "fa fa-plus",
							action : app.addAppTable
						});
					}else{
						actions.push({
							label : "删除",
							icon : "fa fa-minus",
							action : app.removeAppTable
						});
					}
					cb(actions);
				}
			}
		});
		$("#appSearch").keydown(function(e){
			if(e.keyCode==13){
			   var $tree = $("#app_tree");
			   var text = $(this).val();
			   if(!text){
				   // 显示所有节点
				   $tree.jstree("show_all");
				   var tmps = $tree.jstree("get_text","_ROOT").split("(");
				   $tree.jstree("set_text","_ROOT",tmps[0]);
			   }else{
				   // 显示匹配的节点
				   var data = $tree.data("jstree")._model.data;
				   text = text.toLowerCase();
				   for(var name in data){
					   var node = data[name];
					   if(name == "#" || name == "_ROOT"){
						   continue;
					   }
					   if(name.toLowerCase().indexOf(text) >= 0){
						   $tree.jstree("show_node",name,true);
						   if(node.parent){
							   $tree.jstree("show_node",node.parent,true);
						   }
					   }else{
						   $tree.jstree("hide_node",name,true);
					   }
				   }
				   $tree.jstree("redraw",true);
			   }
			}
		});
	},
	// 添加应用
	addApp : function(){
		$.dialog({
			title : '添加应用',
			content : $("#app_form").html(),
			beforeShow : function(){
				this.find("form").validationEngine();
				this.find("[name]").val("");
			},
			callback : function(op){
				if(op == "ok"){
					var $form = this.find("form");
					if($form.validationEngine("validate")){
						$.post(basePath + "/app/add",$form.serialize(),function(resp){
							if(resp.success){
								$.msg("添加成功",function(){
									location.reload();
								});
							}
						});
					}
					return false;
				}
			}
		});
	},
	addAppTable : function(menu){
		// 获取应用名
		var $tree = $("#app_tree");
		var node = $tree.jstree("get_node",menu.reference);
		if(node){
			var appId = node.id;
			$.prompt("请输入表名",function(tableName){
				if(tableName){
					$.post(basePath + "/app/addTable",{appId : appId, tableName : tableName},function(resp){
						if(resp.success){
							$.msg("添加成功",function(){
								location.reload();
							});
						}
					});
				}
			})
		}
	},
	removeAppTable : function(menu){
		// 获取应用名
		var $tree = $("#app_tree");
		var node = $tree.jstree("get_node",menu.reference);
		if(node){
			var tableName = node.id;
			$.confirm("确定删除吗?",function(){
				$.post(basePath + "/app/removeTable",{tableName : tableName},function(resp){
					if(resp.success){
						$.msg("删除成功",function(){
							location.reload();
						});
					}
				});
			});
		}
	}
}

$(function(){
	$.initRouter($("#page-wrapper"),function(){
		this.append('<div style="clear:both;"></div>');
	});
});