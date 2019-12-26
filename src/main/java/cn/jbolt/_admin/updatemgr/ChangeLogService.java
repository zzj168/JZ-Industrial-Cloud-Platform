package cn.jbolt._admin.updatemgr;

import java.util.Date;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.ChangeLog;
import cn.jbolt.common.model.JboltVersion;
import cn.jbolt.common.model.SystemLog;

/**
 * jbolt升级管理中升级日志模块Service
 * @ClassName:  ChangeLogService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class ChangeLogService extends BaseService<ChangeLog> {
	private ChangeLog dao = new ChangeLog().dao();
	@Inject
	private JBoltVersionService jBoltVersionService;

	@Override
	protected ChangeLog dao() {
		return dao;
	}
	/**
	 * 更新一个版本的changeLog
	 * @param userId
	 * @param changeLog
	 * @return
	 */
	public Ret submit(Integer userId, ChangeLog changeLog) {
		if(changeLog==null||notOk(changeLog.getJboltVersionId())){return fail(Msg.PARAM_ERROR);}
		if(notOk(changeLog.getContent())){
			return fail("请输入changelog的内容");
		}
		JboltVersion jboltVersion=jBoltVersionService.findById(changeLog.getJboltVersionId());
		if(jboltVersion==null){
			return fail("JBolt的版本信息不存在");
		}
		boolean success=false;
		if(notOk(changeLog.getId())){
			changeLog.setCreateTime(new Date());
			success=changeLog.save();
		}else{
			success=changeLog.update();
		}
		if(success){
			addUpdateSystemLog(jboltVersion.getId(), userId, SystemLog.TARGETTYPE_JBOLT_VERSION, jboltVersion.getVersion(),"的ChangeLog");
		}
		
		return success?success("成功"):fail("失败");
	}

	/**
	 * 根据版本获取changeLog
	 * @param jboltVersionId
	 * @return
	 */
	public ChangeLog findByJboltVersionId(Integer jboltVersionId) {
		return findFirst(Kv.by("jbolt_version_id", jboltVersionId));
	}

}
