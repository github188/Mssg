define(["tree","base","echarts","template"],function(tree,base,echart,template){
	var project = $(".content-padding-box").attr("project");
	var add_data = null;
	var chartOption1 ={
		color:["#04a0fa","#fa7e04"],
		legend:{
			right:10,
			data:[],
		},
		grid:{
			bottom:30,
			right:30,
			left:30,
			top:30
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: {
				animation: false
			}
		},
		xAxis: {
			type: 'category',
			data: [],
			splitNumber:10,
			boundaryGap: false,
			axisTick:{
				lineStyle:{
					color:"#666"
				}
			},
			axisLine:{
				lineStyle:{
					color:"#666"
				}
			}
		},
		yAxis: {
			type: 'value',
			boundaryGap: [0, 0],
			splitLine: {
				show: false
			},
			axisTick:{
				lineStyle:{
					color:"#666"
				}
			},
			axisLine:{
				lineStyle:{
					color:"#666"
				}
			}
			//offset:-20
		},
		tooltip:{
			show:true,
			trigger:'axis',
			formatter:function (d) {
				var res = d[0].name + "<br/>";
				$.each(d,function(index,item){
					item.seriesName = item.seriesName.split(" ")[0];
					res += item.seriesName + ":" + item.value + "<br/>";
				})
				return res;
			}
		},
		series: [{
			name: '流入',
			type: 'line',
			showSymbol: false,
			hoverAnimation: false,
			smooth:true,
			areaStyle: {normal: {opacity:0.3}},
			//lineStyle:{normal:{color:"#04a0fa"}},
			data: []
		},
			{
				name: '流出',
				type: 'line',
				smooth:true,
				showSymbol: false,
				hoverAnimation: false,
				areaStyle: {normal: {opacity:0.3}},
				data: [],
			}]
	};


	var chartOption2 = {
		tooltip: {
			trigger: 'item',
			formatter: "{a} <br/>{b}: {c} ({d}%)"
		},
		legend: {
			orient: 'vertical',
			x: 'right',
			data:[]
		},
		series: [
			{
				name:'订阅设备',
				type:'pie',
				radius: ['50%', '70%'],
				avoidLabelOverlap: false,
				label: {
					normal: {
						show: false,
						position: 'inner'
					},
					emphasis: {
						show: false,
						textStyle: {
							fontSize: '10',
							fontWeight: 'bold'
						}
					}
				},
				labelLine: {
					normal: {
						show: false
					}
				},
				data:[]
			}
		]
	};
	//详细数据展示
	function setDetailBoxs(){
		var url = "";
		if(project=="master"){
			url = $.base + "/monitor/resSurvey";
		}else{
			url = $.base + "/monitor/getResSurvey";
		}
		$.ajax({
			type:"GET",
			url:url,
			error:function(){
				alert("出错了！");
			},
			success:function(data){
				if(data.code==0){
					var res = data.data;
					var res_arr = [];
					if(project=="master"){
						res_arr = [
							{"name":"发布资源次数","value":res.resource,icon:"icon-ziyuangongxiang1"},
							{"name":"订阅次数","value":res.subscribe,icon:"icon-iconfontzhizuobiaozhun023108"},
							{"name":"实时播放次数","value":res.realtime,icon:"icon-map_shishishipin"},
							{"name":"历史播放次数","value":res.history,icon:"icon-huifulishishipin"},
							{"name":"共享单位","value":res.sharedUnit,icon:"icon--danwei-hui"},
							{"name":"在线单位","value":res.onlineUnit,icon:"icon-danwei-lan"},
							{"name":"总用户数","value":res.totalUser,icon:"icon-iconfontyonghu"},
							{"name":"在线用户","value":res.loginUser,icon:"icon-zaixian"}
						];
					}else{
						res_arr = [
							{"name":"共享资源次数","value":res.resource,icon:"icon-ziyuangongxiang1"},
							{"name":"订阅次数","value":res.subscribe,icon:"icon-iconfontzhizuobiaozhun023108"},
							{"name":"实时视频播放次数","value":res.realtime,icon:"icon-map_shishishipin"},
							{"name":"历史视频播放次数","value":res.history,icon:"icon-huifulishishipin"}
						]
					}
					//模版
					var menu = template.compile($("#detail-template").html());
					template.registerHelper("format",function(value){
						return format(value);
					});
					$("#detail-boxs").html(menu(res_arr));
				}
			}
		});
	}
	//格式化
	var format = function(value){
		var str = String(value);
		var arr = str.split(".");
//		var reg = /(\d+)(\d{3})/g;
		var integerArr = arr[0].split("");
		var integerStr = "";
//		if(reg.test(arr[0])){
//			integer = arr[0].replace(reg,"$1"+","+"$2");
//		}else{
//			integer = arr[0];
//		}
		for(var i=integerArr.length-3;i>0;i=i-3){
			integerArr[i-1] += ",";
		}
		integerStr = integerArr.join("");
		if(arr.length>1){
			return integerStr + "." + arr[1];
		}else{
			return integerStr;
		}
	}
	//设置滚动条
	function setScroll(){
		var occupy_height = 0;
		if(project=="master"){
			occupy_height = 190;
		}else{
			occupy_height = 90;
		}
		$("#tables").height(function(){
			var _height = $(this).parents(".content-padding-box").height()-occupy_height;
			return _height + "px";
		})
		base.scroll({
			container:$("#tables")
		});
	}
	//设置热门资源表格
	function hotResourceTable(days){
		var url = "";
		var columns = new Object();
		if(project=="master"){
			url = $.base + "/monitor/popResource?days="+days;
			columns = [
				{title:"资源名称",data:"resourceName"},
				{title:"发布时间",data:"createTime"},
				{title:"设备数",data:"dsQty"},
				{title:"订阅单位数",data:"subscribe"}
			];
		}else if(project=="slave"){
			url = $.base + "/monitor/getPopResource?days="+days;
			columns = [
				// {title:"序号",data:""},
				{title:"资源名称",data:"resourceName"},
				{title:"设备数",data:"dsQty"},
				{title:"实时观看次数",data:"realtime"},
				{title:"历史观看次数",data:"history"}
			];
		}
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var userTable = $(".hot-resource").DataTable({
				"autoWidth": false,
			  	"processing":false,
			  	"ordering":false,
			  	"searching":false,
			  	"info":false,
			  	"paging":false,
			  	"lengthChange":false,
				"serverSide":true,
			  	"destroy":true,
			  	"language":{
			  		"url":"../../js/lib/json/chinese.json"
			  	},
			  	"ajax":{
			  		url:url,
					type:"get",
					contentType:"application/json",
					data:function(d){

					},
					dataFilter:function(data){
						(typeof(data)=="string")?data = JSON.parse(data):data=data;
						var d = data;
						var res = new Array();
						if(project=="master"){
							$.each(d.data,function(index,item){
								if(index<5){
									res.push(item);
								}
							});
						}else if(project=="slave"){
							$.each(d.data,function(index,item){
								if(index<10){
									res.push(item);
								}
							});
						}
						d.data = res;
						return JSON.stringify(d);
					}
			  	},
			  	"columns":columns,
			  	"columnDefs":[
			  	],
			  	 "drawCallback": function(settings) {
			  	 }
			});
		})
	}
	//设置用户观看
	var userViewTable = function(days){
		var url = "";
		var columns = new Object();
		url =$.base + "/monitor/topViewUser?days="+days;
		columns = [
			{title:"姓名",data:"username"},
			{title:"单位",data:"companyName"},
			{title:"实时",data:"realtime"},
			{title:"历史",data:"history"},
			{title:"预览次数",data:"preview"}
		];
		if(project=="master"){
			url =$.base + "/monitor/topViewUser?days="+days;
			columns = [
				{title:"姓名",data:"username"},
				{title:"单位",data:"companyName"},
				{title:"实时",data:"realtime"},
				{title:"历史",data:"history"},
				{title:"预览次数",data:"preview"}
			];
		}else if(project=="slave"){
			url =$.base + "/monitor/getTopViewUser?days="+days;
			columns = [
				// {title:"序号",data:"id"},
				{title:"用户名称",data:"username"},
				{title:"观看设备数",data:"viewDevice"},
				{title:"实时观看次数",data:"realtime"},
				{title:"历史观看次数",data:"history"}
			];
		}
		require(["datatables.net","bsDatatables","resDatatables"],function(){
			var userTable = $(".user-view-table").DataTable({
				"autoWidth": false,
				"processing":false,
				"ordering":false,
				"searching":false,
				"info":false,
				"paging":false,
				"lengthChange":false,
				"serverSide":true,
				"destroy":true,
				"language":{
					"url":"../../js/lib/json/chinese.json"
				},
				"ajax":{
					url:url,
					type:"get",
					contentType:"application/json",
					data:function(d){

					},
					dataFilter:function(data){
						(typeof(data)=="string")?data = JSON.parse(data):data=data;
						var d = data;
						var res = new Array();
						if(project=="master"){
							$.each(d.data,function(index,item){
								if(index<5){
									res.push(item);
								}
							});
						}else if(project=="slave"){
							$.each(d.data,function(index,item){
								if(index<10){
									res.push(item);
								}
							});
						}
						d.data = res;
						return JSON.stringify(d);
					}
				},
				"columns":columns,
				"columnDefs":[
				],
				"drawCallback": function(settings) {
				}
			});
		})
	}
	//实时流量
	var getFlow = function(){
		var flow_obj = {};
		$.ajax({
			type:"GET",
			url:$.base + "/monitor/getTraffic",
			async:false,
			error:function(){

			},
			success:function(data){
				var res = data.data;
				flow_obj.in = Math.ceil(Math.random()*100);
				flow_obj.out = Math.ceil(Math.random()*100);
				if(data.code==0){
					// flow_obj.in = res.in;
					// flow_obj.out = res.out;

				}
			}
		});
		return flow_obj;
	}
	var constantFlow = function(){
		$("#constant-flow").height(function(){
			var _height = $(this).parents(".content-padding-box").height()-530
			return _height;
		});
		var flow_data = getFlow();
		var option_data_in = [];
		var option_data_out = [];
		var option_data_time = [];
		var cur_time = Date.parse(new Date());
		option_data_in.push(flow_data.in);
		option_data_out.push(flow_data.out);
		option_data_time.push(formatTime(cur_time));

		chartOption1.series[0].data = option_data_in;
		chartOption1.series[1].data = option_data_out;
		chartOption1.xAxis.data = option_data_time;
		// 基于准备好的dom，初始化echarts实例
		var myChart = echart.init(document.getElementById('constant-flow'));
		// 使用刚指定的配置项和数据显示图表。
		myChart.setOption(chartOption1);
		add_data = setInterval(function(){
			var update_data = getFlow();
			cur_time = cur_time + 5000;
			option_data_in.push(update_data.in);
			option_data_out.push(update_data.out);
			option_data_time.push(formatTime(cur_time));
			chartOption1.legend.data = [
				{name:"流入 " + update_data.in,"textStyle":{color:"#04a0fa"}},
				{name:"流出 " + update_data.out,"textStyle":{color:"#fa7e04"}}
			];
			chartOption1.series[0].name = "流入 " + update_data.in;
			chartOption1.series[1].name = "流出 " + update_data.out;
			chartOption1.series[0].data = option_data_in;
			chartOption1.series[1].data = option_data_out;
			chartOption1.xAxis.data = option_data_time;
			myChart.setOption(chartOption1);
		},5000);
	}

	var formatTime = function(timestamp){
		var date = new Date(timestamp);
		var year = date.getFullYear();
		var month = date.getMonth()<10? "0"+(date.getMonth()+1) : (date.getMonth()+1);
		var day = date.getDate()<10? "0"+date.getDate() : date.getDate();
		var hour = date.getHours()<10? "0"+date.getHours() : date.getHours();
		var minute = date.getMinutes()<10? "0"+date.getMinutes() : date.getMinutes();
		var second = date.getSeconds()<10? "0"+date.getSeconds() : date.getSeconds();
		return year + "-" + month + "-" + day + " " + hour +":" + minute + ":" + second;
	}
	//订阅单位Top5
	var orderNum = function(days){
		$("#order-num").height(function(){
			var _height = $(this).parents(".content-padding-box").height()-530
			return _height;
		});
		var legend_names = [];
		var series_data = [];
		$.ajax({
			type:"GET",
			url:$.base + "/monitor/topSubUnit?days="+days,
			async:false,
			error:function(){

			},
			success:function(data){
				if(data.code==0){
					$.each(data.data,function(index,item){
						legend_names.push(item.companyName);
						series_data.push({
							"name":item.companyName,
							"value":Number(item.subCount)
						})
					});
					// 基于准备好的dom，初始化echarts实例
					var myChart = echart.init(document.getElementById('order-num'));
					// debugger
					chartOption2.legend.data = legend_names;
					chartOption2.series[0].data = series_data;
					// 使用刚指定的配置项和数据显示图表。
					myChart.setOption(chartOption2);
				}
			}
		})
	}
	//刷新表格
	function getNewTable(){
		$(".time-change-tab li").unbind().on("click",function(){
			var _this = this;
			if($(_this).parent().attr("name") == "资源订阅单位"){
				orderNum($(_this).find("a").attr("value"));
			}else if($(_this).parent().attr("name") == "热门资源排名"){
				hotResourceTable($(_this).find("a").attr("value"));
			}else if($(_this).parent().attr("name") == "用户观看排名"){
				userViewTable($(_this).find("a").attr("value"));
			}
		});
	}
	return{
		run:function(){
			setScroll();
			hotResourceTable(1);
			userViewTable(1);
			getNewTable();
			if(project=="master"){
				constantFlow();
				orderNum(1);
			}
			setDetailBoxs();
			//清空定时器
			$("#menu-list").find("ul.child-menu-list li").bind("click",function(){
				clearInterval(add_data);
			});
		}
	}
	
})