define(["tree", "base"], function (tree, base) {
	var isIE =  (!!window.ActiveXObject || "ActiveXObject" in window)||(window.navigator.userAgent.indexOf("MSIE")>=1);
    //设置树
    function setTree() {
        //滚动条
        base.scroll({
            container: $(".left-tree")
        });
        var areaId = "";
        $.ajax({
            type: "GET",
//         url:"../../json/catalogingManage/catalogManageTree1.json",
            url: $.base + "/catalog/listAll",
            error: function () {

            },
            success: function (data) {
            	var rootNode = {
            		cataName:"根目录",
					id:"-1"
            	}
            	if(data.code==0){
					data.data.push(rootNode)
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
						callback: {
							onClick: zTreeOnClick
						}
					};
					var zNodes = data.data;
					//生成树
					var treeObj = $.fn.zTree.init($("#ztree-bar"), setting, zNodes);
					//获取树的全部节点
					var nodes = treeObj.getNodes();
					//展开树节点
					if (nodes.length>0) {
						treeObj.expandNode(nodes[0], true, false, true);
					}
					//默认选中树的第一个节点
					if (nodes.length > 0) {
						treeObj.selectNode(nodes[0]);
						//初始化按钮
						setBtns(nodes[0]);
						if(nodes[0].level == 0) {
							setCatalogTable();
						} else if(nodes[0].level == 1) {
							setContentTable();
						}
					}
				}
            }
        });
    }

    //树的点击事件
    var zTreeOnClick = function (event, treeId, treeNode) {
        setBtns(treeNode);
        if (treeNode.level == 0) {
            setCatalogTable();
        } else if (treeNode.level == 1) {
            setContentTable(treeNode);
        }
    };

    //设置按钮
    var setBtns = function (node) {
        $(".btns-box").html("");
        var buttons = "";
        if (node.level == 0) {
            //新增、删除、修改
            buttons = '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="addCatalog"><span class="iconfont icon-xinzeng"></span>新增</button>' +
            		  '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="delCatalog" disabled><span class="iconfont icon-3"></span>删除</button>' +
               		  '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="editCatalog" disabled><span class="iconfont icon-bianji"></span>修改</button>';
        } else if (node.level == 1) {
            //编目、修改、删除、提交审核
            buttons = '<button type="button" attr="'+node.id+'" class="btn btn-no-border"  operate="addContent"><span class="iconfont icon-xinzeng"></span>编目</button>' +
                	  '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="editContent" disabled><span class="iconfont icon-bianji"></span>修改</button>' +
               		  '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="delContent" disabled><span class="iconfont icon-3"></span>删除</button>' +
       			      '<button type="button" attr="'+node.id+'" class="btn btn-no-border" operate="submitContent" disabled><span class="iconfont icon-iconfontxiaolvdashitubiao33320"></span>提交审核</button>';
        }
        $(".btns-box").html(buttons);
        //弹出模态框
        modalShow(node.id, node.level);
    }

    //弹出模态框
    var modalShow = function (id, level) {
 		$("button[operate='addCatalog'],button[operate='editCatalog'],button[operate='addContent'],button[operate='editContent']").unbind().on("click", function () {
 			var _this = this;
            var page = new Object();
            var checkParams = new Object();
            if($("#catalogTable .checkedSingle:checked").length==1){
            	checkParams = JSON.parse($("#catalogTable .checkedSingle:checked").attr("checkParams"));
            }
            //按钮操作
            if ($(this).attr("operate") == "addCatalog") {
                page.header = "新增目录";
                page.url = "catalogingManage/catalogManage/addCatalog.html";
                page.callback = function(){
                	$.ajax({
                		type:"get",
                		url:$.base+"/sysUser/findApprovalMan",
                		async:true,
                		success:function(d){
                			if(d.success){
                				$.each(d.data,function(i,o){
                					$("select[name='approvalId']").append("<option value='"+o.id+"'>"+o.userName+"</option>")
                				})
                			}
                		}
                	});
                	
                }
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
                		url:$.base+"/catalog/add",
                		type:"get",
                		data:form,
                		xhrFields:{withCredentials:true},
                		success:function(d){
                			if(d.code==0){
                				setTree();
                				$("#modal-" + id).modal("hide");
                                infoModal("新增成功");
                			}else{
								infoModal(d.message);
							}
                		}
                	})
                }
            } else if ($(this).attr("operate") == "editCatalog") {
                page.header = "修改目录";
                page.checkBack = checkParams;
                page.url = "catalogingManage/catalogManage/editCatalog.html";
                page.callback = function(){
					$.ajax({
						type:"get",
						url:$.base+"/sysUser/findApprovalMan",
						async:true,
						success:function(d){
							if(d.success){
								$.each(d.data,function(i,o){
									var option="";
									if(checkParams.approvalId==o.id){
										option+="<option value='"+o.id+"' selected>"+o.userName+"</option>";
									}else{
										option+="<option value='"+o.id+"'>"+o.userName+"</option>";
									}
									$("select[name='approvalId']").append(option)
								})
							}
						}
					});
                	base.form.init(checkParams,$("#form"))
                };
                page.btnBack = function(){
                	var form = base.form.getParams($("#form"))
                	form.id = page.checkBack.id;
                	var pass = base.form.validate({
							form:$("form"),
							checkAll:true
						});
					if(!pass){
						return;
					}
					$.ajax({
                		url:$.base+"/catalog/modify",
                		type:"get",
                		data:form,
                		success:function(d){
                			if(d.success){
                				setTree();
                				$("#modal-" + id).modal("hide");
                			}else{
								infoModal(d.message);
							}
                		}
                	})
                }
            } else if ($(this).attr("operate") == "addContent") {
                page.header = "资源编目";
                page.url = "catalogingManage/catalogManage/addContent.html";
            } else if ($(this).attr("operate") == "editContent") {
                page.header = "资源修改";
                page.checkBack = checkParams;
                page.url = "catalogingManage/catalogManage/editContent.html";
            };
            $.ajax({
                type: "GET",
                url: page.url,
                error: function () {
                    alert("出错了！");
                },
                success: function (data) {
                    var modalPage = {
                        "id": id,
                        "header": page.header,
                        "data": data,
                       	"checkBack":page.checkBack,
                        "buttons": [
                            {
                                "id": "save_" + id,
                                "cls": "btn-primary btn-confirm",
                                "content": "确定",
                                "clickEvent":function(){
                                	if(page.btnBack){
                                		page.btnBack()
                                	}
                                }
                            },
                            {
                                "id": "clear_" + id,
                                "cls": "btn-warning btn-clear",
                                "content": "重置"
                            }
                        ],
                        "callback":function(){
                        	if(page.callback){
                        		page.callback();
                        	}
                        }
                    };
                    //编目
                    if ($(_this).attr("operate") == "addContent"){
                    	var excute = {};
                    	excute.stepBack = function(){
                    		var form = getForms(".pane-box");
                    		if(form.resLevel == 0){
                    			form.resLevel = "完全共享";
                    		}else{
                    			form.resLevel = "部分共享";
                    		}
                    		$.ajax({
                				type:"get",
                				url:$.base+"/dictItem/findAllIndustry",
                				async:false,
                				success:function(d){
                					$.each(d.data, function(i,o) {
                        				if(o.dictItemCode == form.resIndustry){
                        					form.resIndustry = o.dictItemName;
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
                        				if(o.dictItemCode == form.resMain){
                        					form.resMain = o.dictItemName;
                        				}
                        			});
                				}
                			});
                    		base.form.init(form,$("#content-3"));
                    	}
                    	excute.saveBack = function(){
							var flag = false;
							var form = getForms(".pane-box");
							form.iconRoot = $("#iconRoot span").html();
							form.catalogId = id;
							$.ajax({
								url:$.base+"/res/insertRes",
								type:"get",
								async:false,
								data:form,
								success:function(d){
                                    if(d.success){
                                        infoModal("编目成功")
                                    }else{
										$(".modal-backdrop").eq(1).remove();
										$("#modal-tip").remove();
										infoModal(d.message);
										flag=true;
									}
									tableReload($("#catalogTable table"));
								}
							})
							return flag;
                    	}
                    	excute.callback = function(){
                    		$(".media-object").attr("src",$.base+"/res/downloadIcon?fileName=defaultIcon.png");
                    		pictureUpload();
//                  		行业和主题赋值
							resMain();
                    		deletePicture("defaultIcon.png");
                    	}
                        setStepList(".modal",modalPage,excute);//分步
                    }/*修改编目*/
                    else if($(_this).attr("operate") == "editContent"){
						var state = true;
						var params = JSON.parse($("#catalogTable .checkedSingle:checked").attr("checkparams"));
						if(params.resStatus!="待提交"){
							state = false;
							infoModal("该目录"+params.resStatus+",不能修改！");
						}else{
							var excute = {};
							excute.callback = function(){
								resMain();
								base.form.init(checkParams,$("#content-1"));
								base.form.init(checkParams,$("#content-2"));
								$("#iconRoot").html('<span>'+checkParams.iconRoot+'</span><i class="glyphicon glyphicon-trash" style="cursor:pointer"></i>')
								$(".media-object").attr("src",$.base+"/res/downloadIcon?fileName="+checkParams.iconRoot);
								pictureUpload();
								deletePicture(checkParams.iconRoot)
							}
							excute.stepBack = function(){
								var form = getForms(".pane-box");
								if(form.resLevel == 0){
									form.resLevel = "完全共享";
								}else{
									form.resLevel = "部分共享";
								}
								$.ajax({
                    				type:"get",
                    				url:$.base+"/dictItem/findAllIndustry",
                    				async:false,
                    				success:function(d){
                    					$.each(d.data, function(i,o) {
	                        				if(o.dictItemCode == form.resIndustry){
	                        					form.resIndustry = o.dictItemName;
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
	                        				if(o.dictItemCode == form.resMain){
	                        					form.resMain = o.dictItemName;
	                        				}
	                        			});
                    				}
                    			});
								base.form.init(form,$("#content-3"));
							}
							excute.saveBack = function(){
								var b = base.getChecks("call")
								var form = getForms(".pane-box");
								var flag = false;
								form.id = b[0];
								form.iconRoot = $("#iconRoot span").html();
								$.ajax({
									url:$.base+"/res/updateRes",
									type:"get",
									async:false,
									data:form,
									success:function(d){
										if(d.success){
											infoModal("修改成功")
										}else{
											$(".modal-backdrop").eq(1).remove();
											$("#modal-tip").remove();
											infoModal(d.message);
											flag=true;
										}
										tableReload($("#catalogTable table"));
									}
								})
								return flag;
							}
							setStepList(".modal",modalPage,excute);//分步
						}
                    }else{
                    	initModal(modalPage);
                    }
                }
            });
        });
        $("button[operate='submitContent']").unbind().on("click",function(){
        	var state = true;
        	var params = JSON.parse($("#catalogTable .checkedSingle:checked").attr("checkparams"));
			if(params.resStatus!="待提交"){	
				state = false;
			}
        	if(state==true){
        		$.ajax({
        			type:"get",
        			url:$.base+"/res/pendRes?rid="+params.id,
        			async:true,
        			success:function(d){
        				if(d.success){
        					tableReload($("#catalogTable table"));
        					infoModal("提交成功！");
        				}else{
							infoModal(d.message);
						}
        			}
        		});
        	}else{
        		infoModal("选择记录中包含非待提交状态资源请重新选择");
        	}
        	
        })
        //删除按钮
        $("button[operate='delCatalog'],button[operate='delContent']").unbind().on("click",function(){
        	var page = {};
			var state = true;
        	if($(this).attr("operate")=="delCatalog"){
        		page.url = "/catalog/delete?id=";
        		page.callback= function(){
        			setTree();
        		}
        	}else if($(this).attr("operate") == "delContent"){
				var params = JSON.parse($("#catalogTable .checkedSingle:checked").attr("checkparams"));
				if(params.resStatus!="待提交"){
					state = false;
					infoModal("该目录"+params.resStatus+",不能删除！");
				}else{
					page.url = "/res/delRes?rid=";
					page.callback = function(){
						tableReload($("#catalogTable table"));
					}
				}
        	}
			if(state){
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
								var b = base.getChecks("call")
								$.ajax({
									type:"get",
									url:$.base+page.url+b,
									async:true,
									success:function(d){
										if(d.success){
											infoModal("删除成功");
											page.callback();
										}else{
											infoModal(d.message);
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
			}
        })
    }
    //行业和主题赋值
    var resMain = function(){
    	$.ajax({
    		url:$.base+"/dictItem/findAllMain",
    		type:"get",
			async:false,
    		success:function(d){
    			$.each(d.data, function(i,o) {
    				$("#resMain").append("<option value='"+o.dictItemCode+"'>"+o.dictItemName+"</option>");
    			});
    		}
    	})
    	$.ajax({
    		url:$.base+"/dictItem/findAllIndustry",
    		type:"get",
			async:false,
    		success:function(d){
    			$.each(d.data, function(i,o) {
    				$("#resIndustry").append("<option value='"+o.dictItemCode+"'>"+o.dictItemName+"</option>");
    			});
    		}
    	})
    }
    //  资源编目图片上传
	var pictureUpload = function(){
		$("#pictureCheckBtn").unbind().on("click",function(){
			if($("#iconRoot span").text()){
				infoModal("只能上传一张图片！")
				return false;
			}
			$("#file").remove();
			$("body").append("<input type='file' id='file' name='file'>")
			$("#file").click();
			if(isIE){
				$("#file").bind('input propertychange', function(){
					uploadImg();
				})
			}else{
				$("#file").on("change",function(){
					uploadImg();
				})
			}

		})
	}
	function uploadImg(){
		var photoExt=$("#file").val().substr($("#file").val().lastIndexOf(".")).toLowerCase();//获得文件后缀名
		if(photoExt!='.jpg'&&photoExt!='.png'&&photoExt!='.bmp'){
			infoModal("图片格式不正确，请重新上传");
			return false;
		}
		var fileSize = 2*1024;
//		if (isIE && !$("#file").files) {
//			var filePath = $("#file").val();
//			var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
//			var file = fileSystem.GetFile (filePath);
//			fileSize = file.size;
//		}else {
// 			fileSize = $("#file")[0].files[0].size;
//		}
// 		fileSize=Math.round(fileSize/1024*100)/100; //单位为KB
// 		if(fileSize>=2024){
// 			infoModal("照片最大尺寸为2MB，请重新上传!");
// 			return false;
// 		}
        var fileInput = $("#file")[0];
        var byteSize = fileInput.files[0].fileSize ? fileInput.files[0].fileSize : fileInput.files[0].size;
        if(Math.ceil(byteSize / 1024) > fileSize) {
            infoModal("照片最大尺寸为2MB，请重新上传!")	;

            return false;
        }
		base.form.fileUpload({
			url:$.base+ '/res/uploadIcon',
			id:"file",
			params:{},
			async: false,
			dataType:"text",
			success:function(data){
				data = JSON.parse(data)
				$("#iconRoot").html('<span>'+data.data+'</span><i class="iconfont icon-shanchu" style="cursor:pointer"></i>')
				$(".media-object").attr("src",$.base+"/res/downloadIcon?fileName="+data.data);
			},
			error:function(d){
			}
		});
	}
	//删除编目图片
	var deletePicture = function(icon){
		$("#iconRoot").on("click",".iconfont",function(){
			$("#iconRoot").html('');
			$(".media-object").attr("src",$.base+"/res/downloadIcon?fileName="+icon);
		})
	}
    //设置表格
    var setCatalogTable = function () {
    	//表格内容高度
    	var scrollY = $("#catalogTable").height()-100;//100表格thead高度和表格tfooter的高度之和
        $("#catalogTable").html('<table class="table table-primary table-bordered table-striped text-center"></table>');
        require(["datatables.net", "bsDatatables", "resDatatables"], function () {
            var catalogTable = $("#catalogTable table").DataTable({
                "autoWidth": false,
                "processing": false,
                "ordering": false,
                "searching": false,
                "info": true,
                "paging": true,
                "scrollY":scrollY,
                "lengthChange": false,
                "destroy": true,
                "language": {
                    "url": "../../js/lib/json/chinese.json"
                },
                "ajax": {
//                  url: "../../json/catalogingManage/catalogManage.json",
					url:$.base + "/catalog/listAll",
                    type: "get",
                    contentType: "application/json",
                    data: function (d) {

                    }
                },
                "columns": [
                    {title: "<input type='checkbox' class='form-control checkedAll' value='' />", data: 'id'},
                    {title: "序号", data: "id"},
                    {title: "目录名称", data: "cataName"},
                    {title: "目录编码", data: "cataCode"},
//                  {title: "目录审核人", data: "approval"}
                ],
                "columnDefs":[
			  		{
			  			"targets":0,
			  			"render":function(data,d,obj){
			  				return "<input type='checkbox' name='call' class='form-control checkedSingle' value='"+data+"' checkParams='"+JSON.stringify(obj)+"'/>";
			  			}
			  		},
			  		{
			  			"targets":1,
			  			"render":function(data,d,obj,row){
			  				return row.row+1;
			  			}
			  		}
			  	],
                "drawCallback": function (settings) {
                	//滚动条
			        base.scroll({
			            container: $(".dataTables_scrollBody")
			        });
			  	 	window.setCheckbox($("#catalogTable"));
                    $("input[type='checkbox']").on("click", function() {
                        /*
                         *修改按钮
                         * --只有选中一条的时候可以修改
                        */
                        var _this = this;
                        var params = new Object();
						if ($(".checkedSingle:checked").length == 1) {
							$(".btns-box").find("button[operate='editCatalog']").prop("disabled",false);
							$(".btns-box").find("button[operate='delCatalog']").prop("disabled",false);
						} else {
							$(".btns-box").find("button[operate='editCatalog']").prop("disabled", true);
							$(".btns-box").find("button[operate='delCatalog']").prop("disabled", true);
						}
                        $.each(settings.json.data,function(index,item){
                       		if(item.id==$(_this).attr("id")){
                       			params = JSON.stringify(item);
                       			$(_this).attr("checkParams",params);
                       		}
                        })
                        if ($(".checkedSingle:checked").length == 1) {
                            $(".btns-box").find("button[operate='editCatalog']").prop("disabled",false);
                            $(".btns-box").find("button[operate='delCatalog']").prop("disabled",false);
                        } else {
                            $(".btns-box").find("button[operate='editCatalog']").prop("disabled", true);
                            $(".btns-box").find("button[operate='delCatalog']").prop("disabled", true);
                        }
                    })
                }
            });
        })
    }

    //设置编目表格
    var setContentTable = function (node) {
    	//表格内容高度
    	var scrollY = $("#catalogTable").height()-100;//100表格thead高度和表格tfooter的高度之和
        $("#catalogTable").html('<table class="table table-primary table-bordered table-striped text-center"></table>');
        require(["datatables.net", "bsDatatables", "resDatatables"], function () {
            var catalogTable = $("#catalogTable table").DataTable({
            	"serverSide":true,
                "autoWidth": false,
                "processing": false,
                "ordering": false,
                "searching": false,
                "info": true,
                "paging": true,
                "scrollY":scrollY,
                "lengthChange": false,
                "destroy": true,
                "language": {
                    "url": "../../js/lib/json/chinese.json"
                },
                "ajax": {
                    url: $.base+"/res/findAllResByCatalogId",
                    type: "get",
                    contentType: "application/json",
                    data: function (d) {
                    	var length = d.length;
                    	var start = d.start;
						 var params = $.extend( {},{ 
						 	 "cid":node.id,
		                     "page": Math.floor(start/length)+1, 
		                     "size":10 
		                  }); 
		                  return params; 
                    },
                    dataFilter:function(data){ 
	                  var d = JSON.parse(data).data;
	                  return JSON.stringify(d); 
	               }
                },
                "columns": [
                    {title: "<input type='checkbox' class='form-control checkedAll' value='' />", data: "id"},
                    {title: "序号", data: "id"},
                    {title: "资源名称", data: "resName"},
                    {title: "资源类型", data: "resType"},
                    {title: "编目人", data: "createUser"},
//                  {title: "审核人", data: "approval"},
                    {title: "资源状态", data: "resStatus"}
                ],
                "columnDefs": [
                    {
                        "targets": 0,
                        "render": function (data,d,o) {
                            return "<input type='checkbox' class='form-control checkedSingle' value='"+data+"' checkParams='"+JSON.stringify(o)+"' name='call'/>";
                        }
                    },
                    {
                        "targets": 1,
                        "render": function (data,d,o,r) {
                            return r.row +1;
                        }
                    },
                    {
                    	"targets":2,
                    	"render":function(data,d,obj){
                    		return "<a href='javascript:void(0);' class='resDetail' rid='"+obj.id+"'>"+data+"</a>"
                    	}
                    },
                    {
                    	"targets":3,
                    	"render":function(value){
                    		if(value==9){
                    			return "视频"
                    		}
                    	}
                    }
                ],
                "drawCallback": function (settings) {
                	//滚动条
			        base.scroll({
			            container: $(".dataTables_scrollBody")
			        });
			        window.setCheckbox($("#catalogTable"));
					$(".btns-box").find("button[operate='editContent']").prop("disabled", true);
					$(".btns-box").find("button[operate='delContent'],button[operate='delContent']").prop("disabled", true);
					$(".btns-box").find("button[operate='delContent'],button[operate='submitContent']").prop("disabled", true);
                    $("input[type='checkbox']").on("click", function () {
                        /*
                         *修改、查看流程按钮
                         * --只有选中一条的时候可以修改
                        */
                       var _this = this;
                       var params = new Object();
                       $.each(settings.json.data,function(index,item){
                       		if(item.id==$(_this).attr("id")){
                       			params = JSON.stringify(item);
                       			$(_this).attr("checkParams",params);
                       		}
                       })
//                     提交,删除,修改
                        if ($(".checkedSingle:checked").length == 1) {
                            $(".btns-box").find("button[operate='editContent']").prop("disabled",false);
                             $(".btns-box").find("button[operate='delContent'],button[operate='delContent']").prop("disabled",false);
                             $(".btns-box").find("button[operate='delContent'],button[operate='submitContent']").prop("disabled",false);
                        } else {
                            $(".btns-box").find("button[operate='editContent']").prop("disabled", true);
                             $(".btns-box").find("button[operate='delContent'],button[operate='delContent']").prop("disabled", true);
                             $(".btns-box").find("button[operate='delContent'],button[operate='submitContent']").prop("disabled", true);
                        }
                    })
//                  资源详情
					$(".resDetail").unbind().on("click",function(){
						var rid = $(this).attr("rid");
						$.ajax({
							url:"catalogingManage/catalogManage/resDetail.html",
							type:"get",
							success:function(data){
								var modalPage = {
			                        "header":"资源详情",
			                        "data": data,
			                        "buttons": [
			                            {
			                                "id": "close",
			                                "cls": "btn-primary btn-confirm",
			                                "content": "取消"
			                            }
			                        ],
			                        "callback":function(){
			                        	$.ajax({
			                        		type:"get",
			                        		url:$.base+"/res/findOneRes?rid="+rid,
			                        		async:true,
			                        		success:function(d){
			                        			$(".media-object").attr("src",$.base+"/res/downloadIcon?fileName="+d.data.iconRoot)
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
			                        			
			                        			base.form.init(data,$(".pane-box"))
			                        		}
			                        	});
			                        }
			                    };
			                    initModal(modalPage);
							}
						})
					})
                }
            });
        })
    }
    return {
        run: function () {
            setTree();
        }
    }

})