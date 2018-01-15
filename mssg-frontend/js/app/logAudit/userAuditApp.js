define(["bootstrap","template","base","dateTimePicker"],function(bootstrap,template,base,dateTimePicker){
	//日期选择器
	function dateTimePickers(){
		require(["dateTimePickerCN"],function(){
			$("#beginTime,#endTime").datetimepicker({
				language:  'zh-CN',
				weekStart: 1,
				todayBtn:  1,
				autoclose: 1,
				todayHighlight: 1,
				startView: 4,
				forceParse: 0,
				showMeridian: 1,
				format:"yyyy-mm-dd hh:ii:ss",
			}).on('changeDate', function (ev) {
				$(this).datetimepicker('hide');
			}) ;
		});
	}
	//用户
	function getuserList(){
		$(".table-container").height($(".content-padding-box").height()-60);
		base.scroll({
			container:$("#user-list").parents(".table-container")
		});
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var url = $.base+"/onLineLog/findAllOnLineLogByCondition";
			var userTable = $("#user-list").DataTable({
				"autoWidth": false,
				"processing":false,
				"ordering":false,
				"searching":false,
				"serverSide":true,
				"info":true,
				"paging":true,
				"lengthChange":false,
				"destroy":true,
				"language":{
					"url":"../../js/lib/json/chinese.json"
				},
				"ajax":{
					type:"GET",
					url:url,
					contentType: "application/json",
					data:function(d){
						var param = {};
						param.userName = $("input[name='loginName']").val();
						param.companyName = $("input[name='visitedUnit']").val();
						param.loginTime =  $("#beginTime").val();
						param.logoutTime =  $("#endTime").val();
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
					{title:"序号",data:"id"},
					{title:"用户名",data:"loginName"},
					{title:"登录IP",data:"loginIp"},
					{title:"单位名称",data:"companyName"},
					// {title:"流出流量",data:"onlineRate"},
					{title:"访问设备数",data: "visitCount"},
					{title:"上线时间",data:"loginTime"},
					{title:"下线时间",data:"logoutTime"},
					{title:"操作",data: "logoutTime"},
				],
				"columnDefs":[
					{
						"targets":0,
						"render":function(data,status,o,row){
							return row.row+1;
						}
					},
					{
						"targets":4,
						"render":function(data,status,obj){
							return "<a href='#' class='deviceDetails' obj='"+JSON.stringify(obj)+"'>"+data+"</a>";
						}
					},
					{
						"targets":6,
						"render":function(data,status){
							return data==""?"--":data;
						}
					},
					{
						"targets":-1,
						"render":function(data,status,obj){
							var disableStatus = false;
							data==""?disableStatus = false:disableStatus=true;
							if(disableStatus){
								return "<button class='btn btn-primary' disabled='true' style='margin-right: 5px;'>强制下线</button>";
							}else{
								return "<button class='btn btn-primary forceUserOffline' style='margin-right: 5px;'id='"+obj.id+"' userId='"+obj.userId+"'>强制下线</button>";
							}
						}
					}
				],
				"drawCallback": function(settings) {
//					设备详情
					deviceDetails();
					forceUserOffline();
				}
			});
		})
	}
	//查询历史
	function btnQuery(){
		$("#btn-query").on("click",function(){
			getuserList();
		})
	}
	//强制用户下线
	function forceUserOffline(){
		$(".forceUserOffline").unbind().on("click",function(){
			var obj = {};
			obj.userId = $(this).attr("userId");
			obj.id = $(this).attr("id");
			var modal = initModal({
				"id":"del",
				"header":"提示",
				"size":"sm",
				"data":"确定强制下线吗？",
				buttons:[{
					"id":"confirm_del",
					"cls":"btn-primary btn-confirm",
					"content":"确定",
					"clickEvent":function(){
						$("#modal-del").modal('hide');
						$.ajax({
							type:"get",
							url:$.base+"/onLineLog/forceUserOffline",
							async:true,
							data:obj,
							success:function(d){
								if(d.code == 0){
									infoModal("下线成功");
									tableReload($(".dataTable"));
								}else{
									infoModal(data.message);
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
			
			
		})
	}
	//设备详情
	var deviceDetails = function(){
		$(".deviceDetails").unbind().on("click",function(){
			var obj = JSON.parse($(this).attr("obj"));
			$.ajax({
				type:"GET",
				url:"safeAudit/userAudit/details.html",
				error:function(){
					alert("出错了！");
				},
				success:function(data){
					var modalPage = {
//						"id":id,
						"header":"详情",
						"data":data,
						"buttons":[
							{
								"id":"clear",
								"cls":"btn-warning btn-clear",
								"content":"取消"
							}
						],
						callback:function(){
							$(".titleUnitUser").html(obj.companyName+"单位："+obj.userName+"用户操作记录")
							detailTable(obj.id);
						}
					};
					initModal(modalPage);
				}
			});
		})
	}
//	详情内部表格
	var detailTable = function(onLineId){
		$("#details").parents(".table-container").height($(".content-padding-box").height()-200);
		base.scroll({
			container:$("#details").parents(".table-container")
		});
		var params = new Object();
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var url = $.base+"/onLineLog/findAllDeviceVistByonLineId";
			var userTable = $("#details").DataTable({
				"autoWidth": false,
				"processing":false,
				"ordering":false,
				"searching":false,
				"serverSide":true,
				"info":true,
				"paging":true,
				"lengthChange":false,
				"destroy":false,
				"language":{
					"url":"../../js/lib/json/chinese.json"
				},
				"ajax":{
					type:"GET",
					url:url,
					data:function(d){
						var params = $.extend( {"onLineId":onLineId}, {
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
					{title:"发生时间",data:"vistTime"},
					{title:"访问设备",data:"deviceName"},
					{title:"操作",data:"op_type"},
					{title:"访问时长",data:"vistLong"},
				],
				"columnDefs":[
					{
						"targets":2,
						"render":function(data,status,o,row){
							var val = '';
							if(data == 1){
								val = "实时视频";
							}else if(data == 2){
								val = "历史视频";
							}else if(data == 3){
								val = "下载";
							}
							return val;
						}
					},
				],
				"drawCallback": function(settings) {
				}
			});
		})
	}
	return {
		run:function(){
			getuserList();
			dateTimePickers();
			btnQuery();
		}
	}

});
	