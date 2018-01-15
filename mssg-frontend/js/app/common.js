define(["base"],function(base){
	var self = {
//		行政区域
		xzqy:function(val){
			var dictItemName = null;
			$.ajax({
				url:$.base+"/dictItem/findAllxzqu",
				type:"get",
				async:false,
				success:function(d){
					$.each(d.data, function(i,o) {
						if(o.dictItemCode == val){
							dictItemName = o.dictItemName;
						}
					});
				}
			})
			return dictItemName;
		},
		//摄像机功能类型
		sxjgnlx:function(val){
			var arr = [];
				if(val!=null){
				arr = val.split("/");
			}
			var valArr = '';
			$.ajax({
				type:"get",
				url:"../../json/common/common.json",
				async:false,
				success:function(d){
					
					var data = d[0].SXJGNLX;
					$.each(arr, function(i,o) {
						i==0?valArr+=data[o]:valArr+="/"+data[o];
					});
				}
			});
			return valArr;
		},
		//摄像机位置类型
		sxjwzlx:function(val){
			var arr = [];
			if(val!=null){
				arr = val.split("/");
			}
			var valArr = '';
			$.ajax({
				type:"get",
				url:"../../json/common/common.json",
				async:false,
				success:function(d){
					var data = d[0].SXJWZLX;
					$.each(arr, function(i,o) {
						i==0?valArr+=data[o]:valArr+="/"+data[o];
					});
				}
			});
			return valArr;
		},
		//所属部门行业
		ssbmhy:function(val){
			var arr = [];
			if(val!=null){
				arr = val.split("/");
			}
			var valArr = '';
			$.ajax({
				type:"get",
				url:"../../json/common/common.json",
				async:false,
				success:function(d){
					var data = d[0].SSBMHY;
					$.each(arr, function(i,o) {
						i==0?valArr+=data[o]:valArr+="/"+data[o];
					});
				}
			});
			return valArr;
		},
		selectVal:function(val,item){
			var name = null;
			$.ajax({
				url:"../../json/common/common.json",
				type:"get",
				async:false,
				success:function(d){
					var data = d[0][item];
					name = data[val];
				}
			})
			return name;
		}
	};
	return self;
});