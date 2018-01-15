define(["bootstrap","template","base","date5.0"],function(bootstrap,template,base,laydate){
	var ztreeName = '';
	var target="";//判断点击的是date还是date1
	// function getList(resStatus){
//	function getList(resStatus){
//		var param = {};
//		param.resStatus = resStatus;
//		param.resName = $("#resourceName").val();
//		if($("#catalogId").val()){
//			param.catalogId = $("#catalogId").attr("dataId");
//		}
//		param.searchTime = $("#searchTime").val();
//		$.ajax({
//			type:"GET",
//			url:$.base+"/res/findAllResByCondition",
//			data:param,
//			error:function(){
//				alert("出错了");
//			},
//			success:function(data){
//				//改变data里的值
//				$.each(data.data, function(index,item) {
//					$.ajax({
//	    				type:"get",
//	    				url:$.base+"/dictItem/findAllIndustry",
//	    				async:false,
//	    				success:function(d){
//	    					$.each(d.data, function(i,o) {
//	            				if(o.dictItemCode == data.data[index].resIndustry){
//	            					data.data[index].resIndustry = o.dictItemName;
//	            				}
//	            			});
//	    				}
//	    			});
//	    			$.ajax({
//	    				type:"get",
//	    				url:$.base+"/dictItem/findAllMain",
//	    				async:false,
//	    				success:function(d){
//	    					$.each(d.data, function(i,o) {
//	            				if(o.dictItemCode == data.data[index].resMain){
//	            					data.data[index].resMain = o.dictItemName;
//	            				}
//	            			});
//	    				}
//	    			});
//				});
//				var temp = template.compile($("#rsc-list-template").html());
//				var height = 0;
//				$(".tab-content").height($(".content-padding-box").height()-45);
//				template.registerHelper("buttons", function(value,id,name) {
//					var str = "";
//				 	if(value=="已发布"){
//				 		str = '<button class="btn btn-block btn-success cancelContent" id="" attr="'+id+'">撤销</button>';
//				 	}else{
//				 		str = '<button class="btn btn-block btn-success republishContent" id="" attr="'+id+'" name="'+name+'">发布</button>'+
//				 			  '<button class="btn btn-block btn-success deleteContent" id="" attr="'+id+'">删除</button>';
//				 	}
//				 	return str;
//				});
//				$("#rsc-list .list-body").html(temp(data.data));
//				//滚动条
//				$("#rsc-list").height(function(){
//					var height = $("#stateTabContent").height()-$("#stateTabContent .form-box").outerHeight();
//					return height + "px";
//				});
//				base.scroll({
//					container:$("#rsc-list")
//				});
//				//发布按钮
//				republishContent();
//				//点击标题资源预览
//				resourcePreview();
//				//撤销按钮
//				cancelContent();
//				//删除按钮
//				deleteContent();
//			}
//		});
//	}
	var getList = function(resStatus){
		var param = {};
		param.resStatus = resStatus;
		param.resName = $("#resourceName").val();
		if($("#catalogId").val()){
			param.catalogId = $("#catalogId").attr("dataId");
		}
		param.searchTime = $("#searchTime").val();
		var scrollY = $(".table-box").height() - 40;
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var userroleTable = $("#registerApprovalTable").DataTable({
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
			  		url:$.base+"/res/findAllResByCondition",
					type:"get",
					contentType:"application/json",
					data:function(d){
						var params = $.extend(param,{
							"page": Math.floor(d.start/d.length)+1,
							"size":10
						});
						return params;
					},
					dataFilter:function(data){
						var d = JSON.parse(data).data;
						return JSON.stringify(d);
					}
			  	},
			  	"columns":[
					{data:"data","sWidth":"100%"}
			  	],
			  	"columnDefs":[
			  		{
			  			"targets":0,
			  			"render":function(data,type,row,meta){
			  				//				//改变data里的值
							$.ajax({
			    				type:"get",
			    				url:$.base+"/dictItem/findAllIndustry",
			    				async:false,
			    				success:function(d){
			    					$.each(d.data, function(i,o) {
			            				if(o.dictItemCode == row.resIndustry){
			            					row.resIndustry = o.dictItemName;
			            				}
			            			});
			    				}
			    			});
			    			$.ajax({
			    				type:"get",
			    				url:$.base+"/dictItem/findAllMain",
			    				async:false,
			    				success:function(d){
			    					$.each(d.data, function(i,o) {
			            				if(o.dictItemCode == row.resMain){
			            					row.resMain = o.dictItemName;
			            				}
			            			});
			    				}
			    			});
			  				var str = "";
						 	if(row.resStatus=="已发布"){
						 		str = '<button class="btn btn-block btn-success cancelContent" id="" attr="'+row.id+'">撤销</button>';
						 	}else{
						 		str = '<button class="btn btn-block btn-success republishContent" id="" attr="'+row.id+'" name="'+row.resName+'">发布</button>'+
						 			  '<button class="btn btn-block btn-success deleteContent" id="" attr="'+row.id+'">删除</button>';
						 	}
							var content = '<li>'+
								'<div class="row">'+
									'<div class="col-md-7">'+
										'<div class="media">'+
											'<div class="media-left">'+
											   ' <a href="#">'+
											        '<img class="media-object" data-src="holder.js/72x72" src="'+$.base+"/res/downloadIcon?fileName="+row.iconRoot+'" data-holder-rendered="true" style="width: 72px; height: 72px;">'+
											    '</a>'+
											'</div>'+
											'<div class="media-body">'+
											    '<h4 style="cursor: pointer;" class="resourec_title" data-id="'+row.id+'">'+row.resName+'<span class="label label-info">视频</span></h4>'+
											    '<span>'+row.resAbstract+'</span>'+
											    '<div class="row-fluid more-info">'+
											   	    '<span>'+row.resIndustry+'</span>'+
											   	    '<span>'+row.resMain+'</span>'+
											    '</div>'+
											'</div>'+
										'</div>'+
									'</div>'+
									'<div class="col-md-5">'+
										'<div class="col-md-4 text-center">'+
											'<p class="header">发布单位</p>'+
											'<p class="content">'+row.createCompany+'</p>'+
										'</div>'+
										'<div class="col-md-4 text-center">'+
											'<p class="header">申请状态</p>'+
											'<p class="content">'+row.resStatus+'</p>'+
										'</div>'+
										'<div class="col-md-4 text-center">'+
											str+
											'<div class="time-info">'+row.submitTime+'</div>'+
										'</div>'+
									'</div>'+
								'</div>'+
							'</li>'
							return content;
			  			}
			  		}
			  	],
			  	 "drawCallback": function(settings) {
			  	 	//滚动条
			  	 	base.scroll({
						container:$(".dataTables_scrollBody")
					});
					//发布按钮
					republishContent();
					//点击标题资源预览
					resourcePreview();
					//撤销按钮
					cancelContent();
					//删除按钮
					deleteContent();
			  	 }
			});
		})
	}
	//查询
	function searchRsc(){
		$("#searchRsc").on("click",function(){
			$("#rsc-list .list-body").html("");
			getList($("#stateTabs li.active").attr("key"));
		});
		//点击不同状态查询
		$("#stateTabs li").on("click",function(){
			$(".form-box").find(".form-control").val("");
			getList($(this).attr("key"));
		})
	}
	
	//发布目录
	function republishCatalog(){
		//input的name必须为catalogTree
		$("input[name='catalogId']").unbind().on("click",function(){
			$("#catalogIdContent").parents(".select-tree-box").width(($(this).outerWidth()-2)+"px").show();
			base.scroll({
				container:$(".select-tree-box")
			});
			$.ajax({
				url:$.base+"/catalog/listAll",
				type:"get",
				success:function(data){
					var setting = {
						data: {
	                        key: {
	                            name: "cataName",
	                            pId:"pid"
	                        },
	                        simpleData: {
	                            enable: true,
	                            idKey: "id",
	                            pIdKey: "pid",
	                            rootPId: null
	                        }
	                    },
						callback:{
							onClick:zTreeCityCheck
						}
					};
					var zNodes = data.data;
					//生成树
					var treeObj = $.fn.zTree.init($("#catalogIdContent"),setting,zNodes);
				}
			})
			var i = 0;
			$(document).bind().on("click",function(e){
				i++;
				var target = $(e.target);
				if(target.closest("input[name='catalogId'],input[name='catalogId1']").length == 0&&(i!=0)){
					$(".select-tree-box").hide();
				}
			});
		})
	}

	function zTreeCityCheck(event, treeId, treeNode){
		$("#catalogIdContent").parents(".select-tree-box").hide();
		$("input[name='catalogId']").val(treeNode.cataName).attr("dataId",treeNode.id);
	}

	//点击资源标题预览
	var resourcePreview = function(){
		$(".resourec_title").unbind().on("click",function(){
			var resourceId = $(this).attr("data-id");
			$.ajax({
				type:"GET",
				url:"resourceManage/resourceRepublish/view.html",
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					//size可选————sm(小) md(中) lg(大) ,默认lg
					modalPage = {
						"header":"发布预览",
						"data":data,
						"callback":function(){
							detail(resourceId)
						}
					};
					initModal(modalPage);
				}
			});
			
		})
	}

	//撤销按钮
	var cancelContent = function(){
		$(".cancelContent").unbind().on("click",function(){
			var rid = $(this).attr("attr");
			initModal({
				header:"撤销",
				size:"sm",
				data:"确定撤销吗？",
				buttons:[
					{
						id:"confirm",
						cls:"btn-primary btn-confirm",
						content:"确定",
						clickEvent:function(){
							$(".modal").modal("hide");
							$.ajax({
								type:"get",
								url:$.base+"/res/revokeRes?rid="+rid,
								async:true,
								success:function(d){
									if(d.success){
										getList($("#stateTabs .active").attr("key"));
										infoModal("撤销成功");
									}else{
										infoModal("撤销失败");
									}
								}
							});
						}
					},{
						"id":"cancel",
						"cls":"btn-primary btn-confirm",
						"content":"取消"
					}
				]
			})
		})
	}

	//	删除按钮
	var deleteContent = function(){
		$(".deleteContent").unbind().on("click",function(){
			var rid = $(this).attr("attr");
			initModal({
				header:"删除",
				size:"sm",
				data:"确定删除吗？",
				buttons:[
					{
						id:"confirm",
						cls:"btn-primary btn-confirm",
						content:"确定",
						clickEvent:function(){
							$(".modal").modal("hide");
							$.ajax({
								type:"get",
								url:$.base+"/res/unSubmit?rid="+rid,
								async:true,
								success:function(d){
									if(d.success){
										getList($("#stateTabs .active").attr("key"));
										infoModal("删除成功");
									}else{
										infoModal("删除失败");
									}
								}
							});
						}
					},{
						"id":"cancel",
						"cls":"btn-primary btn-confirm",
						"content":"取消"
					}
				]
			})
		})
	}

	//发布
	function republishContent(id){
		$(".republishContent").unbind().on("click",function(){
			ztreeName = $(this).attr("name")
			var _this = this;
			$.ajax({
				type:"GET",
				url:"resourceManage/resourceRepublish/republishForm.html",
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var excute = {};
						/*点击完成调用接口*/
					    excute.saveBack = function(){
							var treeObj = $.fn.zTree.getZTreeObj("treeDemo2");
							var nodes = treeObj.transformToArray(treeObj.getNodes());
							var timestamp = parseInt(Math.random()*1000000)+""+Date.parse(new Date());
//							var length = 0;
							$.each(nodes,function(i,o){
								if(o.parent_id == null){
									length++;
								}
							})
//							if(length==1){
//								var deviceId = nodes[0].deviceId;
//								$.each(nodes,function(i,o){
//									if(o.parent_id == deviceId){
//										nodes[i].parent_id=timestamp;
//									}
//								})
//								nodes[0].deviceId = timestamp;
//							}else if(length>1){
								$.each(nodes,function(i,o){
									if(o.parent_id == null){
										o.parent_id = timestamp;
									}
								})
								nodes.push({deviceId:timestamp,parent_id:null,dsName:ztreeName})
//							}
							var obj = {
								rid:$(_this).attr("attr"),
								equipmentCatalogBeans:nodes,
								realDays:$(".tempValue").attr("val")!="时间段"?$(".tempValue").attr("val"):$(".tempValue").val(),
								histDays:$(".tempValue1").attr("val")!="时间段"?$(".tempValue1").attr("val"):$(".tempValue1").val(),
								realControl:'0',
								record:'0',
								realSnap:'0',
								histSnap:'0',
								download:'0'
							};
							var real = $("#real_operate").attr("val");
							var history = $("#history_operate").attr("val");
							if(real){
								real = real.split(",");
								$.each(real, function(i,o){
									switch(o){
										case "1":
											obj.realControl = "1";
											break;
										case "2":
											obj.record = "1";
											break;
										case "3":
											obj.realSnap = "1";
									}
								});
							}
							if(history){
								history = history.split(",");
								$.each(history,function(i,o){
									switch(o){
										case "1":
											obj.download = "1";
											break;
										case "2":
											obj.histSnap = "1";
									}
								})
							}
							$.ajax({
								url:$.base+"/res/publishRes",
								type:"post",
								data:JSON.stringify(obj),
								contentType:"application/json",
								success:function(d){
									if(d.success){
										$(".modal").modal("hide");
										getList($("#stateTabs .active").attr("key"));
										infoModal("发布成功")
									}else{
										infoModal(d.message)
									}
								}
							})
						}
					    excute.callback = function(){
					    	var rid = $(_this).attr("attr")
					    	detail(rid);
					    	//时间段双日历插件
					    	calendarTime()
					    }
					    excute.stepBack = function(){
					    	var treeObj = $.fn.zTree.getZTreeObj("treeDemo2");
							var nodes = treeObj.getNodes();
					    	var setting = {
									data: {
				                         key: {
				                            name: "dsName",
				                            pId:"parent_id",
				                        },
				                        simpleData: {
				                            enable: true,
				                            idKey: "deviceId",
				                            pIdKey: "parent_id",
				                            rootPId: null
				                        }
				                    }
								};
							var zNodes = setIcon(nodes);
							//生成树
							var treeObj = $.fn.zTree.init($("#detailTree"),setting,nodes);
							//添加滚动条
							//$("#treeBox").height(225).width(200);
							base.scroll({
								container:$("#treeBox"),
							    axis:"xy"
							})
					    }
					var modalPage = {
								"id":id,
								"header":"资源发布",
								"data":data,
							};
					setStepList(".modal",modalPage,excute);
					$(".modal").on("shown.bs.modal",function(){
						/*媒体源下拉赋值*/
//						mediaSelect();
						selectBox()
						treeTable();
						getDragTree();
						setChecked();
						multiTree();
						multiTree1();
					})
				}
			});
		})
	}

	//详情展示赋值
	var detail = function(rid){
		$.ajax({
    		type:"get",
    		url:$.base+"/res/findOneRes?rid="+rid,
    		async:true,
    		success:function(d){
    			$("#content-3 .media-object").attr("src",$.base+"/res/downloadIcon?fileName="+d.data.iconRoot)
    			var data = d.data;
    			if(data.resLevel==0){
    				data.resLevel = "完全共享";
    			}else{
    				data.resLevel = "部分共享";
    			}
    			$.ajax({
    				type:"get",
    				url:$.base+"/dictItem/findAllIndustry",
    				async:false,
    				success:function(d){
    					$.each(d.data, function(i,o) {
            				if(o.dictItemCode == data.resIndustry){
            					data.resIndustry = o.dictItemName;
            				}
            			});
    				}
    			});
    			$.ajax({
    				type:"get",
    				url:$.base+"/dictItem/findAllMain",
    				async:false,
    				success:function(d){
    					$.each(d.data, function(i,o) {
            				if(o.dictItemCode == data.resMain){
            					data.resMain = o.dictItemName;
            				}
            			});
    				}
    			});
    			base.form.init(data,$("#content-3"))
    		}
    	});
	}
	var calendarTime = function(){
		var val1 = $("#realDays").val();
		var val2 = $("#histDays").val();
		var temp = "";
		var temp1= "";
		$("#date").off().on("click",function(e){
			var e = e || window.event;
			target = e.target.id;
			laydate.render({
				elem: '#date',
				show: true,
				range:"~",
				type: 'datetime',
				closeStop: '#realDays',
				done: function(value, date, endDate){
					if($("#realDays").val()=="时间段"){
						$(".tempValue").attr("val",value);
					}
					temp = value;
					$("#realDays").val("");
					$(".tempValue").val(value)
				}
			});
		});
		$("#date1").off().on("click",function(e){
			var e = e || window.event;
			target = e.target.id;
			laydate.render({
				elem: '#date1',
				show: true,
				range:"~",
				type: 'datetime',
				closeStop: '#histDays',
				done: function(value, date, endDate){
					if($("#histDays").val()=="时间段"){
						$(".tempValue1").attr("val",value);
					}
					temp1 = value;
					$("#histDays").val("");
					$(".tempValue1").val(value)
				}
			});
		})
		$("#realDays").on("change",function(){
			var val = $(this).val();
			var valText = $("#realDays option:selected").text();
			$(".tempValue").css("visibility","visible");
			$(".tempValue").val(valText)
			$(".tempValue").attr("val",val);
			$("#realDays").val("");
			if(val!="1"&&val!="0"&&val!="2"&&val!="3"&&val!="-1"){
				$("#date").trigger("click");
			}
		})
		$("#histDays").on("change",function(){
			var val = $(this).val();
			var valText = $("#histDays option:selected").text();
			$(".tempValue1").css("visibility","visible");
			$(".tempValue1").val(valText)
			$(".tempValue1").attr("val",val);
			$("#histDays").val("");
			if(val!="1"&&val!="0"&&val!="2"&&val!="3"&&val!="-1"){
				$("#date1").trigger("click");
			}
		})
		$(".modal-body").off().on("click",function(e){
			//点击laydate
			var e = e || window.event;
			if($(".layui-laydate").length == 0){
				return; //如果时间插件未曾出现过就return
			}
			if(target=="date" && e.target.className!="layui-laydate"){ //说明点击的是实时观看
				if(temp){
					$(".tempValue").val(temp);
				}
			}else if(target=="date1" && e.target.className!="layui-laydate"){ //说明点击的是历史观看
				if(temp1){
					$(".tempValue1").val(temp1);
				}
			}

		})
	}
	//设置多选树selectBox
	function selectBox(){
		selectTree("mediaJsonContent",$.base+"/media/findLowLevel");
		selectTree("realContent","../../json/resourceManage/realOpt.json");
		selectTree("historyContent","../../json/resourceManage/historyOpt.json");
		selectTree("eqLevelContent","../../json/resourceManage/jkdw.json");
		selectTree("positionContent","../../json/resourceManage/sbwzlx.json");
		var clickName = "";
		var box = "";
		var i = 0;
		$(document).bind("click",function(e){
			var target = $(e.target);
			if(target.closest("input[name='"+clickName+"'],.select-tree-box").length == 0&&i!=0){
				$("input[name='"+clickName+"']").siblings(".select-tree-box").hide();
			}
			clickName = (e.target.name=="mediaJson"||e.target.name=="history_operate"||e.target.name=="eqLevelInput"||e.target.name=="positionInput")?e.target.name:clickName;
			i++;
		})
		$(".select-tree-input").on("click",function(){
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
						key: {
                            name: "mediaName",
                        }
					},
					callback: {
						onCheck: zTreeOnCheck
					}
				};
				var zNodes = data.data;
				//生成树
				var treeObj = $.fn.zTree.init($("#"+obj),setting,zNodes);
				//获取树的全部节点
//				var nodes = treeObj.getNodes();
				//默认选中树的第一个节点
//				if(nodes.length > 0){
//					treeObj.selectNode(nodes[0].children[0]);
//				}
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
				elem.push({"name":item.mediaName,"code":item.deviceId});
			}
		});
		var vals = "";
		var codes = ""
		if(elem.length>0){
			$.each(elem,function(i1,o1){
				i1==0?vals+=o1.name:vals+=","+o1.name;
				i1==0?codes+=o1.code:codes+=","+o1.code;
			});
		}
		$("#"+treeId).parents(".select-tree-box").siblings(".select-tree-input").val(vals);
		$("#"+treeId).parents(".select-tree-box").siblings(".select-tree-input").attr("val",codes);
		multiTree();
	}
