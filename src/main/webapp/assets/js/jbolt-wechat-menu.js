/**
 * JBolt 微信公众平台 自定义菜单脚本
 */
var menu = $("#menu");
var menuPortal = $("#menuPortal");
var level1Box = $("#level1Box");
/**
 * 右侧表单选择菜单类型的change事件处理
 * @param r
 * @param val
 * @returns
 */
function changeTitle(r, val) {
	switch (val) {
	case "none":
		$("#menuTitle").text("菜单值");
		$("#menuBox").hide();
		$("#menuValue").val("");
		$("#menuValue").data("rule", "");
		$("#appIdBox").hide();
		$("#pagepathBox").hide();
		$("#appId").hide().val("");
		$("#appId").data("rule", "");
		$("#pagepath").hide().val("");
		$("#pagepath").data("rule", "");
		break;
	case "view":
		$("#menuTitle").text("URL");
		$("#menuBox").show();
		$("#menuValue").data("rule", "required");
		$("#appIdBox").hide();
		$("#pagepathBox").hide();
		$("#appId").hide().val("");
		$("#appId").data("rule", "");
		$("#pagepath").hide().val("");
		$("#pagepath").data("rule", "");
		break;
	case "keywords":
		$("#menuTitle").text("关键词");
		$("#menuBox").show();
		$("#menuValue").data("rule", "required");
		$("#appIdBox").hide();
		$("#pagepathBox").hide();
		$("#appId").hide().val("");
		$("#appId").data("rule", "");
		$("#pagepath").hide().val("");
		$("#pagepath").data("rule", "");
		break;
	case "click":
		$("#menuTitle").text("事件KEY");
		$("#menuBox").show();
		$("#menuValue").data("rule", "required");
		$("#appIdBox").hide();
		$("#pagepathBox").hide();
		$("#appId").hide().val("");
		$("#appId").data("rule", "");
		$("#pagepath").hide().val("");
		$("#pagepath").data("rule", "");
		break;
	case "miniprogram":
		$("#menuTitle").text("默认URL");
		$("#menuBox").show();
		$("#menuValue").data("rule", "required");
		$("#appIdBox").show();
		$("#pagepathBox").show();
		$("#appId").show().data("rule", "required");
		$("#pagepath").show().data("rule", "required");
		break;
	default:
		$("#menuTitle").text("事件KEY");
		$("#menuBox").show();
		$("#menuValue").data("rule", "required");
		$("#appIdBox").hide();
		$("#pagepathBox").hide();
		$("#appId").hide().val("");
		$("#appId").data("rule", "");
		$("#pagepath").hide().val("");
		$("#pagepath").data("rule", "");
		break;
	}
}
/**
 * 插入一级菜单里的空位
 * @param number
 * @returns
 */
function insertLevel1Blank(number) {
	for (var i = 1; i <= number; i++) {
		level1Box
				.append('<li data-id="0" data-pid="0"><span><i class="fa fa-plus"></i></span></li>');
	}
}
/**
 * 一级菜单循环渲染
 * @param menus
 * @returns
 */
function insertLevel1Menus(menus) {
	for ( var i in menus) {
		level1Box.append('<li data-id="' + menus[i].id + '" data-pid="'
				+ menus[i].pid + '"><ul class="level2"></ul><span>'
				+ menus[i].name
				+ '</span><i class="fa fa-remove text-danger"></i></li>');
	}
}
/**
 * 插入添加按钮
 * @param ul
 * @param pid
 * @returns
 */
function insertPlusBtn(ul, pid) {
	ul.append('<li data-id="0" data-pid="' + pid
			+ '"><i class="fa fa-plus"></i></li>');
}
/**
 * 二级菜单循环渲染
 * @param ul
 * @param menus
 * @returns
 */
