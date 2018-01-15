define(["base","datatables.net","tree"],function(base){
	var pageSize = 10;
	var self = {
		search:function(grid){
			grid.reload();
		},
		reset:function(form,grid){
			base.form.reset(form,function(){
				if(grid){
					grid.reload();
				}
			});
		},
		checkByGridButton:function(cbs){
			
			if(cbs&&cbs.length>0){
				var cd = $(cbs).filter(":checked");
				if(cd.length==1){
					$(".ui-grid-buttonbar .modify").removeClass("disabled");
					$(".ui-grid-buttonbar .delete").removeClass("disabled");
				}else if(cd.length>1){
					$(".ui-grid-buttonbar .modify").removeClass("disabled");
					$(".ui-grid-buttonbar .modify").addClass("disabled");
					$(".ui-grid-buttonbar .delete").removeClass("disabled");
				}else{
					$(".ui-grid-buttonbar .modify").removeClass("disabled");
					$(".ui-grid-buttonbar .modify").addClass("disabled");
					$(".ui-grid-buttonbar .delete").removeClass("disabled");
					$(".ui-grid-buttonbar .delete").addClass("disabled");
				}
			}
			
		},
		submit:function(option){
			var url = option.url?option.url:"";
			var params = option.params?option.params:null;
			var callback = option.callback?option.callback:null;
			var type = option.type?option.type:"get";
			var requestTip = base.requestTip();
			if(url){
				base.ajax({
					url:url,
					params:params,
					type:type,
					timeout:10000,
					success:function(data){
						if(callback){
							callback();
						}
						switch(data.code){
							case '0':
								requestTip.success(data.message);
							break;
							
							default:
								requestTip.error(data.message);
							break;
						}
						self.initButtonbar($(".ui-grid-buttonbar"));
					},
					beforeSend:function(){
						requestTip.wait();
					},
					error:function(){
						requestTip.error();
					}
				})
			}
			
		},
		
		setLocation:function(menuMapData,id){
			$(".ui-location ul").html("");
			var self = {};
			var data = base.findParentToArray(menuMapData,id);
			if(data&&data.length>0){
				$(data).each(function(i,o){
					var node = document.createElement("li");
					$(node).html(o.label);
					$(".ui-location ul").append(node);
					if(i!=data.length-1){
						$(node).append("<i class='fa fa-angle-right'></i>");
					}
				});
			}
		},
		getStar:function(starNumber){
			var s = "";
			for(var i = 1;i<=5;i++){
				if(i<=starNumber){
					s += "<i class='light fa fa-star'></i>";
				}else{
					s += "<i class='fa fa-star'></i>";
				}
			}
			return s;
		},
		loadPage:function(id){
			require(["app/mainApp"],function(app){
				app.loadPage(id);
			});
		},
		drawChart:function(option){
			var self = {};
			self.chartContainer = option.chartContainer?option.chartContainer:null;
			self.chartOption = option.chartOption?option.chartOption:null;
			self.data = option.data?option.data:null;
			self.callback = option.callback?option.callback:null;
			
			self.draw = function(){
				self.setOption();
				base.highCharts({
					container:self.chartContainer,
					chartOption:self.chartOption,
					callback:self.callback
				});
			};
			self.setOption = function(){
				self.chartOption.series = self.data;
			};
			
			self.draw();
		},
		
		arrayStrToNumber:function(data){
			var d = [];
			$(data).each(function(i,o){
				d.push(Number(o));
			});
			return d;
		},
		setPanelButtonbar:function(){
			$(".ui-panel-buttonbar li").on("click",function(){
				$(this).parent().children("li").removeClass("active");
				$(this).addClass("active");
			});
		},
		setGridLinkGroup:function(){
			$(".ui-grid-linkGroup li").on("click",function(){
				$(this).parent().children("li").removeClass("active");
				$(this).addClass("active");
			});
		},
		mergeTreeData:function(data,rootId){
			var self = {};
			var data = data;

			$(data).each(function(i,o){
				if(o.ds_code==rootId){
					o.icon = "../images/0.png";
					o.open = true;
					return true;
				}else{
					var hasChild = false;
					$(data).each(function(i1,o1){
						if(o1.parent_id == o.ds_code){
							o.icon = "../images/2.png";
							o.open = true;
							hasChild = true;
							return false;
						}
					})
					if(hasChild==false){
						o.icon = "../images/4.png";
					}
				}
			});


			return data;
		},
		gridFilter:function(data){
            var d = $.parseJSON(data);
            switch(d.code){
            	case "0":
            		d.recordsTotal = d.data.totalElements;
            		d.recordsFiltered = d.data.totalElements;
            		/*d.recordsTotal = 100;
            		d.recordsFiltered =100;*/
            		d.data = d.data.content;
            	break;
            	
            	default:
            		d.recordsTotal = 0;
            		d.recordsFiltered = 0;
            		d.data = [];
            		
            		var reqTip = base.requestTip();
            		reqTip.error(d.message);
            	break;
            }
            return JSON.stringify(d); 
       	},
       	gridPageFliter:function(d,size){
       		d.page = d.start/d.length;
       		d.size = size?size:pageSize;
       	},
       
       	getUserSession:function(req){
       		req.setRequestHeader("Test", "test");
       	},
       	getParams:function(d,form){
       		self.gridPageFliter(d);
			var params = base.form.getParams($(form),true);

			if(params==""){
				params+="size="+d.size+"&page="+d.page;
			}else{
				params+="&size="+d.size+"&page="+d.page;
			}
			return params;
        },
        getPostParams:function(d,form){
        	self.gridPageFliter(d);
        	var params = base.form.getParams($(form));
        	if(params){
        		$.extend(d,params);
        	}
        	return JSON.stringify(d);
        },
        initButtonbar:function(buttonbar){
        	$(buttonbar).find(".add").show();
        	$(buttonbar).find(".modify").removeClass("disabled").addClass("disabled");
        	$(buttonbar).find(".delete").removeClass("disabled").addClass("disabled");
        },
        treeTable:function(setting){
        	var self = {};
        	self.tmpRow = null;
			self.dg = function(data){
				if(data&&data.length>0){
					$(data).each(function(i,o){
						var row = document.createElement("tr");
						$(row).attr("data-tt-id",o.ds_code);
						$(row).attr("data-tt-parent-id",o.parent_id);
						$(row).addClass("ui-treeTableChild");
						var s = "";
						$(setting.aoColumns).each(function(i1,o1){
							var type = o1.type?o1.type:null;
							switch(type){
								case "checkbox":
									s+="<td><input type='checkbox' class='cb' name='cb' value='"+o[o1.data]+"'/></td>";
								break;

								case "SELECT":
									s+="<td>"+
											"<select class='form-control' resId='"+o[o1.data]+"'>" +
											"<option value='1'>一天</option>" +
											"<option value='2'>最近一周</option>" +
											"<option value='3'>一个月</option>" +
											"<option value='0'>任意时间</option>" +
											// "<option value='0'>时间段</option>" +
											"<option value='-1'>无权限</option>" +
											"</select>"+
										"</td>";
								break;

								case "conOperate":
									s+='<td>'+
											'<div class="select-box">' +
											'<input type="text" class="form-control select-tree-input" name="conOperate" resId="" resId="" readonly="" val="" resId="'+o[o1.data]+'"/>'+
											'<i class="select-icon fa icon-right fa-angle-down"></i>'+
											'<div class="select-tree-box">'+
											'<div class="ztree" id="conOperate"></div>'+
											'</div>' +
											'</div>'
										'</td>';
									break;

								case "hisOperate":
									s+='<td>'+
										'<div class="select-box">' +
										'<input type="text" class="form-control select-tree-input" name="hisOperate" resId="" resId="" readonly="" val="" resId="'+o[o1.data]+'"/>'+
										'<i class="select-icon fa icon-right fa-angle-down"></i>'+
										'<div class="select-tree-box">'+
										'<div class="ztree" id="hisOperate"></div>'+
										'</div>' +
										'</div>'
									'</td>';
									break;

								default:
									s+="<td>"+o[o1.data]+"</td>";
								break;
							}
						});
						
						$(row).html(s);
		    			$(self.tmpRow).after(row);
		    			self.tmpRow = row;
						self.dg(o.children);
						selectBox();
					});
				}
			};
			var data = setting.json.data;
        	
        	$(data).each(function(i,o){
        		if(o.children&&o.children.length>0){
        			self.tmpRow = $(setting.nTable).find("tbody tr[rootrow='"+i+"']");
        			self.dg(o.children);
        		}
        	});
        	base.treeTable({
        		container:setting.nTable,
				setting:{
					expandable:true
				}
        	});
        }
	};
	return self;
});