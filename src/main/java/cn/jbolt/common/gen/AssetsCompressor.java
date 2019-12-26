package cn.jbolt.common.gen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
/**
 * 静态资源压缩工具
 * @ClassName:  AssetsCompressor   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月7日   
 */
public abstract class AssetsCompressor {
	/**
	 * 项目的绝对路径
	 */
	protected final static String JBOLT_PROJECT_PATH=System.getProperty("user.dir");
	protected final static String JS_URL="https://tool.oschina.net/action/jscompress/js_compress?munge=1&linebreakpos=5000";
	protected final static String CSS_URL="https://tool.oschina.net/action/jscompress/css_compress?linebreakpos=5000";
	/**
	 * 压缩
	 * @param srcFilePath
	 * @param target
	 */
	protected void go(String url,String srcFilePath,String target){
		System.out.println(target+" 处理中...");
		String jsonResult=HttpUtil.post(url, FileUtil.readUtf8String(srcFilePath));
		System.out.println(jsonResult);
		if(jsonResult!=null&&jsonResult.indexOf("result")!=-1){
			JSONObject jsonObject=JSON.parseObject(jsonResult);
			if(jsonObject.containsKey("result")){
				String content=jsonObject.getString("result");
				if(content!=null&&content.trim().length()>0){
					FileUtil.writeUtf8String(content, target);
				}else {
					FileUtil.writeUtf8String("", target);
				}
			}
		}
	}
	/**
	 * 压缩JS
	 * @param srcFilePath
	 * @param target
	 */
	protected void js(String srcFilePath,String target){
		System.out.println("正在处理JS压缩...");
		go(JS_URL, srcFilePath, target);
		System.out.println("JS压缩完成...");
	}
	/**
	 * 压缩CSS
	 * @param srcFilePath
	 * @param target
	 */
	protected void css(String srcFilePath,String target){
		System.out.println("正在处理CSS压缩...");
		go(CSS_URL, srcFilePath, target);
		System.out.println("CSS压缩完成...");
	}
	
	
	protected abstract void change();
	
	protected void changeHtml(String path,String version,String[] jsFiles,String[] cssFiles){
		if(jsFiles==null&&cssFiles==null){
			return;
		}
		String html=FileUtil.readUtf8String(path);
		Document doc=Jsoup.parse(html);
		doc.outputSettings().prettyPrint(true);
		if(jsFiles!=null){
			for(String js:jsFiles){
				Element element=doc.selectFirst("script[src*=\""+js+"\"]");
				if(element!=null){
					String oldsrc=element.attr("src");
					System.out.println("原数据:"+oldsrc);
					String src=element.attr("src");
					if(src.indexOf("js?v=")!=-1){
						src=src.substring(0, src.indexOf("?v="));
					}
					src=src+"?v="+version;
					html=StrUtil.replace(html, oldsrc, src);
					System.out.println("现数据:"+src);
					
				}
			}
		}
		if(cssFiles!=null){
			for(String css:cssFiles){
				Element element=doc.selectFirst("link[href*=\""+css+"\"]");
				if(element!=null){
					String oldHref=element.attr("href");
					System.out.println("原数据:"+oldHref);
					String href=element.attr("href");
					if(href.indexOf("css?v=")!=-1){
						href=href.substring(0, href.indexOf("?v="));
					}
					href=href+"?v="+version;
					html=StrUtil.replace(html, oldHref, href);
					System.out.println("现数据:"+href);
					
				}
			}
		}
//		System.out.println(html);
		FileUtil.writeUtf8String(html, path);
		System.out.println("处理完html："+path);
		
	}
}
