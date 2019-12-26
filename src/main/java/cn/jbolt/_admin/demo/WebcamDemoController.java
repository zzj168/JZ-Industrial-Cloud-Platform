package cn.jbolt._admin.demo;

import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
/**
 * webcam组件 demo
 * @ClassName:  WebcamDemoController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月29日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.DEMO)
@UnCheckIfSystemAdmin
public class WebcamDemoController extends BaseController {
	public void index() {
		render("webcam.html");
	}
	
	public void upload() {
		UploadFile file=getFile("file","demo/webcam");
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJsonData(JFinal.me().getConstants().getBaseUploadPath()+"/demo/webcam/"+file.getFileName());
	}
}
