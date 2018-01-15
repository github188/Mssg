define(["tree","base"],function(tree,base){

	//设置表格
	var registerApprovalTable = function(){
		var url = ""
		url = $.base+"/apprlist/findAllRegditerPage";
		var scrollY = $(".table-box").height() - 100;
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
			  		url:url,
					type:"get",
					contentType:"application/json",
					data:function(d){
						var params = $.extend({},{
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
					{title:"",data:'id'},
			  		{title:"序号",data:"id"},
			  		{title:"注册用户名",data:"loginName"},
			  		{title:"姓名",data:"userName"},
			  		{title:"单位名称",data:"companyName"},
			  		{title:"状态",data:"apprStatus"}
			  	],
			  	"columnDefs":[
			  		{
			  			"targets":0,
			  			"render":function(data,d,o){
			  				var vals = "<input type='checkbox' class='form-control checkedSingle' name='call' value='"+data+"' />";
			  				if(o.apprStatus != "未审批"){
			  					vals = "<input type='checkbox' class='form-control checkedSingle' name='call' value=''/>";
			  				}
			  				return vals;
			  			}
			  		},
			  		{
			  			"targets":1,
			  			"render":function(data,d,o,r){
			  				return r.row+1;
			  			}
			  		}
			  	],
			  	 "drawCallback": function(settings) {
			  	 	//滚动条
					base.scroll({
						container:$(".dataTables_scrollBody")
					});
			  	 	$("input[type='checkbox']").on("click",function(){
			  	 		/*
			  	 		 *修审批按钮
			  	 		 * --只有选中一条的时候可以审批
			  	 		 * */
			  	 		if($(".checkedSingle:checked").length == 1){
				  	 		$(".btns-box").find("button[operate='examine']").removeAttr("disabled");
				  	 		examineBtn();
				  	 	}else{
				  	 		$(".btns-box").find("button[operate='examine']").attr("disabled",true);
				  	 	}
			  	 	})
			  	 }
			});
		})
	}
	//审批按钮
	var examineBtn = function(){
		$("button[operate='examine']").unbind().on("click",function(){
			var page = new Object();
			page.header = "审批";
			page.url = "resourceManage/registerApproval/examine.html";
			page.id = "examine";
			var checkId = base.getChecks("call")[0];
			if(checkId == ''){
				infoModal("非待审批状态！");
				return;
			}
			var excute = {};
        	excute.saveBack = function(){
				var form = getForms($("#content-3"));
				form.id=checkId;
				$.ajax({
					url:$.base+"/apprlist/apprRegister",
					data:form,
					type:'get',
					success:function(d){
						if(d.success){
							$(".modal").modal("hide");
							tableReload($(".dataTable"));
						}
					}
				})
        	}
        	excute.callback = function(){
        		$.ajax({
        			url:$.base+"/company/findAllComLevel",
        			type:"get",
        			async:true,
        			success:function(d){
        				$.each(d.data, function(i,o) {
        					$("#content-3 select[name='companylevel']").append("<option value='"+o.dictItemCode+"'>"+o.dictItemName+"</option>")
        				});
        			}
        		})
        		$.ajax({
        			url:$.base+"/apprlist/findOneSysUer?id="+checkId,
        			type:"get",
        			success:function(d){
        				base.form.init(d.data,$("#content-1"));
                		base.form.init(d.data,$("#content-2"));
                		$("#content-3 select[name='companylevel']").val(d.data.companyLevel)
        			}
        		})
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
						"data":data
					};
					setStepList(".modal",modalPage,excute);//分步
				}
			});
		});
	}
	return{
		run:function(){
			registerApprovalTable();
		}
	}
	
})