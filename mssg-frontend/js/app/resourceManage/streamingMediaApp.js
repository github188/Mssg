define(["bootstrap","template","base","tree"],function(bootstrap,template,base,tree){
	function platformList(){
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			//表格内容高度
			var url = "";
			if($(".content-box").attr("project")=="master"){
				url = $.base+"/media/findAllMediaInfoByCondition";
			}else if($(".content-box").attr("project")=="slave"){
				url = $.base+"/mediaInfoSlave/findAllMediaInfoByCondition";
			}
			var scrollY = $(".table-box").height()-140;
			var catalogTable = $("#platformList").DataTable({
				"autoWidth": false,
				"processing":false,
				"ordering":false,
				"searching":true,
				"info":true,
				"paging":true,
				"lengthChange":false,
				"scrollY":scrollY,
				"destroy":true,
				"serverSide":true,
				"language":{
					"url": "../../js/lib/json/chinese.json"
				},
				"ajax":{
					url:url,
					type:"GET",
					contentType:"application/json",
					data:function(d){
						var params = $.extend( {}, d, {
							"page": Math.floor(d.start/d.length)+1,
							"size":10,
							"mediaName":$("input[aria-controls='platformList']").val()
						});
						return params;
					},
					dataFilter:function(data){
						var res = JSON.parse(data);
						if(res.code!=0){
							infoModal(res.message);
						}
						return JSON.stringify(res.data);
					}
				},
				"columns":[
					{title:"<input type='checkbox' class='form-control checkedAll' value='' />",data:'id'},
					{title:"顺序",data:"id"},
					{title:"视频平台名称",data:"mediaName"},
					{title:"IP地址",data:"ipAddress"},
					{title:"端口",data:"sessionPort"},
					{title:"类型",data:"mediaType"},
					// {title:"视频厂商",data:"manuName"}
				],
				"columnDefs":[
					{
						"targets":0,
						"render":function(data){
							return "<input type='checkbox' id='"+data+"' class='form-control checkedSingle' value='' />";
						},
						"searchable": false
					},
					{
						"targets":1,
						"render":function(id,type,data,row){
							return row.row+1;
						},
						"searchable": false
					},
					{
						"targets":2,
						"searchable": true
					},
					{
						"targets":5,
						"render":function(data){
							var type = '';
							if(data==1){
								type = "上级";
							}else{
								type = "下级"
							}
							return type
						},
						"searchable": false
					},
					{
						"targets":[3,4],
						"searchable": false
					}
				],
				"drawCallback": function(settings) {
					//将媒体园管理的搜索框加上placeholder
					$("#platformList_filter input").attr("placeholder","请输入视频平台名称")
					//滚动条
					base.scroll({
						container:$(".dataTables_scrollBody")
					});
					window.setCheckbox($("#platformList").parents(".table-box"));
					$("input[type='checkbox']").on("click",function(){
						if($(".checkedSingle:checked").length == 0){
							$(".btns-box").find("button[operate='view'],button[operate='edit'],button[operate='del'],button[operate='submit'],button[operate='catalog']").attr("disabled",true);
						}else if($(".checkedSingle:checked").length == 1){
							$(".btns-box").find("button[operate='view'],button[operate='edit'],button[operate='del'],button[operate='submit'],button[operate='catalog']").removeAttr("disabled");
						}else{
							$(".btns-box").find("button[operate='edit'],button[operate='view'],button[operate='del'],button[operate='catalog']").attr("disabled",true);
							// $(".btns-box").find("button[operate='del']").removeAttr("disabled");
						}
					})
				}
			});
		})
	}

	//查询
	function searchMediaCatalog(){
		$(".btn-search").unbind().on("click",function(){
			//重新向表格里添加数据
			platformList();
		})
	}
	//按钮操作
	var btnOperate = function(id,level){
		//删除
		$("button[operate='del']").unbind().on("click",function(){
			var modal = initModal({
				"id":"del",
				"header":"提示",
				"size":"sm",
				"data":"确定删除该项吗？",
				buttons:[{
					"id":"confirm_del",
					"cls":"btn-primary btn-confirm",
					"content":"确定",
					"clickEvent":function(){
						var checkId = $("#platformList .checkedSingle:checked").attr("id");
						var url = "";
						if($(".content-box").attr("project")=="master"){
							url = $.base+"/media/delMedia?mid="+checkId;
						}else if($(".content-box").attr("project")=="slave"){
							url = $.base+"/mediaInfoSlave/delMedia?mid="+checkId;
						}
						$("#modal-del").modal('hide');
						$.ajax({
							type:"GET",
							url:url,
							success:function(data){
								var res = {};
								(typeof(data)=="string")? res = JSON.parse(data): res = data;
								if(res.code == 0){
									infoModal("删除成功",function(){
										platformList();
									});
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

		});
		//查看 新增 修改 目录
		$("button[operate='view'],button[operate='add'],button[operate='edit'],button[operate='catalog']").unbind().on("click",function(){
			$(".modal").remove();
			var page = new Object();
			var _this = this;
			var checkParams = new Object();
			var id = $(this).attr("operate");
			var url = "";//查看单个的媒体流信息的接口
			var checkId = $("#platformList .checkedSingle:checked").attr("id");
			if($(".content-box").attr("project")=="master"){
				url = $.base+"/media/findOneMediaInfo?mid="+checkId;
			}else if($(".content-box").attr("project")=="slave"){
				url = $.base+"/mediaInfoSlave/findOneMediaInfo?mid="+checkId;
			}
			//按钮操作
			if($(this).attr("operate")=="view"){
				page.header = "查看";
				page.url = "resourceManage/streamingMediaManage/view.html";
				page.callback = function(){
					$.ajax({
						url:url,
						type:"get",
						success:function(d){
							var data = d.data;
							if(data.auth == 1){
								data.auth ="开启";
							}else{
								data.auth ="关闭";
							}
							if(data.mediaType == 1){
								data.mediaType ="上级";
							}else{
								data.mediaType = "下级";
							}
							base.form.init(data,$("#form"))
						}
					})
				}
			}else if($(this).attr("operate")=="add"){
				page.header = "新增";
				page.url = "resourceManage/streamingMediaManage/add.html";
			}else if($(this).attr("operate")=="edit"){
				page.header = "修改";
				page.url = "resourceManage/streamingMediaManage/edit.html";
				page.callback = function(){
					$.ajax({
						url:url,
						type:"get",
						success:function(d){
							base.form.init(d.data,$("#form"));
						}
					})
				}
			}else if($(this).attr("operate")=="catalog"){
				page.header = "媒体源设备目录";
				page.url = "resourceManage/streamingMediaManage/catalog.html";
				page.buttons = [
					{
						"id":"cancel",
						"cls":"btn-primary btn-confirm",
						"content":"关闭"
					}
				]
			};
			//渲染页面
			$.ajax({
				type:"GET",
				url:page.url,
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var modalPage = new Object();
					//size可选————sm(小) md(中) lg(大) ,默认lg
					if($(_this).attr("operate")=="view"){
						modalPage = {
							"id":id,
							"header":page.header,
							"data":data,
							buttons:[{
								"id":"save_"+id,
								"cls":"btn-primary btn-confirm",
								"content":"确定",
								"clickEvent":function(){
									$(".modal").modal('hide');
								}
							}],
							"callback":function(){
								if(page.callback){
									page.callback()
								}
							}
						};
						initModal(modalPage);
						//多选树
					}else if($(_this).attr("operate")=="catalog"){
						modalPage = {"id":id,"header":page.header,"data":data,"size":"sm",buttons:page.buttons};
						initModal(modalPage);
						mediaTree();
					}else{
						modalPage = {
							"id":id,
							"header":page.header,
							"data":data,
							"callback":function(){
								if(page.callback){
									page.callback()
								}
							},
							"buttons":[
								{
									"id":"test_"+id,
									"cls":"btn-primary btn-test",
									"content":"测试连接",
									"clickEvent":function(){
										var serverIp = $("#form input[name='ipAddress']").val();
										var serverPort = $("#form input[name='sessionPort']").val();
										testLink(serverIp,serverPort);
									}
								},
								{
									"id":"save_"+id,
									"cls":"btn-primary btn-confirm ",
									"content":"确定",
									// "attr":{
									// 	"disabled":true
									// },
									"clickEvent":function(){
										//点击确定的时候首先要测试下链接看是否通过
										var serverIp = $("#form input[name='ipAddress']").val();
										var serverPort = $("#form input[name='sessionPort']").val();
										var passwordPass = true;
										var pass = base.form.validate({
											form:$("form"),
											checkAll:true
										});
										if(!pass){
											testLink(serverIp,serverPort);
										}
										if($("#form input[name='password']").val()!="" && $("#form input[name='repassword']").val()!=""){
											passwordPass = validatePwd();
										}

										$("#form input[name='password'],#form input[name='repassword']").change(function(){
											passwordPass = validatePwd(this);
										})
										if(!pass||!passwordPass){return false;}
										if($(_this).attr("operate")=="edit"){
											editItem();
										}else if($(_this).attr("operate")=="add"){
											addItem();
										}
									}
								},
								{
									"id":"clear_"+id,
									"cls":"btn-warning btn-clear",
									"content":"重置 ",
									"clickEvent":function(){
										clearForm();
									}
								}
							]
						};
						initModal(modalPage,function(){
							selectBox($("input[name='singalFormat']").val(),$("input[name='mediaFormat']").val());
						});

					}
				}
			});
		})
	}
	function validatePwd(obj){
		if(obj == undefined){
			obj = $("#form input[name='repassword']");
		}
		var pass = true;
		$("#form input[name='password'],#form input[name='repassword']").removeClass("errorStyle");
		$("#form input[name='password'],#form input[name='repassword']").siblings(".ui-form-error").remove();
		if($("#form input[name='password']").val()!="" && $("#form input[name='repassword']").val()!=""){
			if($("#form input[name='password']").val()!=$("#form input[name='repassword']").val()){
				$(obj).addClass("errorStyle");
				$(obj).parent().append('<div class="ui-form-error">密码不一致，请重新输入</div>');
				pass = false;
			}
		}else{
			if($("#form input[name='password']").val()==""){
				obj = $("#form input[name='password']");
			}else{
				obj = $("#form input[name='repassword']");
			}
			$(obj).addClass("errorStyle");
			$(obj).parent().append('<div class="ui-form-error">必填项</div>');
		}
		return pass;
		// });
	}
	//测试连接
	function testLink(serverIp,serverPort){
		$("#form input[name='ipAddress'],#form input[name='sessionPort']").keyup(function(){
			$(this).removeClass("errorStyle");
			$(this).siblings(".ui-form-error").remove();
		});
		if( !serverIp || !serverPort){
			if( !serverIp){
				infoModal("请填写服务器IP地址");
			}else{
				infoModal("请填写会话端口");
			}
		}else{
			var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
			if(reg.test(serverIp)){
				var url = "";
				if($(".content-box").attr("project")=="master"){
					url = $.base + "/media/testMediaInfoConn?serverIp="+serverIp+"&&serverPort="+serverPort;
				}else if($(".content-box").attr("project")=="slave"){
					url = $.base + "/mediaInfoSlave/testMediaInfoConn?serverIp="+serverIp+"&&serverPort="+serverPort;
				}
				$.ajax({
					type:"GET",
					url:url,
					error:function(){
						alert("出错了！");
					},
					success:function(data){
						var res = new Object();
						if(typeof(data)=="string"){
							res = JSON.parse(data);
						}else{
							res = data;
						}
						if(res.code!=0){
							infoModal(res.message);
							//$(".modal-domain").find(".btn-confirm").addClass("disabled").prop("disabled",true);
						}else{
							setTimeout(function(){
								infoModal("连接成功",function(){
									// $(_this)[0].html("测试连接").removeAttr("disabled");
									// $(".modal-domain").find(".btn-confirm").removeClass("disabled");
									// $(".modal-domain").find(".btn-confirm").removeAttr("disabled");
									// $("#form input[name='ipAddress'],#form input[name='sessionPort']").change(function(){
									// 	$(".modal-domain").find(".btn-confirm").addClass("disabled");
									// 	$(".modal-domain").find(".btn-confirm").attr("disabled",true);
									// });
								});
							},1000);
						}
					}
				});
			}else{
				$("#form input[name='ipAddress']").removeClass("errorStyle");
				$("#form input[name='ipAddress']").siblings(".ui-form-error").remove();
				$("#form input[name='ipAddress']").addClass("errorStyle");
				$("#form input[name='ipAddress']").parent().append('<div class="ui-form-error">请填写正确的IP</div>');
			}
		}
	}
	//重置
	function clearForm(){
		var input_arr = $("#form").find("input,select");
		$.each(input_arr,function(index,item){
			if($(item)[0].nodeName == "SELECT"){
				$(item).find("option").prop("selected",false);
				$(item).find("option:eq(0)").prop("selected",true);
			}else if($(item)[0].nodeName == "INPUT"){
				$(item).val("").removeClass("errorStyle");
			};
			$(item).siblings(".ui-form-error").remove();
		});
		var signalTreeObj = $.fn.zTree.getZTreeObj("signalFormatContent");
		var mediaTreeObj = $.fn.zTree.getZTreeObj("mediaFormatContent");
		signalTreeObj.checkAllNodes(false);
		mediaTreeObj.checkAllNodes(false);
		$(".modal-domain").find(".btn-confirm").addClass("disabled").prop("disabled",false);
	}
	//新增
	function addItem(){
		var url = "";
		if($(".content-box").attr("project")=="master"){
			url = $.base+"/media/insertMedia";
		}else if($(".content-box").attr("project")=="slave"){
			url = $.base + "/mediaInfoSlave/insertMedia";
		}
		var params = getForms($(".modal-domain"));
		$.ajax({
			type:"POST",
			url:url,
			"contentType":"application/json",
			data:JSON.stringify(params),
			error:function(){
				alert("出错了！");
			},
			success:function(data){
				var res = new Object();
				(typeof(data)=="string")? res = JSON.parse(data): res = data;
				if(res.code!=0){
					infoModal(res.message);
				}else{
					$(".modal-domain").modal("hide");
					infoModal("新增成功",function(){
						platformList();
					});
				}
			}
		});
	}
	//修改
	function editItem(){
		var url = "";
		if($(".content-box").attr("project")=="master"){
			url = $.base+"/media/updateMediaInfo";
		}else if($(".content-box").attr("project")=="slave"){
			url = $.base + "/mediaInfoSlave/updateMediaInfo";
		}
		var params = getForms($(".modal-domain"));
		params.id = $("#platformList .checkedSingle:checked").attr('id');
		$.ajax({
			type:"POST",
			url:url,
			"contentType":"application/json",
			data:JSON.stringify(params),
			error:function(){
				alert("出错了！");
			},
			success:function(data){
				var res = new Object();
				(typeof(data)=="string")? res = JSON.parse(data): res = data;
				if(res.code!=0){
					infoModal(res.message);
				}else{
					$(".modal-domain").modal("hide");
					infoModal("修改成功",function(){
						platformList();
					});
				}
			}
		});
	}
	//selectBox
	function selectBox(singalVal,mediaVal){
		var click_name = "";
		var box = "";
		var i = 0;

		selectTree("signalFormatContent","../../json/resourceManage/singleFormat.json",singalVal);
		selectTree("mediaFormatContent","../../json/resourceManage/mediaFormat.json",mediaVal);
		//显示和隐藏下拉树
		$(".select-tree-input").unbind().on("click",function(){
			$(this).siblings(".select-tree-box").toggle();
		});
		//点击其他地方，关闭下拉树
		$(document).bind().on("click",function(e){
			var target = $(e.target);
			if(target.closest("input[name='"+click_name+"'],.select-tree-box").length == 0&&i!=0){
				$("input[name='"+click_name+"']").siblings(".select-tree-box").hide();
			}
			click_name = (e.target.name=="singalFormat"||e.target.name=="mediaFormat")?e.target.name:click_name;
			i++;
		})
	}
	//下拉树
	var selectTree = function(obj,url,checkedList){
		var checked_list = checkedList;
		var checked_arr = [];
		base.scroll({
			container:$(".select-tree-box")
		});
		$.ajax({
			type:"GET",
			url:url,
			error:function(){

			},
			success:function(data){
				var setting = {
					check: {
						enable: true
					},
					data: {
					},
					callback: {
						onCheck: zTreeOnCheck
					}
				};
				var zNodes = data.data;
				//生成树
				var treeObj = $.fn.zTree.init($("#"+obj),setting,zNodes);
				//获取树的全部节点
				var nodes = treeObj.getNodes();
				var checked_nodes = [];//选中的结点
				//复选
				if(checked_list!=""){
					checked_arr = checked_list.split(",");
					var nodesArr = treeObj.transformToArray(treeObj.getNodes());
					$.each(nodesArr,function(i1,o1){
						$.each(checked_arr,function(i2,o2){
							if(o1.name == o2){
								checked_nodes.push(o1);
							}
						})
					})
				}
				//默认选中树的第一个节点
				if(nodes.length > 0){
					treeObj.selectNode(nodes[0].children[0]);
				}
				if(checked_nodes.length > 0){
					$.each(checked_nodes,function(index,item){
						treeObj.checkNode(checked_nodes[index], true, true);
					})
				}
			}
		});
	}
	function zTreeOnCheck(event, treeId, treeNode){
		//获取已经勾选的
		var treeObj = $.fn.zTree.getZTreeObj(treeId);
		var nodes = treeObj.getCheckedNodes(true);
		var elem = new Array();

		$.each(nodes,function(index,item){
			if(!item.isParent){
				elem.push(item.name);
			}
		});
		var vals = "";
		if(elem.length>0){
			$.each(elem,function(i1,o1){
				i1==0?vals+=o1:vals+=","+o1;
			});
		}
		$("#"+treeId).parents(".select-tree-box").siblings(".select-tree-input").val(vals);
	}

	//master目录
	var mediaTree = function(){
		//滚动条
		base.scroll({
			container:$(".ztree")
		});
		var checkId = $("#platformList .checkedSingle:checked").attr("id");
		$.ajax({
			type:"GET",
			url:$.base + "/media/findOdsByMediaId?mid="+checkId,
			error:function(){

			},
			success:function(data){
				var setting = {
					data: {
						key:{
							name:"dsName",
							pId:"parentid"
						},
						simpleData: {
                            enable: true,
                            idKey: "deviceId",
                            pIdKey: "pid",
                            rootPId: null
                        }
					},
					callback: {
					}
				};
				if(data.code==0){

				}
				var zNodes = setIcon(data.data);
				//生成树
				var treeObj = $.fn.zTree.init($("#mediaTree"),setting,zNodes);
				//获取树的全部节点
				var nodes = treeObj.getNodes();
				//默认选中树的第一个节点
				if(nodes.length > 0){
					treeObj.selectNode(nodes[0]);
				}
			}
		});
	}
	//改变目录树中设备的图标
	var setIcon = function(zNodes){
		$.each(zNodes,function(index,item){
			if(item.ds_type==5){
				item.iconSkin = "diy01";
			}
			// if(item.children.length>0){
			// 	setIcon(item.children);
			// }
		});
		return zNodes;
	}
	return {
		run:function(){
			platformList();
			searchMediaCatalog();
			btnOperate();
		}
	}
})