jQuery(document).ready(function($){
    //打开窗口
        $('.cd-popup-trigger3').on('click', function(event){
            event.preventDefault();
            $('.cd-popup3').addClass('is-visible3');
            //$(".dialog-addquxiao").hide()
        });
        //关闭窗口
        $('.cd-popup3').on('click', function(event){
            if( $(event.target).is('.cd-popup-close') || $(event.target).is('.cd-popup3') ) {
                event.preventDefault();
                $(this).removeClass('is-visible3');
            }
        });
 });
		

(function ($) {
	$('body').on('click', '.item', function (){
		$(this).next().slideToggle(100);
		$('.dis').not($(this).next()).slideUp('slow');
	});
}(jQuery));