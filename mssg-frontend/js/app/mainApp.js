define(["bootstrap","template","base","tree","cookies"],function(bootstrap,template,base,tree){
	//设定初始页面
	var choose_menu_name = "视频监控";//一级目录名称
	var child_index = 1;//在一级目录下的第几个孩子，从0开始计数
	//左侧菜单
	function setMenu(){
		if(JSON.parse(sessionStorage.getItem("loginInfo"))==null){
			var old_url =  window.location.href;
			var new_url = old_url.replace(/main/,'login');
			window.location.href = new_url;
		}else{
			var data = JSON.parse(sessionStorage.getItem("loginInfo")).loginUserInfo.menuList;
			$.each(data,function(index,item){
				if(item.label == "实时" || item.label == "历史"){
					item.isFrame = 1;
				}else{
					item.isFrame = 0;
				}
			})
			//将树转为数组
			var result = new Array()
			console.log(data);
			result = mapToArray(data,"0");
			//初始化
			var initChild = {
				url:result[0].items[0].url,
				label:result[0].items[0].label,
				parentName:result[0].label,
				isFrame:result[0].items[0].isFrame==1?1:0
			};
			var activeObj = {
				parent:0,
				child:0
			}

			$.each(result,function(index,item){
				//初始化需要打开页面的父级
				var i = child_index;//0--第一个孩子，1--第二个孩子，以此类推
				if(item.label==choose_menu_name){
					//初始化页面是父级的第几个孩子
					i>(item.items.length-1)?i=0:i=i;
					initChild = item.items[i];
					initChild.parentName = item.label;
					initChild.isFrame = item.items[i].isFrame;
					activeObj = {
						parent:index,
						child:i
					}
				}
			});
			//左侧active效果
			result[activeObj.parent].active = true;
			result[activeObj.parent].items[activeObj.child].active = true;
			loadPages(initChild.url,initChild.parentName,initChild.label,initChild.isFrame);//最后一个参数--右侧内容是否是iframe嵌套（0-不是，1-是）
			//模版
			var menu = template.compile($("#menu-template").html());
			$("#menu-list").html(menu(result));
			//li加选中效果
			$(".child-menu-list li").on("click",function(){
				loadPages($(this).attr("url"),$(this).attr("parentName"),$(this).attr("selfName"),$(this).attr("isFrame"));
				$(this).parents(".panel-group").find("li").removeClass("active");
				$(this).addClass("active");
			});
			//箭头方向
			$(".panel .panel-collapse").on("shown.bs.collapse",function(){
				$(this).parent().find(".panel-heading i.icon-right").removeClass("fa-angle-down").addClass("fa-angle-up");
			});
			$(".panel .panel-collapse").on("hidden.bs.collapse",function(){
				$(this).find("li").removeClass("active");
				$(this).parent().find(".panel-heading i.icon-right").removeClass("fa-angle-up").addClass("fa-angle-down");
			});
			//滚动条
			base.scroll({
				container:$(".page-menubar")
			});
		}
	}
	//右侧菜单
	function setRightNav(){
		$("#headerOperate li[name='personCenter']").unbind().on("click",function(){
			$.ajax({
				type:"GET",
				url:"personCenter/personCenter.html",
				error:function(){},
				success:function(data){
					var person_modal = initModal({
						id:"person_center",
						header:"个人中心",
						size:"md",
						data:data,
					},function(){
						$("#personCenterTabs").height($("#personCenterContent").height()+"px");
						editPwd();
					})
				}
			})
		})
//		退出
		$("#headerOperate li[name='loginOut']").unbind().on("click",function(){
			var url = "";
			if($("#menu-list").attr("project")=="slave"){
				url = $.base+"/slaveLogin/commonLogout";
			}else if($("#menu-list").attr("project")=="master"){
				url = $.base+"/commonLogout";
			}
			
			var modal = initModal({
				"id":"del",
				"header":"提示",
				"size":"sm",
				"data":"确定退出吗？",
				buttons:[{
					"id":"confirm_del",
					"cls":"btn-primary btn-confirm",
					"content":"确定",
					"clickEvent":function(){
						var roleId = base.getChecks("call");
						$("#modal-del").modal('hide');
						$.ajax({
							type:"post",
							url:url,
							success:function(d){
								d = JSON.parse(d);
								if(d.code == 0){
									window.location.href = "login.html";
								}else{
									infoModal(res.message);
								}
							}
						});
					}
				},
					{
						"id":"cancel",
						"cls":"btn-primary btn-confirm",
						"content":"取消"
					}]
			});
		})
	}
	function validatePwd(obj){
		if(obj == undefined){
			obj = $("input[name='reNewPwd']");
		}
		var pass = true;
		$("input[name='oldPwd'],input[name='newPwd'],input[name='reNewPwd']").removeClass("errorStyle");
		$("input[name='oldPwd'],input[name='newPwd'],input[name='reNewPwd']").siblings(".ui-form-error").remove();

		if($("input[name='oldPwd']").val()!="" && $("input[name='newPwd']").val()!="" && $("input[name='reNewPwd']").val()!=""){
			if($("input[name='newPwd']").val()!=$("input[name='reNewPwd']").val()){
				$(obj).addClass("errorStyle");
				$(obj).parent().append('<div class="ui-form-error">密码不一致，请重新输入</div>');
				pass = false;
			}
		}else{
			$("#editPwd").find("input").each(function(index,item){
				if($(item).val()==""){
					$(item).addClass("errorStyle");
					$(item).parent().append('<div class="ui-form-error">必填项</div>');
				}
			});
			pass = false;
		}
		return pass;
		// });
	}
	//修改密码
	var editPwd = function(){
		$("#btnEditPwd").unbind().on("click",function(){
			var pass = validatePwd();
			if(pass){
				var url = "";
				if($("body").attr("project")=="master"){
					url = $.base+"/sysUser/updateSysUserpwd";
				}else if($("body").attr("project")=="slave"){
					url = $.base+"/user/updateSysUserpwd";
				}
				$.ajax({
					type:"POST",
					url:url,
					contentType:"application/json",
					data:JSON.stringify({
						oldpwd:$("input[name='oldPwd']").val(),
						newpwd:$("input[name='newPwd']").val()
					}),
					error:function(){},
					success:function(data){
						if(data.code==0){
							$(".modal-domain").modal("hide");
							infoModal("修改成功");
						}else{
							infoModal(data.message);
						}
					}
				})
			}
		});
	}
	//根据id查找对象所在数组的下标
	var posIndex = function(data,id){
		var position = 0;
		$.each(data,function(index,item){
			if(id==item.id){
				position = index;
			}
		})
		return position;
	}
	
	//将树转为数组
	window.mapToArray = function(data,pid){
		var root = pid;
		var result = new Array();
		if(data){
			var i=0;
			$.each(data,function(index,item){
				if(item.pid == root){
					result.push(item);
					if(!item.items){
						result[i].items = [];
					}
					i++;
				}
				/*else{
                    var pos = posIndex(result,item.pid);
                    result[pos].items.push(item);
                }*/
			})
			$.each(result,function (k1,v1) { //循环父级
                $.each(data,function(k2,v2){
					if(v1.id == v2.pid){
                        result[k1].items.push(v2)
					}
                })
            })

		}
		return result;
	}
	
	//加载页面
	var loadPages = function(url,parentName,selfName,isFrame){
		$(".location-info").html(parentName+"<i class='fa fa-angle-right'></i>"+selfName);

		if(isFrame==1){
			$(".page-content").html('<iframe allowTransparency="true" frameborder="0" height="100%" width="100%"  src="'+url+'" scrolling="yes" id="videoBox"></iframe>');
		}else{
			$.ajax({
				type:"GET",
				url:url,
				error:function(){
					alert("加载错误！");
				},
				success:function(data){
					$(".page-content").html(data);
				}
			})
		}
	}
	
	//信息提示框
	window.infoModal = function(info,clickBack){
		$(".modal-info").modal("hide");
		$(".modal-info").remove();
		var modal = '<div class="modal modal-info fade" id="modal-tip" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel">'+
					 	'<div class="modal-dialog modal-sm" role="document">'+
					    	'<div class="modal-content">'+
					     	 	'<div class="modal-header">'+
					     	 		 ' <button type="button" class="close" data-dismiss="modal" aria-label="Close">'+
						     	 		  '<span aria-hidden="true">&times;</span>'+
					     	 		  '</button>'+
  									  '<h4 class="modal-title">提示</h4>'+
					     	 	'</div>'+
					     	 	'<div class="modal-body">'+info+'</div>'+
					     	 	'<div class="modal-footer">'+
					     	 		'<button class="btn btn-primary" data-dismiss="modal">确定</button>'+
					     	 	'</div>'
					    	'</div>'+
					  	'</div>'+
					'</div>';
		$("body").append(modal);
		$("#modal-tip").modal("show");
        $("#modal-tip").on("show.bs.modal",function(){
            $(".modal-info,.modal-backdrop").remove();
		})
		$("#modal-tip").on("shown.bs.modal",function(){
			// var top = $(document).height()-$("#modal-tip .modal-dialog").height()-20;
			// (top>0)?top=top:top=0;
			var top = 160;
			$("#modal-tip .modal-dialog").css("margin-top",top/2+"px");
		})
		if(clickBack){
			$("#modal-tip").on("hidden.bs.modal",function(){
				clickBack();
			})
		}
	}
	
	//设置checkbox全选反选
	window.setCheckbox = function(obj){
		$(obj).find(".checkedAll").unbind().on("click",function(){
  	 		if($(this).is(":checked")){
  	 			$(".checkedSingle").each(function(index,item){
  	 				$(item).prop("checked",true);
  	 			});
  	 		}else{
  	 			$(".checkedSingle").each(function(index,item){
  	 				$(item).prop("checked",false);
  	 			});
  	 		}
  	 	});
	 	$(obj).find(".checkedSingle").unbind().on("click",function(){
	 		if($(".checkedSingle:checked").length==$(".checkedSingle").length){
	 			$(".checkedAll").prop("checked",true);
	 		}else{
	 			$(".checkedAll").prop("checked",false);
	 		}
	 	});
	}
	
	//设置步骤页
	window.setStepList = function(obj,modalPage,excute){
		initModal(modalPage);
		$(obj).find(".modal-footer").html("");
		var span_arr = $(".step-list").find("li span");
		//设置整体居中
		$(".step-list").css("width",(200*(span_arr.length-1)+30));
		$(obj).on("shown.bs.modal",function(){
			//设置标题居中显示
			$.each(span_arr,function(index,item){
				$(item).css("right",(-$(item).width()/2+12)+"px");
			});
			var buttons = "<button class='btn btn-primary btn-previous' order='0' style='display:none'>上一步</button>"+
						  "<button class='btn btn-primary btn-next' order='0'>下一步</button>"+
						  "<button class='btn btn-primary btn-complete' order='0' style='display:none'>完成</button>";
			//添加按钮--上一步/下一步/保存
			if($(obj).find(".modal-footer").length>0){
				$(obj).find(".modal-footer").html(buttons);
			}else{
				$(obj).find(".modal-content").append("<div class='modal-footer'>"+buttons+"</div>");
			}
			 if(excute){
			 	if(excute.callback){
			 		excute.callback();
			 	}
			 }
			getPreviousStep(obj,span_arr.length);
			getNextStep(obj,span_arr.length,excute);
			btnComplete(obj,excute);
		});
	}
	
	
	//编目--下一步
	window.getNextStep = function(obj,pages,excute){
		$(obj).find(".btn-next").unbind().on("click",function(){
			var currentPage = Number($(this).attr("order"))+1;
			//校验
			var pass = base.form.validate({
					form:$(".panes-content>div.pane-box:eq("+(currentPage-1)+")").find("form"),
					checkAll:true
				});
			if(!pass){return;}
			
			//设置页码
			$(obj).find(".modal-footer button").attr("order",currentPage);
			//显示当页
			$(".panes-content>div.pane-box:eq("+currentPage+")").show().siblings().hide();
			$(".step-list li:eq("+(currentPage)+")").addClass("active");
			if(currentPage>=pages-1){
				$(obj).find(".btn-next").hide();
				$(obj).find(".btn-complete").show();
				if(excute){
					if(excute.stepBack){
						excute.stepBack();
					}
				}
			}else{
				$(obj).find(".btn-complete").hide();
				$(obj).find(".btn-next").show();
			}
			if(currentPage>0){
				$(obj).find(".btn-previous").show();
			}
		});
	}
	
	//编目--上一步
	window.getPreviousStep = function(obj,pages,excute){
		$(obj).find(".btn-previous").unbind().on("click",function(){
			var currentPage = Number($(this).attr("order"))-1;
			$(obj).find(".modal-footer button").attr("order",currentPage);
			$(".panes-content>div.pane-box:eq("+currentPage+")").show().siblings().hide();
			$(".step-list li:eq("+(currentPage+1)+")").removeClass("active");
			if(currentPage<pages-1){
				$(obj).find(".btn-complete").hide();
				$(obj).find(".btn-next").show();
			}else{
				$(obj).find(".btn-next").hide();
				$(obj).find(".btn-complete").show();
			}
			if(currentPage<1){
				$(obj).find(".btn-previous").hide();
			}
		});
	}
	
	//编目--保存
	window.btnComplete = function(obj,excute){
		$(obj).find(".btn-complete").unbind().on("click",function(){
			//校验
			var currentPage = Number($(this).attr("order"))+1;
			var pass = base.form.validate({
					form:$(".panes-content>div.pane-box:eq("+(currentPage-1)+")").find("form"),
					checkAll:true
				});
			if(!pass){return;}
			if(excute){
				if(excute.saveBack){
					excute.saveBack();
				}
			}
			$(obj).modal("hide");
		});
	}
	
	//将input数据用表格展示
//	window.inputToTable = function(obj){
//		var input_groups = $(obj).find(".form-group");
//		$.each(input_groups,function(index,item){
//			$(item).find(".control-label").html();
//		});
//	}
	
	var doubleClick = function(event, treeId, treeNode){
		$("input[name='"+treeId+"']").val(treeNode.ds_name);
		$(".modal-catalog-tree").modal('hide');
	}
	//目录树---input点击出现
	window.catalogTree = function(obj,url,setting){
		$(obj).val("");
		//滚动条
		base.scroll({
			container:$(".left-tree")
		});
		$.ajax({
			type:"GET",
			url:url,
			error:function(){
				
			},
			success:function(data){
				// $(".modal-catalog-tree").remove();
				// var modal = '<div class="modal modal-catalog-tree fade" id="modal-tip" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel">'+
				// 	 	'<div class="modal-dialog modal-sm" role="document">'+
				// 	    	'<div class="modal-content">'+
				// 	     	 	'<div class="modal-body">'+
				// 	     	 		'<div class="ztree" id="catalogTree"></div>'
				// 	     	 	'</div>'+
				// 	    	'</div>'+
				// 	  	'</div>'+
				// 	'</div>';
				// $("body").append(modal);
				// $(".modal-catalog-tree").modal('show');
				var zNodes = data.data;
				//生成树
				var setting = {
					data: {
						key:{
							name:"cataName"
						},
						simpleData: {
							enable: true,
							idKey:"id",
							pIdKey:"pid"
						}
					},
					callback: {
						onClick:function(treeObj,treeid,node){
							$(obj).val(node.cataName).attr("cataId",node.id);
						}
					}
				};
				var treeObj = $.fn.zTree.init($(obj).siblings(".select-tree-box").find(".ztree"),setting,zNodes);
				//展开一级节点
				var nodes = treeObj.getNodes();
				if (nodes.length>0) {
					treeObj.expandNode(nodes[0], true, false, true);
				}
			}
		});
	}
	//清空form表单
	var clearForm = function(obj){
		var input_arr = $(obj).find("input,select");
		$.each(input_arr,function(index,item){
			if($(item)[0].nodeName == "SELECT"){
				$(item).find("option").prop("selected",false);
				$(item).find("option:eq(0)").prop("selected",true);
			}else if($(item)[0].nodeName == "INPUT"){
				$(item).val("");
			}
			$(item).removeClass("errorStyle").siblings().remove();
		})
	}

	//校验
	var validate = function(obj){
		var pass = base.form.validate({
				form:$("form"),
				checkAll:true
			});
		if(!pass){return false;}
		
		$(obj).modal("hide");
//		infoModal("保存成功！");
	}
	
	//初始化模态框
	window.initModal = function(page,callback){
		if(!page.recall){
			$(".modal-domain").remove();
			$(".modal-backdrop ").remove();
		}else{
			debugger
			$("#modal-"+page.id).remove();
		}
		var buttons = "";
		var modalFooter = "";
		var id = page.id;
		page.header?page.header:page.header="标题";
		page.size?page.size:page.size="lg";
		var height = page.height?page.height:'auto';
		var modal = '<div class="modal modal-domain fade" data-backdrop="static" id="modal-'+page.id+'" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel">'+
					 	'<div class="modal-dialog modal-'+page.size+'" role="document">'+
					    	'<div class="modal-content">'+
					     	 	'<div class="modal-header">'+
					     	 		 ' <button type="button" class="close" data-dismiss="modal" aria-label="Close">'+
						     	 		  '<span aria-hidden="true">&times;</span>'+
					     	 		  '</button>'+
  									  '<h4 class="modal-title" id="modal-header">'+page.header+'</h4>'+
					     	 	'</div>'+
					     	 	'<div class="modal-body" style="height:'+height+'">'+page.data+'</div>'+
					     	 	'<div class="modal-footer"></div>'+
					    	'</div>'+
					  	'</div>'+
					'</div>';
		$("body").append(modal);
		//按钮
		if(page.buttons!=null&&page.buttons!=undefined){
			$.each(page.buttons,function(index,item){
				var tag = null;
				item.cls==undefined||item.cls=="" ? item.cls="btn-affirm":item.cls;
				item.attr==undefined||item.attr=="" ? item.attr="":item.attr;
				tag = document.createElement("button");
				$(tag).addClass("btn "+item.cls+"");
				$(tag).html(item.content);
				if(item.id){
					$(tag).attr("id",item.id);
				}
				if(item.attr){
					for(var key in item.attr){
						$(tag).attr(key,item.attr[key]);
					}
				}
				if(item.clickEvent!=undefined){
					$(tag).on("click",function() {
						item.clickEvent()
					})
				}
				if(item.content == "关闭" || item.content == "取消"){
					$(tag).attr("data-dismiss","modal");
				}
				$("body .modal-footer").append(tag);
			});
		}
		//回显
		if(page.checkBack){
			checkBack($("#modal-"+page.id),page.checkBack);
		}
		$(".modal-domain").modal("show");
		$(".modal-domain").on("shown.bs.modal",function(){
			// var top = $(document).height()-$(".modal-domain .modal-dialog").height()-20;
			var top = 100;
			// (top>0)?top=top:top=0;
			$(".modal-domain .modal-dialog").css("margin-top",top/2+"px");
			if(callback){
				callback();
			}
		})
//		回调
		if(page.callback){
			page.callback();
		}
		$(".modal .modal-footer").unbind().on("click","button",function(){
			if($(this).html()=="清空"||$(this).html()=="重置"){
				clearForm($("#modal-" + id));//清空操作
			}
		})
	}
	//回显
	window.checkBack = function(obj,params){
		// $(obj).find("input,select,textarea").each(function(index,item){
		// 	for (var key in params) {
		// 		if($(item).attr("name")==key){
		// 			$(item).val(params[key]);
		// 		}
		// 	}
		// })
		$(obj).find("[name='"+name+"']").html = params[name];
	}
	//设置按钮
	function setBtn(fun){
		fun();
	}
	//获取参数
	window.getForms = function(obj){
		var params = new Object();
		$(obj).find("input,select,textarea").each(function(index,item){
			params[$(item).attr("name")] = $(item).val();
		})
		return params;
	}
	//时间格式
	window.translateDate = function(time){
		var newTime = new Date(parseInt(time));
		var year = newTime.getFullYear();
		var month = newTime.getMonth()<9?("0"+(newTime.getMonth()+1)):(newTime.getMonth()+1);
		var day = newTime.getDate()<10?("0"+newTime.getDate()):newTime.getDate();
		var hour = newTime.getHours()<10?("0"+newTime.getHours()):newTime.getHours();
		var minute = newTime.getMinutes()<10?("0"+newTime.getMinutes()):newTime.getMinutes();
		var second = newTime.getSeconds()<10?("0"+newTime.getSeconds()):newTime.getSeconds();
		return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
	}
	window.tableReload = function(id){
		var table = id.DataTable();
		table.draw( false );
	}
	var i = 0;
	function quitLogin(){
			var url = "";
			if($("#menu-list").attr("project")=="slave"){
				url = $.base+"/slaveLogin/commonLogout";
			}else if($("#menu-list").attr("project")=="master"){
				url = $.base+"/commonLogout";
			}
			$.ajax({
				type:"post",
				url:url,
				async:false,
				success:function(d){
					d = JSON.parse(d);
					if(d.code == 0){
					}else{
					}
				}
			});
	}
    //ajax错误
    function getAjaxError(){
        $(document).ajaxSuccess(function(evt, req, settings){
        	debugger
			if(req.responseJSON){
				if(req.responseJSON.code==403){
					var old_url =  window.location.href;
					var new_url = old_url.replace(/main/,'login');
                    $(".modal-info,.modal-backdrop").remove();
                    infoModal("用户重复登录、超时、无权限或被强制下线",function(){
                        window.location.href = new_url;
					});
				}
			}
		});
		$(document).ajaxError(function(evt, req, settings){
			if(req.responseJSON){
				if(req.responseJSON.status==403){
					var old_url =  window.location.href;
					var new_url = old_url.replace(/main/,'login');
					window.location.href = new_url;
				}
			}
		});
    }
	return {
		run:function(){
			setMenu();//设置左侧菜单栏
			setRightNav();
			getAjaxError();
			//滚动条
			base.scroll({
				container:$("iframe")
			});
			var _beforeUnload_time = 0, _gap_time = 0;
			var is_fireFox = navigator.userAgent.indexOf("Firefox")>-1;//是否是火狐浏览器






			// var isIE = document.all?true:false;
			// window.addEventListener("unload",function (event) {
			// 	var e = event || window.event;
			// 	var n;//鼠标在当前窗口上的水平位置
			// 	isIE ? n = window.event.screenX - window.screenLeft:window.pageXOffset - window.screenLeft;
			// 	var b = n > document.documentElement.scrollWidth-20;
			// 	if(b && window.event.clientY < 0 || window.event.altKey || window.event.ctrlKey){
			// 		关闭浏览器时你想做的事
			// 	}else if(event.clientY > document.body.clientHeight || event.altKey){
			// 		关闭浏览器时你想做的事
			// 	}
			// });
			// window.onbeforeunload = function (){
			// 	event.preventDefault();
			// 	if(is_fireFox)//火狐关闭执行
			// 		quitLogin();
			// 	return _beforeUnload_time = new Date().getTime();
			// };






			// $(window).on('beforeunload',function(){
			// 	event.preventDefault();
			// 	if(is_fireFox)//火狐关闭执行
			// 		quitLogin();
			// 	return _beforeUnload_time = new Date().getTime();
			// });
			// window.onunload = function (){
			// 	console.log(new Date().getTime());
			// 	console.log(_beforeUnload_time)
			// 	_gap_time = new Date().getTime() - _beforeUnload_time;
			// 	if(_gap_time <= 5){
			// 		debugger
			// 		quitLogin();
			// 	}else{
			// 	}
			// }
			// window.onunload = onunload_message;
			// function onunload_message(){
			// 	var warning="确认退出?";
			// 	return warning;
			// }
		}
	};
});