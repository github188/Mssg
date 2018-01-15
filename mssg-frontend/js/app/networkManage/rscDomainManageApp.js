define(["tree","base"],function(tree,base){
	//设置树
	function setTree(){
		//滚动条
		base.scroll({
			container:$(".left-tree")
		});
		$.ajax({
			type:"GET",
			url:"../../json/networkManage/domainTree1.json",
			error:function(){
				
			},
			success:function(data){
				var setting = {	
					data: {
						key:{
							name:"ds_name"
						}
//						simpleData: {
//							enable: true
//						}
					},
					callback: {
						onClick: zTreeOnClick
					}
				};
				var zNodes = data.data;
				//生成树
				var treeObj = $.fn.zTree.init($("#ztree-bar"),setting,zNodes);
				//获取树的全部节点
				var nodes = treeObj.getNodes();
				//默认选中树的第一个节点
				if(nodes.length > 0){
					//初始化按钮
					setBtns(nodes[0]);
					if(nodes[0].children.length>0){
						treeObj.expandNode(nodes[0].children[0]);
						if(nodes[0].children[0].level == 1){
							setRootTable();
						}else if(nodes[0].children[0].level == 2){
							setChildTable();
						}
					}
				}
			}
		});
	}
	
	//树的点击事件
	var zTreeOnClick = function(event, treeId, treeNode) {
		setBtns(treeNode);
		if(treeNode.level == 0){
			setRootTable();
		}else if(treeNode.level == 1){
			setChildTable();
		}
	};
	
	//设置按钮
	var setBtns = function(node){
		$(".btns-box").html("");
		var buttons = "";
		//新增、删除、修改
		if(node.level==0){
			buttons = '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="add"><span class="iconfont icon-xinzeng"></span>新增</button>'+
				      '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="del" disabled><span class="iconfont icon-3"></span>删除</button>'+
				      '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="edit" disabled><span class="iconfont icon-bianji"></span>修改</button>';
				
		}else if(node.level==1){
			buttons =  '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="join"><span class="iconfont icon-xinzeng"></span>加入</button>'+
					   '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="del" disabled><span class="iconfont icon-3"></span>删除</button>';
		}
		$(".btns-box").html(buttons);
		//弹出模态框
		modalShow(node.id,node.level);
	}

	//弹出模态框
	var modalShow = function(id,level){
		$("button[operate='add'],button[operate='edit'],button[operate='join']").unbind().on("click",function(){
			$(".modal-catalog").remove();
			var _this = this;
			var page = new Object();
			//按钮操作
			if($(this).attr("operate")=="add"){
				page.header = "新增";
				page.url = "networkManage/rscDomainManage/add.html";
			}else if($(this).attr("operate")=="edit"){
				page.header = "修改";
				page.url = "networkManage/rscDomainManage/edit.html";
			}else if($(this).attr("operate")=="join"){
				page.header = "加入";
				page.url = "networkManage/rscDomainManage/join.html";
			};
			$.ajax({
				type:"GET",
				url:page.url,
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var modalPage = "";
					if($(_this).attr("operate")=="join"){
						modalPage = {
							"id":id,
							"header":page.header,
							"data":data,
							"size":"sm",
							"buttons":[
								{
									"id":"save_"+id+"",
									"cls":"btn-primary btn-checked",
									"content":"确定"
								},
								{
									"id":"cancel_"+id+"",
									"cls":"btn-warning btn-cancel",
									"content":"取消"
								}
							]
						};
						joinUnitTree();
					}else{
						modalPage = {
							"id":id,
							"header":page.header,
							"data":data,
							"buttons":[
								{
									"id":"save_"+id+"",
									"cls":"btn-primary btn-confirm",
									"content":"确定"
								},
								{
									"id":"cancel_"+id+"",
									"cls":"btn-warning btn-clear",
									"content":"重置"
								}
							]
						};
					}
					
					initModal(modalPage);
					$("#modal-"+id).modal("show");
					validate(".btn-confirm",$("#modal-"+id+""));//校验表单（点击按钮，校验的容器）
					clearForm(".btn-clear",$("#modal-"+id+""));//清空操作（点击按钮，清空的容器）
					modalConfirm();
				}
			});
		})
	}

	//设置表格
	var setRootTable = function(){
		$("#domainBox").html('<table class="table table-primary table-bordered table-striped text-center"></table>');
		//滚动条
		base.scroll({
			container:$("#domainBox")
		});
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var catalogTable = $("#domainBox table").DataTable({
				"autoWidth": false,
			  	"processing":false,
			  	"ordering":false,
			  	"searching":false,
			  	"info":true,
			  	"paging":true,
			  	"lengthChange":false,
			  	"destroy":true,
			  	"language":{
			  		"url":"../../js/lib/json/chinese.json"
			  	},
			  	"ajax":{
			  		url:"../../json/networkManage/rscDomainTable.json",
					type:"get",
					contentType:"application/json",
					data:function(d){

					}
			  	},
			  	"columns":[
			  		{title:"<input type='checkbox' class='form-control checkedAll' value='' />",data:''},
			  		{title:"序号",data:"id"},
			  		{title:"域名称",data:"domainName"},
			  		{title:"域管理员",data:"domainAdministrator"},
			  		{title:"联系电话",data:"telephone"},
			  		{title:"所属单位",data:"unit"}
			  	],
			  	"columnDefs":[
			  		{
			  			"targets":0,
			  			"render":function(data){
			  				return "<input type='checkbox' class='form-control checkedSingle' value='' />";
			  			}
			  		}
			  	],
			  	 "drawCallback": function(settings) {
			  	 	window.setCheckbox($("#domainBox"));
			  	 	$("input[type='checkbox']").on("click",function(){
			  	 		if($(".checkedSingle:checked").length == 0){
				  	 		$(".btns-box").find("button[operate='edit'],button[operate='del']").attr("disabled",true);
				  	 	}else if($(".checkedSingle:checked").length == 1){
				  	 		$(".btns-box").find("button[operate='edit'],button[operate='del']").removeAttr("disabled");
				  	 	}else{
				  	 		$(".btns-box").find("button[operate='edit']").attr("disabled",true);
				  	 		$(".btns-box").find("button[operate='del']").removeAttr("disabled");
				  	 	}
			  	 	})
			  	 }
			});
		})
	}
	
	//设置编目表格
	var setChildTable = function(){
		$("#domainBox").html('<table class="table table-primary table-bordered table-striped text-center"></table>');
		//滚动条
		base.scroll({
			container:$("#domainBox")
		});
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var catalogTable = $("#domainBox table").DataTable({
				"autoWidth": false,
			  	"processing":false,
			  	"ordering":false,
			  	"searching":false,
			  	"info":true,
			  	"paging":true,
			  	"lengthChange":false,
			  	"destroy":true,
			  	"language":{
			  		"url":"../../js/lib/json/chinese.json"
			  	},
			  	"ajax":{
			  		url:"../../json/networkManage/domainUnit.json",
					type:"get",
					contentType:"application/json",
					data:function(d){

					}
			  	},
			  	"columns":[
			  		{title:"<input type='checkbox' class='form-control checkedAll' value='' />",data:""},
			  		{title:"序号",data:"id"},
			  		{title:"单位名称",data:"unitName"},
			  		{title:"单位联系人",data:"unitAdministrator"},
			  		{title:"联系电话",data:"telephone"},
			  		{title:"联系邮箱",data:"email"},
			  		{title:"单位对外IP端",data:"ip"}
			  	],
			  	"columnDefs":[
			  		{
			  			"targets":0,
			  			"render":function(data){
			  				return "<input type='checkbox' class='form-control checkedSingle' value='' />";
			  			}
			  		}
			  	],
			  	 "drawCallback": function(settings) {
			  	 	window.setCheckbox($("#domainBox"));
			  	 	$("input[type='checkbox']").on("click",function(){
			  	 		if($(".checkedSingle:checked").length > 0){
 							$(".btns-box").find("button[operate='del']").removeAttr("disabled");
				  	 	}else{
				  	 		$(".btns-box").find("button[operate='del']").attr("disabled",true);
				  	 	}
			  	 	})
			  	 }
			});
		})
	}
	
	//单位树
	var joinUnitTree = function(){
		//滚动条
		base.scroll({
			container:$(".ztree")
		});
		$.ajax({
			type:"GET",
			url:"../../json/networkManage/unitsTree.json",
			error:function(){
				
			},
			success:function(data){
				var setting = {	
					data: {
						key:{
							// name:"ds_name"
						}
//						simpleData: {
//							enable: true
//						}
					},
					check:{
						enable:true
					},
					callback: {
						onClick: zTreeOnClick
					}
				};
				var zNodes = data.data;
				//生成树
				var treeObj = $.fn.zTree.init($("#joinUnits"),setting,zNodes);
				//获取树的全部节点
				var nodes = treeObj.getNodes();
				//默认选中树的第一个节点
				if(nodes.length > 0){
					//初始化按钮
					// setBtns(nodes[0]);
					if(nodes[0].children.length>0){
						treeObj.expandNode(nodes[0].children[0]);
						// if(nodes[0].children[0].level == 1){
						// 	setRootTable();
						// }else if(nodes[0].children[0].level == 2){
						// 	setChildTable();
						// }
					}
				}
			}
		});
	}
	//单位树模态框的确认按钮
	var modalConfirm = function(){
		$(".btn-checked").unbind().on("click",function(){
			infoModal("添加成功！");
		})
	}
	return{
		run:function(){
			setTree();
		}
	}
	
})