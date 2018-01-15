/**
	 * fableFactory(fableUI)前端核心库 版本v1.0，版权归属江苏飞搏软件（fablesoft）
	 * **/
define(["jquery","bootstrap","jqueryUI"],function(){
	var fableFactory = {};
	fableFactory.$scope = {};
	fableFactory.$scope.entities = [];
	fableFactory.tagLib = "li,em,map,ul,div,span,h1,h2,h3,h4,h5,h6,ol,img,canvas,label,dl,dt";
	fableFactory.msgGroup = {};
	fableFactory.$handle = {};
	fableFactory.IE = function(){
		if(window.XMLHttpRequest=="undefined"){
			return 6;
		}else{
			if(!$.support.style){
				return 7;
			}else{
				if(!$.support.opacity){
					return 8;
				}else{
					return 9;
				}
			}
		}
	}();
	fableFactory.isIE = function(){
		if(document.all){
			return true;
		}else{
			return false;
		}
	}();
	fableFactory.console = function(log){
		if(!fableFactory.isIE){
			console.log(log);
		}
	};

	fableFactory.error = function(errorCode,errorLog){
		if(!fableFactory.isIE){
			console.log("<"+errorCode+"错误>:"+errorLog);
		}
	};
	fableFactory.renderFucGroup = {"before":[],"after":[]};
	fableFactory.render = function(params){
		var self = {};
		
		self.beforeRenderCallback = function(){
			$(fableFactory.renderFucGroup.before).each(function(i,o){
				o();
			});
		};
		
		self.afterRenderCallback = function(){
			$(fableFactory.renderFucGroup.after).each(function(i,o){
				o();
			});
		};
		
		
		self.process = function(params){
			if(params){
				if(params.option){
					var option = params.option;
					var type = option.type?option.type:"custom";
					var fableFactoryTmp = fableFactory;
					var container = params.container?params.container:$("body");
					
					$(type.split(".")).each(function(i,o){
						if(!fableFactoryTmp[o]){
							if(type.split(".").length==1){
								if(fableFactory.tagLib.indexOf(type)>-1){
									fableFactoryTmp = fableFactory.custom;
									option.customType = option.type;
									option.type = "custom";
								}else{
									fableFactory.console("\""+type+"\""+"是无效的标签");
									fableFactoryTmp = null;
								}
							}
							return false;
						}else{
							fableFactoryTmp = fableFactoryTmp[o];
						}
						
					});
					
					if(fableFactoryTmp){
						//console.log(typeof(new fableFactoryTmp()))
						
							
						if(option.beforeCreateCallback){
							fableFactory.val(option.beforeCreateCallback)(option);
						}
						
						var domEntity = fableFactoryTmp({
							container:container,
							option:option
						//parentOption:engineOption.option
						});
						if(domEntity.create){
							
							domEntity.create();
						}
						//fableFactory.setProperty(domEntity,option);
						if(option.afterCreateCallback){
							fableFactory.val(option.afterCreateCallback)(domEntity,option);
						}
//						if(o.entityId){
//							fableFactoryFactory.entities[o.entityId] = obj;
//						}
						if(option.items&&option.items.length>0){
							$(option.items).each(function(i1,o1){
								self.process({
									container:domEntity.element,
									option:o1
									//parentOption:engineOption.option
								});
							})
						}
					}
				}		
			}
		};
		self.beforeRenderCallback();
		self.process(params);
		self.afterRenderCallback();
	};

	/**
	 * 页面body区域
	 * **/
	fableFactory.page = function(elementOption){
		var self = {};
		self.element = null;
		self.container = null;
		self.option = null;
		if(!elementOption){
			self.container = $("body");
		}else{
			self.container = elementOption.container?elementOption.container:$("body");
			self.option = elementOption.option?elementOption.option:null;
		}
		self.create = function(){
			self.element = $("body");
			fableFactory.setProperty(self);
			return self;
		};
		return self;
	};
	/**
	 * 页面页头区域
	 * **/
	fableFactory.header = function(elementOption){
		var self = {};
		self.element = null;
		self.container = null;
		self.option = null;
		if(!elementOption){
			self.container = $("body");
		}else{
			self.container = elementOption.container?elementOption.container:$("body");
			self.option = elementOption.option?elementOption.option:null;
		}
		
		self.create = function(){
			if(fableFactory.IE>8){
				self.element = document.createElement("header");
			}else{
				self.element = document.createElement("div");
				$(self.element).addClass("header");
			}
			fableFactory.setProperty(self);
			$(self.container).append(self.element);
			return self;
		};
		return self;
	};
	/**
	 * 页面主体区域
	 * **/
	fableFactory.section = function(elementOption){
		var self = {};
		self.element = null;
		self.container = null;
		self.option = null;
		if(!elementOption){
			self.container = $("body");
		}else{
			self.container = elementOption.container?elementOption.container:$("body");
			self.option = elementOption.option?elementOption.option:null;
		}
		
		self.create = function(){
			if(fableFactory.IE>8){
				self.element = document.createElement("section");
			}else{
				self.element = document.createElement("div");
				$(self.element).addClass("section");
			}
			fableFactory.setProperty(self);
			$(self.container).append(self.element);
			return self;
		};
		return self;
	};
	
	/**
	 * 页面页脚区域
	 * **/
	fableFactory.footer = function(elementOption){
		var self = {};
		self.element = null;
		self.container = null;
		self.option = null;
		if(!elementOption){
			self.container = $("body");
		}else{
			self.container = elementOption.container?elementOption.container:$("body");
			self.option = elementOption.option?elementOption.option:null;
		}
		self.create = function(){
			if(fableFactory.IE>8){
				self.element = document.createElement("footer");
			}else{
				self.element = document.createElement("div");
				$(self.element).addClass("footer");
			}
			fableFactory.setProperty(self);
			$(self.container).append(self.element);
			return self;
		};
		return self;
	};
	
	fableFactory.custom = function(elementOption){
		var self = {};
		self.element = null;
		self.container = null;
		self.option = null;
		if(elementOption){ 
			self.container = elementOption.container;
			self.option = elementOption.option?elementOption.option:null;
		}	
		self.create = function(){
			if(!self.option.customType){
				self.element = document.createElement("div");
			}else{
				self.element = document.createElement(self.option.customType);
			}
			fableFactory.setProperty(self);
			$(self.container).append(self.element);
			return self;
		};
		return self;
	};
	
	fableFactory.setProperty = function(domEntity){
		var obj = domEntity.element;
		var option = domEntity.option;
		if(option){
			if(option.id){
				$(obj).attr("id",option.id);
			}
			if(option.name){
				switch($(obj).get(0).tagName.toLowerCase()){
					case "input":
					case "select":
					case "textarea":
						$(obj).attr("name",option.name);
					break;
					default:
						$(obj).addClass(option.name);
					break;
				}
			}
			if(option.cls){
				$(obj).addClass(option.cls.replace(/,/g," "));
			}
			if(option.style){
				$(obj).attr("style",option.style.replace(/,/g,";"));
			}
			if(option.show==undefined||option.show){
			}else{
				$(obj).hide();
			}
			if(option.width){
				$(obj).css("width",option.width);
			}
			if(option.title){
				$(obj).attr("title",option.title);
			}
			if(option.height){
				$(obj).css("height",option.height);
			}
			if(option.value){
				$(obj).val(fableFactory.val(option.value));
			}
			if(option.readonly){
				$(obj).attr("readonly","readonly");
			}
			if(option.disabled){
				$(obj).attr("disabled","disabled");
			}
			if(option.attr){
				$(option.attr.split(",")).each(function(i,o){
					var oy = o.split("::");
					if(oy.length>1){
						$(obj).attr(oy[0],fableFactory.val(oy[1]));
					}
				});
			}
			if(option.html){
				if(option.type&&option.type.split(".")[0]=="button"){
					$(obj).append("<span>"+fableFactory.val(option.html)+"</span>");
				}else{
					$(obj).html(fableFactory.val(option.html));
				}
				
			}
			if(option.text){
				if(option.type&&option.type.split(".")[0]=="button"){
					$(obj).append("<span>"+fableFactory.val(option.text)+"</span>");
				}else{
					$(obj).text(fableFactory.val(option.text));
				}
				
			}
			if(option.target){
				$(obj).attr("target",option.target);
			}
			if(option.src){
				$(obj).attr("src",option.src);
			}
			if(option.method){
				$(obj).attr("method",option.method);
			}
			if(option.action){
				$(obj).attr("action",option.action);
			}
			if(option.placeholder){
				$(obj).attr("placeholder",option.placeholder);
			}
		
			if(option.clickCallback){
				$(obj).on("click",function(){
					fableFactory.call(option.clickCallback,{
						dom:obj,
						entity:domEntity
					});
				});
				
			}
			if(option.changeCallback){
				$(obj).on("change",function(){
					fableFactory.call(option.changeCallback,{
						dom:obj,
						entity:domEntity
					});
				});
			}
			if(option.rule){
				var rule = fableFactory.val(option.rule);
				$(obj).attr("rule",option.rule);
			}
			if(option.requiredTip&&option.requiredTip=="true"){
				$(domEntity.formGroup).append("<em class='fableFactory-form-isRequired'>*</em>");
			}
			if(option.formTip){
				$(domEntity.formGroup).append("<em class='fableFactory-form-tip'>("+option.formTip+")</em>");
			}
			
		}
	};
	/**
	 * 取值功能
	 * 参数说明(1.String  2.Object)
	 * 一.对于String参数，统一使用双括号取值。
		  1.不带修饰符$scope取值,例如:{{name}}
		  2.id取值
		    2.1 取对象,例如：{{#name}},是取id为name的对象
		    2.2 取对象值,例如：{{#name::val}},是取对象的a值
		  	2.3 取对象的属性,例如：{{#name::@a}},是取对象的a属性
		    2.4 取对象的html,例如：{{#name::html}}or{{.name::?}},是取对象的a的html内容
		    2.5 取对象的text,例如：{{#name::text}}or{{.name::??}},是取对象的a的text内容
		  3.class取值
		    2.1 取对象,例如：{{.name}},是取id为name的对象 {{.name?5}}下标0开始，表示取第6个元素
		    2.2 取对象值,例如：{{.name::val}},是取对象的a值
		  	2.3 取对象的属性,例如：{{.name::@a}},是取对象的a属性
		    2.4 取对象的html,例如：{{.name::html}},是取对象的a的html内容
		    2.5 取对象的text,例如：{{.name::text}},是取对象的a的text内容
	      二.对于Object参数，直接返回该Object对象，常用于方法，数据的注入
	 * **/
	fableFactory.val = function(obj){
		var self = {};
		self.analyze = function(s){
			
			var v = "";
			
			switch(s.substr(0,1).toLowerCase()){
				case "#":
					
					var domObj = null;
					if($(s.split("::")[0])){
						domObj = $(s.split("::")[0]);
					}
					if(!domObj||domObj.length<1){return "";}
					
					if(s.split("::").length>1){
						var valType = s.split("::")[1];
						switch(valType){
							case "val":
								v = domObj.val();
							break;
							
							case "html":
								v = domObj.html();
							break;
							
							case "text":
								v = domObj.text();
							break;
						}
						if(valType.substr(0,1).toLowerCase()=="@"){
							v = domObj.attr(valType.split("@")[1]);
						}
					}else{
						v = $(s);
					}
				break;
				
				case ".":
					var domObj = null;
					var tmp = s.split("::");
					if(tmp[0].split("?").length>1){
						if(!isNaN(tmp[0].split("?")[1])){
							domObj = $(tmp[0].split("?")[0]+":eq("+tmp[0].split("?")[1]+")");
						}else{
							domObj =  $(tmp[0].split("?")[0]);
						}
					}else{
						domObj =  $(tmp[0]);
					}
					if(!domObj||domObj.length<1){return "";}
					if(tmp.length>1){
						var valType = tmp[1];
						if(valType.substr(0,1).toLowerCase()=="@"){
							v = domObj.attr(valType.split("@")[1]);
						}else{
							switch(valType){
								case "val":
									v = domObj.val();
								break;
								
								case "html":
									v = domObj.html();
								break;
								
								case "text":
									v = domObj.text();
								break;
							}
						}
						
					}else{
						v = $(s);
					}
				break;
				
				default:
					v = (fableFactory.$scope)[s]?(fableFactory.$scope)[s]:null;
				break;
			}
			return v;
		};
		if(!obj){return "";}
		
		if(typeof(obj)!="string"){
			return obj;
		}else{
			var s1 = null;
			if(obj.split("{{").length<2){
				s1 = obj;
			}
			else if(obj.split("{{").length==2&&obj.split("}}").length==2){
				s1 = self.analyze(obj.replace(/{{/, "").replace(/}}/, ""));	
			}else{
				s1="";
				$(obj.split("{{")).each(function(i,o){
					if(o.split("}}").length>1){
						var v = o.split("{{")[0];
						s1+=self.analyze(v.split("}}")[0])+o.split("}}")[1];
					}else{
						s1+=o;
					}
				});
			}
			
			return s1;
		}
		
	};
	fableFactory.call = function(callback,params){
		callback = fableFactory.val(callback);
		if(typeof(callback)=="string"){
     		if(fableFactory.$scope[callback]){
     			fableFactory.$scope[callback](params?params:null);
     		}
 		}else{
 			callback(params?params:null);
 		}
	};
	fableFactory.link = function(elementOption){
		var self = {};
		self.element = null;
		self.container = null;
		self.option = null;
		if(elementOption){
			self.container = elementOption.container;
			self.option = elementOption.option?elementOption.option:null;
		}
		self.create = function(){
			if(self.container&&self.option){
				self.element = document.createElement("a");
				fableFactory.setProperty(self);
				$(self.container).append(self.element);
			}
			
			return self;
		};
		return self;
	};
	/**layout:1.inline 2.horizontal**/
	fableFactory.form = function(elementOption){
		var self = {};
		self.element = null;
		self.container = null;
		self.option = null;
		if(elementOption){
			self.container = elementOption.container;
			self.option = elementOption.option?elementOption.option:null;
		}
		self.create = function(){	
			if(self.container&&self.option){
				self.element = document.createElement("form");
				$(self.element).attr("role","form");
				if(self.option.data){
					self.option.items = fableFactory.val(self.option.data);
				}
				if(self.option.layout){
					$(self.element).addClass("form-"+self.option.layout);
				}
				fableFactory.setProperty(self);
				$(self.container).append(self.element);
			}
			
			return self;
		};
		return self;
	};
	
	fableFactory.form.input = function(elementOption){
		var self = {};
		self.domContainer = null;
		self.element = null;
		self.label = null;
		self.formGroup = null;
		self.container = null;
		self.option = null;
		if(elementOption){
			self.container = elementOption.container;
			self.option = elementOption.option?elementOption.option:null;
		}
		self.create = function(){
			
			if(self.option.element){
				self.element = $(self.option.element);
			}else{
				if(self.container&&self.option){
					
					self.createFormGroup();
					self.createLabel();
					self.createElement();
				}
			}
			fableFactory.setProperty(self);
			return self;
		};
		
		self.createFormGroup = function(){
			self.formGroup = document.createElement("div");
			$(self.formGroup).addClass("form-group fableFactory-form-group");
			$(self.container).append(self.formGroup);
		};
		self.labelClass= "control-label";
		self.createLabel = function(){
			if(self.option.label){
				self.label = document.createElement("label");
				//$(self.label).addClass("sr-only");
				if(self.option.id){
					$(self.label).attr("for",self.option.id);
				}
				$(self.label).html(self.option.label+"：");
				$(self.formGroup).append(self.label);
				if(self.option.col){
					var cy = self.option.col.split(",");
					if(!isNaN(cy[0])){
						$(self.label).addClass("col-sm-"+cy[0]);
					}
				}
				if(self.option.labelWidth){
					$(self.label).css("width",self.option.labelWidth+"px");
				}
				$(self.label).addClass(self.labelClass);
			}
			
		};
		self.elementClass = "form-control";
		self.createElement = function(){
			self.element = document.createElement("input");
			$(self.element).addClass(self.elementClass);
			if(self.option.col){
				self.domContainer = document.createElement("div");
				var cy = self.option.col.split(",");
				if(!isNaN(cy[1])){
					$(self.domContainer).addClass("col-sm-"+cy[1]);
				}
				$(self.formGroup).append(self.domContainer);
				$(self.domContainer).append(self.element);
			}else{
				$(self.formGroup).append(self.element);
			}
			
		};
		return self;
	};
	
	fableFactory.form.input.password = function(elementOption){
		var self = fableFactory.form.input.call(this,elementOption);
		
		self.create = function(){
			if(self.option.element){
				self.element = $(self.option.element);
				self.set();
			}else{
				if(self.container&&self.option){
					self.createFormGroup();
					self.createLabel();
					self.createElement();
					self.set();
				}
			}
			
			return self;
		};
		self.set = function(){
			$(self.element).attr("type","password");
			fableFactory.setProperty(self);
		};
		return self;
	};
	
	fableFactory.form.input.date = function(elementOption){
		var self = fableFactory.form.input.call(this,elementOption);
		self.option = elementOption.option?elementOption.option:null;
		self.dateOption = self.option.dateOption?self.option.dateOption:null;
		self.create = function(){
			if(self.option.element){
				self.element = $(self.option.element);
				self.set();
			}else{
				if(self.container&&self.option){
					self.createFormGroup();
					self.createLabel();
					self.createElement();
					self.set();
				}
			}
			
			return self;
		};
		self.set = function(){
			require(["date"],function(){
				$(self.element).addClass("laydate-icon");
				$(self.element).attr("readonly","readonly");
				$(self.element).css("cursor","pointer");
				$(self.element).on("click",function(){
					laydate(self.dateOption);
				});
				fableFactory.setProperty(self);
			});
		};
		return self;
	};
	fableFactory.form.input.hidden = function(elementOption){
		var self = fableFactory.form.input.call(this,elementOption);
		self.elementClass = "";
		self.create = function(){
			if(self.option.element){
				self.element = $(self.option.element);
				self.set();
			}else{
				if(self.container&&self.option){
					self.createElement();
				}
			}
			
			return self;
		};
		self.createElement = function(){
			self.element = document.createElement("input");
			$(self.element).attr("type","hidden");
			$(self.container).append(self.element);
			
		};
		
		return self;
	};
	fableFactory.form.input.file = function(elementOption){
		var self = fableFactory.form.input.call(this,elementOption);
		self.elementClass = "";
		self.labelClass="";
		self.create = function(){
			if(self.option.element){
				self.element = $(self.option.element);
				self.set();
			}else{
				if(self.container&&self.option){
					self.createFormGroup();
					self.createLabel();
					self.createElement();
					self.set();
				}
			}
			
			return self;
		};
		self.set = function(){
			$(self.element).css("display","inline-block");
			$(self.element).attr("type","file");
		};
		return self;
	};
	fableFactory.form.select = function(elementOption){
		var self = {};
		self.domContainer = null;
		self.element = null;
		self.label = null;
		self.formGroup = null;
		self.container = null;
		self.option = null;
		if(elementOption){
			self.container = elementOption.container;
			self.option = elementOption.option?elementOption.option:null;
		}
		self.create = function(){
			if(self.option.element){
				self.element = $(self.option.element);
			}else{
				if(self.container&&self.option){
					self.createFormGroup();
					self.createLabel();
					self.createElement();
				}
			}
			fableFactory.setProperty(self);
			
			return self;
		};
		self.createFormGroup = function(){
			self.formGroup = document.createElement("div");
			
			$(self.formGroup).addClass("form-group fableFactory-form-group");
			$(self.container).append(self.formGroup);
		};
		self.createLabel = function(){
			if(self.option.label){
				self.label = document.createElement("label");
				//$(self.label).addClass("sr-only");
				if(self.option.id){
					$(self.label).attr("for",self.option.id);
				}
				$(self.label).html(self.option.label+"：");
				$(self.formGroup).append(self.label);
				if(self.option.col){
					var cy = self.option.col.split(",");
					if(!isNaN(cy[0])){
						$(self.label).addClass("col-sm-"+cy[0]);
					}
				}
				$(self.label).addClass("control-label");
			}
			
		};
		self.createElement = function(){
			self.element = document.createElement("select");
			if(self.option.domType){
				$(self.element).attr("type",self.option.domType);
			}
			$(self.element).addClass("form-control");
			if(self.option.data){
				self.createOptions(fableFactory.val(self.option.data));
			}else if(self.option.remoteUrl){
				fableFactory.remote({
					url:fableFactory.val(self.option.remoteUrl),
					params:fableFactory.getObj(self.option.remoteParams),
					successCallback:function(data){
						self.createOptions(data);
					}
				});
			}
			if(self.option.col){
				self.domContainer = document.createElement("div");
				var cy = self.option.col.split(",");
				if(!isNaN(cy[1])){
					$(self.domContainer).addClass("col-sm-"+cy[1]);
				}
				$(self.formGroup).append(self.domContainer);
				$(self.domContainer).append(self.element);
			}else{
				$(self.formGroup).append(self.element);
			}
			
		};
		self.createOptions = function(data){
			$(self.element).append("<option value='-1'>*请选择*</option>");
			var hasSelected = false;
			$(data).each(function(i,o){
				var option = document.createElement("option");
				$(option).attr("value",o.value);
				$(self.element).append(option);
				if(o.selected){
					hasSelected = true;
					$(option).attr("selected",true);
				}else{
					if(!hasSelected){
						if(self.option.selected&&!isNaN(self.option.selected)){
							if(self.option.selected==i){
								$(option).attr("selected",true);
							}
						}else if(self.option.selectedValue){
							if(o.value==fableFactory.val(self.option.selectedValue)){
								$(option).attr("selected",true);
							}
						}
					}
					
					
				}
				$(option).text(o.text);
				
			});
		};
		return self;
	};
	fableFactory.getObj = function(paramsObj,keyParam){
		var params = {};
		if(!paramsObj){return params};
		
		if(typeof(paramsObj)!="string"){
			return paramsObj;
		}else{
			if(keyParam){
				$(paramsObj.split(",")).each(function(i,o){
					var tmpObj = o.split("::");
					
					if(tmpObj.length>1){
						var isKey = false;
						for(var k in keyParam){
							if(tmpObj[0]==k){
								params[tmpObj[0]] = keyParam[k];
								isKey = true;
							}
						}
						if(!isKey){
							var v = fableFactory.val(tmpObj[1]);;
							if(v=="true"){
								params[tmpObj[0]] = true;
							}else if(v=="false"){
								params[tmpObj[0]] = false;
							}else if(!isNaN(v)){
								params[tmpObj[0]] = Number(v);
							}else{
								params[tmpObj[0]] = fableFactory.val(tmpObj[1]);
							}
						}
					}
				});
			}else{
				$(paramsObj.split(",")).each(function(i,o){
					var tmpObj = o.split("::");
					if(tmpObj.length>1){
						var v = fableFactory.val(tmpObj[1]);
						if(v=="true"){
							params[tmpObj[0]] = true;
						}else if(v=="false"){
							params[tmpObj[0]] = false;
						}else if(!isNaN(v)){
							params[tmpObj[0]] = Number(v);
						}else{
							params[tmpObj[0]] = fableFactory.val(tmpObj[1]);
						}
						
					}
					
				});
			}
		}
		
		return params;
	};
	fableFactory.remote = function(option){
		var self = {};
		if(option){
			self.params = option.params?option.params:{};
			self.url = fableFactory.val(option.url);
			self.async = option.async?option.async:true;
			self.type = option.type?option.type:"get";
			self.dataType = option.dataType?option.dataType:"json";
			self.successCallback = option.successCallback?option.successCallback:null;
			self.errorCallback = option.errorCallback?option.errorCallback:null;
			self.beforeCallback = option.beforeCallback?option.beforeCallback:null;
			self.timeout = option.timeout?option.timeout:-1;
			/*self.hasError = option.hasError?option.hasError:false;
			self.hasBefore = option.hasBefore?option.hasBefore:false;
			self.errorText = option.errorText?option.errorText:"加载出错！";
			self.beforeText = option.beforeText?option.beforeText:"加载中...";*/
			self.remoteObj = null;
			self.isLongConnection =  option.isLongConnection?option.isLongConnection:false;
			if(self.isLongConnection){
				self.timeout = 0;
			}
			self.isConnect = true;
			self.connect = function(){
				self.remoteObj = $.ajax({
			         type:	self.type,
			         async: self.async,
			         url:	self.url,
			         contentType: function(){
	         						if(self.type.toLowerCase()=="post"){
	         							return "application/json; charset=utf-8";
	         						}else{
	         							return null;
	         						}}(),
			         data : function(){
		         					if(self.type.toLowerCase()=="post"){
		         						return JSON.stringify(self.params);
		         					}else{
		         						return self.params;
		         					}}(),
			         dataType:self.dataType,
			         timeout:self.timeout,
			         success: function(data){
			         	
			         	if(self.successCallback){
			         		
			         		fableFactory.call(self.successCallback,data);
			         		if(self.isLongConnection){
			         			if(self.isConnect)
			         			self.connect();
			         		}
			         	}
			         	
			         },
			         error: function(XMLHttpRequest, textStatus, errorThrown){
			         	if(self.errorCallback){
			         		fableFactory.call(self.errorCallback);
			         		if(self.isLongConnection){
			         			if(self.isConnect)
			         			self.connect();
			         		}
			         	}
			         },
			         beforeSend:function(XMLHttpRequest){
			         	
		              	if(self.beforeCallback){
		              		fableFactory.call(self.beforeCallback);
		              		
			         	}
		             },
		             complete:function(XMLHttpRequest, textStatus){
		             	if(textStatus=="timeout"){
		             		if(!self.isLongConnection){//如果非长链接，超时就断开请求
		             			self.remoteObj.abort();
		             		}
		             	}
		             }
	 			});
	 			return self;
			};
			self.stop = function(){
				self.isConnect = false;
				self.remoteObj.abort();
			};
			self.run = function(){
				self.isConnect = true;
				self.connect();
			};
			self.connect();
	 		return self;
		}
		
	};
	fableFactory.form.select.selectGroup = function(domOption){
		var self = {};
		self.domContainer = null;
		self.domGroup = [];
		self.label = null;
		self.formGroup = null;
		self.container = null;
		self.option = null;
		self.element = null;
		if(domOption){
			self.container = domOption.container;
			self.option = domOption.option?domOption.option:null;
		}
		self.create = function(){
			
			if(self.container&&self.option){
				self.createFormGroup();
				self.createLabel();
				self.createInit();
				self.entity = fableFactory.form.input.hidden({
					container:self.formGroup,
					option:{
						name:self.option.name
					}
				}).create();
				self.element = self.entity.element;
			}
			
			return self;
		};
		self.createInit = function(val,trigger){
			if(self.option.data){
				self.createElement(fableFactory.val(self.option.data),val,trigger);
			}else if(self.option.remoteData){
				var keyParam = null;
				if(self.option.key&&val){
					keyParam = {};
					keyParam[self.option.key] = val;
				}
				fableFactory.remote({
					url:fableFactory.val(self.option.remoteData),
					params:fableFactory.getObj(self.option.params,keyParam),
					successCallback:function(data){
						self.createElement(data,val,trigger);
					}
				});
			}
			
			
		};
		self.createElement = function(data,parentVal,trigger){
			var nodeData = null;
			var findData = function(data,v){
				$(data).each(function(i,o){
					if(nodeData){return false;}
					if(o.value==v){
						if(o.items&&o.items.length>0){
							nodeData = o.items;
						}
						return false;
					}
					if(o.items&&o.items.length>0){
						findData(o.items,v);
					}
				});
			};
			if(!parentVal){
				nodeData = data;
			}else{
				findData(data,parentVal);
			}
			
			if(nodeData){
				var selectObj = document.createElement("select");
				if(self.option.domType){
					$(selectObj).attr("type",self.option.domType);
				}
				$(selectObj).addClass("form-control");
				
				if(self.option.col){
					var domContainer = document.createElement("div");
					var cy = self.option.col.split(",");
					if(!isNaN(cy[1])){
						$(domContainer).addClass("col-sm-"+cy[1]);
					}
					$(self.formGroup).append(domContainer);
					$(domContainer).append(selectObj);
				}else{
					$(self.formGroup).append(selectObj);
				}
				
				selectObj.selId = fableFactory.getRandom(1000,9999);
				if(trigger){
					$(trigger).after(selectObj);
				}
				self.domGroup.push(selectObj);
				
				var setSelVal = function(){
					var s = "";
					$(self.element).val("");
					$(self.domGroup).each(function(i,o){
						
						if($(o).val()!="-1"){
							s+=$(o).val()+",";
						}
					});
					$(self.element).val(fableFactory.clearLastCharacter(s));
				};
				$(selectObj).on("change",function(){
					$(this).nextAll("select").remove();
					if($(this).val()!="-1"){
						self.createInit($(this).val(),this);
						
					}else{
						for(var i=0,j=self.domGroup.length;i<j;i++){
							if(self.domGroup[i].selId==selectObj.selId){
								self.domGroup = self.domGroup.splice(0,i+1);
								break;
							}
						}
						
					}
					setSelVal();
				});
				$(selectObj).append("<option value='-1'>*请选择*</option>");
				$(nodeData).each(function(i,o){
					var option = document.createElement("option");
					$(option).attr("value",o.value);
					if(o.selected){
						$(option).attr("selected",true);
					}
					$(option).text(o.text);
					$(selectObj).append(option);
				});
				
			}
			
			
		};
		self.createFormGroup = function(){
			self.formGroup = document.createElement("div");
			
			$(self.formGroup).addClass("form-group fableFactory-form-group");
			$(self.container).append(self.formGroup);
		};
		self.createLabel = function(){
			if(self.option.label){
				self.label = document.createElement("label");
				//$(self.label).addClass("sr-only");
				if(self.option.id){
					$(self.label).attr("for",self.option.id);
				}
				$(self.label).html(self.option.label+"：");
				$(self.formGroup).append(self.label);
				if(self.option.col){
					var cy = self.option.col.split(",");
					if(!isNaN(cy[0])){
						$(self.label).addClass("col-sm-"+cy[0]);
					}
				}
				$(self.label).addClass("control-label");
			}
			
		};
		return self;
	};
	fableFactory.clearLastCharacter = function(str){
		str=str.substring(0,str.length-1);
		return str;
	};
	fableFactory.getRandom = function(min,max){
		return Math.round(Math.random() * (max - min) + min);
	};
	/**
	 *表单校验
	 * validates是表单控件校验规则（必须）
	  		属性
	  		1.必填项 required(boolean) (例如：required:true)
	  		2.整数  number(boolean或整数组) (例如: number:true   number[200,500]{200-500之间})
	  		3.数字  floating(boolean或数组) (例如: floating:true   floating[0.3,1]{0.3-1之间})
	  		4.金额格式 isMoney (boolean)
	  		5.邮件格式 isEmail (boolean)
	  		6.手机格式 isMobile (boolean)
	  		7.固定电话 isTelephone (boolean)
	  		8.数据长度 length (int) 不超过n个字符
	  		9.过滤非法字符，只允许数字、字母、下划线  isFliter (boolean)
	  		//10.ajax校验  ajaxUrl(url)
	 * 
	 * container（可选），表单容器，不填默认是整个document
	 * **/
	fableFactory.validate = function(validates,container){
		var isPass = true;
		var errorObj = null;
		if(!container){
			container = $("body");
		}
		$("#errorMsg").remove();
		var errorfuc = function(text){
			return "<div id='errorMsg' style='background:'><i class='ion-android-cancel'></i><span style='color:red'>"+text+"</span><i class='ion-android-arrow-dropdown'></i>"+"</div>";
		};
		if(validates){
			//$(validates).each(function(i,o){
			for(var validate in validates){
				var obj = $(container).find("[name='"+validate+"']");
				
				if(obj){
					var rules = fableFactory.getObj(validates[validate]);
					
					switch($(obj)[0].tagName.toLowerCase()){
						case "input":
							var type = "text";
							if($(obj).attr("type")){
								type = $(obj).attr("type").toLowerCase();
							}
							switch(type){
								case "text":
								case "password":
								
									for(var rule in rules){
										switch(rule){
											case "isRequired":
											case "required"://是否必填
												if(rules[rule]){
													
													if(!$(obj).val()){
														$(obj).parent().append(errorfuc("必填项"));
														isPass = false;
														return false;	
													}
												}
												
											break;
											
											case "length"://字符串长度
												if($(obj).val().length>rules[rule]){
													
													$(obj).parent().append(errorfuc("不能超过"+rules[rule]+"个字符"));
													isPass = false;
													return false;
												}
											break;
											
											case "number"://1.boolean(非0整数) 2.整数组 int[min,max],分别是数字的上下限
												var n = rules[rule];
												var ex = /^\d+$/;
												if($.isArray(n)){
													if (!ex.test($(obj).val())){
														
														$(obj).parent().append(errorfuc("非整数"));
														isPass = false;
														return false;
													}else{
														if($(obj).val()<=0){
															
															$(obj).parent().append(errorfuc("不能为0或负数"));
															isPass = false;
															return false;
														}
														if($(obj).val()<n[0]){
															
															$(obj).parent().append(errorfuc("数字过小"));
															isPass = false;
															return false;
														}
														if($(obj).val()>n[1]){
															
															$(obj).parent().append(errorfuc("数字过大"));
															isPass = false;
															return false;
														}
													}
												}else{
													if (n){
														if (!ex.test($(obj).val())){
															
															$(obj).parent().append(errorfuc("非整数"));
															isPass = false;
															return false;
														}
														if($(obj).val()<=0){
															
															$(obj).parent().append(errorfuc("不能为0或负数"));
															isPass = false;
															return false;
														}
													}
													
												}
												
												
											break;
											
											case "floating"://浮点型
											    var isFloat = /^\d+(\.\d+)?$/;
											    var n = rules[rule];
											    if($.isArray(n)){
													if (!isFloat.test($(obj).val())){
														
														$(obj).parent().append(errorfuc("非数字"));
														isPass = false;
														return false;
													}else{
														if($(obj).val()<=0){
															
															$(obj).parent().append(errorfuc("不能为0或负数"));
															isPass = false;
															return false;
														}
														if($(obj).val()<n[0]){
															
															$(obj).parent().append(errorfuc("数字过小"));
															isPass = false;
															return false;
														}
														if($(obj).val()>n[1]){
															
															$(obj).parent().append(errorfuc("数字过大"));
															isPass = false;
															return false;
														}
													}
												}else{
													if (n){
														if (!isFloat.test($(obj).val())){
															
															$(obj).parent().append(errorfuc("非数字"));
															isPass = false;
															return false;
														}
														if($(obj).val()<=0){
															
															$(obj).parent().append(errorfuc("不能为0或负数"));
															isPass = false;
															return false;
														}
													}
													
												}
											break;
											
											case "isMoney"://金额
												var isMoney = /^\d+\.?\d{1,2}$/;
												var money = $(obj).val();
												if(rules[rule]){
													if(!isMoney.test(money)){
														
														$(obj).parent().append(errorfuc("金额格式不符"));
														isPass = false;
														return false;
													}
												}
												
											break;
											
											case "isMobile"://手机号
												var mobile = $(obj).val();
												var isMobile = /(1[3-9]\d{9}$)/;
												if(rules[rule]){
													if (!isMobile.test(mobile)) {
														
														$(obj).parent().append(errorfuc("请填写正确的手机号码"));
														isPass = false;
														return false;
													}
												}
											break;
											
											case "isTelephone"://电话
												var phone = $(obj).val();
												var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
												if(rules[rule]){
													if (!isPhone.test(phone)) { 
														
														$(obj).parent().append(errorfuc("请填写正确的电话号码"));
														isPass = false;
														return false;
													}
												}
											break;
											
											case "isEmail"://邮箱
												var isEmail = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
												var email = $(obj).val();
												if(rules[rule]){
													if(!isEmail.test(email)){
														
														$(obj).parent().append(errorfuc("请填写正确的邮箱地址"));
														isPass = false;
														return false;
													}
												}
											break;
											
											case "isFliter"://只允许字母、数字、下划线
												var isFliter = /^\w+$/;
												if(rules[rule]){
													if(!isFliter.test($(obj).val())){
														
														$(obj).parent().append(errorfuc("只允许字母、数字、下划线"));
														isPass = false;
														return false;
													}
												}
											break;
											
											case "card"://身份证
												 var reg = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
												 if(rules[rule]){
												 	if(reg.test($(obj).val()) === false){  
												 		
												 		$(obj).parent().append(errorfuc("身份证填写错误"));
												        return false;  
												    }else{
												    	var cityCode={ 11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",  
												            21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",  
												            33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",  
												            42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",  
												            51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",  
												            63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"  
												           };  
												 	var province = $(obj).val().substring(0,2);
											     	if(cityCode[province] == undefined){
											     		
												 		$(obj).parent().append("<div class='errorTip'><i class='ion-android-cancel'></i>无效的身份证格式</div>");
											        	return false;  
											   		 }
												    }
												 	  
												 }
												 
											break;
											
										}
									}
								break;
								
								case "radio":
								case "checkbox":
									
									for(var rule in rules){
										switch(rule){
											case "isRequired":
											case "required"://是否必填
												if(fableFactory.getCheckbox(o.name).length==0){
													if($(obj).parent().hasClass("inputGroup")){
														
														$(obj).parent().append(errorfuc("必选项"));
													}
													if($(obj).parent().parent().hasClass("inputGroup")){
														
														$(obj).parent().append(errorfuc("必选项"));
													}
													isPass = false;
													return false;
												}
											break
										}
									}
									
								break;
							}
						break;
						
						case "select":
							
							for(var rule in rules){
								switch(rule){
									case "isRequired":
									case "required"://是否必填
										if($(obj).val()=="-1"){
											$(obj).parent().append(errorfuc("必选项"));
											isPass = false;
											return false;
										}
									break;
								}
							}
							
						break;
						
						case "textarea":
							
							for(var rule in rules){
								switch(rule){
									case "isRequired":
									case "required"://是否必填
										if(!$(obj).val()){
											$(obj).parent().append(errorfuc("必选项"));
											isPass = false;
											return false;
										}
									break;
									
									case "isFliter":
										var isFliter = /^\w+$/;
										if(rules[rule]){
											if(!isFliter.test($(obj).val())){
											
												$(obj).parent().append(errorfuc("只允许字母、数字、下划线"));
												isPass = false;
												return false;
											}
										}
									break;
									
									case "length":
										if($(obj).val().length>rules[rule]){
											
											$(obj).parent().append(errorfuc("不能超过"+rules[rule]+"个字符"));
											isPass = false;
											return false;
										}
									break;
								}
							}
							
						break;
					}
				}}}
		return isPass;
	};
	
	
	fableFactory.button = function(elementOption){
		if(!elementOption){
			elementOption = {};
		}
		var self = {};
		self.container = elementOption.container?elementOption.container:$("body");
		self.option = elementOption.option?elementOption.option:{};
		self.element = null;
		self.create = function(){
			
			if(self.option.element){
				self.element = $(self.option.element);
			}else{
				if(self.container&&self.option){
					self.createElement();
				}
			}
			fableFactory.setProperty(self);
			
			return self;
		};
		self.createElement = function(){
			self.element = $(document.createElement("button"));
			if(self.option.cls){
				$(self.element).addClass(self.option.cls);
			}else{
				$(self.element).addClass("btn btn-primary");
			}
			$(self.container).append(self.element);

		};
		return self;
	};
	fableFactory.button.sh = function(elementOption){
		var self = fableFactory.button.call(this,elementOption);
		self.elementClass = "";
		self.labelClass="";
		self.create = function(){
			
			if(self.container&&self.option){
				
				self.createElement();
				self.set();
			}
			fableFactory.setProperty(self);
			return self;
		};
		self.set = function(){
			self.isOpen = self.option.isOpen?self.option.isOpen:"true";
			if(self.isOpen=="true"){
				$(self.element).prepend("<i class='ion-ios-minus-empty'></i>");
			}else{
				$(self.element).prepend("<i class='ion-ios-browsers-outline'></i>");
			}
			$(self.element).on("click",function(){
				if(self.isOpen=="true"){
					$(self.element).find("i").attr("class","ion-ios-browsers-outline")
					self.isOpen ="false";
				}else{
					$(self.element).find("i").attr("class","ion-ios-minus-empty")
					self.isOpen = "true";
				}
			});
		};
		return self;
	};
	
	fableFactory.button.close = function(elementOption){
		
		var self = fableFactory.button.call(this,elementOption);
		
		self.create = function(){
			if(self.container&&self.option){
				
				self.createElement();
				
			}
			fableFactory.setProperty(self);
			return self;
		};
		
		return self;
	};
	/**获取选中的checkbox**/
	fableFactory.getCheckbox = function(name){
		var cbs = [];
		var cbs = $("input[name='"+name+"']:checked");
	    return cbs;
	};
	/**轮播组件**/
	fableFactory.carousel = function(elementOption){
    	var self = {};
    	if(!elementOption){
    		return;
    	}else{
    		self.option = elementOption.option?elementOption.option:null;
	    	self.container = $(elementOption.container)?$(elementOption.container):null;
	    	if(!self.option||!self.container){return;}
	    	self.index = 0;
	    	self.data = self.option.data?self.option.data:null;
	    	self.remoteData = self.option.remoteData?self.option.remoteData:null;
	    	self.step = self.option.step?self.option.step:4;
	    	self.interval = self.option.interval?self.option.interval:false;
	    	self.carouselBody = $(self.container).find(".carousel-inner");
    	}
    	
    	self.setCarousel = function(){
    		if(self.option.setCarouselCallback){
    			self.option.setCarouselCallback(self);
    		}
    		
    	};
    	self.next = function(){
    		$(self.container).carousel('next');
    	};
    	self.prev = function(){
    		$(self.container).carousel('prev');
    	};
    	self.drawCarousel = function(){
    		if(self.option.slideCallback){
    			$(self.container).on("slide.bs.carousel",function(){
    				self.option.slideCallback(self);
    			});
    		}
    		
			if(self.data){
				
		    	self.setCarousel();
		    	$(self.container).carousel({
					interval: self.interval
				});
			}
    	};
    	self.create = function(){
    		fableFactory.setProperty(self);
    		self.drawCarousel();
    		self.setCarousel();
			return self;
    	};
		
		return self;
    };
    /**进度条**/
    fableFactory.progress = function(elementOption){
    	var self = {};
    	self.element = null;
    	self.option = elementOption.option?elementOption.option:null;
    	self.container = elementOption.container?elementOption.container:null;
    	self.data = Number(self.option.data)?Number(self.option.data):0;
    	self.animate = self.option.animate?self.option.animate:true;
    	self.progressColor = self.option.progressColor?self.option.progressColor:"#0298f7";
    	self.bgColor = self.option.bgColor?self.option.bgColor:"#222";
    	self.radius = self.option.radius?self.option.radius:5;
    	self.label = null;
    	self.height = self.option.height?self.option.height:9;
    	self.progress = null;
    	self.labelColor = self.option.labelColor?self.option.labelColor:"#fff";
    	self.progressLine = null;
    	self.create = function(){
    		$(self.container).html("");
    		if(!self.option.label){
    			self.setLabel();
    		}else{
    			if(self.option.label.show||self.option.label.show=="undefined")
    			self.setLabel();
    		}
    		
    		self.setProgress();
    		fableFactory.setProperty(self);
    		return self;
    	};
    	self.setLabel = function(){
    		self.label = document.createElement("div");
    		$(self.label).css("text-align","center");
    		$(self.label).css("padding-top","3px");
    		$(self.label).css("position","relative");
    		$(self.label).css("z-index",3);
    		$(self.label).css("top",0);
    		$(self.label).css("color",self.labelColor);
    		$(self.label).css("font-size","10px");
    		$(self.label).html(self.data+"%");
    		$(self.container).append(self.label);
    	};
    	self.setProgress = function(){
    		self.progress = document.createElement("div");
    		
    		$(self.progress).css("border-radius",self.radius);
    		$(self.progress).css("width","100%");
    		$(self.progress).css("position","relative");
    		$(self.progress).css("z-index",1);
    		$(self.progress).css("top",-14);
    		//$(self.progress).css("border","1px solid rgba(0,255,255,0.4)");
    		$(self.progress).css("background",self.bgColor);
			$(self.progress).css("padding",0);
			$(self.progress).css("height",self.height);
    		//$(self.progress).css("color",self.option.progress.color?self.option.progress.color:["#0093ca"]);
    		$(self.progress).css("font-size","13px");
    		$(self.container).append(self.progress);
    		self.setProgressLine();
    	};
    	self.animate = function(){
    		if(self.animate){
    			$(self.progressLine).css("width",0);
    			$(self.progressLine).animate(
    				{"width":$(self.progress).width()*(self.data/100)+"%"},
    				1500
    			);
    		}else{
    			$(self.progressLine).css("width",$(self.progress).width()*(self.data/100)+"%");
    		}
    	};
    	self.setProgressLine = function(){
    		self.progressLine = document.createElement("div");
    		$(self.progressLine).css("border-radius",self.radius);
    		if(jQuery.isArray(self.progressColor)){
    			var d =  null;
    			var min = 0;
    			var max = 0;
    			$(self.progressColor).each(function(i,o){
    				if(o.split(":").length>1){
    					min = max;
    					max = o.split(":")[1];
    				}else{
    					min = max;
    					max = Math.round(100/self.progressColor.length*(i+1));
    				}
    				if(self.data>=min&&self.data<max){
    					d = o;
    				}
    			});
    			if(d){
    				$(self.progressLine).css("background",d.split(":")[0]);
    			}
    			
    		}else{
    			$(self.progressLine).css("background",self.progressColor);
    		}
			$(self.progressLine).css("padding",0);
			$(self.progressLine).css("height","100%");
    		$(self.progress).append(self.progressLine);
    		self.animate();
    	};
    	return self;
    };
    /**圆形进度条**/
    fableFactory.roundLoader = function(elementOption){
    	var self = {};
    	self.option = elementOption.option?elementOption.option:null;
    	self.container = elementOption.container?elementOption.container:null;
    	self.time = self.option.time?self.option.time:null;
    	self.showPercentage = self.option.showPercentage?self.option.showPercentage:false;
    	self.element = null;
    	self.value = self.value?self.value:0;
    	self.create = function(){
    		if(self.option&&self.container){
    			require(["radialIndicator"],function(){
    				fableFactory.setProperty(self);
    				self.set();
    			});
    		}
    		return self;
    	};
    	self.set = function(){
    		
    		if(self.time){
    			
    			window.setInterval(function(){
    				if(self.value<100){
    					self.value = self.value+1;
    				}else{
    					self.value = 0;
    					if(self.option.drawCallback){
    						self.option.drawCallback(self);
    					}
    				}
    				self.draw();
    			},self.time/100);
    		}else{
    			self.draw();
    		}
    		
    		
    	};
    	self.draw = function(){
    		
    		$(self.container).html("");
    		
    		self.element = $(self.container).radialIndicator({
    			radius:12,
    			showPercentage:self.showPercentage,
    			displayNumber:false,
    			barWidth:2.5,
    			roundCorner:true,
    			barBgColor:"rgba(0,0,0,0)",
    			barColor:"#20c6fc",
    			shadowColor:"#20c6fc",
    			shadowRadius:5
    		}).data("radialIndicator");
    		self.element.value(self.value);
    	};
    	return self;
    };
    /**echarts组件**/
    fableFactory.echarts = function(option){
		var self = {};
		self.container = null;
		self.option = option.option;
		self.dataOption = null;
		self.parentOption = option.parentOption;
		self.chart = null;
		self.data = null;
		self.echarts = null;
		self.theme = null;
		self.chartEle = null;
		self.option.seriesType =  self.option.seriesType?self.option.seriesType:"map";
		self.element = document.createElement("div");
			$(self.element).addClass("echarts-chart");
			fableFactory.setProperty(self);
			if(fableFactory.isFalse(self.option.show)){
				$(self.element).hide();
			}else{
				$(self.element).show();
			}
			
		
		$(option.container).append(self.element);
		self.mapSelectFuc = function(chart){
			if(self.option.seriesType=="map"){
				chart.on("mapselectchanged",function(param){
					fableFactory.$scope[self.option.mapSelectedCallback](param,self);
				});
			}else{
				chart.on("click",function(param){
					fableFactory.$scope[self.option.mapSelectedCallback](param,self);
				});
			}
			
			
		};
		self.clickFuc = function(chart){
			require(["echartsConfig"],function(ecConfig){
				chart.on("click", function(param){
				
					fableFactory.$scope[self.option.clickCallback](param,self);
				});
			});
			
		};
		self.hoverFuc = function(chart){
			require(["echartsConfig"],function(ecConfig){
				chart.on("hover", function(param){
					fableFactory.$scope[self.option.hoverCallback](param);
				});
			});
		};
		self.create = function(){
			if(self.option.option){
				
				
				self.option.dataOption = fableFactory.val(self.option.option);
				
				if(self.option.setOptionDataCallback){
					fableFactory.$scope[self.option.setOptionDataCallback](self.option);	
				}
				if(self.option.type=="map"){
					
					require(["echarts2.0"],function(){
						
						if(self.option.mapData&&self.option.mapName){
								//self.mapName = self.option.mapName?self.option.mapName:"myMap";
								echarts.util.mapData.params.params[self.option.mapName] = {
								    getGeoJson: function (callback) {
								    	if(typeof(self.option.mapData)=="string"){
								    		$.getJSON(self.option.mapData, function (data) {
								            // 压缩后的地图数据必须使用 decode 函数转换
								           	 callback(echarts.util.mapData.params.decode(data));
								       	 });
								    	}else{
								    		callback(echarts.util.mapData.params.decode(self.option.mapData));
								    	}
								        
								    }
								};
								self.echarts = echarts;
								
								
								self.chart = self.echarts.init(self.element,self.theme);
								
								self.chartEle =self.chart.setOption(self.option.dataOption,true);
								
								$(window).on("resize",function(){
									self.reDraw();
									
								});
								if(self.option.mapSelectedCallback){
									self.mapSelectFuc(self.chart);
								}
								if(self.option.clickCallback){
									self.clickFuc(self.chart);
								}
								if(self.option.hoverCallback){
									self.hoverFuc(self.chart);
								}
						
						}else{
							self.echarts = echarts;
							/*
							if(!self.option.dataOption.series[0].data||self.option.dataOption.series[0].data.length==0){
									$(self.container).attr("id",self.option.id);
									$(self.container).html("<div style='text-align:center;color:#fff;padding:15px 0 0 0;'>无数据</div>");
									return;
								}
							*/
							
							
							self.chart = self.echarts.init(self.element,self.theme);
							self.chartEle = self.chart.setOption(self.option.dataOption,true);
						
							$(window).on("resize",function(){
								self.reDraw();
							});
							if(self.option.mapSelectedCallback){
								self.mapSelectFuc(self.chart);
							}
							if(self.option.clickCallback){
								self.clickFuc(self.chart);
							}
							if(self.option.hoverCallback){
								self.hoverFuc(self.chart);
							}
						}
					});
				}else{
					require(["echarts.min",self.option.theme],function(echarts,theme){
						self.echarts = echarts;
						self.theme = theme;
						
						self.chart = self.echarts.init(self.element,self.theme);
						self.chartEle = self.chart.setOption(self.option.dataOption,true);
					
						$(window).on("resize",function(){
							self.reDraw();
						});
						if(self.option.mapSelectedCallback){
							self.mapSelectFuc(self.chart);
						}
						if(self.option.clickCallback){
							self.clickFuc(self.chart);
						}
						if(self.option.hoverCallback){
							self.hoverFuc(self.chart);
						}
					});
				}

			}

		};
		
		self.setChartOption = function(chartOption){
			self.option.dataOption = chartOption;
		};
		
		self.getChartOption = function(){
			return self.option.dataOption;
		};
		
		self.refresh = function(chartOption){
			self.setChartOption(chartOption);
			self.chart.setOption(self.option.dataOption,true);
		};
		
		self.reDraw = function(){
			/*
			if(!self.option.dataOption.series[0].data||self.option.dataOption.series[0].data.length==0){
				$(self.container).attr("id",self.option.id);
				$(self.container).html("<div style='text-align:center;color:#fff;padding:15px 0 0 0;'>无数据</div>");
				return;
			}
			*/
			
			self.chart = self.echarts.init(self.element,self.theme);
			self.chart.setOption(self.option.dataOption,true); 
			if(self.option.mapSelectedCallback){
				self.mapSelectFuc(self.chart);
			}
			if(self.option.entityId){
				fableFactory.$scope.entities[self.option.entityId] = self;
			}
			
		};
		self.create();
		return self;
	};
	/**判断是否为空**/
	fableFactory.isFalse = function(val){
		var isFalse = true;
		if(val==undefined){
			isFalse = false;
		}else{
			if(val){
				isFalse = false;
			}else{
				isFalse = true;
			}
		}
		return isFalse;
	};
	/**highCharts组件**/
	fableFactory.highCharts = function(elementOption){
		var self = {};
		self.container = elementOption.container?elementOption.container:null;
		self.option = elementOption.option?elementOption.option:null;
		self.chartOption = self.option.chartOption?self.option.chartOption:null;
		self.theme = self.option.theme?self.option.theme:"dark";
		self.element = null;
		self.isMain = self.option.isMain?self.option.isMain:true;
		self.create = function(){
			if(self.container&&self.chartOption){
				if(self.isMain){
					self.element = self.container;
				}else{
					self.element = document.createElement("div");
					$(self.container).append(self.element);
				}
				fableFactory.setProperty(self);
				$(self.element).highcharts(self.chartOption);
			}
			return self;
		}
		return self;
	};
	/**旋转的地图**/
	fableFactory.spreader = function(elementOption){
		var self ={};
		self.container = elementOption.container?elementOption.container:null;
		self.option = elementOption.option?elementOption.option:null;
		self.data = self.option.data?self.option.data:null;
		self.step = self.option.step?self.option.step:0;
		self.mapOption = self.option.mapOption?self.option.mapOption:null;
		self.radius = self.option.radius?self.option.radius:0.05;
		self.offset = self.option.offset?self.option.offset:10;
		self.origin = self.option.origin?self.option.origin:[0,0];
		self.mapData = {"type": "FeatureCollection","features": [],"UTF8Encoding": true};
		self.markLineData = [];
		self.markPointData = [];
		self.geoCoord = {};
		self.originId = null;
		self.element = null;
		self.isMain = self.option.isMain?self.option.isMain:true;
		self.create = function(){
			if(self.data&&self.data.length>0){
				if(self.isMain){
					self.element = self.container;
				}else{
					self.element = document.createElement("div");
					$(self.container).append(self.element);
				}
				self.setData(self.data);
				
				self.draw();
			}
			return self;
		};
		
		self.draw = function(){
			if(self.mapOption){
				var echartEntity = fableFactory.echarts({
					container:self.element,
					option:{
						type:"map",
						style:"width:100%;height:100%",
						option:self.mapOption,
						mapData:self.mapData,
						mapName:"service",
						clickCallback:self.option.clickCallback?self.option.clickCallback:null,
						hoverCallback:self.option.hoverCallback?self.option.hoverCallback:null
					}
				});
			}else{
				fableFactory.console("无地图配置");
			}
			
		};
		self.getPosition = function(x,y,angle,radius){
			if(!x||!y||!angle){return [];}
			var radius = radius?radius:self.radius;
			var x1 = (Number(x) + Math.sin(2*Math.PI/360*angle)*radius);
			var y1 = (Number(y) + Math.cos(2*Math.PI/360*angle)*radius);
			var cp = [x1,y1];
			return cp;
		};
		self.setData = function(data,parentData){
			$(data).each(function(i,o){
				var id = o.id
				var cp = [];
				var angle = 0;
				var radius = 0;
				if(parentData){
					
					if(parentData.cp[0]==self.origin[0]&&parentData.cp[1]==self.origin[1]){
						angle = self.step>0?(360/self.step)*(i+1):(360/data.length)*(i+1);
						radius = self.radius;
					}else{
						var fw = parentData.angle;
						angle = fw/1.5+(10)*(i+1);
						radius = self.radius;
					}
					cp = self.getPosition(parentData.cp[0],parentData.cp[1],angle,radius);
				}else{
					cp = self.origin;
				}
				if(self.originId){
					self.originId = id;
				}
				self.geoCoord[o.name] = cp;
				if(parentData){
					self.markLineData.push([{"name":o.name},{"name":parentData.data.name,"value":o.value}]);
					self.markPointData.push({"name":o.name,"value":o.value});
				}else{
					self.markLineData.push([{"name":o.name},{"name":self.data[0].name,"value":o.value}]);
				}
				
				var item = {
					"type": "Feature",
					"id": id,
					"properties": {
						"name": o.name,
						"cp": cp,
						"childNum": 1
					},
					"geometry": {
						"type": "Polygon",
						"coordinates": [
							"@@DB@DF@@@B@@@@@@@B@B@@@@@@@@@@@D@BAB@@@B@B@@@B@B@BA@@B@B@@BD@@F@B@@@B@@@@@B@B@B@@@B@@@@@@@FD@@CA@@AD@BC@@@A@EB@@CD@@AB@B@R@@@@@@@@@@@@ED@@@@@D@@@@@B@@E@@@BA@@A@@@A@@@A@@@@@@@@@@@A@@@CB@@@@@@@@@@AA@@@@@@@@AF@B@@@B@@@@@@@@@@@@@@AB@@BD@@@@@@@@@A@@@@B@@AB@@@@B@@@@@FBBA@CD@@EAA@@@A@@@@@@B@@@@@@@@@@@BB@@@@B@@@@@@@B@D@@@@@@@@@@@@AB@@@A@@@@A@A@@@@@@@@A@@@AA@@@A@@@A@A@@@@@AA@@A@@@E@@@@@@@A@C@@@@@@@@@@@E@@@@CB@@@@A@@@@@A@AA@@@@@@@@@BA@@@A@@A@@@BE@@IBE@G@C@C@@@@BC@A@@@C@C@A@C@A@A@@B@@@@BB@@@B@B@@@B@B@@@B@B@B@B@B@@@D@@CA@C@@AB@@@@@@@@C@@@A@@@EH@@AD@@@@@@@@AC@@C@@A@A@AB@@@@A@@@A@@AC@@ABG@A@@@@BE@C@A@@@@@AAAAA@@@A@C@@@@@@AA@@@@@@C@KBO@C@@@C@A@@@A@AAA@@@E@@@E@@CD@@@@@@A@@@@@C@@@@B@@AA@@C@@@@@@A@@@A@BGA@@@@@A@@A@@@@@@C@C@CB@@@@C@@@A@@@A@A@@@@@A@@@@@A@@AI@@@B@A@@B@B@B@@@@@@@B@@A@@@@A@@AB@BA@A@@@@@@DE@@@@@@B@@@BEA@@A@A@AAA@A@@@@@A@@B@@@@A@@@@@A@@@@@@@@@B@@B@@@@B@@@@@@@@@B@@D@@@DJ@B@D@@@@@H@NB@B@B@@@L@@@B@@@BAH@J@J@F@@@@@@A@@@A@@@@B@@@@@D@B@JG@@BB@@B@@@@@DA@@@@@@B@@@@B@AD@@@@@B@NJ@@B@@@@@@@@@@@@F@AJ@@@@C@AX@@@@@@AB@@A@@F@N@NMA@F@B"
						],
						"encodeOffsets": [[
								119181,
								40920
							]
						]
					}
				}
				self.mapData.features.push(item);
				if(o.items&&o.items.length>0){
					var cpData = {"cp":cp,"data":o,"angle":angle};
					self.setData(o.items,cpData);
				}
				
			});
			self.mapOption.series[0].geoCoord= self.geoCoord;
			self.mapOption.series[0].markLine.data = self.markLineData;
			self.mapOption.series[0].markPoint.data = self.markPointData;
		};
		return self;
	};
	/**模态窗**/
	fableFactory.modal = function(elementOption){
		if(!elementOption){
			elementOption = {};
		}
		var self = {};
		self.container = elementOption.container?elementOption.container:$("body");
		self.option = elementOption.option?elementOption.option:{};
		self.modalId = self.option.id?self.option.id:"m"+fableFactory.getRandom(1000,9999);
		self.label = self.option.label?self.option.label:"新窗口";
		self.labelColor = self.option.labelColor?self.option.labelColor:"#222";
		self.background = self.option.background?self.option.background:"#fff";
		self.modalLabelId = self.option.id+"label";
		self.style = self.option.style?self.option.style:"";
		self.size = self.option.size?self.option.size:null;
		self.hide = self.option.hide?self.option.hide:false;
		self.center = self.option.center?self.option.center:true;
		self.drag = self.option.drag?self.option.drag:false;
		self.modalEntity = {};
		self.modal = null;
		self.element = null;
		self.modalDialog = null;
		self.modalContent = null;
		self.modalHeader = null;
		self.modalFooter = null;
		
		self.create = function(){
			if($("#"+self.modalId).length>0){
				$("#"+self.modalId).modal("show");
			}else{
				self.createModal();
			}
			return self;
		};
		self.createModal = function(){
			self.modal = document.createElement("div");
			$(self.modal).attr("id",self.modalId);
			$(self.modal).addClass("modal fade");
			
			$(self.container).append(self.modal);
			$(self.modal).attr("role","dialog");
			$(self.modal).attr("tabindex","-1");
			$(self.modal).attr("aria-labelledby",self.modalId+"Label");
			
			if(self.hide){
				$(self.modal).attr("aria-hidden","true");
			}else{
				$(self.modal).attr("aria-hidden","false");
			}
			var entity = {};
			entity.element =  self.modal;
			entity.option = {};
			entity.option.id = self.modalId;
			entity.option.cls = self.option.cls;
			fableFactory.setProperty(entity);
			self.createModalDialog();
			if(!self.hide){
				$(self.modal).modal("show");
			}
		};
		self.createModalDialog = function(){
			self.modalDialog = document.createElement("div");
			$(self.modalDialog).addClass("modal-dialog");
			$(self.modal).append(self.modalDialog);
			if(self.size){
				$(self.modalDialog).css("width",self.size);
			}
			self.createModalContent();
		};
		self.createModalContent = function(){
			self.modalContent = document.createElement("div");
			$(self.modalContent).addClass("modal-content");
			$(self.modalDialog).append(self.modalContent);
			
			self.createModalHeader();
			self.createModalBody();
			self.createModalFooter();
		};
		self.createModalHeader = function(){
			self.modalHeader = document.createElement("div");
			$(self.modalHeader).addClass("modal-header");
			$(self.modalContent).append(self.modalHeader);
			if(self.background){
				$(self.modalHeader).css("background",self.background);
			}
			if(self.drag){
				$(self.modalDialog).draggable({
					handle: ".modal-header",   
				    cursor: 'move',   
				    refreshPositions: false
				});
			}
			fableFactory.render({
				container:self.modalHeader,
				option:{
					"items":[
						{
							"type":"button",
							"cls":"close",
							"attr":"data-dismiss::modal,aria-hidden::true",
							"text":"X"
						},
						{
							"type":"custom",
							"customType":"h4",
							"id":self.modalId+"Label",
							"text":self.label,
							"style":"color:"+self.labelColor,
							"cls":"modal-title"
						}
					]
				}
			})
		};
		self.createModalBody = function(){
			self.element = document.createElement("div");
			$(self.element).addClass("modal-body");
			$(self.modalContent).append(self.element);
			var entity = {};
			entity.element =  self.element;
			entity.option = {};
			entity.option.html = self.option.html;
			fableFactory.setProperty(entity);
		};
		
		self.createModalFooter = function(){
			self.modalFooter = document.createElement("div");
			$(self.modalFooter).addClass("modal-footer");
			$(self.modalContent).append(self.modalFooter);
			if(self.option.buttons&&self.option.buttons.length>0){
				$(self.option.buttons).each(function(i,o){
					self.createModalButton(o);
				});
			}
		};
		self.show = function(){
			$(self.modal).modal("show");
		};
		self.hide = function(){
			$(self.modal).modal("hide");
		};
		self.createModalButton = function(buttonOption){
			switch(buttonOption.type){
				case "button.close":
					buttonOption.text = "关闭";
					buttonOption.clickCallback = function(){
						self.hide();
					};
					fableFactory.button.close({
						container:self.modalFooter,
						option:buttonOption
					}).create();
				break;
				default:
					fableFactory.button({
						container:self.modalFooter,
						option:buttonOption
					}).create();
				break;
			}
		};
		return self;
	};

	/**消息框**/
	fableFactory.msg = function(elementOption){
		if(!elementOption){
			elementOption = {};
		}
		var self = {};
		self.container = elementOption.container?elementOption.container:$("body");
		self.option = elementOption.option?elementOption.option:{};
		self.msgId = self.option.id?self.option.id:"msg"+fableFactory.getRandom(1000,9999);
		self.width = self.option.width?self.option.width:200;
		self.height = self.option.height?self.option.height:150;
		self.background = self.option.background?self.option.background:"#025daa";
		self.msg = null;
		self.msgHeader = null;
		self.msgHeaderLabel = null;
		self.msgHeaderButtonbar = null;
		self.element = null;
		self.minBtn = null;
		self.min = self.option.min?self.option.min:true;
		self.closeBtn = null;
		self.animate = self.option.animate?self.option.animate:"slide";
		self.drag = self.option.drag?self.option.drag:false;
		self.position = self.option.position?self.option.position:"right";
		self.create = function(){
			var entity = self.findEntity();
			if(!entity){
				
				self.createMsg();
			}
			
			return self;	
		};
		self.findEntity = function(){
			return fableFactory.msgGroup[self.msgId]?fableFactory.msgGroup[self.msgId]:null;
		};
		self.createMsg = function(){
			self.msg = document.createElement("div");
			$(self.msg).addClass("msg-box");
			$(self.msg).css("width",self.width);
			if(self.position=="left"){
				$(self.msg).css("left",1);
			}else{
				$(self.msg).css("right",1);
			}
			$(self.msg).css("bottom",self.height);
			$(self.container).append(self.msg);
			var entity = {};
			entity.element =  self.msg;
			entity.option = {};
			entity.option.id = self.msgId;
			entity.option.style = self.option.style;
			entity.option.cls = self.option.cls;
			fableFactory.setProperty(entity);
			self.createMsgHeader();
			self.createMsgContent();
			fableFactory.msgGroup[self.msgId] = self;
			self.show();
		};
		self.createMsgHeader = function(){
			self.msgHeader = document.createElement("div");
			$(self.msgHeader).addClass("msg-header");
			$(self.msgHeader).attr("onselectstart","return false");
			$(self.msgHeader).css("-moz-user-select","none");
			$(self.msg).append(self.msgHeader);
			if(self.drag){
				$(self.msg).draggable({
					handle: ".msg-header",   
				    cursor: 'move',   
				    refreshPositions: false
				});
			}
			if(self.option.labelColor){
				$(self.msgHeader).css("color",self.option.labelColor);
			}
			$(self.msgHeader).css("background",self.background);
			$(self.msgHeader).on("dblclick",function(){
				if($(this).find("i").hasClass("ion-ios-minus-empty")){
					self.hideContent($(this));
				}else{
					self.showContent($(this));
				}
			});
			self.createMsgHeaderLabel();
			self.createMsgHeaderButton();
		};
		self.createMsgHeaderLabel = function(){
			self.msgHeaderLabel = document.createElement("div");
			$(self.msgHeaderLabel).addClass("msg-header-label");
			if(self.option.label){
				$(self.msgHeaderLabel).html(self.option.label);
			}
			
			$(self.msgHeader).append(self.msgHeaderLabel);
		};
		self.createMsgHeaderButton = function(){
			self.msgHeaderButtonbar = document.createElement("div");
			$(self.msgHeaderButtonbar).addClass("msg-header-buttonbar");
			if(self.min){
				self.minBtn = document.createElement("button");
				
				$(self.minBtn).html("<i class='ion-ios-minus-empty'></i>");
				$(self.msgHeaderButtonbar).append(self.minBtn);
				$(self.minBtn).on("click",function(){
					if($(this).find("i").hasClass("ion-ios-minus-empty")){
						self.hideContent($(this));
					}else{
						self.showContent($(this));
					}
				});
			}
			self.closeBtn = document.createElement("button");
			$(self.closeBtn).html("<i class='ion-android-close'></i>");
			$(self.msgHeaderButtonbar).append(self.closeBtn);
			$(self.msgHeader).append(self.msgHeaderButtonbar);
			$(self.closeBtn).on("click",function(){
				self.close();
			});
		};
		self.createMsgContent = function(){
			self.element = document.createElement("div");
			$(self.element).addClass("msg-body");
			var entity = {};
			entity.element =  self.element;
			entity.option = {};
			entity.option.html = self.option.html;
			fableFactory.setProperty(entity);
			$(self.element).css("height",self.height-30);
			$(self.msg).append(self.element);
		};
		self.show = function(){
			var entity = self.findEntity();
			if(entity){
				$(entity.msg).show();
				entity.animateHandle(entity,"up");
			}
		};
		
		self.animateHandle = function(entity,type){
			if(entity.animate){
				
				switch(entity.animate){
					case "fade":
						
						if(type=="up"){
							 $(entity.msg).css("opacity",0);
							 $(entity.msg).css("bottom",-entity.height);
							 $(entity.msg).animate({opacity:1,bottom:0},1000);
						}else{
							 $(entity.msg).css("opacity",1);
							 $(entity.msg).animate({opacity:0,bottom:-entity.height},1000);
						}
					break;
					
					case "slide":
						if(type=="up"){
							 $(entity.msg).css("bottom",-entity.height);
							 $(entity.msg).animate({bottom:0},500);
						}else{
							 $(entity.msg).animate({bottom:-entity.height},500);
						}
					break;
				}
			}
			
		};
		
		self.showContent = function(){
			var entity = self.findEntity();
			if(entity){
				$(entity.msg).css("height","auto");
				$(entity.element).show();
				$(entity.minBtn).find("i").attr("class","ion-ios-minus-empty");
			}
			
		};
		self.hideContent = function(){
			var entity = self.findEntity();
			if(entity){
				$(entity.msg).css("height","auto");
				$(entity.element).hide();
				$(entity.minBtn).find("i").attr("class","ion-android-checkbox-outline-blank");
			}
		};
		self.close = function(){
			var entity = self.findEntity();
			if(entity){
				$(entity.msg).remove();
				delete fableFactory.msgGroup[self.msgId];
			}
			
			
		};
		return self;
	};
	/**滚动菜单栏**/
	fableFactory.bannerMenuBar = function(elementOption){
		if(!elementOption){
			elementOption = {};
		}
		var self = {};
		self.container = elementOption.container?elementOption.container:$("body");
		self.option = elementOption.option?elementOption.option:{};
		self.bannerMenuBarwidth = self.option.bannerMenuBarwidth?self.option.bannerMenuBarwidth:"100%";
		self.hasButtonBar = self.option.buttonBar?self.option.buttonBar:true;
		self.itemSize = self.option.itemSize?self.option.itemSize:50;
		self.arrowSize = self.option.arrowSize?self.option.arrowSize:20;
		self.itemPageNumber = self.option.itemPageNumber?self.option.itemPageNumber:6;
		self.theme = self.option.theme?self.option.theme:"default";
		self.element = null;
		self.buttonBar = null;
		self.leftBar = null;
		self.rightBar = null;
		self.menuBar = null;
		self.itemBar = null;
		self.saveCallback = self.option.saveCallback?self.option.saveCallback:null;
		self.create = function(){
			self.createBannerMenuBar();
			return self;
		};
		self.close = function(){
			$(self.element).hide();
		};
		self.show = function(){
			$(self.element).show();
		};
		self.createBannerMenuBar = function(){
			self.element = document.createElement("div");
			$(self.element).addClass("bannerMenuBar_"+self.theme);
			$(self.element).css("width",self.bannerMenuBarwidth);
			fableFactory.setProperty(self);
			$(self.container).append(self.element);
			self.createBannerMenuBar_ButtonBar();
			self.createBannerMenuBar_MenuBar();
		};
		self.createBannerMenuBar_ButtonBar = function(){
			if(self.hasButtonBar){
				self.buttonBar = document.createElement("div");
				$(self.buttonBar).addClass("bannerMenuBar_"+self.theme+"_buttonBar");
				$(self.element).append(self.buttonBar);
				/**保存按钮**/
				fableFactory.button({
					container:$(self.buttonBar),
					option:{
						text:"保存",
						clickCallback:self.saveCallback
					}
				}).create();
				
				/**取消按钮**/
				fableFactory.button({
					container:$(self.buttonBar),
					option:{
						text:"取消",
						clickCallback:function(){
							self.close();
						}
					}
				}).create();
			}
		};
		
		self.createBannerMenuBar_ButtonBar_Button = function(){
			
		};
		self.createBannerMenuBar_MenuBar = function(){
			
		};
		self.createBannerMenuBar_MenuBar_LeftBar = function(){
			
		};
		self.createBannerMenuBar_MenuBar_RightBar = function(){
			
		};
		self.createBannerMenuBar_MenuBar_ItemBar = function(){
			
		};
		self.createBannerMenuBar_MenuBar_ItemBar_Item = function(){
			
		};
		return self;
	};
	
	fableFactory.loadPage = function(option){
		if(!option){
			option = {};
		}
		var self = {};
		self.container = option.container?option.container:$("body");
		fableFactory.remote({
			dataType:"text",
			url:option.url,
			successCallback:function(data){
				$(self.container).html(data);
				if(option.successCallback){
					fableFactory.call(option.successCallback,data);
				}
			},
			errorCallback:function(){
				$(self.container).html("<div style='width:100%;height:100%;position:relative;'><div style=' position:absolute; top:50%;left:50%;'><div style='position:relative; top:-50%;left:-50%;'><i class='fa fa-exclamation-triangle fa-1-big' style='margin-right:5px;color:red'></i>加载出错！</div></div></div>");
			},
			beforeCallback:function(){
				$(self.container).html("<div style='width:100%;height:100%;position:relative;'><div style=' position:absolute; top:50%;left:50%;'><div style='position:relative; top:-50%;left:-50%;'><i class='fa fa-spinner fa-pulse fa-1-big fa-fw margin-bottom' style='margin-right:5px;'></i>页面加载中...</div></div></div>");
			}
		})
	};
	fableFactory.treeMenu = function(elementOption){
		if(!elementOption){
			elementOption = {};
		}
		var self = {};
		self.container = elementOption.container?elementOption.container:$("body");
		self.option = elementOption.option?elementOption.option:{};
		self.element = null;
		self.data = self.option.data?self.option.data:null;
		self.create = function(){
			self.createMenu(self.container,self.data);
			return self;
		};
		self.createMenu = function(parentNode,data){
			if(data.length>0){
				var ul = document.createElement("ul");
				$(data).each(function(i,o){
					var li = document.createElement("li");
					if(o.icon){
						var icon = document.createElement("i");
						$(icon).addClass(o.icon);
						$(li).append(icon);
					}
					if(o.text){
						var text = document.createElement("span");
						$(text).html(o.text);
						$(li).append(text);
					}
					if(o.items&&o.items.length>0){
						var arrow = document.createElement("em");
						$(arrow).addClass("ion-ios-arrow-forward");
						$(li).append(arrow);
						self.createMenu(li,o.items);
					}
				});
			}
			
		};
		return self;
	};
	fableFactory.topo = function(elementOption){
		if(!elementOption){
			elementOption = {};
		}
		var self = {};
		self.containerId = elementOption.containerId?elementOption.containerId:null;
		self.option = elementOption.option?elementOption.option:{};
		self.element = null;
		self.nodeGroup = {};
		self.edgeGroup = {};
		self.create = function(){
			
			if(self.containerId){
				self.element = new Q.Graph(self.containerId);
				console.log(window.Q)
			}
			return self;

			
		};
		self.createNode = function(nodeOption){
			if(nodeOption.name){
				if(self.element){
					self.nodeGroup[nodeOption.name] = self.element.createNode(nodeOption.label, nodeOption.x, nodeOption.y);
					if(nodeOption.image){
						self.nodeGroup[nodeOption.name].image = nodeOption.image;
					}
				}
			}
		};
		self.createEdge = function(edgeOption){
			if(edgeOption.name){
				if(edgeOption.nodeName1&&edgeOption.nodeName2){
					self.edgeGroup[edgeOption.name] = graph.createEdge(edgeOption.label, self.nodeGroup[edgeOption.nodeName1], self.nodeGroup[edgeOption.nodeName2]);
				}
			}
		};
		return self;
	};
	return fableFactory;
});