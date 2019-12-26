package cn.jbolt.common.gen;

import java.util.Date;

import cn.jbolt.common.util.DateUtil;

/** 
 * 资源在线压缩生成  
 * @ClassName:  AssetsCompressor   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月15日 下午3:26:10   
 * 
 */
public class JBoltAssetsCompressor extends AssetsCompressor{
	
	public static void main(String[] args) {
		JBoltAssetsCompressor assetsCompressor=new JBoltAssetsCompressor();
		//开始压缩jbolt-admin.js
		//js源文件
		String jbolt_admin_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-admin.js";
		//js压缩后文件
		String jbolt_admin_min_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-admin.min.js";
		//执行JS压缩
		assetsCompressor.js(jbolt_admin_js, jbolt_admin_min_js);
		
		//开始压缩jbolt-wechat-menu.js
		//js源文件
		String jbolt_wechat_menu_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-wechat-menu.js";
		//js压缩后文件
		String jbolt_wechat_menu_min_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-wechat-menu.min.js";
		//执行JS压缩
		assetsCompressor.js(jbolt_wechat_menu_js, jbolt_wechat_menu_min_js);
		
		//开始压缩jbolt-admin.css
		//css源文件
		String jbolt_admin_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-admin.css";
		//CSS、压缩后文件
		String jbolt_admin_min_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-admin.min.css";
		//执行css压缩
		assetsCompressor.css(jbolt_admin_css, jbolt_admin_min_css);
		
		
		//开始压缩jbolt-wechat-menu.css
		//css源文件
		String jbolt_wechat_menu_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-wechat-menu.css";
		//CSS、压缩后文件
		String jbolt_wechat_menu_min_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-wechat-menu.min.css";
		//执行css压缩
		assetsCompressor.css(jbolt_wechat_menu_css, jbolt_wechat_menu_min_css);
		
		//开始压缩login.css
		//css源文件
		String login_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/login.css";
		//css压缩后文件
		String login_min_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/login.min.css";
		//执行JS压缩
		assetsCompressor.css(login_css, login_min_css);
		
		//开始执行html替换
		assetsCompressor.change();
		
		System.out.println("===提示：压缩后，请手动刷新项目工程目录,获取最新压缩文件===");
	}
	@Override
	protected void change() {
		String version=DateUtil.format(new Date(), DateUtil.YMDHMSS2);
		//处理jbolt里js css
		String jbolt_assets_html_path=JBOLT_PROJECT_PATH+"/src/main/webapp/_view/_admin/common/__jboltassets.html";
		changeHtml(jbolt_assets_html_path, version,new String[]{"jbolt-admin","jbolt-wechat-menu"},new String[]{"jbolt-admin","jbolt-wechat-menu","login"});
		
	}
	
}
