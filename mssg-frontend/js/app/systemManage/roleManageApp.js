define(["tree", "base"], function (tree, base) {

    //设置表格
    var roleTable = function () {
        //滚动条
        base.scroll({
            container: $("#roleManageTable").parents(".table-box")
        });
        require(["datatables.net", "bsDatatables", "resDatatables"], function () {
            var roleTable = $("#roleManageTable").DataTable({
                "autoWidth": false,
                "processing": false,
                "ordering": false,
                "searching": false,
                "info": true,
                "paging": true,
                "lengthChange": false,
                "destroy": true,
                "serverSide":true,
                "language": {
                    "url": "../../js/lib/json/chinese.json"
                },
                "ajax": {
                    url: $.base+"/role/findPageRole",
                    type: "get",
                    contentType: "application/json",
                   	data:function(d){
						 var params = $.extend( params,{
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
                "columns": [
                    {title: "<input type='checkbox' class='form-control checkedAll' value='' />", data: 'id'},
                    {title: "角色名称", data: "roleName"},
                    {title: "描述", data: "role_decription"}
                ],
                "columnDefs": [
                    {
                        "targets": 0,
                        "render": function (data,display,datas) {
                            return "<input type='checkbox' class='form-control checkedSingle' roleCode='"+datas.roleCode+"' name='call' value='"+data+"' />";
                        }
                    }
                ],
                "drawCallback": function (settings) {
                    window.setCheckbox($("#roleManageTable"));
                    $("input[type='checkbox']").on("click", function () {
                        /*
                         *修改、删除、配置菜单按钮
                         * --只有选中一条的时候可以修改
                         * */
                        var _this = this;
                        if ($(".checkedSingle:checked").length == 1) {
                            $(".btns-box").find("button[operate='edit'],button[operate='del'],button[operate='menuSetting']").attr("roleCode",$(_this).attr("roleCode")).removeAttr("disabled");
                        } else {
                            $(".btns-box").find("button[operate='edit'],button[operate='del'],button[operate='menuSetting']").attr("disabled", true);
                        }
                    })
                    setModal();
                }
            });
        })
    }
    var setModal = function () {
    	//  	新增修改
        $("button[operate='add'],button[operate='edit']").unbind().on("click", function () {
            var page = new Object();
            if($(this).attr("operate") == "add"){
	            page.header = "新增角色";
	            page.url = "systemManage/roleManage/add.html";
	            page.id = "addRole";
	            page.btnBack = function(){
	            	var form = base.form.getParams($("#form"))
	            	$.ajax({
	            		type:"post",
	            		url:$.base+"/role/insertRole",
	            		async:true,
	            		data:JSON.stringify(form),
	            		contentType: "application/json",
	            		success:function(d){
	            			if(d.success){
	            				$(".modal").modal("hide");
                                infoModal("新增成功");
	            				tableReload($("#roleManageTable"));
	            			}else{
                                infoModal(d.message);
                            }
	            		}
	            	});
	            }
            }else if($(this).attr("operate") == "edit"){
            	var roleId = base.getChecks("call");
                if($(this).attr("roleCode")==10000001){
                    infoModal("该角色为超级管理员，不可修改");
                }else{
                    page.header = "修改角色";
                    page.url = "systemManage/roleManage/add.html";
                    page.id = "editRole";
                    page.btnBack = function(){
                        var form = base.form.getParams($("#form"))
                        form.id = roleId[0];
                        $.ajax({
                            type:"post",
                            url:$.base+"/role/updateRole",
                            async:true,
                            data:JSON.stringify(form),
                            contentType: "application/json",
                            success:function(d){
                                if(d.success){
                                    $(".modal").modal("hide");
                                    infoModal("修改成功");
                                    tableReload($("#roleManageTable"));
                                }else{
                                    infoModal(d.message);
                                }
                            }
                        });
                    }
                    page.callback = function(){
                        $.ajax({
                            url:$.base+"/role/findOneRole?roleId="+roleId,
                            type:"get",
                            success:function(d){
                                base.form.init(d.data,$("#form"))
                            }
                        })
                    }
                }
            }
            if(JSON.stringify(page) != "{}"){
                $.ajax({
                    type: "GET",
                    url: page.url,
                    error: function () {
                        alert("出错了！");
                    },
                    success: function (data) {
                        var modalPage = {
                            "id": page.id,
                            "header": page.header,
                            "data": data,
                            "buttons": [
                                {
                                    "id": "save_" + page.id + "",
                                    "cls": "btn-primary btn-confirm",
                                    "content": "确定",
                                    "clickEvent":function(){
                                        var pass = base.form.validate({
                                            form:$("form"),
                                            checkAll:true
                                        });
                                        if(!pass){
                                            return;
                                        }
                                        if(page.btnBack){
                                            page.btnBack();
                                        }
                                    }
                                },
                                {
                                    "id": "clear_" + page.id + "",
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
                        initModal(modalPage);
                    }
                });
            }
        });
        //删除
        $("button[operate='del']").unbind().on("click", function () {
           if($(this).attr("roleCode")==10000001){
               infoModal("该角色为超级管理员，不可删除");
           }else{
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
                           var roleId = base.getChecks("call");
                           $("#modal-del").modal('hide');
                           $.ajax({
                               type:"GET",
                               url:$.base+"/role/delRole?roleId="+roleId,
                               success:function(d){
                                   if(d.code == 0){
                                       infoModal("删除成功");
                                       tableReload($("#roleManageTable"));
                                   }else{
                                       infoModal(d.message);
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
           }
        })
    }
    //配置菜单
    var setMenus = function () {
        $("button[operate='menuSetting']").unbind().on("click", function () {
            var page = new Object();
            page.header = "配置菜单";
            page.url = "systemManage/roleManage/menuSetting.html";
            page.id = "setMenus";
            $.ajax({
                type: "GET",
                url: page.url,
                error: function () {
                    alert("出错了！");
                },
                success: function (data) {
                    var modalPage = {
                        "id": page.id,
                        "header": page.header,
                        "data": data,
                        "buttons": [
                            {
                                "id": "save_" + page.id + "",
                                "cls": "btn-primary btn-confirm",
                                "content": "确定",
                                "clickEvent":function(){
                                	var treeObj = $.fn.zTree.getZTreeObj(("menuSetting"));
									var nodes = treeObj.getCheckedNodes(true);
									var arr = [];
									var roleId = base.getChecks("call");
									$.each(nodes, function(i,o) {
										arr.push(o.id)
									});
									$.ajax({
										type:"get",
										url:$.base+"/role/confingMenu?roleId="+roleId+"&menuId="+arr,
										async:true,
										success:function(d){
											if(d.success){
												$(".modal").modal("hide");
												infoModal("配置成功");
											}
										}
									});
                                }
                            },
                            {
                                "id": "clear_" + page.id + "",
                                "cls": "btn-warning btn-clear",
                                "content": "取消"
                            }
                        ]
                    };
                    initModal(modalPage,function(){
                    	setCheckTree();
                    });
                }
            });
        });
    }
    //菜单树
    var setCheckTree = function () {
        $(".ztree-box").css("max-height",$(document).height()-200);
        base.scroll({
            container:$(".ztree-box")
        })
        var roleId = base.getChecks("call")
        $.ajax({
            type: "GET",
            url: $.base+"/role/findAllMenu?roleId="+roleId,
            error: function () {

            },
            success: function (data) {
                var setting = {
                    check: {
                        enable: true
                    },
                    data: {
                       key:{
                           name: "label",
                           children:"items"
                       }
                    },
                    callback: {
                         onCheck: function (event, treeId, treeNode) {
                             if(!treeNode.checked){
                                $("#"+treeNode.id).hide()
                                 $("[pid='"+treeNode.id+"']").hide()
                             }else{
                                 $("#"+treeNode.id).show()
                                 $("[pid='"+treeNode.id+"']").show()
                             }
                         }
                    }
                };
                //将树转为数组
                var result = new Array();
                result = window.mapToArray(data.data.list, "0");
                // var zNodes = data;
                //生成树
                var treeObj = $.fn.zTree.init($("#menuSetting"), setting, result);
                var nodes = treeObj.transformToArray(treeObj.getNodes());
                var checkObj = [];
                var lis = "";
                $.each(nodes, function(i1,o1) {
                    lis+="<li class='pull-left'  id='"+o1.id+"' pid='"+o1.pid+"' style='display: none;width:100px;;text-align:center'>"+o1.label+"</li>";
                	$.each(data.data.menuid,function(i2,o2){
                		if(o1.id == o2){
                			checkObj.push(o1)
                		}
                	})
                });
                //获取选中的树节点
                $(".roles").html(lis);
				for (var i=0, l=checkObj.length; i < l; i++) {
					treeObj.checkNode(checkObj[i], true, true);
                    $(".roles").find("#"+checkObj[i].id).show();
				}
            }
        });
    }
    
    return {
        run: function () {
            roleTable();
            setMenus();
        }
    }

})