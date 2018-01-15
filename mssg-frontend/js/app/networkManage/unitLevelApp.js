define(["tree","base"],function(tree,base){

	//设置表格
	var unitLevelTable = function(){
		var url = ""
//		url = "../../json/resourceManage/unitLevel.json";
		url = $.base+"/company/getComLevel";
		var scrollY = $(".table-box").height() - 100;
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var userroleTable = $("#unitLevelTable").DataTable({
				"autoWidth": false,
			  	"processing":false,
			  	"ordering":false,
			  	"searching":false,
			  	"scrollY":scrollY,
			  	"info":true,
			  	"paging":true,
			  	"lengthChange":false,
			  	"destroy":true,
			  	"serverSide":true,
			  	"language":{
			  		"url":"../../js/lib/json/chinese.json"
			  	},
			  	"ajax":{
			  		url:url,
					type:"get",
					contentType:"application/json",
					data:function(d){
						var params = $.extend({},{
							"page": Math.floor(d.start/d.length)+1,
							"size":10,
						});
						return params;
					},
					dataFilter:function(data){
						var d = JSON.parse(data).data;
						return JSON.stringify(d);
					}
			  	},
			  	"columns":[
					{title:"<input type='checkbox' class='form-control checkedAll' value='' />",data:'comLevel'},
			  		{title:"序号",data:"cnLevel"},
			  		{title:"级别",data:"cnLevel"},
			  		{title:"关联设备等级",data:"equipLevel"}
			  	],
			  	"columnDefs":[
			  		{
			  			"targets":0,
			  			"render":function(data,d,obj){
			  				return "<input type='checkbox' class='form-control checkedSingle' attr='"+JSON.stringify(obj)+"' name='call' value='"+data+"' />";
			  			}
			  		},
			  		{
			  			"targets":1,
			  			"render":function(data,d,o,row){
			  				return row.row+1;
			  			}
			  		},
			  		{
			  			"targets":3,
			  			"render":function(data){
			  				var Json = null;
							$.ajax({
								url:"../../json/common/common.json",
								type:"get",
								async:false,
								success:function(d){
									Json=d[0].JKDWLX;
								}
							})
							var vals = "";
							$.each(data, function(i,o) {
								i==0?vals+=Json[o]:vals+=","+Json[o];
							});
							return vals;
			  			}
			  		}
			  	],
			  	 "drawCallback": function(settings) {
			  	 	//滚动条
					base.scroll({
						container:$(".dataTables_scrollBody")
					});
			  	 	window.setCheckbox($("#unitLevelTable").parents(".table-box"));
			  	 	$("input[type='checkbox']").on("click",function(){
			  	 		/*
			  	 		 *修改/删除按钮
			  	 		 * --只有选中一条的时候可以修改和删除
			  	 		 * */
			  	 		if($(".checkedSingle:checked").length == 1){
				  	 		$(".btns-box").find("button[operate='edit']").removeAttr("disabled");
				  	 		$(".btns-box").find("button[operate='del']").prop("disabled",false);
				  	 		editBtn();
				  	 		deleteBtn();
				  	 	}else{
				  	 		$(".btns-box").find("button[operate='edit']").attr("disabled",true);
				  	 		$(".btns-box").find("button[operate='del']").prop("disabled", true);
				  	 	}
			  	 	})
			  	 }
			});
		})
	}
	//删除按钮
	var deleteBtn = function(){
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
						var checkId = base.getChecks("call")
						var url = "";
							url = $.base+"/company/deleteComLevelConfig?comLevel="+checkId;
						$("#modal-del").modal('hide');
						$.ajax({
							type:"GET",
							url:url,
							success:function(data){
								if(data.code == 0){
									infoModal("删除成功");
									tableReload($(".dataTable"));
								}else{
									infoModal(data.message);
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
	//新增按钮
	var addBtn = function(){
		$("button[operate='add']").unbind().on("click",function(){
			var page = new Object();
			page.header = "新增";
			page.url = "networkManage/unitLevel/add.html";
			page.id = "add";
			$.ajax({
				type:"GET",
				url:page.url,
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var modalPage = {
						"id":page.id,
						"header":page.header,
						"data":data,
						"buttons":[
							{
								"id":"save_",
								"cls":"btn-primary btn-confirm",
								"content":"确定",
								"clickEvent":function(){
									var pass = base.form.validate({
											form:$("form"),
											checkAll:true
										});
									if(!pass){
										return;
									}
									var d={};
									d.comLevel = $("#comLevel").val();
									d.levelJson = "["+$("#levelJson").attr("code")+"]";
									d.remark = $("#remark").val();
									$.ajax({
										type:"get",
										url:$.base+"/company/addComLevel",
										data:d,
										async:true,
										success:function(d){
											if(d.success){
												$(".modal").modal("hide");
												tableReload($(".dataTable"));
											}
										}
									});
								}
							},
							{
								"id":"clear_",
								"cls":"btn-warning btn-clear",
								"content":"取消"
							}
						],
						"callback":function(){
							$.ajax({
								url:$.base+"/company/findComLevelNotUsed",
								type:"get",
								success:function(d){
									$.each(d.data,function(i,o){
										$("#comLevel").append("<option value='"+o.dictItemCode+"'>"+o.dictItemName+"</option>")
									})
								}
							})
						}
					};
					initModal(modalPage);
					selectBox();
				}
			});
		});
	}
	//修改按钮
	var editBtn = function(){
		$("button[operate='edit']").unbind().on("click",function(){
			var page = new Object();
			page.header = "修改";
			page.url = "networkManage/unitLevel/edit.html";
			page.id = "edit";
			$.ajax({
				type:"GET",
				url:page.url,
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var modalPage = {
						"id":page.id,
						"header":page.header,
						"data":data,
						"buttons":[
							{
								"id":"save_",
								"cls":"btn-primary btn-confirm",
								"content":"确定",
								"clickEvent":function(){
									var pass = base.form.validate({
											form:$("form"),
											checkAll:true
										});
									if(!pass){
										return;
									}
									var d={};
									d.comLevel = $("#comLevel").attr("comlevel");
									d.levelJson = "["+$("#levelJson").attr("code")+"]";
									d.remark = $("#remark").val();
									$.ajax({
										type:"get",
										url:$.base+"/company/modifyComLevel",
										data:d,
										async:true,
										success:function(d){
											if(d.success){
												$(".modal").modal("hide");
												tableReload($(".dataTable"));
											}
										}
									});
								}
							},
							{
								"id":"clear_",
								"cls":"btn-warning btn-clear",
								"content":"取消"
							}
						],
						"callback":function(){
							var data = JSON.parse($(".checkedSingle:checked").attr("attr"));
							$("#comLevel").val(data.cnLevel);
							$("#comLevel").attr("comLevel",data.comLevel);
							
							var Json = null;
							$.ajax({
								url:"../../json/common/common.json",
								type:"get",
								async:false,
								success:function(d){
									Json=d[0].JKDWLX;
								}
							})
							var vals = "";
							$.each(data.equipLevel, function(i,o) {
								i==0?vals+=Json[o]:vals+=","+Json[o];
							});
							$("#levelJson").val(vals);
							$("#levelJson").attr("code",data.equipLevel);
							selectBox(vals);
						}
					};
					initModal(modalPage)
//					selectBox();
				}
			});
		});
	}
	//selectBox
	function selectBox(vals){
		var click_name = "";
		var box = "";
		var i = 0;

		selectTree("levelJsonContent","../../json/resourceManage/jkdwlx.json",vals);
//		selectTree("mediaFormatContent","../../json/resourceManage/mediaFormat.json");
		//显示和隐藏下拉树
		$(".select-tree-input").unbind().on("click",function(){
			$(this).siblings(".select-tree-box").toggle();
		});
		//点击其他地方，关闭下拉树
		$(document).bind("click",function(e){
			var target = $(e.target);
			if(target.closest("input[name='"+click_name+"'],.select-tree-box").length == 0&&i!=0){
				$("input[name='"+click_name+"']").siblings(".select-tree-box").hide();
			}
			click_name = (e.target.name=="levelJson")?e.target.name:click_name;
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
		var elem = new Array();
		var nodes = treeObj.getCheckedNodes(true);
		if(treeNode.checked&&treeNode.id=="-1"){
			$.each(nodes, function(i,o) {
				if(o.id!="-1"){
					treeObj.checkNode(o, false, true);
				}else{
					nodes.splice(0,nodes.length);
					nodes.push(o);
				}
			});
		}else if(treeNode.checked&&treeNode.id!="-1"){
			$.each(nodes, function(i,o) {
				if(o.id=="-1"){
					treeObj.checkNode(o, false, true);
					nodes.splice(i,i+1);
				}
			});
		}
		$.each(nodes,function(index,item){
			if(!item.isParent){
				elem.push({"name":item.name,"code":item.id});
			}
		});
		var vals = "";
		var codes = "";
		if(elem.length>0){
			$.each(elem,function(i1,o1){
				i1==0?vals+=o1.name:vals+=","+o1.name;
				i1==0?codes+=o1.code:codes+=","+o1.code;
			});
		}
		$("#"+treeId).parents(".select-tree-box").siblings(".select-tree-input").val(vals);
		$("#"+treeId).parents(".select-tree-box").siblings(".select-tree-input").attr("code",codes);
	}
	return{
		run:function(){
//			新增按钮
			addBtn();
			unitLevelTable();
		}
	}
	
})