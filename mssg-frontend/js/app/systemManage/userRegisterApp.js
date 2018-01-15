define(["tree","base","app/mainApp"],function(tree,base) {
	var companyInput = '<div class="col-md-12 form-group">'+
								'<label class="col-sm-2 control-label">单位</label>'+
								'<div class="col-sm-10">'+
									'<input type="text" class="form-control" name="cname" role="{required:true}"/>'+
									'<span class="btn-setting" id="setBack">返回</span>'+
									'<span class="asterisk"></span>'+
								'</div>'+
							'</div>'+
							'<div id="unitSetBox">'+
								'<div class="col-md-12 form-group">'+
									'<label class="col-sm-2 control-label">单位地址</label>'+
									'<div class="col-sm-10">'+
										'<input type="text" class="form-control" name="caddress" role="{required:true}"/>'+
									'</div>'+
									'<span class="asterisk"></span>'+
								'</div>'+
								'<div class="col-md-6 form-group">'+
									'<label class="col-sm-4 control-label">单位联系人</label>'+
									'<div class="col-sm-8">'+
										'<input type="text" class="form-control" name="ccontacts" role="{required:true}"/>'+
									'</div>'+
									'<span class="asterisk"></span>'+
								'</div>'+
								'<div class="col-md-6 form-group">'+
									'<label class="col-sm-4 control-label">职务</label>'+
									'<div class="col-sm-8">'+
										'<input type="text" class="form-control" name="cposition" role="{required:true}"/>'+
									'</div>'+
									'<span class="asterisk"></span>'+
								'</div>'+
								'<div class="col-md-6 form-group">'+
									'<label class="col-sm-4 control-label">办公电话</label>'+
									'<div class="col-sm-8">'+
										'<input type="text" class="form-control" name="cofficePhone" role="{required:true,telephone:true}"/>'+
									'</div>'+
									'<span class="asterisk"></span>'+
								'</div>'+
								'<div class="col-md-6 form-group">'+
									'<label class="col-sm-4 control-label">手机</label>'+
									'<div class="col-sm-8">'+
										'<input type="text" class="form-control" name="ctelephone" role="{required:true,mobile:true}"/>'+
									'</div>'+
									'<span class="asterisk"></span>'+
								'</div>'+
							'</div>'
	var setContent = function(){
		$("#setUnit").unbind().on("click",function(){
			$("#unitDetail select").removeAttr("role");
			$("#unitDetail").hide();
			$("#addUnit").append(companyInput);
			$("#unitDetail select").empty();
		})
		$("#addUnit").unbind().on("click","#setBack",function(){
			inputVal();
			$("#unitDetail select").attr("role","{require:true}");
			$("#unitDetail").show();
			$("#addUnit").empty();
		})
//		角色赋值
		$.ajax({
			url:$.base+"/slaveLogin/findAllRoleRegister",
			type:"get",
			success:function(d){
				$.each(d.data, function(i,o) {
					$("#roleId").append("<option value='"+o.id+"'>"+o.roleName+"</option>")
				});
			}
		})
	}
	//单位下拉框赋值
	var inputVal = function(){
		$.ajax({
			type:"get",
			url:$.base+"/slaveLogin/findAllCompanys",
			async:true,
			success:function(d){
				$.each(d.data, function(i,o){
					$("#unitDetail select").append("<option value='"+o.id+"'>"+o.companyName+"</option>")
				});
			}
		});
	}
		
	//注册
	var registerBtn = function(){
		$("#btn-register").unbind().on("click",function(){
			var pass = base.form.validate({
					form:$("#form"),
					checkAll:true
				});
			if(!pass){return;}
			//获取密码与确认密码的值，然后进行比较
			var pwd1 = $(".comparePwd1").val();
			var pwd2 = $(".comparePwd2").val();
			if(pwd1!=pwd2){
				$(".comparePwd1").after('<div class="ui-form-error pwdTip" style="color:red;text-align:left;">两次输入的密码不一致！</div>');
				$(".comparePwd1").css("border","1px solid #ff0000");
				return ;
			}else{
				$(".pwdTip").remove();
				$(".comparePwd1").css("border","1px solid #ccc")
			}
			var form = base.form.getParams($("#form"));
			$.ajax({
				type:"post",
				url:$.base+"/slaveLogin/registerSysUser",
				async:true,
				data:JSON.stringify(form),
				contentType:"application/json",
				success:function(d){
					if(d.success){
						infoModal("注册成功");
						$("input").val("");
					}else{
						infoModal(d.message);
						//$("input").val("");
					}
				}
			});
		})
	}
	//重置
	var clearVal = function(){
		$(".btn-warning").unbind().on("click",function(){
			$("input").val("");
		})
		base.scroll({
			container:$(".form-body")
		});
		
	}
    return{
		run:function(){
			setContent();
			inputVal();
			registerBtn();
			clearVal();
		}
	}
	
})