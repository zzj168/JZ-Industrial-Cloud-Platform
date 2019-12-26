//拿到window doc和body
var jboltWindow=$(window);
var jboltDocument=$(document);
var jboltBody=$("body");
var jboltWindowHeight=jboltWindow.height();
//定义引入界面是否启用pjax
var mainPjaxContainerId='#jbolt-container';
var mainPjaxContainer=$(mainPjaxContainerId);
//summernote组件默认上传地址 JBOlt内封装
var summernote_img_uploadurl="/admin/summernote/upload/image/";
//是否引入启用Select2
var hasImportSelect2=false;
var g = function (id) {
    return "string" == typeof id ? document.getElementById(id) : id;
};

var isOk=function(obj){
	return obj&&obj.length>0;
}
/**
 * 图片查看器
 */
var ImageViewerUtil={
		initViewers:function(viewers){
			if(!isOk(viewers)){return false;}
			var len=viewers.length;
			for(var i=0;i<len;i++){
				this.initViewer(viewers.eq(i));
			}
		},
		init:function(parentEle){
			 var parent=getRealJqueryObject(parentEle);
			 if(!isOk(parent)){return false;}
			 var viewers=parent.find("[data-imgviewer]");
			 if(!isOk(viewers)){return false;}
			 this.initViewers(viewers);
		},
		initViewer:function(viewer){
			var isImg=viewer[0].tagName.toLowerCase()=="img";
			if(isImg){
				var orignurl=viewer.data("orignurl");
				if(orignurl){
					viewer.viewer({navbar:false,url:"data-orignurl"});
				}else{
					viewer.viewer({navbar:false});
				}
			}else{
				var useorign=viewer.data("useorign");
				if(useorign){
					viewer.viewer({url:"data-orignurl"});
				}else{
					viewer.viewer();
				}
			}
		}
}
/**
 * 处理autocomplate组件的items
 * @param column_attr
 * @returns
 */
function processAutocompleteItem(data,column_attr){
	var text="";
	if(column_attr.indexOf(",")==-1){
		text=data[column_attr];
		if(!text&&(text=="undefined"||text==undefined)){
			if(column_attr!="text"){
				text=data["text"];
				if(!text&&(text=="undefined"||text==undefined)){
					text=data["name"];
					if(!text&&(text=="undefined"||text==undefined)){
						text=data["title"];
					}
				}
			}else{
				if(!text&&(text=="undefined"||text==undefined)){
					text=data["name"];
					if(!text&&(text=="undefined"||text==undefined)){
						text=data["title"];
					}
				}
			}
			
		}
		return "<span style='width:100%;'>"+text+"</span>";
	}
	var attrs=column_attr.split(",");
	var t;
	for(var i in attrs){
		t=data[attrs[i]];
		text=text+"<span>"+(t?t:"-")+"</span>";
	}
	return text;
}
/**
 *自动关键词查询检索 完成组件封装
 */
var AutocompleteUtil={
		initInputs:function(inputs){
			if(!isOk(inputs)){return false;}
			var len=inputs.length;
			for(var i=0;i<len;i++){
				this.initInput(inputs.eq(i));
			}
		},
	init:function(parentEle){
		 var parent=getRealJqueryObject(parentEle);
		 if(!isOk(parent)){return false;}
		 var inputs=parent.find("input[data-autocomplete]");
		 if(!isOk(inputs)){return false;}
		 this.initInputs(inputs);
	},defaultFormat:function(data,column_attr){
		return processAutocompleteItem(data,column_attr);
	},
	processGetSyncEditor:function(input,defaultValueAttr){
		var syncEditorId=input.data("sync-editor-id");
		if(!syncEditorId){return  null;}
		var syncEditorType=input.data("sync-editor-type");
		var syncAttr=input.data("sync-attr");
		var editorInput=document.createElement("input");
		var $ei=$(editorInput);
		$ei.data("editor-id",syncEditorId);
		$ei.data("editor-type",syncEditorType);
		$ei.data("value-attr",syncAttr?syncAttr:defaultValueAttr);
		return  $ei;
	},
	processGetAutocomplateHiddens:function(hiddenInputIds){
		var result=new Array();
		if(hiddenInputIds){
			if(hiddenInputIds.indexOf(",")==-1){
				result.push($("#"+hiddenInputIds));
			}else{
				var arr=hiddenInputIds.split(",");
				for(var i in arr){
					result.push($("#"+arr[i]));
				}
			}
		}
		return result;
		
	},changeHiddenInputValue:function(input,data,selectValueDefault){
		var valueAttr=input.data("value-attr");
		var val="";
		if(valueAttr){
			if(valueAttr=="value"){
				val=val+(selectValueDefault?selectValueDefault:"");
			}else{
				val=val+data[valueAttr];
			}
		}else{
			val=val+(selectValueDefault?selectValueDefault:"");
		}
		
		var editorType=input.data("editor-type");
		if(editorType){
			var inputId=input.data("editor-id");
			if(editorType=="neditor"){
				UE.getEditor(inputId).execCommand('insertHtml', val);
			}else if(editorType=="summernote"){
				$('#'+inputId).summernote('code', val);
			}else{
				input.val(val);
			}
		}else{
			input.val(val);
		}
		
	},changeHiddenInputsValue:function(inputs,data,selectValueDefault){
		if(isArray(inputs)){
			for(var i in inputs){
				this.changeHiddenInputValue(inputs[i],data,selectValueDefault);
			}
		}else{
			this.changeHiddenInputValue(inputs,data,selectValueDefault);
		}
		
	},clearHiddenInputsValue:function(inputs){
		if(isArray(inputs)){
			for(var i in inputs){
				inputs[i].val("");
			}
		}else{
			inputs.val("");
		}
	},initInput:function(input){
		var url=input.data("url");
		if(!url){
			LayerMsgBox.alert("autocomplete组件未设置data-url属性",2);
			return false;
		}
		var hiddenInputId=input.data("hiddeninput");
		var synceditor_id=input.data("sync-editor-id");
		if(!hiddenInputId&&!synceditor_id){
			LayerMsgBox.alert("autocomplete组件未设置data-hiddeninput或data-sync-editor-id属性,需关联指定的隐藏域或者editor",2);
			return false;
		}
		var that=this,
		syncInputArray=new Array(),
		width=input.data("width"),
		widthAuto=false,
		text_attr=input.data("text-attr"),
		value_attr=input.data("value-attr"),
		column_attr=input.data("column-attr"),
		formatHandler=input.data("format-handler"),
		handler=input.data("handler"),
		limit=input.data("limit");
		if(!limit){
			limit=100;
		}
		if(!width){
			width=parseInt(input.css("width"));
			widthAuto=true;
		}
		if(!text_attr){
			text_attr="text";
		}
		if(!value_attr){
			value_attr="value";
		}
		if(!column_attr){
			column_attr=text_attr;
		}
		if(column_attr.indexOf(",")!=-1&&widthAuto){
			var arr=column_attr.split(",");
			var autoW=(arr.length+1)*90;
			if(autoW>width){
				width=autoW;
			}
		}
		
		var hiddenInput=that.processGetAutocomplateHiddens(hiddenInputId),
		editor=that.processGetSyncEditor(input,value_attr);
		if(editor){
			hiddenInput.push(editor);
		}
		if(hiddenInput.length==0){
			LayerMsgBox.alert("请关联指定的正确的隐藏域或者editor",2);
			return false;
		}
		
		//开始绑定
		input.autocomplete(url,{
		    minChars:0,
		    width:width,
		    scrollHeight:300,
		    matchContains:true,
		    autoFill: false,
		    matchSubset:false,
		    mustMatch:false,
		    dataType: 'json',
		    max:limit,
		    parse: function(res) {
		      var rows = [];
		      if(!res || (!res.data)){
		        return rows;
		      }
		      
		      var datas=res.data,
		      size=datas.length,
		      text,value;
		      for(var i=0;i<size;i++){
		    	text=processOptionText(datas[i],text_attr);
				value=processOptionValue(datas[i],value_attr);
		        rows[rows.length] = {
		          data:datas[i],       //下拉框显示数据格式
		          value:value,   //选定后实际数据格式
		          result:text//选定后输入框显示数据格式
		        };
		      }
		      return rows;
		    },
		    formatItem: function(row, i, max) {
		    	return that.defaultFormat(row,column_attr);
		    }
		  }).result(function(e,data,value,sec){
			  that.changeHiddenInputsValue(hiddenInput,data,value);
			  if(handler){
					var exe_handler=eval(handler);
					if(exe_handler&&typeof(exe_handler)=="function"){
						exe_handler(input,hiddenInput,value);
					}
				}
			  
		  }).bind("unmatch", function(){
			  //取消选择的时候 清空hidden
			  input.val("");
			  that.clearHiddenInputsValue(hiddenInput);
			  if(handler){
					var exe_handler=eval(handler);
					if(exe_handler&&typeof(exe_handler)=="function"){
						exe_handler(input,hiddenInput,value);
					}
				}
		  }).blur(function(){
		   	if(isArray(hiddenInput)){
		   		var clear=true,hinput;
		   		for(var i in hiddenInput){
		   			hinput=hiddenInput[i];
		   			if($.trim(hinput.val())){
		   				clear=false;
				   	}
		   		}
		   		if(clear){
		   			input.val("");
		   		}
		   	}else{
		   		if(hiddenInput.val()==""){
			   		input.val("");
			   	}
		   	}
			}).on("input",function(){
				that.clearHiddenInputsValue(hiddenInput);
			 }); 
		
	}
}






/**
 * JBoltLayer组件
 * 炫富从左侧或者右侧滑出
 */
var JBoltLayerUtil={
		init:function(){
			var that=this;
			jboltBody.on("click","[data-jboltlayertrigger]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				that.open($(this));
				return false;
			}).on("click","[data-closejboltlayer]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				that.close();
				return false;
			}).on("click",".jbolt_layer_portal",function(e){
				  e.stopPropagation();
			}).on("click",".jbolt_admin_main",function(e){
					that.close();
			});
		},open:function(trigger){
			var url=trigger.attr("href");
			if(!url){
				url=trigger.data("url");
			}
			if(!url){
				LayerMsgBox.alert("jboltlayertrigger未设置URL",2);
				return false;
			}
			
			var existLayer=$("#jbolt_layer");
			if(existLayer&&existLayer.length==1){
				existLayer.remove();
			}
			var triggerid=trigger.attr("id");
			if(!triggerid){
				triggerid=randomId();
				trigger.attr("id",triggerid);
			}
			var top=trigger.data("top");
			if(!top&&top!=0){
				top=0;
			}
			var height=trigger.data("height");
			if(height){
				if(top==0){
					top=jboltWindowHeight-height;
					if(top<0){
						top=0;
					}
				}
			}
			var hasClose=true;
			if(trigger[0].hasAttribute("data-noclose")){
				hasClose=false;
			}
			
			var noMask=trigger[0].hasAttribute("data-nomask");
			
			var layer='<div data-triggerid="'+triggerid+'" class="jbolt_layer '+(noMask?" nomask ":"")+(hasClose?"":" noclose ")+'" data-closejboltlayer id="jbolt_layer"><div class="jbolt_layer_portal"  style="top:'+top+'px;"   data-ajaxportal  data-url="'+url+'"></div></div>';
			jboltBody.append(layer);
			if(hasClose){
				$("#jbolt_layer").append('<a data-closejboltlayer  style="top:'+top+'px;"  class="jbolt_layer_close">&times;</a>');
			}
			this.load();
			this.showOpenAnimate(trigger);
		},load:function(url){
			var existLayer=$("#jbolt_layer");
			if(existLayer&&existLayer.length==1){
				var layerPortal=existLayer.find(".jbolt_layer_portal");
				if(url){
					layerPortal.ajaxPortal(true,url,true);
				}else{
					layerPortal.ajaxPortal();
				}
			}
		},close:function(){
			var that=this;
			var existLayer=$("#jbolt_layer");
			if(existLayer&&existLayer.length==1){
				var triggerid=existLayer.data("triggerid");
				var jboltlayertrigger=$("#"+triggerid);
				var confirmAttr=jboltlayertrigger.attr("data-confirm");
				var hasConfirm=(typeof(confirmAttr)!="undefined");
				if(hasConfirm){
					var confirm=jboltlayertrigger.data("confirm");
					if(!confirm){
						confirm="确认关闭?";
					}
					LayerMsgBox.confirm(confirm,function(){
						that.showCloseAnimateAndRemove(jboltlayertrigger,existLayer);
					})
				}else{
					that.showCloseAnimateAndRemove(jboltlayertrigger,existLayer);
				}
				
			}
		},showCloseAnimateAndRemove:function(trigger,existLayer){
			var dir=trigger.data("dir");
			if(!dir){
				dir="right";
			}
			var layerPortal=existLayer.find(".jbolt_layer_portal");
			var layerClose=existLayer.find(".jbolt_layer_close");
			if(dir=="right"){
				layerPortal.css({
					width:"0px",
					right:"0px"
				});
				layerClose.css({
					width:"0px",
					right:"0px"
				});
			}else{
				layerPortal.css({
					left:"0px",
					width:"0px"
				});
				layerClose.css({
					left:"0px",
					width:"0px"
				});
			}
			
			existLayer.fadeOut(300,function(){
				existLayer.remove();
			});
			 
		},showOpenAnimate:function(trigger){
			var existLayer=$("#jbolt_layer");
			if(existLayer&&existLayer.length==1){
				existLayer.fadeIn(200);
				var layerPortal=existLayer.find(".jbolt_layer_portal");
				var layerClose=existLayer.find(".jbolt_layer_close");
				var dir=trigger.data("dir");
				if(!dir){
					dir="right";
				}
				
				//处理遮罩
				if(existLayer.hasClass("nomask")){
					existLayer.addClass(dir);
				}
				
				var width=trigger.data("width");
				var winWidth=jboltWindow.width();
				if(!width){
					width=winWidth/2;
					if(width<900){
						width=900;
					}else if(width>1360){
						width=1360;
					}
				}
				
				if(dir=="right"){
					var left=(winWidth-width)+"px";
					layerPortal.css({
						width:width,
						right:"0px"
					});
					setTimeout(function(){
						layerClose.css({
							right:width
						}).fadeIn(200);
					}, 300);
					
				}else{
					var layerWidth=width+"px";
					layerPortal.css({
						left:"0px",
						width:layerWidth
					});
					setTimeout(function(){
						layerClose.css({
							left:width
						}).fadeIn(200);
					}, 300);
				}
				
			}
		}
}

function getMousePos(event) {
    var e = event || window.event;
    var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
    var scrollY = document.documentElement.scrollTop || document.body.scrollTop;
    var x = e.pageX || e.clientX + scrollX;
    var y = e.pageY || e.clientY + scrollY;
    //alert('x: ' + x + '\ny: ' + y);
    return { 'x': x, 'y': y };
}


/**
 * Jbolt封装 tabs选项卡组件
 */
var jbolt_tabbar=$("#jbolt_tabbar");
var jbolt_tabs_container=$("#jbolt_tabs");
var jbolt_tabs_array=[];
/**
 * tab组件的右键菜单配置
 */
var JBoltTabContextifyOptions = {
		dividerClass:"divider",
		items:[
		  {header: '<i class="fa fa-th mr-1"></i>可选操作'},
		  {text: '<i class="fa fa-close mr-1"></i>关闭这个', onclick: function(e) {
			  JBoltTabUtil.closeJboltTab(e.data.key);
		  }},
		  {text: '<i class="fa fa-refresh mr-1"></i>刷新', onclick: function(e) {
			  JBoltTabUtil.showJboltTab(e.data.key,false,refreshPjaxContainer);
		  }},
		  {divider: true},
		  {text: '<i class="fa fa-window-close-o mr-1"></i>关闭其它', onclick: function(e) {
			  JBoltTabUtil.closeOtherJboltTab(e.data.key);
		  }},
		  {text: '<i class="fa fa-toggle-left mr-1"></i>关闭左侧', onclick: function(e) {
			  JBoltTabUtil.closeAllLeftJboltTab(e.data.key);
		  }},
		  {text: '<i class="fa fa-toggle-right mr-1"></i>关闭右侧', onclick: function(e) {
			  JBoltTabUtil.closeAllRightJboltTab(e.data.key);
		  }},
		  {text: '<i class="fa fa-window-close mr-1"></i>关闭所有', onclick: function(e) {
			  JBoltTabUtil.closeAllJboltTab();
		  }},
		]};
/**
 * 选项卡组件
 */
