package cn.jbolt._admin.updatemgr;

import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.model.ChangeLog;
import cn.jbolt.common.model.JboltVersion;
import cn.jbolt.common.model.JboltVersionFile;
import cn.jbolt.common.util.DateUtil;

/**
 * @author 小木 qq:909854136
 * @version 创建时间：2019年1月9日 下午2:48:24
 */
@CheckPermission(PermissionKey.JBOLTVERSION)
public class JBoltVersionAdminController extends BaseController {
	@Inject
	private JBoltVersionService service;
	@Inject
	private ChangeLogService changeLogService;
	@Inject
	private JboltVersionFileService jboltVersionFileService;
	
	/**
	 * 管理页面分页查询数据
	 */
	public void index() {
		set("pageData", service.paginate(getPageNumber(), getPageSize()));
		render("index.html");
	}

	public void uploadImage() {
		String todayFolder = DateUtil.getNowStr("yyyyMMdd");
		UploadFile file = getFile("img", "jboltversion/" + todayFolder);
		if (file == null || file.getFile() == null) {
			renderJsonFail("上传失败");
		} else {
			String path = JFinal.me().getConstants().getBaseUploadPath() + "/jboltversion/" + todayFolder + "/"
					+ file.getFileName();
			if (StrKit.notBlank(MainConfig.BASE_UPLOAD_PATH_PRE)) {
				path = path.replace(MainConfig.BASE_UPLOAD_PATH_PRE, "");
			}
			renderJsonData(path);
		}
	}
	
	public void changelog() {
		Integer jboltVersionId=getInt(0);
		set("jboltVersionId", jboltVersionId);
		set("imghost", PropKit.get("editor_imghost"));
		set("changelog", changeLogService.findByJboltVersionId(jboltVersionId));
		render("changelog.html");
	}
	@ActionKey("/admin/jboltversion/changelog/submit")
	public void submitChangeLog() {
		renderJson(changeLogService.submit(getSessionAdminUserId(),getModel(ChangeLog.class,"changelog")));
	}
	public void files() {
		Integer jboltVersionId=getInt(0);
		set("jboltVersionId", jboltVersionId);
		set("files", jboltVersionFileService.getFilesByJboltVersionId(jboltVersionId));
		render("files.html");
	}
	public void addFile() {
		Integer jboltVersionId=getInt(0);
		set("jboltVersionId", jboltVersionId);
		render("addfile.html");
	}
	public void editFile() {
		Integer fileId=getInt(0);
		JboltVersionFile file=jboltVersionFileService.findById(fileId);
		if(file==null){
			renderFormError("数据不存在");
			return;
		}
		set("jboltVersionFile", file);
		if(isOk(file.getJboltVersionId())){
			set("jboltVersionId", file.getJboltVersionId());
		}
		render("editfile.html");
	}
	public void saveFile() {
		renderJson(jboltVersionFileService.save(getSessionAdminUserId(),getModel(JboltVersionFile.class,"jboltVersionFile")));
	}
	public void updateFile() {
		renderJson(jboltVersionFileService.update(getSessionAdminUserId(),getModel(JboltVersionFile.class,"jboltVersionFile")));
	}
	public void deleteFile() {
		renderJson(jboltVersionFileService.delete(getSessionAdminUserId(),getInt(0)));
	}

	public void add() {
		render("add.html");
	}

	public void edit() {
		set("jboltVersion", service.findById(getInt(0)));
		render("edit.html");
	}

	public void save() {
		renderJson(service.save(getSessionAdminUserId(), getModel(JboltVersion.class, "jboltVersion")));
	}

	public void update() {
		renderJson(service.update(getSessionAdminUserId(), getModel(JboltVersion.class, "jboltVersion")));
	}

	public void delete() {
		renderJson(service.delete(getSessionAdminUserId(), getInt(0)));
	}

	public void toggleIsNew() {
		renderJson(service.doToggleIsNew(getSessionAdminUserId(), getInt(0)));
	}
}
