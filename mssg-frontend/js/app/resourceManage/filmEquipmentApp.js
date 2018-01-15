define(["tree","base","app/common"],function(tree,base,common){

	//设置表格
	var deviceManageTable = function(){
		var url = ""
		url = $.base+"/equipment/findAllPageByCondition";
//		url = $.pathM+"/originalds/findAllPageByCondition"
		var scrollY = $(".table-box").height() - 100;
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var userroleTable = $("#userManageTable").DataTable({
				"serverSide":true,
				"autoWidth": false,
			  	"processing":false,
			  	"ordering":false,
			  	"searching":false, 
			  	"scrollY":scrollY,
			  	"info":true,
			  	"paging":true,
			  	"lengthChange":false,
			  	"destroy":true,
			  	"language":{
			  		"url":"../../js/lib/json/chinese.json"
			  	},
			  	"ajax":{
			  		url:url,
					type:"get",
					contentType:"application/json",
					data:function(d){
						var obj={};
						obj.dsName=$("#dsName").val();
						obj.jkdwlx=$("#jkdwlx").val();
						if($("#locationType").val()){
							obj.locationType=$("#locationType").attr("val");
						}
						obj.mediaDeviceId=$("#mediaDeviceId").val();
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
//					{title:"<input type='checkbox' class='form-control checkedAll' value='' />",data:''},
			  		{title:"",data:"id"},
			  		{title:"序号",data:"id"},
			  		{title:"设备名称",data:"equipmentName"},
			  		{title:"IP地址",data:"ipAddress"},
			  		{title:"监控点位类型",data:"jkdwlx"},
			  		{title:"摄像机类型",data:"equipmentType"},
			  		{title:"摄像机位置类型",data:"sxjkwzlx"}
			  		// {title:"视频厂商",data:"manuName"}
			  	],
			  	"columnDefs":[
			  		{
			  			"targets":0,
			  			"render":function(data){
			  				return "<input type='checkbox' name='call' class='form-control checkedSingle' value='"+data+"' />";
			  			}
			  		},
			  		{
			  			"targets":1,
			  			"render":function(data,d,o,row){
			  				return row.row+1;
			  			}
			  		}
			  	],
			  	 "drawCallback": function(settings) {
			  	 	//滚动条
					base.scroll({
						container:$(".dataTables_scrollBody")
					});
			  	 	window.setCheckbox($("#userManageTable"));
			  	 	$("input[type='checkbox']").on("click",function(){
			  	 		/*
			  	 		 *修改按钮
			  	 		 * --只有选中一条的时候可以修改
			  	 		 * */
			  	 		if($(".checkedSingle:checked").length == 1){
				  	 		$(".btns-box").find("button[operate='edit'],button[operate='view']").removeAttr("disabled");
				  	 		editBtn();
				  	 		viewBtn();
				  	 	}else{
				  	 		$(".btns-box").find("button[operate='edit'],button[operate='view']").attr("disabled",true);
				  	 	}
			  	 	})
			  	 	// 上传按钮
			  	 	importBtn();
			  	 	exportBtn();
			  	 	searchBut();
			  	 }
			});
		})
	}
	//查询按钮
	var searchBut = function(){
		$("#searchBut").unbind().on("click",function(){
			deviceManageTable();
		})
	}
	//修改按钮
	var editBtn = function(){
		$("button[operate='edit']").unbind().on("click",function(){
			var page = new Object();
			page.header = "修改设备信息";
			page.url = "resourceManage/filmEquipment/edit.html";
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
						"callback":function(){
							var oid = base.getChecks("call");
							$.ajax({
								url:$.base+"/equipment/findOneEquipBydsCode?oid="+oid,
								type:"get",
								success:function(d){
									base.form.init(d.data[0],$("#content-1"))
									base.form.init(d.data[0],$("#content-2"))
									base.form.init(d.data[0],$("#content-3"))
									//修改前赋值
									//行政区域
									$("#xzqy").val(common.xzqy(d.data[0].xzqy))
									$("#xzqy").attr("val",d.data[0].xzqy)
									//摄像机功能类型
									$("#sxjgnlx").val(common.sxjgnlx(d.data[0].sxjgnlx))
									$("#sxjgnlx").attr("val",d.data[0].sxjgnlx)
									//摄像机位置类型
									$("#sxjwzlx").val(common.sxjwzlx(d.data[0].sxjwzlx))
									$("#sxjwzlx").attr("val",d.data[0].sxjwzlx)
									//所属部门行业
									$("#ssbmhy").val(common.ssbmhy(d.data[0].ssbmhy))
									$("#ssbmhy").attr("val",d.data[0].ssbmhy)
								}
							})
						}
					};
					var excute = {};
					excute.saveBack = function(){
						var form1 = getForms($("#content-1"));
						var form2 = getForms($("#content-2"));
						var form3 = getForms($("#content-3"));
						var data = $.extend(form1,form2,form3);
						data.id = base.getChecks("call")[0];
						data.xzqy=$("#xzqy").attr("val");
						data.sxjgnlx = $("#sxjgnlx").attr("val");
						data.sxjwzlx = $("#sxjwzlx").attr("val");
						data.ssbmhy = $("#ssbmhy").attr("val");
						$.ajax({
							type:"post",
							url:$.base+"/equipment/updateEquipAttribute",
							data:JSON.stringify(data),
							async:true,
							contentType:"application/json",
							success:function(d){
								if(d.code!=0){
									infoModal(d.message);
								}else{
									$(".modal").modal("hide");
									infoModal("修改成功",function(){
										deviceManageTable();
									});
								}
							}
						});
					}
					setStepList(".modal",modalPage,excute);//分步
                    //多选树
					selectBox();
					// 行政区域树
					selectXZQY();
				}
			});
		});
	}
	// 行政区域树
	function selectXZQY(){
		$.ajax({
			url:$.base+"/dictItem/findAllxzqu",
			type:"get",
			success:function(data){
				var setting = {
					data: {
                        key: {
                            name: "dictItemName",
                            pId:"dictPid"
                        },
                        simpleData: {
                            enable: true,
                            idKey: "dictItemCode",
                            pIdKey: "dictPid",
                            rootPId: null
                        }
                    },
					callback:{
						onClick:zTreeCityCheck
					}
				};
				var zNodes = data.data;
				//生成树
				var treeObj = $.fn.zTree.init($("#xzqyContent"),setting,zNodes);
			}
		})
	}
	function zTreeCityCheck(event, treeId, treeNode){
		$("#xzqyContent").parents(".select-tree-box").hide();
		$("#xzqy").val(treeNode.dictItemName)
		$("#xzqy").attr("val",treeNode.dictItemCode)
	}
	//设置多选树selectBox
	function selectBox(){
		selectTree("mediaFormatContent","../../json/resourceManage/sxjgnlx.json");
		selectTree("locationTypeSearch","../../json/resourceManage/sxjwzlx.json");
		selectTree("locationTypeContent","../../json/resourceManage/sxjwzlx.json");
		selectTree("ssbmhyContent","../../json/resourceManage/ssbmhy.json");
		var clickName = "";
		var box = "";
		var i = 0;
		$(document).bind().on("click",function(e){
			var target = $(e.target);
			if(target.closest("input[name='"+clickName+"'],.select-tree-box").length == 0&&i!=0){
				$("input[name='"+clickName+"']").siblings(".select-tree-box").hide();
			}
			clickName = (e.target.name=="locationType")?e.target.name:clickName;
			i++;
		})
		$(".select-tree-input").unbind().on("click",function(){
			$(this).siblings(".select-tree-box").toggle();
		});
	}
	var selectTree = function(obj,url){
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
				//默认选中树的第一个节点
				if(nodes.length > 0){
					treeObj.selectNode(nodes[0].children[0]);
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
				elem.push({"name":item.name,"code":item.id});
			}
		});
		var vals = "";
		var codes = ""
		if(elem.length>0){
			$.each(elem,function(i1,o1){
				i1==0?vals+=o1.name:vals+="/"+o1.name;
				i1==0?codes+=o1.code:codes+="/"+o1.code;
			});
		}
		$("#"+treeId).parents(".select-tree-box").siblings(".select-tree-input").val(vals);
		$("#"+treeId).parents(".select-tree-box").siblings(".select-tree-input").attr("val",codes);
	}
	//查看按钮
	var viewBtn = function(){
		$("button[operate='view']").unbind().on("click",function(){
			var id = base.getChecks("call");
			$.ajax({
				url:$.base+"/equipment/findOneEquipBydsCodeForView?oid="+id,
				type:"get",
				success:function(d){
					if(d.code==0){
						view(d);
					}else{
						infoModal(d.message);
					}
				}
			})
		})
	}
	function view(d){
		$.ajax({
			type:"GET",
			url:"resourceManage/filmEquipment/view.html",
			error:function(){
				alert("出错了！");
			},
			success:function(res){
				var data = d.data[0];
				var modalPage = {
					"header":"查看",
					"data":res,
					"height":"500px",
					"callback":function(){
						// data.sbcs = common.SBCS(data.sbcs)
						//行政区域
						// data.xzqy = common.xzqy(d.data[0].xzqy);
						//摄像机功能类型
						// data.sxjgnlx = common.sxjgnlx(d.data[0].sxjgnlx);
						//摄像机位置类型
						// data.sxjwzlx = common.sxjwzlx(d.data[0].sxjwzlx);
						//所属部门行业
						// data.ssbmhy = common.ssbmhy(d.data[0].ssbmhy);
						// data.sbcs = common.selectVal(d.data[0].sbcs,"SBCS");
						// data.jkdwlx = common.selectVal(d.data[0].jkdwlx,"JKDWLX");
						// data.sxjlx = common.selectVal(d.data[0].sxjlx,"SXJLX");
						// data.bgsx = common.selectVal(d.data[0].bgsx,"BGSX");
						// data.sxjbmgs = common.selectVal(d.data[0].sxjbmgs,"SXJBMGS");
						// data.jkfw = common.selectVal(d.data[0].jkfw,"JKFW");
						// data.lwsx = common.selectVal(d.data[0].lwsx,"LWSX");
						// data.sbzt = common.selectVal(d.data[0].sbzt,"SBZT");
						base.form.init(data,$(".pane-box"))
					}
				};
				initModal(modalPage);
				base.scroll({
					container:$(".modal-body")
				});
			}
		});
	}
	// 上传按钮
	var importBtn = function(){
		//监控label事件
		var labelFile = document.getElementById("labelFile");
		$("#labelFile").on("click",function () {
			$("#file").on("change",function(){
				base.form.fileUpload({
					url:$.base+"/equipment/import",
					id:"file",
					dataType:"text",
					params:{},
					success:function(data){
						var res = JSON.parse(data);
						if(res.success){
							infoModal("上传成功");
							$("#file").val("")
						}else{
							infoModal("上传失败");
							$("#file").val("")
						}
					},
					error:function(d){
						infoModal("服务器错误");
						$("#file").val("")
					}
				});
			})
		});
		// labelFile.addEventListener("click",function () {
        //
		// });
		// $("button[operate='import']").unbind().on("click",function(e){
		// 	//var e = e || window.event;
		// 	//e .stopPropagation();
		// 	$(".btns-box #file").remove();
		// 	$(".btns-box").append("<input type='file' id='file' name='multipartFile'>");
		// 	$("#file").click();
		// 	$("#file").on("change",function(){
		// 		debugger
		// 		base.form.fileUpload({
	     //           url:$.base+"/equipment/import",
	     //           id:"file",
		// 			dataType:"text",
	     //           params:{},
	     //           success:function(data){
		// 			   var res = JSON.parse(data);
	     //           		if(res.success){
	     //           			infoModal("上传成功")
	     //           		}else{
	     //           			infoModal("上传失败")
	     //           		}
		//             },
		//             error:function(d){
		// 				infoModal("服务器错误")
		//             }
	     //       });
		// 	})
		// })
	}
	// 导出按钮
	var exportBtn = function(){
		$("button[operate='export']").unbind().on("click",function(){
			window.location.href = $.base+"/equipment/export"
		})
	}
	// 搜索input框赋值
	var searchInputValue = function(){
		//所属平台
		$.ajax({
			url:$.base+"/media/findAll",
			type:"get",
			success:function(d){
				$.each(d.data,function(i,o){
					$("#mediaDeviceId").append("<option value='"+o.deviceId+"'>"+o.mediaName+"</option>")
				})
			}
		})
	}
	//清空设置
	function clearForm(){
		$("#clearForm").on("click",function(){
			$(".form-box").find(".form-control").val("");
			var treeObj = $.fn.zTree.getZTreeObj("locationTypeSearch");
			treeObj.checkAllNodes(false);
		})
	}
	return{
		run:function(){
			deviceManageTable();
			searchInputValue();
			selectBox();
			clearForm();
		}
	}
	
})