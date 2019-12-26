package cn.jbolt._admin.user;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.jboltfile.JboltFileService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.Option;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.config.UploadFolder;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.CACHE;
@CheckPermission(PermissionKey.USER)
@UnCheckIfSystemAdmin
public class UserAdminCotroller extends BaseController {
	@Inject
	private UserService service;
	@Inject
	private RoleService roleService;
	@Inject
	private JboltFileService jboltFileService;
	/**
	 * 用户表数作为选项数据源
	 */
	public void options() {
		renderJsonData(service.getCommonList("id,name"));
	}
	/**
	 * 管理首页
	 */
	public void index(){
		set("pageData", service.paginateAdminList(getPageNumber(),getPageSize(),getKeywords()));
		keepPara();
		render("index.html");
	}
	/**
	  * 获取用户列表 
	  * 通过关键字匹配 
	 * autocomplete组件使用
	 */
	@UnCheck
	public void autocomplete() {
		renderJsonData(service.getAutocompleteList(get("q"), getInt("limit",10),true,"name,username,pinyin,phone"));
	}
	/**
	 * 上传头像
	 */
	public void uploadAvatar(){
		String uploadPath=UploadFolder.todayFolder(UploadFolder.USER_AVATAR);
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJson(jboltFileService.saveImageFile(getSessionAdminUserId(),file,uploadPath));
	}
	
	/**
	 * 上传头像
	 */
	public void uploadMyAvatar(){
		String uploadPath=UploadFolder.todayFolder(UploadFolder.USER_AVATAR);
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		Integer userId=getSessionAdminUserId();
		Ret ret=jboltFileService.saveImageFile(userId,file,uploadPath);
		if(ret.isFail()) {
			renderJson(ret);
			return;
		}
		String url=ret.getStr("data");
		renderJson(service.updateUserAvatar(userId,url));
	}
	@Before(CacheInterceptor.class)
	public void sexSelect(){
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("男",User.SEX_MALE));
		options.add(new OptionBean("女",User.SEX_FEMALE));
		options.add(new OptionBean("未知",User.SEX_NONE));
		renderJsonData(options);
	}
	/**
	 * 进入自身密码修改界面
	 */
	@CheckPermission(PermissionKey.USERPWD)
	public void pwd(){
		render("pwd.html");
	}
	/**
	 * 进入自身头像修改界面
	 */
	@CheckPermission(PermissionKey.USERAVATAR)
	public void avatar(){
		setAttr("avatar", CACHE.me.getUserAvatar(getSessionAdminUserId()));
		render("avatar.html");
	}
	/**
	 * 进入重置用户密码界面
	 */
	@CheckPermission(PermissionKey.USER)
	public void editpwd(){
		set("userId", getInt(0));
		render("editpwd.html");
	}
	/**
	 * 重置用户密码
	 */
	@CheckPermission(PermissionKey.USER)
	public void submitpwd(){
		Integer userId=getInt("userId");
		String newPwd=get("newPwd");
		String reNewPwd=get("reNewPwd");
		if(notOk(newPwd)||notOk(reNewPwd)){ 
			renderJsonFail(Msg.PARAM_ERROR);
			return;
		}
		if(newPwd.equals(reNewPwd)==false){
			renderJsonFail("两次新密码输入不一致");
			return;
		}
		renderJson(service.submitPwd(getSessionAdminUserId(),userId,newPwd));
	}
	
	/**
	 * 修改用户自己的密码
	 */
	@CheckPermission(PermissionKey.USERPWD)
	public void updatepwd(){
		String oldPwd=get("oldPwd");
		String newPwd=get("newPwd");
		String reNewPwd=get("reNewPwd");
		if(notOk(oldPwd)||notOk(newPwd)||notOk(reNewPwd)){ 
			renderJsonFail(Msg.PARAM_ERROR);
			return;
			}
		if(newPwd.equals(reNewPwd)==false){
			renderJsonFail("两次新密码输入不一致");
			return;
		}
		renderJson(service.doUpdatePwd(getSessionAdminUserId(),oldPwd,newPwd));
	}
	
	
	/**
	 * 新增
	 */
	public void add(){
		set("roles", roleService.findAll());
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		Integer userId=getInt(0);
		Integer myId=getSessionAdminUserId();
		User user=service.findById(userId);
		User me=service.findById(myId);
		if(user.getIsSystemAdmin()&&me.getIsSystemAdmin()==false) {
			renderDialogError("无权修改超管员信息");
			return;
		}
		set("user", user);
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(User.class, "user")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(User.class, "user")));
	}
	/**
	 * 删除
	 */
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换启用状态
	 */
	public void toggleEnable(){
		renderJson(service.toggleEnable(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换启用状态
	 */
	public void deleteRole(){
		renderJson(service.deleteUserRole(getSessionAdminUserId(),getInt(0),getInt(1)));
	}
}
