package cn.jbolt._admin.demo;

import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Kv;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.jboltfile.JboltFileService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.UploadFolder;
/**
 * Demo演示-Bootstrap-fileinput文件上传
 * @ClassName:  MultipleFileSyncUploaderDemoController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月1日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.DEMO)
@UnCheckIfSystemAdmin
public class MultipleFileSyncUploaderDemoController extends BaseController {
	@Inject
	private JboltFileService jboltFileService;
	public void index() {
		render("multiplefilesyncuploader.html");
	}
	
	public void submit() {
		keepPara();
		setMsg("这是提交表单跳转页面，返回到本页面后的效果，输入框里带着值");
		render("multiplefilesyncuploader.html");
	}
	
	/**
	 * 上传文件 同步批量上传文件
	 */
	public void upload(){
		//上传到今天的文件夹下
		String uploadPath=UploadFolder.todayFolder(UploadFolder.DEMO_FILE_UPLOADER);
		List<UploadFile> files=getFiles(uploadPath);
		if(!isOk(files)) {
			renderBootFileUploadError("文件上传失败!");
			return;
		}
		renderJsonSuccess();
	}
	
	
}
