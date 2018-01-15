define(["bootstrap","template","base","date5.0"],function(bootstrap,template,base,laydate){
	var treetable_simple_data = {};
	var treeBox = 0 ,treeBox1 = 0 ;
//	function getList(state){
	//		var size = 9999;
//		var page = 1;
//		var type = state?state:$("#stateTabs .active a").attr("status");
//		var resName = encodeURI($("input[name='resName']").val());
//		var flag=0;
//		var catalogid =  $("input[name='catalogid']").attr("catalogid")?$("input[name='catalogid']").attr("catalogid"):"";
//		var searchTime =  $("select[name='searchTime']").val();
//		$.ajax({
//			type:"GET",
//			url:$.base + "/apprlist/findAllPageByCondition?type="+type+"&size="+size+"&page="+page+"&resName="+resName+"&catalogid="+catalogid+"&searchTime="+searchTime,
//			error:function(){
//				alert("出错了");
//			},
//			success:function(data){
//				if(data.code!=0){
//					infoModal(data.message);
//				}else{
//					var res = data.data.data;
//					if(res.length==0){
//						$("#rsc-list .list-body").html("<li class='text-center'>暂无数据</li>");
//					}else{
//						var temp = template.compile($("#rsc-list-template").html());
//						$(".tab-content").height($(".content-padding-box").height()-40);
//						template.registerHelper("img",function(value){
//							var url = $.base+"/res/downloadIcon?fileName="+value;
//							return '<img class="media-object" data-src="holder.js/72x72" src="'+url+'" data-holder-rendered="true" style="width: 72px; height: 72px;">';
//						});
//						template.registerHelper("button",function(value,id){
//							var buttons = "";
//							if(value == 1){
//								buttons = '<button class="btn btn-primary examineBtn" dataid="'+id+'">待审批</button>';
//							}else if(value==2){
//								buttons = '<button class="btn btn-success">已审批</button>';
//							}else if(value == 3){
//								buttons = '<button class="btn btn-danger">已拒绝</button>';
//							}
//							return buttons;
//						});
//						$("#rsc-list .list-body").html(temp(res));
//						//滚动条
//						base.scroll({
//							container:$("#rsc-list")
//						});
//					}
//				}
//				approvalConfirm();
//			}
//		});
//	}
	var getList = function(state){
		flag=0;
		var param = {};
		param.type = state?state:$("#stateTabs .active a").attr("status");
		param.resName = encodeURI($("input[name='resName']").val());
		param.catalogid =  $("input[name='catalogid']").attr("catalogid")?$("input[name='catalogid']").attr("catalogid"):"";
		param.searchTime =  $("select[name='searchTime']").val();
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
			  		url:$.base + "/apprlist/findAllPageByCondition",
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
			  				var buttons = "";
							if(row.apprStatus == 1){
								buttons = '<button class="btn btn-primary examineBtn" dataid="'+row.id+'">待审批</button>';
							}else if(row.apprStatus==2){
								buttons = '<button class="btn btn-success">已审批</button>';
							}else if(row.apprStatus == 3){
								buttons = '<button class="btn btn-danger">已拒绝</button>';
							}
			  				var content = '<li>'+
											'<div class="row">'+
												'<div class="col-md-7">'+
													'<div class="media">'+
														'<div class="media-left">'+
														    '<a href="#">'+
																'<img class="media-object" data-src="holder.js/72x72" src="'+$.base+"/res/downloadIcon?fileName="+row.resIcon+'" data-holder-rendered="true" style="width: 72px; height: 72px;">'+
														    '</a>'+
														'</div>'+
														'<div class="media-body">'+
														    '<h4>'+row.resName+'<span class="label label-info">'+row.resType+'</span></h4>'+
														    '<span>'+row.resAbstract+'</span>'+
														    '<div class="row-fluid more-info">'+
														   	    '<span>'+row.mainCategory+'</span>'+
														   	    '<span'+row.hyCategory+'</span>'+
														    '</div>'+
														'</div>'+
													'</div>'+
												'</div>'+
												'<div class="col-md-5">'+
													'<div class="col-md-4 text-center">'+
													'</div>'+
													'<div class="col-md-4 text-center">'+
														'<p class="header">资源状态</p>'+
														'<p class="content">'+row.resStatus+'</p>'+
													'</div>'+
													'<div class="col-md-4 text-center">'+
														buttons+
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
					approvalConfirm();
			  	 }
			});
		})
	}
	//查询
	function searchRsc(){
		$(".btn-search").on("click",function(){
			$("#rsc-list .list-body").html("");
			getList();
		});
		$("#stateTabs li").on("click",function(){
			var type = $(this).find('a').attr('status');
			getList(type);
		})
	}
	
	//发布目录
	function republishCatalog(){
		$("input[name='catalogid']").unbind().on("click",function(){
			$(".select-tree-box").width(function(){
				return $("input[name='catalogid']").outerWidth()+"px";
			})
			$("#catalogidContent").parents(".select-tree-box").show();
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
					var treeObj = $.fn.zTree.init($("#catalogidContent"),setting,zNodes);
				}
			})
		})
		var i = 0;
		//点击其他地方，关闭下拉树
		$(document).bind("click",function(e){
			var target = $(e.target);
			if(target.closest("input[name='catalogid'],.select-tree-box").length == 0&&i!=0){
				$("input[name='catalogid']").siblings(".select-tree-box").hide();
			}
			i++;
		})

	}
	function zTreeCityCheck(event, treeId, treeNode){
		$("#catalogidContent").parents(".select-tree-box").hide();
		$("#catalogid").val(treeNode.cataName)
		$("#catalogid").attr("catalogid",treeNode.id)
	}
	//确认审批
	var approvalConfirm = function(id){
		$(".examineBtn").unbind().on("click",function(){
			var _this = this;
			$.ajax({
				type:"GET",
				url:"resourceManage/subscriptionApproval/approvalForm.html",
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var id = $(_this).attr("dataid");
					var excute = {};
						/*点击完成调用接口*/
						excute.saveBack = function(){
							//审核
						var apprMsg = $("#approvalAdvice").val();
						var apprStatus = $("#content-4 select[name='approvalResult']").val();
						var data = {}
						data.id = id;
						data.apprMsg = apprMsg;
						data.apprStatus = apprStatus;
						data.subscribeFormPrvs = treetable_simple_data;
						$.ajax({
							type:"post",
							url:$.base+"/apprlist/apprResSub",
							data:JSON.stringify(data),
							async:true,
							contentType:"application/json",
							success:function(d){
								if(d.success){
									$(".modal").modal("hide");
									getList();
									infoModal("审批成功");
								}
							}
						});
					}
				    excute.callback = function(){
					    	//请求方信息
					    	$.ajax({
					    		url:$.base+"/apprlist/findOneResSub?id="+id,
					    		type:"get",
					    		success:function(d){
					    			base.form.init(d.data,$("#content-1"))
					    			if(d.data.applyDocPath){
					    				$("#applyDocPath").attr("href",$.base+"/apprlist/downloadZip?fileName="+d.data.applyDocPath);
					    			}
					    		}
					    	})
					    	//元数据信息
					    	$.ajax({
					    		url:$.base+"/apprlist/findOneResource?id="+id,
					    		type:"get",
					    		success:function(d){
					    			base.form.init(d.data.vr,$("#dataInfo"))
					    			$("#content-2 .media-object").attr("src",$.base+"/res/downloadIcon?fileName="+d.data.vr.iconRoot)
					    			var setting = {
						                    data: {
						                        key: {
						                            name: "ds_name",
						                            pId:"parent_id"
						                        },
						                        simpleData: {
						                            enable: true,
						                            idKey: "ds_code",
						                            pIdKey: "parent_id",
						                            rootPId: null
						                        }
						                    }
						                };
						                var zNodes = d.data.vds;
						                var nos = setIcondata(zNodes)
                                    //生成树
                                    var treeObj = $.fn.zTree.init($("#resouceTree"), setting, nos);
                                    var width = $("#content-2").width()/4-15;
                                    $("#treeBox").parent().width(200+"px").height(205+"px");
                                    //debugger
                                    base.scroll({
                                        container:$("#treeBox").parent(),
                                        axis:"yx"
                                        // scrollbarPosition:"outside"
                                    });
					    		}
					    	})
					    	
					    	//请求权限处理
					    	$.ajax({
					    		url:$.base+"/apprlist/findOneResourceAndPrv?id="+id,
					    		type:"get",
					    		success:function(d){
					    			treetable_simple_data = d.data.vlist;
					    			setTreeCheck(d.data.vlist);
					    		}
					    	})
					    	calendarTime();
					    	operateTreeData()
					    }

					var modalPage = {
								"id":id,
								"header":"订阅审批",
								"data":data,
							};
					setStepList(".modal",modalPage,excute);
					$(".modal").on("shown.bs.modal",function(){
						treeBox1 = 0;
					})
				}
			});
		});
	}
	var setIcondata = function(zNodes){
			$.each(zNodes,function(index,item){
				if(item.ds_type==5){
					item.iconSkin = "diy01";
				}
				if(item.children!=undefined){
					setIcon(item.children);
				}
			});
			return zNodes;
		}
	//设置左侧树
	var setTreeCheck = function(data){
		//滚动条
		//用$("#content-2")不用$("#content-3")
		$("#treeBox1").parent().height(225).width(270);
		if(treeBox1==0){
			base.scroll({
				container:$("#treeBox1").parent(),
				axis:"yx"
			});
		}
		treeBox1++;
		var setting = {
			data: {
				key:{
					name:"dsName"
				},
				simpleData: {
					enable: true,
					idKey:"dsCode",
					pIdKey:"pid"
				}
			},
			edit: {
				enable: true,
				drag:{
					isCopy : false,
					isMove : false
				},				
				showRemoveBtn: showRemove,
				showRenameBtn: false,
				removeTitle: "删除"
			},
			callback:{
				onClick:zTreeOnClick
			}
		};
		if(data.length==0){
			isSubscribe = false;
			$("#resouceTreeCheck").html("暂无数据");
		}else{
			var zNodes = setIcon(data);
			//生成树
			var treeObj = $.fn.zTree.init($("#resouceTreeCheck"),setting,zNodes);
            //获取树的全部节点
			var nodes = treeObj.getNodes();
			//默认选中树的第一个节点
			if(nodes.length > 0){
				treeObj.selectNode(nodes[0]);
				zTreeOnClick("","",nodes[0]);
			}
		}
	}
	function showRemove(treeId, treeNode){
		if(treeNode.level == 0){
			return false;	
		}else{
			return true;
		}
	}
	var arr = [];
	function zTreeOnClick(treeId,treeNode,obj){
		treetableCheckObj = obj;
		$.each(treetable_simple_data, function(i,o) {
			if(obj.dsCode == o.dsCode){
				if(o.realTime!=1&&o.realTime!=2&&o.realTime!=3&&o.realTime!=0&&o.realTime!='-1'){
					$("#realDays").val("");
					$(".tempValue").val(o.realTime).css("visibility","visible")
				}else{
					$(".tempValue").val(o.realTime)
					$("#realDays").val(o.realTime)
				}
				if(o.histTime!=1&&o.histTime!=2&&o.histTime!=3&&o.histTime!=0&&o.histTime!='-1'){
					$("#histDays").val("");
					$(".tempValue1").val(o.histTime).css("visibility","visible")
				}else{
					$("#histDays").val(o.histTime);
					$(".tempValue1").val(o.histTime)
				}
				
				o.realControl=="1"?$("input[name='realControl']").prop("checked",true):$("input[name='realControl']").prop("checked",false);
				o.record=="1"?$("input[name='record']").prop("checked",true):$("input[name='record']").prop("checked",false);
				o.realSnap=="1"?$("input[name='realSnap']").prop("checked",true):$("input[name='realSnap']").prop("checked",false);
				o.download=="1"?$("input[name='download']").prop("checked",true):$("input[name='download']").prop("checked",false);
				o.hisSnap=="1"?$("input[name='histSnap']").prop("checked",true):$("input[name='histSnap']").prop("checked",false);
			}
		});
		arr = [];
		getId(obj);
	}
	var getId = function(node){
   		arr.push(node.dsCode);
   		if(node.children){	
			$.each(node.children,function(i1,o1){
				getId(o1);
			})
	    }
	}
	//操作权限改变树数据
	function operateTreeData(){
		$("#realDays,#histDays").on("change",function(){
			operateEvent("","");
		})
		$("input[name='realControl'],input[name='record'],input[name='realSnap'],input[name='download'],input[name='histSnap']").on("click",function(){
			operateEvent("","");
		})
	}
	function operateEvent(value,value1){
		$.each(arr, function(i,o) {
			$.each(treetable_simple_data, function(i1,o1) {
				if(o == o1.dsCode){
					treetable_simple_data[i1].realTime = value.indexOf("~")!=-1?$(".tempValue").val():$(".tempValue").attr("val");
					treetable_simple_data[i1].histTime =value1.indexOf("~")!=-1?$(".tempValue1").val():$(".tempValue1").attr("val");
					treetable_simple_data[i1].realControl = $("input[name='realControl']").prop("checked")?"1":"0";
					treetable_simple_data[i1].record = $("input[name='record']").prop("checked")?"1":"0";
					treetable_simple_data[i1].realSnap = $("input[name='realSnap']").prop("checked")?"1":"0";
					treetable_simple_data[i1].download = $("input[name='download']").prop("checked")?"1":"0";
					treetable_simple_data[i1].histSnap = $("input[name='histSnap']").prop("checked")?"1":"0";
				}
			});
		});
	}
	