function insertLevel2Menus(ul, menus) {
	for ( var i in menus) {
		ul.append('<li data-id="' + menus[i].id + '" data-pid="' + menus[i].pid
				+ '"><span>' + menus[i].name
				+ '</span><i class="fa fa-remove text-danger"></i></li>');
	}
}
/**
 * 根据PID读取二级菜单
 * @param ul
 * @param pid
 * @param callback
 * @returns
 */
function readLevel2MenusByPid(ul, pid, callback) {
	ul.empty();
	Ajax.get("/admin/wechat/menu/level2List/"+mpId+"-" + pid, function(res) {
		var menus = res.data;
		if (menus && menus.length > 0) {
			insertLevel2Menus(ul, menus);
			var size = 5 - menus.length;
			if (size > 0) {
				insertPlusBtn(ul, pid);
			}
		} else {
			insertPlusBtn(ul, pid);
		}
		if (callback) {
			callback();
		}
	});
}
/**
 * 读取二级菜单
 * @returns
 */
function readLevel2Menus() {
	$(".level1>li").each(function() {
		var l1 = $(this);
		var id = l1.data("id");
		var ul = l1.find("ul");
		readLevel2MenusByPid(ul, id,function(){
		});
	});

}
/**
 * 读取一级菜单
 * @param callback
 * @returns
 */
function readLevel1Menus(callback) {
	level1Box.empty();
	Ajax.get("/admin/wechat/menu/level1List/"+mpId, function(res) {
		var menus = res.data;
		if (menus && menus.length > 0) {
			insertLevel1Menus(menus);
			// 处理空白
			var size = 3 - menus.length;
			if (size > 0) {
				insertLevel1Blank(size);
			}
			if (callback) {
				callback();
			}
		} else {
			insertLevel1Blank(3);
		}
	});
}


/**
 * 创建新菜单
 * @param level
 * @param pid
 * @param btn
 * @param callback
 * @returns
 */
function createNewMenu(level, pid, btn, callback) {
	LayerMsgBox.loading("执行中...", 3000);
	Ajax.post("/admin/wechat/menu/save",
					{
						"mpId":mpId,
						"menu.pid" : pid
					},
					function(res) {
						var menu = res.data;
						if (level == 2) {
							btn.before('<li class="active" data-id="'
											+ menu.id
											+ '" data-pid="'
											+ menu.pid
											+ '"><span>'
											+ menu.name
											+ '</span><i class="fa fa-remove text-danger"></i></li>');
							if (menu.sortRank == 5) {
								btn.remove();
							}
						} else if (level == 1) {
							btn.addClass("active");
							btn.data("id", menu.id);
							btn.data("pid", menu.pid);
							btn.html('<ul class="level2"></ul><span>'
											+ menu.name
											+ '</span><i class="fa fa-remove text-danger"></i>');
							var ul = btn.find("ul");
							insertPlusBtn(ul, menu.id)
						}
						if (callback) {
							callback(res.data.id);
						}
					});
}
/**
 * 切换右侧区域内容
 * @param id
 * @returns
 */
function changeFormPortal(id) {
	LayerMsgBox.loading("请稍等...", 500);
	var url = "/admin/wechat/menu/edit/" +mpId+"-"+ (id ? id : 0);
	menuPortal.ajaxPortal(true, url, true);
}
/**
 * 刷新数据并保持一级菜单中选中的依然选中
 * @returns
 */
function refreshAllDatas(){
	var activeMenu=$("ul.level1>li.active");
	var refreshActiveId=0;
	if(activeMenu&&activeMenu.length>0){
		refreshActiveId=activeMenu.data("id");
	}
	readLevel1Menus(function() {
		if(refreshActiveId){
			$("ul.level1>li[data-id='"+refreshActiveId+"']").addClass("active");
		}
		readLevel2Menus();
	});
}
/** 
 * 删除菜单后的处理逻辑 是否需要清空右侧表单
 * @param id
 * @returns
 */
function delMenuProcess(id){
	var activeMenu=$(".menus ul>li.active");
	if(activeMenu&&activeMenu.length>0){
		var activeId=activeMenu.data("id");
		if(activeId==id){
			menuPortal.empty();
		}
	}
}
 