var JBoltTabUtil={
		init:function(){
			var that=this;
			that.createJboltTabContextMenu();
			that.initAdminLeftNavTabsEvent();
			that.initTabsEvent();
			that.initTabsTriggerEvent();
			
		},
		initTabsTriggerEvent:function(){
			var that=this;
			jboltBody.on("click","[data-tabtrigger]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var nav=$(this);
				  var url=nav.attr("href");
				  if(!url){
					  url=nav.data("url");
				  }
				  var target=nav.attr("target");
				  if(!target){
					  target=nav.data("target");
				  }
				  if(url&&url.indexOf("javascript")==-1){
					  if(target&&target=="_self"){
						  currentTabContentRedirectWithAjaxPortal(url);
					  }else{
						  var key=nav.data("key");
						  var text=nav.data("title");
						  if(!text){
							  text=nav.text();
						  }
						  var currentTab=that.getCurrentTab();
						  if(currentTab){
							  var triggerKey=currentTab.data("key");
							  that.addJboltTab(key,url,text,triggerKey);
						  }else{
							  that.addJboltTab(key,url,text);
						  }
						  
					  }
					  
				  }
			});
		},initAdminLeftNavTabsEvent:function(){
			var that=this;
			jboltBody.on("click",".jbolt_admin_left_navs a:not([target])",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var nav=$(this);
				  var url=nav.attr("href");
				  if(url&&url.indexOf("javascript")==-1){
					  var key=nav.data("key");
					  var text=nav.text().replace("├","");
					  that.addJboltTab(key,url,text);
				  }
			});
		},initTabsEvent:function(){
			var that=this;
			//点击标签页 切换显示
			jboltBody.on("click","ul.jbolt_tabs>li",function(e){
				e.preventDefault();
				e.stopPropagation();
				var key=$(this).data("key");
				that.showJboltTab(key,true);
			}).on("click","ul.jbolt_tabs>li>i.close",function(e){
				e.preventDefault();
				e.stopPropagation();
				var key=$(this).parent().data("key");
				that.closeJboltTab(key);
			}).on("click","#jbolt_tabbar .jbolt_tab_left",function(e){
				e.preventDefault();
				e.stopPropagation();
				that.jboltTabToLeft();
			}).on("click","#jbolt_tabbar .jbolt_tab_right",function(e){
				e.preventDefault();
				e.stopPropagation();
				that.jboltTabToRight();
			}).on("click","#jboltTabContextMenu>a",function(e){
				e.preventDefault();
				e.stopPropagation();
				var func=$(this).data("func");
				var key=$(this).data("key");
				switch (func) {
					case "close":
						JBoltTabUtil.closeJboltTab(key);
						break;
					case "closeAll":
						JBoltTabUtil.closeAllJboltTab();
						break;
					case "closeLeft":
						JBoltTabUtil.closeAllLeftJboltTab(key);
						break;
					case "closeRight":
						JBoltTabUtil.closeAllRightJboltTab(key);
						break;
					case "closeOther":
						JBoltTabUtil.closeOtherJboltTab(key);
						break;
					case "refresh":
						JBoltTabUtil.showJboltTab(key,false,refreshPjaxContainer);
						break;
				}
				that.hideJboltContextMenu();
				return false;
			}).on("dblclick","ul.jbolt_tabs>li",function(e){
				e.preventDefault();
				e.stopPropagation();
				var key=$(this).data("key");
				that.showJboltTab(key,true);
				var tabContent=mainPjaxContainer.find(" div#tab_content_"+key);
				if(tabContent&&tabContent.length==1){
					LayerMsgBox.confirm("确认刷新此选项卡内容？",function(){
						tabContent.ajaxPortal(true);
					});
				}
			}).on("contextmenu","ul.jbolt_tabs>li",function(e){
				e.preventDefault();
				e.stopPropagation();
				var pos=getMousePos(e);
				that.showTabContextmenu($(this),pos);
				return false;
			}).on("click",'#jboltTabContextMenu',function(e){
				e.preventDefault();
				e.stopPropagation();
				return false;
			}).on("click",function(){
				that.hideJboltContextMenu();
			});
		},
		createJboltTabContextMenu:function(){
			var jboltTabContextMenu=$("#jboltTabContextMenu");
			if(!jboltTabContextMenu||jboltTabContextMenu.length==0){
				var html='<div id="jboltTabContextMenu" class="dropdown-menu">'+
					'<span class="dropdown-item-text"><i class="fa fa-th mr-1"></i>可选操作</span>'+
					'<div class="dropdown-divider"></div>'+
					'<a class="dropdown-item" data-func="close" href="javascript:void(0)"><i class="fa fa-close mr-1"></i>关闭这个</a>'+
					'<a class="dropdown-item" data-func="refresh" href="javascript:void(0)"><i class="fa fa-refresh mr-1"></i>刷新</a>'+
					'<a class="dropdown-item" data-func="closeOther" href="javascript:void(0)"><i class="fa fa-window-close-o mr-1"></i>关闭其它</a>'+
					'<a class="dropdown-item" data-func="closeLeft" href="javascript:void(0)"><i class="fa fa-toggle-left mr-1"></i>关闭左侧</a>'+
					'<a class="dropdown-item" data-func="closeRight" href="javascript:void(0)"><i class="fa fa-toggle-right mr-1"></i>关闭右侧</a>'+
					'<a class="dropdown-item" data-func="closeAll" href="javascript:void(0)"><i class="fa fa-window-close mr-1"></i>关闭所有</a>'+
					'</div>';
				jboltBody.append(html);
			}
		},
		hideJboltContextMenu:function(){
			var jboltTabContextMenu=$("#jboltTabContextMenu");
			if(jboltTabContextMenu&&jboltTabContextMenu.length==1){
				jboltTabContextMenu.hide();
			}
		},
		showTabContextmenu:function(tabEle,pos){
			var tab=$(tabEle);
			var key=tab.data("key");
			var jboltTabContextMenu=$("#jboltTabContextMenu");
			if(!jboltTabContextMenu||jboltTabContextMenu.length==0){
				this.createJboltTabContextMenu();
			}
			jboltTabContextMenu.data("key",key);
			jboltTabContextMenu.find("a").data("key",key);
			jboltTabContextMenu.css({
				top:pos.y,
				left:pos.x
			});
			jboltTabContextMenu.show();
		},
		/**
		 * 得到当前显示的tab
		 */
		getCurrentTab:function(){
			var tab=jbolt_tabs_container.find("li.active");
			if(tab&&tab.length==1){
				return tab;
			}
			return null;
		},
		/**
		 * 得到当前显示的tabcontent部分
		 */
		getCurrentTabContent:function(){
			var tabContent=mainPjaxContainer.find("div.jbolt_tabcontent.active");
			if(tabContent&&tabContent.length==1){
				return tabContent;
			}
			return null;
		},
		/**
		 * 判断是否已经存在
		 * @param key
		 * @returns
		 */
		isJboltTabExist:function(key){
			var existLi=jbolt_tabs_container.find("li[data-key='"+key+"']");
			return existLi&&existLi.length==1
		},
		/**
		 * 添加一个不带创建content的tab 用于特殊情况初始化页面tab
		 */
		addJboltTabWithoutContentUrl:function(){
			var that=this;
			var jboltPage=mainPjaxContainer.find(".jbolt_page[data-key]");
			if(jboltPage&&jboltPage.length==1){
				var url=jboltPage.data("key");
				var nav=$(".jbolt_admin_left_navs a[href='"+url+"']");
				if(nav&&nav.length==1){
					var key=nav.data("key");
					var text=$.trim(nav.text().replace("├",""));
					that.addJboltTabWithoutContent(key,url,text);
				}else{
					//如果没有按照key找到nav 就找本身设置的text 作为tab的text
					var text=jboltPage.data("text");
					if(text){
						that.addJboltTabWithoutContent(key,url,text);
					}else{
						var dontchangeleftnav=jboltPage.data("dontchangeleftnav");
						if(dontchangeleftnav){
							//如果没有规定不能改变 就改一下
							var likeNavs=$(".jbolt_admin_left_navs a[href]");
							if(isOk(likeNavs)){
								var cunav,cuhref,cutext,maxlen,maxtext;
								likeNavs.each(function(){
									cunav=$(this);
									cuhref=cunav.attr("href");
									cutext=$.trim(cunav.text().replace("├",""));
									if(url.indexOf(cuhref)!=-1){
										if((!maxlen||!maxtext)||cuhref.length>maxlen){
											maxtext=cutext;
											maxlen=cuhref.length;
										}
									}
								});
								if(!maxtext){
									var h1=jboltPage.find(".jbolt_page_title h1");
									if(isOk(h1)){
										maxtext=h1.text();
									}
									if(!maxtext){
										maxtext=" ";
									}
								}
								that.addJboltTabWithoutContent(key,url,maxtext);
							}
						}
					}
				}
			}
			
		},
		/**
		 * 底层方法 
		 */
		addJboltTabWithoutContent:function(key,url,text){
			var that=this;
			var exist=that.isJboltTabExist(key);
			if(!exist){
				jbolt_tabs_container.append('<li id="tab_'+key+'" data-key="'+key+'" data-url="'+url+'"><span>'+text+'</span><i class="close">&times;</i></li>');
				var tabContent=mainPjaxContainer.find("div.jbolt_tabcontent");
				if(tabContent&&tabContent.length==1){
					tabContent.data("key",key);
					tabContent.data("url",url);
					tabContent.attr("id","tab_content_"+key);
				}
			}
			that.showJboltTab(key);
		},
		/**
		 * 初始化
		 */
		initTabContextMenuEvent:function(){
			$('#tab_'+key).tabContextMenu(JBoltTabContextifyOptions);
		},
		/**
		 * 添加一个tab选项卡
		 * @param key
		 * @param url
		 * @param text
		 * @param triggerTabKey 是从另一个tab过来的就要带着这个
		 */
		addJboltTab:function(key,url,text,triggerTabKey){
			var that=this;
			var exist=that.isJboltTabExist(key);
			if(exist){
				that.showJboltTab(key,true);
			}else{
				triggerTabKey=triggerTabKey?triggerTabKey:"";
				jbolt_tabs_container.append('<li id="tab_'+key+'" data-key="'+key+'" data-trigger-tab-key="'+triggerTabKey+'" data-url="'+url+'"><span>'+text+'</span><i class="close">&times;</i></li>');
				mainPjaxContainer.append('<div class="jbolt_tabcontent" id="tab_content_'+key+'" data-trigger-tab-key="'+triggerTabKey+'" data-ajaxportal data-key="'+key+'" data-url="'+url+'"></div>')
				mainPjaxContainer.find("div#tab_content_"+key).ajaxPortal(true);
				that.showJboltTab(key);
				that.jboltTabToRight();
			}
			
			
		},hideAllJboltTab:function(){
			jbolt_tabs_container.find("li.active").removeClass("active");
			mainPjaxContainer.find("div.jbolt_tabcontent.active").removeClass("active");
		},
		/**
		 * 在内页跳转后 将tab的url跟随content的替换掉
		 */
		changeUrlByContentPortal:function(){
			var currentTab=this.getCurrentTab();
			var currentTabContent=this.getCurrentTabContent();
			var url=currentTabContent.data("url");
			currentTab.data("url",url);
		},changeUrl:function(url){
			var currentTab=this.getCurrentTab();
			var currentTabContent=this.getCurrentTabContent();
			if(currentTab&&currentTab.length==1){
				currentTab.data("url",url);
				currentTabContent.data("url",url);
				currentTab.attr("data-url",url);
				currentTabContent.attr("data-url",url);
			}
		},
		/**
		 * 切换显示一个tab选项卡
		 * @param key
		 * @param processChange
		 * @returns
		 */
		showJboltTab:function(key,processChange,callback){
			var that=this;
			var li=jbolt_tabs_container.find("li#tab_"+key);
			if(li&&li.length==1){
				var isCurrent=li.hasClass("active");
				//如果当前正好active 就不用管了
				if(!isCurrent){
					//隐藏当前tab
					that.hideAllJboltTab();
					//激活需要显示的
					li.addClass("active");
					mainPjaxContainer.find("div#tab_content_"+key).addClass("active");
					openLeftNav(li.data("url"));
				}
				if(processChange){
					that.changeTabLocation();
				}
				if(callback){
					callback();
				}
			}
			
		},
		/**
		 * 将左侧导航的第一个弹出选项卡
		 */
		showFirstLeftNavTab:function(){
			var that=this;
			var firstNav=$(".jbolt_admin_left_navs a[data-hasurl]").first();
			if(firstNav&&firstNav.length==1){
				var url=firstNav.attr("href");
				if(url&&url.indexOf("javascript")==-1){
				  var key=firstNav.data("key");
				  var text=firstNav.text().replace("├","");
				  that.addJboltTab(key,url,text);
				}
			}
			
		},openTabByNav:function(nav){
			var url=nav.attr("href");
			if(url&&url.indexOf("javascript")==-1){
				var key=nav.data("key");
				var text=nav.text().replace("├","");;
				this.addJboltTab(key,url,text);
			}
		},getTabCount:function(){
			var lis=jbolt_tabs_container.find("li");
			if(lis&&lis.length>0){
				return lis.length;
			}
			return 0;
		},
		/**
		 * 关闭指定tab
		 * @param tab
		 */
		close:function(tab){
			
			this.closeJboltTab(tab.data("key"));
		},
		/**
		 * 关闭一个指定的Tab选项卡
		 * @param key
		 * @returns
		 */
		closeJboltTab:function(key){
			var that=this;
			var li=jbolt_tabs_container.find("li#tab_"+key);
			if(li&&li.length==1){
				var active=li.hasClass("active");
				if(active){
					that.changeToBrotherTab(li);
				}
				li.remove();
				mainPjaxContainer.find("div#tab_content_"+key).remove();
				
				var count=that.getTabCount();
				if(count==0){
					that.showFirstLeftNavTab();
				}
				
				if(!active){
					that.changeTabLocation();
				}
				
				
			}
		},
		/**
		 * 关闭其它
		 */
		closeOtherJboltTab:function(key){
			var that=this;
			that.showJboltTab(key);
			var lis=jbolt_tabs_container.find("li:not("+"#tab_"+key+")");
			if(lis&&lis.length>0){
				lis.each(function(){
					var li=$(this);
					var li_key=li.data("key");
					li.remove();
					mainPjaxContainer.find("div#tab_content_"+li_key).remove();
				});
			}
		},
		/**
		 * 关闭所有
		 */
		closeAllJboltTab:function(){
			jbolt_tabs_container.empty();
			mainPjaxContainer.empty();
			this.showFirstLeftNavTab();
		},
		/**
		 * 关闭左侧所有
		 */
		closeAllLeftJboltTab:function(key){
			var that=this;
			var self=jbolt_tabs_container.find("li#tab_"+key);
			var lis=self.prevAll("li");
			var selfIsActive=self.hasClass("active");
			//判断删除的里面有没有active的
			var hasActive=false;
			if(lis&&lis.length>0){
				lis.each(function(){
					var li=$(this);
					var li_key=li.data("key");
					if(li.hasClass("active")){
						hasActive=true;
					}
					li.remove();
					mainPjaxContainer.find("div#tab_content_"+li_key).remove();
				});
			}
			
			if(!selfIsActive&&hasActive){
				that.showJboltTab(key);
			}
		
		},
		/**
		 * 关闭右侧所有
		 */
		closeAllRightJboltTab:function(key){
			var that=this;
			var self=jbolt_tabs_container.find("li#tab_"+key);
			var lis=self.nextAll("li");
			var selfIsActive=self.hasClass("active");
			//判断删除的里面有没有active的
			var hasActive=false;
			if(lis&&lis.length>0){
				lis.each(function(){
					var li=$(this);
					var li_key=li.data("key");
					if(li.hasClass("active")){
						hasActive=true;
					}
					li.remove();
					mainPjaxContainer.find("div#tab_content_"+li_key).remove();
				});
			}
			
			if(!selfIsActive&&hasActive){
				that.showJboltTab(key);
			}
		
		},
		/**
		 * 切换到兄弟节点
		 */
		changeToBrotherTab:function(li){
			var that=this;
			var brother=li.prev();
			var exist=brother&&brother.length==1;
			if(!exist){
				brother=li.next();
				exist=brother&&brother.length==1;
			}
			if(exist){
				var key=brother.data("key");
				that.showJboltTab(key);
			}else{
				that.showFirstLeftNavTab();
			}
		},checkCanToLeft:function(){
			var jbolt_tabbar_width=jbolt_tabbar.width();
			var boxWidth=jbolt_tabs_container.outerWidth();
			if(boxWidth>jbolt_tabbar_width){
				//标签总长度大于容器长度 说明满足先决条件了 再去判断 显示长度
				var marginleft=Math.abs(parseInt(jbolt_tabs_container.css("margin-left")));
				var lwidth=Math.abs(parseInt(jbolt_tabbar_width-boxWidth));
				if(marginleft<lwidth){
					//如果左移距离小于差距 就可以左移
					return true;
				}
			}
			return false;
			
		},checkCanToRight:function(){
			var marginleft=Math.abs(parseInt(jbolt_tabs_container.css("margin-left")));
			return marginleft!=0;
		},
		/**
		 * 显示最左侧
		 * @returns
		 */
		jboltTabToLeft:function(){
			jbolt_tabs_container.css("margin-left","0px");
		},
		/**
		 * 显示最右侧
		 * @returns
		 */
		jboltTabToRight:function(){
			var jbolt_tabbar_width=jbolt_tabbar.width();
			var boxWidth=jbolt_tabs_container.outerWidth();
			if(boxWidth>jbolt_tabbar_width){
				var mleft=(jbolt_tabbar_width/2)-boxWidth;
				var leftLi=null;
				var len=0;
				var absmLeft=Math.abs(mleft);
				jbolt_tabs_container.find("li").each(function(){
					mleft=len*-1;
					len=len+$(this).outerWidth();
					if(len>=absmLeft){
						return false;
					}
				});
				jbolt_tabs_container.css("margin-left",mleft+"px");
			}
		},changeTabLocation:function(){
			var that=this;
			var jbolt_tabbar_width=jbolt_tabbar.width();
			var boxWidth=jbolt_tabs_container.outerWidth();
			if(boxWidth>jbolt_tabbar_width){
				//如果超长 才会动弹 拿到激活的tab 让它居中
				var tab=that.getCurrentTab();
				var marginleft=Math.abs(parseInt(jbolt_tabs_container.css("margin-left")));
				var len=0;
				var len1=0;
				jbolt_tabs_container.find("li").each(function(){
					var li=$(this);
					len1=len1+li.outerWidth();
					if(li.data("key")==tab.data("key")){
						return false;
					}else{
						len=len+li.outerWidth();
					}
				});
				if(marginleft==0){
					//说明没动弹 那就判断到激活的距离是不是在显示区域内
					if(len<=jbolt_tabbar_width){
						
					}else{
						var mleft=len*-1;
						jbolt_tabs_container.css("margin-left",mleft+"px");
					}
				}else{
					if(len1<=jbolt_tabbar_width){
						that.jboltTabToLeft();
					}else{
						var mml=(jbolt_tabbar_width/2)-boxWidth;
						var mleft=len*-1;
						if(len>Math.abs(parseInt(mml))){
							that.jboltTabToRight();
						}else{
							jbolt_tabs_container.css("margin-left",mleft+"px");
						}
					}
				}
				
			}else{
				that.jboltTabToLeft();
			}
		}
}

