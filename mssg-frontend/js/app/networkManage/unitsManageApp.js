define(["tree","base"],function(tree,base){
	//设置按钮
	var setBtns = function(){
		$(".btns-box").html("");
		var buttons = "";
		//新增、删除、修改
		buttons = '<button type="button" class="btn btn-no-border" operate="add"><span class="iconfont icon-xinzeng"></span>新增</button>'+
				      '<button type="button" class="btn btn-no-border" operate="del" disabled><span class="iconfont icon-3"></span>删除</button>'+
			    	  '<button type="button" class="btn btn-no-border" operate="edit" disabled><span class="iconfont icon-xinzeng"></span>修改</button>'
//			    	  '<button type="button" class="btn btn-no-border" operate="edit" disabled><span class="iconfont icon-xinzeng"></span>停用</button>'+
//			    	  '<button type="button" class="btn btn-no-border" operate="edit" disabled><span class="iconfont icon-xinzeng"></span>启用</button>';

		var html = '<div class="col-md-6">'+buttons+'</div>';
		$(".btns-box").html(html);
		//弹出模态框
		modalShow();
	}

	//弹出模态框
	var modalShow = function(){
		$("button[operate='add'],button[operate='edit']").unbind().on("click",function(){
			$(".modal-catalog").remove();
			var page = new Object();
			//按钮操作
			if($(this).attr("operate")=="add"){
				page.header = "新增";
				page.url = "networkManage/unitsManage/add.html";
				page.btnBack = function(){
					var form = base.form.getParams($("#form"))
                	var pass = base.form.validate({
							form:$("form"),
							checkAll:true
						});
					if(!pass){
						return;
					}
					$.ajax({
						type:"get",
						url:$.base+"/company/add",
						async:true,
						data:form,
						success:function(d){
							if(d.success){
								$(".modal").modal("hide");
								tableReload($(".dataTable"));
							}else{
								infoModal(d.message);
								return
							}
						}
					});
				}
			}else if($(this).attr("operate")=="edit"){
				page.header = "修改";
				page.url = "networkManage/unitsManage/edit.html";
				page.callback = function(){
					var checkId = base.getChecks("call")
					$.ajax({
						type:"get",
						url:$.base+"/company/findById?id="+checkId,
						async:true,
						success:function(d){
							base.form.init(d.data,$("#form"));
						}
					});
				};
				page.btnBack = function(){
					var checkId = base.getChecks("call")
					var form = base.form.getParams($("#form"))
					form.id = checkId[0];
					var pass = base.form.validate({
							form:$("form"),
							checkAll:true
						});
					if(!pass){
						return;
					}
					$.ajax({
						type:"get",
						url:$.base+"/company/modify",
						async:true,
						data:form,
						success:function(d){
							if(d.success){
								$(".modal").modal("hide");
								tableReload($(".dataTable"));
							}else{
								infoModal(d.message);
								return
							}
						}
					});
				}
			};
			$.ajax({
				type:"GET",
				url:page.url,
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var modalPage = {
						"header":page.header,
						"data":data,
						"buttons":[
							{
								"id":"save_",
								"cls":"btn-primary btn-confirm",
								"content":"确定",
								"clickEvent":function(){
									if(page.btnBack){
										page.btnBack();
									}
								}
							},
							{
								"id":"clear_",
								"cls":"btn-warning btn-clear",
								"content":"重置"
							}
						],
						"callback":function(){
							searchInputVal(1)
							if(page.callback){
								page.callback();
							}
						}
					};
					initModal(modalPage);
				}
			});
		})
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
							url = $.base+"/company/delete?companyId="+checkId;
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
	//详情
	var viewPage = function(){
		$(".viewPage").unbind().on("click",function(){
			var _this = this;
			$.ajax({
				type:"GET",
				url:"networkManage/unitsManage/view.html",
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var modalPage = {
						"header":"详情",
						"data":data,
						"callback":function(){
							$.ajax({
								url:$.base+"/company/findById?id="+$(_this).attr("rid"),
								type:"get",
								success:function(d){
									 d.data.comType = d.data.comType==1?"管理单位":"共享单位";
									 base.form.init(d.data,$("#form"))
								}
							})
						}
					};
					initModal(modalPage);
				}
			});
		})
	}
//	查询input框赋值
	function searchInputVal(val){
		$.ajax({
			type:"get",
			url:$.base+"/company/findAllComLevel",
			async:true,
			success:function(d){
				if(d.success){
					$.each(d.data, function(i,o) {
						if(val){
							$(".modal .comLevel").append("<option value='"+o.dictItemCode+"'>"+o.dictItemName+"</option>")
						}else{
							$(".comLevel").append("<option value='"+o.dictItemCode+"'>"+o.dictItemName+"</option>")
						}
					});
				}
			}
		});
	}
	//查询按钮
	var searchBtn = function(){
		$("#searchRsc").unbind().on("click",function(){
			setUnitTable();
		})
	}
	//设置表格
	var setUnitTable = function(){
		$("#catalogTable").html('<table class="table table-primary table-bordered table-striped text-center"></table>');
		//滚动条
		base.scroll({
			container:$("#catalogTable")
		});
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var catalogTable = $("#catalogTable table").DataTable({
				"autoWidth": false,
			  	"processing":false,
			  	"ordering":false,
			  	"searching":false,
			  	"info":true,
			  	"paging":true,
			  	"lengthChange":false,
			  	"destroy":true,
			  	"serverSide":true,
			  	"language":{
			  		"url":"../../js/lib/json/chinese.json"
			  	},
			  	"ajax":{
//			  		url:"../../json/networkManage/units.json",
					url:$.base+"/company/listAll",
					type:"get",
					contentType:"application/json",
					data:function(d){
						var obj = {};
						obj.nameOrCode = $("#nameOrCode").val();
						obj.comLevel = $("#comLevel").val();
						var params = $.extend(obj,{
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
			  		{title:"",data:'id'},
			  		{title:"单位名称",data:"name"},
			  		{title:"单位地址",data:"address"},
			  		{title:"联系人",data:"contacts"},
			  		{title:"办公电话",data:"officePhone"}
			  		// {title:"状态",data:"officePhone"},
			  	],
			  	"columnDefs":[
			  		{
			  			"targets":0,
			  			"render":function(data){
			  				return "<input type='checkbox' class='form-control checkedSingle' name='call' value='"+data+"' />";
			  			}
			  		},
			  		{
			  			"targets":1,
			  			"render":function(data,d,obj){
			  				return "<a href='javascript:void(0);' class='viewPage' rid='"+obj.id+"'>"+data+"</a>";
			  			}
			  		}
			  	],
			  	 "drawCallback": function(settings) {
			  	 	window.setCheckbox($("#catalogTable"));
			  	 	$("input[type='checkbox']").on("click",function(){
			  	 		if($(".checkedSingle:checked").length == 0){
				  	 		$(".btns-box").find("button[operate='edit'],button[operate='del']").attr("disabled",true);
				  	 	}else if($(".checkedSingle:checked").length == 1){
				  	 		$(".btns-box").find("button[operate='edit'],button[operate='del']").removeAttr("disabled");
				  	 	}else{
				  	 		$(".btns-box").find("button[operate='edit']").attr("disabled",true);
				  	 		$(".btns-box").find("button[operate='del']").attr("disabled",true);
//				  	 		$(".btns-box").find("button[operate='del']").removeAttr("disabled");
				  	 	}
			  	 	})
			  	 	viewPage();
			  	 }
			});
		})
	}
	return{
		run:function(){
			setBtns();
			setUnitTable();
			searchInputVal();
			searchBtn();
		}
	}
	
})