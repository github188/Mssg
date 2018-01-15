define(["tree","base","bootstrap"],function(tree,base,bootstrap){
	var infoModal = function(info,clickBack){
		$(".modal-info").modal("hide");
		$(".modal-info").remove();
		var modal = '<div class="modal modal-info fade" id="modal-tip" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel">'+
			'<div class="modal-dialog modal-sm" role="document">'+
			'<div class="modal-content">'+
			'<div class="modal-header">'+
			' <button type="button" class="close" data-dismiss="modal" aria-label="Close">'+
			'<span aria-hidden="true">&times;</span>'+
			'</button>'+
			'<h4 class="modal-title">提示</h4>'+
			'</div>'+
			'<div class="modal-body">'+info+'</div>'+
			'<div class="modal-footer">'+
			'<button class="btn btn-primary" data-dismiss="modal">确定</button>'+
			'</div>'
		'</div>'+
		'</div>'+
		'</div>';
		$("body").append(modal);
		$("#modal-tip").modal("show");
		$("#modal-tip").on("shown.bs.modal",function(){
			// var top = $(document).height()-$("#modal-tip .modal-dialog").height()-20;
			// (top>0)?top=top:top=0;
			var top = 160;
			$("#modal-tip .modal-dialog").css("margin-top",top/2+"px");
		})
		if(clickBack){
			$("#modal-tip").on("hidden.bs.modal",function(){
				clickBack();
			})
		}
	}
	//用户注册弹出框
	function userRegister(){
		$("#registerLink").on("click",function(){
//			var modal = base.modal({
//				width:800,
//				label:"用户注册",
//				modalOption:{"backdrop":"static","keyboard":false},
//				url:"systemManage/userRegister/register.html",
//				buttons:[
//					{
//						label:"确定",
//						cls:"btn btn-info",
//						clickEvent:function(){
//						}
//					},
//					{
//						label:"取消",
//						cls:"btn btn-info",
//						clickEvent:function(){
//							modal.hide();
//						}
//					}
//				],
//				callback:function(){
//					$(".modal-dialog").css({"marginTop":50})
//					$("#setUnit").on("click",function(){
//						var text = $(this).html();
//						if(text == "设置"){
//							$("#unitSetBox").append(setUnit);
//							$(this).html("返回");
//						}else{
//							$("#unitSetBox").html("");
//							$(this).html("设置");
//						}
//					})
//				}
//			});
			window.location.href = "systemManage/userRegister/register.html"
		})
	}
	var setContent = function(){
		//	全局对象存储
		var obj = {};
		if($(".lg_header").attr("project") == "master"){
			obj.valCadeUrl = $.base+"/createValCade";
			obj.loginUrl = $.base+"/login";
		}else if($(".lg_header").attr("project") == "slave"){
			obj.valCadeUrl = $.base+"/slaveLogin/createslaveValCade";
			obj.loginUrl = $.base+"/slaveLogin/slaveCommonLogin";
		}
		
		//	生成验证码
		$(".captcha_img").attr("src",obj.valCadeUrl)
		
		$(".login_submit").unbind().on("click",function(){
			if($(".login_ursePass[name='loginName']").val()==''||$(".login_ursePass[name='login_ursePass']").val()){
				infoModal("用户名或密码不能为空");
				return;
			}
			var data = base.form.getParams("#form");
			$.ajax({
				url:obj.loginUrl,
				type:"post",
				data:JSON.stringify(data),
				dataType:"json",
				contentType:"application/json",
				success:function(d){
					if(d.success){
						sessionStorage.setItem("loginInfo",JSON.stringify(d.data));
						window.location.href = "main.html";
					}else{
						infoModal(d.message);
					}
				}
			})
		})
	}
//	登陆
	return{
		run:function(){
			userRegister();
			setContent();
		}
	}
})