;(function($) {
    $.fn.extend({
        toJsonObject : function() {
            var o = {};
            var a = this.serializeArray();
            $.each(a, function() {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [ o[this.name] ];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
        }
    });

})(jQuery)


;(function($) {
    $.fn.extend({
        toggleContent : function(content) {
        	var html=this.html();
        	if(html&&html>0){
        		this.html("");
        	}else{
        		this.html(content);
        	}
        	
        }
    });

})(jQuery)


/**
 * 生成随机ID
 */
function randomId(){
	var ran=Math.random();
	ran=ran.toString().replace(".","");
	return new Date().getTime()+ran;
}

/**
 * switchBtn enableBtn
 */
var SwitchBtnUtil={
		initBtn:function(_btn){
			var that=this;
				  var src=_btn.attr("src");
				  if(!src){
					  //如果没设置src就是动态设置的 根据data-value
					  var value=_btn.data("value");
					  if(!value||value=="null"||value=="undefined"){
						  value=false;
					  }else{
						  value=true;
					  }
					  var style=_btn.data("style");
					  if(!style){
						  style="default";
					  }
					  var src="/assets/img/switch/"+style+"/"+(value?"on":"off")+".png";
					  _btn.attr("src",src);
				  }
				  
				  _btn.off("click").on("click",function(e){
					  e.preventDefault();
					  e.stopPropagation();
					  var doing=_btn.data("doing");
					  if(doing){
						  return false;
					  }
					  doing=true;
					  _btn.data("doing",true);
						var url=_btn.data("url");
						var confirm=_btn.data("confirm");
						if(url){
							if(confirm){
								LayerMsgBox.confirm(confirm,function(){
									that.switchIt(_btn);
								},function(){
									_btn.data("doing",false);
								});
							}else{
								that.switchIt(_btn);
							}
							
						}else{
							that.switchSuccessHandler(_btn);
//							LayerMsgBox.alert("组件未设置URL地址",2);
						}
				  });
		},
		initBtns:function(btns){
			if(!isOk(btns)){return false;}
			var that=this;
			var len=btns.length;
			for(var i=0;i<len;i++){
				that.initBtn(btns.eq(i));
			}
			
		},
		  init:function(parentEle){
			  var that=this;
			  var parent=getRealJqueryObject(parentEle);
			  if(!isOk(parent)){return false;}
			  var btns=parent.find("img[data-switchbtn]");
			  if(!isOk(btns)){return false;}
			  that.initBtns(btns);
		  },switchIt:function(_btn){
			  var that=this;
			  var url=_btn.data("url");
				LayerMsgBox.loading("正在执行...",10000);
				$.ajax({
					type:"post",
					url:url,
					timeout : 10000, //超时时间设置，单位毫秒
					dataType:"json",
					success:function(data){
						if(data.state=="ok"){
							that.switchSuccessHandler(_btn);
						}else{
							 _btn.data("doing",false);
							LayerMsgBox.alert(data.msg,2);
						}
					},
					error:function(){
						 _btn.data("doing",false);
						LayerMsgBox.alert("操作失败",2);
					}
				});
			  },switchSuccessHandler:function(_btn){
			    var src=_btn.attr("src");
				var handler=_btn.data("handler");
				var on=src.indexOf("off")!=-1;
				if(src.indexOf("off")!=-1){
					src=src.replace("off","on");
				}else{
					src=src.replace("on","off");
				}
				_btn.attr("src",src);
				LayerMsgBox.closeLoadingNow();
				if(handler){
					var exe_handler=eval(handler);
					if(exe_handler&&typeof(exe_handler)=="function"){
						LayerMsgBox.success("操作成功",300,function(){
							exe_handler(_btn,on);
							 _btn.data("doing",false);
						 });
					}else{
						LayerMsgBox.success("操作成功",300,function(){
							 _btn.data("doing",false);
						 });
					}
				}else{
					LayerMsgBox.success("操作成功",300,function(){
						 _btn.data("doing",false);
					 });
				}
				
				
				
				
		  }
}


/*var DragScrollElementUtil={
		init:function(id){
			$('#'+id).niceScroll({
			    cursorcolor: "#ccc",//#CC0071 光标颜色
			    cursoropacitymax: 1, //改变不透明度非常光标处于活动状态（scrollabar“可见”状态），范围从1到0
			    touchbehavior: false, //使光标拖动滚动像在台式电脑触摸设备
			    cursorwidth: "5px", //像素光标的宽度
			    cursorborder: "0", // 游标边框css定义
			    cursorborderradius: "5px",//以像素为光标边界半径
			    autohidemode: true //是否隐藏滚动条
			});
		}
}
var DragScrollUtil={
		init:function(parentId){
			  var that=this;
			  var eles=null;
			  if(parentId){
				  eles=$('#'+parentId).find("[data-dragscroll]")
			  }else{
				  eles=jboltBody.find("[data-dragscroll]");
			  }
			  if(eles&&eles.length>0){
				  for(var i in eles){
					  var id=eles[i].id;
					  if(!id){
						id=randomId();
						eles[i].id=id;
					  }
					  DragScrollElementUtil.init(id);
				  }
			  }
		}
} */
 
function processHiddenInput(inputName,hiddenInputId){
	var ids=getCheckedIds(inputName);
	$("#"+hiddenInputId).val(ids);
}
/**
 * checkbox工具类封装
 */
var CheckboxUtil={
		initCheckBoxEvent:function(ck,name,hiddenInputId,handler){
					if(handler){
					  var exe_handler=eval(handler);
					  if(exe_handler&&typeof(exe_handler)=="function"){
						  ck.find("input[type='checkbox'][name='"+name+"']").unbind("change").on("change",function(){
							  if(handler=="processHiddenInput"){
								  processHiddenInput(name,hiddenInputId);
							  }else{
								  var input=$(this);
								  exe_handler(input,input.is(":checked"));
							  }
						  });
					  }
				  }
		},
		initCheckboxs:function(checkboxs){
			if(!isOk(checkboxs)){return false;}
			var len=checkboxs.length;
			for(var i=0;i<len;i++){
				this.initCheckbox(checkboxs.eq(i));
			}
		},
		initCheckbox:function(ck){
			var that=this,
			handler=ck.data("handler"),
			name=ck.data("name"),
			value=ck.data("value")+"",
			defaultValue=ck.data("default")+"",
			hiddenInputId=ck.data("hiddeninput"),
			url=ck.data("url"),
			label=ck.data("label");
			 if(!value){value="";}else{value=value+""}
			  if(!defaultValue){defaultValue="";}else{defaultValue=defaultValue+""}
			  if(url){
				  that.insertDatas(ck,url,name,label,function(){
					  that.initCheckBoxEvent(ck,name,hiddenInputId,handler);
					  that.setChecked(name,value,defaultValue);
				  });
			  }else{
				  that.initCheckBoxEvent(ck,name,hiddenInputId,handler);
				  that.setChecked(name,value,defaultValue);
			  }
			
		 
		},
		  init:function(parentEle){
			  var parent=getRealJqueryObject(parentEle);
			  if(!isOk(parent)){return false;}
			  var checkboxs=parent.find("[data-checkbox]");
			  if(!isOk(checkboxs)){return false;}
			  this.initCheckboxs(checkboxs);
		  },insertDatas:function(ck,url,name,label,callback){
			  var that=this;
			  ck.empty();
			  
			  var width=ck.data("width");
			  var labelWidth="";
			  var radioWidth="";
			  if(width){
				  var arr=width.split(",");
				  labelWidth=arr[0];
				  radioWidth=arr[1];
			  }else{
				  labelWidth="100px";
				  radioWidth="col";
			  }
			  var html='';
			  if(label){
				 if(labelWidth.indexOf("px")!=-1){
					 html= '<label class="col-auto col-form-label" style="width:'+labelWidth+'">'+label+'</label>';
				 }else{
					 html= '<label class="'+labelWidth+' col-form-label">'+label+'</label>';
				 }
			  }
			  
			  var inline="";
			  var isInline=ck.data("inline");
			  if(isInline){
				  inline="checkbox-inline";
			  }
			  
				var text_attr=ck.data("text-attr");
	      		if(!text_attr){
	      			text_attr="text";
	      		}
	      		
	      		var value_attr=ck.data("value-attr");
	      		if(!value_attr){
	      			value_attr="value";
	      		}
	      		
				Ajax.get(url,function(res){
					html+='<div class="'+radioWidth+'"  style="padding-top: 1px;">';
  					var list=res.data;
  					var nodotname=name.replace("\\.","_");
  					if(list&&list.length>0){
  						var optionItem,text,value;
  						for(var i in list){
  							optionItem=list[i];
  							text=processOptionText(optionItem,text_attr);
  							value=processOptionValue(optionItem,value_attr);
  							nodotname=nodotname+"_"+i;
  							var radioHtml = '<div class="checkbox checkbox-primary '+inline+'">'+
  								'<input  id="'+nodotname+'" type="checkbox" name="'+name+'" value="'+value+'"/>'+
  									'<label for="'+nodotname+'">'+text+'</label>'+
  								'</div>';
      						html+=radioHtml;
  	  					}
  						html+="</div>";
  						ck.html(html);
  						
  						if(callback){
  							callback();
  						}
  					}
				});
			  
		  },
		  checkByArray:function(name,values){
			  values=values.toString();
			  if(values.indexOf(",")!=-1){
				  var arr=values.split(",");
				  if(arr&&arr.length>0){
					  for(var i in arr){
						  var input=$("input[type='checkbox'][name='"+name+"'][value='"+arr[i]+"']");
						  input.attr("checked","checked");
					  }
				  }
			  }else{
				  var input=$("input[type='checkbox'][name='"+name+"'][value='"+values+"']");
				  input.attr("checked","checked");
			  }
			  
			  
			  
		  },
		  setChecked:function(name,value,defaultValue){
			  var that=this;
			  if(value){
				  that.checkByArray(name,value);
			  }else{
				  if(defaultValue||defaultValue==0||defaultValue=="0"){
						  that.checkByArray(name,defaultValue);
				  }
			  }
			 
		  }
}

  

 
/**
 * 富文本编辑器组件初始化
 */
var HtmlEditorUtil={
		ing:false,
		initEditors:function(editors){
			 if(!isOk(editors)){return false;}
			 var that=this,
			 len=editors.lenght;
			 for(var i=0;i<len;i++){
				 that.initEditor(editors.eq(i));
			 }
		},
		init:function(parentEle){
			 var parent=getRealJqueryObject(parentEle);
			 if(!isOk(parent)){return false;}
			 var editors=jboltBody.find("[data-editor]");
			 if(!isOk(editors)){return false;}
			 this.ing=false;
			 this.initEditor(editors);
			
		},initNEditor:function(htmlEditor){
			var that=this;
			var editorId=htmlEditor.attr("id");
			if(!editorId){
				alert("请设置编辑器的id属性");
				return false;
			}
			var umjs=$("script[src*='neditor']");
			if(!umjs||umjs.length==0){
				LayerMsgBox.alert("未引入NEditor的js");
				return false;
			}

			var imgmaxsize=htmlEditor.data("imgmaxsize");
			if(!imgmaxsize){
				imgmaxsize=200;
			}
			imgmaxsize=imgmaxsize*1024;
			var videomaxsize=htmlEditor.data("videomaxsize");
			if(!videomaxsize){
				videomaxsize=10;
			}
			videomaxsize=videomaxsize*1024*1024*1024;
		var options={
				  imageMaxSize:imgmaxsize,
				  videoMaxSize:videomaxsize,
			    //关闭字数统计
			      wordCount:false,
			      //关闭elementPath
			      elementPathEnabled:false,
			      toolbars: [
					[   'source',//源码
				        'undo', //撤销
				        'redo', //重做
				        'bold', //加粗
				        'italic', //斜体
				        'underline', //下划线
				        'strikethrough', //删除线
				        'fontborder', //字符边框
				        'indent', //首行缩进
				        'superscript', //上标
				        'subscript', //下标
				        'justifyleft', //居左对齐
				        'justifyright', //居右对齐
				        'justifycenter', //居中对齐
				        'justifyjustify', //两端对齐
				        'forecolor', //字体颜色
				        'backcolor', //背景色
				        'removeformat', //清除格式
				        'formatmatch', //格式刷
				        'autotypeset', //自动排版
				        'touppercase', //字母大写
				        'tolowercase', //字母小写
				        'fontfamily', //字体
				        'fontsize', //字号
				        'paragraph', //段落格式
				        'customstyle', //自定义标题
				        'searchreplace', //查询替换
				        'blockquote', //引用
				        'pasteplain', //纯文本粘贴模式
				        'selectall', //全选
				        'cleardoc', //清空文档
				        
				        'link', //超链接
				        'unlink', //取消链接
				        'emotion', //表情
				        'spechars', //特殊字符
				        'insertorderedlist', //有序列表
				        'insertunorderedlist', //无序列表
				        'horizontal', //分隔线
				        'date', //日期
				        'time', //时间
//				        'simpleupload', //单图上传
				        'insertimage', //多图上传
				        'wordimage',
				        'imagenone', //默认
				        'imageleft', //左浮动
				        'imageright', //右浮动
				        'imagecenter', //居中
				        'lineheight', //行间距
				        'map', //Baidu地图
				        'insertvideo', //视频
				        'insertcode', //代码语言
				        'inserttable', //插入表格
					     'edittable', //表格属性
					     'edittd', //单元格属性
			        	'insertrow', //前插入行
				        'insertcol', //前插入列
				        'mergeright', //右合并单元格
				        'mergedown', //下合并单元格
				        'deleterow', //删除行
				        'deletecol', //删除列
				        'splittorows', //拆分成行
				        'splittocols', //拆分成列
				        'splittocells', //完全拆分单元格
				        'deletecaption', //删除表格标题
				        'inserttitle', //插入标题
				        'mergecells', //合并多个单元格
				        'deletetable', //删除表格
				        'insertparagraphbeforetable', //"表格前插入行"
				      
				        
				        'directionalityltr', //从左向右输入
				        'directionalityrtl', //从右向左输入
				        'rowspacingtop', //段前距
				        'rowspacingbottom', //段后距
				        'background', //背景
				        'template', //模板
				        'scrawl', //涂鸦
				        'drafts', // 从草稿箱加载
				        'print',
				        'preview', //预览
				        'help', //帮助
				        'fullscreen', //全屏
				    ]
				],
				allowDivTransToP:false
				
		}
		var width=htmlEditor.data("width");
		var height=htmlEditor.data("height");
		if(!width){
			width="100%";
		}
		if(!height){
			height=300;
		}
		options['initialFrameWidth']=width;
		options['initialFrameHeight']=height;
		var urlprefix=htmlEditor.data("urlprefix");
		if(urlprefix){
			options['imageUrlPrefix']=urlprefix;
			options['scrawlUrlPrefix']=urlprefix;
			options['videoUrlPrefix']=urlprefix;
			options['fileUrlPrefix']=urlprefix;
		}else{
			var imageUrlPrefix=htmlEditor.data("imageurlprefix");
			if(imageUrlPrefix){
				options['imageUrlPrefix']=imageurlprefix;
			}
			var scrawlUrlPrefix=htmlEditor.data("scrawlurlprefix");
			if(scrawlUrlPrefix){
				options['scrawlUrlPrefix']=scrawlUrlPrefix;
			}
			var videoUrlPrefix=htmlEditor.data("videourlprefix");
			if(videoUrlPrefix){
				options['videoUrlPrefix']=videoUrlPrefix;
			}
			var fileUrlPrefix=htmlEditor.data("fileurlprefix");
			if(fileUrlPrefix){
				options['fileUrlPrefix']=fileUrlPrefix;
			}
		}
		
		
		
		
		that.processInitNEditor(htmlEditor,options);
		},processInitNEditor:function(htmlEditor,options){
			var that=this;
			var editorId=htmlEditor.attr("id");
			if(!editorId){
				alert("请设置编辑器的id属性");
				return false;
			}
			var hiddenInputId=htmlEditor.data("hiddeninput");
			UE.delEditor(editorId);
			var neditor = UE.getEditor(editorId,options);
			neditor.addListener("contentChange",function(){
	        	  if(hiddenInputId){
	        		  var hidden=$("#"+editorId).closest(".jbolt_page").find("#"+hiddenInputId);
	        		  if(hidden&&hidden.length>0){
	        			  var content=neditor.getContent();
	        			  hidden.val(content);
	        		  }
	        	  }
			});
			
			
			
		},initSummernoteEditor:function(htmlEditor){
			var that=this;
			var editorId=htmlEditor.attr("id");
			if(!editorId){
				alert("请设置编辑器的id属性");
				return false;
			}
			var width=htmlEditor.data("width");
			var height=htmlEditor.data("height");
			var maxsize=htmlEditor.data("maxsize");
			if(!maxsize){
				maxsize=200;//默认不超过200K
			}
			var placeholder=htmlEditor.data("placeholder");
			var options={lang:"zh-CN", toolbar: [
				['style', ['style']],
				['font', ['bold', 'italic', 'underline', 'clear','strikethrough', 'superscript', 'subscript']],
			    ['fontsize', ['fontsize']],
			    ['color', ['color']],
			    ['para', ['ul', 'ol', 'paragraph','height']],
			    ['insert', ['hr','table','link', 'picture','video']],
			    ['misc',['fullscreen','codeview','undo','redo','help']]
			  ], callbacks: {
		          onImageUpload: function(files, editor, $editable) {
		        	  if(that.ing){
		        		     alert("有文件正在上传，请稍后~~");
		        	  }else{
		        		  that.ing=true;
		        		  var len=files.length;
		            	  for(var i=0;i<len;i++){
		            			if(files[i].size/1024>maxsize){
		        		    		that.ing=false; 
		        		    		LayerMsgBox.alert("图片文件不能大于"+maxsize+"k",2);
		        		    		return false;
		        				}
		            		that.sendSummernoteFile(htmlEditor,files[i]);
		                  }
		            	  that.ing=false;
		        	  }
		                  
		             
		          }, onChange: function(contents, $editable) {
		        	  var hiddenInputId=htmlEditor.data("hiddeninput");
		        	  if(hiddenInputId){
		        		  var hidden=$("#"+hiddenInputId);
		        		  if(hidden&&hidden.length>0){
		        			  hidden.val(contents);
		        		  }
		        	  }
		          },onPaste:function(e){
		        	  if(that.ing){
		        		  alert("有文件正在上传，请稍后~~");
		        	  }else{
		        		  that.parseIamge(e,htmlEditor);
		        	  }
		        	  
		          }
		      }};
			if(placeholder){
				options.placeholder=placeholder;
			}
			if(width){
				options.width=width+"px";
			}
			if(!height){
				height=300;
			}
			options.height=height+"px";
			htmlEditor.summernote(options);
			
		
			
		},initEditor:function(htmlEditor){
			var that=this;
			var editorId=htmlEditor.attr("id");
			if(!editorId){
				alert("请设置编辑器的id属性");
				return false;
			}
			var type=htmlEditor.data("editor");
			if(type=="summernote"){
				that.initSummernoteEditor(htmlEditor);
			}else if(type=="neditor"){
				that.initNEditor(htmlEditor);
			}else{
				LayerMsgBox.alert("data-editor类型错误",2);
			}
		},parseIamge:function(e,editor){
			 var that=this;
			 that.ing=true; 
			 var maxsize=editor.data("maxsize");
			 if(!maxsize){
				 maxsize=200;
			 }
			 var eve=e.originalEvent;
			/* var items=eve.clipboardData.items;
			 if(!items||items.length==0){
				 that.ing=false;
				 return false;
			 }
			for(var i in items){
				var item=items[i];
				console.log(item)
				if(item&&item.kind == "file"&&(item.type.match(/^image/))){
					if(item.type.indexOf("png")!=-1||item.type.indexOf("jpg")!=-1||item.type.indexOf("gif")!=-1){
						if(that.ing==false){
							return false;
						}
						that.changeToBolbDataUpload(editor,item);
					}
				}
			}*/
			 
			 var files=eve.clipboardData.files;
			 if(!files||files.length==0){
				 that.ing=false;
				 return false;
			 }
			 eve.stopPropagation();
			 eve.preventDefault();
			 
			for(var i in files){
				var file=files[i];
				if(that.ing==false||!(file.type.match(/^image/))){
					return false;
				}
				if(file.size/1024>maxsize){
		    		that.ing=false; 
		    		LayerMsgBox.alert("剪贴板中图片文件不能大于"+maxsize+"k",2);
		    		return false;
				}
				that.sendSummernoteFile(editor,file);
			}
			
		}/*,changeToBolbDataUpload:function(editor,file){
			    var that=this;
			    that.ing=true; 
			    var reader = new FileReader();
			    // 读取文件后将其显示在网页中
			    reader.onloadend = function(){
			    	var dataURI=this.result;
			    	var blob=dataURItoBlob(dataURI);
			    	if(blob.size/1024>200){
			    		that.ing=false; 
			    		alert("剪贴板中图片文件不能大于200k");
			    	}else{
			    		that.sendSummernoteFile(editor,blob);
			    	}
			    };
			    // 读取文件
			    reader.readAsDataURL( file );
		}*/,sendSummernoteFile:function(editor,file){
			var that=this;
			that.ing=true;
			var imgUploadUrl=editor.data("imguploadurl");
			if(!imgUploadUrl){
				imgUploadUrl=summernote_img_uploadurl;
			}
			var fileInputName=editor.data("fileinputname");
			if(!fileInputName){
				fileInputName="file";
			}
			  var imghost=editor.data("imghost");
			  var fd = new FormData();
			    fd.append(fileInputName, file);
			    $.ajax({
			        type:"post",
			        url: imgUploadUrl,
			        data: fd,
			        timeout : 60000, //超时时间设置，单位毫秒
			        cache:false, 
			        async:true, 
			        processData: false,
			        contentType: false,
			        success:function (res) {
			        	
			        	if(res.state=="ok"){
			        		if(res.data){
			        			editor.summernote('insertImage', (imghost?(imghost+"/"):"/")+res.data);
			        			LayerMsgBox.success("上传成功",1000);
			        		}else{
			        			LayerMsgBox.success("上传异常",1000);
			        		}
			        	}else{
			        		LayerMsgBox.error(res.msg,1000);
			        	}
			        	
			        	that.ing=false;
			        	
			        },
			        error:function (err) {
			        	that.ing=false;
			        	LayerMsgBox.error("网络异常",1000);
			        }
			    });
			
		}
}
/**
 * radio工具类封装
 */
var RadioUtil={
		initRadioEvent:function(r,name,handler){
			  if(handler){
				  var exe_handler=eval(handler);
				  if(exe_handler&&typeof(exe_handler)=="function"){
					 r.find("input[type='radio'][name='"+name+"']").unbind("click").on("click",function(e){
						  //e.preventDefault();
						  //e.stopPropagation();
						  var r= $(this);
						  var val=r.val();
						  exe_handler(r,val);
					  });
				  }
			  }
		},
		initRadios:function(radios){
			if(!isOk(radios)){return false;}
			 var len=radios.length;
			 for(var i=0;i<len;i++){
				 this.initRadio(radios.eq(i));
			 }
		},
		initRadio:function(r){
			var that=this,
			  value=r.data("value")+"",
			  defaultValue=r.data("default")+"",
			  name=r.data("name"),
			  handler=r.data("handler"),
			  url=r.data("url"),
			  label=r.data("label");
			  if(!value){value="";}else{value=value+""}
			  if(!defaultValue){defaultValue="";}else{defaultValue=defaultValue+""}
			  if(url){
				  that.insertDatas(r,url,name,label,function(){
					  that.initRadioEvent(r,name,handler);
					  that.setChecked(name,value,defaultValue);
				  });
			  }else{
				  that.initRadioEvent(r,name,handler);
				  that.setChecked(name,value,defaultValue);
			  }
		},
		  init:function(parentEle){
			  var parent=getRealJqueryObject(parentEle);
			  if(!isOk(parent)){return false;}
			  var radios=parent.find("[data-radio]");
			  if(!isOk(radios)){return false;}
			  this.initRadios(radios);
			
		  },insertDatas:function(r,url,name,label,callback){
			  var that=this;
			  r.empty();
			  
			  var width=r.data("width");
			  var labelWidth="";
			  var radioWidth="";
			  if(width){
				  var arr=width.split(",");
				  labelWidth=arr[0];
				  radioWidth=arr[1];
			  }else{
				  labelWidth="100px";
				  radioWidth="col";
			  }
			  var html='';
			  if(label){
				 if(labelWidth.indexOf("px")!=-1){
					 html= '<label class="col-auto col-form-label" style="width:'+labelWidth+'">'+label+'</label>';
				 }else{
					 html= '<label class="'+labelWidth+' col-form-label">'+label+'</label>';
				 }
			  }
			  
			  var inline="";
			  var isInline=r.data("inline");
			  if(isInline){
				  inline="radio-inline";
			  }
			  var text_attr=r.data("text-attr");
	      		if(!text_attr){
	      			text_attr="text";
	      		}
	      		
	      		var value_attr=r.data("value-attr");
	      		if(!value_attr){
	      			value_attr="value";
	      		}
				Ajax.get(url,function(res){
					html+='<div class="'+radioWidth+'"  style="padding-top: 1px;">';
  					var list=res.data;
  					var nodotname=name.replace("\\.","_");
  					if(list&&list.length>0){
  						var optionItem,text,value;
  						for(var i in list){
  							optionItem=list[i];
  							text=processOptionText(optionItem,text_attr);
  							value=processOptionValue(optionItem,value_attr);
  							nodotname=nodotname+"_"+i;
  							var radioHtml = '<div class="radio radio-primary '+inline+'">'+
  								'<input  id="'+nodotname+'" type="radio" name="'+name+'" value="'+value+'"/>'+
  									'<label for="'+nodotname+'">'+text+'</label>'+
  								'</div>';
      						html+=radioHtml;
  	  					}
  						html+="</div>";
  						r.html(html);
  						
  						if(callback){
  							callback();
  						}
  					}
				});
			  
		  },
		  setChecked:function(name,value,defaultValue){
			
			  if(value&&value.length>0){
				  $("input[type='radio'][name='"+name+"'][value='"+value+"']").click();
			  }else{
				  if(defaultValue){
					  if(defaultValue=="all"){
						 $("input[type='radio'][name='"+name+"'][data-all]").click();
					  }else if(defaultValue=="options_first"){
						  $("input[type='radio'][name='"+name+"']:first").click();
					  }else if(defaultValue=="options_last"){
						  $("input[type='radio'][name='"+name+"']:last").click();
					  }else{
						  $("input[type='radio'][name='"+name+"'][value='"+defaultValue+"']").click();
					  }
					 
				  }
			  }
		  }
}


//弹出tips
var LayerTipsUtil={
		initTip:function(tip){
			 var trigger=tip.data("trigger");
			 if(trigger&&trigger=="click"){
				 var tipsIndex=0;
				 tip.off("click").on("click",function(e){
					 e.stopPropagation();
					 e.preventDefault();
					  var tipsMsg=$(this).data("content");
					  tipsIndex=layer.tips(tipsMsg, this, {
						  tips: [4, '#3595CC'],
						  time: 10000
						});
					  
					  $("#layui-layer"+tipsIndex).on("click",function(e){
							 e.stopPropagation();
							 e.preventDefault();
						 });
						 jboltBody.on("click",function(){
							  layer.close(tipsIndex);
						 });
						 
				  });
				
				 
			 }else{
				 var tipsIndex=0;
				 tip.off("mouseover").on("mouseover",function(){
					  var tipsMsg=$(this).data("content");
					  tipsIndex=layer.tips(tipsMsg, this, {
						  tips: [4, '#3595CC'],
						  time: 4000
						});
				  }).off("mouseout").on("mouseout",function(){
					  layer.close(tipsIndex);
				  });
			 }
		  
		},
		initTips:function(tips){
			if(!isOk(tips)){return false;}
			var that=this;
			var len=tips.length;
			for(var i=0;i<len;i++){
				that.initTip(tips.eq(i));
			}
		},
		  init:function(parentEle){
			  var parent=getRealJqueryObject(parentEle);
			  if(!isOk(parent)){return false;}
			  var tips=parent.find("[data-tipsbtn]");
			  if(!isOk(tips)){return false;}
			  this.initTips(tips);
		  }
}

/**
 * layerPhoto弹出层组件工具类
 */
var LayerPhotoUtil={
		init:function(){
			 var that=this;
			 jboltBody.on("click","[data-photobtn]",function(e){
					  e.preventDefault();
					  e.stopPropagation();
					  var btn= $(this);
					  var url=null;
					  var target=btn.data("target");
					  if(this.tagName.toLowerCase()=="a"){
						  url=btn.attr("href");
					  }else if(this.tagName.toLowerCase()=="img"){
						url=btn.attr("src");
					}
					if(!url){
						url=btn.data("url");
					}
					if(url){
						var datas=null;
						var ablum=btn.data("ablum");
						if(ablum){
							datas=that.getablum(ablum,url);
							if(!datas){
								datas=[{
						    	      "src":url, //原图地址
					    	    }];
							}
						}else{
							datas=[{
					    	      "src":url, //原图地址
				    	    }];
						}
						var options={
							    photos: {
							    	  "title": "JBolt图片查看器", 
							    	  "start": 0, //初始显示的图片序号，默认0
							    	  "data": datas
							    	}
							    ,anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机（请注意，3.0之前的版本用shift参数）
							  };
						if(target&&target=="parent"){
							layer.photos(options);
						}else{
							layer.photos(options);
						}
						 
					}else{
						alert("页面存在未设置图片地址的 photobtn");
					}
					 
				  });
		},
		getablum:function(ablum,myurl){
			 var photoBtns=jboltBody.find("[data-photobtn][data-ablum='"+ablum+"']");
			 if(!isOk(photoBtns)){return null;}
			 
			 var psUrls=new Array();
			 psUrls.push({src:myurl});
			 photoBtns.each(function(){
				 var btn= $(this);
				  var url=null;
				  if(this.tagName.toLowerCase()=="a"){
					  url=btn.attr("href");
				  }else if(this.tagName.toLowerCase()=="img"){
					url=btn.attr("src");
					}
					if(!url){
						url=btn.data("url");
					}
					if(url!=myurl){
						psUrls.push({src:url});
					}
			 });
			 
			 return psUrls;
		}
}
/**
 * 自动Ajax加载内容的Portal
 */
;(function($){
		$.extend($.fn, {
			ajaxPortal:function(replaceBody,url,replaceOldUrl,callback){
				return this.each(function(){
					var portal=$(this);
					var l_url="";
					if(url){
						l_url=url;
					}else{
						l_url=portal.data("url")
					}
					if(l_url.indexOf("?")!=-1){
						l_url=l_url+"&t="+new Date().getTime();
					}else{
						l_url=l_url+"?t="+new Date().getTime();
					}
					var autoload=portal.data("autoload");
					if(autoload==undefined){
						autoload=true;
					}
					if((replaceBody==undefined&&autoload)||(replaceBody!=undefined)){
						$.ajax({
							  type:"GET",
							  url: l_url,
							  beforeSend:function(xhr) {
			                        xhr.setRequestHeader("AJAX-PORTAL","true");
			                    },
							  dataType: "html",
							  success:function(html){
								if(replaceBody){
									portal.empty().html(html);
								}else{
									portal.append(html);
								}
								if(replaceOldUrl&&url){
									portal.data("url",url);
								}
								var portalId=portal.attr("id");
								if(!portalId){
									portalId=randomId();
									portal.attr("id",portalId);
								}
								
								afterAjaxPortal(portal);
								if(callback){
									callback();
								}
							}
							});
					
					}
				});
			}
		});
	})(jQuery);
/**
 * ajaxPortal后执行的初始化
 * @param portalId
 * @returns
 */
function afterAjaxPortal(portal){
	$('.tooltip.show').remove();
	SelectUtil.initAutoSetValue(portal);
	JAtomFileUploadUtil.init(portal);
	JAtomImgUploader.init(portal);
	SwitchBtnUtil.init(portal);
	FormDate.init(portal);
	LayerTipsUtil.init(portal);
	HtmlEditorUtil.init(portal);
	ImageViewerUtil.init(portal);
	RadioUtil.init(portal);
	CheckboxUtil.init(portal);
	AutocompleteUtil.init(portal);
	Select2Util.init(portal);
	SelectUtil.init({parent:portal});
	MasterSlaveUtil.init(portal);
	initToolTip(portal);
	MultipleFileInputUtil.init(portal);
	initAjaxPortal(portal);
}

/**
 * 检测文件大小
 * @param file 
 * @param maxSize kb单位
 * @returns
 */
function validateFileMaxSize(file,maxSize){
	  var fileSize=(file.files[0].size/1024).toFixed(1);
	  var gt=(fileSize>maxSize);
	  var formateSize=fileSize+"KB";
	  if(fileSize>1024){
		  formateSize=((fileSize/1024).toFixed(1))+"M";
	  }
	  if(gt){LayerMsgBox.alert("您选择的文件["+formateSize+"],上传限制不能超过 "+maxSize+"KB",2);}
    return gt;
}

//限制上传文件的类型和大小
function validateExcel(file,maxSize){
	  // 返回 KB，保留小数点后两位
	  var fileName = file.value;
	  if(!/.(xls|xlsx)$/.test(fileName)){
		  LayerMsgBox.alert("文件类型必须是xls,xlsx中的一种",2);
		  return false;
	  }
	  if(validateFileMaxSize(file,maxSize)){
		  return false;
	  }
	  return true;
}
//限制上传文件的类型和大小
function validateNormal(file,maxSize){
	  // 返回 KB，保留小数点后两位
	  var fileName = file.value;
	  if(!/.(xls|xlsx|jpg|jpeg|png|JPG|rar|zip|pdf|mp4|flv|mp3|doc|docx)$/.test(fileName)){
		  LayerMsgBox.alert("此文件类型不允许上传",2);
		  return false;
	  }
	  if(validateFileMaxSize(file,maxSize)){
		  return false;
	  }
	  return true;
}
/**
 * 判断是不是img
 */
function isImg(fileName){
	  return /.(jpg|jpeg|png|JPG)$/.test(fileName);
}
//限制上传文件的类型和大小
function validateImg(file,maxSize){
    // 返回 KB，保留小数点后两位
    var fileName = file.value;
    if(isImg(fileName)==false){
  	  	 LayerMsgBox.alert("图片类型必须是jpeg,jpg,png中的一种",2);
           return false;
     }
    if(validateFileMaxSize(file,maxSize)){
  	  return false;
    }
    return true;
}
//验证file
function validateFile(file,accept,maxSize){
	  	var ele=file[0];
	  	//默认两M
	  	if(!maxSize){maxSize=1024*1024*1024*2;}
	  	var passValidate=true;
	  	if(accept){
			switch (accept) {
			case "img":
				passValidate=validateImg(ele,maxSize);
				break;
			case "excel":
				passValidate=validateExcel(ele,maxSize);
				break;
			case "file":
				passValidate=validateNormal(ele,maxSize);
				break;
			}
		}else{
			passValidate=validateNormal(ele,maxSize);
		}
	  	return passValidate;
}



//建立一個可存取到該file的url
function getObjectURL(ele) {
	  var file=ele.files[0];
	  var url = null ;
	  if (window.createObjectURL!=undefined) { // basic
		  url = window.createObjectURL(file);
	  } else if (window.URL!=undefined) { // mozilla(firefox)
		  url = window.URL.createObjectURL(file);
	  } else if (window.webkitURL!=undefined) { // webkit or chrome
		  url = window.webkitURL.createObjectURL(file) ;
	  }
	  return url ;
}

//专门图片上传组件
var JAtomImgUploader={
		  tpl:'<input type="file"   {@if rule}data-rule="${rule}"{@/if}  {@if tips}data-tips="${tips}"{@/if} ><p class="j_img_uploder_msg"><span class="j_file_name"></span><i class="fa fa-remove j_text_danger j_remove_file"></i></p>',
		  init:function(parentEle){
				var that=this;
				var parent=getRealJqueryObject(parentEle);
				if(!isOk(parent)){return false;}
				var imgBoxs=parent.find(".j_img_uploder");
				if(!isOk(imgBoxs)){return false;}
				var len=imgBoxs.length;
				for(var i=0;i<len;i++){
					JAtomImgUploader.initSingle(imgBoxs.eq(i));
				}
				
		  },
		  initSingle:function(box){
			  	var that=this;
			    var rule=box.data("rule");
				var tips=box.data("tips");
				var area=box.data("area");
				if(area){
					var arr=area.split(",");
					box.css({
						"width":arr[0],
						"height":arr[1]
					})
				}
				
				box.html(juicer(that.tpl,{rule:rule,tips:tips}));
				var value=box.data("value");
				if(value&&value!="/assets/img/uploadimg.png"){
					var bg="#999 url("+value+") center center no-repeat";
					box.css({
						"background":bg,
						"background-size":"100%"
					});
					box.find("p.j_img_uploder_msg").show();
				}
				box.find("input[type='file']").on("change",function(event){
					var file=$(this);
					 var files = event.target.files; 
					that.changeFile(file);
				});
				box.find(".j_remove_file").on("click",function(){
					var removefile=$(this);
					that.removeFile(removefile);
				});

		  },
		  removeFile:function(removeBtn){
			  var uploder=removeBtn.closest(".j_img_uploder");
			  var removehandler=uploder.data("removehandler");
				uploder.find("input[type='file']").val("");
				uploder.find("span.j_file_name").text("");
				uploder.find("p.j_img_uploder_msg").hide();
				uploder.css({
					"background":"url('assets/img/uploadimg.png') center center no-repeat",
					"background-size":"80%"
				});
				var hiddeninput=uploder.data("hiddeninput");
				if(hiddeninput){
					$("#"+hiddeninput).val("");
				}
				if(removehandler){
					var exe=eval(removehandler);
					if(exe){
						exe(uploder);
					}
				}
		  },
		  changeFile:function(file){
			  var uploder=file.closest(".j_img_uploder");
				var maxSize=uploder.data("maxsize");
				var fileValue=file.val();
				var hiddeninput=uploder.data("hiddeninput");
				var handler=uploder.data("handler");
				if(hiddeninput&&handler&&handler!="uploadMultipleFile"){
					$("#"+hiddeninput).val("");
				}
				
				if(fileValue){
					if(validateFile(file,"img",maxSize)){
						var arr=fileValue.split('\\');
						var fileName=arr[arr.length-1];
						if(handler&&handler!="uploadMultipleFile"){
							uploder.find("span.j_file_name").text(fileName).attr("title",fileName);
							uploder.find("p.j_img_uploder_msg").show();
							
							//出预览图
							var fileData=getObjectURL(file[0]);
							if(fileData){
								uploder.css({
									"background":"#999 url('"+fileData+"') center center no-repeat",
									"background-size":"100%"
								});
							}
						}
						uploder.closest(".form-group").removeClass("has-error");
						
							
					}else{
						file.val("");
						uploder.find("span.j_file_name").text("");
						uploder.find("p.j_img_uploder_msg").hide();
						uploder.css({
							"background":"url('assets/img/uploadimg.png') center center no-repeat",
							"background-size":"80%"
						});
						return false;
					}
				}else{
					uploder.find("input[type='file']").val("");
					uploder.find("span.j_file_name").text("");
					uploder.find("p.j_img_uploder_msg").hide();
					uploder.css({
						"background":"url('assets/img/uploadimg.png') center center no-repeat",
						"background-size":"80%"
					});
				}
				if(handler){
					var isMultiple=(handler=="uploadMultipleFile");
					if(handler=="uploadFile"||isMultiple){
						var url=uploder.data("url");
						if(!url){
							LayerMsgBox.alert("未设置文件上传地址 data-url",2);
						}else{
							 LayerMsgBox.loading("处理中",1000);
							var hiddeninput=uploder.data("hiddeninput");
							var imgbox=uploder.data("imgbox");
							var limit=uploder.data("limit");
							var imgs=$("#"+imgbox+" img");
							if(imgs&&imgs.length>=limit){
								LayerMsgBox.alert("最多上传["+limit+"]张",5)
								uploder.find("input[type='file']").val("");
								uploder.find("span.j_file_name").text("");
								uploder.find("p.j_img_uploder_msg").hide();
								uploder.css({
									"background":"url('assets/img/uploadimg.png') center center no-repeat",
									"background-size":"80%"
								});
								return false;
							}
							
							var fileName=uploder.data("filename");
							if(!fileName){
								fileName="file";
							}
							
							uploadFile("img",url,fileName,file[0].files[0],hiddeninput,null,null,isMultiple,imgbox,function(){
								if(isMultiple){
									uploder.find("input[type='file']").val("");
								}
							});
						}
					}else{
						var exe_handler=eval(handler);
						  if(exe_handler&&typeof(exe_handler)=="function"){
							  exe_handler(file.val(),file);
						}
					}
					
				}
		  }
}

function processImagesHiddenInputValue(imgboxId,hiddenInputId){
	var imgBox=$("#"+imgboxId);
	var hiddenInput=$("#"+hiddenInputId);
	var value="";
	var imgs=imgBox.find("img");
	var length=imgs.length;
	var lindex=length-1;
	if(length>0){
		imgs.each(function(i,item){
			var img=$(this);
			value=value+img.attr("src");
			if(i<lindex){
				value=value+",";
			}
		});
		
	}
	
	
	hiddenInput.val(value);
}

 function removeUploadImgBoxLi(ele){
	var remove=$(ele);
	var li=remove.closest("li");
	var hiddenInputId=li.data("hiddeninput");
	var imgboxId=li.data("imgbox");
	li.remove();
	
	processImagesHiddenInputValue(imgboxId,hiddenInputId);
	
	
}

function imgGotoLeft(i){
	var fa=$(i);
	var li=fa.closest("li");
	var prev=li.prev();
	if(prev){
		var newLi=li.clone();
		prev.before(newLi);
		var hiddenInputId=li.data("hiddeninput");
		var imgboxId=li.data("imgbox");
		li.remove();
		
		processImagesHiddenInputValue(imgboxId,hiddenInputId);
		
		
	}else{
		layer.msg("已经是第一个",{time:1000});
	}
	
}
function imgGotoRight(i){
	var fa=$(i);
	var li=fa.closest("li");
	var next=li.next();
	if(next){
		var newLi=li.clone();
		next.after(newLi);
		var hiddenInputId=li.data("hiddeninput");
		var imgboxId=li.data("imgbox");
		li.remove();
		
		processImagesHiddenInputValue(imgboxId,hiddenInputId);
	}else{
		layer.msg("已经是最后一个",{time:1000});
	}
}

function uploadFile(type,url,name,file,hiddeninput,filenameInput,sizeinput,isMultiple,imgbox,callback){
	LayerMsgBox.loading("上传中",10000);
	 var fileSize=parseInt((file.size/1024).toFixed(0));
	  var fd = new FormData();
	    fd.append(name, file);
	    $.ajax({
	        type:"POST",
	        url: url,
	        data: fd,
	        timeout : 60000, //超时时间设置，单位毫秒
	        cache:false, 
	        async:true, 
	        processData: false,
	        contentType: false,
	        success:function (res) {
	        	if(res.state=="ok"){
	        		if(sizeinput){
	        			$("#"+sizeinput).val(fileSize);
	        		}
	        		if((type=="img")&&hiddeninput&&res.data){
	        			var datas=res.data;
	        			var hinput=$("#"+hiddeninput);
	        			var hvalue=$.trim(hinput.val());
	        			if(isMultiple&&imgbox){
	        				$("#"+imgbox).append("<li data-imgbox='"+imgbox+"' data-hiddeninput='"+hiddeninput+"'><img src='"+datas+"'/><div class='optbox' ><i title='删除' onclick='removeUploadImgBoxLi(this)' class='fa fa-trash'></i><i onclick='imgGotoLeft(this)' class='fa fa-arrow-left' title='左移'></i><i title='右移' onclick='imgGotoRight(this)' class='fa fa-arrow-right'></i></div></li>")
	        				if(!hvalue){
	        					hvalue=datas;
	        				}else{
	        					hvalue=hvalue+","+datas;
	        				}
	        			}else{
	        				hvalue=datas;
	        			}
	        			hinput.val(hvalue);
	        		}else
	        		if((type=="file")&&hiddeninput&&res.data){
	        			$("#"+hiddeninput).val(res.data.fileUrl);
	        			if(filenameInput){
	        				var v=$("#"+filenameInput).val();
	        				if((v&&v.length>2)==false){
	        					$("#"+filenameInput).val(res.data.fileName);
	        				}
	        				
	        			}
	        		}
	        		if(callback){callback();}
	        		LayerMsgBox.success("上传成功",1000);
	        	}else{
	        		LayerMsgBox.error(res.msg,1000);
	        	}
	        },
	        error:function (err) {
	        	LayerMsgBox.error("网络异常",1000);
	        }
	    });
}

function getBase64Image(img) {
 var canvas = document.createElement("canvas");
 canvas.width = img.width;
 canvas.height = img.height;
 var ctx = canvas.getContext("2d");
 ctx.drawImage(img, 0, 0, img.width, img.height);
 var dataURL = canvas.toDataURL("image/png");
 return dataURL;
}

function dataURItoBlob(dataURI) {
    var byteString = atob(dataURI.split(',')[1]);
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    return new Blob([ab], {type: mimeString });
}
/**
 * 上传组件封装
 */
var JAtomFileUploadUtil={
		tpl:'<div class="j_upload_file"><input type="file"  {@if rule}data-rule="${rule}"{@/if} {@if tips}data-tips="${tips}"{@/if}  /></div><p class="j_upload_file_box_msg"><span class="j_file_name"></span><i class="fa fa-remove j_text_danger j_remove_file"></i></p>',
		initFileBoxUI:function(fileBoxs){
			if(!isOk(fileBoxs)){return false;}
			
			var len=fileBoxs.length;
			var box,rule,tips;
			for(var i=0;i<len;i++){
				box=fileBoxs.eq(i);
				rule=box.data("rule");
				tips=box.data("tips");
				box.html(juicer(this.tpl,{rule:rule,tips:tips}));
			}
			
		},
		initFileBoxEvent:function(fileBoxs){
			if(!isOk(fileBoxs)){
				return false;
			}
			var that=this;
			// onchange事件
			fileBoxs.find("input[type='file']").on("change",function(){
				var file=$(this);
				var box=file.closest(".j_upload_file_box");
				var accept=box.data("accept");
				var maxSize=box.data("maxsize");
				var fileValue=file.val();
				if(fileValue){
					if(validateFile(file,accept,maxSize)){
						var arr=fileValue.split('\\');
						var fileName=arr[arr.length-1];
						box.find("span.j_file_name").text(fileName);
						box.find("p.j_upload_file_box_msg").show();
						box.find(".j_upload_file").addClass("j_reupload");
						box.closest(".form-group").removeClass("has-error");
						var imgpreview=box.data("imgpreview");
						//如果是图片 而且设置了要出预览 就出预览图
						if(imgpreview){
							if(isImg(fileName)){
								var imgpreviewBox=$(imgpreview);
								if(imgpreviewBox&&imgpreviewBox.length){
									var url=getObjectURL(file[0]);
									if(url){
										imgpreviewBox.html("<img src='"+url+"'/>");
									}
								}
								
							}
						}
					}else{
						that.clearIt(box);
						return false;
					}
				}else{
					that.clearIt(box);
				}
				
				var handler=box.data("handler");
				if(handler){
					if(handler=="uploadFile"){
						var url=box.data("url");
						if(!url){
							LayerMsgBox.alert("未设置文件上传地址 data-url",2);
						}else{
							var hiddeninput=box.data("hiddeninput");
							var sizeinput=box.data("sizeinput");
							var fileNameInput=box.data("filenameinput");
							var fileName=box.data("filename");
							if(!fileName){
								fileName="file";
							}
							uploadFile("file",url,fileName,file[0].files[0],hiddeninput,fileNameInput,sizeinput);
						}
					}else{
						var exe_handler=eval(handler);
						  if(exe_handler&&typeof(exe_handler)=="function"){
							  exe_handler(file.val(),file);
						}
					}
					
				}
			});
			fileBoxs.find(".j_remove_file").on("click",function(){
				var removefile=$(this);
				var box=removefile.closest(".j_upload_file_box");
				that.clearIt(box);
			});
			
		},
		init:function(parentEle){
			var that=this;
			var parent=getRealJqueryObject(parentEle);
			if(!isOk(parent)){return false;}
			
			//得到符合条件的组件 进行初始化
			var fileBoxs=parent.find(".j_upload_file_box");
			if(isOk(fileBoxs)){
				//初始化UI
				that.initFileBoxUI(fileBoxs);
				//初始化事件
				that.initFileBoxEvent(fileBoxs);
			}	
			
		},
		//清空选择文件
		clearIt:function(box){
			box.find("input[type='file']").val("");
			box.find("span.j_file_name").text("");
			box.find("p.j_upload_file_box_msg").hide();
			box.find(".j_upload_file").removeClass("j_reupload");
			var imgpreview=box.data("imgpreview");
			if(imgpreview){
				$(imgpreview).empty();
			}
		}
}




//layer msg模块封装
var LayerMsgBox={
		alert:function(msg,icon,handler){
			if(icon){
				layer.alert(msg,{icon:icon}, function(index){
						if(handler){
							handler();
						}
					  layer.close(index);
					});  
			}else{
				layer.alert(msg, function(index){
					if(handler){
						handler();
					}
				  layer.close(index);
				});  
			}
			
		},
		confirm:function(msg,handler,cancelHandler){
			layer.confirm(msg, {icon: 3, title:'请选择'}, function(index){
				if(handler){
					handler();
				}
				layer.close(index);
			},function(index){
				if(cancelHandler){
					cancelHandler();
				}
				layer.close(index);
			});
		},
		/**
		 * 弹出成功信息,并执行回调方法
		 * @param msg
		 * @param time
		 * @param handler
		 */
		success:function(msg,time,handler){
			if(!msg){msg="操作成功";}
			if(!time){time=1000;}
			var index=layer.msg(msg,{time:time,icon:1},function(){
				if(handler){
					handler();
				}
			});
			return index;
		},


		/**
		 * 弹出Error,并执行回调方法
		 * @param msg
		 * @param time
		 */
		error:function(msg,time,handler){
			if(!msg){msg="错误";}
			if(!time){time=1500;}
			var index=layer.msg(msg,{time:time,icon:2},function(){
				if(handler){
					handler();
				}
			});
			return index;
		},
		prompt:function(title,defaultMsg,handler,type){
			if(type==undefined){
				type=2;
			}
			var i=layer.prompt({title: title,value:(defaultMsg?defaultMsg:""),formType: type}, function(text, index){
				if(handler){
					handler(index,text);
				}
			});
			return i;
		},
		/**
		 * 弹出进度
		 * @param msg
		 * @param time
		 */
		loading:function(msg,time,handler){
			if(!msg){msg="执行中...";}
			var index=null;
			time=(time?time:10000);
			if(time){
				index=layer.msg(msg,{time:time,icon:16,shade:0.3},function(){
					if(handler){
						handler();
					}
				});
			}else{
				index=layer.msg(msg,{icon:16});
			}
			return index;
		},
		close:function(index){
			layer.close(index);
		},
		closeAll:function(type){
			if(type){
				layer.closeAll(type);
			}else{
				layer.closeAll();
			}
		},
		closeLoading:function(){
			setTimeout(function(){
				layer.closeAll('dialog'); //关闭加载层
			}, 500);
		},
		closeLoadingNow:function(){
				layer.closeAll('dialog'); //关闭加载层
		},
		load:function(type,time){
			var index=null;
			if(time){
				index=layer.load(type,{time:time});
			}else{
				index=layer.load(type);
			}
			return index;
		},
		closeLoad:function(){
			setTimeout(function(){
				layer.closeAll('loading'); //关闭加载层
			}, 200);
		},
		closeLoadNow:function(){
				layer.closeAll('loading'); //关闭加载层
		}

}

/**
 * ajax封装
 */
var jboltAjaxTimeout=60000;
var Ajax={
		uploadBase64File:function(url,base64Data,fileName,success,error,sync){
			var blob=dataURItoBlob(base64Data);
			this.uploadBlob(url,blob,fileName,success,error,sync);
		},
		uploadBlob:function(url,blob,fileName,success,error,sync){
			var formData=new FormData();
			formData.append("file",blob,fileName);
			this.uploadFormData(url,formData,success,error,sync);
		},
		uploadFormData:function(url,formData,success,error,sync){
		    var async=true;
		    if(sync){async=false;}
			$.ajax({
				url:url,
				type:"post",
				processData: false,
                contentType: false,
				timeout : jboltAjaxTimeout, //超时时间设置，单位毫秒
				async:async,
				data:formData,
				success:function(data){
					if(data.state=="ok"){
						if(success){
							success(data);
						}
					}else{
						if(data.msg&&data.msg=="jbolt_system_locked"){
							showJboltLockSystem();
						}else{
							LayerMsgBox.alert(data.msg,2);
							if(error){
								error();
							}
						}
						
					}
				},
				error:function(){
					LayerMsgBox.alert("网络通讯异常",2);
					if(error){
						error();
					}
					
				}
				
			});
			jboltAjaxTimeout=60000;
		},
		  post:function(url,data,success,error,sync){
			    var async=true;
			    if(sync){async=false;}
				$.ajax({
					url:url,
					type:"post",
					dataType:"json",
					timeout : jboltAjaxTimeout, //超时时间设置，单位毫秒
					async:async,
					data:data,
					success:function(data){
						if(data.state=="ok"){
							if(success){
								success(data);
							}
						}else{
							if(data.msg&&data.msg=="jbolt_system_locked"){
								showJboltLockSystem();
							}else{
								LayerMsgBox.alert(data.msg,2);
								if(error){
									error();
								}
							}
							
						}
					},
					error:function(){
						LayerMsgBox.alert("网络通讯异常",2);
						if(error){
							error();
						}
						
					}
					
				});
				jboltAjaxTimeout=60000;
			},
			get:function(url,success,error,sync){
				var async=true;
			    if(sync){async=false;}
				$.ajax({
					url:url,
					type:"get",
					dataType:"json",
					timeout : jboltAjaxTimeout, //超时时间设置，单位毫秒
					async:async,
					success:function(data){
						if(data.state=="ok"){
							if(success){
								success(data);
							}
						}else{
							if(data.msg&&data.msg=="jbolt_system_locked"){
								showJboltLockSystem();
							}else{
								LayerMsgBox.alert(data.msg,2);
								if(error){
									error();
								}
							}
						}
					},
					error:function(){
						LayerMsgBox.alert("网络通讯异常",2);
						if(error){
							error();
						}
					}
					
				});
				jboltAjaxTimeout=60000;
			}
}
/**
 * 处理option value取值
 * @param data
 * @param value_attr
 * @returns
 */
function processOptionValue(data,value_attr){
	var value=data[value_attr];
	if(!value&&(value=="undefined"||value==undefined)){
		if(value_attr!="value"){
			 value=data["value"];
			 if(!value&&(value=="undefined"||value==undefined)){
				 value=data["id"];
			 }
		}else{
			value=data["id"];
		}
		
	}
	return value;
}
/**
 * 处理option text取值
 * @param data
 * @param text_attr
 * @returns
 */
function processOptionText(data,text_attr){
	var text="";
	if(text_attr.indexOf(",")==-1){
		text=data[text_attr];
		if(!text&&(text=="undefined"||text==undefined)){
			if(text_attr!="text"){
				text=data["text"];
				if(!text&&(text=="undefined"||text==undefined)){
					text=data["name"];
					if(!text&&(text=="undefined"||text==undefined)){
						text=data["title"];
					}
				}
			}else{
				if(!text&&(text=="undefined"||text==undefined)){
					text=data["name"];
					if(!text&&(text=="undefined"||text==undefined)){
						text=data["title"];
					}
				}
			}
			
		}
		return text;
	}
	var attrs=text_attr.split(",");
	var t;
	for(var i in attrs){
		t=data[attrs[i]];
		text=text+"["+(t?t:"-")+"]";
	}
	return text;
}

/**
   * select工具类
   */
  var SelectUtil={
		  setValueToOther:function(select){
				var setvaluetoId=select.data("setvalueto");
				if(setvaluetoId){
					var setvaluetoEle=$("#"+setvaluetoId);
					if(setvaluetoEle&&setvaluetoEle.length>0){
						var selectText=select.data("text");
						var values=select.val();
						if(selectText){
							var option=select.find("option").first();
							if(option.is(":selected")){
								values.shift();
							}
						}
						setvaluetoEle.val(values?values:"");
						setTimeout(function(){
								var reg = new RegExp("&nbsp;","g")
								select.next().find("li.select2-selection__choice").each(function(){
								var li=$(this);
								if(li.attr("title")==selectText){
									li.remove();
									}else{
										var html=li.html();
										html=html.replace(reg,"");
										html=html.replace("├","");
										li.html(html);
										}
							
							});
							},60);
					}
				}else{
					LayerMsgBox.alert("请配置data-setvalueto属性",2);
					}
				
			},
		  processItems:function(html,list,appendHandler,text_attr,value_attr,onlyleaf,level){
			  var that=this;
			   /*var text,value;
			   for(var i in list){
					   text=processOptionText(list[i],text_attr);
					   value=processOptionValue(list[i],value_attr);
  						var option = '<option value="'+value+'">&nbsp;&nbsp;&nbsp;&nbsp; ├'+text+'</option>';
  						if(appendHandler){
							option=appendHandler(option,list[i]);
						}
  						html+=option;
  					}*/
				var text,value,optionItem,option_items;
				var levelText="&nbsp;&nbsp;&nbsp;&nbsp;";
				var smallLevelText="&nbsp;&nbsp;&nbsp;";
				if(level>2){
					for(var i=0;i<(level-2);i++){
						levelText=levelText+smallLevelText;
					}
				}
				levelText=levelText+" ├ ";
				for(var i in list){
					optionItem=list[i];
					option_items=optionItem.items;
					var hasItems=option_items&&option_items.length>0;
					text=levelText+processOptionText(optionItem,text_attr);
					value=processOptionValue(optionItem,value_attr);
					var option = '<option value="'+value+'">'+text+'</option>';
					if(hasItems&&onlyleaf){
						option='<optgroup data-value="'+value+'" label="'+text+'">';
					}else{
						option='<option value="'+value+'">'+text+'</option>';
					}
					if(appendHandler){
						option=appendHandler(option,optionItem);
					}
					html+=option;
					if(hasItems){
						html=that.processItems(html,option_items,appendHandler,text_attr,value_attr,onlyleaf,(level+1));
						if(onlyleaf){
							html+="</optgroup>";
						}
					}
				}
					return html;
		},processChangeEventHandler:function(_thisSelect,setting){
			var that=this;
			if(setting&&setting.handler){
				if(setting.handler=="setValueToOther"){
					that.setValueToOther(_thisSelect);
				}else{
					setting.handler(_thisSelect);
				}
					
				}else{
					var handler=_thisSelect.data("handler");
					if(handler){
						if(handler=="setValueToOther"){
							that.setValueToOther(_thisSelect);
						}else{
							var exe_handler=eval(handler);
							if(exe_handler&&typeof(exe_handler)=="function"){
								exe_handler(_thisSelect);
							}
						}
						
					}
				}
				
		},
		readAndInsertItems:function(_thisSelect,setting,refreshing){
				   var that=this;
		      		var selectedValue=_thisSelect.data("select");
		      		if(refreshing){
		      			selectedValue=_thisSelect.val();
		      		}
		      		var text_attr=_thisSelect.data("text-attr");
		      		if(!text_attr){
		      			text_attr="text";
		      		}
		      		
		      		var value_attr=_thisSelect.data("value-attr");
		      		if(!value_attr){
		      			value_attr="value";
		      		}
		      		var appendHandler = _thisSelect.data("append-handler");
		      		if(appendHandler){
						appendHandler=eval(appendHandler);
					}
		      		_thisSelect.empty();
		      		if(_thisSelect.data("text")){
		      			_thisSelect.append('<option value="'+_thisSelect.data("value")+'">'+_thisSelect.data("text")+'</option>');
		      		}
		      		var url=null;
		      		if(setting&&setting.url){
		      			url=setting.url;
		      		}else{
		      			url=_thisSelect.data("url");
		      		}
		      		var onlyleaf=_thisSelect.data("onlyleaf");
		      		if(url!=null){
		      			$.ajax({
			      			type:"GET",
			      			url:url,
			      			dataType:"json",
			      			timeout : 10000, //超时时间设置，单位毫秒
			      			context:_thisSelect,
			      			success:function(result){
			      				if(refreshing){
			      					LayerMsgBox.closeLoading();
			      				}
			      				if(result.state=="ok"){
			      					var html="";
			      					var list=result.data;
			      					var text,value,optionItem,option_items;
			      					for(var i in list){
			      						optionItem=list[i];
			      						option_items=optionItem.items;
			      						var hasItems=option_items&&option_items.length>0;
			      						text=processOptionText(optionItem,text_attr);
			      						value=processOptionValue(optionItem,value_attr);
			      						var option = '<option value="'+value+'">'+text+'</option>';
			      						if(hasItems&&onlyleaf){
			      							option='<optgroup data-value="'+value+'" label="'+text+'">';
			      						}else{
			      							option='<option value="'+value+'">'+text+'</option>';
			      						}
			      						if(appendHandler){
											option=appendHandler(option,optionItem);
										}
			      						html+=option;
			      						if(hasItems){
			      							html=that.processItems(html,option_items,appendHandler,text_attr,value_attr,onlyleaf,2);
			      							if(onlyleaf){
			      								html+="</optgroup>";
			      							}
			      						}
			      					}
			      					_thisSelect.append(html);
			      					if(selectedValue||(typeof(selectedValue)=="boolean")){
			      						selectedValue=selectedValue.toString();
			      						if(selectedValue.indexOf(",")!=-1){
				      						var arr=selectedValue.split(",");
				      						_thisSelect.val(arr);
				      					}else{
				      						_thisSelect.val(selectedValue);
				      					}
			      						if(!_thisSelect.val()){
			      							var options=_thisSelect.find("option");
			      							if(options&&options.length>0){
			      								_thisSelect.val(options.eq(0).val());
			      							}
			      						}
			      					}else if(selectedValue==0||selectedValue=="0"){
			      						var options=_thisSelect.find("option[value='0']");
		      							if(options&&options.length>0){
		      								_thisSelect.val(selectedValue);
		      							}
			      					}
			      					
			      					
			      				
			      					_thisSelect.change();
			      				}
			      				
			      				Select2Util.initAutoLoadSelect(_thisSelect);
			      			}
			      		});
		      		}
		},
		/**
		 * 处理刷新
		 */
		processRefresh:function(_thisSelect,setting){
				if(_thisSelect.data("refresh")){
	    			var exist=_thisSelect.parent().find(".fa-refresh");
	    			if(exist&&exist.size()>0){
	    				return false;
	    				//exist.parent().remove();
	    			}
	    			var refreshBtn=document.createElement("div");
	    			refreshBtn.className="input-group-append hand";
	    			refreshBtn.innerHTML='<i class="fa fa-refresh"></i>';
	    			$(refreshBtn).click(function(){
	    				LayerMsgBox.loading("正在刷新数据...",10000);
	    				that.readAndInsertItems(_thisSelect,setting,true);
	    			});
	    			_thisSelect[0].parentElement.appendChild(refreshBtn);
	    		}
		},
		/**
		 * 查找
		 */
		findSelect:function(setting){
			var select=null;
			if(setting){
	      		if(setting.selectId){
	      			if(setting.parent){
	      				if(typeof setting.parent=="object"){
	      					if(isDOM(setting.parent)){
	      						select=$(setting.parent).find("#"+setting.selectId);
	      					}else{
	      						select=setting.parent.find("#"+setting.selectId);
	      					}
	      				}else{
	      					if(setting.parent.indexOf("#")!=-1){
	          					select=$(setting.parent).find("#"+setting.selectId);
	          				}else{
	          					select=$("#"+setting.parent).find("#"+setting.selectId);
	          				}
	      				}
	      				
	      			}else{
	      				select=$("#"+setting.selectId);
	      			}
	      			
	      		}else{
	      			if(setting.parent){
	      				if(typeof setting.parent=="object"){
	      					if(isDOM(setting.parent)){
	      						select=$(setting.parent).find("select[data-autoload]");
	      					}else{
	      						select=setting.parent.find("select[data-autoload]");
	      					}
	      					
	      				}else{
	  	    				if(setting.parent.indexOf("#")!=-1){
	  	    					select=$(setting.parent).find("select[data-autoload]");
	  	    				}else{
	  	    					select=$("#"+setting.parent).find("select[data-autoload]");
	  	    				}
	      				}
	      			}else{
	      				select=$("select[data-autoload]"); 
	      			}
	      		}
	      	}else{
	      		select=$("select[data-autoload]");
	      	}
			return select;
		},
		  /**
		   * 处理select
		   * setting selectId parent callback
		   */
		   init:function(setting){
			   var that=this;
		       var selects=that.findSelect(setting);
		       if(selects&&selects.length>0){
		    	   //循环处理 这样写性能高一点
		    	   var len=selects.length;
		    	   for(var i=0;i<len;i++){
		    		   that.processOneSelect(selects.eq(i),setting);
		    	   }
		       }
		    },
		    /**
		     * 处理单个select
		     */
		    processOneSelect:function(_thisSelect,setting){
	      		var that=this;
	      		//处理二级联动事件绑定
	      		that.processLinkAge(_thisSelect,setting);
	      		//
	      		if((_thisSelect.data("url")||(setting&&setting.url))){
	      			//读取并渲染数据
	  	    		that.readAndInsertItems(_thisSelect,setting,false);
	  	    		that.processRefresh(_thisSelect,setting);
	      		}else{
	      			var selectedValue=_thisSelect.data("select");
	      			if(selectedValue){
	      				_thisSelect.val(selectedValue);
	      			}
	      		}
		    },
		    /**
		     * 处理联动
		     */
		    processLinkAge:function(_thisSelect,setting){
		    	var that=this;
		    	var islinkage=_thisSelect.data("linkage");
	      		if(islinkage){
	      			_thisSelect.unbind("change");
	      		}
	      		_thisSelect.on("change",function(){
	      			var beforechange=_thisSelect.data("beforechange");
	      			if(beforechange){
						var exe=eval(beforechange);
						if(exe){
							exe(_thisSelect);
						}
					}
	      			//change事件
	      			that.processChangeEventHandler(_thisSelect,setting);
	      			
	      			var sonId=_thisSelect.data("sonid");
	      			if(islinkage&&sonId){
	      				var srcUrl=$("#"+sonId).data("srcurl");
	      				var url="";
	      				if(srcUrl){
	      					if(srcUrl[srcUrl.length-1]=="="){
	      						url=srcUrl+_thisSelect.val();
	      					}else{
	      						url=srcUrl+"/"+_thisSelect.val();
	      					}
	      					
	      				}else{
	      					url=$("#"+sonId).data("url")+"/"+_thisSelect.val();
	      				}
	      				SelectUtil.init({parent:_thisSelect.closest("form"),selectId:sonId,url:url});
	      			}
	      		});
		    },
  /**
   * 设置select选中值
   * @param id
   * @param value
   */
   setValue:function(id,value,defaultValue){
	  if(value){
		  $("#"+id).val(value);
	  }else{
		  if(defaultValue){
			  $("#"+id).val(defaultValue);
		  }
	  }

  },initAutoSetValue:function(parentEle){
	  var parent=getRealJqueryObject(parentEle);
	  if(isOk(parent)){
		  parent.find("select[data-autosetvalue]").each(function(){
			  var select=$(this);
			  var value=select.data("autosetvalue");
			  select.val(""+value);
		  });
	  }
	 
  }
  }
 

$.fn.size=function(){
	return this.length;
}


/**
 * 删除一行tr
 */
function removeTr(obj){
	$(obj).closest("tr").remove();
}
/**
 * 删除一行tr
 */
function removeByKey(obj){
	var removeKey=$(obj).data("removekey");
	$("[data-removekey='"+removeKey+"']").remove();
}

//上移 
function eleMoveUp(current) { 
  var prev = current.prev();  //获取当前<tr>前一个元素
  if (current.index() > 0) { 
    current.insertBefore(prev); //插入到当前<tr>前一个元素前
  } 
} 
// 下移 
function eleMoveDown(current) { 
  var next = current.next(); //获取当前<tr>后面一个元素
  if (next) { 
    current.insertAfter(next);  //插入到当前<tr>后面一个元素后面
  } 
} 


//PageOpt初始化工具
var PageOptUtil={
		  init:function(){
			  var self=this;
			  self.initDialogOptEvent();
			  self.initAjaxLinkEvent();
			  self.initPortalLink();
			  self.initAjaxCheckInput();
		  },
		  initAjaxCheckInput:function(){
			  jboltBody.on("blur","input[data-ajax-check-url]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var input=$(this),val=$.trim(input.val()),url=input.data("ajax-check-url");
				  if(val&&url){
					  LayerMsgBox.loading("校验中...",10000);
					  
					  $.ajax({
							url:url,
							type:"post",
							dataType:"json",
							timeout : 10000,
							data:{"data":val},
							success:function(res){
								if(res.state=="ok"){
									showItCheckSuccessResult(input);
									LayerMsgBox.closeLoading();
								}else{
									if(res.msg&&res.msg=="jbolt_system_locked"){
										showJboltLockSystem();
									}else{
										showItCheckFailResult(input);
										LayerMsgBox.error(res.msg,1500);
									}
									
								}
							},
							error:function(){
								showItCheckFailResult(input);
								LayerMsgBox.error("网络通讯异常",1500);
							}
							
						});
					  
				  }
				  return false;
			  });
		  },
		  //初始化弹出dialog的按钮
		  initDialogOptEvent:function(){
			  jboltBody.on("click","[data-dialogbtn]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var target=$(this).data("target");
				  if(target=="parent"){
				  	parent.DialogUtil.openBy(this);
				  }else if(target=="outparent"){
				  	parent.parent.DialogUtil.openBy(this);
				  }else{
				  	 DialogUtil.openBy(this);
				  }
				  return false;
			  });
		  },
		  initAjaxLinkEvent:function(){
			  jboltBody.on("click","[data-ajaxbtn]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var action=$(this);
				  var url=action.attr("href");
				  if(!url){
					  url=action.data("url");
				  }
				  if(!url){
					  alert("请设置URL地址");
					  return false;
				  }
				  var okhandler=action.attr("handler");
				  if(!okhandler){
					  okhandler=action.data("handler");
				  }
				  
				  var dataconfirm=action.data("confirm");
				  var dataloading=action.data("loading");
				 
				  var ajaxFun=function(){
					  var timeout=action.data("timeout");
					  if(timeout){
						  jboltAjaxTimeout=timeout;
					  }
					  LayerMsgBox.loading(dataloading,jboltAjaxTimeout);
					  //开始执行ajax
					  Ajax.get(url,function(data){
						  
						  if(okhandler){
							  LayerMsgBox.closeLoadingNow();
							  if(okhandler=="removeTr"){
								  removeTr(action);
							  }else if(okhandler=="removeByKey"){
								  removeByKey(action);
							  }else if(okhandler=="moveUp"){
								  eleMoveUp(action.closest("tr"));
							  }else if(okhandler=="moveDown"){
								  eleMoveDown(action.closest("tr"));
							  }else  if(okhandler=="refreshPortal"){
								  var portalId=action.data("portal");
								  if(portalId){
									  LayerMsgBox.success("操作成功",500,function(){
										  $("#"+portalId).ajaxPortal(true);
									  });
								  }
								  
							  }else{
								  var exe_handler=eval(okhandler);
								  if(exe_handler&&typeof(exe_handler)=="function"){
									  exe_handler(data);
									}
							  }
							 
						  }else{
							  LayerMsgBox.success("操作成功",600);
						  }
					  });
				  }
				  if(dataconfirm){
					  LayerMsgBox.confirm(dataconfirm, ajaxFun);
				  }else{
					  ajaxFun();
				  }
				 
				  return false;
			  });
		  },
		  initPortalLink:function(){
			  jboltBody.on("click","[data-portalbtn]",function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var action=$(this);
				  var portalId=action.data("portalid");
				  if(!portalId){
					  alert("请设置data-portalid");
					  return false;
				  }
				  var url=action.attr("href");
				  if(!url){
					  url=action.data("url");
				  }
				  if(!url){
					  alert("请设置URL地址");
					  return false;
				  }
				  var portal=$("#"+portalId);
				  if(!portal||portal.length==0){
					  alert("data-portalid不正确");
					  return false;
				  }
				  LayerMsgBox.loading("正在匹配...",30000);
				  portal.ajaxPortal(true,url,true,function(){
					  LayerMsgBox.success("匹配完成",500);
				  });
				  
			  });
			  
		  }
}

