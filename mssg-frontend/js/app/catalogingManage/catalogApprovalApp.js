define(["tree","base"],function(tree,base){
	//设置树
	function setTree(){
		$.ajax({
			type:"GET",
			url:"../../json/catalogingManage/catalogManageTree1.json",
			error:function(){
				
			},
			success:function(data){
				var setting = {	
					data: {
						key:{
							name:"ds_name",
							pId:"pid"
						},
						simpleData: {
							enable: true
						}
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
					treeObj.selectNode(nodes[0].children[0]);
					//初始化按钮
					setBtns(nodes[0].children[0]);
					if(nodes[0].children[0].level == 1){
						setCatalogTable();
					}else if(nodes[0].children[0].level == 2){
						setContentTable();
					}
					
				}
			}
		});
	}
	
	//树的点击事件
	var zTreeOnClick = function(event, treeId, treeNode) {
		setBtns(treeNode);
		if(treeNode.level == 1){
			setCatalogTable();
		}else if(treeNode.level == 2){
			setContentTable();
		}
	};
	
	//设置按钮
	var setBtns = function(node){
		$(".btns-box").html("");
		if(node.level == 1){
			//审核
			var buttons = '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="catalogApproval" disabled><span class="iconfont icon-yishenhe"></span>审核</button>';
			$(".btns-box").html(buttons);
		}else{
			//审核
			var buttons = '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="catalogApproval" disabled><span class="iconfont icon-yishenhe"></span>审核</button>';
			$(".btns-box").html(buttons);
		}
		//弹出模态框
		modalShow(node.id,node.level);
	}

	//弹出模态框
	var modalShow = function(id,level){
		$("button[operate='catalogApproval']").unbind().on("click",function(){
			$(".modal-catalog").remove();
			var page = new Object();
			//按钮操作
			if($(this).attr("operate")=="catalogApproval"){
				page.id = "approval";
				page.header = "审核";
				page.url = "catalogingManage/catalogApproval/approval.html";
			};
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
					};
					initModal(modalPage);
					if(level==2){
						var modalPage = {
							buttons:[
								{
									"id":001,
									"content":"上一步",
									"cls":"btn-previous"
								},
								{
									"id":002,
									"content":"下一步",
									"cls":"btn-next"
								},
								{
									"id":003,
									"content":"完成",
									"cls":"btn-complete"
								}
							]
						};
						setStepList($("#modal-"+page.id),modalPage);
					}
				}
			});
		})
	}


	//设置表格
	var setCatalogTable = function(){
		$(".table-box").html('<table class="table table-primary table-bordered table-striped text-center" id="catalogTable"></table>');
		//滚动条
		base.scroll({
			container:$(".table-box")
		});
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var catalogTable = $("#catalogTable").DataTable({
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
			  		url:"../../json/catalogingManage/catalogManage.json",
					type:"get",
					contentType:"application/json",
					data:function(d){

					}
			  	},
			  	"columns":[
			  		{title:"<input type='checkbox' class='form-control checkedAll' value='' />",data:''},
			  		{title:"序号",data:"id"},
			  		{title:"目录名称",data:"catalogName"},
			  		{title:"目录编码",data:"catalogCode"},
			  		{title:"编制单位",data:"catalogUnit"}
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
			  	 	window.setCheckbox($("#catalogTable"));
			  	 }
			});
		})
	}
	
	//设置编目表格
	var setContentTable = function(){
		$(".table-box").html('<table class="table table-primary table-bordered table-striped text-center" id="catalogTable"></table>');
		//滚动条
		base.scroll({
			container:$(".table-box")
		});
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var catalogTable = $("#catalogTable").DataTable({
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
			  		url:"../../json/catalogingManage/catalogApprovalTable.json",
					type:"get",
					contentType:"application/json",
					data:function(d){

					}
			  	},
			  	"columns":[
			  		{title:"<input type='checkbox' class='form-control checkedAll' value='' />",data:""},
			  		{title:"序号",data:"id"},
			  		{title:"资源名称",data:"resourceName"},
			  		{title:"资源类型",data:"resourceType"},
			  		{title:"编目人",data:"editCatalogPerson"},
			  		{title:"审核人",data:"auditor"},
			  		{title:"资源状态",data:"resourceStatus"}
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
			  	 	window.setCheckbox($("#catalogTable"));
			  	 	$("input[type='checkbox']").on("click",function(){
			  	 		if($(".checkedSingle:checked").length > 0){
				  	 		$(".btns-box").find("button[operate='catalogApproval']").removeAttr("disabled");
				  	 	}else{
			  	 			$(".btns-box").find("button[operate='catalogApproval']").attr("disabled",true);
				  	 	}
			  	 	})
			  	 }
			});
		})
	}
	return{
		run:function(){
			setTree();
		}
	}
	
})