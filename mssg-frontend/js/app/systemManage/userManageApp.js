define(["tree","base"],function(tree,base) {
	var login_id =  JSON.parse(sessionStorage.getItem("loginInfo")).loginUserInfo.id;
	//设置表格
	var roleTable = function () {
		var url = "";
		var roleId = "";
        var comId = "";
		if ($("#userManage").attr("project") == "master") {
			url = $.base + "/sysUser/findPageUserByCondition";
		} else if ($("#userManage").attr("project") == "slave") {
			url = $.base + "/user/listComUser";
		}
		($("select[name='role']").val() == -1) ? roleId = "" : roleId = $("select[name='role']").val();
        ($("select[name='unit']").val() == -1) ? comId = "" : comId = $("select[name='unit']").val();
        var scrollY = $(".table-box").height() - 100;
		require(["datatables.net", "bsDatatables", "resDatatables"], function () {
			var userroleTable = $("#userManageTable").DataTable({
				"autoWidth": false,
				"processing": false,
				"ordering": false,
				"searching": false,
				"info": true,
				"scrollY": scrollY,
				"paging": true,
				"lengthChange": false,
				"serverSide": true,
				"destroy": true,
				"language": {
					"url": "../../js/lib/json/chinese.json"
				},
				"ajax": {
					url: url,
					type: "get",
					contentType: "application/json",
					data: function (d) {
						if ($("#userManage").attr("project") == "master") {
							var params = $.extend({}, d, {
								"page": Math.floor(d.start / d.length) + 1,
								"size": 10,
                                 "userName":$("input[name='userName']").val(),
                                 "loginName":$("input[name='loginName']").val(),
                                 "roleId":roleId,
                                 "comId":comId
							});
						} else if ($("#userManage").attr("project") == "slave") {
							var params = $.extend({}, d, {
								"page": Math.floor(d.start / d.length) + 1,
								"size": 10,
								"loginName": $("input[name='loginName']").val(),
								"userName": $("input[name='userName']").val(),
								"roleId": roleId
							});
						}

						return params;
					},
					dataFilter: function (data) {
						var d = JSON.parse(data).data;
						return JSON.stringify(d);
					}
				},
				"columns": [
					{title: "<input type='checkbox' class='form-control checkedAll' value='' />", data: ''},
					{title: "序号", data: "id"},
					{title: "用户名", data: "loginName"},
					{title: "用户角色", data: "userRole"},
					{title: "姓名", data: "userName"},
					{title: "身份账号", data: "idCard"},
					{title: "手机", data: "telphone"},
					{title: "办公电话", data: "cellPhoneNumber"},
					{title: "单位", data: "companyName"},
					{title: "状态", data: "state"}
				],
				"columnDefs": [
					{
						"targets": 0,
						"render": function (row, display, data) {
							return "<input type='checkbox' userId='" + data.id + "' class='form-control checkedSingle' value='' />";
						}
					},
					{
						"targets": 1,
						"render": function (cur, display, data, row) {
							return row.row + 1;
						}
					},
					{
						"targets": -1,
						"render": function (cur, display, data, row) {
							if (cur == 1) {
								return "停用";
							} else {
								return "启用";
							}
						}
					}
				],
				"initComplete":function(){
					base.scroll({
						container: $(".dataTables_scrollBody")
					});
				},
				"drawCallback": function (settings) {
					window.setCheckbox($("#userManageTable").parents(".table-box"));

					$("input[type='checkbox']").on("click", function () {
						/*
						 *修改按钮
						 * --只有选中一条的时候可以修改
						 * */
						if ($(".checkedSingle:checked").length == 1) {
							$(".btns-box").find("button[operate='view'],button[operate='del'],button[operate='edit'],button[operate='stop'],button[operate='start'],button[operate='resetPwd'],button[operate='certificateDownload'],button[operate='import']").removeAttr("disabled");
						} else {
							$(".btns-box").find("button[operate='view'],button[operate='del'],button[operate='edit'],button[operate='stop'],button[operate='start'],button[operate='resetPwd'],button[operate='certificateDownload'],button[operate='import']").attr("disabled", true);
						}
					})
				}
			});
		})
	}
	//
	var getRoles = function(){
		var roles = [];
        var url = "";
        if ($("#userManage").attr("project") == "master") {
            url = $.base+"/role/findAllRole";
        } else if ($("#userManage").attr("project") == "slave") {
            url = $.base+"/user/findAllRoleRegister";
        }
		$.ajax({
			type:"GET",
			url:url,
			async:false,
			error:function(){

			},
			success:function(data){
				if(data.code==0){
					roles = data.data;
				}
			}
		});
		return roles;
	}
    var allUnit = function(){
        var unitInfo = {};
		url = $.base+"/sysUser/findAllCompany";
        $.ajax({
            type:"GET",
            url:url,
            async:false,
            error:function(){

            },
            success:function(data){
                if(data.code){
                    unitInfo = data.data;
                }
            }
        });
        return unitInfo;
	}
    //查询单位
    var loginUnit = function(roleId){
        roleId = (roleId==undefined)?"":roleId;
        var unitInfo = {};
        var url = "";
        if ($("#userManage").attr("project") == "master") {
            url = $.base+"/sysUser/findComByRoleType?roleId="+roleId;
        } else if ($("#userManage").attr("project") == "slave") {
            url = $.base+"/user/findAllCompany";
        }
        $.ajax({
            type:"GET",
            url:url,
            async:false,
            error:function(){

            },
            success:function(data){
                if(data.code){
                    unitInfo = data.data;
                }
            }
        });
        return unitInfo;
    }
	//添加用户角色
	function addRole(){
		var roles = getRoles();
		if(roles){
			var options = "<option value='-1' selected='selected' disabled='disabled'>请选择</option>";
			$.each(roles,function(index,item){
				options += "<option value='"+item.id+"'>"+item.roleName+"</option>";
			});
			$("select[name='role']").append(options);
		}
	}
    //添加用户单位
    function addUnit(){
        var units = allUnit();
        if(units){
            if(Array.isArray(units)==false){
                $("select[name='unit']").append("<option value='"+units.id+"'>"+units.name+"</option>");
            }else{
                var options = "<option value='-1' selected='selected' disabled='disabled'>请选择</option>";
                $.each(units,function(index,item){
                    options += "<option value='"+item.id+"'>"+item.companyName+"</option>";
                });
                $("select[name='unit']").append(options);
            }
        }
    }
	//查询
	function queryRes(){
		$(".btn-query").unbind().on("click",function(){
			roleTable();
		})
	}
	//重置
	function clearForms(){
		$(".btn-clear").unbind().on("click",function(){
			$("input[name='loginName']").val("");
			$("input[name='userName']").val("");
			$("select[name='role']").val(-1);
			$("select[name='unit']").val(-1);
			roleTable();
		})
	}
	//新增修改按钮
	function addAndEditBtn(){
		$("button[operate='add'],button[operate='edit'],button[operate='view']").unbind().on("click",function(){
			var page = new Object();
			var _this = this;
			var label="确定";
			if($(_this).attr("operate")=="edit"){
				page.header = "修改用户";
				page.url = "systemManage/userManage/edit.html";
				page.id = "editRole";
			}else if($(_this).attr("operate")=="add"){
				page.header = "新增用户";
				page.url = "systemManage/userManage/add.html";
				page.id = "addRole";
			}else{
				page.header = "查看用户";
				page.url = "systemManage/userManage/view.html";
				page.id = "viewRole";
				label="关闭"
			}
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
								"id":"save_"+page.id,
								"cls":"btn-primary btn-confirm",
								"content":label,
								"clickEvent":function(){
									var pass = base.form.validate({
										form:$("#form"),
										checkAll:true
									});
									if(!pass){return false;}
									var params = getForms($(".modal-domain"));
									if($(_this).attr("operate")=="add"){
										addUser(params);
									}else if($(_this).attr("operate")=="edit"){
										params.id = $("#userManageTable .checkedSingle:checked").attr("userId");
										editUser(params);
									}else{
										$("#viewRole").modal('hide');
									}
								}
							},
							{
								"id":"clear_"+page.id,
								"cls":"btn-warning btn-clear",
								"content":"重置"
							}
						]
					};
					if($(_this).attr("operate")=="view"){
						modalPage.buttons.splice(1,1)
					}
					initModal(modalPage,function(){
						//登录人所在单位
						var unit = loginUnit();
						if(unit) {
							if (Array.isArray(unit) == false) {
								$("select[name='comId']").append("<option value='" + unit.id + "'>" + unit.name + "</option>");
							}
						}
						//获取角色
						var roles = getRoles();
						var roleOptions = "<option value='-1' selected='selected' disabled='disabled'>请选择</option>";
						if(roles){
							var roleOptions = "<option value='-1' selected='selected'>请选择</option>";
							$.each(roles,function(index,item){
								roleOptions += "<option value='"+item.id+"'>"+item.roleName+"</option>";
							});
							$("select[name='roleId']").append(roleOptions);
							if ($("#userManage").attr("project") == "master"){
								$("select[name='roleId']").on("change",function(){
									var roleId = $(this).val();
									$("select[name='comId'] option").remove();
									//登录人所在单位
									var units = loginUnit(roleId);
									if(units){
										var unitOptions = "";
										$.each(units,function(index,item){
											unitOptions += "<option value='"+item.id+"'>"+item.companyName+"</option>";
										});
										$("select[name='comId']").append(unitOptions);
									}
								})
							}

						}
						if($(_this).attr("operate")=="edit"){
							var userId  = $("#userManageTable .checkedSingle:checked").attr("userId");
							var write_back = viewDetail(userId);
							$(".modal-domain").find("input,textarea").each(function(index,item){
								$(item).val(write_back[$(item).attr("name")]);
							});
							$(".modal-domain select[name='roleId']").val(write_back.roleId);
							if(write_back.userName=='admin'){
								$(".modal-domain").find("input[name='loginName']").attr('disabled',"disabled")
								$(".modal-domain").find("input[name='userName']").attr('disabled',"disabled")
								$(".modal-domain").find("input[name='idCard']").attr('disabled',"disabled")
								$(".modal-domain").find("input[name='comId']").attr('disabled',"disabled")
								$(".modal-domain").find("input[name='description']").attr('disabled',"disabled")
							}
							//登录人所在单位
							if ($("#userManage").attr("project") == "master"){
								var init_units = loginUnit(write_back.roleId);
								if(init_units){
									var init_unitOptions = "";
									$.each(init_units,function(index,item){
										init_unitOptions += "<option value='"+item.id+"'>"+item.companyName+"</option>";
									});
									$("select[name='comId']").append(init_unitOptions);
								}
								$(".modal-domain select[name='comId']").val(write_back.companyId);
							}
						}
						if($(_this).attr("operate")=="view"){
							var userId  = $("#userManageTable .checkedSingle:checked").attr("userId");
							var write_back = viewDetail(userId);
							$.each(write_back,function (key,val) {
								$("."+key).html(val);
							})
						}
					});
				}
			});
		});
	}
	//修改密码按钮
	function editPwdBtn(){
		$("button[operate='resetPwd']").unbind().on("click",function(){
			var page = new Object();
			var _this = this;
			$.ajax({
				type:"GET",
				url:"systemManage/userManage/resetPwd.html",
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var modalPage = {
						"id":"editPwd",
						"header":"修改密码",
						"data":data,
						"size":"sm",
						"buttons":[
							{
								"id":"save_editPwd",
								"cls":"btn-primary btn-confirm",
								"content":"确定",
								"clickEvent":function(){
									var userId  = $("#userManageTable .checkedSingle:checked").attr("userId");
									var pass = base.form.validate({
										form:$("form"),
										checkAll:true
									});
									if(!pass){return false;}
                                    if ($("#userManage").attr("project") == "master") {
                                        url = $.base+"/sysUser/resetUserPwd";
                                    } else if ($("#userManage").attr("project") == "slave") {
                                        url = $.base+"/user/restPassword";
                                    }
									$.ajax({
										type:"POST",
										url:url,
                                        contentType:"application/json",
                                        data:JSON.stringify({
                                            id:userId,
                                            password:$("input[name='password']").val()
                                        }),
										error:function(){

										},
										success:function(data){
											if(data.code==0){
                                                $(".modal-domain").modal("hide");
												infoModal("修改成功",function(){
													roleTable();
												});
											}else{
												infoModal(data.message);
											}

										}
									})
								}
							},
							{
								"id":"clear_editPwd",
								"cls":"btn-warning btn-clear",
								"content":"取消"
							}
						]
					};
					initModal(modalPage);
				}
			});
		});
	}

	//需要用户确认的模态框
	function setTipModal(obj,url,operate){
		$(obj).unbind().on("click",function(){
			var page = new Object();
			var _this = this;
			var userId  = $("#userManageTable .checkedSingle:checked").attr("userId");
			var modal_show = true;
			var obj = viewDetail(userId);
			page.part_data = "";
            if(!isEmptyObject(obj)){
                if(operate=="启用" && obj.state == 0){//0-启用，1-停用
                    modal_show = false;
                    infoModal("该用户已启用");
                }
                if(operate=="停用" && obj.state == 1){//0-启用，1-停用
                    modal_show = false;
                    infoModal("该用户已停用");
                }
				if(operate=="删除" && login_id == userId){
					page.part_data = '该用户为当前登录用户，';
				}
                if(modal_show == true){
                    var modal = initModal({
                        id:"del",
                        header:"提示",
                        data:page.part_data + "确定"+operate+"吗？",
                        size:"sm",
                        buttons:[
                            {
                                "id":"save_"+page.id,
                                "cls":"btn-primary btn-confirm",
                                "content":"确定",
                                "clickEvent":function(){
                                    $(".modal-domain").modal("hide");
                                    $.ajax({
                                        type:"GET",
                                        url:$.base+ url+"?userId=" + userId,
                                        error:function(){},
                                        success:function(data){
                                            if(data.code==0){
                                                infoModal(operate+"成功",function(){
                                                    roleTable();
                                                });
                                            }else{
                                                infoModal(data.message);
                                            }
                                        }
                                    })
                                }
                            },
                            {
                                "id":"clear_"+page.id,
                                "cls":"btn-warning btn-clear",
                                "content":"取消"
                            }
                        ]
                    })
                }
            }else{
                infoModal("系统出错");
            }

		});
	}
	//导入
    function leadIn(){
        $("#fileInput").unbind().on("change",function(){
//      	$("#file").remove();
//			$("body").append("<input type='file' id='file' name='multipartFile'>")
//			$("#file").click();
//			$("#file").on("change",function(){
				base.form.fileUpload({ 
	               url:$.base+ '/user/leadIn',
	               id:"fileInput", 
	               params:{},
	               success:function(data){
	               		if(data.success){
	               			infoModal("上传成功")
	               		}else{
	               			infoModal("上传失败")
	               		}
		            },
		            error:function(d){
		            	initModal("服务器错误")
		            }
	           });
//			})
        });
    }
	//新增用户
	var addUser = function(params){
        var url = "";
        if ($("#userManage").attr("project") == "master") {
            url = $.base+"/sysUser/insertSysUser";
        } else if ($("#userManage").attr("project") == "slave") {
            url = $.base+"/user/add";
        }
		$.ajax({
			type:"POST",
			url:url,
			data:JSON.stringify(params),
			contentType:"application/json",
			error:function(){

			},
			success:function(data){
				if(data.code==0){
					$(".modal-domain").modal("hide");
					infoModal("新增成功",function(){
						roleTable();
					});
				}else{
					infoModal(data.message);
				}
			}
		})
	}
	//修改用户
	var editUser = function(params){
        var url = "";
        if ($("#userManage").attr("project") == "master") {
            url = $.base+"/sysUser/updateSysUser";
        } else if ($("#userManage").attr("project") == "slave") {
            url = $.base+"/user/modify";
        }
		$.ajax({
			type:"POST",
			url:url,
			data:JSON.stringify(params),
			contentType:"application/json",
			error:function(){

			},
			success:function(data){
				if(data.code==0){
					$(".modal-domain").modal("hide");
					infoModal("修改成功",function(){
						roleTable();
					});
				}else{
					infoModal(data.message);
				}
			}
		})
	}
	//查看用户详情
	var viewDetail = function(userId){
        var url = "";
        if ($("#userManage").attr("project") == "master") {
            url = $.base+"/sysUser/findOneUser?userId="+userId;
        } else if ($("#userManage").attr("project") == "slave") {
            url = $.base+"/user/findOneUser?userId="+userId;
        }
		var obj = {};
		$.ajax({
			type:"GET",
			url:url,
			async:false,
			error:function(){

			},
			success:function(data){
				if(data.code==0){
					// base.form.init(data.data,$("#form"));
					obj = data.data;
				}
			}
		});
		return obj;
	}
	//判断对象是否为空
    var isEmptyObject = function(e) {
        var t;
        for (t in e)
            return !1;
        return !0
    }
    return{
		run:function(){
			addRole();
            addUnit();
			roleTable();
			queryRes();
			clearForms();
            addAndEditBtn();
			editPwdBtn();
            leadIn();
            if ($("#userManage").attr("project") == "master") {
                setTipModal($("button[operate='del']"),"/sysUser/delSysUser","删除");
                setTipModal($("button[operate='start']"),"/sysUser/forceUserEnabled","启用");
                setTipModal($("button[operate='stop']"),"/sysUser/forceUserDisabled","停用");
            } else if ($("#userManage").attr("project") == "slave") {
                setTipModal($("button[operate='del']"),"/user/delete","删除");
                setTipModal($("button[operate='start']"),"/user/enableUser","启用");
                setTipModal($("button[operate='stop']"),"/user/disabled","停用");
            }

			// setStartStop();
		}
	}
	
})