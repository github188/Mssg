define(["tree","base","app/mainApp","dateTimePicker","bootstrap"],function(tree,base,main,dateTimePicker,bootstrap){
	var indicator = 0;
    var deviceIds = new Array();
	var video_params = [];
    //获取当前日期前一天
    var dealDate = function(date){
        var year = date.getFullYear();
        var month = date.getMonth()<9?"0"+(date.getMonth()+1):(date.getMonth()+1);
        var day = date.getDate()-1<=9?"0"+date.getDate():date.getDate();
        return year+"-"+month+"-"+day;
    }
	var cur_date = new Date();
	var view_date = dealDate(cur_date);
	//设置树
	function setTree(){
		var level = $(".equip-box [name='eqLevel']").attr("show")=="true"?$(".equip-box [name='eqLevel']").val():"";
		var name = $(".equip-box [name='eqName']").attr("show")=="true"?$(".equip-box [name='eqName']").val():"";
		var location = $(".equip-box [name='position']").attr("show")=="true"?$(".equip-box [name='position']").val():"";
		//滚动条
		$(".tree-box .ztree").parent().height($(".tree-box").height()-$(".equip-tree").height()-$(".equip-box").height()-80).width(190+"px");
		base.scroll({
			container:$(".tree-box .ztree").parent(),
			axis:"yx",
			scrollbarPosition:"outside"
		});
		$.ajax({
			type:"GET",
			url:$.base + "/resSubscribe/findSubscribe?dsName=" + name + "&dsLevel=" + level + "&locationType=" + location,
			error:function(){
			},
			success:function(data){
				var res = (typeof(data)=="string")?JSON.parse(data):data;
				var nodes = [];
				var znode = [{dsId:null,dsName:"根目录",pid:-1}];
				var setting = {
					data: {
						key:{
							name:"dsName"
						},
						simpleData: {
							enable: true,
							name:"dsName",
							idKey: "dsCode",
							pIdKey: "pid",
							rootPId: null
						}
					},
					callback: {
						beforeDblClick: zTreeBeforeDblClick,
						onClick: zTreeOnClick
					}
				};
				if(res.code==0){
					$.each(res.data,function(index,item){
						$("body").append('<div class="ztree" style="display: none" id="ztree-bar-'+index+'"></div>');
						var node = setIcon(res.data[index]);
						if(node.length>0){
							var treeObj = $.fn.zTree.init($("#ztree-bar-"+index),setting,node);
							nodes.push(treeObj.transformTozTreeNodes(item)[0]);
						}
					});
					znode[0].children = nodes;
					//获取树的全部节点
					var setting2 = {
						data: {
							key:{
								name:"dsName"
							}
						},
						callback: {
							// beforeDblClick: zTreeBeforeDblClick,
							onClick: zTreeOnClick
						}
					};
					var treeObj = $.fn.zTree.init($("#ztree-bar"),setting2,znode);
					for(var i=0;i<nodes.length;i++){
						$("#ztree-bar-"+i).remove();
					}
				}
			}
		});
	}
	//禁止双击
	function zTreeBeforeDblClick(treeId, treeNode) {
		return false;
	};
	//改变目录树中设备的图标
	var setIcon = function(zNodes){
		$.each(zNodes,function(index,item){
			if(item.dsType==5){
				item.iconSkin = "diy01";
			}
			if(item.children){
				setIcon(item.children);
			}
		});
		return zNodes;
	}
	//树的点击事件
	var zTreeOnClick = function(event, treeId, treeNode) {
		$("#ztree-bar").width();
		if(treeNode.dsType == 5){
			if(treeNode.isOpen != true){
				getParams(treeNode.dsId);
				treeNode.isOpen = true;
				var index = Number($("#curPlayIndex").val());
				var add_flag = true;
				if(video_params.length>0){
					$.each(video_params,function(i1,o1){
						if(o1.index==index){
							add_flag = false;
						}
					})
				}
				var cur_screennum = Number($("#curPlayIndex").val());
				$(".is-play").removeAttr("disabled",false).removeClass("disabled");
				if(add_flag){
					video_params.push({
						index:index,
						deviceId:treeNode.dsId
					})
				}else{
					$.each(video_params,function(i1,o1){
						if(o1.index==index){
							var old_deviceId = o1.deviceId;
							setClose(old_deviceId,i1);
							o1.deviceId = treeNode.dsId;
						}
					});
				}

				var treeObj = $.fn.zTree.getZTreeObj("ztree-bar");
				var nodes = treeObj.transformToArray(treeObj.getNodes());
			}else{
				var cur_screennum = Number($("#curPlayIndex").val());
				var status = JSON.parse(ocxPWinStatus2());
				var pos = 0;
				$.each(video_params,function (i1,o1) {
					if(o1.deviceId==treeNode.dsId){
						pos = o1.index;
					}
				});
				alert("该设备已在第"+(pos+1)+"个窗口播放");
			}
		}
	};
	//选中播放器
	function getPlayer(){
		// $(".view-list li").each(function(index,item){
		// 	$(item).unbind().on("click",function(){
		// 		// alert(1111);
		// 	})
		// });
	}
	//获取参数
	var getParams = function(deviceId){
		// deviceId = "52010100001310013109";
		$.ajax({
			type:"GET",
			url:$.base + "/support/equipment-config",
			success:function(data){
				var res = data.data;
				var sipId = res.deviceId;
				// var sipId = "34030000002000000625";
				var sipId_10 = (sipId!=null)?sipId.substring(0,10):null;
				var host = res.host;
				// var host ="192.168.30.74";
				var ip = res.localIp;
				var transferType = (res.transferType=="udp")?"--no-tcp\n":"--rtp-over tcp\n";
				//var port = Math.ceil(Math.random()*10000+10000);
				var port = res.port;
				var sip_cfg =   "# This is a comment in the config file.\n"+
					"# pjsua --config-file alice.cfg\n"+
					"#--registrar sip:"+host+":"+port+";transport=udp\n"+
					"#--proxy sip:192.168.30.9:5060;transport=udp\n"+
					"--id sip:"+sipId+"@"+sipId_10+"\n"+
					"--realm "+sipId_10+"\n"+
					"--username "+sipId+"\n"+
					"--password 12345678"+"\n"+
					"--bound-addr "+"0.0.0.0"+"\n"+
					"--local-port "+res.port+"\n"+
					transferType+
					"--log-file=c:\\\\Hplayer_log.txt\n"+
					"--stun "+host+"\n"+
					"--no-cli-console\n"+
					"--digest-alg md5\n"+
					"--cli-telnet-port=4040";
				var url = "sip://sip:"+deviceId+"@"+host+":"+res.port;//deviceId(设备Id)@host:port
				CheckVersion();
				ocxInit(sip_cfg);
				recordInfoQuery(deviceId,sipId,sipId_10,host,port);
				playVod(deviceId,host,port);
			}
		})
	}
	function datetimepickers(){
		require(["dateTimePickerCN"],function(){
			var date = new Date();
			var year = date.getFullYear();
			var month = date.getMonth()<9?"0"+(date.getMonth()+1):(date.getMonth()+1);
			var day = date.getDate()-1<=9?"0"+(date.getDate()-1):(date.getDate()-1);
			var today = year + "-" + month + "-" + day;
			$("#datetimepicker-box").datetimepicker({
				language:  'zh-CN',
				todayHighlight: 1,
				startView: 2,
				minView:2,
				endDate:today,
				format:"yyyy-mm-dd"
			}).on('changeDate', function(ev){
				view_date = dealDate(ev.date);
			});
		});
	}
	//设置音量
	function setVol(){
		var is_drag = false;
		$('[data-toggle="tooltip"]').tooltip();
		var drag_element = document.getElementById("drag_element");
		//按下
		if (navigator.userAgent.match(/Trident/i) && navigator.userAgent.match(/MSIE 8.0/i)) { //判断浏览器内核是否为Trident内核IE8.0
//	       alert('IE8');
		}else{
			drag_element.addEventListener("mousedown",function(e){
				var e = e || window.event;
				//鼠标偏移位置
				mouseoffset_X = e.pageX - drag_element.offsetLeft;
				is_drag = true;
			});
			//移动
			document.onmousemove = function(e){
				var e = e || window.event;
				e.preventDefault();
				//鼠标当前位置
				var cur_X = e.pageX;
				var value = 0;
				//元素的新位置
				var move_X = 0;
				if(is_drag===true){
					move_X = cur_X -mouseoffset_X;
					//设定范围
					var max_X = drag_element.parentNode.parentNode.offsetWidth - drag_element.offsetWidth;
					move_X = Math.min(max_X,Math.max(0,move_X));
					value = Math.ceil(move_X/max_X*100);
					drag_element.style.left = value + "%";
					$(".progress-bar").width(value + "%");
					$("#drag_element").attr({"data-original-title":value});
					$("#drag_element").removeAttr("title");
					// $(".drag-progress .color-progress").width(value + "%");
				}
			}
			//松开
			document.onmouseup = function(){
				is_drag = false;
			}
		}
	}
	//按钮操作
	function btnOperate(){
		//录像
		video:{
			$(".btn-common").on("click",function(){
			})
		}
	}
	//转换日期格式
	function transdatetodt(timestr){
		var date=new Date();
		date.setFullYear(timestr.substring(0,4));
		date.setMonth(timestr.substring(5,7)-1);
		date.setDate(timestr.substring(8,10));
		date.setHours(timestr.substring(11,13));
		date.setMinutes(timestr.substring(14,16));
		date.setSeconds(timestr.substring(17,19));
		return Date.parse(date)/1000;
	}
	//获取设备信息
	function CheckVersion(){
		var obj = document.getElementById("PlayPanel");
		var version = obj.Version;
		var buildTime = obj.BuildDateTime;
	}
	//设置分屏
	function SetScreenNum(num){
		var obj = document.getElementById("PlayPanel");
		obj.ScreenNum = num;
	}
	//初始化控件
	function ocxInit(sip_cfg){
		var obj = document.getElementById("PlayPanel");
		var ret = obj.Init(sip_cfg);
	}
	//反初始化
	function ocxUnInit(){
		var obj = document.getElementById("PlayPanel");
		var ret = obj.UnInit();
	}
	//关闭
	function closeVedio(index){
		var play = document.getElementById("PlayPanel");
		ret = play.StopEx(index);
	}
	function ocxPWinStatus1(){
		var obj = document.getElementById("PlayPanel");
		var ret = obj.GetPlayWindowStatus(0);
		return ret;
	}
	function ocxPWinStatus2(){
		var obj = document.getElementById("PlayPanel");
		var ret = obj.GetAllPlayWindowStatus();
		return ret;
	}
	//截屏
	function snapshot(){
		var obj = document.getElementById("PlayPanel");
		var ret =  obj.SnapShot("E:\\aaa",1);
	}
	//开始录像
	function recordStart(){
		var obj = document.getElementById("PlayPanel");
		var ret = obj.StartRecord("E:\\aaa");
	}
	//结束录像
	function recordStop(){
		var obj = document.getElementById("PlayPanel");
		var ret = obj.StopRecord();
	}
	//录像检索
	function recordInfoQuery(deviceId,sipId,sipId_10,host,port){
		var obj = document.getElementById("PlayPanel");
		var local = "sip:"+sipId+"@"+sipId_10;
		var remote = "sip:"+deviceId+"@"+host+":"+port;
		var start_time = view_date + "T00:00:00";
		var end_time = view_date + "T23:59:59";
		var record_type = "All";
		var param = "ext";
		// start_time = document.getElementById("start_time").value;
		// end_time = document.getElementById("end_time").value;
		/*
		 start_time : 2017-09-12T00:00:00
		 end_time   : 2017-09-23T00:00:00
		 type: All/time/manual     所有/定时/手动
		 */
		//var ret = obj.GbSendMsg(1,local,remote,devid,param);
		var ret = obj.GbSendQueryRecordInfo(local,remote,deviceId,start_time,end_time,record_type,param);
	}
	//播放历史视频
	function playVod(deviceId,host,port){
		var obj = document.getElementById("PlayPanel");
		var vod_time = view_date + "T00:00:00";//开始时间
		var remote = "sip:"+deviceId+"@"+host+":"+port;
		var nBeginTime = transdatetodt(vod_time);
		var nEndTime = nBeginTime + 30;
		ret = obj.PlayVod(remote,nBeginTime,nEndTime);
	}
	//暂停
	function playVodPause(){
		var obj = document.getElementById("PlayPanel");
		ret = obj.VodPause();
	}
	//播放
	function playVodResume(){
		var obj = document.getElementById("PlayPanel");
		ret = obj.VodResume();
	}
	//回放速度
	function playVodSpeed(speed){
		var obj = document.getElementById("PlayPanel");
		ret = obj.VodSpeed(vod_speed);
	}
	//分屏
	function seperateScreen(){
		$(".btn-icons-box button").unbind().on("click",function(){
			var sreenNum = Number(($(this).attr("num")));
			var cur_screennum = Number($("#curPlayIndex").val());
			var status = JSON.parse(ocxPWinStatus2());
			if(sreenNum==1){
				for(var key in status){
					if(key!=cur_screennum && status[key]==1){
						$.each(video_params,function (i1,o1) {
							if(o1.index==key){
								setClose(o1.deviceId,i1);
							}
						})
					}
				}
			}else{
				if(sreenNum<cur_screennum){
					for(var key in status){
						if(key>sreenNum && status[key]==1){
							$.each(video_params,function (i1,o1) {
								if(o1.index==key){
									setClose(o1.deviceId,i1);
								}
							})
						}
					}
				}
			}
			$(this).siblings().removeClass("active");
			$(this).addClass("active");
			SetScreenNum(sreenNum);
		});
	}
	//右侧操作栏滚动条
	function operateScroll(){
		var paddingTop = ($(".right-operate-box .right-box").height() - 620)>0?($(".right-operate-box .right-box").height() - 620):0;
		$(".right-operate-box .right-box").css("padding-top",(paddingTop/2)+"px");
		base.scroll({
			container:$(".right-operate-box .right-box")
		});
	}
	//右侧菜单栏展开收起
	function expandOperateMenu(){
		$(".expand-box").unbind().on("click",function(){
			if($(this).attr("isExpand")=="false"){
				$(this).attr("isExpand","true").find("span.iconfont").removeClass("icon-expand").addClass("icon-retract");
				$(this).parents(".view-container").addClass("isConstract");
			}else{
				$(this).attr("isExpand","false").find("span.iconfont").removeClass("icon-retract").addClass("icon-expand");
				$(this).parents(".view-container").removeClass("isConstract");
			}
		});
	}
	//关闭之后 恢复节点可点击状态
	function setClose(removeId,win_index){
		//改变已关闭视屏的状态
		var treeObj = $.fn.zTree.getZTreeObj("ztree-bar");
		var nodes = treeObj.transformToArray(treeObj.getNodes());
		$.each(nodes,function(index,item){
			if(item.dsId == removeId){
				var curNode = treeObj.getNodesByParam("dsId", removeId, null);
				if (curNode.length>0) {
					curNode[0].isOpen = false;
					treeObj.updateNode(curNode[0]);
				}
			}
		});
		closeVedio(win_index);
	}
	function setCondition(){
		$("#res-device").on("change",function(){
			$(".equip-box").find("select,input").attr("show",false).hide();
			var val = $(this).val();
			switch (val){
				case "-1":
					break;
				case "1":
					$(".equip-box").find("select[name='eqLevel']").attr("show",true).show();
					break;
				case "2":
					$(".equip-box").find("select[name='position']").attr("show",true).show();
					break;
				case "3":
					$(".equip-box").find("input[name='eqName']").attr("show",true).show();
					break;
			}
			setTree();
			conditionChange();
		});
	}
	function conditionChange(){
		$(".equip-box").find("select,input").on("change",function(){
			setTree();
		})
	}
	//截屏按钮
	function snapPicture(){
		$("#snapScreen").unbind().on("click",function(){
			snapshot();
		})
	}
	//录像按钮
	function snapVideo(){
		$("#saveVideo").unbind().on("click",function(){
			if($(this).attr("status")==0){
				//开始录像
				recordStart();
				$(this).attr({"status":1,"title":"正在录像中..."});
			}else{
				//结束录像
				recordStop();
				$(this).attr({"status":0,"title":"开始录像"});
			}
		})
	}
	//关闭当前窗口的播放
	function stopCurVedio(){
		$(".stop-cur-vedio").unbind().on("click",function(){
			debugger
			var cur_window = $("#curPlayIndex").val();
			var status = JSON.parse(ocxPWinStatus2());
			for(var key in status){
				if(key==cur_window && status[key]==1){
					$.each(video_params,function (i1,o1) {
						if(o1.index==key){
							setClose(o1.deviceId,i1);
						}
					})
				}
			}
		});
	}
	//暂停播放
	function isPlay(){
		debugger
		$(".is-play").unbind().on("click",function(){
			var _this = this;
			if($(_this).attr("title")=="暂停"){
				//执行暂停操作
				$(_this).attr("title","播放").find("span").removeClass("icon-zanting2").addClass("icon-next");
				playVodPause();
			}else{
				//执行播放操作
				$(_this).attr("title","暂停").find("span").removeClass("icon-next").addClass("icon-zanting2");
				playVodResume();
			}
		})
	}
	//快放慢放
	function playVodBtn(){
		var speed_arr = [25,50,100,200,400];
		var i = 2;
		$(".fast-play").unbind().on("click",function(){
			i++;
			$(".slow-play").removeClass("disabled").removeAttr("disabled");
			if(i<=speed_arr.length-1){
				playVodSpeed(speed_arr[i]);
			}
			if(i>=speed_arr.length-1){
				$(".fast-play").addClass("disabled").attr("disabled",true);
			}
		});
		$(".slow-play").unbind().on("click",function(){
			i--;
			$(".fast-play").removeClass("disabled").removeAttr("disabled");
			if(i>=0){
				playVodSpeed(speed_arr[i]);
			}
			if(i<=0){
				$(".slow-play").addClass("disabled").attr("disabled",true);
			}
		});
	}
	return{
		run:function(){
			// setChangeInput();
			//刷新浏览器之后 关闭所有视频
			var iframe = window.parent.document.getElementById("videoBox");
			window.onunload = function() {
				ocxUnInit();
			}
			datetimepickers();
			seperateScreen();
			setTree();
			getPlayer();
			btnOperate();
			seperateScreen();
			operateScroll();
			expandOperateMenu();
			// setVol();
			setCondition();
			snapPicture();
			snapVideo();
			stopCurVedio();
			isPlay();
			playVodBtn();//快放慢放
			/*
			*      检查版本——CheckVersion
			*      设置分屏数目——SetScreenNum
			*      初始化——ocxInit
			*      反初始化——ocxUnInit
			*      窗口状态1（单个窗口状态）——ocxPWinStatus1
			*      窗口状态2（所有窗口状态）——ocxPWinStatus2
			*      播放文件——PlayFile
			*      停止——Stop
			*      国标实时——PlayGbReal
			*      截图——snapshot
			*      开始录像——recordStart
			*      停止录像——recordStop
			*      录像查询测试——recordInfoQuery
			*      回放测试——playVod
			*      回放速度——playVodSpeed
			*      回放跳转——playVodSeek
			*      回放暂停——playVodPause
			*      回放恢复——playVodResume
			*      下载测试——downloadRecord
			*      云台控制——ptzControl
			*/

			// setDate();
			//刷新浏览器之后 关闭所有视频
			window.onbeforeunload  = function (){
				ocxUnInit();
			}
		}
	}
	
})