//双日历插件
	var calendarTime = function(){
		var val1 = $("#realDays").val();
		var val2 = $("#histDays").val();
		var temp="",temp1="",target="";
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
					$("#realDays").val("");
					$(".tempValue").val(value);
					temp = value;
					operateEvent(value);
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
					$("#histDays").val("");
					$(".tempValue1").val(value);
					temp1 = value;
					operateEvent("",value);
				}
			});
		})
		$("#realDays").on("change",function(){
			var val = $(this).val();
			// flag=1;
			// if(val==0 || val==1 || val==2 || val==3){
			// 	return;
			// }
			var valText = $("#realDays option:selected").text();
			$(".tempValue").attr("realVal",val).css("visibility","visible")//option value
			$(".tempValue").val(valText);//option的text
			$(".tempValue").attr("val",val);//时间段
			$("#realDays").val("");
			if(val!="1"&&val!="0"&&val!="2"&&val!="3"&&val!="-1"){
				//temp = $(".tempValue").val()
				//$(".tempValue").val("");
				$("#date").trigger("click");
			}
		})
		$("#histDays").on("change",function(){
			var val = $(this).val();
			// if(val==0 || val==1 || val==2 || val==3){
			// 	return;
			// }
			var valText = $("#histDays option:selected").text();
			$(".tempValue1").attr("realVal",val).css("visibility","visible")//option value
			$(".tempValue1").val(valText);
			$(".tempValue1").attr("val",val);//时间段
			$("#histDays").val("");
			if(val!="1"&&val!="0"&&val!="2"&&val!="3"&&val!="-1"){
				//temp1 = $(".tempValue1").val()
				//$(".tempValue1").val("")
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
	//改变目录树中设备的图标
	var setIcon = function(zNodes){
		$.each(zNodes,function(index,item){
			if(item.dsType==5){
				item.iconSkin = "diy01";
			}
			if(item.children!=undefined){
				setIcon(item.children);
			}
		});
		return zNodes;
	}
	//重置
	function clearForm(){
		$(".btn-clear").on("click",function(){
			$(".form-box").find(".form-control").each(function(index,item){
				if($(item)[0].tagName=="INPUT"){
					$(item).val("");
					$(item).attr("resId","");
				}else if($(item)[0].tagName=="SELECT"){
					$(item).val(0);
				}
			})
			getList($("#stateTabs li.active").find("a").attr("status"));
		});
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
	