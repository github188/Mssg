define(["bootstrap","template","base","dateTimePicker"],function(bootstrap,template,base,dateTimePicker){
	//设置滚动条
	function setScroll(){
		base.scroll({
			container:$(".forms-box").parent()
		});
	}
	function setContent(){
//		初始化
		$.ajax({
			url:$.base+"/ser/findAll",
			type:"get",
			success:function(d){
				base.form.init(d.data['0001'],$("#centent-1"));
				base.form.init(d.data['0002'],$("#centent-2"));
				base.form.init(d.data['0003'],$("#centent-3"));
				base.form.init(d.data['0004'],$("#centent-4"));
				base.form.init(d.data['0005'],$("#centent-5"));
				base.form.init(d.data['0006'],$("#centent-6"));
				base.form.init(d.data['0007'],$("#centent-7"));
			}
		})
		//保存
		$("#saveBtn").unbind().on("click",function(){
			var arr = [];
			for(var i=1;i<=7;i++){
				var obj ={};
				obj.secCode = "000"+i;
				obj = $.extend(getForms($("#centent-"+i)),obj);
				arr.push(obj)
			}
			$.ajax({
				type:"post",
				url:$.base+"/ser/updateSecurityConfig",
				async:false,
				data:JSON.stringify(arr),
				contentType:"application/json",
				success:function(d){
					if(d.success){
						infoModal("安全配置成功")
					}
				}
			});
		})
	}
	return {
		run:function(){
			setScroll();
			setContent();
		}
	}
	
});
	