package cn.jbolt._admin.demo;

import com.jfinal.aop.Inject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.jboltfile.JboltFileService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt._admin.updatemgr.JBoltVersionService;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.UploadFolder;
import cn.jbolt.common.model.Demotable;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.DateUtil;

/**
* @author 小木 qq 909854136
* @version 创建时间：2019年3月19日 下午11:45:18
*/
@CheckPermission(PermissionKey.DEMO)
@UnCheckIfSystemAdmin
public class DemoController extends BaseController {
	@Inject
	private DictionaryService dictionaryService;
	@Inject
	private JBoltVersionService jBoltVersionService;
	@Inject
	private UserService userService;
	@Inject
	private JboltFileService jboltFileService;
	
	public void index(){
		render("index.html");
	}

	/*
	 * public void tabtrigger(){ render("tabtrigger.html"); }
	 */
	public void jboltlayertrigger(){
		render("jboltlayertrigger.html");
	}
	/**
	 * 读取用户表数据给Select
	 */
	public void customattr() {
		renderJsonData(userService.findAll());
	}
	public void level3menu() {
		render("level3menu.html");
	}
	public void temp(){
		if(isAjax()){
			renderJsonSuccess();
		}else{
			renderHtml("<h2>这是Demo临时测试界面</h2>");
		}
	
	}
	/**
	 * 主从表管理案例
	 */
	public void masterSlaveMgr() {
		
	}
	
	public void submit(){
		String[] aihao=getParaValues("aihao");
		if(aihao!=null&&aihao.length>0){
			set("aihao", ArrayUtil.join(aihao, ","));
		}
		String[] aihaoCheck=getParaValues("aihaoCheck");
		if(aihaoCheck!=null&&aihaoCheck.length>0){
			set("aihaoCheck", ArrayUtil.join(aihaoCheck, ","));
		}
		keepPara();
		render("index.html");
	}
	
	public void btn(){
	}
	public void submitbtn(){
		renderJsonSuccess();
	}
	public void submitDateAndTime(){
		System.out.println(getDateTime("datetime"));
		String time=get("time");
		System.out.println(time);
		System.out.println(DateUtil.getTime(time));
		Demotable demotable=getModel(Demotable.class,"dt");
		System.out.println(JsonKit.toJson(demotable));
		keepModel(Demotable.class,"dt");
		keepPara();
		render("index.html");
	}
	
	public void submitLayDate(){
		keepPara();
		render("index.html");
	}
	/**
	 * 前端组件数据源Demo
	 */
	public void dictionary(){
		renderJsonData(dictionaryService.getOptionListByType(get("key")));
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
	/*
	 * public void summernoteInJBoltLayer() { set("imghost",
	 * PropKit.get("editor_imghost")); render("summernote_jboltlayer.html"); }
	 */
	
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
