package cn.jbolt._admin.dictionary;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.DictionaryType;

/**
 * 数据字典类型管理Controller
 * @ClassName:  DictionaryTypeAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission({PermissionKey.DICTIONARY})
@UnCheckIfSystemAdmin
public class DictionaryTypeAdminController extends BaseController {
	@Inject
	private DictionaryTypeService service;
	/**
	 * 加载管理portal
	 */
	public void mgr(){
		set("dictionaryTypes",service.findAll());
		render("mgrportal.html");
	}
	
	public void add(){
		render("add.html");
	}
	public void edit(){
		Integer id=getInt(0);
		if(notOk(id)){
			renderFormError("数据不存在");
			return;
		}
		DictionaryType type=service.findById(id);
		if(type==null){
			if(notOk(id)){
				renderFormError("数据不存在");
				return;
			}
		}
		set("dictionaryType",type);
		render("edit.html");
	}
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(DictionaryType.class, "dictionaryType")));
	}
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(DictionaryType.class, "dictionaryType")));
	}
	
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt(0)));
	}
}
