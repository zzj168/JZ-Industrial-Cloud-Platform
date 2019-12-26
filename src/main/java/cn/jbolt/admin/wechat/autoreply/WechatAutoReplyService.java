package cn.jbolt.admin.wechat.autoreply;

import java.util.Date;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.common.model.WechatMpinfo;

/**   
 * 微信公众号回复规则
 * @ClassName:  WechatAutoReplyService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日 下午11:58:43   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatAutoReplyService extends BaseService<WechatAutoreply> {
	private WechatAutoreply dao = new WechatAutoreply().dao();
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatKeywordsService wechatKeywordsService;
	@Inject
	private WechatReplyContentService wechatReplyContentService;

	@Override
	protected WechatAutoreply dao() {
		return dao;
	}

	/**
	 * 检测指定公众平台是否已经有配置了
	 * @param mpId
	 * @return
	 */
	public boolean checkWechatMpinfoInUse(Integer mpId) {
		return exists("mp_id", mpId);
	}
	/**
	 * 删除指定公众平台的自动回复模块的配置信息
	 * @param mpId
	 */
	public Ret deleteByMpId(Integer mpId) {
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		//关键词
		wechatKeywordsService.deleteByMpId(mpId);
		//回复
		wechatReplyContentService.deleteByMpId(mpId);
		//自身
		return deleteBy(Kv.by("mp_id", mpId));
		
	}
	/**
	 * 分页条件查询自动回复规则
	 * @param mpId
	 * @param type
	 * @param keywords
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<WechatAutoreply> paginateAdminMgrList(Integer mpId, Integer type, 
			String keywords, int pageNumber,int pageSize) {
		
		return daoTemplate(
				"wechat.autoreply.paginateAdminList", 
				Kv.by("type", type)
				.set("mpId",mpId)
				.setIfNotBlank("keywords", keywords)
				).paginate(pageNumber, pageSize);
	}
	/**
	 * 保存新建规则
	 * @param userId
	 * @param mpId
	 * @param type
	 * @param wechatAutoreply
	 * @return
	 */
	public Ret save(Integer userId, Integer mpId, Integer type, WechatAutoreply wechatAutoreply) {
		return submit(userId, mpId, type, wechatAutoreply, false);
	}
	/**
	 * 提交
	 * @param userId
	 * @param mpId
	 * @param type
	 * @param wechatAutoreply
	 * @param systeLogType
	 */
	public Ret submit(Integer userId, Integer mpId, Integer type, WechatAutoreply wechatAutoreply,boolean update) {
		if(notOk(mpId)||notOk(type)||wechatAutoreply==null
				||(!update&&isOk(wechatAutoreply.getId()))
				||(update&&notOk(wechatAutoreply.getId()))
				||notOk(wechatAutoreply.getName())||notOk(wechatAutoreply.getType())||notOk(wechatAutoreply.getMpId())) {
			return fail(Msg.PARAM_ERROR);
		}
		//检测表单提交Model的参数和提交地址附带参数是否一致
		if(wechatAutoreply.getMpId().intValue()!=mpId.intValue()) {return fail("操作异常:公众平台mpId");}
		if(wechatAutoreply.getType().intValue()!=type.intValue()) {return fail("操作异常:公众平台type");}
		if(update) {
			//更新时需要判断数据存在
			WechatAutoreply dbAutoreply=findById(wechatAutoreply.getId());
			if(dbAutoreply==null) {return fail(Msg.DATA_NOT_EXIST);}
			//检测数据库数据和提交地址附带参数是否一致
			if(dbAutoreply.getMpId().intValue()!=mpId.intValue()) {return fail("操作异常:公众平台mpId");}
			if(dbAutoreply.getType().intValue()!=type.intValue()) {return fail("操作异常:公众平台type");}
		}
		//校验公共平台存在
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {return fail("关联的微信公众平台"+Msg.DATA_NOT_EXIST);}
		String name=wechatAutoreply.getName().trim();
		wechatAutoreply.setName(name);
		boolean success=false;
		//save和update分别处理
		if(update) {
			if(checkNameExist(mpId,type,name,wechatAutoreply.getId())) {return fail(Msg.DATA_SAME_NAME_EXIST);}
			success=wechatAutoreply.update();
		}else {
			if(checkNameExist(mpId,type,name,-1)) {return fail(Msg.DATA_SAME_NAME_EXIST);}
			wechatAutoreply.setCreateTime(new Date());
			wechatAutoreply.setUserId(userId);
			wechatAutoreply.setEnable(false);
			success=wechatAutoreply.save();
		}
		if(success) {
			//添加日志
			addSystemLog(wechatAutoreply.getId(), userId, update?SystemLog.TYPE_UPDATE:SystemLog.TYPE_SAVE, SystemLog.TARGETTYPE_WECHAT_AUTOREPLY, wechatAutoreply.getName(),",属于公众平台【"+wechatMpinfo.getName()+"】");
		}
		return ret(success);
	}
	/**
	 * 检测重名 同一账号同一类型下不能重名
	 * @param mpId
	 * @param type
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean checkNameExist(Integer mpId, Integer type,String name, Integer id) {
		Sql sql=selectSql().selectId().eqQM("mp_id","type","name").idNoteqQM().first();
		Integer existId = queryInt(sql,mpId,type,name,id);
		return isOk(existId);
	}

	/**
	 * 更新规则
	 * @param userId
	 * @param mpId
	 * @param type
	 * @param wechatAutoreply
	 * @return
	 */
	public Ret update(Integer userId, Integer mpId, Integer type, WechatAutoreply wechatAutoreply) {
		return submit(userId, mpId, type, wechatAutoreply,true);
	}
	/**
	 * 删除
	 * @param userId
	 * @param mpId
	 * @param type
	 * @param id
	 * @return
	 */
	public Ret deleteAutoreply(Integer userId, Integer mpId, Integer type, Integer id) {
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		Ret ret=checkOptParas(mpId,type,id);
		if(ret.isFail()) {return ret;}
		Kv kv=ret.getAs("data");
		WechatAutoreply wechatAutoreply=kv.getAs("wechatAutoreply");
		WechatMpinfo wechatMpinfo=kv.getAs("wechatMpinfo");
		boolean success=wechatAutoreply.delete();
		if(success) {
			wechatKeywordsService.deleteByAutoReplyId(wechatAutoreply.getId());
			wechatReplyContentService.deleteByAutoReplyId(wechatAutoreply.getId());
			//添加日志
			addSystemLog(wechatAutoreply.getId(), userId, SystemLog.TYPE_DELETE, SystemLog.TARGETTYPE_WECHAT_AUTOREPLY, wechatAutoreply.getName(),",属于公众平台【"+wechatMpinfo.getName()+"】");
		}
		return ret(success);
	}
	/**
	 * 更新删除操作时的校验处理
	 * @param mpId
	 * @param type
	 * @param id
	 * @return
	 */
	private Ret checkOptParas(Integer mpId, Integer type, Integer id) {
		if(notOk(mpId)||notOk(type)||notOk(id)) {
			return fail(Msg.PARAM_ERROR);
		}
		WechatAutoreply wechatAutoreply=findById(id);
		if(wechatAutoreply.getMpId().intValue()!=mpId.intValue()) {return fail("操作异常:公众平台mpId");}
		if(wechatAutoreply.getType().intValue()!=type.intValue()) {return fail("操作异常:公众平台type");}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {return fail("关联的微信公众平台"+Msg.DATA_NOT_EXIST);}
		return success(Kv.by("wechatAutoreply", wechatAutoreply).set("wechatMpinfo",wechatMpinfo), Msg.SUCCESS);
	}
	/**
	 * 切换启用禁用状态
	 * @param userId
	 * @param mpId
	 * @param type
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Integer userId, Integer mpId, Integer type, Integer id) {
		Ret ret=checkOptParas(mpId,type,id);
		if(ret.isFail()) {return ret;}
		Kv kv=ret.getAs("data");
		WechatAutoreply wechatAutoreply=kv.getAs("wechatAutoreply");
		WechatMpinfo wechatMpinfo=kv.getAs("wechatMpinfo");
		if(wechatAutoreply.getEnable()==null) {
			wechatAutoreply.setEnable(false);
		}
		//执行切换之前需要处理一个事儿 就是判断是否必须只能启用唯一性
		if(type.intValue()==WechatAutoreply.TYPE_DEFAULT||type.intValue()==WechatAutoreply.TYPE_SUBSCRIBE) {
			if(wechatAutoreply.getEnable()==false) {
				//关注回复和无消息默认回复 需要唯一性处理
				changeCurrrentEnableToFalse(mpId,type);
			}
		}
		wechatAutoreply.setEnable(!wechatAutoreply.getEnable());
		boolean success=wechatAutoreply.update();
		if(success) {
			//添加日志
			addSystemLog(wechatAutoreply.getId(), userId, SystemLog.TYPE_UPDATE, SystemLog.TARGETTYPE_WECHAT_AUTOREPLY, wechatAutoreply.getName(),"的启用状态:"+wechatAutoreply.getEnable()+"属于公众平台【"+wechatMpinfo.getName()+"】");
		}
		return ret(success);
	}
	/**
	 * 将指定类型数据的enable=true的设置为false
	 * @param mpId
	 * @param type
	 */
	private void changeCurrrentEnableToFalse(Integer mpId,Integer type) {
		if(type.intValue()==WechatAutoreply.TYPE_DEFAULT||type.intValue()==WechatAutoreply.TYPE_SUBSCRIBE) {
			Sql sql=updateSql().set("enable", false).eqQM("mp_id","enable","type");
			update(sql,mpId,TRUE(),type);
		}
	}
	/**
	 * 查找指定公众平台下配置的一个启用的默认或者关注回复
	 * @param mpId
	 * @return
	 */
	public WechatAutoreply getTheEnableAutoReply(Integer mpId,int type) {
		return findFirst(Kv.by("mp_id", mpId).set("type",type).set("enable",TRUE()));
	}
	
	

}
