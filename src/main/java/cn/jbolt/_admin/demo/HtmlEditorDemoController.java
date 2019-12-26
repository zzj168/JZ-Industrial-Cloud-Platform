package cn.jbolt._admin.demo;

import com.jfinal.aop.Inject;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.jboltfile.JboltFileService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.UploadFolder;
/**
 * Demo演示-富文本编辑器 组件页面
 * @ClassName:  HtmlEditorDemoController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月1日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.DEMO)
@UnCheckIfSystemAdmin
public class HtmlEditorDemoController extends BaseController {
	@Inject
	private JboltFileService jboltFileService;
	public void index() {
		render("htmleditor.html");
	}
	
	public void summernote() {
		set("imghost", PropKit.get("editor_imghost"));
		render("summernote.html");
	}
	public void neditorSubmit() {
		if(isAjax()) {
			renderJsonSuccess();
		}else {
			keepPara();
			set("imghost", PropKit.get("editor_imghost"));
			render("neditor.html");
		}
	}
	public void summernoteInDialog() {
		set("imghost", PropKit.get("editor_imghost"));
		render("summernote_dialog.html");
	}
	public void neditor() {
		set("urlprefix", PropKit.get("editor_imghost"));
		render("neditor.html");
	}
	public void neditorInDialog() {
		set("urlprefix", PropKit.get("editor_imghost"));
		render("neditor_dialog.html");
	}
	/**
	 * 上传SummernoteImage图片
	 */
	public void uploadSummernoteImage(){
		//上传到今天的文件夹下
		String todayFolder=UploadFolder.todayFolder();
		String uploadPath=UploadFolder.DEMO_EDITOR_IMAGE+"/"+todayFolder;
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file.getContentType())){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJson(jboltFileService.saveImageFile(getSessionAdminUserId(),file,uploadPath));
	}
	
	public void editorSubmit() {
		if(isAjax()) {
			renderJsonSuccess();
		}else {
			keepPara();
			set("imghost", PropKit.get("editor_imghost"));
			render("summernote.html");
		}
	}
	
	
	
}
