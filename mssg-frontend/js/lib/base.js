define(["jquery", "bootstrap"],
function() {
	var base = {};
	base.$scope = {};
	base.IE8 = function() {
		if (!$.support.opacity) {
			return true
		} else {
			return false
		}
	};
	base.IE = function() {
		if (!$.support.opacity && !$.support.style && window.XMLHttpRequest == undefined) {
			return 6
		} else {
			if (!$.support.opacity && !$.support.style && window.window.XMLHttpRequest != undefined) {
				return 7
			} else {
				if (!$.support.opacity) {
					return 8
				} else {
					return 9
				}
			}
		}
	} ();
	base.isIE = function() {
		if (document.all) {
			return true
		} else {
			return false
		}
	} ();
	base.setProperty = function(e, t) {
		if (t) {
			if (t.cls) {
				$(e).addClass(t.cls.replace(/,/g, " "))
			}
			if (t.style) {
				$(e).attr("style", t.style.replace(/,/g, ";"))
			}
			if (t.id) {
				$(e).attr("id", self.id)
			}
			if (t.name) {
				$(e).attr("name", self.name)
			}
		}
	};
	base.getRandom = function(e, t) {
		return Math.round(Math.random() * (t - e) + e)
	};
	base.ajax = function(e, t) {
		var a = {};
		t = t ? t: true;
		if (e) {
			a.params = e.params ? e.params: {};
			a.url = e.url ? e.url: "";
			a.async = e.async ? e.async: true;
			a.type = e.type ? e.type: "post";
			a.dataType = e.dataType ? e.dataType: "json";
			a.success = e.success ? e.success: null;
			a.error = e.error ? e.error: null;
			a.beforeSend = e.beforeSend ? e.beforeSend: null;
			a.complete = e.complete ? e.complete: null;
			a.timeout = e.timeout ? e.timeout: -1;
			a.ajaxObj = null;
			a.isConnect = a.isConnect ? a.isConnect: true;
			if (a.dataType == "text") {
				a.type = "get"
			}
			a.connect = function() {
				a.ajaxObj = $.ajax({
					type: a.type,
					async: a.async,
					url: a.url,
					contentType: function() {
						if (a.type.toLowerCase() == "post") {
							return "application/json; charset=utf-8"
						} else {
							return null
						}
					} (),
					data: function() {
						if (a.type.toLowerCase() == "post") {
							return JSON.stringify(a.params)
						} else {
							return a.params
						}
					} (),
					dataType: a.dataType,
					timeout: a.timeout,
					success: function(e) {
						if (a.success) {
							a.success(e)
						}
					},
					error: function(e, t, r) {
						if (a.error) {
							a.error()
						}
					},
					beforeSend: function(e) {
						if (a.beforeSend) {
							a.beforeSend()
						}
					},
					complete: function(e, t) {
						if (a.complete) {
							a.complete()
						}
					}
				});
				return a
			};
			a.stop = function() {
				a.ajaxObj.abort()
			};
			a.run = function() {
				a.connect()
			};
			if (t) {
				if (a.url) {
					a.connect()
				}
			}
			return a
		}
	};
	base.ajax.load = function(e) {
		var t = base.ajax(e, false);
		t.dataType = "text";
		t.type = "get";
		t.errorText = e.errorText ? e.errorText: "资源加载出错,请联系管理员!";
		t.container = e.container ? e.container: null;
		t.error = function() {
			if (t.container) {
				base.bulletin(t.container, t.errorText)
			}
		};
		t.beforeSend = function() {
			if (t.container) {
				base.loading($(t.container))
			}
		};
		if (!t.url) {
			base.bulletin(t.container, t.errorText)
		} else {
			t.connect()
		}
		return t
	};
	base.bulletin = function(e, t) {
		$(e).html("<div style='display:table;width:100%;height:100%;text-align:center;'><div class='theme-bulletin'>" + t + "</div></div>")
	};
	base.loading = function(e) {
		$(e).html("<div style='display:table;width:100%;height:100%;text-align:center;'>" + "<i class='fa fa-spinner fa-pulse fa-3x fa-fw' style='font-size:24px;text-align:center;display: table-cell;vertical-align: middle;width:24px;height:24px;color:#aaa'></i></div>")
	};
	base.template = function(e) {
		var t = {};
		t.data = e.data ? e.data: null;
		t.container = e.container ? e.container: null;
		t.templateId = e.templateId ? e.templateId: null;
		t.callback = e.callback ? e.callback: null;
		t.loading = e.loading ? e.loading: false;
		t.helper = e.helper ? e.helper: null;
		if (t.data && t.container && t.templateId) {
			if (t.loading) {
				base.loading(t.container)
			}
			require(["template"],
			function(e) {
				var a = e.compile($("#" + t.templateId).html());
				e.registerHelper("null",
				function(e, t) {
					if (!e || e == "") {
						return t.fn(this)
					} else {
						return t.inverse(this)
					}
				});
				e.registerHelper("ne",
				function(e, t, a) {
					if (e != t) {
						return a.fn(this)
					} else {
						return a.inverse(this)
					}
				});
				e.registerHelper("e",
				function(e, t, a) {
					if (e == t) {
						return a.fn(this)
					} else {
						return a.inverse(this)
					}
				});
				e.registerHelper("g",
				function(e, t, a) {
					if (e > t) {
						return a.fn(this)
					} else {
						return a.inverse(this)
					}
				});
				e.registerHelper("ge",
				function(e, t, a) {
					if (e >= t) {
						return a.fn(this)
					} else {
						return a.inverse(this)
					}
				});
				e.registerHelper("le",
				function(e, t, a) {
					if (e <= t) {
						return a.fn(this)
					} else {
						return a.inverse(this)
					}
				});
				e.registerHelper("l",
				function(e, t, a) {
					if (e < t) {
						return a.fn(this)
					} else {
						return a.inverse(this)
					}
				});
				if (t.helper && t.helper.length > 0) {
					$(t.helper).each(function(t, a) {
						e.registerHelper(a.name, a.event)
					})
				}
				var r = a(t.data);
				$(t.container).html(r);
				if (t.callback) {
					t.callback(t)
				}
			})
		}
	};
	base.modal = function(option) {
		if(!option) {
			option = {};
		}
		var self = {};
		self.container = option.container ? option.container : $("body");
		self.modalId = option.id ? option.id : "m" + base.getRandom(1000, 9999);
		self.label = option.label ? option.label : "新窗口";
		//self.labelColor = option.labelColor?option.labelColor:"#222";
		self.background = option.background ? option.background : "#fff";
		self.modalLabelId = option.id + "label";
		self.style = option.style ? option.style : "";
		self.width = option.width ? option.width : null;
		self.visible = option.visible ? option.visible : false;
		self.center = option.center ? option.center : true;
		self.drag = option.drag ? option.drag : false;
		self.url = option.url ? option.url : null;
		self.height = option.height ? option.height : null;
		self.radius = option.radius ? option.radius : 5;
		self.contentStyle = option.contentStyle ? option.contentStyle : "";
		self.labelStyle = option.labelStyle ? option.labelStyle : "";
		self.modalEntity = {};
		self.modal = null;
		self.modalBody = null;
		self.modalDialog = null;
		self.modalContent = null;
		self.modalHeader = null;
		self.modalFooter = null;
		self.modalOption = option.modalOption?option.modalOption:{};
		self.callback = option.callback?option.callback:null;
		self.customScroll = option.customScroll ? option.customScroll : true;
		self.context = option.context ? option.context : "";
		self.drag = option.drag ? option.drag : false;
		self.rebuilding = option.rebuilding ? option.rebuilding : false;

		self.create = function() {
			if($("#" + self.modalId).length > 0) {
				if(self.rebuilding) {
					$("#" + self.modalId).remove();
					self.createModal();
				} else {
					self.modal = $("#" + self.modalId);
					self.setModal();
					self.modalDialog = $("#" + self.modalId).find(".modal-dialog");
					self.setModalDialog();
					self.modalContent = $("#" + self.modalId).find(".modal-content");
					self.setModalContent();
					self.modalHeader = $("#" + self.modalId).find(".modal-header");
					self.setModalHeader();
					self.modalBody = $("#" + self.modalId).find(".modal-body");
					self.setModalBody();
					self.modalFooter = $("#" + self.modalId).find(".modal-footer");
					self.setModalFooter();

				}
			} else {
				self.createModal();

			}
			if(!self.visible) {
				self.show();
			}
		};

		self.createModal = function() {
			self.modal = document.createElement("div");
			$(self.container).append(self.modal);
			self.setModal();
			self.modalDialog = document.createElement("div");
			$(self.modal).append(self.modalDialog);
			self.setModalDialog();
			self.modalContent = document.createElement("div");
			$(self.modalDialog).append(self.modalContent);
			self.setModalContent();
			self.modalHeader = document.createElement("div");
			$(self.modalContent).append(self.modalHeader);
			self.setModalHeader();
			self.modalBody = document.createElement("div");
			$(self.modalContent).append(self.modalBody);
			self.setModalBody();
			self.modalFooter = document.createElement("div");
			$(self.modalContent).append(self.modalFooter);
			self.setModalFooter();
		};
		self.setModal = function() {
			$(self.modal).attr("id", self.modalId);
			$(self.modal).attr("class", "modal fade");

			$(self.modal).attr("role", "dialog");
			$(self.modal).attr("tabindex", "-1");
			$(self.modal).attr("aria-labelledby", self.modalId + "Label");
			if(self.visible) {
				$(self.modal).attr("aria-hidden", "true");
			} else {
				$(self.modal).attr("aria-hidden", "false");
			}
			if(self.cls) {
				$(self.modal).attr("class", option.cls.replace(/,/g, " "));
			}
			if(self.style) {
				$(self.modal).attr("style", option.style.replace(/,/g, ";"));
			}

		};
		self.setModalDialog = function() {
			$(self.modalDialog).attr("class", "modal-dialog");
			if(self.width) {
				$(self.modalDialog).css("width", self.width);
			}
			if(self.background) {
				$(self.modalDialog).css("background", self.background);
			}
			$(self.modalDialog).css("border-radius", self.radius);
		};
		self.setModalContent = function() {
			$(self.modalContent).attr("class", "modal-content");
			//$(self.modalContent).css("background-color","transparent");
			$(self.modalContent).css("border", "1px solid #ccc");
			$(self.modalContent).css("border-radius", self.radius);
		};
		self.setModalHeader = function() {
			$(self.modalHeader).attr("class", "modal-header");

			if(self.drag) {
				require(["jqueryUI"],function(){
					$(self.modalDialog).draggable({
						handle: ".modal-header",
						cursor: 'move',
						refreshPositions: false
					});
					$(self.modalHeader).css("cursor","move");
				});
				
			}
			$(self.modalHeader).html("<button class='close' data-dismiss='modal' aria-hidden='true'><i class='fa fa-remove' style='font-weight:normal;font-size:16px;'></i></button><h4 class='modal-title'  id='" + self.modalId + "Label'>" + self.label + "</h4>");
			if(self.labelStyle){
				$(self.modalHeader).find("h4").attr("style", self.labelStyle.replace(/,/g, ";"));
			}
		};
		self.setModalBody = function() {
			$(self.modalBody).attr("class", "modal-body");
			if(self.height && self.height > 0) {
				$(self.modalBody).css("height", self.height);
				$(self.modalBody).css("overflow", "auto");
			}
			if(self.contentStyle){
				$(self.modalBody).attr("style", self.contentStyle.replace(/,/g, ";"));
			}

		};

		self.setModalFooter = function() {
			$(self.modalFooter).attr("class", "modal-footer");

			$(self.modalFooter).html("");
			if(option.buttons && option.buttons.length > 0) {
				$(option.buttons).each(function(i, o) {
					o.container = $(self.modalFooter);
					base.form.button(o);
				});
			}
			/*base.form.button({
				container: $(self.modalFooter),
				label: "关闭",
				cls:"btn btn-default",
				clickEvent: function() {
					self.hide();
				}
			});*/
		};

		self.show = function() {
			$(self.modal).on('show.bs.modal', function() {
				self.loadContext();
				if(self.showEvent) {
					self.showEvent();
				}
			});
			$(self.modal).on('shown.bs.modal', function() {
				
				if(self.shownEvent) {
					self.shownEvent();
				}
			});
			$(self.modal).on('hidden.bs.modal', function() {
				$(self.modal).remove();
			});
			self.modalOption.show = true;
			self.modalOption.center = self.center;
			$(self.modal).modal(self.modalOption);

		};
		self.hide = function() {
			$(self.modal).on('hide.bs.modal', function() {
				if(self.hideEvent) {
					self.hideEvent();
				}
			})

			$(self.modal).on('hidden.bs.modal', function() {
				if(self.hiddenEvent) {
					self.hiddenEvent();
				}
				$(self.modal).remove();
			});
			$(self.modal).modal("hide");
		};

		self.loadContext = function(opt) {
			if(!opt){
				opt = option;
			}
			$(self.modalBody).html("");

			if(opt.context) {
				$(self.modalBody).html(opt.context);
				if(self.callback){
					self.callback(self);
				}
			} else if(opt.url) {
				base.ajax({
					url: opt.url,
					dataType: "text",
					success: function(text) {
						$(self.modalBody).html(text);
						if(self.callback){
							self.callback(self);
						}
						if(self.customScroll) {
							base.scroll({
								container:$(self.modalBody)
							});
						}

					},
					beforeSend: function() {
						base.loading(self.modalBody);
					}

				});
			}
		};
		self.create();
		return self;
	};
	base.glass = function(e) {
		if (base.isIE) {
			if (base.IE <= 8) {
				return
			}
		}
		var t = {};
		t.radius = e.radius ? e.radius: 15;
		t.element = e.element ? e.element: null;
		t.canvas = null;
		t.img = e.img ? e.img: null;
		var a = new Image;
		$(a).attr("src", $(t.img).attr("src"));
		t.fixHeight = e.fixHeight ? e.fixHeight: 0;
		t.width = a.width ? a.width: $(t.element).width();
		t.height = a.height ? a.height: $(t.element).height();
		t.top = e.top ? e.top: $(t.element).offset().top;
		t.left = e.left ? e.left: $(t.element).offset().left;
		t.x = e.x ? e.x: 0;
		t.y = e.y ? e.y: 0;
		t.cls = e.cls ? e.cls: null;
		t.Mu = [512, 512, 456, 512, 328, 456, 335, 512, 405, 328, 271, 456, 388, 335, 292, 512, 454, 405, 364, 328, 298, 271, 496, 456, 420, 388, 360, 335, 312, 292, 273, 512, 482, 454, 428, 405, 383, 364, 345, 328, 312, 298, 284, 271, 259, 496, 475, 456, 437, 420, 404, 388, 374, 360, 347, 335, 323, 312, 302, 292, 282, 273, 265, 512, 497, 482, 468, 454, 441, 428, 417, 405, 394, 383, 373, 364, 354, 345, 337, 328, 320, 312, 305, 298, 291, 284, 278, 271, 265, 259, 507, 496, 485, 475, 465, 456, 446, 437, 428, 420, 412, 404, 396, 388, 381, 374, 367, 360, 354, 347, 341, 335, 329, 323, 318, 312, 307, 302, 297, 292, 287, 282, 278, 273, 269, 265, 261, 512, 505, 497, 489, 482, 475, 468, 461, 454, 447, 441, 435, 428, 422, 417, 411, 405, 399, 394, 389, 383, 378, 373, 368, 364, 359, 354, 350, 345, 341, 337, 332, 328, 324, 320, 316, 312, 309, 305, 301, 298, 294, 291, 287, 284, 281, 278, 274, 271, 268, 265, 262, 259, 257, 507, 501, 496, 491, 485, 480, 475, 470, 465, 460, 456, 451, 446, 442, 437, 433, 428, 424, 420, 416, 412, 408, 404, 400, 396, 392, 388, 385, 381, 377, 374, 370, 367, 363, 360, 357, 354, 350, 347, 344, 341, 338, 335, 332, 329, 326, 323, 320, 318, 315, 312, 310, 307, 304, 302, 299, 297, 294, 292, 289, 287, 285, 282, 280, 278, 275, 273, 271, 269, 267, 265, 263, 261, 259];
		t.Sh = [9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16, 16, 16, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 18, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24];
		t.BlurStack = function() {
			this.r = 0;
			this.g = 0;
			this.b = 0;
			this.a = 0;
			this.next = null
		};
		t.draw = function() {
			var e = $(t.element).width();
			var a = $(t.element).height();
			$(t.canvas).css("width", e);
			$(t.canvas).css("height", a);
			$(t.canvas).css("left", t.left);
			$(t.canvas).css("top", t.top);
			$(t.canvas).attr("width", e);
			$(t.canvas).attr("height", a);
			$(t.canvas).css("position", "absolute");
			if (t.cls) {
				$(t.canvas).addClass(t.cls)
			}
			var r = 0;
			if ($(t.element).css("z-index")) {
				r = Number($(t.element).css("z-index").split("px")[0])
			}
			$(t.canvas).css("zIndex", r + 1);
			var n = t.canvas.getContext("2d");
			n.clearRect(0, 0, e, a);
			var o = 0;
			if ($(window).height() < document.documentElement.scrollHeight) {
				o = a + Math.floor(window.screen.availHeight / t.height * a) + 10
			} else {
				o = a + Math.floor(window.screen.availHeight / t.height * a)
			}
			var l = t.width;
			n.drawImage($(t.img)[0], t.left, t.top, l, o, t.x, t.y, e, a);
			t.stackBlurCanvasRGBA(0, 0, e, a)
		};
		t.create = function() {
			t.canvas = document.createElement("canvas");
			$(t.element).parent().append(t.canvas);
			t.draw()
		};
		t.stackBlurCanvasRGBA = function(e, a, r, n) {
			var o = t.canvas.getContext("2d");
			var l;
			try {
				try {
					l = o.getImageData(e, a, r, n)
				} catch(t) {
					try {
						netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserRead");
						l = o.getImageData(e, a, r, n)
					} catch(e) {
						throw new Error("unable to access local image data: " + e);
						return
					}
				}
			} catch(e) {
				throw new Error("unable to access image data: " + e)
			}
			var i = l.data;
			var s, c, d, u, f, p, m, b, h, g, $, v, y, w, C, k, A, I, x, S, E, B, P, O;
			var N = t.radius + t.radius + 1;
			var D = r << 2;
			var T = r - 1;
			var M = n - 1;
			var L = t.radius + 1;
			var j = L * (L + 1) / 2;
			var z = new BlurStack;
			var F = z;
			for (d = 1; d < N; d++) {
				F = F.next = new BlurStack;
				if (d == L) var G = F
			}
			F.next = z;
			var H = null;
			var R = null;
			m = p = 0;
			var W = mul_table[t.radius];
			var q = shg_table[t.radius];
			for (c = 0; c < n; c++) {
				k = A = I = x = b = h = g = $ = 0;
				v = L * (S = i[p]);
				y = L * (E = i[p + 1]);
				w = L * (B = i[p + 2]);
				C = L * (P = i[p + 3]);
				b += j * S;
				h += j * E;
				g += j * B;
				$ += j * P;
				F = z;
				for (d = 0; d < L; d++) {
					F.r = S;
					F.g = E;
					F.b = B;
					F.a = P;
					F = F.next
				}
				for (d = 1; d < L; d++) {
					u = p + ((T < d ? T: d) << 2);
					b += (F.r = S = i[u]) * (O = L - d);
					h += (F.g = E = i[u + 1]) * O;
					g += (F.b = B = i[u + 2]) * O;
					$ += (F.a = P = i[u + 3]) * O;
					k += S;
					A += E;
					I += B;
					x += P;
					F = F.next
				}
				H = z;
				R = G;
				for (s = 0; s < r; s++) {
					i[p + 3] = P = $ * W >> q;
					if (P != 0) {
						P = 255 / P;
						i[p] = (b * W >> q) * P;
						i[p + 1] = (h * W >> q) * P;
						i[p + 2] = (g * W >> q) * P
					} else {
						i[p] = i[p + 1] = i[p + 2] = 0
					}
					b -= v;
					h -= y;
					g -= w;
					$ -= C;
					v -= H.r;
					y -= H.g;
					w -= H.b;
					C -= H.a;
					u = m + ((u = s + t.radius + 1) < T ? u: T) << 2;
					k += H.r = i[u];
					A += H.g = i[u + 1];
					I += H.b = i[u + 2];
					x += H.a = i[u + 3];
					b += k;
					h += A;
					g += I;
					$ += x;
					H = H.next;
					v += S = R.r;
					y += E = R.g;
					w += B = R.b;
					C += P = R.a;
					k -= S;
					A -= E;
					I -= B;
					x -= P;
					R = R.next;
					p += 4
				}
				m += r
			}
			for (s = 0; s < r; s++) {
				A = I = x = k = h = g = $ = b = 0;
				p = s << 2;
				v = L * (S = i[p]);
				y = L * (E = i[p + 1]);
				w = L * (B = i[p + 2]);
				C = L * (P = i[p + 3]);
				b += j * S;
				h += j * E;
				g += j * B;
				$ += j * P;
				F = z;
				for (d = 0; d < L; d++) {
					F.r = S;
					F.g = E;
					F.b = B;
					F.a = P;
					F = F.next
				}
				f = r;
				for (d = 1; d <= t.radius; d++) {
					p = f + s << 2;
					b += (F.r = S = i[p]) * (O = L - d);
					h += (F.g = E = i[p + 1]) * O;
					g += (F.b = B = i[p + 2]) * O;
					$ += (F.a = P = i[p + 3]) * O;
					k += S;
					A += E;
					I += B;
					x += P;
					F = F.next;
					if (d < M) {
						f += r
					}
				}
				p = s;
				H = z;
				R = G;
				for (c = 0; c < n; c++) {
					u = p << 2;
					i[u + 3] = P = $ * W >> q;
					if (P > 0) {
						P = 255 / P;
						i[u] = (b * W >> q) * P;
						i[u + 1] = (h * W >> q) * P;
						i[u + 2] = (g * W >> q) * P
					} else {
						i[u] = i[u + 1] = i[u + 2] = 0
					}
					b -= v;
					h -= y;
					g -= w;
					$ -= C;
					v -= H.r;
					y -= H.g;
					w -= H.b;
					C -= H.a;
					u = s + ((u = c + L) < M ? u: M) * r << 2;
					b += k += H.r = i[u];
					h += A += H.g = i[u + 1];
					g += I += H.b = i[u + 2];
					$ += x += H.a = i[u + 3];
					H = H.next;
					v += S = R.r;
					y += E = R.g;
					w += B = R.b;
					C += P = R.a;
					k -= S;
					A -= E;
					I -= B;
					x -= P;
					R = R.next;
					p += r
				}
			}
			o.putImageData(l, e, a)
		};
		t.create();
		return t
	};
	base.spreader = function(e) {
		var t = {};
		t.container = e.container ? e.container: null;
		t.data = e.data ? e.data: null;
		t.step = e.step ? e.step: 0;
		t.mapOption = e.mapOption ? e.mapOption: null;
		t.radius = e.radius ? e.radius: .05;
		t.offset = e.offset ? e.offset: 10;
		t.origin = e.origin ? e.origin: [0, 0];
		t.mapData = {
			type: "FeatureCollection",
			features: [],
			UTF8Encoding: true
		};
		t.markLineData = [];
		t.markPointData = [];
		t.geoCoord = {};
		t.originId = null;
		t.element = null;
		t.isMain = e.isMain ? e.isMain: true;
		t.create = function() {
			if (t.data && t.data.length > 0) {
				if (t.isMain) {
					t.element = t.container
				} else {
					t.element = document.createElement("div");
					$(t.container).append(t.element)
				}
				t.setData(t.data);
				t.draw()
			}
			return t
		};
		t.draw = function() {
			if (t.mapOption) {
				var a = base.echartsOld({
					container: t.element,
					type: "map",
					style: "width:100%;height:100%",
					option: t.mapOption,
					mapData: t.mapData,
					mapName: "service",
					clickEvent: e.clickEvent ? e.clickEvent: null,
					hoverEvent: e.hoverEvent ? e.hoverEvent: null
				})
			}
		};
		t.getPosition = function(e, a, r, n) {
			if (!e || !a || !r) {
				return []
			}
			var n = n ? n: t.radius;
			var o = Number(e) + Math.sin(2 * Math.PI / 360 * r) * n;
			var l = Number(a) + Math.cos(2 * Math.PI / 360 * r) * n;
			var i = [o, l];
			return i
		};
		t.setData = function(e, a) {
			$(e).each(function(r, n) {
				var o = n.id;
				var l = [];
				var i = 0;
				var s = 0;
				if (a) {
					if (a.cp[0] == t.origin[0] && a.cp[1] == t.origin[1]) {
						i = t.step > 0 ? 360 / t.step * (r + 1) : 360 / e.length * (r + 1);
						s = t.radius
					} else {
						var c = a.angle;
						i = c / 1.5 + 10 * (r + 1);
						s = t.radius
					}
					l = t.getPosition(a.cp[0], a.cp[1], i, s)
				} else {
					l = t.origin
				}
				if (t.originId) {
					t.originId = o
				}
				t.geoCoord[n.name] = l;
				if (a) {
					t.markLineData.push([{
						name: n.name
					},
					{
						name: a.data.name,
						value: n.value
					}]);
					t.markPointData.push({
						name: n.name,
						value: n.value
					})
				} else {
					t.markLineData.push([{
						name: n.name
					},
					{
						name: t.data[0].name,
						value: n.value
					}])
				}
				var d = {
					type: "Feature",
					id: o,
					properties: {
						name: n.name,
						cp: l,
						childNum: 1
					},
					geometry: {
						type: "Polygon",
						coordinates: ["@@DB@DF@@@B@@@@@@@B@B@@@@@@@@@@@D@BAB@@@B@B@@@B@B@BA@@B@B@@BD@@F@B@@@B@@@@@B@B@B@@@B@@@@@@@FD@@CA@@AD@BC@@@A@EB@@CD@@AB@B@R@@@@@@@@@@@@ED@@@@@D@@@@@B@@E@@@BA@@A@@@A@@@A@@@@@@@@@@@A@@@CB@@@@@@@@@@AA@@@@@@@@AF@B@@@B@@@@@@@@@@@@@@AB@@BD@@@@@@@@@A@@@@B@@AB@@@@B@@@@@FBBA@CD@@EAA@@@A@@@@@@B@@@@@@@@@@@BB@@@@B@@@@@@@B@D@@@@@@@@@@@@AB@@@A@@@@A@A@@@@@@@@A@@@AA@@@A@@@A@A@@@@@AA@@A@@@E@@@@@@@A@C@@@@@@@@@@@E@@@@CB@@@@A@@@@@A@AA@@@@@@@@@BA@@@A@@A@@@BE@@IBE@G@C@C@@@@BC@A@@@C@C@A@C@A@A@@B@@@@BB@@@B@B@@@B@B@@@B@B@B@B@B@@@D@@CA@C@@AB@@@@@@@@C@@@A@@@EH@@AD@@@@@@@@AC@@C@@A@A@AB@@@@A@@@A@@AC@@ABG@A@@@@BE@C@A@@@@@AAAAA@@@A@C@@@@@@AA@@@@@@C@KBO@C@@@C@A@@@A@AAA@@@E@@@E@@CD@@@@@@A@@@@@C@@@@B@@AA@@C@@@@@@A@@@A@BGA@@@@@A@@A@@@@@@C@C@CB@@@@C@@@A@@@A@A@@@@@A@@@@@A@@AI@@@B@A@@B@B@B@@@@@@@B@@A@@@@A@@AB@BA@A@@@@@@DE@@@@@@B@@@BEA@@A@A@AAA@A@@@@@A@@B@@@@A@@@@@A@@@@@@@@@B@@B@@@@B@@@@@@@@@B@@D@@@DJ@B@D@@@@@H@NB@B@B@@@L@@@B@@@BAH@J@J@F@@@@@@A@@@A@@@@B@@@@@D@B@JG@@BB@@B@@@@@DA@@@@@@B@@@@B@AD@@@@@B@NJ@@B@@@@@@@@@@@@F@AJ@@@@C@AX@@@@@@AB@@A@@F@N@NMA@F@B"],
						encodeOffsets: [[119181, 40920]]
					}
				};
				t.mapData.features.push(d);
				if (n.items && n.items.length > 0) {
					var u = {
						cp: l,
						data: n,
						angle: i
					};
					t.setData(n.items, u)
				}
			});
			t.mapOption.series[0].geoCoord = t.geoCoord;
			t.mapOption.series[0].markLine.data = t.markLineData;
			t.mapOption.series[0].markPoint.data = t.markPointData
		};
		t.create();
		return t
	};
	base.echartsOld = function(e) {
		var t = {};
		t.container = null;
		t.dataOption = null;
		t.parentOption = e.parentOption;
		t.chart = null;
		t.data = null;
		t.echarts = null;
		t.theme = null;
		t.chartEle = null;
		e.seriesType = e.seriesType ? e.seriesType: "map";
		t.element = document.createElement("div");
		$(t.element).addClass("echarts-chart");
		if (e.show) {
			$(t.element).hide()
		} else {
			$(t.element).show()
		}
		$(e.container).append(t.element);
		base.setProperty(t.element, e);
		t.mapSelectFuc = function(a) {
			if (e.seriesType == "map") {
				a.on("mapselectchanged",
				function(a) {
					e.mapSelectedEvent(a, t)
				})
			} else {
				a.on("click",
				function(a) {
					e.mapSelectedEvent(a, t)
				})
			}
		};
		t.clickFuc = function(a) {
			require(["echartsConfig"],
			function(r) {
				a.on("click",
				function(a) {
					e.clickEvent(a, t)
				})
			})
		};
		t.hoverFuc = function(t) {
			require(["echartsConfig"],
			function(a) {
				t.on("hover",
				function(t) {
					e.hoverEvent(t)
				})
			})
		};
		t.create = function() {
			if (e.option) {
				e.dataOption = e.option;
				if (e.setOptionDataEvent) {
					e.setOptionDataEvent(e)
				}
				if (e.type == "map") {
					require(["echarts2.0"],
					function() {
						if (e.mapData && e.mapName) {
							echarts.util.mapData.params.params[e.mapName] = {
								getGeoJson: function(t) {
									if (typeof e.mapData == "string") {
										$.getJSON(e.mapData,
										function(e) {
											t(echarts.util.mapData.params.decode(e))
										})
									} else {
										t(echarts.util.mapData.params.decode(e.mapData))
									}
								}
							};
							t.echarts = echarts;
							t.chart = t.echarts.init(t.element, t.theme);
							t.chartEle = t.chart.setOption(e.dataOption, true);
							$(window).on("resize",
							function() {
								t.reDraw()
							});
							if (e.mapSelectedEvent) {
								t.mapSelectFuc(t.chart)
							}
							if (e.clickEvent) {
								t.clickFuc(t.chart)
							}
							if (e.hoverEvent) {
								t.hoverFuc(t.chart)
							}
						} else {
							t.echarts = echarts;
							t.chart = t.echarts.init(t.element, t.theme);
							t.chartEle = t.chart.setOption(e.dataOption, true);
							$(window).on("resize",
							function() {
								t.reDraw()
							});
							if (e.mapSelectedCallback) {
								t.mapSelectFuc(t.chart)
							}
							if (e.clickCallback) {
								t.clickFuc(t.chart)
							}
							if (e.hoverCallback) {
								t.hoverFuc(t.chart)
							}
						}
					})
				} else {
					require(["echarts.min", e.theme],
					function(a, r) {
						t.echarts = a;
						t.theme = r;
						t.chart = t.echarts.init(t.element, t.theme);
						t.chartEle = t.chart.setOption(e.dataOption, true);
						$(window).on("resize",
						function() {
							t.reDraw()
						});
						if (e.mapSelectedCallback) {
							t.mapSelectFuc(t.chart)
						}
						if (e.clickCallback) {
							t.clickFuc(t.chart)
						}
						if (e.hoverCallback) {
							t.hoverFuc(t.chart)
						}
					})
				}
			}
		};
		t.setChartOption = function(t) {
			e.dataOption = t
		};
		t.getChartOption = function() {
			return e.dataOption
		};
		t.refresh = function(a) {
			t.setChartOption(a);
			t.chart.setOption(e.dataOption, true)
		};
		t.reDraw = function() {
			t.chart = t.echarts.init(t.element, t.theme);
			t.chart.setOption(e.dataOption, true);
			if (e.mapSelectedEvent) {
				t.mapSelectFuc(t.chart)
			}
		};
		t.create();
		return t
	};
	base.carousel = function(e) {
		var t = {};
		if (!e) {
			return
		} else {
			t.container = $(e.container) ? $(e.container) : null;
			if (!e || !t.container) {
				return
			}
			t.index = 0;
			t.data = e.data ? e.data: null;
			t.remoteData = e.remoteData ? e.remoteData: null;
			t.step = e.step ? e.step: 4;
			t.interval = e.interval ? e.interval: false;
			t.carouselBody = $(t.container).find(".carousel-inner")
		}
		t.setCarousel = function() {
			if (e.setCarouselEvent) {
				e.setCarouselEvent(t)
			}
		};
		t.slid = function() {
			if (e.slidEvent) {
				$(t.container).on("slid.bs.carousel",
				function() {
					e.slidEvent(t)
				})
			}
		};
		t.slide = function() {
			if (e.slideEvent) {
				$(t.container).on("slide.bs.carousel",
				function() {
					e.slideEvent(t)
				})
			}
		};
		t.next = function() {
			$(t.container).carousel("next")
		};
		t.prev = function() {
			$(t.container).carousel("prev")
		};
		t.drawCarousel = function() {
			if (e.slideEvent) {
				$(t.container).on("slide.bs.carousel",
				function() {
					e.slideEvent(t)
				})
			}
			if (t.data) {
				$(t.container).carousel({
					interval: t.interval
				})
			}
		};
		t.create = function() {
			base.setProperty(t.carouselBody, e);
			t.slid();
			t.slide();
			t.drawCarousel();
			t.setCarousel();
			return t
		};
		t.create();
		return t
	};
	base.highCharts = function(e) {
		var t = {};
		t.container = e.container ? e.container: null;
		t.chartOption = e.chartOption ? e.chartOption: null;
		t.theme = e.theme ? e.theme: "dark";
		t.callback = e.callback ? e.callback: null;
		t.element = null;
		t.isMain = e.isMain ? e.isMain: true;
		t.create = function() {
			if (t.container && t.chartOption) {
				if (t.isMain) {
					t.element = t.container
				} else {
					t.element = document.createElement("div");
					$(t.container).append(t.element)
				}
				base.setProperty(t.element, e);
				if (t.callback) {
					$(t.element).highcharts(t.chartOption, t.callback)
				} else {
					$(t.element).highcharts(t.chartOption)
				}
			}
			return t
		};
		t.create();
		return t
	};
	base.progress = function(e) {
		var t = {};
		t.element = null;
		t.container = e.container ? e.container: null;
		t.data = e.data.replace("/g", "%");
		t.data = Number(t.data) ? Number(t.data) : 0;
		t.animate = e.animate ? e.animate: true;
		t.progressColor = e.progressColor ? e.progressColor: "#0298f7";
		t.bgColor = e.bgColor ? e.bgColor: "#111";
		t.radius = e.radius ? e.radius: 5;
		t.label = null;
		t.height = e.height ? e.height: 9;
		t.progress = null;
		t.labelColor = e.labelColor ? e.labelColor: "#fff";
		t.progressLine = null;
		t.timer = e.timer ? e.timer: 1e3;
		t.create = function() {
			$(t.container).css("height", t.height);
			$(t.container).css("float", "left");
			$(t.container).html("");
			if (!e.label) {
				t.setLabel()
			} else {
				if (e.label.show || e.label.show == "undefined") t.setLabel()
			}
			t.setProgress();
			base.setProperty(t.element, e);
			return t
		};
		t.setLabel = function() {
			t.label = document.createElement("div");
			$(t.label).addClass("progressLabel");
			$(t.label).css("text-align", "center");
			$(t.label).css("position", "relative");
			$(t.label).css("height", t.height);
			$(t.label).css("z-index", 3);
			if (t.height > 9) {
				$(t.label).css("top", t.height - 9)
			} else {
				$(t.label).css("top", -(9 - t.height + 3))
			}
			$(t.label).css("color", t.labelColor);
			$(t.label).css("font-size", t.height);
			$(t.label).html(0 + "%");
			$(t.container).append(t.label)
		};
		t.progressGlass = null;
		t.setProgressGlass = function() {
			t.progressGlass = document.createElement("div");
			$(t.progressGlass).addClass("progressGlass");
			$(t.progressGlass).css("background-color", "rgba(255,255,255,0.35)");
			$(t.progressGlass).css("height", t.height * .4);
			$(t.progressGlass).css("position", "absolute");
			$(t.progressGlass).css("border-radius", t.radius);
			$(t.progressGlass).css("left", 2);
			$(t.progressGlass).css("right", 2);
			$(t.progressGlass).css("top", 1);
			$(t.progress).append(t.progressGlass)
		};
		t.setProgress = function() {
			t.progress = document.createElement("div");
			$(t.progress).css("border-radius", t.radius);
			$(t.progress).css("width", "100%");
			$(t.progress).css("position", "relative");
			$(t.progress).css("z-index", 1);
			$(t.progress).css("top", -6);
			$(t.progress).css("background", t.bgColor);
			$(t.progress).css("padding", 1);
			$(t.progress).css("height", t.height);
			$(t.progress).css("font-size", "13px");
			$(t.progress).css("box-shadow", "0 0 5px rgba(0,0,0,0.7)");
			$(t.container).append(t.progress);
			if (!base.IE8()) {
				t.setProgressGlass()
			}
			t.setProgressLine()
		};
		t.clear = function() {
			$(t.progressLine).css("width", 0)
		};
		t.timeoutFuc = null;
		t.animate = function() {
			if (t.animate) {
				$(t.progressLine).css("width", 0);
				var e = 0;
				t.timeoutFuc = window.setInterval(function() {
					e++;
					$(t.label).html(e + "%");
					if (e >= t.data) {
						window.clearInterval(t.timeoutFuc)
					}
				},
				t.timer / t.data);
				$(t.progressLine).animate({
					width: $(t.progress).width() * (t.data / 100)
				},
				t.timer)
			} else {
				$(t.label).html(t.data + "%");
				$(t.progressLine).css("width", $(t.progress).width() * (t.data / 100))
			}
		};
		t.setProgressLine = function() {
			t.progressLine = document.createElement("div");
			$(t.progressLine).addClass("progressLine");
			$(t.progressLine).css("border-radius", t.radius);
			if (jQuery.isArray(t.progressColor)) {
				var e = null;
				var a = 0;
				var r = 0;
				$(t.progressColor).each(function(n, o) {
					if (o.split(":").length > 1) {
						a = r;
						r = o.split(":")[1]
					} else {
						a = r;
						r = Math.round(100 / t.progressColor.length * (n + 1))
					}
					if (t.data >= a && t.data < r) {
						e = o
					}
				});
				if (e) {
					var n = e.split(":")[0];
					$(t.progressLine).css("background", n);
					$(t.progressLine).css("box-shadow", "0 0 15px " + n)
				}
			} else {
				$(t.progressLine).css("background", t.progressColor)
			}
			$(t.progressLine).css("padding", 0);
			$(t.progressLine).css("height", "100%");
			$(t.progress).append(t.progressLine);
			t.animate()
		};
		t.create();
		return t
	};
	base.roundLoader = function(e) {
		var t = {};
		t.container = e.container ? e.container: null;
		t.time = e.time ? e.time: null;
		t.showPercentage = e.showPercentage ? e.showPercentage: false;
		t.element = null;
		t.value = t.value ? t.value: 0;
		t.create = function() {
			if (base.IE8()) {
				return
			}
			if (e && t.container) {
				require(["radialIndicator"],
				function() {
					base.setProperty(t.element, e);
					t.set()
				})
			}
			return t
		};
		t.set = function() {
			if (t.time) {
				window.setInterval(function() {
					if (t.value < 100) {
						t.value = t.value + 1
					} else {
						t.value = 0;
						if (e.drawEvent) {
							e.drawEvent(t)
						}
					}
					t.draw()
				},
				t.time / 100)
			} else {
				t.draw()
			}
		};
		t.draw = function() {
			$(t.container).html("");
			t.element = $(t.container).radialIndicator({
				radius: 12,
				showPercentage: t.showPercentage,
				displayNumber: false,
				barWidth: 2.5,
				roundCorner: true,
				barBgColor: "rgba(0,0,0,0)",
				barColor: "#20c6fc",
				shadowColor: "#20c6fc",
				shadowRadius: 5
			}).data("radialIndicator");
			t.element.value(t.value)
		};
		t.create();
		return t
	};
	base.scroll = function(e) {
		var t = {};
		t.container = e.container ? e.container: null;
		t.topEvent = e.topEvent ? e.topEvent: null;
		t.bottomEvent = e.bottomEvent ? e.bottomEvent: null;
		t.scrollEvent = e.scrollEvent ? e.scrollEvent: null;
		t.xScroll = e.xScroll ? e.xScroll: false;
		t.axis = e.axis?e.axis:"y";
		t.scrollbarPosition = e.scrollbarPosition?e.scrollbarPosition:"inside";
		t.scrollOption = {
			mouseWheel: true
		};
		t.create = function() {
			require(["jqScrollbar"],
			function() {
				if (t.scrollEvent) {
					t.scrollOption.callbacks.onScroll = function() {
						t.scrollEvent(t)
					}
				}
				if (t.bottomEvent) {
					t.scrollOption.callbacks.onTotalScroll = function() {
						t.bottomEvent(t)
					}
				}
				if (t.topEvent) {
					t.scrollOption.callbacks.onTotalScrollBack = function() {
						t.topEvent(t)
					}
				}
				if (t.xScroll) {
					t.scrollOption.horizontalScroll = t.xScroll
				}
				if(t.axis){
					t.scrollOption.axis = t.axis;
				}
				if(t.scrollbarPosition){
					t.scrollOption.scrollbarPosition = t.scrollbarPosition;
				}
				$(t.container).mCustomScrollbar(t.scrollOption);
				return t
			});
			return t
		};
		t.destroy = function() {
			$(t.container).mCustomScrollbar("destroy")
		};
		t.disable = function() {
			$(t.container).mCustomScrollbar("disable")
		};
		t.create();
		return t
	};
	base.grid = function(e) {
		var t = {};
		t.container = e.container ? e.container: null;
		t.table = null;
		t.ajaxType = e.ajaxType ? e.ajaxType: "get";
		t.header = null;
		t.body = null;
		t.footer = null;
		t.params = {};
		t.columns = null;
		t.gridOption = e.gridOption ? e.gridOption: null;
		t.pagination = e.pagination ? e.pagination: true;
		t.paginationContainer = null;
		t.paginationNumber = 5;
		t.url = e.url ? e.url: null;
		t.data = e.data ? e.data: null;
		t.pageSize = e.pageSize ? e.pageSize: 10;
		t.pageNumber = 1;
		t.callback = e.callback ? e.callback: null;
		t.total = 0;
		t.pageCount = 0;
		t.pageItemOption = {
			first: {
				type: "first",
				title: "首页",
				number: 1,
				context: "<i class='fa fa-angle-double-left'></i>"
			},
			previous: {
				type: "previous",
				title: "上一页",
				number: t.pageNumber - 1,
				context: "<i class='fa fa-angle-left'></i>"
			},
			moreLeft: {
				type: "moreLeft",
				context: "..."
			},
			moreRight: {
				type: "moreRight",
				context: "..."
			},
			next: {
				type: "next",
				number: t.pageNumber - 1,
				title: "下一页",
				context: "<i class='fa fa-angle-right'></i>"
			},
			last: {
				type: "last",
				number: t.pageCount,
				title: "末页",
				context: "<i class='fa fa-angle-double-right'></i>"
			},
			jump: {
				type: "jump",
				context: "到第<input type='text' class='pageInput form-control'/>页<button style='margin-left:5px' class='btn btn-primary'>确定</button>"
			},
			page: {
				number: null,
				type: "page",
				context: null,
				cls: "fb-page-item"
			}
		};
		t.create = function() {
			if (t.container) {
				t.drawTable()
			}
			return t
		};
		t.drawTable = function() {
			if ($(t.container).children("table").length == 0) {
				t.table = document.createElement("table");
				base.setProperty(t.table, t.gridOption.grid);
				$(t.container).append(t.table)
			} else {
				t.table = $(t.container).children("table")[0];
				base.setProperty(t.table, t.gridOption.grid)
			}
			t.drawHeader();
			t.drawBody();
			t.drawFooter();
			if (t.pagination) {
				t.query({
					isPagination: true
				})
			}
		};
		t.drawSort = function(e, a) {
			var r = "<div class='btn-group'><a class='dropdown-toggle' data-toggle='dropdown'><span>" + a.columnContext + "</span><i class='fa fa-sort-down'></i></a>" + "<ul class='dropdown-menu' role='menu'>" + "<li><a type='desc'>降序</a></li>" + "<li><a type='asc'>升序</a></li>" + "<li><a>正常</a></li>" + "</ul>";
			$(e).html(r);
			$(e).find(".dropdown-menu a").unbind("click").on("click",
			function() {
				switch ($(this).attr("type")) {
				case "desc":
					t.params.sort = a.columnName + "_desc";
					break;
				case "asc":
					t.params.sort = a.columnName + "_asc";
					break;
				default:
					if (t.params.sort) delete t.params.sort;
					break
				}
				t.pageNumber = 1;
				t.query({
					isPagination: true
				})
			})
		};
		t.drawHeader = function() {
			if ($(t.container).children("thead").length == 0) {
				if (t.gridOption) {
					if (t.gridOption.columns && t.gridOption.columns.length > 0) {
						t.columns = t.gridOption.columns;
						t.header = document.createElement("thead");
						$(t.table).append(t.header);
						$(t.columns).each(function(e, a) {
							var r = document.createElement("tr");
							$(t.header).append(r);
							$(a).each(function(e, a) {
								var n = document.createElement("th");
								base.setProperty(n, a);
								if (a.sort) {
									t.drawSort(n, a)
								} else {
									$(n).html(a.columnContext)
								}
								$(r).append(n)
							})
						})
					}
				}
			}
		};
		t.query = function(e) {
			$(t.body).html("");
			t.params.pageSize = t.pageSize;
			t.params.pageNumber = t.pageNumber;
			if (t.url) {
				base.ajax({
					url: t.url,
					params: t.params,
					type: t.ajaxType,
					success: function(a) {
						$(t.body).html("");
						var r = a.data;
						t.total = a.total;
						$(r).each(function(e, a) {
							var r = document.createElement("tr");
							$(t.body).append(r);
							$(t.columns[0]).each(function(e, n) {
								var o = document.createElement("td");
								if (t.gridOption.columnDefine[n.columnName]) {
									var l = t.gridOption.columnDefine[n.columnName];
									if (l.drawEvent) {
										l.drawEvent(a[n.columnName], o)
									}
								} else {
									$(o).html(a[n.columnName])
								}
								$(r).append(o)
							})
						});
						if (e) {
							if (e.isPagination) {
								t.drawPagination()
							}
						}
						if (t.callback) {
							t.callback()
						}
					},
					beforeSend: function() {
						$(t.body).html("<tr><td class='tb-loadContainer' style='height:200px' colspan='" + $(t.table)[0].rows.item(0).cells.length + "'></td></tr>");
						base.loading($(t.body).find(".tb-loadContainer"))
					}
				})
			}
		};
		t.drawBody = function() {
			$(t.table).remove("tbody");
			t.body = document.createElement("tbody");
			$(t.table).append(t.body)
		};
		t.drawFooter = function() {
			$(t.table).remove("tfoot");
			t.footer = document.createElement("tfoot");
			$(t.table).append(t.footer)
		};
		t.drawPagination = function() {
			if (!t.paginationContainer) {
				t.paginationContainer = document.createElement("ul");
				$(t.paginationContainer).addClass("fb-grid-pagination");
				$(t.container).append(t.paginationContainer)
			} else {
				$(t.paginationContainer).html("")
			}
			if (t.total % t.pageSize == 0) {
				t.pageCount = t.total / t.pageSize
			} else {
				t.pageCount = Math.floor(t.total / t.pageSize) + 1
			}
			t.drawPaginationItem()
		};
		t.setActive = function(e) {
			$(t.paginationContainer).find(".active").removeClass("active");
			$(e).addClass("active")
		};
		t.drawPaginationItem = function() {
			$(t.paginationContainer).find("li").remove();
			t.createPageItem("info");
			if (t.pageCount <= t.paginationNumber) {
				for (var e = 1; e <= t.pageCount; e++) {
					t.createPageItem("page", e)
				}
			} else {
				if (t.pageNumber < t.paginationNumber) {
					for (var e = 1; e <= t.paginationNumber; e++) {
						t.createPageItem("page", e)
					}
					t.createPageItem("moreRight");
					t.createPageItem("next", t.pageNumber + 1);
					t.createPageItem("last", t.pageCount)
				} else if (t.pageNumber > t.pageCount - t.paginationNumber) {
					t.createPageItem("first", 1);
					t.createPageItem("previous", t.pageNumber - 1);
					t.createPageItem("moreLeft");
					for (var e = t.pageCount - t.paginationNumber; e <= t.pageCount; e++) {
						t.createPageItem("page", e)
					}
				} else {
					t.createPageItem("first", 1);
					t.createPageItem("previous", t.pageNumber - 1);
					t.createPageItem("moreLeft");
					for (var e = t.pageNumber - Math.floor(t.paginationNumber / 2); e <= t.pageNumber + Math.floor(t.paginationNumber / 2); e++) {
						t.createPageItem("page", e)
					}
					t.createPageItem("moreRight");
					t.createPageItem("next", t.pageNumber + 1);
					t.createPageItem("last", t.pageCount)
				}
			}
			t.createPageItem("jump")
		};
		t.createPageItem = function(e, a) {
			var r = document.createElement("li");
			$(t.paginationContainer).append(r);
			$(r).attr("type", e);
			switch (e) {
			case "info":
				$(r).html("<div style='letter-spacing:0.5px;padding:0 20px;font-size:13px;'>共" + t.pageCount + "页(" + t.total + "条记录)" + "" + "</div>");
				break;
			case "first":
				$(r).html("<i class='fa fa-angle-double-left'></i>");
				$(r).attr("title", "首页");
				$(r).on("click",
				function() {
					t.pageNumber = a;
					t.drawPaginationItem();
					t.query()
				});
				break;
			case "previous":
				$(r).html("<i class='fa fa-angle-left'></i>");
				$(r).attr("title", "上一页");
				$(r).on("click",
				function() {
					t.pageNumber = a;
					t.drawPaginationItem();
					t.query()
				});
				break;
			case "next":
				$(r).html("<i class='fa fa-angle-right'></i>");
				$(r).attr("title", "下一页");
				$(r).on("click",
				function() {
					t.pageNumber = a;
					t.drawPaginationItem();
					t.query()
				});
				break;
			case "last":
				$(r).html("<i class='fa fa-angle-double-right'></i>");
				$(r).attr("title", "末页");
				$(r).on("click",
				function() {
					t.pageNumber = a;
					t.drawPaginationItem();
					t.query()
				});
				break;
			case "page":
				$(r).addClass("fb-page-item");
				$(r).html(a);
				if (t.pageNumber == a) {
					$(r).addClass("active")
				}
				$(r).on("click",
				function() {
					t.pageNumber = a;
					t.drawPaginationItem();
					t.query()
				});
				break;
			case "moreLeft":
			case "moreRight":
				$(r).html("...");
				break;
			case "jump":
				$(r).html("到第<input type='number' class='pageInput form-control'/>页<button style='margin-left:5px' class='btn btn-primary'>确定</button>");
				$(r).find("button").on("click",
				function() {
					var e = $(r).find("input").val();
					if (e) {
						if (e < 1) {
							e = 1;
							$(r).find("input").val(e)
						} else if (e > t.pageCount) {
							e = t.pageCount
						}
						t.pageNumber = e;
						t.query({
							isPagination: true
						})
					}
				});
				break
			}
		};
		t.pageTo = function(e) {
			t.pageNumber = e;
			t.drawPaginationItem();
			t.query()
		};
		t.reset = function() {
			t.pageNumber = 1;
			t.pageSize = e.pageSize ? e.pageSize: t.pageSize;
			t.params = {};
			t.query({
				isPagination: true
			})
		};
		t.search = function(e) {
			t.pageNumber = 1;
			t.pageSize = e.pageSize ? e.pageSize: t.pageSize;
			if (e) {
				t.params = e.params ? e.params: t.params
			}
			t.query({
				isPagination: true
			})
		};
		t.addRow = function(e, a) {
			var r = document.createElement("tr");
			$(t.body).append(r);
			$(e).each(function(e, t) {
				var a = document.createElement("td");
				a.html(t);
				$(r).append(a)
			});
			if (a) {
				a(t)
			}
		};
		t.deleteRow = function(e) {
			$(e).parents("tr").remove()
		};
		t.create();
		return t
	};
	base.getCR = function(e, t) {
		var a = null;
		var r = "";
		if ((typeof e).toLowerCase() == "string") {
			a = $("input[name='" + e + "']:checked");
			if (t) {
				$(a).each(function(e, t) {
					if (e == $(a).length - 1) {
						r += $(t).val()
					} else {
						r += $(t).val() + ","
					}
				});
				return r
			} else {
				return a
			}
		} else {
			a = $("input[name='" + $(e).attr("name") + "']:checked");
			if (t) {
				$(a).each(function(e, t) {
					if (e == $(a).length - 1) {
						r += $(t).val()
					} else {
						r += $(t).val() + ","
					}
				})
			} else {
				return a
			}
		}
	};
	base.selectAll = function(e, t, a) {
		$(e).on("click",
		function() {
			if ($(this).is(":checked")) {
				$(t).prop("checked", true)
			} else {
				$(t).prop("checked", false)
			}
			if (a) {
				a(e, t)
			}
		});
		$(t).on("click",
		function() {
			if ($(t).filter(":checked").length == $(t).length) {
				$(e).prop("checked", true)
			} else {
				$(e).prop("checked", false)
			}
			if (a) {
				a(e, t)
			}
		})
	};
	base.clearLastCharacter = function(e) {
		e = e.substring(0, e.length - 1);
		return e
	};
	base.formInterface = function() {};
	base.form = new base.formInterface;
	base.form.getParams = function(e) {
		var t = {};
		var a = {};
		a.form = e ? e: null;
		if (a.form) {
			var r = $(a.form)[0].elements;
			for (var n = 0,
			o = r.length; n < o; n++) {
				var l = r[n];
				var i = $(l).attr("name");
				if (!i) {
					continue
				}
				if (l.tagName.toLowerCase() == "input") {
					if ($(l).attr("type").toLowerCase() == "checkbox" || $(l).attr("type").toLowerCase() == "radio") {
						if (t[i]) {
							continue
						} else {
							var s = "";
							$(a.form).find("input[name='" + i + "']").each(function(e, t) {
								if ($(t).is(":checked")) {
									s += $(t).val() + ","
								}
							});
							if (s != "") {
								s = base.clearLastCharacter(s);
								t[i] = s
							}
						}
					} else if ($(l).attr("type").toLowerCase() == "button") {
						continue
					} else {
						if ($(l).val()) {
							t[i] = $(l).val()
						}
					}
				} else if (l.tagName.toLowerCase() == "select" && $(l).val() == "-1") {
					continue
				} else if (l.tagName.toLowerCase() == "button") {
					continue
				} else {
					t[i] = $(l).val()
				}
			}
		}
		return t
	};
	base.form.validate = function(option) {
		var self = {};
		self.form = option.form?option.form:null;
		self.roles = option.roles?option.roles:null;
		self.promptType = option.promptType?option.promptType:"follow";
		self.checkAll = option.checkAll==true?true:false;
		self.errorMsg = [];
		self.isPass = true;
		self.tipPosition = option.tipPosition?option.tipPosition:"bottom";
		self.passCallback = option.passCallback?option.passCallback:null;
		self.notPassCallback = option.notPassCallback?option.notPassCallback:null;
		self.error = function(role,errorText,obj){
			switch(self.promptType){
				case "top":
					if($(obj).attr("type")){
						if($(obj).attr("type").toLowerCase()=="checkbox"||$(obj).attr("type").toLowerCase()=="radio"){
							//$(obj).parent().addClass("errorStyle");
						}else{
							$(obj).addClass("errorStyle");
						}
					}else{
						$(obj).addClass("errorStyle");
					}
					
					
				break;
				
				case "tip":
					if($(obj).attr("type")){
						if($(obj).attr("type").toLowerCase()=="checkbox"||$(obj).attr("type").toLowerCase()=="radio"){
							$(obj).parent().attr("data-toggle","tooltip");
							$(obj).parent().attr("data-placement",self.tipPosition);
							$(obj).parent().attr("data-trigger","manual");
							$(obj).parent().attr("title",errorText);
							//$(obj).parent().addClass("errorStyle");
						}else{
							$(obj).attr("data-toggle","tooltip");
							$(obj).attr("data-placement",self.tipPosition);
							$(obj).attr("data-trigger","manual");
							$(obj).attr("title",errorText);
							$(obj).addClass("errorStyle");
						}
					}else{
						$(obj).attr("data-toggle","tooltip");
						$(obj).attr("data-placement",self.tipPosition);
						$(obj).attr("data-trigger","manual");
						$(obj).attr("title",errorText);
						$(obj).addClass("errorStyle");
					}
					
				break;
				
				case "follow":
					$(obj).parent().find(".ui-form-error").remove();
					$(obj).parent().append("<div class='ui-form-error' style='color:red;text-align:left;'>"+errorText+"</div>");
					if($(obj).attr("type")!="checkbox"&&$(obj).attr("type")!="radio"){
						$(obj).addClass("errorStyle");
					}
					
				break;
				
			}
			self.errorMsg.push(errorText+"<br>");
			return true;
		};
		self.verify = function(obj,role){
			var errorText = "";
			var hasError = false;
			var iname =  role.text?role.text:$(obj).attr("name");

			for(var key in role){
				switch(key){
					case "required"://是否必填
					
						if(role[key]){
							
							switch($(obj)[0].tagName.toLowerCase()){
								case "select":
								
									var val = $(obj).val();
									
									if(!val||val=="-1"){
										errorText = "必选项";
										hasError = self.error(role,errorText,obj);
									}
								break;
								
								case "textarea":
									var val = $(obj).val();
									if(!val){
										errorText = "必填项";
										hasError = self.error(role,errorText,obj);
									}
								break;
								
								case "input":
									switch($(obj).attr("type")){
										case "checkbox":
										case "radio":
										
											if(base.getChecks($(obj).attr("name")).length==0){
												errorText = "必选项";
												
												hasError = self.error(role,errorText,obj);
											}
										break;
										
										
										
										default:
										
											var val = $(obj).val();
											if(!val){
												errorText = "必填项";
												
												hasError = self.error(role,errorText,obj);
											}
										break;
									}
								break;
							}
						}
					break;
					
					case "int"://是否是整数
					case "float"://是否是小数
					case "number"://是否是数字
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						var ex = /^\d+$/;
						var ds = "整数";
						if(key=="float"){
							ex = /^\d+(\.\d+)?$/;
							ds = "小数";
						}else if(key=="number"){
							ds ="数字";
						}
						
						if(isNaN(Number(val))){
							errorText = "必须是"+ds;
							hasError = self.error(role,errorText,obj);
						}else{
							
							if(!ex.test(val)&&key!="number"){
								errorText = "必须是"+ds;
								hasError = self.error(role,errorText,obj);
							}else{
								switch((typeof(role[key])).toLowerCase()){
									case "number":
										
										if(Number(val)>role[key]){
											errorText = "数字不能大于"+role[key];
											hasError = self.error(role,errorText,obj);
										}
									break;
									
									default:
										if(jQuery.isArray(role[key])&&role[key].length>=2){
											if(val<role[key][0]){
												errorText = "不能小于"+role[key][0];
												hasError = self.error(role,errorText,obj);
											}else if(val>role[key][1]){
												errorText = "不能大于"+role[key][1];
												hasError = self.error(role,errorText,obj);
											}
										}
									break;
								}
							}
						}
					break;
					
					
					case "length"://字符串长度
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(base.getByteLen(val)>role[key]){
							errorText = "不能超过"+role[key]+"个字符";
							hasError = self.error(role,errorText,obj);
						}
					break;
                    case "zhMinLength":
                        var val = $(obj).val();
                        if(!val||val==""){
                            return;
                        }
                        if(val.length<role[key]){
                            errorText = "输入的最小长度不能少于"+role[key];
                            hasError = self.error(role,errorText,obj);
                        }
                        break;
					case "zhLength":
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(val.length>role[key]){
							errorText = "输入的最大长度不能超过"+role[key];
							hasError = self.error(role,errorText,obj);
						}
						break;
					case "identityCard"://身份证
						errorText = "身份证号不正确";
						var ex = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "en"://英文
						errorText = "必须是英文";
						var ex = /^[a-z]*|[A-Z]*$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "ip"://ip地址
						errorText = "IP地址不正确";
						var ex = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "ips_port":
						errorText = "IP地址不正确";
						var ipEx = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
						var portEx = /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						var myError = false;
						$(val.split(";")).each(function(i,o){
							var ip = null;
							var port = null;
							if(o.split(":")[0] !="" && o.split(":")[0]){
								ip = o.split(":")[0];
							}else{
								myError = true;
								return false;
							}
							if(o.split(":")[1] !="" && o.split(":")[1]){
								port = o.split(":")[1];
							}else{
								myError = true;
								return false;
							}
							
							if(!ipEx.test(ip)){
								myError = true;
								return false;
							}
							if(port){
								if(!portEx.test(port)){
									myError = true;
									return false;
								}
							}
							
						});
						if(myError){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "port"://端口
						errorText = "端口不正确";
						var ex = /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "money"://货币 
						var ex = /^\d+.?\d{0,2}$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(isNaN(val)){
							errorText = "格式不正确";
							hasError = self.error(role,errorText,obj);
						}else{
							 val = Number(val);
							if(!ex.test(val)){
								errorText = "格式不正确";
								hasError = self.error(role,errorText,obj);
							}
						}
					break;
					
					case "mobile"://手机号
						errorText = "手机号码不正确";
						var ex =  /^[1][3,4,5,7,8][0-9]{9}$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "telephone"://普通电话 
						errorText = "电话号码不正确";
						//var ex = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
						var ex = /^0\d{2,3}-?\d{7,8}$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "email"://邮箱
						errorText = "邮箱地址不正确";
						var ex = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "filter"://只允许字母、数字、下划线
					//^[a-zA-Z]{1}\w*$
						var ex = /^[a-zA-Z]{1}\w*$/  ///^\w+$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							
							errorText = "只允许字母、数字、下划线";
							
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					case "filterCN":
						var ex = /^[\u4E00-\u9FA5a-zA-Z0-9_]+$/; 
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							
							errorText = "不能包含特殊字符";
							
							hasError = self.error(role,errorText,obj);
						}
					break;
					/**以什么开头**/
					case "first":
						var fs = role[key];
						var l = fs.length;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if (val.slice(0,l) != fs) {
							errorText = "必须以"+fs+"开头";
							hasError = self.error(role,errorText,obj);
						}

					break;
					
					/**网址**/
					case "website":
						var strRegex = "^[^\s]+"; 
						var ex = new RegExp(strRegex);
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							errorText = "网络地址不正确";
							hasError = self.error(role,errorText,obj);
						}
						
					break;
					
					/**en_number英文和数字**/
					case "en_number":
						errorText = "必须是英文或数字";
						var ex = /^[0-9a-zA-Z]+$/;
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					/**文件大小**/
					case "fileSize":
						errorText = "文件大小不能超过"+role[key]+"kb";
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!base.form.validateFileSize($(obj),Number(role[key]))){
							hasError = self.error(role,errorText,obj);
						}
					break;
					
					/**文件名**/
					case "fileName":
						errorText = "必须是"+role[key]+"的文件";
						var val = $(obj).val();
						if(!val||val==""){
							return;
						}
						if(!base.form.validateFileExtname($(obj),role[key])){
							hasError = self.error(role,errorText,obj);
						}
					break;
					/**password密码，至少1个字母或1个数字的6-18位**/
					case "password":
						errorText = "密码格式不正确，至少1个字母和数字且6-18个字符";
						var val = $(obj).val();
						var ex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,18}$/;
						if(!val||val==""){
							return;
						}
						if(!ex.test(val)){
							hasError = self.error(role,errorText,obj);
						}
					break;
				}
			}
			return hasError;
		};
		self.init = function(){
			self.errorMsg = [];
			self.isPass = true;
			$(self.form).find(".alert").remove();
			$(self.form).find("[data-toggle='tooltip']").tooltip('destroy');
			$(self.form).find("[data-toggle='tooltip']").removeAttr("title");
			$(self.form).find("[data-toggle='tooltip']").removeAttr("data-toggle");
			$(".errorStyle").removeClass("errorStyle");
			$(".ui-form-error").remove();
			if($("#errorStyle").length==0){
				$("head").append("<style id='errorStyle'>.errorStyle{border:1px solid red;}</style>");
			}
		};
		self.scan = function(){
			self.init();
			if(self.form){
				if(self.roles){
					var i = 0;
					for(var name in self.roles){
						var role = self.roles[name];
						var obj = $(self.form).find("[name='"+name+"']");
						
						if(role&&obj.length>0){
							if(self.checkAll){
								self.verify(obj,role);
							}else{
								if(self.verify(obj,role)){
									break;
								}
							}
						}
						i++;
					}
				}else{
					var tmp = {};
					if($(self.form).length==0){return;}
					var eles = $(self.form)[0].elements;
					for(var i=0,j=eles.length;i<j;i++){
						var o =  $(eles[i]);
						var role = o.attr("role");
						if(role){
							var roleData = eval("("+role+")");
							var name = o.attr("name");
							
							if(self.checkAll){
								
								if(tmp[name]){
									continue;
								}else{
									self.verify(o,roleData);
									tmp[name] = true;
								}
							}else{
								
								if(tmp[name]){
									continue;
								}else{
									if(self.verify(o,roleData)){
										break;
									};
								}
							}
						}
					}
				}
				if(self.errorMsg.length>0){
					switch(self.promptType){
						case "tip":
							if(self.checkAll){
								$(self.form).find("[data-toggle='tooltip']").tooltip("show");
							}else{
								$(self.form).find("[data-toggle='tooltip']:eq(0)").tooltip("show");
							}
							
						break;
						
						case "top":
							var alertDialog = document.createElement("div");
							$(alertDialog).addClass("alert alert-danger");
							$(self.form).prepend(alertDialog);
							if(self.checkAll){
								$(self.errorMsg).each(function(i,o){
									$(alertDialog).append(i+1+"."+o);
								});
							}else{
								$(alertDialog).append(self.errorMsg[0]);
							}
						break;
					}
					self.isPass = false;
				}
			}else{
				self.isPass =false;
			}
			if(self.isPass){
				if(self.passCallback){
					self.passCallback(self);
				}
			}else{
				if(self.notPassCallback){
					self.notPassCallback(self);
				}
			}
			return self.isPass;
		};
		
		self.scan();
		return self.isPass;
	};
	base.form.reset = function(e, t) {
		if (e) {
			$(e)[0].reset();
			if (t) {
				t(e)
			}
		}
	};
	base.form.button = function(e) {
		var t = {};
		t.label = e.label ? e.label: "确定";
		t.disabled = e.disabled ? e.disabled: false;
		t.container = e.container ? e.container: $("body");
		t.element = null;
		t.clickEvent = e.clickEvent ? e.clickEvent: null;
		t.id = e.id ? e.id: null;
		t.cls = e.cls ? e.cls: null;
		t.style = e.style ? e.style: null;
		t.create = function() {
			t.element = document.createElement("button");
			$(t.element).html(t.label);
			if (t.cls) {
				$(t.element).addClass(e.cls.replace(/,/g, " "))
			} else {
				$(t.element).addClass("btn btn-primary")
			}
			if (t.style) {
				$(t.element).attr("style", e.style.replace(/,/g, ";"))
			}
			if (t.id) {
				$(t.element).attr("id", t.id)
			}
			if (t.disabled) {
				$(t.element).addClass("disabled")
			} else {
				if (t.clickEvent) {
					$(t.element).on("click",
					function() {
						t.clickEvent(t)
					})
				}
			}
			$(t.container).append(t.element)
		};
		t.create();
		return t
	};
	base.form.date = function(option) {
		var self = {};
		self.dateOption = option.dateOption?option.dateOption:{};
		
		self.element = option.element?option.element:null;
		
		if(option.type){
			self.dateOption.type = option.type?option.type:null;
		}
		if(option.dateOption&&option.dateOption.type){
			self.dateOption.type = option.dateOption.type?option.dateOption.type:null;
		}
		if(option.range){
			self.dateOption.range = "~";
		}
		if(option.dateOption&&option.dateOption.range){
			self.dateOption.range =  "~";
		}
		self.theme = "#01a9c2";
		if(option.theme){
			switch(option.theme){
				case "weilan":
					self.theme = "#029dd0";
				break;
				
				case "molv":
					self.theme = "#03bf4e";
				break;
				
				case "red":
					self.theme = "#f60505";
				break;
				
				case "orange":
					self.theme = "#fda917";
				break;
				
				default:
					self.theme = option.theme;
				break;
			}
		}
		self.dateOption.theme = self.theme;
		if(option.isTime){
			self.dateOption.type = "datetime";
		}
		self.create = function(){
			if($(self.element).length>0){
				var tgn = self.element.tagName;
				
				require(["date5.0"],function(laydate){
					if($(self.element).length>1){
						$(self.element).each(function(){
							self.readonly(this);
							self.dateOption.elem = this;
							laydate.render(self.dateOption);
						});
					}else{
						self.readonly($(self.element)[0]);
						self.dateOption.elem = $(self.element)[0];
						laydate.render(self.dateOption);
					}
					
				});
				
			}
		};
		self.readonly = function(el){
			var tgn = $(el)[0].tagName;
			if(tgn){
				switch(tgn.toLowerCase()){
					case "input":
					case "select":
					case "textarea":
						// $(el).attr("readonly",true);
					break;
				}
			}
		};
		self.create();
	};
	base.form.autoSelect = function(e) {
		var t = {};
		t.container = e.container ? e.container: null;
		t.data = e.data ? e.data: null;
		t.url = e.url ? e.url: null;
		t.params = e.params ? e.params: {};
		t.size = e.size ? e.size: 10;
		t.highLight = e.highLight ? e.highLight: true;
		t.autoSelectObj = null;
		t.type = e.type ? e.type: "get";
		t.ajaxSettings = e.ajaxSettings ? e.ajaxSettings: {};
		t.dropContainer = e.dropContainer ? e.dropContainer: null;
		t.clickCallback = e.clickCallback ? e.clickCallback: null;
		t.width = e.width ? e.width: "auto";
		t.create = function() {
			if (t.container) {
				require(["autoSelect"],
				function() {
					if (t.data) {
						t.autoSelectObj = $(t.container).autoSelect({
							lookup: t.data,
							dropContainer: t.dropContainer,
							clickCallback: t.clickCallback,
							width: t.width,
							onSelect: function(e) {}
						})
					} else if (t.url) {
						t.autoSelectObj = $(t.container).autoSelect({
							serviceUrl: t.url,
							params: t.params,
							type: t.type,
							dropContainer: t.dropContainer,
							ajaxSettings: t.ajaxSettings,
							clickCallback: t.clickCallback,
							width: t.width,
							lookupFilter: function(e, t, a) {},
							onSelect: function(e) {}
						})
					}
				})
			}
			return t
		};
		t.disable = function() {
			t.autocompleteObj.disable()
		};
		t.create();
		return t
	};
	base.echarts = function(e) {
		var t = {};
		t.container = e.container ? e.container: $("body");
		t.chartOption = e.chartOption ? e.chartOption: null;
		t.chart = null;
		t.echarts = e.echarts ? e.echarts: null;
		t.drawCallback = e.drawCallback ? e.drawCallback: null;
		t.create = function() {
			if (!t.echarts) {
				return
			}
			if (t.chartOption) {
				if (t.beforeEvent) {
					t.beforeEvent(t)()
				}
				t.chart = t.echarts.init($(t.container)[0]);
				if (t.chartOption.length > 0) {
					$(t.chartOption).each(function(e, a) {
						t.chart.setOption(a)
					})
				} else {
					t.chart.setOption(t.chartOption)
				}
				if (t.drawCallback) {
					t.drawCallback(t)
				}
				$(window).on("resize",
				function() {
					t.chart.resize()
				})
			}
			return t
		};
		t.refresh = function(e) {
			t.chartOption = e;
			t.chart.setOption(t.chartOption)
		};
		t.create();
		return t
	};
	base.earthRollMap = function(e) {
		var t = {};
		t.rippleImg = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/Pg0KPCFET0NUWVBFIHN2ZyBQVUJMSUMgIi0vL1czQy8vRFREIFNWRyAxLjEvL0VOIiANCiJodHRwOi8vd3d3LnczLm9yZy9HcmFwaGljcy9TVkcvMS4xL0RURC9zdmcxMS5kdGQiPg0KPHN2ZyB3aWR0aD0iNTAiIGhlaWdodD0iNTAiPiAgICANCiAgICA8Y2lyY2xlIGN4PSIyNSIgY3k9IjI1IiByPSI1IiAgc3Ryb2tlPSJyZ2IoNCwxOTgsMjU0KSIgbGluZWpvaW49InJvdW5kIiBzdHJva2Utd2lkdGg9IjMiIGZpbGwtb3BhY2l0eT0iMSIgZmlsbD0icmdiYSgwLDAsMCwwKSIgc3Ryb2tlLW9wYWNpdHk9IjAuMiI+DQogICAgICAgIDxhbmltYXRlIGF0dHJpYnV0ZVR5cGU9InhtbCIgIGF0dHJpYnV0ZU5hbWU9InIiIGZyb209IjAiIHRvPSIyMCIgZHVyPSIxcyIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiPjwvYW5pbWF0ZT4NCiAgICAgICAgPGFuaW1hdGUgYXR0cmlidXRlTmFtZT0ib3BhY2l0eSIgZnJvbT0iMSIgdG89IjAiIGJlZ2luPSIwcyIgZHVyPSIxcyIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiIC8+DQogICAgPC9jaXJjbGU+DQo8L3N2Zz4=";
		t.container = e.container ? e.container: $("body");
		t.data = e.data ? e.data: null;
		t.mapOption = {
			backgroundColor: "#999",
			globe: {
				baseTexture: "/service/images/earth-specbetter.jpg",
				displacementScale: 0,
				environment: "#000f1a",
				shading: "color",
				realisticMaterial: {
					roughness: .9
				},
				postEffect: {
					enable: false,
					bloom: {
						enable: true,
						bloomIntensity: .1
					}
				},
				depthOfField: {
					enable: true
				},
				FXAA: {
					enable: true
				},
				viewControl: {
					autoRotate: true,
					distance: 170,
					center: [0, 0, 0],
					beta: 130,
					alpha: 30,
					autoRotateAfterStill: .5
				}
			},
			series: t.data
		};
		t.create = function() {
			require(["echartsGl"],
			function() {
				var e = base.echarts({
					container: t.container,
					option: t.mapOption
				})
			})
		};
		t.create()
	};
	base.geoRollMap = function(e) {
		var t = {};
		t.container = e.container ? e.container: $("body");
		t.data = e.data ? e.data: null;
		t.mapEntity = null;
		t.pointsMapEntity = null;
		t.mapName = e.mapName ? e.mapName: "world";
		t.mapOption = {
			geo3D: {
				map: t.mapName,
				shading: "color",
				regionHeight: 6,
				silent: true,
				groundPlane: {
					show: false
				},
				light: {
					main: {
						intensity: 0
					},
					ambient: {
						intensity: 0
					}
				},
				viewControl: {
					distance: 50,
					center: [116.405285, 39.904989]
				},
				itemStyle: {
					areaColor: "#111"
				},
				boxHeight: .5
			},
			series: t.data
		};
		t.create = function() {
			require(["echarts"],
			function(e) {
				base.ajax({
					type: "get",
					url: "/service/map/" + t.mapName + ".json",
					success: function(a) {
						e.registerMap(t.mapName, a);
						require(["echartsGl"],
						function() {
							t.mapEntity = base.echarts({
								container: t.container,
								option: [{
									series: [{
										type: "map",
										map: t.mapName
									}]
								},
								t.mapOption]
							})
						})
					}
				})
			})
		};
		t.create()
	};
	base.isBoolean = function(e) {
		if (typeof e.toLowerCase() == "boolean") {
			return true
		} else {
			return false
		}
	};
	base.loadPage = function(e) {
		var t = {};
		t.url = e.url ? e.url: null;
		t.container = e.container ? e.container: null;
		t.params = e.params ? e.params: null;
		t.callback = e.callback ? e.callback: null;
		t.scrolls = e.scrolls ? e.scrolls: null;
		t.load = function() {
			if (t.container && t.url) {
				base.ajax({
					type: "get",
					url: t.url,
					dataType: "text",
					params: t.params,
					success: function(e) {
						$(t.container).html(e);
						if (t.callback) {
							t.callback(t)
						}
						if (t.scrolls) {
							$(t.scrolls.split(",")).each(function(e, t) {
								if ($(t).length > 0) {
									base.scroll({
										container: $(t)
									})
								}
							})
						}
					},
					beforeSend: function() {
						base.loading($(t.container))
					},
					error: function() {
						$(t.container).html("加载错误")
					}
				})
			}
		};
		t.load()
	};
	base.require = function(e, t) {
		require([e],
		function() {
			if (t) {
				t()
			}
		})
	};
	base.datatables = function(e) {
		var t = {};
		t.option = e.option ? e.option: null;
		t.container = e.container ? e.container: null;
		t.grid = null;
		t.callback = e.callback ? e.callback: null;
		t.language = {
			sProcessing: "处理中...",
			sLengthMenu: "显示 _MENU_ 项结果",
			sZeroRecords: "没有匹配结果",
			sInfo: "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
			sInfoEmpty: "显示第 0 至 0 项结果，共 0 项",
			sInfoFiltered: "(由 _MAX_ 项结果过滤)",
			sInfoPostFix: "",
			sSearch: "搜索:",
			sUrl: "",
			sEmptyTable: "表中数据为空",
			sLoadingRecords: "载入中...",
			sInfoThousands: ",",
			oPaginate: {
				sFirst: "首页",
				sPrevious: "上页",
				sNext: "下页",
				sLast: "末页"
			},
			oAria: {
				sSortAscending: ": 以升序排列此列",
				sSortDescending: ": 以降序排列此列"
			}
		};
		t.create = function() {
			if (t.container && t.option) {
				t.option.language = t.language;
				$.fn.dataTable.ext.errMode = "none";
				t.grid = $(t.container).DataTable(t.option)
			}
		};
		t.reload = function() {
			$(t.container).find("thead input[type='checkbox']").prop("checked", false);
			t.grid.ajax.reload()
		};
		t.addRow = function(e) {
			t.grid.row.add(e).draw(false)
		};
		t.deleteRow = function(e) {
			$(t.container).find("tbody tr").removeClass("selected");
			$(e).parents("tr").addClass("selected");
			t.grid.row(".selected").remove().draw(false)
		};
		t.create();
		return t
	};
	base.requestTip = function(e) {
		var t = {};
		t.container = null;
		if (!e) {
			e = {}
		}
		t.width = e.width ? e.width: 200;
		t.height = e.height ? e.height: 36;
		t.color = e.color ? e.color: "#555";
		t.bg = e.bg ? e.bg: "#f2f2f2";
		t.waitWord = e.waitWord ? e.waitWord: "正在提交...";
		t.successWord = e.successWord ? e.successWord: "提交成功";
		t.errorWord = e.errorWord ? e.errorWord: "提交错误";
		t.create = function() {
			t.container = document.createElement("div");
			$(t.container).css("position", "absolute");
			$(t.container).css("z-index", "2000");
			$(t.container).css("top", 10);
			$(t.container).css("left", "50%");
			$(t.container).css("right", "50%");
			$(t.container).css("text-align", "center");
			$(t.container).css("border", "1px solid #aaa");
			$(t.container).css("background", t.bg);
			$(t.container).css("width", t.width);
			$(t.container).css("height", t.height);
			$(t.container).css("margin", "auto");
			$(t.container).css("display", "none");
			$(t.container).css("line-height", t.height + "px");
			$(t.container).css("border-radius", 5);
			$("body").append(t.container)
		};
		t.wait = function(e) {
			t.waitWord = e ? e: t.waitWord;
			if (!t.container) {
				t.create()
			}
			$(t.container).html("<i class='fa fa-spinner fa-pulse fa-3x fa-fw' style='color:#aaa;margin-right:4px;font-size:14px;'></i>" + t.waitWord);
			$(t.container).fadeIn(100,
			function() {})
		};
		t.success = function(e) {
			t.successWord = e ? e: t.successWord;
			if (!t.container) {
				t.create()
			}
			$(t.container).html("<i class='fa fa-check-circle' style='color:#01b617;margin-right:4px;font-size:18px;'></i>" + t.successWord);
			window.setTimeout(function() {
				$(t.container).fadeOut(function() {
					$(t.container).remove()
				})
			},
			3e3)
		};
		t.error = function(e) {
			t.errorWord = e ? e: t.errorWord;
			if (!t.container) {
				t.create()
			}
			$(t.container).html("<i class='fa fa-times-circle' style='color:red;margin-right:4px;font-size:18px;'></i>" + t.errorWord);
			window.setTimeout(function() {
				$(t.container).fadeOut(function() {
					$(t.container).remove()
				})
			},
			3e3)
		};
		return t
	};
	base.arrayToMap = function(e, t, a) {
		var r = {};
		r.data = e ? e: null;
		r.pid = t ? t: null;
		r.map = {};
		if (r.data && r.data.length > 0) {
			if (r.pid) {
				var n = 0;
				r.transform = function(e) {
					var t = 0;
					$(r.data).each(function(o, l) {
						if (l.pid == e) {
							r.map[l.id] = l;
							if (e == r.pid) {
								n++
							}
							t++;
							if (a) {
								if (t == 1 && n == 1) {
									r.map[l.id].active = true
								}
							}
							r.transform(l.id)
						}
					})
				};
				r.transform(r.pid)
			}
		}
		return r.map
	};
	base.mapToArray = function(e, t) {
		var a = {};
		a.data = e ? e: null;
		a.array = [];
		a.pid = t ? t: null;
		if (a.data && a.pid) {
			a.thansform = function(e, t) {
				for (var r in a.data) {
					var n = a.data[r];
					if (n.pid == e) {
						if (t) {
							if (!t.items) {
								t.items = []
							}
							t.items.push(n);
							a.thansform(n.id, n)
						} else {
							a.array.push(n);
							a.thansform(n.id, n)
						}
					}
				}
			};
			a.thansform(t)
		}
		return a.array
	};
	base.confirm = function(e) {
		var t = {};
		t.label = e.label ? e.label: "删除";
		t.context = e.context ? e.context: "<div style='text-align:center;font-size:13px;'>确定是否删除?</div>";
		t.confirmCallback = e.confirmCallback ? e.confirmCallback: null;
		t.cancelCallback = e.cancelCallback ? e.cancelCallback: null;
		t.width = 200;
		t.height = 30;
		t.create = function() {
			var e = base.modal({
				label: t.label,
				context: t.context,
				width: t.width,
				height: t.height,
				buttons: [{
					label: "确定",
					cls: "btn btn-info",
					clickEvent: function() {
						if (t.confirmCallback) {
							t.confirmCallback()
						}
						e.hide()
					}
				},
				{
					label: "取消",
					cls: "btn btn-warning",
					clickEvent: function() {
						if (t.cancelCallback) {
							t.cancelCallback()
						}
						e.hide()
					}
				}]
			})
		};
		t.create();
		return t
	};
	base.findParentToArray = function(e, t) {
		var a = {};
		a.data = e ? e: null;
		a.id = t ? t: null;
		var r = [];
		if (a.data && a.id) {
			a.find = function(e) {
				if (a.data[e]) {
					r.push(a.data[e]);
					a.find(a.data[e].pid)
				}
			};
			a.find(a.id)
		}
		return r.reverse()
	};
	base.tree = function(e) {
		var t = {};
		t.container = e.container ? e.container: null;
		t.setting = e.setting ? e.setting: null;
		t.data = e.data ? e.data: null;
		t.treeObj = null;
		t.drawCallback = e.drawCallback ? e.drawCallback: null;
		t.selectNodeId = e.selectNodeId ? e.selectNodeId: null;
		t.selectNode = function(e, a) {
			$(e).each(function(e, a) {
				if (a.id == t.selectNodeId) {
					t.treeObj.selectNode(a);
					t.treeObj.setting.callback.onClick(null, a.tId, a);
					return false
				} else {
					if (a.children && a.children.length > 0) {
						t.selectNode(a.children)
					}
				}
			})
		};
		t.create = function() {
			$.fn.zTree.init(t.container, t.setting, t.data);
			if (t.drawCallback) {
				t.treeObj = $.fn.zTree.getZTreeObj($(t.container).attr("id"));
				t.drawCallback(t.treeObj)
			}
			if (t.selectNodeId) {
				t.treeObj = $.fn.zTree.getZTreeObj($(t.container).attr("id"));
				t.selectNode(t.treeObj.getNodes(), t.treeObj)
			}
		};
		t.create();
		return t
	};
	base.steps = function(e) {
		var t = {};
		t.data = e.data ? e.data: null;
		t.container = e.container ? e.container: null;
		t.width = 0;
		t.header = {};
		t.body = {};
		t.footer = {};
		t.buttonbar = null;
		t.animate = e.animate == false ? false: true;
		t.tipSize = e.tipSize ? e.tipSize: 30;
		t.color = e.color ? e.color: "#039bda";
		t.showNumber = e.showNumber == false ? false: true;
		t.buttons = e.buttons ? e.buttons: null;
		t.lineHeight = e.lineHeight ? e.lineHeight: 10;
		t.buttonGroupToggle = e.buttonGroupToggle ? e.buttonGroupToggle: null;
		if (t.container) {
			t.width = $(t.container).width()
		}
		t.currentStep = e.currentStep ? e.currentStep: 0;
		if (t.currentStep > t.data.length) {
			t.currentStep = t.data.length
		}
		t.create = function() {
			$(t.container).html("");
			if (t.data && t.data.length > 0) {
				t.setHeader();
				t.setBody();
				t.setFooter()
			}
		};
		t.setHeader = function(e) {
			t.header.element = document.createElement("div");
			$(t.header.element).addClass("ui-steps-header");
			$(t.container).append(t.header.element);
			t.header.createStepbar = function() {
				t.header.stepbar = document.createElement("ul");
				$(t.header.stepbar).addClass("ui-steps-stepbar");
				$(t.header.element).append(t.header.stepbar);
				t.header.createItem = function(e, a) {
					var r = document.createElement("li");
					$(t.header.stepbar).append(r);
					$(r).css("width", t.width / t.data.length);
					var n = document.createElement("div");
					$(n).attr("type", "label");
					$(n).html(e.label);
					$(r).append(n);
					var o = document.createElement("div");
					$(o).attr("type", "tip");
					$(o).css("width", t.tipSize);
					$(o).css("height", t.tipSize);
					if (t.showNumber) {
						$(o).html(a + 1)
					}
					$(o).css("line-height", t.tipSize + "px");
					$(o).css("font-size", t.tipSize / 3 * 2);
					$(r).append(o)
				};
				$(t.data).each(function(e, a) {
					t.header.createItem(a, e)
				})
			};
			t.header.createLinebar = function() {
				t.header.linebar = document.createElement("div");
				$(t.header.linebar).addClass("ui-steps-linebar");
				$(t.header.linebar).css("width", t.width / t.data.length * (t.data.length - 1));
				$(t.header.linebar).css("margin-left", t.width / t.data.length / 2);
				$(t.header.linebar).css("margin-right", t.width / t.data.length / 2);
				$(t.header.linebar).css("height", t.lineHeight);
				$(t.header.element).append(t.header.linebar);
				$(t.header.linebar).css("top", -t.tipSize + t.lineHeight);
				t.header.line = document.createElement("div");
				$(t.header.line).addClass("ui-steps-line");
				$(t.header.linebar).append(t.header.line)
			};
			t.header.step = function(e) {
				var a = $(t.header.linebar).width() / (t.data.length - 1) * t.currentStep;
				if (t.animate) {
					$(t.header.line).animate({
						width: a
					})
				} else {
					$(t.header.line).css("width", a)
				}
				$(t.header.stepbar).find("li").removeClass("active");
				$(t.header.stepbar).find("li").slice(0, t.currentStep + 1).addClass("active")
			};
			t.header.back = function() {
				if (t.currentStep <= 0) {
					return
				}
				t.currentStep = t.currentStep - 1;
				t.header.step(true)
			};
			t.header.forward = function() {
				if (t.currentStep == t.data.length - 1) {
					return
				}
				t.currentStep = t.currentStep + 1;
				t.header.step(false)
			};
			t.header.createStepbar();
			t.header.createLinebar();
			t.header.step()
		};
		t.getStep = function() {
			return t.currentStep
		};
		t.getStepCount = function() {
			return t.data.length
		};
		t.toStep = function(e) {
			if (e < 0) {
				e = 0
			}
			if (e > t.data.length - 1) {
				e = t.data.length - 1
			}
			if (e == t.currentStep) {
				return
			}
			if (e < t.currentStep) {
				t.currentStep = e;
				t.header.step();
				t.body.step();
				t.footer.check()
			} else {
				t.currentStep = e;
				t.header.step();
				t.body.step();
				t.footer.check()
			}
		};
		t.setBody = function() {
			t.body.element = document.createElement("div");
			$(t.body.element).addClass("ui-steps-body");
			$(t.container).append(t.body.element);
			t.body.createCarousel = function() {
				t.body.carouselbar = document.createElement("ul");
				$(t.body.carouselbar).addClass("ui-steps-carouselbar");
				$(t.body.element).append(t.body.carouselbar);
				$(t.body.carouselbar).css("width", $(t.container).width() * t.data.length);
				$(t.data).each(function(e, a) {
					t.body.createCarouselItem(a, e)
				})
			};
			t.body.createCarouselItem = function(e, a) {
				var r = document.createElement("li");
				$(t.body.carouselbar).append(r);
				$(r).html(e.content);
				$(r).css("width", $(t.container).width());
				if (a == t.currentStep) {
					$(r).addClass("active")
				}
				if (e.contentToggle) {
					$(r).html($(e.contentToggle).children().clone());
					$(e.contentToggle).remove()
				}
				if (e.contentUrl) {
					base.loadPage({
						container: r,
						url: e.contentUrl,
						callback: o.callback ? o.callback: null
					})
				}
				if (e.content) {
					$(r).html(e.content)
				}
				if (e.callback) {
					e.callback(t)
				}
			};
			t.body.step = function(e) {
				var a = -t.width * t.currentStep;
				if (t.animate) {
					$(t.body.carouselbar).animate({
						marginLeft: a
					})
				} else {
					$(t.body.carouselbar).css("margin-left", a)
				}
				$(t.body.carouselbar).children("li").removeClass("active");
				$(t.body.carouselbar).children("li:eq(" + t.currentStep + ")").addClass("active")
			};
			t.body.back = function() {
				if (t.currentStep < 0) {
					return
				}
				t.body.step(true)
			};
			t.body.forward = function() {
				if (t.currentStep == t.data.length) {
					return
				}
				t.body.step(false)
			};
			t.body.createCarousel();
			t.body.step()
		};
		t.back = function(e) {
			if (e) {
				if (!e(t)) {
					return
				}
			}
			t.header.back();
			t.body.back();
			t.footer.check()
		};
		t.forward = function(e) {
			if (e) {
				if (!e(t)) {
					return
				}
			}
			t.header.forward();
			t.body.forward();
			t.footer.check()
		};
		t.setFooter = function() {
			if (t.buttonGroupToggle) {
				t.footer.element = $(t.buttonGroupToggle)
			} else {
				t.footer.element = document.createElement("div");
				$(t.footer.element).addClass("ui-steps-footer");
				$(t.container).append(t.footer.element)
			}
			t.footer.createButton = function() {
				if (t.buttons && t.buttons.length > 0) {
					$(t.buttons).each(function(e, a) {
						var r = null;
						var n = a.type ? " " + a.type: "";
						var o = a.cls ? a.cls: "btn btn-info" + n;
						var l = null;
						switch (a.type) {
						case "back":
							r = a.label ? a.label: "上一步";
							l = function(e) {
								var r = a.callback ? a.callback: null;
								t.back(r)
							};
							break;
						case "forward":
							r = a.label ? a.label: "下一步";
							l = function(e) {
								var r = a.callback ? a.callback: null;
								t.forward(r)
							};
							break;
						case "confirm":
							r = a.label ? a.label: "确定";
							l = function(e) {
								if (a.callback) {
									if (!a.callback(e, t)) {
										return
									}
								}
							};
							break;
						default:
							r = a.label ? a.label: null;
							l = function(e) {
								if (a.callback) {
									if (!a.callback(e, t)) {
										return
									}
								}
							};
							break
						}
						base.form.button({
							container: t.footer.element,
							label: r,
							cls: o,
							clickEvent: l
						})
					})
				}
			};
			t.footer.check = function(e) {
				var a = t.getStepCount();
				if (t.currentStep == 0) {
					$(t.footer.element).find(".back").addClass("disabled");
					$(t.footer.element).find(".back").hide();
					$(t.footer.element).find(".forward").removeClass("disabled");
					$(t.footer.element).find(".forward").show();
					$(t.footer.element).find(".confirm").addClass("disabled");
					$(t.footer.element).find(".confirm").hide()
				} else if (t.currentStep == a - 1) {
					$(t.footer.element).find(".back").removeClass("disabled");
					$(t.footer.element).find(".back").show();
					$(t.footer.element).find(".forward").addClass("disabled");
					$(t.footer.element).find(".forward").hide();
					$(t.footer.element).find(".confirm").removeClass("disabled");
					$(t.footer.element).find(".confirm").show()
				} else {
					$(t.footer.element).find(".back").removeClass("disabled");
					$(t.footer.element).find(".back").show();
					$(t.footer.element).find(".forward").removeClass("disabled");
					$(t.footer.element).find(".forward").show();
					$(t.footer.element).find(".confirm").addClass("disabled");
					$(t.footer.element).find(".confirm").hide()
				}
			};
			t.footer.createButton();
			t.footer.check()
		};
		t.create();
		return t
	};
	/**获取选中的checkbox或radio**/
	base.getChecks = function(name){
    	var cbs = $("input[name='"+name+"']");
    	var ary = [];
    	$(cbs).each(function(i,o){
    		if($(o).is(':checked')) {
    			ary.push($(o).val());
    		}
    	});
    	return ary;
	};
	/*自动填充form表单*/
	base.form.init = function(dataMap, form) {
         for(var key in dataMap) {
            var e = $(form).find("[name='" + key + "']"); 
            if(e.length == 0) { 
               continue; 
            }
            var type = e.attr("type");
            if(type) {
               type = type.toLowerCase();
            }
            switch(e[0].nodeName=="INPUT"&&type) {
               case "checkbox":
               case "radio":
                  base.setChecks(dataMap[key], e.attr("name"));
                  break;

               case "hidden":
               case "text":
                  e.val(dataMap[key]);
                  break;

               default:
                  switch(e[0].tagName.toLowerCase()) {
                     case "select":
                     case "textarea":
                        e.val(dataMap[key]);
                        break;

                     default:
                        e.text(dataMap[key]);
                        break;
                  }
                  break;
            }
         } 
     }; 
     /**文件上传**/
	base.form.fileUpload = function(option){
		var self = {};
		self.url = option.url?option.url:null;
		self.success = option.success?option.success:null;
		self.error = option.error?option.error:null;
		self.id = option.id?option.id:null;
		self.params = option.params?option.params:null;
		self.dataType = option.dataType?option.dataType:"json";
		self.create = function(){
			if(!self.url||!self.id){return;}
			require(["fileUpload"],function(){
				var formId = 'jUploadForm' + self.id;
				var oldForm = $('#'+formId);  
				if(oldForm.length>0){  
					oldForm.remove();  
				};
				jQuery.ajaxFileUpload({ 
					url : self.url,
					secureuri : false,
					fileElementId : self.id,
					dataType : self.dataType,
					data:self.params,
					success : function(data, status){
						if(self.success){
							self.success(data);
						}
						self.clear();
					},
					error:function(data, status,e){
						if(self.error){
							self.error(data);
						}
						self.clear();
					}
				});
			});
		};
		self.clear = function(){
			var file = $(self.id) 
			file.after(file.clone().val("")); 
			file.remove(); 
		};
		self.create();
	};
   return base
});