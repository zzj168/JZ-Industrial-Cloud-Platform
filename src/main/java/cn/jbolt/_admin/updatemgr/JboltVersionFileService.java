package cn.jbolt._admin.updatemgr;

import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.JboltVersion;
import cn.jbolt.common.model.JboltVersionFile;
import cn.jbolt.common.model.SystemLog;

/**
 * JBolt版本更新文件管理Service
 * @ClassName:  JboltVersionFileService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JboltVersionFileService extends BaseService<JboltVersionFile> {
	private JboltVersionFile dao = new JboltVersionFile().dao();
	@Inject
	private JBoltVersionService jBoltVersionService;
	@Override
	protected JboltVersionFile dao() {
		return dao;
	}
	/**
	 * 得到一个新版本的更新文件列表
	 * @param jboltVersionId
	 * @return
	 */
	public List<JboltVersionFile> getFilesByJboltVersionId(Integer jboltVersionId) {
		return getCommonList(Kv.by("jbolt_version_id", jboltVersionId));
	}
	/**
	 * 保存
	 * @param userId
	 * @param jboltVersionFile
	 * @return
	 */
	public Ret save(Integer userId, JboltVersionFile jboltVersionFile) {
		if(jboltVersionFile==null||isOk(jboltVersionFile.getId())||notOk(jboltVersionFile.getUrl())||notOk(jboltVersionFile.getJboltVersionId())){return fail(Msg.PARAM_ERROR);}
		JboltVersion jboltVersion=jBoltVersionService.findById(jboltVersionFile.getJboltVersionId());
		if(jboltVersion==null){return fail("JBolt的版本信息不存在");}
		boolean success=jboltVersionFile.save();
		if(success){
			addSaveSystemLog(jboltVersionFile.getId(), userId, SystemLog.TARGETTYPE_JBOLT_VERSION_FILE,jboltVersion.getVersion()+":"+jboltVersionFile.getUrl());
		}
		return ret(success);
	}
	/**
	 * 更新
	 * @param userId
	 * @param jboltVersionFile
	 * @return
	 */
	public Ret update(Integer userId, JboltVersionFile jboltVersionFile) {
		if(jboltVersionFile==null||notOk(jboltVersionFile.getId())||notOk(jboltVersionFile.getUrl())||notOk(jboltVersionFile.getJboltVersionId())){return fail(Msg.PARAM_ERROR);}
		JboltVersion jboltVersion=jBoltVersionService.findById(jboltVersionFile.getJboltVersionId());
		if(jboltVersion==null){return fail("JBolt的版本信息不存在");}
		boolean success=jboltVersionFile.update();
		if(success){
			addUpdateSystemLog(jboltVersionFile.getId(), userId, SystemLog.TARGETTYPE_JBOLT_VERSION_FILE,jboltVersion.getVersion()+":"+jboltVersionFile.getUrl());
		}
		return ret(success);
	}
	/**
	 * 删除
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId,Integer id){
		Ret ret=deleteById(id);
		if(ret.isOk()){
			JboltVersionFile file=ret.getAs("data");
			JboltVersion jboltVersion=jBoltVersionService.findById(file.getJboltVersionId());
			addDeleteSystemLog(file.getId(), userId, SystemLog.TARGETTYPE_JBOLT_VERSION_FILE,jboltVersion.getVersion()+":"+file.getUrl());
		}
		return ret;
	}
	
	 
	/**
	 * 删除一个版本下的更新文件
	 * @param jboltVersionId
	 * @return
	 */
	public Ret deleteByVersion(Integer jboltVersionId) {
		return deleteBy(Kv.by("jbolt_version_id", jboltVersionId));
	}

}
