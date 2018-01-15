define(["bootstrap","template","base","tree","app/commonApp","date5.0"],function(bootstrap,template,base,tree,common,laydate){
    var treetable_data = {};
    var treetable_simple_data = {};
	var fileName = "";
	var isSubscribe = true;
	var treetableCheckObj = {};
	//获取详情
//	function getList(state){
//		var res = encodeURI($("input[name='res']").val());
//		var catalogId =  $("input[name='catalogId']").attr("resId");
//		var resMain =  $("input[name='themeId']").attr("resId");
//		var resIndustry =  $("input[name='industryId']").attr("resId");
//		var searchTime =  $("select[name='searchTime']").val();
//		$("#rsc-list .list-body").html("");
//		$.ajax({
//			type:"GET",
//			url:$.base + "/res/find?resName="+res+"&catalogId="+catalogId+"&searchTime="+searchTime+"&subStatus="+state+"&resMain="+resMain+"&resIndustry="+resIndustry,
//			error:function(){
//				alert("出错了");
//			},
//			success:function(data){
//				var res = data.data;
//				if(data.code!=0){
//					infoModal(data.message);
//				}else{
//					if(res.length==0){
//						$("#rsc-list .list-body").html("<li class='text-center'>暂无数据</li>");
//					}else{
//						var temp = template.compile($("#rsc-list-template").html());
//						$(".tab-content").height($(".content-padding-box").height()-40);
//						template.registerHelper("status",function(value){
//							if(value==0){
//								return "未订阅";
//							}else if(value==1){
//								return "待审核";
//							}else if(value==2){
//								return "审核通过";
//							}else if(value==3){
//								return "已拒绝";
//							}else if(value==4){
//								return "去订阅";
//							}else if(value==5){
//								return "已共享";
//							}
//							return value;
//						});
//						template.registerHelper("button",function(value,data){
//							var buttons = "";
//							if(value==0||value==3||value==4){
//								buttons = "订阅";
//							}else if(value==1||value==2||value==5){
//								buttons = "去订阅";
//							}
//							return buttons;
//						});
//						template.registerHelper("type",function(value){
//							if(value==9){
//								return "视频";
//							}
//						});
//						template.registerHelper('if_condition', function(v1,index,opts) {
//							var data = opts.data.root;
//							if(v1==2||v1==5){
//								return '<button class="btn btn-block btn-success btn-share" resSubscribeId="'+data[index].resSubscribeId+'" resId="'+data[index].resId+'">共享设置</button>';
//							}
//						});
//						$("#rsc-list .list-body").html(temp(res));
//						//滚动条
//                      $("#rsc-list").height($(".tab-content").height()-$(".form-box").outerHeight());
//						base.scroll({
//							container:$("#rsc-list")
//						});
//						isSubscribes();
//						shareSetting();
////						点击标题显示详情
//						subscriptionDetails();
//					}
//				}
//			}
//		});
//	}
	var getList = function(state){
		var param = {};
		param.res = encodeURI($("input[name='res']").val());
		param.catalogId =  $("input[name='catalogId']").attr("resId");
		param.resMain =  $("input[name='themeId']").attr("resId");
		param.resIndustry =  $("input[name='industryId']").attr("resId");
		param.searchTime =  $("select[name='searchTime']").val();
		param.subStatus=state;
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
			  		url:$.base + "/res/find",
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
			  				if(row.resType==9){
								row.resType = "视频"
							}
			  				var state='';
			  				if(row.state==0){
								state="未订阅";
							}else if(row.state==1){
								state="待审核";
							}else if(row.state==2){
								state="审核通过";
							}else if(row.state==3){
								state="已拒绝";
							}else if(row.state==4){
								state="去订阅";
							}else if(row.state==5){
								state="已共享";
							}
							var buttons = "";
							if(row.state==0||row.state==3||row.state==4){
								buttons = "订阅";
							}else if(row.state==1||row.state==2||row.state==5){
								buttons = "去订阅";
							}
							var but = '';
							if(row.state==2||row.state==5){
								but = '<button class="btn btn-block btn-success btn-share" resSubscribeId="'+row.resSubscribeId+'" resId="'+row.resId+'">共享设置</button>';
							}
			  				var content = '<li>'+
											'<div class="row">'+
												'<div class="col-md-7">'+
													'<div class="media">'+
														'<div class="media-left">'+
														    '<a href="#">'+
														        '<img class="media-object" data-src="holder.js/72x72" src="../../support/resource/downloadIcon?fileName='+row.iconRoot+'" data-holder-rendered="true" style="width: 72px; height: 72px;">'+
														    '</a>'+
														'</div>'+
														'<div class="media-body">'+
														    '<h4 class="subscriptionDetails" id="'+row.resSubscribeId+'" state="'+row.state+'" style="cursor:pointer;">'+row.resName+'<span class="label label-info">'+row.resType+'</span></h4>'+
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
														'<p class="header">资源状态</p>'+
														'<p class="content">'+state+'</p>'+
													'</div>'+
													'<div class="col-md-4 text-center">'+
														'<p class="header">发布时间</p>'+
														'<p class="content">'+row.createTime+'</p>'+
													'</div>'+
													'<div class="col-md-4 text-center">'+
														'<button class="btn btn-block btn-success btn-subscribe" resSubscribeId="'+row.resSubscribeId+'" resId="'+row.resId+'">'+buttons+'</button>'+
														but+
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
					isSubscribes();
					shareSetting();
//					点击标题显示详情
					subscriptionDetails();
			  	 }
			});
		})
	}
	//ul的点击事件
	function setLiClick(){
		$("#stateTabs li").on("click",function(){
			getList($(this).find("a").attr("status"));
		})
	}
	//订阅
	var isSubscribes = function(){
		$(".btn-subscribe").unbind().on("click",function(){
			var resId = $(this).attr("resId");
			var resSubscribeId = $(this).attr("resSubscribeId");
			var _this = this;
			if($(_this).html()=="订阅"){
				$.ajax({
					type:"GET",
					url:"resourceManage/resourceSubscribe/isSubscribe.html",
					error:function(){
						alert("出错了！");
					},
					success:function(data){
						var modalPage = {
							"id":resId,
							"header":"订阅资源",
							"data":data,
							"buttons":[
								{
									"id":"save_"+resId,
									"cls":"btn-primary btn-subscribe",
									"content":"订阅",
									"clickEvent":function(){
										//确认订阅
										if(isSubscribe){
											$(".modal").modal("hide");
											$(".modal,.modal-backdrop").remove();
											$.ajax({
												type:"GET",
												url:"resourceManage/resourceSubscribe/subscribeForm.html",
                                                async:false,
												error:function(){
													alert("出错了！");
												},
												success:function(data){
													var modalPage = {
														"id":"auth",
														"header":"订阅流程",
														"data":data,
													};
													var excute = {};
													excute.saveBack = function(){
														var params = {
															resId: resId,
															applyCause: $("textarea[name='reason']").val(),
															fileName:  fileName,
															linkMan:  $("input[name='applicant']").val(),
															phone:  $("input[name='phone']").val(),
															duty:$("input[name='duty']").val(),
															cellPhone:$("input[name='telephone']").val()
														};

														/**********************************************************/

//														var auths = [];
//														var conOperate = $("input[name='conOperate']").val().split(",");
//														var hisOperate = $("input[name='hisOperate']").val().split(",");
//														$.each(treetable_simple_data,function(index,item){
//															auths.push({
//																download: hisOperate.indexOf("下载")==-1?0:1,
//																dsId: item.id,
//																histSnap:hisOperate.indexOf("截图")==-1?0:1,
//																playBack:0,
//																realControl:conOperate.indexOf("控制")==-1?0:1,
//																realPlay:0,
//																realSnap:conOperate.indexOf("截图")==-1?0:1,
//																record:conOperate.indexOf("录像")==-1?0:1,
//																histTime:$("select[name='realView']").val(),
//																realTime:$("select[name='hisView']").val()
//															})
//														})

														/**********************************************************/

														params.prvs = treetable_simple_data;
														$.ajax({
															type:"POST",
															url:$.base+"/resSubscribe/add",
															data:JSON.stringify(params),
                                                            async:false,
															contentType:"application/json",
															error:function(){

															},
															success:function(data){
																if(data.code==0){
																	infoModal("申请订阅成功",function(){
																		getList($("#stateTabs li.active").find("a").attr("status"));
																	});
																}else{
																	infoModal(data.message);
																}
															}
														})
													}
													setStepList(".modal",modalPage,excute);
													setChecked();
													$(".modal").on("shown.bs.modal",function(){
//														selectBox();
														uploadFile();
//														authSelect($("input[name='conOperate']"),"constant");
//														authSelect($("input[name='hisOperate']"),"history");
//														左侧树
														treeData(resId)
//														日历插件
														calendarTime();
														//操作权限时改变树数据
														operateTreeData();
													})
													// treeTable(resId);
													// setGrid(resId);
												}
											});
										}else{
											$(".modal-domain").modal("hide");
											infoModal("没有可订阅的资源，请联系管理员");
										}
									}
								},
								{
									"id":"cancel_"+resId,
									"cls":"btn-warning btn-cancel",
									"content":"取消"
								}
							]
						};
						initModal(modalPage,function(){
							subscribeInfo(resId);
						});
					}
				});
			}else if($(_this).html()=="去订阅"){
				var modal = initModal({
					id:"tip",
					header:"提示",
					size:"sm",
					data:"确定取消订阅吗？",
					buttons:[
						{
							"id":"save_"+resId,
							"cls":"btn-primary",
							"content":"确定",
							"clickEvent":function(){
								//确认订阅
								$(".modal").modal("hide");
								$(".modal,.modal-backdrop").remove();
								$.ajax({
									type:"GET",
									url:$.base+"/resSubscribe/cancel?id="+resSubscribeId,
									error:function(){
										alert("出错了！");
									},
									success:function(data){
										if(data.code==0){
											infoModal("取消订阅成功！");
											getList($("#stateTabs li.active").find("a").attr("status"));
										}else{
											infoModal(data.message);
										}
									}
								});
							}
						},
						{
							"id":"cancel_"+resId,
							"cls":"btn-warning btn-cancel",
							"content":"取消"
						}
					]
				})
			}

		});
	}
	//上传文件
	var uploadFile = function(){
		$("#upfile").unbind().on("click",function(){
            var _this = this;
            var photoExt=$("#inputFile").val().substr($("#inputFile").val().lastIndexOf(".")).toLowerCase();//获得文件后缀名
            if(photoExt!='.zip'){
                infoModal("格式不正确，请重新上传");
                return false;
            }
            var fileSize = 10*1024;
            var fileInput = $("#inputFile")[0];
            var byteSize = fileInput.files[0].fileSize ? fileInput.files[0].fileSize : fileInput.files[0].size;
            if(Math.ceil(byteSize / 1024) > fileSize) {
                infoModal("文件不能大于10M!")	;
                return false;
            }
            base.form.fileUpload({
                url:$.base+"/support/subscribe/fileUpload",
                id:"inputFile",
                dataType:"text",
                params:{},
                success:function(data){
                    var res = (typeof(data)=="string")?JSON.parse(data):data;
                    if(res.code==0){
                        fileName = res.data;
                        $(_this).siblings("span").html("*上传成功").css({'color':'green'});
                    }else{
                        $(_this).siblings("span").html("*上传失败");
                    }
                },
                error:function(d){
                    $(_this).siblings("span").html("*服务器异常");
                }
            });
		})
	}
	//共享设置
	var shareSetting = function(id){
		$(".btn-share").unbind().on("click",function(){
			var resId = $(this).attr("resId");
			var resSubscribeId = $(this).attr("resSubscribeId");
			$.ajax({
				type:"GET",
				url:"resourceManage/resourceSubscribe/shareSetting.html",
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					shareList(resSubscribeId);
					var modalPage = {
						"id":resId,
						"header":"共享关联",
						"data":data,
						"buttons":[
							{
								"id":"save_"+resId,
								"cls":"btn-primary btn-submit",
								"content":"确定",
								"clickEvent":function(){
									//推送

								}
							},
							{
								"id":"cancel_"+resId,
								"cls":"btn-warning btn-cancel",
								"content":"取消"
							}
						]
					};
					initModal(modalPage,function () {
						subscribeInfo(resId);
					});
				}
			});
		});
	}
	function shareList(resSubscribeId){ 
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var scrollY = 200;
			var catalogTable = $("#shareSettingTable").DataTable({
				"autoWidth": false,
				"processing":false,
				"ordering":false,
				"info":true,
				"lengthChange":false,
//				"scrollY":scrollY,
				"paging":false,
				"searching":false,
				"destroy":true,
				"serverSide":true,
				"language":{
					"url": "../../js/lib/json/chinese.json"
				},
				"ajax":{
					url:$.base+"/media/findMediaAndStatus",
					type:"GET",
					contentType:"application/json",
					data:function(d){
						var params = $.extend( {}, d, {
							"page": Math.floor(d.start/d.length)+1,
							"size":10
						});
						return params;
					},
					dataFilter:function(data){
						var res = JSON.parse(data);
						if(res.code!=0){
							infoModal(res.message);
						}
						res.recordsFiltered = res.data.length;
						res.recordsTotal = res.data.length;
						return JSON.stringify(res);
					}
				},
				"columns":[
					{title:"<input type='checkbox' class='form-control checkedAll' value='' />",data:'id'},
					{title:"序号",data:"id"},
					{title:"视频平台名称",data:"mediaName"},
					{title:"IP地址",data:"ipAddress"},
					{title:"端口",data:"sessionPort"},
					{title:"类型",data:"mediaType"},
					{title:"视频厂商",data:"manuName"}
				],
				"columnDefs":[
					{
						"targets":0,
						"render":function(data){
							return "<input type='checkbox' id='"+data+"' class='form-control checkedSingle' value='' />";
						}
					},
					{
						"targets":1,
						"render":function(id,type,data,row){
							return row.row+1;
						}
					},
					{
						"targets":2,
						"render":function(value,type,data,row){
							return "<a href='javascript:void(0);' class='viewMediaName' id='"+data.id+"'>"+value+"</a>"
						}
					},
					{
						"targets":5,
						"render":function(value){
							return value=="1"?"上级":"下级";
						}
					}
				],
				"drawCallback": function(settings) {
					//滚动条
					base.scroll({
						container:$(".dataTables_scrollBody")
					});
					window.setCheckbox($("#shareSettingTable_wrapper"));
					var mediaArr = [];
					$("input[type='checkbox']").on("click",function(){
						mediaArr = [];
						$(".checkedSingle:checked").each(function(index,item){
							mediaArr.push($(item).attr("id"));
						})
						submitOffer(resSubscribeId,mediaArr);
					});
					submitOffer(resSubscribeId,mediaArr);
					viewMediaName();
				}
			});
		})
	}
