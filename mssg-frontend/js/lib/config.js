require.config({
	baseUrl:(function() {
	    var curWwwPath = window.document.location.href;
	    var pathName = window.document.location.pathname;
	    var pos = curWwwPath.indexOf(pathName);
	    var localhostPath = curWwwPath.substring(0,pos);
	    var projectName = pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	    return(localhostPath);
	})() + "/js",
	shim: {
		bootstrap:["jquery"],
		jOrgChart:["jquery"],
		cityselect:["jquery"],
		carousel:['jquery'],
		easyui:['jquery'],
		jqScrollbar:['jquery','jqMousewheel'],
		jqMousewheel:["jquery"],
		jscroll:['jquery'],
		htEdgetype:['htTopo'],
		htAutolayout:['htTopo'],
		htDashflow:['htTopo'],
		htLive:['htTopo'],
		htHtmlNode:['htTopo'],
		binder:['jquery'],
		'template' : {
			exports:"template"
		},
		binder:['jquery'],
		'treeGrid' : {
			exports:"treeGrid"
		},
		graphLayout:['jquery'],
		//topo:["graphLayout"],
		graph:["topo"],
		highCharts:{
			exports: "Highcharts",
      		deps: ["jquery"]
      	},
      	highChartsMore:{
      		exports: "highChartsMore",
      		deps: ["highCharts"]
      	},
		highCharts3D:{
			exports: "Highcharts3D",
      		deps: ["highCharts"]
      	},
      	gauge:["highCharts","highChartsMore"],
		exp:["highCharts"],
		highDark:["highCharts"],
        'baiduMap': {
            deps: ['jquery'],
            exports: 'baiduMap'
        },
		'date':{
			deps: ['jquery'],
			exports: 'date'
		},
   		echartsGl:["echarts"],
   		tree:['jquery'],
		dateTimePicker:["jquery"],
		/*easyui:{
			exports: "easyui",
      		deps: ["jquery","easyuiCN"]
      	},
		easyuiCN:["jquery"]*/
	},
	
	paths:{
		"jquery" : "lib/jquery-1.11.3.min",
		"async":"lib/async",
		"bootstrap":"lib/bootstrap.min",
		"template":"lib/handlebars-v4.0.8",
		"radialIndicator":"lib/radialIndicator.min",
		"bsDatatables":"lib/dataTables.bootstrap",
		"resDatatables":"lib/dataTables.responsive.min",
		"datatables.net":"lib/jquery.dataTables.min",
		"jqueryUI":"lib/jquery-ui.min",
		"carousel":"lib/carousel",
		"popover":"lib/popover",
		"treeGrid":"lib/treeGrid/treeGrid",
		"echartsConfig":"lib/echartsConfig",
		"echarts" : "lib/echarts.min",
		"echarts2.0" : "lib/echarts2.0",
		"echartsGl":"lib/echarts-gl.min",
		"chinaGeo":"lib/map/chinaGeo",
		"jOrgChart":"lib/jquery.jOrgChart",
		"jqScrollbar":"lib/jquery.mCustomScrollbar.min",
		"radialIndicator":"lib/radialIndicator",
		"jqMousewheel":"lib/jquery.mousewheel.min",
		"jscroll":"lib/jscroll",
		"dateFormat":"lib/dateFormat",
		"graph":"lib/graph.editor",
		"topo":"lib/qunee-min",
		"graphLayout":"lib/graph.layout",
		"highCharts":"lib/highcharts",
		"highChartsMore":"lib/highcharts-more",
		"highCharts3D":"lib/highcharts-3d",
		"exp":"lib/exporting",
		"highDark":"lib/dark-unica",
		"gauge":"lib/solid-gauge",
		"date":"lib/laydate/laydate",
		"baiduMap":"http://api.map.baidu.com/api?v=2.0&ak=MCGlo2EFVeVl5w3jeGsbjIMGGClYLm2f",
		"base":"lib/base",
		"htTopo":"lib/ht",
		"htEdgetype":"lib/ht-edgetype",
		"htAutolayout":"lib/ht-autolayout",
		"htDashflow":"lib/ht-dashflow",
		"htLive":"lib/ht-live",
		"htHtmlNode":"lib/ht-htmlnode",
		"autoSelect":"lib/jquery.autoSelect",
		"three":"lib/three.min",
		"tree":"lib/jquery.ztree.all.min",
		"dateTimePicker":"lib/bootstrap-datetimepicker.min",
		"dateTimePickerCN":"lib/bootstrap-datetimepicker.zh-CN",
		"fileUpload":"lib/ajaxfileupload.min",
		"cookies":"lib/jquery.cookie",
		"treeTable":"lib/jquery.treetable",
		"date5.0":"lib/laydate5.0.min"
	},
	map: {
        '*': {
            'jquery': 'jquery-config'
        },
        'jquery-config': {
            'jquery': 'jquery'
        }
   }
});

define('jquery-config', ['jquery'], function(){
	var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPath = curWwwPath.substring(0,pos);
    var projectName = pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    $.base = localhostPath;
    $.pathM = "https://192.168.230.165:8443";
    $.pathS = "http://192.168.230.166:8090";
    return $;
});