//table初始化工具
var TableUtil={
		  init:function(){
			  var self=this;
			  self.initDelOptEvent();
			  self.initEditOptEvent();
			  self.initAddOptEvent();
		  },
		  //初始化删除按钮
		  initDelOptEvent:function(){
			  jboltDocument.on("click",'.jbolt_table_delbtn',function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var action=$(this);
				  var url=action.attr("href");
				  var okhandler=action.attr("handler");
				  if(!okhandler){
					  okhandler=action.data("handler");
				  }
				  var confirm=action.data("confirm");
				  LayerMsgBox.confirm(confirm?confirm:"确定删除此项？", function(){
					  LayerMsgBox.loading("删除中",10000);
					  //开始执行ajax
					  Ajax.get(url,function(ret){
						  LayerMsgBox.closeLoading();
						  LayerMsgBox.success(ret.msg?ret.msg:"操作成功");
						  if(okhandler){
							  if(okhandler=="removeTr"){
								  removeTr(action);
							  }else if(okhandler=="removeByKey"){
								  removeByKey(action);
							  }else if(okhandler=="refreshPortal"){
								  var portalId=action.data("portal");
								  if(portalId){
									  LayerMsgBox.success("操作成功",500,function(){
										  $("#"+portalId).ajaxPortal(true);
									  });
								  }
							  }else{
								  var exe_handler=eval(okhandler);
								  if(exe_handler&&typeof(exe_handler)=="function"){
									  exe_handler();
									}
							  }
							 
						  }
					  });
				  });
				  return false;
			  });
		  },
		//初始化删除按钮
		  initEditOptEvent:function(){
			  jboltDocument.on("click",'.jbolt_table_editbtn',function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var target=$(this).data("target");
				  if(target=="parent"){
				  	parent.DialogUtil.openBy(this);
				  }else if(target=="outparent"){
				  	parent.parent.DialogUtil.openBy(this);
				  }else{
				  	 DialogUtil.openBy(this);
				  }
				  return false;
			  });
		  },
		//初始化新增按钮
		  initAddOptEvent:function(){
			  jboltDocument.on("click",'.jbolt_table_addbtn',function(e){
				  e.preventDefault();
				  e.stopPropagation();
				  var target=$(this).data("target");
				  if(target=="parent"){
				  	parent.DialogUtil.openBy(this);
				  }else if(target=="outparent"){
				  	parent.parent.DialogUtil.openBy(this);
				  }else{
				  	 DialogUtil.openBy(this);
				  }
				  return false;
			  });
		  }
}


