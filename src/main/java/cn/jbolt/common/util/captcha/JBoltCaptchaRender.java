package cn.jbolt.common.util.captcha;

import javax.servlet.http.Cookie;

import com.jfinal.captcha.CaptchaManager;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.jbolt.base.ControllerKit;
import cn.jbolt.common.model.GlobalConfig;

public class JBoltCaptchaRender extends CaptchaRender {
		protected String type=GlobalConfig.CAPTCHA_TYPE_DEFAULT;
		public JBoltCaptchaRender() {
			super();
		}
		public JBoltCaptchaRender(String type) {
			this.type=type;
		}
		/**
		 * 生成验证码
		 */
		@Override
		public void render() {
			switch (type) {
			case GlobalConfig.CAPTCHA_TYPE_DEFAULT:
				super.render();
				break;
			case GlobalConfig.CAPTCHA_TYPE_GIF:
				renderGifCaptcha();
				break;
			case GlobalConfig.CAPTCHA_TYPE_BUBBLE_PNG:
				renderBubblePngCaptcha();
				break;
			default:
				super.render();
				break;
			}
		
			
			
		}
		
		/**
		 * 生成gif 验证码
		 * @param controller
		 */
		private void renderGifCaptcha() {
			JBoltCaptcha captcha = new JBoltGifCaptcha(108,40,4);
			genCaptcha(captcha);
		}
		/**
		 * 生成气泡PNG验证码
		 * @param controller
		 */
		private void renderBubblePngCaptcha() {
			JBoltCaptcha captcha = new JBoltSpecCaptcha(108,40,4);
			genCaptcha(captcha);
		}
		
		protected String genKey() {
			String captchaKey = getCaptchaKeyFromCookie();
			if (StrKit.isBlank(captchaKey)) {
				captchaKey = StrKit.getRandomUUID();
			}
			return captchaKey;
		}
		
		
		/**
		 * 底层生成方法
		 * @param response
		 * @param captcha
		 */
		private void genCaptcha(JBoltCaptcha captcha) {
			captcha.gen(genKey());
			CaptchaManager.me().getCaptchaCache().put(captcha);
			try {
		        Cookie cookie = new Cookie(captchaName, captcha.getKey());
				cookie.setMaxAge(-1);
				cookie.setPath("/");
				response.addCookie(cookie);
				
				response.setHeader("Pragma", "No-cache");  
		        response.setHeader("Cache-Control", "no-cache");  
		        response.setDateHeader("Expires", 0);  
		        switch (type) {
				case GlobalConfig.CAPTCHA_TYPE_GIF:
					response.setContentType("image/gif"); 
					break;
				case GlobalConfig.CAPTCHA_TYPE_BUBBLE_PNG:
					response.setContentType("image/png"); 
					break;
				}
		        
		        //输出
		        captcha.out(response.getOutputStream());
		        
		         
			} catch (Exception e) {
				Log.getLog(ControllerKit.class).error( "获取验证码异常："+e.getMessage());
			}
		}
	
}
