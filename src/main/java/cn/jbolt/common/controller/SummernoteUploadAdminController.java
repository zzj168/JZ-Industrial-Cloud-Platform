package cn.jbolt.common.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.ext.interceptor.NoUrlPara;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.jboltfile.JboltFileService;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.UploadFolder;
/**
 * 给系统Summernote编辑器组件提供的默认上传路径
 * @ClassName:  SummernoteUploadAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月1日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class SummernoteUploadAdminController extends BaseController {
	@Inject
	private JboltFileService jboltFileService;
	@UnCheck
	@Before(NoUrlPara.class)
	public void index() {
		renderNull();
	}
	@UnCheck
	public void image(){
		String todayFolder=UploadFolder.todayFolder();
		String uploadPath=UploadFolder.EDITOR_SUMMERNOTE_IMAGE+"/"+todayFolder;
		UploadFile file=null;
		try {
			file= getFile("file", uploadPath);
		} catch (RuntimeException e) {
			return;
		}
		if (file != null && file.getFile() != null&&file.getFile().exists()) {
			if(notImage(file.getContentType())){
				renderJsonFail("请上传图片类型文件");
				return;
			}
			//保存图片类文件
			renderJson(jboltFileService.saveImageFile(getSessionAdminUserId(),file,uploadPath));
		}
	}
}