//表单中的时间选择组件
var FormDate={
		  initDate:function(dateEle){
			  var dateType=dateEle.data("type");
			  if(!dateType){
				  dateType="date";
			  }
			  var datefmt=dateEle.data("fmt");
			  if(!datefmt){
				  switch (dateType) {
				  case "date":
					  datefmt="yyyy-MM-dd";
					  break;
				  	case "time":
					  datefmt="HH:mm";
					  break;
					case "datetime":
						datefmt="yyyy-MM-dd HH:mm";
						break;
					default:
						datefmt="yyyy-MM-dd";
						break;
					}
			  }
			  var id=dateEle.attr("id");
			  if(!id){
				  id=dateEle.attr("name");
				  id=id.replace(".","");
				  dateEle.attr("id",id);
			  }
			  var minutes=dateEle.data("minutes");
			  var changeHandler=dateEle.data("changehandler");
			  var doneHandler=dateEle.data("donehandler");
			 dateEle.attr("lay-key","");
			 laydate.render({
				 elem:"#"+id,
				 type:dateType, //日期格式
				 format:datefmt,
				 trigger: 'click', //采用click弹出,
				 ready:function(date){
					 if(datefmt.indexOf("ss")==-1&&dateType!="month"&&dateType!="year"){
						 //没有秒就去掉
						 $(".layui-laydate").addClass("noseconds");
						 $(".layui-laydate .laydate-time-list li ol").eq(2).find("li").remove();  //清空秒
					 }
					 if(minutes&&datefmt.indexOf("HH:mm")!=-1){
						 var minutesArr=minutes.split(",");
						 var box= $(".layui-laydate .laydate-time-list li ol").eq(1);
						 box.find("li").each(function(){
							 var value=this.innerText;
							 var inArr=$.inArray(value, minutesArr);
							 if(inArr==-1){
								 $(this).remove();
							 }
						 });
						
					 }
				 },
				 change: function(value, date, endDate){
					 if(changeHandler){
						 var change_handler=eval(changeHandler);
						 if(change_handler&&typeof(change_handler)=="function"){
							 change_handler(dateEle,value);
						 }
					 }
				 },
				 done: function(value, date, endDate){
					 if(doneHandler){
						 var done_handler=eval(doneHandler);
							if(done_handler&&typeof(done_handler)=="function"){
								done_handler(dateEle,value);
							}
					 }
				},
			 }); 
		  
		  },
		  initDates:function(dates){
			  if(!isOk(dates)){return false;}
			  dates.attr("readonly","readonly");
			  var len=dates.length;
			  for(var i=0;i<len;i++){
				 this.initDate(dates.eq(i)); 
			  }
		  },
		  init:function(parentEle){
			  var parent=getRealJqueryObject(parentEle);
			  if(!isOk(parent)){return false;}
			  var dates=parent.find("[data-date]");
			  if(!isOk(dates)){return false;}
			  this.initDates(dates);
		  }
}



//弹出dialog类库
var DialogUtil={
		  openBy:function(ele){
			  var action=$(ele);
			  var url=action.data("url");
			  if(!url){
				  url=action.attr("href");
			  }
			  var contentid=action.data("contentid");
			  if(!url&&!contentid){LayerMsgBox.alert("没有设置dialog的url. href 或者 dialog-url", 2); return false;}
			  var title=action.data("title");
			  var handler=action.attr("handler");
			  if(!handler){
				  handler=action.data("handler");
			  }
			  var dialog_area=action.data("area");
			  var w="800px";
			  var h="500px";
			  if(dialog_area){
				  var area=dialog_area.split(",");
				  var ww=area[0];
				  var hh=area[1];
				  if(ww.indexOf("px")!=-1||ww.indexOf("%")!=-1){
					  w=ww;
				  }else{
					  w=ww+"px";
				  }
				  if(hh.indexOf("px")!=-1||hh.indexOf("%")!=-1){
					  h=hh;
				  }else{
					  h=hh+"px";
				  }
			  }
			  var dialog_scroll=action.data("scroll");
			  if(!dialog_scroll){
				  dialog_scroll="no";
			  }else{
				  dialog_scroll="yes";
			  }
			  var fs=action.data("fs");
			  if(fs&&(fs=="true"||fs==true)){
				  dialog_scroll="yes";
			  }
			  //close dialog and refresh parent
			  var cdrfp=action.data("cdrfp");
			  if(cdrfp==undefined){
				  cdrfp=false;
			  }
			  var portalId=action.data("portalid");
			  if(!portalId){
				  portalId=action.data("portal");
			  }
		      var btn=action.data("btn");
		      this.openNewDialog({
		    	  title:title,
		    	  width:w,
		    	  height:h,
		    	  url:url,
		    	  scroll:dialog_scroll,
		    	  btn:btn,
		    	  handler:handler,
		    	  cdrfp:cdrfp,
		    	  fs:fs,
		    	  portalId:portalId,
		    	  contentId:contentid
		      });
		  },openNewDialog:function(options){
			  var btn=[];
			  var dbtn=options.btn;
			  if(!dbtn){
		    	  btn=["确定", '关闭'];
		      }else if(dbtn&&dbtn=="no"){
		    	  btn=[];
		      }else if(dbtn&&dbtn=="close"){
		    	  btn=["确定", '关闭'];
		      }
			  var type=2;
			  if(!(options.url)&&options.contentId){
				  type=1;
			  }
			  var content="";
			  if(type==1){
				  content=$("#"+options.contentId);
			  }else {
				  content=[options.url,options.scroll];
			  }
			  var lindex=layer.open({
				  type: type,
				  title: options.title,
				  shadeClose: false,
				  shade: 0.5,
				  maxmin:true,
				  area: [options.width, options.height],
				  content:content,
				  btn:btn, 
				  yes:function(index){
					  if(type==2){
						  var iframeWin = window[$(".layui-layer-iframe").find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
						  iframeWin.submitThisForm(function(){
							  LayerMsgBox.close(index);
							  if(options.handler){
								  if(options.handler=="refreshPortal"){
									  if(options.portalId){
										  LayerMsgBox.success("操作成功",500,function(){
											  $("#"+options.portalId).ajaxPortal(true);
										  });
									  }else{
										  LayerMsgBox.alert("没有配置data-portalid",2);
									  }
									 
								  }else{
									  var exe_handler=eval(options.handler);
										if(exe_handler&&typeof(exe_handler)=="function"){
											exe_handler();
										}
								  }
								  
							  }
						  });
					  }
					  
				  },end:function(){
					  if(options.cdrfp){
						  refreshPjaxContainer();
					  }
				  }
			  });
			  if(options.fs){
				  layer.full(lindex);
			  }
			  if(dbtn&&dbtn=="close"){
				  $("#layui-layer"+lindex).find("a.layui-layer-btn0").hide();
			  }
			  if(dbtn&&dbtn=="no"){
				  $("#layui-layer"+lindex).find(".layui-layer-btn").remove();
			  }
		  }
}


/********************************************************************
 ************************表单验证 开始******************************
 ********************************************************************/  
  String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g, "");}
