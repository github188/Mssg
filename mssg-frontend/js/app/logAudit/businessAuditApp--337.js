define(["bootstrap","template","base","dateTimePicker"],function(bootstrap,template,base,dateTimePicker){
	var liLength = 0;
	var index = 0;//滚动到第几条
	var flag = 1;//定时器默认启动状态
	function getConstantList(){
		$.ajax({
			type:"GET",
			// url:"../../json/logAudit/realTimeLog.json",
			url:$.base+"/businesslog/listrealtime",
			error:function(){
				alert("出错了");
			},
			success:function(data){
				var res = data.data;
                var height = 0
				liLength += res.length;
                $(".tab-content").height($(".content-padding-box").height()-40);
                $("#constant-list").height($(".tab-content").height()-40);
                if(res.length>0){
                    var temp = template.compile($("#log-list-template").html());
                    $("#constant-list .list-body").append(temp(res));
                }
				alert(index);
				if(flag==0 && liLength>0){

					textScroll(index);
				}

			}
		});
	}

	var textScroll = function(i){
		var timer = setInterval(function(){
			if(liLength>0){
				i++;
				$("#constant-list .list-body").animate({
					"marginTop":-41*i + "px"
				},2000);
				index++;
				liLength--;
				flag = 1;
			}else{
				clearInterval(timer);
				flag = 0;//定时器暂停
			}
		},1000);
	}
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
	//历史
	function getHistoryList(){
		$(".table-container").height($("#stateTabContent").height()-80);
		base.scroll({
			container:$(".table-container")
		});
		var params = new Object();
		params.visitedUnit = $("input[name='visitedUnit']").val();
		params.beginTime =  $("#beginTime").val();
		params.endTime =  $("#endTime").val();
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var url = $.base+"/businesslog/listhisbycondition?unit="+params.visitedUnit+"&&startTime="+params.beginTime+"&&endTime="+params.endTime;
			var historyTable = $("#history-list").DataTable({
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
                    dataFilter:function(data){
                        var d = JSON.parse(data).data;
                        return JSON.stringify(d);
                    }
                },
			  	"columns":[
			  		{title:"发生时间",data:"startTime"},
			  		{title:"访问设备",data:"deviceID"},
			  		{title:"访问单位",data:"companyID"},
			  		{title:"访问类型",data:"visitType"},
			  		{title:"访问用户",data: "visitUser"}
			  	],
			  	"columnDefs":[

			  	],
			  	 "drawCallback": function(settings) {
			  	 }
			});
		})
	}

	//定时查询
    function  setQuantitativeCheck(){
        setInterval(function(){
            getConstantList();
        },5000);
    }
    //查询历史
	function historyQuery(){
		$("#historyQuery").on("click",function(){
			getHistoryList();
		})
	}
	return {
		run:function(){
			getConstantList();
			getHistoryList();
            setQuantitativeCheck();
			textScroll(0);
			dateTimePickers();
			historyQuery();
		}
	}
	
});
	