//	下拉框改变数据
	var getDragTree = function(){
		$(".equipment-input").on("change",function(){
			$(".equipment-box input[name='eqName'],.equipment-box #eqLevel,.equipment-box #position").hide();
			var param = {
				eqLevel:'',
				posit:"",
				eqName:""
			};
			switch($(this).val()){
				case "1":
					$(".equipment-box #eqLevel").show();
					break;
				case "2":
					$(".equipment-box #position").show();
					break;
				case "3":
					$(".equipment-box input[name='eqName']").show();
					$(".equipment-box input[name='eqName']").on("change",function(){
						param.eqName = $(".equipment-box input[name='eqName']").val();
						multiTree(param)
					});
					break;
			}
		})
	}
	//拖拽树
	function multiTree(parm){
		var mediaJson = $("#mediaJson").attr("val")?$("#mediaJson").attr("val"):"";
		var eqLevel = parm?parm.eqLevel:"";
		var posit = parm?parm.posit:"";
		var eqName = parm?parm.eqName:"";
		if($(".equipment-input").val()==1){
			eqLevel = $("#eqLevelInput").attr("val");
		}else if($(".equipment-input").val()==2){
			posit = $("#positionInput").attr("val");
		}
		var url = $.base+"/equipment/findByFilter?mediaJson="+mediaJson+"&eqLevel="+eqLevel+"&position="+posit+"&eqName="+eqName;
		$.ajax({
			type:"GET",
			url:url,
			error:function(){
				
			},
			success:function(data){
				var setting1 = {
					data: {
                        key: {
                            name: "dsName",
                            pId:"parent_id"
                        },
                        simpleData: {
                            enable: true,
                            idKey: "deviceId",
                            pIdKey: "parent_id",
                            rootPId: null
                        }
                    },
					edit: {
						enable: true,
						showRemoveBtn: false,
						showRenameBtn: false,
						drag:{
							isCopy : true,
							isMove : false,
							inner:false,
							next:false,
							prev:false
						}
					},
					callback: {
						beforeDrag: beforeDrag,
						beforeDrop: beforeDrop
					}
				};
				var zNodes = setIcon(data.data);
				$.fn.zTree.init($("#treeDemo"), setting1, zNodes);
				//滚动条
				base.scroll({
					container:$(".ztrees-container")
				});	
			}
		});
	}
	function multiTree1(){
		var setting2 = {
				data: {
                     key: {
                        name: "dsName",
                        pId:"parent_id"
                     },
                    simpleData: {
                        enable: true,
                        idKey: "deviceId",
                        pIdKey: "parent_id",
                        rootPId: null
                    }
                },
				edit: {
					enable: true,
					drag:{
						isCopy : false,
						isMove : true
					},				
					showRemoveBtn: true,
					showRenameBtn: true
				},
				view: {
					addHoverDom: addHoverDom,
					removeHoverDom: removeHoverDom,
				}
			};
		$.fn.zTree.init($("#treeDemo2"), setting2);
	}
	//改变目录树中设备的图标
	var setIcon = function(zNodes){
		$.each(zNodes,function(index,item){ 
           if(item.dsType==5){ 
               item.iconSkin = "diy01"; 
           } 
           if(item.children){ 
               setIcon(item.children); 
           } 
       }); 
       return zNodes; 
	}
	var newCount = 1;
	function addHoverDom(treeId, treeNode) {
		newCount++
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
		var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
			+ "' title='add node' onfocus='this.blur();'></span>";
		sObj.after(addStr);
		var btn = $("#addBtn_"+treeNode.tId);
		if (btn) btn.bind("click", function(){
			var zTreeObj = $.fn.zTree.getZTreeObj("treeDemo2");
			zTreeObj.addNodes(treeNode, {deviceId:(100 + newCount), parent_id:treeNode.deviceId});
			return false;
		});
	};

	function removeHoverDom(treeId, treeNode) {
		$("#addBtn_"+treeNode.tId).unbind().remove();
	};

	function beforeDrag(treeId, treeNodes) {
		for (var i=0,l=treeNodes.length; i<l; i++) {
			if (treeNodes[i].drag === false) {
				return false;
			}
		}
		return true;
	}
	
	function beforeDrop(treeId, treeNodes, targetNode, moveType) {
		return targetNode ? targetNode.drop !== false : true;
	}
	//表格树
	var treeTable = function(){
		resize();
		$(".switch").click(function(){
		    if($(this).parents(".outer").is(".open")){
		      $(this).parents(".outer").removeClass("open").next(".inner").removeClass("open");
		    }else{
		      $(this).parents(".outer").addClass("open").next(".inner").addClass("open");
		    }
		});
		
		$("tr.outer, tr.leaf").click(function(){
		    $("tr.focus").removeClass("focus");
		    $(this).addClass("focus");
	 	});
	}
	
	var resize = function(){
    	$(".list tr th").each(function(index){
	     	var width = $(this).width();
	     	$(".list").each(function(i1,o1){
	     		$(this).find("tr td:eq(" + index + ")").css("width", width);
//		      	$(".list tr td:eq(" + index + ")").css("width", width);
	    		$(this).find(".leaf td:eq(" + index + ")").css("width", width);
	     	})

	    });
    }
	//勾选
	function setChecked(){
		//全选
		$("input.checkAll").unbind().on("click",function(){
			var attr = $(this).attr("attr");
			var _this = this;
			$(".checkSingle").each(function(index,item){
				if($(item).attr("attr")==attr){
					if($(_this).is(":checked")==true){
						$(item).prop("checked",true);
					}else{
						$(item).prop("checked",false);
					}
				}

			});
		});
		$("input.checkSingle").unbind().on("click",function(){
			var attr = $(this).attr("attr");
			if($("input.checkSingle[attr="+attr+"]").length == $("input.checkSingle[attr="+attr+"]:checked").length){
				$(".checkAll[attr="+attr+"]").prop("checked",true);
			}else{
				$(".checkAll[attr="+attr+"]").prop("checked",false);
			}
		});
	}
	//清空设置
	function clearForm(){
		$("#clearForm").on("click",function(){
			$(".form-box").find(".form-control").val("");
		})
	}
	return {
		run:function(){
			getList();
			searchRsc();
			republishCatalog();
			clearForm();
		}
	}

});
	