Date.prototype.format = function (fmt) { 
    var o = {
        "M+": this.getMonth() + 1, 
        "d+": this.getDate(), 
        "h+": this.getHours(), 
        "m+": this.getMinutes(),
        "s+": this.getSeconds(), 
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()  
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
  /**
   * 判断对象是否为array
   * @param obj
   * @returns {Boolean}
   */
  function isArray(obj){ 
  	return (typeof obj=='object')&&obj.constructor==Array; 
  } 
  
  function isEmpty(something){
  	return (something=="undefined"||something==null||something==""||something==false);
  	}

  function isNotEmpty(something){
  	return ((something!=null&&something!="undefined"&&something!="")||something==true);
  	}



//摇摆摇摆摇摆起来
  jQuery.fn.shake = function(intShakes /*Amount of shakes*/, intDistance /*Shake distance*/, intDuration /*Time duration*/) {
      this.each(function() {
          var jqNode = $(this);
          jqNode.css({position: 'relative'});
          for (var x=1; x<=intShakes; x++) {
              jqNode.animate({ left: (intDistance * -1) },(((intDuration / intShakes) / 4)))
              .animate({ left: intDistance },((intDuration/intShakes)/2))
              .animate({ left: 0 },(((intDuration/intShakes)/4)));
          }
      });
      return this;
  }
  function processCheckTab(input){
  	var tabpanel=input.closest("div[role='tabpanel']");
  	if(tabpanel&&tabpanel.size()==1){
  		var id=tabpanel.attr("id");
  		$(".nav.nav-tabs .nav-item.nav-link[href='#"+id+"']").tab("show");
  	}
  }
  
  function showItCheckFailResult(input){
	  if(input.is("input")&&input.attr("type")=="hidden"){return;}
  		  input.parents(".form-group").removeClass("bdc-success").addClass("bdc-danger");
          input.shake(2,10,400);
          input.focus();
  }
  
  function showItCheckSuccessResult(input){
	  	if(input.is("input")&&input.attr("type")=="hidden"){return;}
    	input.parents(".form-group").removeClass("bdc-danger").addClass("bdc-success");
	  }
//调用正则表达式验证
  function TestRgexp(re, s) {
      return re.test(s);
  }
  var vcity={ 11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",
          21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",
          33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",
          42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",
          51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",
          63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"
         };
  
var IdCardNoCheckUtil={
		//检查号码是否符合规范，包括长度，类型
		isCardNo : function(card)
		{
		    //身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
		    var reg = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
		    if(!reg.test(card))
		    {
		        return false;
		    }

		    return true;
		},

		//取身份证前两位,校验省份
		checkProvince  : function(card)
		{
		    var province = card.substr(0,2);
		    if(vcity[province] == undefined)
		    {
		        return false;
		    }
		    return true;
		},

		//检查生日是否正确
		checkBirthday  : function(card)
		{
		    var len = card.length;
		    //身份证15位时，次序为省（3位）市（3位）年（2位）月（2位）日（2位）校验位（3位），皆为数字
		    if(len == '15')
		    {
		        var re_fifteen = /^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/;
		        var arr_data = card.match(re_fifteen);
		        var year = arr_data[2];
		        var month = arr_data[3];
		        var day = arr_data[4];
		        var birthday = new Date('19'+year+'/'+month+'/'+day);
		        return this.verifyBirthday('19'+year,month,day,birthday);
		    }
		    //身份证18位时，次序为省（3位）市（3位）年（4位）月（2位）日（2位）校验位（4位），校验位末尾可能为X
		    if(len == '18')
		    {
		        var re_eighteen = /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/;
		        var arr_data = card.match(re_eighteen);
		        var year = arr_data[2];
		        var month = arr_data[3];
		        var day = arr_data[4];
		        var birthday = new Date(year+'/'+month+'/'+day);
		        return this.verifyBirthday(year,month,day,birthday);
		    }
		    return false;
		},

		//校验日期
		verifyBirthday  : function(year,month,day,birthday)
		{
		    var now = new Date();
		    var now_year = now.getFullYear();
		    //年月日是否合理
		    if(birthday.getFullYear() == year && (birthday.getMonth() + 1) == month && birthday.getDate() == day)
		    {
		        //判断年份的范围（3岁到100岁之间)
		        var time = now_year - year;
		        if(time >= 3 && time <= 100)
		        {
		            return true;
		        }
		        return false;
		    }
		    return false;
		},

		//校验位的检测
		checkParity  : function(card)
		{
		    //15位转18位
		    card = this.changeFivteenToEighteen(card);
		    var len = card.length;
		    if(len == '18')
		    {
		        var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
		        var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
		        var cardTemp = 0, i, valnum;
		        for(i = 0; i < 17; i ++)
		        {
		            cardTemp += card.substr(i, 1) * arrInt[i];
		        }
		        valnum = arrCh[cardTemp % 11];
		        if (valnum == card.substr(17, 1))
		        {
		            return true;
		        }
		        return false;
		    }
		    return false;
		},

		//15位转18位身份证号
		changeFivteenToEighteen  : function(card)
		{
		    if(card.length == '15')
		    {
		        var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
		        var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
		        var cardTemp = 0, i;  
		        card = card.substr(0, 6) + '19' + card.substr(6, card.length - 6);
		        for(i = 0; i < 17; i ++)
		        {
		            cardTemp += card.substr(i, 1) * arrInt[i];
		        }
		        card += arrCh[cardTemp % 11];
		        return card;
		    }
		    return card;
		}
		  
}
/**
 * 校验身份证号
 */
function checkIdCardNo(card){
	    //是否为空
	    if(card === '')
	    {
	        LayerMsgBox.alert('请输入身份证号，身份证号不能为空',2);
	        return false;
	    }
	    //校验长度，类型
	    if(IdCardNoCheckUtil.isCardNo(card) === false)
	    {
	    	 LayerMsgBox.alert('您输入的身份证号码不正确，请重新输入',2);
	        return false;
	    }
	    //检查省份
	    if(IdCardNoCheckUtil.checkProvince(card) === false)
	    {
	    	 LayerMsgBox.alert('您输入的身份证号码不正确,请重新输入',2);
	        return false;
	    }
	    //校验生日
	    if(IdCardNoCheckUtil.checkBirthday(card) === false)
	    {
	    	 LayerMsgBox.alert('您输入的身份证号码生日不正确,请重新输入',2);
	        return false;
	    }
	    //检验位的检测
	    if(IdCardNoCheckUtil.checkParity(card) === false)
	    {
	    	 LayerMsgBox.alert('您的身份证校验位不正确,请重新输入',2);
	        return false;
	    }
	    return true;
  }
//验证规则map
  var ruleMap=[
      {type:"number",method:function(value){return (!isNaN(value));}},//数字校验
      {type:"pznumber",method:function(value){return (!isNaN(value)&&value*1>=0);}},//验证正数和0
      {type:"pzint",method:function(value){return ((TestRgexp(/^-?[0-9]\d*$/, value))&&(value*1>=0));}},//验证正数和0
      {type:"pnumber",method:function(value){return (!isNaN(value)&&value*1>0);}},//验证正数
      {type:"int",method:function(value){return TestRgexp(/^-?[0-9]\d*$/, value);}},//整数校验
      {type:"pint",method:function(value){return TestRgexp(/^[0-9]*[1-9][0-9]*$/, value);}},//正整数校验
      {type:"email",method:function(value){return TestRgexp(/\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/, value);}},//Email校验
      {type:"filepath",method:function(value){return TestRgexp(/^([a-zA-Z]){1}:(\\[^\/\\:\*\?\"<>]+)*(\\)?$/, value);}},//URL校验
      {type:"url",method:function(value){return TestRgexp(/^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/, value);}},//URL校验
      {type:"url_nohttp",method:function(value){return TestRgexp(/^((https|http|ftp|rtsp|mms){0,1}(:\/\/){0,1})(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/, value);}},//URL校验
      {type:"date",method:function(value){return TestRgexp(/^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/,value);}},//日期验证 2019-01-01
      {type:"time",method:function(value){return TestRgexp(/^(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/,value);}},//时间验证 12:59:59
      {type:"time_hm",method:function(value){return TestRgexp(/^(20|21|22|23|[0-1]\d):[0-5]\d$/,value);}},//时间验证 12:59
      {type:"datetime",method:function(value){return TestRgexp(/^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/,value);}},//完整日期时间验证 2019-01-01 12:22:23
      {type:"datetime_hm",method:function(value){return TestRgexp(/^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d$/,value);}},//日期时间验证 2019-01-01 12:22
      {type:"money",method:function(value){return TestRgexp(/(^[1-9]\d*(\.\d{1,2})?$)|(^0(\.\d{1,2})?$)/,value);}},//金额 保留2位小数
      {type:"money_4",method:function(value){return TestRgexp(/(^[1-9]\d*(\.\d{1,4})?$)|(^0(\.\d{1,4})?$)/,value);}},//金额 保留4位 小数
      {type:"phone",method:function(value){return TestRgexp(/0?(13|14|15|18)[0-9]{9}/,value);}},//手机
      {type:"tel",method:function(value){return TestRgexp(/[0-9-()（）]{7,18}/,value);}},//座机电话
      {type:"zh_cn",method:function(value){return TestRgexp(/[\u4e00-\u9fa5]/,value);}},//中文
      {type:"ip",method:function(value){return TestRgexp(/(25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)\.(25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)\.(25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)\.(25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)/,value);}},//ip地址
      {type:"postalcode",method:function(value){return TestRgexp(/\d{6}/,value);}},//邮政编码
      {type:"idcardno",method:function(value){return checkIdCardNo(value);}},//15or18位身份证
      {type:"password",method:function(value){
    	  if(!value){return false;}
    	  var len=value.length;
    	  return len>=6&&len<=16;
      }}
  ];
  var isDOM = ( typeof HTMLElement === 'object' ) ?
          function(obj){
              return obj instanceof HTMLElement;
          } :
          function(obj){
              return obj && typeof obj === 'object' && obj.nodeType === 1 && typeof obj.nodeName === 'string';
          }
  /**
   * 表单验证器
   */
  var FormChecker={
  //检查form表单中的input textarea select
		   checkIt:function(checkObj){
			   var self=this;
			   var input=null;
				  if(typeof checkObj =="string"){
					  input=$("#"+checkObj);
				  }else if(typeof checkObj=="object"){
					  if(isDOM(checkObj)){
						  input=$(checkObj); 
					  }else{
						  input=checkObj;
					  }
				  }
			   var error=true;
		          if(input.is(":disabled")){
		        	  return true;
		          }
		          if(typeof(input.data("rule"))!="undefined"){
		              var flag = self.checktype(input);
		              var next=input.next().hasClass("input-group-addon");
		              var prev=input.prev().hasClass("input-group-addon");
		              var ta=input.is("textarea");
		              if (!flag) {
		              	error=false;
		              	
		              	if(input.is("input")&&input.attr("type")=="hidden"){return;}
		              	input.parents(".form-group").removeClass("bdc-success").addClass("bdc-danger");
		              	
		              	processCheckTab(input);
		                  input.shake(2,10,400);
		                  input.focus();
		                  if(input.is("input")&&input.attr("type")=="file"){return;}
		                  
		              }else{
		              	error=true;
		              	if(input.is("input")&&input.attr("type")=="hidden"){return;}
		              	
		              	input.parents(".form-group").removeClass("bdc-danger").addClass("bdc-success");
		              	if(input.is("input")&&input.attr("type")=="file"){return;}
		              }
		          }
		          return error;
		   },
  check:function(form){
	  var checkForm=null;
	  if(typeof form =="string"){
		  checkForm=$("#"+form);
	  }else if(typeof form=="object"){
		  if(isDOM(form)){
			  checkForm=$(form); 
		  }else{
			  checkForm=form;
		  }
	  }
	  var self=this;
      var error=true;
      checkForm.find("input,textarea,select,[data-rule='checkbox'],[data-rule='radio']").each(function(){
          if(!error){
              return error;
          }
          var input=$(this);
          if(input.is(":disabled")){
        	  return true;
          }
          if(typeof(input.data("rule"))!="undefined"){
              var flag = self.checktype(input);
              var next=input.next().hasClass("input-group-addon");
              var prev=input.prev().hasClass("input-group-addon");
              var ta=input.is("textarea");
              if (!flag) {
              	error=false;
              	
              	if(input.is("input")&&input.attr("type")=="hidden"){return;}
              	input.parents(".form-group").removeClass("bdc-success").addClass("bdc-danger");
              	
              	processCheckTab(input);
                  input.shake(2,10,400);
                  input.focus();
                  if(input.is("input")&&input.attr("type")=="file"){return;}
                  
              }else{
              	error=true;
              	if(input.is("input")&&input.attr("type")=="hidden"){return;}
              	
              	input.parents(".form-group").removeClass("bdc-danger").addClass("bdc-success");
              	if(input.is("input")&&input.attr("type")=="file"){return;}
              }
          }
      });
     
      return error;
  },
  checkCheckboxRequired:function(input,mytips,show){
	  var self=this;
	  var name=input.data("name");
	 	 if(!name){
	 		 return false;
	 	 }
	 	var checked= $("input[type='checkbox'][name='"+name+"']:checked");
	 	if(checked&&checked.length>0){
	 		input.removeClass("bdc-danger").addClass("bdc-success");
	 		return true;
	 	}  
	 	input.removeClass("bdc-success").addClass("bdc-danger");
	 	input.shake(2,10,400);
	 	input.focus();
	 	var tips=input.data("tips");
	 	if(!tips){
	 		tips="必须选择至少一个选项";
	 	}
	 	self.showMyTipsIfNeed(input,tips,show);
	 	return false;
  },
  checkSelectRequired:function(select,mytips,show){
	  var self=this;
	  var value=select.val();
	  var selected=(value&&value!=""&&value!="0"&&value!="-1"&&value.length>0);
	 	if(selected){
	 		select.removeClass("bdc-danger").addClass("bdc-success");
	 		return true;
	 	}  
	 	select.removeClass("bdc-success").addClass("bdc-danger");
	 	select.shake(2,10,400);
	 	select.focus();
	 	var tips=select.data("tips");
	 	if(!tips){
	 		tips="必须选择一个选项";
	 	}
	 	self.showMyTipsIfNeed(select,tips,show);
	 	return false;
  },
  checkRadioRequired:function(input,mytips,show){
	  var self=this;
	  var name=input.data("name");
	 	 if(!name){
	 		 return false;
	 	 }
	 	var checked= $("input[type='radio'][name='"+name+"']:checked");
	 	if(checked&&checked.length>0){
	 		input.removeClass("bdc-danger").addClass("bdc-success");
	 		return true;
	 	}  
	 	input.removeClass("bdc-success").addClass("bdc-danger");
	 	input.shake(2,10,400);
	 	input.focus();
	 	var tips=input.data("tips");
	 	if(!tips){
	 		tips="必须选择至少一个选项";
	 	}
	 	self.showMyTipsIfNeed(input,tips,show);
	 	return false;
  },
  //检查给定一个input textarea select的值
  checktype:function(input) {
	  var self=this;
      var type=input.data("rule");
      if(!type){return true;}
      var mytips=input.data("tips"),
        show=input.data("show"),
        notNull=input.data("notnull");
      var value=null;
     if(type=="checkbox"){
    	if(notNull==null||notNull==true||notNull=="true"){
    		return self.checkCheckboxRequired(input,mytips,show);
    	}else{
    		return true;
    	}
    		
     }else  if(type=="radio"){
    	 if(notNull==null||notNull==true||notNull=="true"){
    		 return self.checkRadioRequired(input,mytips,show);
    	 }else{
    		 return true;
    	 }
     }else  if(type=="select"){
    	if(notNull==null||notNull==true||notNull=="true"){
    		return self.checkSelectRequired(input,mytips,show);
    	}else{
    		return true;
    	}
    		
     }else{
    	 value=input.val();
     }
      
      if (show != null) {
          g(show).innerHTML = "";
      }
      //检测是否为空
  	if(typeof value=="string"){
  		value=value.trim();
  	}
          if (!value) {
              //检查配置是否需要进行非空检测
              if(notNull==null||notNull==true||notNull=="true"){
                      if(!mytips){
                    	  if(input.attr("type")=="file"){
                    		  mytips="此文件必须上传";
                    	  }else{
                    		  mytips="必填项";
                    	  }
                      }
                          if (show == null) {
                        	  LayerMsgBox.error("<span class='j_text_danger'>"+mytips+"</span>",1500);
                        	 /* var pos=input.data("pos");
                        	  if(!pos){pos=2;}
                          	layer.tips(mytips,input,{
                          		tips: [pos, '#d9534f'],
                          		time:4000
                          	});*/
                          } else {
                              g(show).innerHTML = mytips;
                          }
                     
              return false;
              }else{
              return true;
              }
           }
      return self.checkTypes(input,type,value,mytips,show,notNull);

  },
  //检测数据类型
  checkTypes:function(input,type,value,mytips,show,notNull){
	  var self=this;
      //验证多个的时候
      var types=type.split(";");
      var checkFlag=true;
      var error=1;
      var success=2;
      var canNotCheck=3;
      for(var i=0;i<types.length;i++){
          var type=types[i];
          var checkResult=self.checkSelf(input,type,value,mytips,show,notNull);
          if(checkResult!=canNotCheck){//判断能否处理 如果处理了 成功了继续下一个type 失败了则直接整个结束
              if(checkResult==success){
                  continue;
              }else{
                  checkFlag=false;
                  break;
              }
          }
          
          //如果上面不能处理 则进入比较处理
          checkResult=self.checkCompare(input,type,value,mytips,show,notNull);
          if(checkResult!=canNotCheck){//判断能否处理 如果处理了 成功了继续下一个type 失败了则直接整个结束
              if(checkResult==success){
                  continue;
              }else{
                  checkFlag=false;
                  break;
              }
          }
          
          
      }
      return checkFlag;
  },

  //检测比较
   checkCompare:function(input,type,value,mytips,show,notNull){
	   var self=this;
      var error=1;
      var success=2;
      var canNotCheck=3;
      var selfValue=self.getRealTypeValue(value);
      var compareValue=self.getCompareValue(type);
      if(type.indexOf("len=")!=-1){
          if(value.length==compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("len>=")!=-1){
          if(value.length>=compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须大于等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("len>")!=-1){
          if(value.length>compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须大于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf(">=")!=-1){
      	if(selfValue>=compareValue){
      		return success;
      	}
        if(!mytips){
      	  mytips="必须大于等于"+compareValue;
        }
      	self.showMyTipsIfNeed(input,mytips,show);
      	return error;
      }else if(type.indexOf("len<=")!=-1){
          if(value.length<=compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须小于等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("len<")!=-1){
          if(value.length<compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="长度必须小于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("<=")!=-1){
      	if(selfValue<=compareValue){
      		return success;
      	}
        if(!mytips){
      	  mytips="必须小于等于"+compareValue;
        }
      	self.showMyTipsIfNeed(input,mytips,show);
      	return error;
      }else if(type.indexOf(">")!=-1){
          if(selfValue>compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="必须大于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("<")!=-1){
          if(selfValue<compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="必须小于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("!=")!=-1){
          if(selfValue!=compareValue){
              return success;
          }
          if(!mytips){
        	  mytips="不能等于"+compareValue;
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }else if(type.indexOf("==")!=-1){
          if(selfValue==compareValue){
              return success;
          }
          if(!mytips){
        	  if(type.indexOf("#")!=-1){
        		  mytips="必须等于id为["+type.substring(type.indexOf("#")+1)+"]元素的值";
        	  }else{
        		  mytips="必须等于"+compareValue;
        	  }
          }
          self.showMyTipsIfNeed(input,mytips,show);
          return error;
      }

      return canNotCheck;
  },
  //得到正确类型的值
  getRealTypeValue:function(value){
      if(!isNaN(value)){
          return Number(value);
      }
      return value;
  },
  //得到需要比较的值
  getCompareValue:function(type){
	  var self=this;
      if(type.indexOf("#")!=-1){
          var cid=type.substring(type.indexOf("#"));
          return self.getRealTypeValue($(cid).val());
      }else{
    	if(type.indexOf("len=")!=-1){
    		  return self.getRealTypeValue(type.substring(4));
        }
      	if(type.indexOf("len")!=-1){
      		type=type.substring(3);
      	}
      	
          if(type.indexOf("=")!=-1){
              return self.getRealTypeValue(type.substring(2));
          }else{
              return self.getRealTypeValue(type.substring(1));
          }
      }
  },
  //显示提示信息
   showMyTipsIfNeed:function(input,mytips,show){
      if(mytips!=null){
                  if (show == null) {
                      LayerMsgBox.error("<span class='j_text_danger'>"+mytips+"</span>",3000);
                	  /*var pos=input.data("pos");
                	  if(!pos){pos=2;}
                	  layer.tips(mytips,input,{
                		  tips: [pos, '#d9534f'],
                    	  time:4000
                	  });*/
                  } else {
                      g(show).innerHTML = mytips;
                  }
       }
  },
  

  //检测自身
   checkSelf:function(input,type,value,mytips,show,notNull){
	   var self=this;
      var error=1;
      var success=2;
      var canNotCheck=3;
      if(type=="string"){
          return success;
      }
      var process=false;
      for(var i=0;i<ruleMap.length;i++){
          if(type==ruleMap[i].type){
              process=true;
              var result=ruleMap[i].method.call(this,value);
              if (!result) {
            	  if(!mytips){
            		  switch (type) {
            		  case "email":
            			  mytips="Email格式不正确";
            			  break;
            		  case "phone":
            			  mytips="手机号格式不正确";
            			  break;
            		  case "tel":
            			  mytips="电话号码格式不正确";
            			  break;
            		  case "money":
            			  mytips="必须为金额(价格)格式 默认最多小数点后保留2位";
            			  break;
            		  case "money_4":
            			  mytips="必须为金额(价格)格式 最多小数点后保留4位";
            			  break;
            		  case "number":
            			  mytips="必须为数字";
            			  break;
            		  case "pnumber":
            			  mytips="必须为正数";
            			  break;
            		  case "pznumber":
            			  mytips="不能为负数";
            			  break;
            		  case "pzint":
            			  mytips="必须为0或正整数";
            			  break;
            		  case "int":
            			  mytips="必须为整数";
            			  break;
            		  case "pint":
            			  mytips="必须为正整数";
            			  break;
            		  case "date":
            			  mytips="必须为日期格式 例：2019-01-01";
            			  break;
            		  case "time":
            			  mytips="必须为时间格式 例：12:59:59";
            		  case "time_hm":
            			  mytips="必须为时间格式 例：12:59";
            			  break;
            		  case "datetime":
            			  mytips="必须为完整日期时间格式 例：2019-01-01 12:59:59";
            			  break;
            		  case "datetime_hm":
            			  mytips="必须为日期时间格式 例：2019-01-01 12:59";
            			  break;
            		  case "idnum":
            			  mytips="必须为18位身份证号 例：370523198901033922";
            			  break;
            		  case "postalcode":
            			  mytips="必须为6位邮政编码 例：6473098";
            			  break;
            		  case "zh_cn":
            			  mytips="必须为中文";
            			  break;
            		  case "ip":
            			  mytips="必须为IP地址正确格式 例：127.0.0.1";
            			  break;
						case "password":
							mytips="密码长度必须为6~16个字符";
							break;
						case "select":
							mytips="必须选择一项";
							break;
					}
            	  }
            	  self.showMyTipsIfNeed(input,mytips,show);
                  return error;
              }
          }
      }
      return process?success:canNotCheck;
   
      
  }
  
  
  }
  /********************************************************************
   ************************表单验证 结束******************************
   ********************************************************************/  
 

/**
 * 初始化左侧导航菜单
 */
function initAdminLeftNav(){
/*	var value=localStorage.getItem('allexpansion');
	if(value=="true"){
		var jbolt_admin=$(".jbolt_admin_left_navs");
		jbolt_admin.addClass("allexpansion");
		jbolt_admin.find(".jbolt_menu_group i.fa.fa-angle-left").removeClass("fa-angle-left").addClass("fa-angle-down");
	}*/
	
	var value=localStorage.getItem('jbolt_hideMenu');
	if(value=="true"){
		$(".jbolt_admin_nav.expansion").removeClass("expansion");
		$(".jbolt_admin").addClass("hideMenu");
	}else{
		$(".jbolt_admin").addClass("normalMenu");
	}
	resizeMorris();
	
	jboltBody.on("click",".jbolt_admin_left_navs .jbolt_admin_nav .jbolt_menu_group",function(){
		$(".jbolt_admin").removeClass("hideMenu").addClass("normalMenu");
		localStorage.setItem('jbolt_hideMenu', false);
		var jbolt_admin_left_navs=$(".jbolt_admin_left_navs");
		
		var h1=$(this);
		var nav=h1.closest("nav");
		var expansion=$(".jbolt_admin_left_navs .jbolt_admin_nav.expansion");
		if(!h1.hasClass("l1link")){
			if(jbolt_admin_left_navs.hasClass("allexpansion")){return false;}
			var pkey=expansion.data("key");
			var hpkey=nav.data("key");
			
				var fa=h1.find("i.fa").last();
				if(fa.hasClass("fa-angle-down")){
					fa.removeClass("fa-angle-down").addClass("fa-angle-left");
				}else{
					fa.removeClass("fa-angle-left").addClass("fa-angle-down");
				}
				if(pkey!=hpkey){
					expansion.removeClass("expansion");
				}
				$(this).parent().toggleClass("expansion");
			
		}else{
			expansion.removeClass("expansion");
			expansion.find(".jbolt_menu_group i.fa.fa-angle-down").removeClass("fa-angle-down").addClass("fa-angle-left");
			nav.addClass("expansion");
			nav.find(".jbolt_menu_group i.fa.fa-angle-left").removeClass("fa-angle-left").addClass("fa-angle-down");
		}
		
	});
	
	jboltBody.on("mouseover",".jbolt_admin.hideMenu  nav.jbolt_admin_nav",function(e){
		var menusBox=$(this).find("ul.jbolt_admin_menus").first(),
		top=parseInt(menusBox.css("top")),
		height=(jboltWindowHeight-top)+"px";
		menusBox.css({"max-height":height});
	});
}




function sendPjax(url, container, extData) {
	$.pjax({
		url: url
		, container: container
		, extData: extData
	});
}

function pjaxSubmitForm(formEle){
	var form=$(formEle);
	var url=formEle.action;
	var datas=form.serialize();
	if(url.indexOf("?")!=-1){
		url=url+"&"+datas;
	}else{
		url=url+"?"+datas;
	}
	$.pjax({
		url: url
		, container: mainPjaxContainerId
	});
	return false;
}
/**
 * 初始化 后台管理的Pjax
 * @returns
 */
function initAdminPjax(){
	$.pjax.defaults.timeout=5000;
	jboltDocument.pjax('a[data-pjax]:not([target]),[data-pjax] a:not([target])', mainPjaxContainerId);
	jboltDocument.on('pjax:timeout', function(event) {
		LayerMsgBox.closeAll();
  		event.preventDefault();
	});
	  //支持表单提交事件无刷新
	   jboltDocument.on('submit', 'form[data-pjaxsubmit]', function (event) {
		   var needcheck=$(this).data("needcheck");
		   if(needcheck){
			   if(FormChecker.check(this)){
				   $.pjax.submit(event, mainPjaxContainerId);
			   }
		   }else{
			   $.pjax.submit(event, mainPjaxContainerId);
		   }
		   return false;
	    });
	jboltDocument.on('pjax:start', function() {
		LayerMsgBox.load(3);
	});
	jboltDocument.on('pjax:end', function() {
		afterPjax();
		LayerMsgBox.closeAll();
	});
	//pjax需要处理的
	afterPjax();
}
/**
 * 刷新当前显示的选项卡内容区域
 * @returns
 */
function refreshCurrentTabContent(){
	var tabContent=JBoltTabUtil.getCurrentTabContent();
	if(tabContent&&tabContent.length==1){
		LayerMsgBox.load(3);
		tabContent.ajaxPortal(true,null,true,function(){
			LayerMsgBox.closeLoad();
		});
	}
}

function isIE() { //ie?
	 if (!!window.ActiveXObject || "ActiveXObject" in window)
	  return true;
	  else
	  return false;
}
/**
 * 刷新主区域
 * @returns
 */
function refreshPjaxContainer(){
	if(self!=top){
		reloadCurrentPage();
	}else{
		var withTabs=isWithtabs();
		if(withTabs){
			refreshCurrentTabContent();
		}else{
			if(isIE()){
				reloadCurrentPage();
			}else{
				$.pjax.reload(mainPjaxContainerId);
			}
		}
	}

}
/**
 * 检测打开的pjax页面 打开打开指定的nav
 * @returns
 */
function initOpenLeftNav(){
	var withTabs=isWithtabs();
	var jbolt_page=null;
	if(withTabs){
		var tabContent=JBoltTabUtil.getCurrentTabContent();
		if(tabContent&&tabContent.length==1){
			jbolt_page=tabContent.find("div.jbolt_page[data-key]:first");
		}
	}else{
		jbolt_page=$("div.jbolt_page[data-key]:first");
	}
	if(jbolt_page&&jbolt_page.length==1){
		var key=jbolt_page.data("key");
		var dontchangeleftnav=jbolt_page.data("dontchangeleftnav");
		if(!dontchangeleftnav){
			openLeftNav(key);
		}
	}
	
}



/**
 * 打开指定key的左侧nav
 * @param key
 * @returns
 */
function openLeftNav(key){
	var activeItem=$(".jbolt_admin_left_navs nav.jbolt_admin_nav a.active");
	if(activeItem.attr("href")==key){
		return false;
	}
	activeItem.removeClass("active");
	var expansion=$(".jbolt_admin_nav.expansion");
	expansion.removeClass("expansion");
	expansion.find(".jbolt_menu_group i.fa.fa-angle-down").removeClass("fa-angle-down").addClass("fa-angle-left");
	
	
	var item=$(".jbolt_admin_left_navs nav.jbolt_admin_nav a[href='"+key+"']");
	item.addClass("active");
	var nav=item.closest("nav");
	if(nav&&nav.size()==1){
		nav.addClass("expansion");
		nav.find(".jbolt_menu_group i.fa.fa-angle-left").removeClass("fa-angle-left").addClass("fa-angle-down");
	}
	
	
	
}

/**
 * pajx end执行后 调用 初始化pjax内容里的一些组件
 * @returns
 */
function afterPjax(){
	//加载界面后需要选中打开左侧选项卡
	$('.tooltip.show').remove();
	initOpenLeftNav();
	SelectUtil.initAutoSetValue(mainPjaxContainer);
	JAtomFileUploadUtil.init(mainPjaxContainer);
	JAtomImgUploader.init(mainPjaxContainer);
	SwitchBtnUtil.init(mainPjaxContainer);
	FormDate.init(mainPjaxContainer);
	LayerTipsUtil.init(mainPjaxContainer);
	HtmlEditorUtil.init(mainPjaxContainer);
	MasterSlaveUtil.init(mainPjaxContainer);
	ImageViewerUtil.init(mainPjaxContainer);
	RadioUtil.init(mainPjaxContainer);
	CheckboxUtil.init(mainPjaxContainer);
	AutocompleteUtil.init(mainPjaxContainer);
	Select2Util.init(mainPjaxContainer);
	SelectUtil.init({parent:mainPjaxContainer});
	initToolTip(mainPjaxContainer);
	MultipleFileInputUtil.init(mainPjaxContainer);
	initAjaxPortal(mainPjaxContainer);
}
/**
 * 得到checkbox选中的数据值
 * @param name
 * @returns
 */
function getCheckedIds(name){
	var ids=new Array();
	var inputs=$("input[name='"+name+"']:checked");
	inputs.each(function(i){
			ids.push($(this).val());
	});
	if(ids.length>0){
		return ids.join(",");
	}
	
	return "";
}

/**
 * 分页提交form
 */
function jboltPageSubmitForm(pbox,pager,form,page){
	var pages=pager.closest(".pages");
	  if(!page){
		  var input=pages.find("#gonu");
		  if(input&&input.length>0){
			  page=input.val();
		  }else{
			  page=1;
		  }
	  }
	  var pageSize=pages.find("#jbolt_pagesize").val();
	  form.append('<input type="hidden" name="page" value="'+page+'"/>')
	  form.append('<input type="hidden" name="pageSize" value="'+pageSize+'"/>')
//	  var action=baseUrl+"?page="+page;
//	  form.attr("action",action);
	  form.submit();
}

function initPage(id,totalPage,pageNumber,formId){
	var withTabs=isWithtabs();
	var pbox=null;
	var pager=$("#"+id);
	var ajaxPortal=pager.closest("[data-ajaxportal]");
	if(ajaxPortal&&ajaxPortal.length==1){
		pbox=ajaxPortal;
	}else{
		if(withTabs){
			pbox=JBoltTabUtil.getCurrentTabContent();
		}else{
			pbox=$(mainPjaxContainer);
			var inDialog=!(pbox&&pbox.length==1);
			if(inDialog){
				pbox=$("body .jbolt_page");
				var notNormalPage=!(pbox&&pbox.length==1);
				if(notNormalPage){
					pbox=jboltBody;
				}
			}
		}
	}
	if(!pbox){
		return false;
	}
	var form=pbox.find("#"+formId);
	var baseUrl = form.attr("action");
	pager.pagination(totalPage,{
		   num_edge_entries:1,
		   current_page:(pageNumber-1),
			callback:function(index,ct){
				if(isNaN(index)==false){
					var page=index+1;
					jboltPageSubmitForm(pbox,pager,form,page);
					return false;
				}
			}
	   });
	  
	pbox.find("#gonu").one("keydown",function(e){
		   if(e.keyCode==109||e.keyCode==189){
			   return false;
		   }
		  
		 
	   });
	pbox.find(".page-btn").one("click",function(){
		jboltPageSubmitForm(pbox,pager,form);
	});
	pbox.find("#jbolt_pagesize").one("change",function(){
		jboltPageSubmitForm(pbox,pager,form,1);
	   });
}

/**
 * 切换左侧导航菜单
 * @returns
 */
function toggleMenuEvent(){
	jboltBody.on("click",".jbolt_toggle_Left_nav_btn",function(){
		$(this).toggleClass("hidden");
		$(".jbolt_admin_nav.expansion").removeClass("expansion");
		$(".jbolt_admin").toggleClass("hideMenu").toggleClass("normalMenu");
		$(".jbolt_admin_left_navs nav.jbolt_admin_nav a.active").removeClass("active");
		var hideMenu=$(".jbolt_admin").hasClass("hideMenu");
		if(!hideMenu){
			initOpenLeftNav();
		}
		localStorage.setItem('jbolt_hideMenu', hideMenu);
		resizeMorris();
	});
}
/**
 * 如果当前页面有morris图表 resize处理
 * @returns
 */
function resizeMorris(){
	setTimeout(function(){
		windowResize();
	}, 1000);
}
/**
 * 自动触发window的resize
 * @returns
 */
function windowResize(){
	 if(document.createEvent) {
         var event = document.createEvent("HTMLEvents");
         event.initEvent("resize", true, true);
         window.dispatchEvent(event);
     } else if(document.createEventObject) {
         window.fireEvent("onresize");
     }
}
/**
 * 返回上一页
 * @returns
 */
function goback(){
	history.go(-1);
}
/**
 * 刷新当前页
 * @returns
 */
function reloadCurrentPage(){
	var url=self.location.href;
	if(url.indexOf("globalconfig")!=-1){
		self.location.href="/admin";
	}else{
		history.go(0);
	}
}
/**
 * 隐藏dialog按钮
 * @returns
 */
function hideParentLayerDialogBtn(index){
	if(index==0||index==1){
		parent.$(".layui-layer-btn .layui-layer-btn"+index).hide();
	}else{
		hideAllParentLayerDialogBtn();
	}
}
/**
 * 隐藏Layer上的所有按钮
 * @returns
 */
function hideAllParentLayerDialogBtn(){
	parent.$(".layui-layer-btn").hide();
}
 

/**
 * 修改按钮标题
 * @returns
 */
function changeParentLayerDialogBtnTitle(index,btnTitle){
	parent.$(".layui-layer-btn .layui-layer-btn"+index).text(btnTitle);
}
/**
 * 修改Dialog上OK按钮标题
 * @returns
 */
function changeParentLayerDialogOkBtnTitle(btnTitle){
	changeParentLayerDialogBtnTitle(0,btnTitle);
}
/**
 * 修改Dialog上Cancel按钮标题
 * @returns
 */
function changeParentLayerDialogCancelBtnTitle(btnTitle){
	changeParentLayerDialogBtnTitle(1,btnTitle);
}
/**
 * 得到按钮
 * @param index
 * @returns
 */
function getParentLayerDialogBtn(index){
	  return parent.$(".layui-layer-btn .layui-layer-btn"+index);
}
/**
 * 显示dialog按钮
 * @returns
 */
function showParentLayerDialogBtn(index){
	  if(index){
		  parent.$(".layui-layer-btn .layui-layer-btn"+index).show();
	  }else{
		  parent.$(".layui-layer-btn").show();
	  }
	  
}
/**
 * 检测是否存在相同按钮
 * @param title
 * @param cssClass
 * @returns
 */
function checkExistLayerDialogBtn(title,cssClass){
	 var sameClassBtns=parent.$(".layui-layer-btn").find("a."+cssClass);
	 var existBtn=null;
	 if(sameClassBtns&&sameClassBtns.length>0){
		 sameClassBtns.each(function(){
			 var btn=$(this);
			 if($.trim(btn.text())==$.trim(title)){
				 existBtn=btn;
				 return false;
			 }
		 });
	 }
	 return existBtn;
}
/**
 * 添加按钮
 * @param title
 * @param cssClass
 * @param clickFunc
 * @returns
 */
function addParentLayerDialogBtn(title,cssClass,clickFunc){
	var existTitleBtn=checkExistLayerDialogBtn(title,cssClass);
	var btnId;
	if(existTitleBtn==null){
		var rId=randomId();
		btnId="lay_btn_"+rId;
		var btn=$("<a id='"+btnId+"' class='"+cssClass+"'>"+title+"</a>");
		btn.on("click",function(e){
			e.preventDefault();
			e.stopPropagation();
			if(clickFunc){
				clickFunc();
			}
		});
		parent.$(".layui-layer-btn").find("a.layui-layer-btn0").after(btn);
	}else{
		btnId=existTitleBtn.attr("id");
		existTitleBtn.off("click").on("click",function(e){
			e.preventDefault();
			e.stopPropagation();
			if(clickFunc){
				clickFunc();
			}
		});
	}
	 return btnId;
}
/**
 * 通过ajax提交form
 * @param formEle
 * @returns
 */
function ajaxSubmitForm(formEle,callback){
	var form=getRealJqueryObject(formEle);
	 if(isOk(form)){
		if(FormChecker.check(form)){
			var url=form.action;
			form.ajaxSubmit({
				type:"post",
				url:url,
				success:function(ret){
					if(ret.state=="ok"){
						if(callback){
							LayerMsgBox.success(ret.msg,500,callback);
						}else{
							LayerMsgBox.success(ret.msg,500);
						}
					}else{
						LayerMsgBox.alert(ret.msg,2);
					}
				}
			});
		}
	}

	return false;

}
/**
 * 得到真实的jquery object
 * @param ele
 * @returns
 */
function getRealJqueryObject(ele){
	if(!ele){
		return jboltBody;
	}
	var type=typeof ele;
	var eleObj=null;
	if(type=="string"){
		if(ele.indexOf("#")!=-1){
			eleObj=$(ele);
		}else{
			eleObj=$("#"+ele);
		}
	}else if(type=="object"){
		  if(isDOM(ele)){
			  eleObj=$(ele); 
		  }else{
			  eleObj=ele;
		  }
	}
	return eleObj;
}
/**
 * 关闭当前tab 并且 刷新指定Tab
 * @param refreshTabKey
 * @returns
 */
function closeCurrentAndReloadTiggerTab(refreshTabKey){
	var withTabMode=isWithtabs();
	if(withTabMode){
		var currentTab=JBoltTabUtil.getCurrentTab();
		if(currentTab&&currentTab.length>0){
			if(refreshTabKey){
				JBoltTabUtil.showJboltTab(refreshTabKey,false,function(){
					JBoltTabUtil.close(currentTab);
					refreshPjaxContainer();
				});
			}else{
				var triggerTabKey=currentTab.data("trigger-tab-key");
				if(triggerTabKey&&JBoltTabUtil.isJboltTabExist(triggerTabKey)){
					JBoltTabUtil.showJboltTab(triggerTabKey,false,function(){
						JBoltTabUtil.close(currentTab);
						refreshPjaxContainer();
					});
				}else{
					JBoltTabUtil.close(currentTab);
				}
				
			}
			
			
		}
	}else{
		refreshPjaxContainer();
	}
	
}
/**
 * form直接提交 带CHECKER
 * @param formEle
 * @returns
 */
function pageFormSubmit(formEle){
	var form=getRealJqueryObject(formEle);
	if(isOk(form)){
		if(FormChecker.check(form)){
			form.attr("onsubmit","return true;");
			form.submit();
		}
	}
	return false;
}
/**
 * 提交form,切换刷新当前Tab
 * @param formEle
 * @returns
 */
function submitFormInCurrentTab(formEle,callback){
	var form=getRealJqueryObject(formEle);
	if(isOk(form)){
		if(FormChecker.check(form)){
			var withTabMode=isWithtabs();
			if(withTabMode){
				var tabContent=JBoltTabUtil.getCurrentTabContent();
				if(tabContent&&tabContent.length==1){
					var params=form.serialize();
					var action=form.attr("action");
					var url=action;
					if(url.indexOf("?")!=-1){
						url=url+"&"+params;
					}else{
						url=url+"?"+params;
					}
					LayerMsgBox.load(3);
					tabContent.ajaxPortal(true,url,true,function(){
						LayerMsgBox.closeLoad();
						if(callback){
							callback();
						}
					});
			}
		}else{
			ajaxSubmitForm(form,callback);
		}
	}
	}

	return false;

}
/**
 * 初始化admin后台的top部分里 关于style切换的东西
 * @returns
 */
function initJboltAdminTopStyleChange(){
	var jbolt_admin_main_top=$(".jbolt_admin_main_top");
	initToolTip(jbolt_admin_main_top);
	
}
/**
 * 判断是否是tabs多选项卡模式
 * @returns
 */
function isWithtabs(){
	var withTabs=$(".jbolt_admin.withtabs");
	return withTabs&&withTabs.length==1;
}
/**
 * 表单提交按照多选项卡模式下 ajaxPortal模式提交
 * @param form
 * @returns
 */
function tabContentFormSubmitWithAjaxPortal(form){
	var tabContent=JBoltTabUtil.getCurrentTabContent();
	if(tabContent&&tabContent.length==1){
		var params=form.serialize();
		var action=form.attr("action");
		var url=action;
		if(url.indexOf("?")!=-1){
			url=url+"&"+params;
		}else{
			url=url+"?"+params;
		}
		LayerMsgBox.load(3);
		tabContent.ajaxPortal(true,url,true,function(){
			LayerMsgBox.closeLoad();
		});
	}
}
/**
 * 触发器 触发当前tab切换URL
 * @param url
 * @returns
 */
function  currentTabContentRedirectWithAjaxPortal(url){
	var tabContent=JBoltTabUtil.getCurrentTabContent();
	if(tabContent&&tabContent.length==1){
		LayerMsgBox.load(3);
		tabContent.ajaxPortal(true,url,true,function(){
			LayerMsgBox.closeLoad();
		});
	}
}
/**
 * 在多选项卡模式下 初始化 tabcontent查询中的主要form提交事件
 * @returns
 */
function initAdminAjaxPortalFormSubmitEvent(){
	   jboltDocument.on('submit', '.jbolt_page_title form,form[data-submittabportal]', function (e) {
		   e.preventDefault();
		   e.stopPropagation();
		   var form=$(this);
		   var needcheck=form.data("needcheck");
		   if(needcheck==undefined||needcheck==null||needcheck=="undefined"||needcheck==""){
			   needcheck=true;
		   }
		   if(needcheck){
			   if(FormChecker.check(this)){
				  tabContentFormSubmitWithAjaxPortal(form);
			   }
		   }else{
			   tabContentFormSubmitWithAjaxPortal(form);
		   }
		   return false;
	    });
}
/**
 * 切换一个隐藏域
 * @param ids
 * @returns
 */
function toggleInputHidden(ids){
	if(ids.indexOf(",")!=-1){
		//多个
		var idsArr=ids.split(",");
		for(var i in idsArr){
			toggleOneInputHidden(idsArr[i]);
		}
	}else{
		toggleOneInputHidden(ids);
	}
}
function toggleOneInputHidden(id){
	var input=g(id);
	if(input.type=="hidden"){
		input.type="text";
	}else{
		input.type="hidden";
	}
}


/*
 * jQuery Autocomplete plugin 1.2.2
 */
;(function($){$.fn.extend({autocomplete:function(urlOrData,options){var isUrl=typeof urlOrData=="string";options=$.extend({},$.Autocompleter.defaults,{url:isUrl?urlOrData:null,data:isUrl?null:urlOrData,delay:isUrl?$.Autocompleter.defaults.delay:10,max:options&&!options.scroll?10:150,noRecord:"No Records."},options);options.highlight=options.highlight||function(value){return value};options.formatMatch=options.formatMatch||options.formatItem;return this.each(function(){new $.Autocompleter(this,options)})},result:function(handler){return this.bind("result",handler)},search:function(handler){return this.trigger("search",[handler])},flushCache:function(){return this.trigger("flushCache")},setOptions:function(options){return this.trigger("setOptions",[options])},unautocomplete:function(){return this.trigger("unautocomplete")}});$.Autocompleter=function(input,options){var KEY={UP:38,DOWN:40,DEL:46,TAB:9,RETURN:13,ESC:27,COMMA:188,PAGEUP:33,PAGEDOWN:34,BACKSPACE:8};var globalFailure=null;if(options.failure!=null&&typeof options.failure=="function"){globalFailure=options.failure}var $input=$(input).attr("autocomplete","off").addClass(options.inputClass);var timeout;var previousValue="";var cache=$.Autocompleter.Cache(options);var hasFocus=0;var lastKeyPressCode;var config={mouseDownOnSelect:false};var select=$.Autocompleter.Select(options,input,selectCurrent,config);var blockSubmit;navigator.userAgent.indexOf("Opera")!=-1&&$(input.form).bind("submit.autocomplete",function(){if(blockSubmit){blockSubmit=false;return false}});$input.bind((navigator.userAgent.indexOf("Opera")!=-1&&!'KeyboardEvent'in window?"keypress":"keydown")+".autocomplete",function(event){hasFocus=1;lastKeyPressCode=event.keyCode;switch(event.keyCode){case KEY.UP:if(select.visible()){event.preventDefault();select.prev()}else{onChange(0,true)}break;case KEY.DOWN:if(select.visible()){event.preventDefault();select.next()}else{onChange(0,true)}break;case KEY.PAGEUP:if(select.visible()){event.preventDefault();select.pageUp()}else{onChange(0,true)}break;case KEY.PAGEDOWN:if(select.visible()){event.preventDefault();select.pageDown()}else{onChange(0,true)}break;case options.multiple&&$.trim(options.multipleSeparator)==","&&KEY.COMMA:case KEY.TAB:case KEY.RETURN:if(selectCurrent()){event.preventDefault();blockSubmit=true;return false}break;case KEY.ESC:select.hide();break;default:clearTimeout(timeout);timeout=setTimeout(onChange,options.delay);break}}).focus(function(){hasFocus++}).blur(function(){hasFocus=0;if(!config.mouseDownOnSelect){hideResults()}}).click(function(){if(options.clickFire){if(!select.visible()){onChange(0,true)}}else{if(hasFocus++>1&&!select.visible()){onChange(0,true)}}}).bind("search",function(){var fn=(arguments.length>1)?arguments[1]:null;function findValueCallback(q,data){var result;if(data&&data.length){for(var i=0;i<data.length;i++){if(data[i].result.toLowerCase()==q.toLowerCase()){result=data[i];break}}}if(typeof fn=="function")fn(result);else $input.trigger("result",result&&[result.data,result.value])}$.each(trimWords($input.val()),function(i,value){request(value,findValueCallback,findValueCallback)})}).bind("flushCache",function(){cache.flush()}).bind("setOptions",function(){$.extend(true,options,arguments[1]);if("data"in arguments[1])cache.populate()}).bind("unautocomplete",function(){select.unbind();$input.unbind();$(input.form).unbind(".autocomplete")});function selectCurrent(){var selected=select.selected();if(!selected)return false;var v=selected.result;previousValue=v;if(options.multiple){var words=trimWords($input.val());if(words.length>1){var seperator=options.multipleSeparator.length;var cursorAt=$(input).selection().start;var wordAt,progress=0;$.each(words,function(i,word){progress+=word.length;if(cursorAt<=progress){wordAt=i;return false}progress+=seperator});words[wordAt]=v;v=words.join(options.multipleSeparator)}v+=options.multipleSeparator}$input.val(v);hideResultsNow();$input.trigger("result",[selected.data,selected.value]);return true}function onChange(crap,skipPrevCheck){if(lastKeyPressCode==KEY.DEL){select.hide();return}var currentValue=$input.val();if(!skipPrevCheck&&currentValue==previousValue)return;previousValue=currentValue;currentValue=lastWord(currentValue);if(currentValue.length>=options.minChars){$input.addClass(options.loadingClass);if(!options.matchCase)currentValue=currentValue.toLowerCase();request(currentValue,receiveData,hideResultsNow)}else{stopLoading();select.hide()}};function trimWords(value){if(!value)return[""];if(!options.multiple)return[$.trim(value)];return $.map(value.split(options.multipleSeparator),function(word){return $.trim(value).length?$.trim(word):null})}function lastWord(value){if(!options.multiple)return value;var words=trimWords(value);if(words.length==1)return words[0];var cursorAt=$(input).selection().start;if(cursorAt==value.length){words=trimWords(value)}else{words=trimWords(value.replace(value.substring(cursorAt),""))}return words[words.length-1]}function autoFill(q,sValue){if(options.autoFill&&(lastWord($input.val()).toLowerCase()==q.toLowerCase())&&lastKeyPressCode!=KEY.BACKSPACE){$input.val($input.val()+sValue.substring(lastWord(previousValue).length));$(input).selection(previousValue.length,previousValue.length+sValue.length)}};function hideResults(){clearTimeout(timeout);timeout=setTimeout(hideResultsNow,200)};function hideResultsNow(){var wasVisible=select.visible();select.hide();clearTimeout(timeout);stopLoading();if(options.mustMatch){$input.search(function(result){if(!result){if(options.multiple){var words=trimWords($input.val()).slice(0,-1);$input.val(words.join(options.multipleSeparator)+(words.length?options.multipleSeparator:""))}else{$input.val("");$input.trigger("result",null)}}})}};function receiveData(q,data){if(data&&data.length&&hasFocus){stopLoading();select.display(data,q);autoFill(q,data[0].value);select.show()}else{hideResultsNow()}};function request(term,success,failure){if(!options.matchCase)term=term.toLowerCase();var data=cache.load(term);if(data){if(data.length){success(term,data)}else{var parsed=options.parse&&options.parse(options.noRecord)||parse(options.noRecord);success(term,parsed)}}else if((typeof options.url=="string")&&(options.url.length>0)){var extraParams={timestamp:+new Date()};$.each(options.extraParams,function(key,param){extraParams[key]=typeof param=="function"?param():param});$.ajax({mode:"abort",port:"autocomplete"+input.name,dataType:options.dataType,url:options.url,data:$.extend({q:lastWord(term),limit:options.max},extraParams),success:function(data){var parsed=options.parse&&options.parse(data)||parse(data);cache.add(term,parsed);success(term,parsed)}})}else{select.emptyList();if(globalFailure!=null){globalFailure()}else{failure(term)}}};function parse(data){var parsed=[];var rows=data.split("\n");for(var i=0;i<rows.length;i++){var row=$.trim(rows[i]);if(row){row=row.split("|");parsed[parsed.length]={data:row,value:row[0],result:options.formatResult&&options.formatResult(row,row[0])||row[0]}}}return parsed};function stopLoading(){$input.removeClass(options.loadingClass)}};$.Autocompleter.defaults={inputClass:"ac_input",resultsClass:"ac_results",loadingClass:"ac_loading",minChars:1,delay:400,matchCase:false,matchSubset:true,matchContains:false,cacheLength:100,max:1000,mustMatch:false,extraParams:{},selectFirst:true,formatItem:function(row){return row[0]},formatMatch:null,autoFill:false,width:0,multiple:false,multipleSeparator:" ",inputFocus:true,clickFire:false,highlight:function(value,term){return value.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)("+term.replace(/([\^\$\(\)\[\]\{\}\*\.\+\?\|\\])/gi,"\\$1")+")(?![^<>]*>)(?![^&;]+;)","gi"),"<strong>$1</strong>")},scroll:true,scrollHeight:300,scrollJumpPosition:true};$.Autocompleter.Cache=function(options){var data={};var length=0;function matchSubset(s,sub){if(!options.matchCase)s=s.toLowerCase();var i=s.indexOf(sub);if(options.matchContains=="word"){i=s.toLowerCase().search("\\b"+sub.toLowerCase())}if(i==-1)return false;return i==0||options.matchContains};function add(q,value){if(length>options.cacheLength){flush()}if(!data[q]){length++}data[q]=value}function populate(){if(!options.data)return false;var stMatchSets={},nullData=0;if(!options.url)options.cacheLength=1;stMatchSets[""]=[];for(var i=0,ol=options.data.length;i<ol;i++){var rawValue=options.data[i];rawValue=(typeof rawValue=="string")?[rawValue]:rawValue;var value=options.formatMatch(rawValue,i+1,options.data.length);if(typeof(value)==='undefined'||value===false)continue;var firstChar=value.charAt(0).toLowerCase();if(!stMatchSets[firstChar])stMatchSets[firstChar]=[];var row={value:value,data:rawValue,result:options.formatResult&&options.formatResult(rawValue)||value};stMatchSets[firstChar].push(row);if(nullData++<options.max){stMatchSets[""].push(row)}};$.each(stMatchSets,function(i,value){options.cacheLength++;add(i,value)})}setTimeout(populate,25);function flush(){data={};length=0}return{flush:flush,add:add,populate:populate,load:function(q){if(!options.cacheLength||!length)return null;if(!options.url&&options.matchContains){var csub=[];for(var k in data){if(k.length>0){var c=data[k];$.each(c,function(i,x){if(matchSubset(x.value,q)){csub.push(x)}})}}return csub}else if(data[q]){return data[q]}else if(options.matchSubset){for(var i=q.length-1;i>=options.minChars;i--){var c=data[q.substr(0,i)];if(c){var csub=[];$.each(c,function(i,x){if(matchSubset(x.value,q)){csub[csub.length]=x}});return csub}}}return null}}};$.Autocompleter.Select=function(options,input,select,config){var CLASSES={ACTIVE:"ac_over"};var listItems,active=-1,data,term="",needsInit=true,element,list;function init(){if(!needsInit)return;element=$("<div/>").hide().addClass(options.resultsClass).css("position","absolute").appendTo(document.body).hover(function(event){if($(this).is(":visible")){input.focus()}config.mouseDownOnSelect=false});list=$("<ul/>").appendTo(element).mouseover(function(event){if(target(event).nodeName&&target(event).nodeName.toUpperCase()=='LI'){active=$("li",list).removeClass(CLASSES.ACTIVE).index(target(event));$(target(event)).addClass(CLASSES.ACTIVE)}}).click(function(event){$(target(event)).addClass(CLASSES.ACTIVE);select();if(options.inputFocus)input.focus();return false}).mousedown(function(){config.mouseDownOnSelect=true}).mouseup(function(){config.mouseDownOnSelect=false});if(options.width>0)element.css("width",options.width);needsInit=false}function target(event){var element=event.target;while(element&&element.tagName!="LI")element=element.parentNode;if(!element)return[];return element}function moveSelect(step){listItems.slice(active,active+1).removeClass(CLASSES.ACTIVE);movePosition(step);var activeItem=listItems.slice(active,active+1).addClass(CLASSES.ACTIVE);if(options.scroll){var offset=0;listItems.slice(0,active).each(function(){offset+=this.offsetHeight});if((offset+activeItem[0].offsetHeight-list.scrollTop())>list[0].clientHeight){list.scrollTop(offset+activeItem[0].offsetHeight-list.innerHeight())}else if(offset<list.scrollTop()){list.scrollTop(offset)}}};function movePosition(step){if(options.scrollJumpPosition||(!options.scrollJumpPosition&&!((step<0&&active==0)||(step>0&&active==listItems.size()-1)))){active+=step;if(active<0){active=listItems.size()-1}else if(active>=listItems.size()){active=0}}}function limitNumberOfItems(available){return options.max&&options.max<available?options.max:available}function fillList(){list.empty();var max=limitNumberOfItems(data.length);for(var i=0;i<max;i++){if(!data[i])continue;var formatted=options.formatItem(data[i].data,i+1,max,data[i].value,term);if(formatted===false)continue;var li=$("<li/>").html(options.highlight(formatted,term)).addClass(i%2==0?"ac_even":"ac_odd").appendTo(list)[0];$.data(li,"ac_data",data[i])}listItems=list.find("li");if(options.selectFirst){listItems.slice(0,1).addClass(CLASSES.ACTIVE);active=0}if($.fn.bgiframe)list.bgiframe()}return{display:function(d,q){init();data=d;term=q;fillList()},next:function(){moveSelect(1)},prev:function(){moveSelect(-1)},pageUp:function(){if(active!=0&&active-8<0){moveSelect(-active)}else{moveSelect(-8)}},pageDown:function(){if(active!=listItems.size()-1&&active+8>listItems.size()){moveSelect(listItems.size()-1-active)}else{moveSelect(8)}},hide:function(){element&&element.hide();listItems&&listItems.removeClass(CLASSES.ACTIVE);active=-1},visible:function(){return element&&element.is(":visible")},current:function(){return this.visible()&&(listItems.filter("."+CLASSES.ACTIVE)[0]||options.selectFirst&&listItems[0])},show:function(){var offset=$(input).offset();element.css({width:typeof options.width=="string"||options.width>0?options.width:$(input).width(),top:offset.top+input.offsetHeight,left:offset.left}).show();if(options.scroll){list.scrollTop(0);list.css({maxHeight:options.scrollHeight,overflow:'auto'});if(navigator.userAgent.indexOf("MSIE")!=-1&&typeof document.body.style.maxHeight==="undefined"){var listHeight=0;listItems.each(function(){listHeight+=this.offsetHeight});var scrollbarsVisible=listHeight>options.scrollHeight;list.css('height',scrollbarsVisible?options.scrollHeight:listHeight);if(!scrollbarsVisible){listItems.width(list.width()-parseInt(listItems.css("padding-left"))-parseInt(listItems.css("padding-right")))}}}},selected:function(){var selected=listItems&&listItems.filter("."+CLASSES.ACTIVE).removeClass(CLASSES.ACTIVE);return selected&&selected.length&&$.data(selected[0],"ac_data")},emptyList:function(){list&&list.empty()},unbind:function(){element&&element.remove()}}};$.fn.selection=function(start,end){if(start!==undefined){return this.each(function(){if(this.createTextRange){var selRange=this.createTextRange();if(end===undefined||start==end){selRange.move("character",start);selRange.select()}else{selRange.collapse(true);selRange.moveStart("character",start);selRange.moveEnd("character",end);selRange.select()}}else if(this.setSelectionRange){this.setSelectionRange(start,end)}else if(this.selectionStart){this.selectionStart=start;this.selectionEnd=end}})}var field=this[0];if(field.createTextRange){var range=document.selection.createRange(),orig=field.value,teststring="<->",textLength=range.text.length;range.text=teststring;var caretAt=field.value.indexOf(teststring);field.value=orig;this.selection(caretAt,caretAt+textLength);return{start:caretAt,end:caretAt+textLength}}else if(field.selectionStart!==undefined){return{start:field.selectionStart,end:field.selectionEnd}}}})(jQuery);

/**
 * jQuery pagination plugin 2.2
 */
 (function($){
	/**
	 * @class Class for calculating pagination values
	 */
	$.PaginationCalculator = function(maxentries, opts) {
		this.maxentries = maxentries;
		this.opts = opts;
	}
	
	$.extend($.PaginationCalculator.prototype, {
		/**
		 * Calculate the maximum number of pages
		 * @method
		 * @returns {Number}
		 */
		numPages:function() {
			return Math.ceil(this.maxentries/this.opts.items_per_page);
		},
		/**
		 * Calculate start and end point of pagination links depending on 
		 * current_page and num_display_entries.
		 * @returns {Array}
		 */
		getInterval:function(current_page)  {
			var ne_half = Math.floor(this.opts.num_display_entries/2);
			var np = this.numPages();
			var upper_limit = np - this.opts.num_display_entries;
			var start = current_page > ne_half ? Math.max( Math.min(current_page - ne_half, upper_limit), 0 ) : 0;
			var end = current_page > ne_half?Math.min(current_page+ne_half + (this.opts.num_display_entries % 2), np):Math.min(this.opts.num_display_entries, np);
			return {start:start, end:end};
		}
	});
	
	// Initialize jQuery object container for pagination renderers
	$.PaginationRenderers = {}
	
	/**
	 * @class Default renderer for rendering pagination links
	 */
	$.PaginationRenderers.defaultRenderer = function(maxentries, opts) {
		this.maxentries = maxentries;
		this.opts = opts;
		this.pc = new $.PaginationCalculator(maxentries, opts);
	}
	$.extend($.PaginationRenderers.defaultRenderer.prototype, {
		/**
		 * Helper function for generating a single link (or a span tag if it's the current page)
		 * @param {Number} page_id The page id for the new item
		 * @param {Number} current_page 
		 * @param {Object} appendopts Options for the new item: text and classes
		 * @returns {jQuery} jQuery object containing the link
		 */
		createLink:function(page_id, current_page, appendopts){
			var lnk, np = this.pc.numPages();
			page_id = page_id<0?0:(page_id<np?page_id:np-1); // Normalize page id to sane value
			appendopts = $.extend({text:page_id+1, classes:""}, appendopts||{});
			if(page_id == current_page){
				lnk = $("<a class='current'>" + appendopts.text + "</a>");
			}
			else
			{
				lnk = $("<a>" + appendopts.text + "</a>")
					.attr('href', this.opts.link_to.replace(/__id__/,page_id+1));
			}
			if(appendopts.classes){ lnk.addClass(appendopts.classes); }
			lnk.data('page_id', page_id);
			return lnk;
		},
		// Generate a range of numeric links 
		appendRange:function(container, current_page, start, end, opts) {
			var i;
			for(i=start; i<end; i++) {
				this.createLink(i, current_page, opts).appendTo(container);
			}
		},
		getLinks:function(current_page, eventHandler) {
			var begin, end,
				interval = this.pc.getInterval(current_page),
				np = this.pc.numPages(),
				fragment = $("<div class='pagination'></div>");
			
			// Generate "Previous"-Link
			if(this.opts.prev_text && (current_page > 0 || this.opts.prev_show_always)){
				fragment.append(this.createLink(current_page-1, current_page, {text:this.opts.prev_text, classes:"prev"}));
			}
			// Generate starting points
			if (interval.start > 0 && this.opts.num_edge_entries > 0)
			{
				end = Math.min(this.opts.num_edge_entries, interval.start);
				this.appendRange(fragment, current_page, 0, end, {classes:'sp'});
				if(this.opts.num_edge_entries < interval.start && this.opts.ellipse_text)
				{
					$("<span class='pagination-break'>"+this.opts.ellipse_text+"</span>").appendTo(fragment);
				}
			}
			// Generate interval links
			this.appendRange(fragment, current_page, interval.start, interval.end);
			// Generate ending points
			if (interval.end < np && this.opts.num_edge_entries > 0)
			{
				if(np-this.opts.num_edge_entries > interval.end && this.opts.ellipse_text)
				{
					$("<span class='pagination-break'>"+this.opts.ellipse_text+"</span>").appendTo(fragment);
				}
				begin = Math.max(np-this.opts.num_edge_entries, interval.end);
				this.appendRange(fragment, current_page, begin, np, {classes:'ep'});
				
			}
			// Generate "Next"-Link
			if(this.opts.next_text && (current_page < np-1 || this.opts.next_show_always)){
				fragment.append(this.createLink(current_page+1, current_page, {text:this.opts.next_text, classes:"next"}));
			}
			$('a', fragment).click(eventHandler);
			return fragment;
		}
	});
	
	// Extend jQuery
	$.fn.pagination = function(maxentries, opts){
		
		// Initialize options with default values
		opts = $.extend({
			items_per_page:1,
			num_display_entries:4,
			current_page:0,
			num_edge_entries:1,
			link_to:"javascript:void(0)",
			prev_text:"<i></i>上一页",
			next_text:"下一页 <i></i>",
			ellipse_text:"...",
			prev_show_always:true,
			next_show_always:true,
			renderer:"defaultRenderer",
			show_if_single_page:false,
			load_first_page:false,
			callback:function(){return false;}
		},opts||{});
		
		var containers = this,
			renderer, links, current_page;
		
		/**
		 * This is the event handling function for the pagination links. 
		 * @param {int} page_id The new page number
		 */
		function paginationClickHandler(evt){
			var links, 
				new_current_page = $(evt.target).data('page_id'),
				continuePropagation = selectPage(new_current_page);
			if (!continuePropagation) {
				evt.stopPropagation();
			}
			return continuePropagation;
		}
		
		/**
		 * This is a utility function for the internal event handlers. 
		 * It sets the new current page on the pagination container objects, 
		 * generates a new HTMl fragment for the pagination links and calls
		 * the callback function.
		 */
		function selectPage(new_current_page) {
			// update the link display of a all containers
			containers.data('current_page', new_current_page);
			links = renderer.getLinks(new_current_page, paginationClickHandler);
			containers.empty();
			links.appendTo(containers);
			// call the callback and propagate the event if it does not return false
			var continuePropagation = opts.callback(new_current_page, containers);
			return continuePropagation;
		}
		
		// -----------------------------------
		// Initialize containers
		// -----------------------------------
                current_page = parseInt(opts.current_page);
		containers.data('current_page', current_page);
		// Create a sane value for maxentries and items_per_page
		maxentries = (!maxentries || maxentries < 0)?1:maxentries;
		opts.items_per_page = (!opts.items_per_page || opts.items_per_page < 0)?1:opts.items_per_page;
		
		if(!$.PaginationRenderers[opts.renderer])
		{
			throw new ReferenceError("Pagination renderer '" + opts.renderer + "' was not found in jQuery.PaginationRenderers object.");
		}
		renderer = new $.PaginationRenderers[opts.renderer](maxentries, opts);
		
		// Attach control events to the DOM elements
		var pc = new $.PaginationCalculator(maxentries, opts);
		var np = pc.numPages();
		containers.bind('setPage', {numPages:np}, function(evt, page_id) { 
				if(page_id >= 0 && page_id < evt.data.numPages) {
					selectPage(page_id); return false;
				}
		});
		containers.bind('prevPage', function(evt){
				var current_page = $(this).data('current_page');
				if (current_page > 0) {
					selectPage(current_page - 1);
				}
				return false;
		});
		containers.bind('nextPage', {numPages:np}, function(evt){
				var current_page = $(this).data('current_page');
				if(current_page < evt.data.numPages - 1) {
					selectPage(current_page + 1);
				}
				return false;
		});
		
		// When all initialisation is done, draw the links
		links = renderer.getLinks(current_page, paginationClickHandler);
		containers.empty();
		if(np > 1 || opts.show_if_single_page) {
			links.appendTo(containers);
		}
		// call callback function
		if(opts.load_first_page) {
			opts.callback(current_page, containers);
		}
	} // End of $.fn.pagination block
	
})(jQuery);
 
 
/**
 * 主从表模块
 */
var MasterSlaveUtil={
	   init:function(parentEle){
		   var parent=getRealJqueryObject(parentEle);
		   if(!isOk(parent)){return false;}
		   var that=this;
		   var boxs=parent.find(".jbolt_master_slave_box");
		   if(!isOk(boxs)){return false;}
		   var len=boxs.length;
		   for(var i=0;i<len;i++){
			   that.initHeight(boxs.eq(i));
		   }
		},
		initMasterTableEvent:function(){
			var that=this;
			jboltBody.on("click",".jbolt_page .jbolt_master_slave_box .jbolt_master_container table.table>tbody>tr",function(){
				var tr=$(this);
				tr.parent().find("tr.active").removeClass("active");
				tr.addClass("active");
				var id=tr.data("id");
				if(!id){
					LayerMsgBox.alert("数据TR绑定data-id为空",2);
				}else{
					var dialog=LayerMsgBox.load(3);
					var portals=tr.closest(".jbolt_master_slave_box").find(".jbolt_slave_container [data-ajaxportal]");
					var size=portals.length;
					if(portals&&size){
						portals.each(function(i){
							var portal=$(this);
							var orign_url=portal.data("orign-url");
							if(!orign_url){
								LayerMsgBox.alert("AjaxPortal组件需要设置data-orign-url属性",2);
							}else{
								var url=orign_url+"/"+id;
								portal.ajaxPortal(true,url,true,function(){
									if(i==size-1){
										LayerMsgBox.close(dialog);
									}
									that.initPortalHeight(portal);
								});
							}
						});
					}
					
				}
			});
		},initPortalHeight:function(portal){
			var jbolt_slave_container=portal.closest(".jbolt_slave_container");
			if(jbolt_slave_container&&jbolt_slave_container.length>0){
				var sheight=jbolt_slave_container.outerHeight()-45;
				jbolt_slave_container.find(".jbolt_slave_body").each(function(){
					var bo=$(this);
					var title=bo.parent().find(".jbolt_slave_top");
					if(title&&title.length>0){
						var titleHeight=title.outerHeight();
						bo.height(sheight-titleHeight);
					}else{
						bo.height(sheight);
					}
				});
				
				
			}
		},
		initHeight:function(box){
			var height=mainPjaxContainer.height()-60;
			var jbolt_page_title=mainPjaxContainer.find(".jbolt_page_title");
			if(jbolt_page_title&&jbolt_page_title.length>0){
				height=height-Math.ceil(jbolt_page_title.height());
				}
			var jbolt_page_content=mainPjaxContainer.find(".jbolt_page_content");
			if(jbolt_page_content&&jbolt_page_content.length>0){
				box.height(Math.floor(height));
				}
		}
	}
/**
 * 处理窗口resize时候jbolt_layer调整
 * @returns
 */
function resizeJBoltLayer(){
	var layerPortal=jboltBody.find(".jbolt_layer_portal");
	if(layerPortal&&layerPortal.length==1){
		var jblayer=layerPortal.closest("#jbolt_layer");
		if(jblayer&&jblayer.length==1){
			var triggerid=jblayer.data("triggerid");
			if(triggerid){
				var triggerEle=$("#"+triggerid);
				if(triggerEle&&triggerEle.length==1){
					var height=triggerEle.data("height");
					if(height){
						var newTop=jboltWindowHeight-height;
						if(newTop<0){
							newTop=0;
						}
						var top=newTop+"px";
						layerPortal.css({"top":top});
						var closeBtn=jblayer.find(".jbolt_layer_close");
						if(closeBtn&&closeBtn.length==1){
							closeBtn.css({"top":top});
						}
					}
				}
			}
		}
	
	}
}
function onwindowReisze(){
	jboltWindow.on("resize",function(){
		MasterSlaveUtil.init(mainPjaxContainer);
		jboltWindowHeight=jboltWindow.height();
		resizeJBoltLayer();
	});
}
/**
 * 切换元素的可见属性
 * @param cssSelector
 * @param full
 * @returns
 */
function toggleVisiable(cssSelector,full){
	if(full){
		$(cssSelector).toggle();
	}else{
		var withTabs=isWithtabs();
		if(withTabs){
			var currentTabContent=JBoltTabUtil.getCurrentTabContent();
			if(currentTabContent&&currentTabContent.length==1){
				currentTabContent.find(cssSelector).toggle();
			}
		}else{
			$(mainPjaxContainer).find(cssSelector).toggle();
		}
	}
	
}
var lockHtmlTpl='<div oncontextmenu="doNothing()" class="j_locksystem noselect" id="j_locksystem">'+
'<div class="j_lockmain"><h1>屏幕已锁</h1><div class="j_lockpassword">'+
'<input onkeyup="unlockSystem(event)" maxlength="40" type="password" id="unlockpwd" placeholder="请在此处，输入登录密码" name="password"  autocomplete="off" /></div></div></div>';
var lockSystemTimer;
function checkLockSystem(){
	console.log("checkLockSystem")
	var j_locksystem=$("#j_locksystem");
	if(j_locksystem&&j_locksystem.length==1){
		//如果已经有了 判断hidden
		j_locksystem.removeClass("__web-inspector-hide-shortcut__");
		j_locksystem.show();
	}else{
		showJBoltLockSystemEle();
	}
	
}
/**
 * 显示锁屏界面
 * @returns
 */
function showJboltLockSystem(){
showJBoltLockSystemEle();	
lockSystemTimer=setInterval(checkLockSystem, 600);
}
function showJBoltLockSystemEle(){
	console.log("调用显示")
	if(self!=top){
		parent.showJboltLockSystem();
	}else{
		var j_locksystem=$("#j_locksystem");
		if(!(j_locksystem&&j_locksystem.length==1)){
			jboltBody.append(lockHtmlTpl);
			j_locksystem=$("#j_locksystem");
		}
		$("#unlockpwd").val("");
		j_locksystem.removeClass("__web-inspector-hide-shortcut__");
		j_locksystem.fadeIn(300);
	}
}
/**
 * 解锁
 * @returns
 */
function unlockSystem(event){
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if(keyCode==13){
		var pwd=$("#unlockpwd").val();
		if(!($.trim(pwd))){
			LayerMsgBox.alert('请输入登录密码',2);
		}else{
			LayerMsgBox.loading("解锁中...",10000);
			Ajax.post("/admin/unLockSystem",{"password":pwd},function(res){
				LayerMsgBox.success("解锁成功",500,closeJboltLockSystem);
			});
		}
	}
}
/**
 * 关闭锁屏界面
 * @returns
 */
function closeJboltLockSystem(){
	if(self!=top){
		parent.closeJboltLockSystem();
	}else{
		console.log("调用关闭")
		clearInterval(lockSystemTimer);
		var jbolt_admin=$(".jbolt_admin");
		if(jbolt_admin&&jbolt_admin.length==1){
			//说明是在系统内部
			$("#unlockpwd").val("");
			$("#j_locksystem").fadeOut(300);
		}else{
			reloadCurrentPage();
		}
		
	}
}
/**
 * 屏蔽右键
 * @returns
 */
function doNothing(){
	window.event.returnValue=false;
	return false;
}
/**
 * 初始化tooltip
 * @param parentEle
 * @returns
 */
function initToolTip(parentEle){
	var tps;
	if(parentEle){
		var parent=getRealJqueryObject(parentEle);
		if(isOk(parent)){
			tps=parent.find("[tooltip],[data-toggle='tooltip']");
		}
	}else{
		tps=jboltBody.find("[tooltip],[data-toggle='tooltip']");
	}
	
	if(tps&&tps.length>0){
		tps.tooltip();
	}
}
/**
 * 动态设置全局后台UI样式
 * @param styleName
 * @returns
 */
function changeUserJboltStyle(styleName){
	var jbolt_user_config_style_box=$("#jbolt_user_config_style_box");
	jbolt_user_config_style_box.find(".jbolt_config_style.active").removeClass("active");
	jbolt_user_config_style_box.find("#jbs_"+styleName).addClass("active");
	var jbolt_admin=$(".jbolt_admin");
	var className=jbolt_admin[0].className;
	var classArr=className.split(" ");
	var newClassName="";
	var oneClass="";
	for(var i in classArr){
		oneClass=classArr[i]; 
		if(oneClass!="default" && oneClass.indexOf("jbolt_style_")==-1){
			newClassName=newClassName+oneClass+" ";
		}
	}
	jbolt_admin[0].className=newClassName+styleName;
}

/**
 * 初始化登录页面样式
 * @returns
 */
function initLoginStyle(){
	var glassStyle=$.cookie('jbolt_login_glassStyle');
	var bgimgBlur=$.cookie('jbolt_login_bgimgBlur');
	
	if(glassStyle&&glassStyle=="true"){
		$(".jbolt_loginform").addClass("jbolt_bg_glass");
	}else{
		$(".jbolt_loginform").removeClass("jbolt_bg_glass");
	}
	if(bgimgBlur&&bgimgBlur=="true"){
		$(".bodybgmask").addClass("blur");
	}else{
		$(".bodybgmask").removeClass("blur");
	}
	
	setTimeout(function(){
		$(".jbolt_formbox").addClass("show");
		$(".jbolt_title").addClass("show");
		},300);

}
/**
 * Select 2 组件
 */
var Select2Util={
		init:function(parentEle){
			this.initNotAutoLoadSelect(parentEle);
		},initNotAutoLoadSelect:function(parentEle){
			if(hasImportSelect2){
				 var parent=getRealJqueryObject(parentEle);
				 if(!isOk(parent)){return false;}
				 var selects=parent.find("select[data-select2]:not([data-autoload]):not([data-linkage])");
				 if(!isOk(selects)){return false;}
				 selects.select2();
			}
		},initAutoLoadSelect:function(select){
			if(hasImportSelect2){
				var type=select.data("select-type");
				if(type&&type=="select2"){
					var placeholder=select.data("placeholder");
					if(!placeholder){
						placeholder=select.attr("placeholder");
						if(!placeholder){
							placeholder=select.data("text");
							if(!placeholder){
								placeholder="请选择";
							}
						}
					}
					
					select.select2({
						theme:"bootstrap",
						allowClear:true,
						placeholder:placeholder
					});
				}
			}
		}
}
/**
 * 检测引入了select2的js
 * @returns
 */
function checkImportSelect2(){
	var hasjs=$("script[src*='select2']");
	return (hasjs&&hasjs.length==1);
}
/**
 * 全局处理select2 宽度等问题
 * @returns
 */
function processGlobalSelect2(){
	hasImportSelect2=checkImportSelect2();
	if(hasImportSelect2){
		$.fn.modal.Constructor.prototype.enforceFocus = function () {};
		$.fn.select2.defaults.set('width', '100%');
		$.fn.select2.defaults.set("theme", "bootstrap");
	}
}
/**
 * 初始化ajaxPortal
 * @returns
 */
function initAjaxPortal(parentEle){
	var portals;
	if(parentEle){
		var parent=getRealJqueryObject(parentEle);
		if(isOk(parent)){
			portals=parent.find("[data-ajaxportal]");
		}
	}else{
		portals=jboltBody.find("[data-ajaxportal]");
	}
	
	if(portals&&portals.length>0){
		portals.ajaxPortal(true);
	}
	
}
/**
 * 初始化imageviewer组件的事件绑定 点击关闭
 * @returns
 */
function processImgviewerEvent(){
	jboltBody.on("click",'.viewer-canvas',function(e){
		if($(e.target).hasClass("viewer-canvas")){
			$(".viewer-close").trigger("click");
		}
	});
}

//定义
$.getMultiScripts = function(arr, path) {
    var _arr = $.map(arr, function(scr) {
        return $.ajax({async:false,dataType:"script",url:((path||"") + scr )});
    });

    _arr.push($.Deferred(function( deferred ){
        $( deferred.resolve );
    }));

    return $.when.apply($, _arr);
}

/**
 * 初始化首次进入JBolt-Admin后台 全局类的初始化
 * @returns
 */
function initJboltAdmin(){
	//初始化左侧导航菜单
	initAdminLeftNav();
	//全局处理select2组件
	processGlobalSelect2();
	
	//TableUtil init 初始化table列表里的edit del按钮
	TableUtil.init();
	PageOptUtil.init();
	//切换左侧menu显示和隐藏
	toggleMenuEvent();
	//JBoltLayer组件
	JBoltLayerUtil.init();
	
	//初始化主从表结构事件
	MasterSlaveUtil.initMasterTableEvent();
	//处理imgviewer组件事件
	processImgviewerEvent();
	//后台顶部的样式
	initJboltAdminTopStyleChange();
	//看图器
	LayerPhotoUtil.init();
	
}
/**
 * 判断是否引入pjax
 * @returns
 */
function isHasPjax(){
	var pjaxScript=$("#pjaxScript");
	return pjaxScript&&pjaxScript.length==1;
}

var MultipleFileInputUtil={
		init:function(parentEle){
			 var parent=getRealJqueryObject(parentEle);
			 if(!isOk(parent)){return false;}
			 var inputs=parent.find("[data-multiplefileinput]");
			 if(!isOk(inputs)){return false;}
			 this.initInputs(inputs);
		},
		initInputs:function(inputs){
			 if(!isOk(inputs)){return false;}
			 var len=inputs.length;
			 for(var i=0;i<len;i++){
				 this.initInput(inputs.eq(i));
			 }
		},
		processHandler:function(handler,res){
			var exe_handler=eval(handler);
			if(exe_handler&&typeof(exe_handler)=="function"){
				exe_handler(res);
			}
		},
		initEvent:function(input){
			var that=this;
			var handler=input.data("handler");
			if(handler){
				var sync=input.data("sync");
				if(sync){
					input.on('filebatchuploadsuccess', function(event, data, previewId, index) {
						that.processHandler(handler,data.response);
					});
				}else{
					input.on('fileuploaded', function(event, data, previewId, index) {
						that.processHandler(handler,data.response);
					});
				}
				
			}
			var extraHandler=input.data("extrahandler");
			if(extraHandler){
				var exeextra_handler=eval(extraHandler);
				if(exeextra_handler&&typeof(exeextra_handler)=="function"){
					exeextra_handler(input);
				}
			}
			
		},initInput:function(input){
			var chooseBtnText=input.data("choosebtntext");
			if(!chooseBtnText){
				chooseBtnText="请选择";
			}
			var maxFileCount=input.data("max-filecount");
			if(!maxFileCount){
				maxFileCount=9;
			}
			var maxFileSize=input.data("max-filesize");
			if(!maxFileSize){
				maxFileSize=200;
			}
			var theme=input.data("theme");
			if(!theme){
				theme="fa";
			}
			var sync=input.data("sync");
			var uploadAsync=true;
			if(sync){
				uploadAsync=false;
			}
			var uploadUrl=input.data("uploadurl");
			if(!uploadUrl){
				LayerMsgBox.alert("请设置多文件组件上传地址：data-uploadurl",2);
			}
			var options={
					enctype: 'multipart/form-data',
			        previewFileType: "image",
			        theme:theme,
			        uploadAsync:uploadAsync,
			        allowedPreviewTypes:["image"],
			        uploadUrl:uploadUrl,
			        maxFileCount:maxFileCount,
			        maxFileSize:maxFileSize,
			        showSort:true,
			        showCaption:false,
			        showCancel:false,
			        language:"zh",
			        browseClass: "btn btn-success",
			        browseLabel: chooseBtnText,
			        removeClass: "btn btn-danger",
			        removeLabel: "清空",
			        uploadClass: "btn btn-info",
			        uploadLabel: "全部上传",
			        fileActionSettings: {                               // 在预览窗口中为新选择的文件缩略图设置文件操作的对象配置
			            showRemove: true,                                   // 显示删除按钮
			            showUpload: false,                                   // 显示上传按钮
			            showDownload: false,                            // 显示下载按钮
			            showZoom: true,                                    // 显示预览按钮
			            showDrag: true,                                        // 显示拖拽
			        }
			    };
			var inputId=input.attr("id");
			var fielInputBox=input.closest(".file-input");
			if(fielInputBox&&fielInputBox.length==1){
				var newInput=input.clone();
				fielInputBox.before(newInput);
				fielInputBox.remove();
				newInput.fileinput(options);
				this.initEvent(newInput);
			}else{
				input.fileinput(options);
				this.initEvent(input);
			}
		}
}

/**
 * 初始化
 * @returns
 */
$(function(){
	var hasPjax=isHasPjax();
	var withtabs=isWithtabs();
	//全局初始化
	initJboltAdmin();
	//如果是需要pjax并且引入了pjax是单页加载模式
	if(needPjax&&hasPjax){
		if(withtabs){
			//pjax+多选项卡模式下
			JBoltTabUtil.init();
			JBoltTabUtil.addJboltTabWithoutContentUrl();
			//找到当前的一个选项卡内容区域 执行加载完成后的js
			var currentTabContent=JBoltTabUtil.getCurrentTabContent();
			if(currentTabContent&&currentTabContent.length==1){
				afterAjaxPortal(currentTabContent);
			}
			//多选项卡模式下的form提交会提交选项卡内容区域
			initAdminAjaxPortalFormSubmitEvent();
		}else{
			//初始化全局pjax
			initAdminPjax();
		}
	}else{
		//非pjax模式下 一般是在直接打开内页地址或者dialog中
		if(withtabs){
			JBoltTabUtil.init();
			initAdminAjaxPortalFormSubmitEvent();
		}
		//上来直接进入界面 需要各种组件的初始化
		SelectUtil.initAutoSetValue();
		JAtomFileUploadUtil.init();
		JAtomImgUploader.init();
		SwitchBtnUtil.init();
		FormDate.init();
		LayerTipsUtil.init();
		HtmlEditorUtil.init();
		ImageViewerUtil.init();
		AutocompleteUtil.init();
		RadioUtil.init();
		CheckboxUtil.init();
		Select2Util.init();
		SelectUtil.init();
		initToolTip();
		MultipleFileInputUtil.init();
		initAjaxPortal();
	}
	

		
		onwindowReisze();
		
	});



