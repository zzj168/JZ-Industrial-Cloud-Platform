package cn.jbolt.admin.mall.spec;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Spec;

/**   
 * 系统商品规格管理
 * @ClassName:  GoodsTypeAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:59:17   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.MALL_SPEC)
public class SpecAdminController extends BaseController {
	@Inject
	private SpecService service;
	public void index(){
		set("goodsTypes", service.findAll());
		render("index.html");
	}
	
	public void add(){
		render("add.html");
	}
	
	public void edit(){
		Integer id=getInt(0);
		if(notOk(id)){
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		Spec spec=service.findById(getInt(0));
		if(spec==null){
			renderFormError(Msg.DATA_NOT_EXIST);
			return;
		}
		set("spec", spec);
		render("edit.html");
	}
	
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(Spec.class,"spec")));
	}
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(Spec.class,"spec")));
	}
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt(0)));
	}
	
 
	
}