/**
 * 删除菜单
 * @param menu
 * @param id
 * @param pid
 * @returns
 */
function delMenu(menu, id, pid) {
	LayerMsgBox.confirm("确定删除此菜单？",function(){
		LayerMsgBox.loading("执行中...", 3000);
		Ajax.get("/admin/wechat/menu/delete/" +mpId+"-"+ id, function(res) {
			LayerMsgBox.success("删除成功", 100, function() {
				
				delMenuProcess(id);
				if (pid == 0) {
					refreshAllDatas();
					menu.data("id", 0);
					menu.html('<span><i class="fa fa-plus"></i></span>');
				} else {
					var ul = menu.parent();
					menu.remove();
					var plusBtn = ul.find("li[data-id='0']");
					if (!plusBtn || (plusBtn && plusBtn.length == 0)) {
						insertPlusBtn(ul, pid);
					}
					
					
				}
			});
		});
	})
	

}
/**
 * 修改一个新菜单之后 
 * @param menu
 * @returns
 */
function changeMenu(menu) {
	var li = $("li[data-id='" + menu.id + "']");
	var ul = li.parent();
	var pid = menu.pid;
	readLevel2MenusByPid(ul, pid, function() {
		var li = $("li[data-id='" + menu.id + "']");
		li.addClass("active");
	});
}
/**
 * 提交编辑表单
 * @returns
 */
function submitModifyForm() {
	var form = $("#menuEditForm");
	if (FormChecker.check(form)) {
		LayerMsgBox.loading("执行中...", 3000);
		var formData = $("#menuEditForm").serialize();
		Ajax.post("/admin/wechat/menu/update", formData, function(res) {
			LayerMsgBox.success("操作成功", 500, function() {
				var menu = res.data;
				if (menu.pid > 0) {
					changeMenu(menu);
				} else {
					refreshAllDatas();
				}
			});
		});
	}

}

/**
 * 将最新的菜单定义同步到公众号
 * @returns
 */
function publishToWeixin() {
	LayerMsgBox.confirm("确认发布到公众号?", function() {
		LayerMsgBox.loading("发布中...", 10000);
		Ajax.get("/admin/wechat/menu/publish/"+mpId, function() {
			LayerMsgBox.success("发布成功,请到公众号里查看效果", 2000);
		});
	});
}
/**
 * 初始化页面组价点击事件
 * @returns
 */
function initMenuEvent(){
	//一级菜单点击事件
	$(".menus").on("click", "ul.level1>li", function(e) {
		e.preventDefault();
		e.stopPropagation();
		$("ul.level1>li.active,ul.level2>li.active").removeClass("active");
		var btn = $(this);
		var id = btn.data("id");
		var pid = btn.data("pid");
		if (id > 0) {
			btn.addClass("active");
			changeFormPortal(id);
		} else {
			createNewMenu(1, pid, btn, function(newId) {
				changeFormPortal(newId);
			});
		}
	});
	//二级菜单点击事件
	$(".menus").on("click", "ul.level2>li", function(e) {
		e.preventDefault();
		e.stopPropagation();
		$("ul.level1>li.active,ul.level2>li.active").removeClass("active");
		var btn = $(this);
		var id = btn.data("id");
		var pid = btn.data("pid");
		if (id > 0) {
			btn.addClass("active");
			changeFormPortal(id);
		} else {
			createNewMenu(2, pid, btn, function(newId) {
				changeFormPortal(newId);
			});
		}
	});
	//点击菜单右上角删除按钮 事件绑定
	$(".menus").on("click", "i.fa-remove", function(e) {
		e.preventDefault();
		e.stopPropagation();
		var btn = $(this);
		var li = btn.parent();
		var id = li.data("id");
		var pid = li.data("pid");
		delMenu(li, id, pid);
	});
}
//初始化
$(function() {
	initMenuEvent();
	readLevel1Menus(function() {
		readLevel2Menus();
	});
});