/**
 * Jbolt-table组件的封装
 */

/**
 * 自动Ajax加载内容的Portal
 */
;(function($){
		$.extend($.fn, {
			jboltTable:function(options){
				var that=this;
				return this.each(function(){
					var table=$(this);
					var thead=table.find("thead");
					var tbody=table.find("tbody");
				});
			},editable:function(options){
				var that=this;
				return this.each(function(){
					 
				});
			}
		});
})(jQuery);

$(function(){
	$("table").jboltTable();
})