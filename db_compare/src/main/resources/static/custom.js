/**
 * 系统公共脚本
 */
$.fn.modal.Constructor.DEFAULTS = {
	backdrop: 'static',
    keyboard: false,
    show: true
};
// 弹出窗垂直居中
$.fn.modal.Constructor.prototype.adjustDialog = function(){
	var modalIsOverflowing = this.$element[0].scrollHeight > document.documentElement.clientHeight;
	this.$element.css({
		paddingLeft:  !this.bodyIsOverflowing && modalIsOverflowing ? this.scrollbarWidth : '',
		paddingRight: this.bodyIsOverflowing && !modalIsOverflowing ? this.scrollbarWidth : ''
	});
	var $modal_dialog = $(this.$element[0]).find('.modal-dialog');
	var m_top = ( $(window).height() - $modal_dialog.height() )/2;
	$modal_dialog.css({'margin': (m_top > 0 ? m_top : 0) + 'px auto'});
};
$.extend({
	// HTML转义
	escapeHtml : function(txt){
		if(!txt){
			return txt;
		}
		return $("<div/>").text(txt).html();
	},
	//Html解码获取Html实体
	decodeHtml : function(html){
	  return $("<div/>").html(html).text();
	},
	// 转换日期字符串到日期对象
	parseDate : function(d){
		var dateParts = d.split("-");
		if(dateParts==d)
			dateParts = d.split("/");
		if(dateParts==d) {
			dateParts = d.split(".");
			if(dateParts.length == 1){
				dateParts[1] = 0;
				dateParts[2] = 0;
			}else if(dateParts.length == 2){
				dateParts[2] = 0;
			}
			return new Date(dateParts[2], (dateParts[1] - 1), dateParts[0]);
		}
		if(dateParts.length == 1){
			dateParts[1] = 0;
			dateParts[2] = 0;
		}else if(dateParts.length == 2){
			dateParts[2] = 0;
		}
		return new Date(dateParts[0], (dateParts[1] - 1) ,dateParts[2]);
	},
	/**
	 * 发送同步请求
	 */
	syncPost : function(url,param,type){
		var result = null;
		$.ajax({
			url : url,
			type : 'POST',
			async : false,
			data : param,
			success : function(resp){
				result = resp;
			},
			dataType : type
		});
		return result;
	},
	toJsonStr : function(obj){
		return JSON.stringify(obj);
	},
	toJson : function(str){
		return JSON.parse(str);
	},
	dialog : function(opts){
		opts = opts || {};
		var html = "<div class='modal " + ((opts.alert || opts.msg || opts.confirm) ? "modal-info" : "") + " fade " 
					 + (opts.large?"bs-example-model-lg":opts.small ? "bs-example-modal-sm" : "") + "' role='dialog'>"
					 + "<div class='modal-dialog " + (opts.large?"modal-lg" : opts.small?"modal-sm" : "") + "'";
		if(opts.width){
			html += " style='width:" + opts.width + "'";
		}
		html += ">";
		html += "<div class='modal-content'><div class='modal-header'>";
		html += "<button type='button' op='close' class='close' aria-label='关闭'>";
		html += "<span aria-hidden='true'>&times;</span></button>";
		html += "<h4 class='modal-title'>" + (opts.title || '消息') + "</h4></div>";
		html += "<div class='modal-body' ";
		if(opts.maxHeight){
			html += "style='max-height:" + opts.maxHeight + ";overflow-y:auto;'";
		}
		html += ">" + (opts.content || '') + "</div>";
		html += "<div class='modal-footer'>";
		if(opts.extraBtns){
			for(var i=0;i<opts.extraBtns.length;i++){
				var btn = opts.extraBtns[i];
				html += "<button type='button' class='btn btn-sm btn-" + (btn.icon || "default")
					   + " fleft' op='" + (btn.op || '') + "'>" + (btn.text || "") + "</button>";
			}
		}
		if(opts.alert || opts.msg){
			html += "<button type='button' class='btn btn-sm btn-primary' op='ok'>确定</button>";
		}else if(opts.yesNo){
			html += "<button type='button' class='btn btn-sm btn-primary' op='yes'>是</button>";
			html += "<button type='button' class='btn btn-sm btn-default' op='no'>否</button>";
		}else if(opts.buttons){
			for(var i=0;i<opts.buttons.length;i++){
				var btn = opts.buttons[i];
				html += "<button type='button' class='btn btn-sm btn-" + (btn.icon || (i == opts.buttons.length -1 ? "primary" : "white")) 
					     + "' op='" + (btn.op || '') + "'>" + (btn.text || "") + "</button>";
			}
		}else if(opts.close){
			html += "<button type='button' class='btn btn-sm btn-primary' op='close'>关闭</button>";
		}else{
			html += "<button type='button' class='btn btn-sm btn-primary' op='ok'>确定</button>";
			html += "<button type='button' class='btn btn-sm btn-default' op='cancel'>取消</button>";
		}
		html += "</div></div></div></div>";
		var $dialog = $(html);
		if(opts.dom){
			$dialog.find(".modal-body").append(opts.dom);
		}
		$("body").append($dialog);
		$dialog.delegate("button[op]","click",function(){
			var op = $(this).attr("op");
			if(opts.callback){
				var result = opts.callback.call($dialog,op);
				if(result === false){
					return;
				}
			}
			$dialog.modal('hide');
		});
		if(opts.beforeShow){
			opts.beforeShow.call($dialog);
		}
		$dialog.on("show.bs.modal",function(e){
			$("body").css("overflow","hidden");
		}).on("hidden.bs.modal",function(e){
			$("body").css("overflow","auto");
			$(this).remove();
		}).modal("show");
		if(opts.msg){
			setTimeout(function(){
				$dialog.modal('hide');
			},2000);
		}
	},
	alert : function(msg,fn, opts){
		$.dialog($.extend({
			content : msg,
			title : '提示',
			alert : true,
			small : true,
			callback : function(op){
				if(op == "ok" && fn){
					return fn.call(this);
				}
			}
		},opts));
	},
	msg : function(msg,fn,time){
		layer.msg($.escapeHtml(msg),{time : time ? time :1500},fn);
	},
	confirm : function(msg, yes,no, opts){
		$.dialog($.extend({
			content : msg,
			title : '确认',
			confirm : true,
			small : true,
			callback : function(op){
				if(op == "ok" && yes){
					return yes.call(this);
				}else if(op == 'cancel' && no){
					return no.call(this);
				}
			}
		},opts));
	},
	yesNo : function(msg,yes,no,opts){
		$.dialog($.extend({
			content : msg,
			title : '确认',
			yesNo : true,
			small : true,
			callback : function(op){
				if(op == "yes" && yes){
					return yes.call(this);
				}else if(op == 'no' && no){
					return no.call(this);
				}
			}
		},opts));
	},
	// 模版解析
	tmpl : function(str,data){
		var cache = $("body").data("__tmpl__");
		if(!cache){
			cache = {};
			$("body").data("__tmpl__",cache);
		}
		var fn = !/\W/.test(str) ? cache[str] = cache[str] || $.tmpl(document.getElementById(str).innerHTML) :
			new Function("obj",
				"var p=[],print=function(){p.push.apply(p,arguments);};" +
				"with(obj){p.push('" +
				str
				.replace(/[\r\t\n]/g, " ")
				.split("{%").join("\t")
				.replace(/((^|%})[^\t]*)'/g, "$1\r")
				.replace(/\t=(.*?)%}/g, "',$1,'")
				.split("\t").join("');")
				.split("%}").join("p.push('")
				.split("\r").join("\\'")
				+ "');}return p.join('');");
		return data ? fn( data ) : fn;
	},
	// 设置路由
	initRouter : function($target,callback){
		$target = $target || $("body");
		var rt = Router();
		rt.on(/(.*)/,function(path){
			var hash = location.hash;
			if(hash && hash.charAt(0) == "#"){
				hash = hash.substr(1);
			}
			$.post(basePath + "/" + hash,function(html){
				$target.html(html);
				if(callback){
					callback.call($target,path,html);
				}
			});
		});
		rt.init();
	}
});
$(function(){
	var currentRequest = {}; // 缓存当前正在请求的Request Md5值
	// 获取请求的token
	function genToken(setting){
		var key = setting.url + "|";
		if(setting.data){
			if($.type(setting.data) == 'string'){
				key += setting.data;
			}else{
				key += $.toJsonStr(setting.data);
			}
		}
		return $.md5(key);
	}
	
	$(document).ajaxSuccess(function(evt,resp){
		if(resp && resp.responseText){
			var tmps = resp.responseText.split("SYS_SCRIPT:");
			if(tmps.length > 1){
				eval(tmps[1]);
				return false;
			}
		}
	});
	
	$.ajaxSetup({
		beforeSend : function(xhr){
			var token = genToken(this);
			if(!token){
				$.msg("无法生成Token，请检查请求参数。。。");
				return false;
			}
			if(currentRequest[token] === 1){
				$.msg("正在处理,请稍后...");
				return false;
			}
			currentRequest[token] = 1;
			return true;
		},
		complete : function(xhr){
			var token = genToken(this);
			delete currentRequest[token];
		}
	});
});