//	共享设置详情
	function viewMediaName(){
		$(".viewMediaName").unbind().on("click",function(){
			var _this = this
			//渲染页面
			$.ajax({
				type:"GET",
				url:"resourceManage/streamingMediaManage/view.html",
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					modalPage = {
						"id":"viewMediaModal",
						"header":"查看",
						"data":data,
						recall:true,
//						buttons:[{
//							"content":"取消",
//							"clickEvent":function(){
////								$(".modal").modal('hide');
//							}
//						}],
						"callback":function(){
							var checkId = $(_this).attr("id");
							$.ajax({
								url:$.base+"/mediaInfoSlave/findOneMediaInfo?mid="+checkId,
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
					};
					initModal(modalPage);
				}
			});
		})
	}
	//点击标题查看详情
	function subscriptionDetails(){
		$(".subscriptionDetails").unbind().on("click",function(){
			var _this=this;
			if($(_this).attr("state")==0 || $(_this).attr("state")==4){
				
			}else if($(_this).attr("state")==1){
				$.ajax({
					type:"GET",
					url:"resourceManage/resourceSubscribe/detailsStep1.html",
	                async:false,
					error:function(){
						alert("出错了！");
					},
					success:function(data){
						var modalPage = {
							"header":"详情",
							"data":data,
						};
						var excute = {};
						excute.saveBack = function(){
						}
						setStepList(".modal",modalPage,excute);
						$(".modal").on("shown.bs.modal",function(){
							
						})
					}
				});
			}else{
				$.ajax({
					type:"GET",
					url:"resourceManage/resourceSubscribe/detailsStep2.html",
	                async:false,
					error:function(){
						alert("出错了！");
					},
					success:function(data){
						var modalPage = {
							"id":"auth",
							"header":"详情",
							"data":data,
						};
						var excute = {};
						excute.saveBack = function(){
						}
						setStepList(".modal",modalPage,excute);
						$(".modal").on("shown.bs.modal",function(){
							$.ajax({
								url:$.base+"/resSubscribe/findSubscribeInfo?subscribeId="+$(_this).attr("id"),
								type:"get",
								success:function(data){
									$("#content-1 .media-object").attr("src","../../support/resource/downloadIcon?fileName="+data.data.resourceInfo.iconRoot)
									if(data.data.resourceInfo.resLevel==0){
				    					data.data.resourceInfo.resLevel = "完全共享";
					    			}else{
					    				data.data.resourceInfo.resLevel = "部分共享";
					    			}
					    			if(data.data.resourceInfo.resType==9){
					    				data.data.resourceInfo.resType = "视频";
					    			}
					    			$.ajax({
					    				type:"get",
					    				url:$.base+"/dictItem/findAllIndustry",
					    				async:false,
					    				success:function(d){
					    					$.each(d.data, function(i,o) {
					            				if(o.dictItemCode == data.data.resourceInfo.resIndustry){
					            					data.data.resourceInfo.resIndustry = o.dictItemName;
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
					            				if(o.dictItemCode == data.data.resourceInfo.resMain){
					            					data.data.resourceInfo.resMain = o.dictItemName;
					            				}
					            			});
					    				}
					    			})
									base.form.init(data.data.resourceInfo,$("#content-1"))
									treetable_simple_data = data.data.subscribeInfo.subscribePrvList
									setTreeCheck(data.data.subscribeInfo.subscribePrvList)
									base.form.init(data.data.subscribeInfo,$("#content-3"))
								}
							})
						})
					}
				});
			}
		})
	}
	//提交推送
	function submitOffer(resSubscribeId,mediaArr){
		$(".btn-submit").unbind().on("click",function(){
			if(mediaArr.length==0){
				infoModal("请先勾选需要共享的视频平台");
			}else{
				$.ajax({
					type:"GET",
					url:$.base+"/resSubscribe/resourceShare?subscribeId="+resSubscribeId+"&&mediaId="+JSON.stringify(mediaArr),
					error:function(){

					},
					success:function(data){
						if(data.code==0){
							$(".modal").modal("hide");
							$(".modal,.modal-backdrop").remove();
							infoModal("推送成功",function(){
								getList($("#stateTabs li.active").find("a").attr("status"));
							});

						}else{
							infoModal(data.message);
						}
					}
				})
			}
		});
	}
	//订阅详情
	var subscribeInfo = function(resId){
		$.ajax({
			type:"GET",
			// url:$.base+"/res/findResourceInfo?resourceId="+resId,
            async:false,
			url:$.base+"/res/findResourceInfo?resourceId="+resId,
			error:function(){

			},
			success:function(data){
				if(data.code!=0){
					infoModal(data.message);
				}else{
					var res = data.data;
					setTree(res.vdataSourceList);
					$(".isSubscribe-box .media-object").attr("src","../../support/resource/downloadIcon?fileName="+res.iconRoot);
					$(".isSubscribe-box .media .media-heading").html(res.resName);
					$(".isSubscribe-box .media .res-abstract").html(res.resAbstract);
					$(".isSubscribe-box .media td[name=resName]").html(res.resName);
					$(".isSubscribe-box .media td[name=englishName]").html(res.englishName);
					if(res.resLevel==0){
    					res.resLevel = "完全共享";
	    			}else{
	    				res.resLevel = "部分共享";
	    			}
	    			if(res.resType==9){
	    				res.resType = "视频";
	    			}
	    			$.ajax({
	    				type:"get",
	    				url:$.base+"/dictItem/findAllIndustry",
	    				async:false,
	    				success:function(d){
	    					$.each(d.data, function(i,o) {
	            				if(o.dictItemCode == res.resIndustry){
	            					res.resIndustry = o.dictItemName;
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
	            				if(o.dictItemCode == res.resMain){
	            					res.resMain = o.dictItemName;
	            				}
	            			});
	    				}
	    			})
					$(".isSubscribe-box .media td[name=resType]").html(res.resType);
					$(".isSubscribe-box .media td[name=resLevel]").html(res.resLevel);
					$(".isSubscribe-box .media td[name=resMain]").html(res.resMain);
					$(".isSubscribe-box .media td[name=resIndustry]").html(res.resIndustry);
				}
			}
		})
	}
	//下拉树
	window.selectBox = function(){
		var click_name = "";
		var box = "";
		var i = 0;
		var select_names = [];
		//显示和隐藏下拉树
		$(".select-tree-input").each(function(index,item){
			select_names.push($(item).attr("name"));
			$(item).unbind().on("click",function(){
				var _this = this;
				$(_this).siblings(".select-tree-box").width(function(){
					return $(_this).outerWidth()+"px";
				})
				$(this).siblings(".select-tree-box").toggle();
			});
		})

		//点击其他地方，关闭下拉树
		$(document).bind("click",function(e){
			var target = $(e.target);
			if(target.closest("input[name='"+click_name+"'],.select-tree-box").length == 0&&i!=0){
				$("input[name='"+click_name+"']").siblings(".select-tree-box").hide();
			}
			click_name = (select_names.indexOf(e.target.name)==-1)?click_name:e.target.name;
			i++;
		})
	}
	//发布目录
    function authSelect(obj,operate){
        var zNodes = [];
        if(operate=="constant"){
            zNodes=[
                {name:"实时操作",id:0,pid:null,value:null},
                {name:"控制",id:1,pid:0,value:"realControl"},
                {name:"录像",id:2,pid:0,value:"record"},
                {name:"截图",id:3,pid:0,value:"realSnap"}
            ];
        }else if(operate=="history"){
            zNodes=[
                {name:"历史操作",id:0,pid:null,value:null},
                {name:"下载",id:1,pid:0,value:"download"},
                {name:"截图",id:2,pid:0,value:"histSnap"}
            ];
        }
        //生成树
        var setting = {
            check: {
                enable: true
            },
            data: {
                key:{
                    name:"name"
                },
                simpleData: {
                    enable: true,
                    name:"name",
                    idKey:"id",
                    pIdKey:"pid"
                }
            },
            callback: {
                // onClick:function(treeObj,treeid,node){
                //     $(obj).val(node[name]).attr("resId",node.value);
                // },
                onCheck: function(event, treeId, treeNode){
                    //获取已经勾选的
                    var treeObj = $.fn.zTree.getZTreeObj(treeId);
                    var nodes = treeObj.getCheckedNodes(true);
                    var elem = new Array();
                    var status = new Array();

                    $.each(nodes,function(index,item){
                        if(!item.isParent){
                            elem.push(item.name);
                            status.push({
                                key:item.value,
                                value:1
                            })
                        }
                    });
                    var vals = "";
                    if(elem.length>0){
                        $.each(elem,function(i1,o1){
                            i1==0?vals+=o1:vals+=","+o1;
                        });
                    }
                    $("#"+treeId).parents(".select-tree-box").siblings(".select-tree-input").val(vals).attr("status",JSON.stringify(status));
                }
            }
        };
        var treeObj = $.fn.zTree.init($(obj).siblings(".select-tree-box").find(".ztree"),setting,zNodes);
        //展开一级节点
        var nodes = treeObj.getNodes();
        base.scroll({
            container:$(".select-tree-box")
        });
        if (nodes.length>0) {
            treeObj.expandNode(nodes[0], true, false, true);
        }
    }
	function selectTree(obj,url,name,id,pid){
		//滚动条
		$.ajax({
			type:"GET",
			url:url,
			error:function(){

			},
			success:function(data){
				var zNodes = data.data;
				//生成树
				var setting = {
					data: {
						key:{
							name:name
						},
						simpleData: {
							enable: true,
							idKey:id,
							pIdKey:pid
						}
					},
					callback: {
						onClick:function(treeObj,treeid,node){
							$(obj).val(node[name]).attr("resId",node[id]);
							$(obj).siblings(".select-tree-box").toggle();
						}
					}
				};
				var treeObj = $.fn.zTree.init($(obj).siblings(".select-tree-box").find(".ztree"),setting,zNodes);
				//展开一级节点
				var nodes = treeObj.getNodes();
				base.scroll({
					container:$(".select-tree-box")
				});
				if (nodes.length>0) {
					treeObj.expandNode(nodes[0], true, false, true);
				}
			}
		});
	}
	//查询
	function searchRsc(){
		$(".btn-search").on("click",function(){
			$("#rsc-list .list-body").html("");
			getList($("#stateTabs li.active").find("a").attr("status"));
		});
	}
	//重置
	function clearForm(){
		$(".btn-clear").on("click",function(){
			$(".form-box").find(".form-control").each(function(index,item){
				if($(item)[0].tagName=="INPUT"){
					$(item).val("");
					$(item).attr("resId","");
				}else if($(item)[0].tagName=="SELECT"){
					$(item).val("");
				}
			})
			getList($("#stateTabs li.active").find("a").attr("status"));
		});
	}

	var treeTable = function(resId){


		$.ajax({
			type:"GET",
			url:$.base+"/res/findResourceInfo?resourceId="+resId,
			error:function(){
			},
			success:function(data){
				if(data.code!=0){
					infoModal(data.message);
				}else{
					var res = data.data.vdataSourceList;
					$.each(res,function(index,item){

					})
				}
			}
		});



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
	//设置树
	var setTree = function(data){
		//滚动条
		//$(".modal .ztree").height("170px");
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
			}
		};
		if(data.length==0){
			isSubscribe = false;
			$("#resouceTree").html("暂无数据");
		}else{
			var zNodes = setIcon(data);
			//生成树
			var treeObj = $.fn.zTree.init($("#resouceTree"),setting,zNodes);
            treetable_data.data = treeObj.transformTozTreeNodes(data);
            treetable_data.recordsTotal = treetable_data.data.length;
            treetable_data.recordsFiltered = treetable_data.data.length;
            //获取树的全部节点
			var nodes = treeObj.getNodes();
			//默认选中树的第一个节点
			if(nodes.length > 0){
				treeObj.selectNode(nodes[0]);
			}
			//var width = $("#treeBox").parents(".modal-body").width();
			base.scroll({
				container:$("#treeBox"),
				axis:"xy"
			});
		}
	}
	var treeData = function(resId){
		$.ajax({
			type:"GET",
			url:$.base+"/res/findResourceInfo?resourceId="+resId,
			error:function(){
				infoMadal("服务器异常");
			},
			success:function(data){
				if(data.code!=0){
					infoModal(data.message);
				}else{
					var res = data.data.vdataSourceList;
					treetable_simple_data = res;
					setTreeCheck(res);
				}
			}
		})
	}
	//设置复选树
	var setTreeCheck = function(data){
		//滚动条
		$(".modal .ztree").height("170px");
		base.scroll({
			container:$(".ztree")
		});
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
				onClick:zTreeOnClick,
				onRemove: zTreeOnRemove
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
	function zTreeOnRemove(event, treeId, treeNode) {
		var treeObj = $.fn.zTree.getZTreeObj("resouceTreeCheck");
		var nodes = treeObj.transformToArray(treeNode);
		var ids=[];
		var arr=[];
		$.each(nodes,function(index,item){
			ids.push(item.dsId)
		})
		$.each(treetable_simple_data,function(i1,o1) {
			if($.inArray(o1.dsId,ids)==-1){
				arr.push(o1)
			}
		});
		treetable_simple_data = arr
	}
	var arr = [];
	function zTreeOnClick(treeId,treeNode,obj){
		treetableCheckObj = obj;
		$.each(treetable_simple_data, function(i,o) {
			if(obj.dsCode == o.dsCode){
				if(o.realTime!=1&&o.realTime!=2&&o.realTime!=3&&o.realTime!=0&&o.realTime!='-1'){
					$("#realView #date").text(o.realTime).attr("selected","selected");
				}else{
					$("#realView").val(o.realTime)
				}
				if(o.histTime!=1&&o.histTime!=2&&o.histTime!=3&&o.histTime!=0&&o.histTime!='-1'){
					$("#hisView #date1").text(o.histTime).attr("selected","selected");
				}else{
					$("#hisView").val(o.histTime)
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
		$("#realView,#hisView").on("change",function(){
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
					treetable_simple_data[i1].histTime = value1.indexOf("~")!=-1?$(".tempValue1").val():$(".tempValue1").attr("val");
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
		var val1 = $("#realView").val();
		var val2 = $("#hisView").val();
		var temp="",temp1="",target="";
		$("#date").off().on("click",function (e) {
			var e  = e || window.event;
			target = e.target.id;
			laydate.render({
				elem: '#date',
				show: true,
				range:"~",
				type: 'datetime',
				closeStop: '#realView',
				done: function(value, date, endDate){
					if($("#realView").val()=="时间段"){
						$(".tempValue").attr("val",value);
					}
					$("#realView").val("");
					$(".tempValue").val(value);
					temp = value;
					operateEvent(value,"");
					// if(value == ""){
					// 	$("#realDays").val(val1)
					// 	$("#date").remove();
					// 	$("#realView").append("<option id='date'>时间段</option>");
					// }
				}
			});
		});
		$("#date1").off().on("click",function (e) {
			var e = e || window.event;
			target = e.target.id;
			laydate.render({
				elem: '#date1',
				show: true,
				range:"~",
				type: 'datetime',
				closeStop: '#hisView',
				done: function(value, date, endDate){
					if($("#hisView").val()=="时间段"){
						$(".tempValue1").attr("val",value);
					}
					$("#hisView").val("");
					$(".tempValue1").val(value);
					temp1 = value;
					operateEvent("",value);
					// if(value == ""){
					// 	$("#hisView").val(val2)
					// 	$("#date1").remove();
					// 	$("#hisView").append("<option id='date'>时间段</option>");
					// }
				}
			});
		});
		$("#realView").on("change",function(){
			var val = $(this).val();
			var valText = $("#realView option:selected").text();
			$(".tempValue").attr("realVal",val).css("visibility","visible")//option value
			$(".tempValue").val(valText);//option的text
			$(".tempValue").attr("val",val);//时间段
			$("#realView").val("");
			if(val!="1"&&val!="0"&&val!="2"&&val!="3"&&val!="-1"){
				$("#date").trigger("click");
			}
		})

		$("#hisView").on("change",function(){
			debugger
			var val = $(this).val();
			var valText = $("#hisView option:selected").text();
			$(".tempValue1").attr("realVal",val).css("visibility","visible")//option value
			$(".tempValue1").val(valText);
			$(".tempValue1").attr("val",val);//时间段
			$("#hisView").val("");
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
    var setGrid = function(resId){
        $(".tables-box").css("max-heihgt",200+"px");
        var scrollY = 150;
        require(["datatables.net","bsDatatables","resDatatables"],function(){
            var catalogTable = $("#table-auth").DataTable({
                "autoWidth": false,
                "processing":false,
                "ordering":false,
                "searching":false,
                "info":false,
                "paging":false,
                "scrollY":scrollY,
                "lengthChange":false,
                "serverSide":true,
                "destroy":true,
                "language":{
                    "url":"../../js/lib/json/chinese.json"
                },
                "ajax":{
                    url:$.base+"/res/findResourceInfo?resourceId="+resId,
                    type:"get",
                    dataFilter:function(d){
                        var res ={};
                        res = treetable_data;
                        res.recordsTotal = treetable_data.length;
                        res.recordsFiltered = treetable_data.length;
                        return JSON.stringify(res);
                    },
                },
                "columns":[
                    // {title:"<input type='checkbox' class='form-control cball' value='' />",data:"ds_code"},
                    {title:"资源(设备)名称",data:"ds_name","sWidth":"30%"},
                    {title:"实时(观看)",data:"ds_code","type":"SELECT"},
                    {title:"实时(操作)",data:"ds_code","type":"conOperate"},
                    {title:"历史(观看)",data:"ds_code","type":"SELECT"},
                    {title:"历史(操作)",data:"ds_code","type":"hisOperate"}
                ],
                "columnDefs":[
                    {
                        "targets":[1,3],
                        "render":function(data){
                            return "<select class='form-control' resId='"+data+"'>" +
                                        "<option value='1'>一天</option>" +
                                        "<option value='2'>最近一周</option>" +
                                        "<option value='3'>一个月</option>" +
                                        "<option value='0'>任意时间</option>" +
                                        // "<option value='0'>时间段</option>" +
                                        "<option value='-1'>无权限</option>" +
                                    "</select>";
                        }
                    },
                    {
                        "targets":2,
                        "render":function(data){
                            return '<div class="select-box">' +
                                        '<input type="text" class="form-control select-tree-input" name="conOperate" resId="" resId="" readonly="" val="" resId="'+data+'"/>'+
                                        '<i class="select-icon fa icon-right fa-angle-down"></i>'+
                                        '<div class="select-tree-box">'+
                                        '<div class="ztree" id="conOperate"></div>'+
                                        '</div>' +
                                    '</div>';
                        }
                    },
                    {
                        "targets":4,
                        "render":function(data){
                            return '<div class="select-box">' +
                                '<input type="text" class="form-control select-tree-input" name="hisOperate" resId="" resId="" readonly="" val="" resId="'+data+'"/>'+
                                '<i class="select-icon fa icon-right fa-angle-down"></i>'+
                                '<div class="select-tree-box">'+
                                '<div class="ztree" id="hisOperate"></div>'+
                                '</div>' +
                                '</div>';
                        }
                    }
                ],
                fnCreatedRow: function(nRow, aData, iDataIndex) {
                    // debugger
                    $(nRow).attr("data-tt-id",aData.ds_code);
                    $(nRow).attr("data-tt-parent-id",aData.parent_id);
                    $(nRow).attr("rootRow",iDataIndex);
                },
                "drawCallback": function(setting) {
                    selectBox();
                    base.scroll({
                        container:$(".dataTables_scrollBody")
                    })
                    /**设置 treetable**/
                    common.treeTable(setting);
                }
            });
        })
    };
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
	return {
		run:function(){
			getList($("#stateTabs li.active").find("a").attr("status"));
			selectBox();
			selectTree($("input[name='catalogId']"),$.base + "/catalog/listAll","cataName","id");
			selectTree($("input[name='industryId']"),$.base + "/resSubscribe/findAllHy","dictItemName","dictItemCode");
			selectTree($("input[name='themeId']"),$.base + "/resSubscribe/findAllMain","dictItemName","dictItemCode");

			setLiClick();
			searchRsc();
			clearForm();
		}
	}
	
});
	