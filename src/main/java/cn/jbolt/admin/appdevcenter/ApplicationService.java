package cn.jbolt.admin.appdevcenter;

import java.util.Date;

import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Application;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.RandomUtil;

public class ApplicationService extends BaseService<Application> {
	private Application dao = new Application().dao();
	@Override
	protected Application dao() {
		return dao;
	}
	/**
	 * 通过AppId获取一个application
	 * @param appId
	 * @return
	 */
	public Application getByAppId(String appId) {
		return findFirst(Kv.by("app_id", appId));
	}
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param type
	 * @param enable 
	 * @return
	 */
	public Page<Application> paginateAdminList(Integer pageNumber, Integer pageSize, String keywords, Integer type, Boolean enable) {
		if(notOk(keywords)&&notOk(type)) {
			return paginate("id","desc",pageNumber, pageSize);
		}
		Kv otherParas=Kv.create();
		if(isOk(type)) {
			otherParas.set("type", type);
		}
		otherParas.setIfNotNull("enable", enable);
		
		if(notOk(keywords)) {
			return paginate(otherParas,"id","desc",pageNumber, pageSize);
		}
		return paginateByKeywords("id","desc",pageNumber, pageSize, keywords, "name,brief_info,app_id", otherParas);
	}
	/**
	  * 删除
	 * @param id
	 * @return
	 */
	public Ret deleteApplication(Integer userId,Integer id) {
		Ret ret=deleteById(id);
		if(ret.isOk()) {
			Application application=ret.getAs("data");
			//TODO 删除统计信息
			//TODO 关联信息等
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_APPLICATION, application.getName());
			return SUCCESS;
		}
		return FAIL;
	}

	/**
	 * 切换状态
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "enable");
		if(ret.isOk()){
			Application application=ret.getAs("data");
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_APPLICATION, application.getName(), "的状态为["+(application.getEnable()?"启用]":"禁用]"));
		}
		return ret;
	}
	/**
	 * 切换接口是否NeedCheckSign
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleNeedCheckSign(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "need_check_sign");
		if(ret.isOk()){
			Application application=ret.getAs("data");
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_APPLICATION, application.getName(), "的属性[是否开启接口校验Signature]为["+(application.getNeedCheckSign()?"开启]":"关闭]"));
			return SUCCESS;
		}
		return FAIL;
	}
	/**
	 * 变更AppSecret
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret changeAppSecret(Integer userId, Integer id) {
		if(notOk(id)) {return fail(Msg.PARAM_ERROR);}
		Application application=findById(id);
		if(application==null) {return fail(Msg.DATA_NOT_EXIST);}
		application.setAppSecret(genAppSecret());
		boolean success=application.update();
		if(success) {
			//cache
			addUpdateSystemLog(application.getId(), userId, SystemLog.TARGETTYPE_APPLICATION, application.getName(), "的AppSecret");
		}
		return ret(success);
	}
	/**
	 * 保存
	 * @param userId
	 * @param app
	 * @return
	 */
	public Ret save(Integer userId, Application app) {
		if(app==null||isOk(app.getId())||notOk(app.getName())||notOk(app.getType())) {
			return fail(Msg.PARAM_ERROR);
		}
		String name=app.getName().trim();
		if(existsName(name)) {
			return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		app.setName(name);
		Date now=new Date();
		app.setCreateTime(now);
		app.setUserId(userId);
		app.setUpdateTime(now);
		app.setUpdateUserId(userId);
		app.setEnable(false);
		app.setAppId(genAppId());
		app.setAppSecret(genAppSecret());
		if(app.getNeedCheckSign()==null) {
			app.setNeedCheckSign(true);
		}
		boolean success=app.save();
		if(success) {
			//添加日志
			addSaveSystemLog(app.getId(), userId, SystemLog.TARGETTYPE_APPLICATION, app.getName());
		}
		return ret(success);
	}
	/**
	 * 生成32位appSecret
	 * @return
	 */
	private String genAppSecret() {
		return Base64Kit.encode(RandomUtil.randomLowWithNumber(32));
	}
	/**
	 * 生成一个appId
	 * @return
	 */
	private String genAppId() {
		String appId="jb"+RandomUtil.randomLowWithNumber(10)+"olt";
		if(exists("app_id", appId)) {
			return genAppId();
		}
		return appId;
	}
	
	
	/**
	 * 更新
	 * @param userId
	 * @param app
	 * @return
	 */
	public Ret update(Integer userId, Application app) {
		if(app==null||notOk(app.getId())||notOk(app.getName())||notOk(app.getType())) {
			return fail(Msg.PARAM_ERROR);
		}
		String name=app.getName().trim();
		if(existsName(name,app.getId())) {
			return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		app.setName(name);
		app.setUpdateTime(new Date());
		app.setUpdateUserId(userId);
		boolean success=app.update();
		if(success) {
			//添加日志
			addUpdateSystemLog(app.getId(), userId, SystemLog.TARGETTYPE_APPLICATION, app.getName());
		}
		return ret(success);
	}
 

}
