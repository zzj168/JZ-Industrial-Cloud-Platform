package cn.jbolt.admin.wechat.autoreply;

import java.util.Collections;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.common.model.WechatKeywords;
import cn.jbolt.common.model.WechatMpinfo;

/**  
 * 微信自定义菜单配置 
 * @ClassName:  WechatKeywordsService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月14日 上午4:31:49   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatKeywordsService extends BaseService<WechatKeywords> {
	private WechatKeywords dao = new WechatKeywords().dao();
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Override
	protected WechatKeywords dao() {
		return dao;
	}
	/**
	 * 删除指定公众平台的自定义菜单数据
	 * @param mpId
	 * @return
	 */
	public Ret deleteByMpId(Integer mpId) {
		return deleteBy(Kv.by("mp_id", mpId));
	}
	public List<WechatKeywords> getListByAutoReplyId(Integer autoReplyId) {
		if(notOk(autoReplyId)) {return Collections.emptyList();}
		return getCommonList(Kv.by("auto_reply_id", autoReplyId));
	}
	/**
	 * 保存
	 * @param userId
	 * @param autoReplyId
	 * @param integer 
	 * @param wechatKeywords
	 * @return
	 */
	public Ret save(Integer userId, Integer autoReplyId, WechatKeywords wechatKeywords) {
		return submit(userId,autoReplyId,wechatKeywords,false);
	}
	/**
	 * 更新
	 * @param userId
	 * @param autoReplyId
	 * @param wechatKeywords
	 * @return
	 */
	public Ret update(Integer userId,Integer autoReplyId, WechatKeywords wechatKeywords) {
		return submit(userId,autoReplyId,wechatKeywords,true);
	}
	public  Ret checkCanOpt(Integer autoReplyId) {
		return checkCanOpt(autoReplyId, null);
	}
	public  Ret checkCanOpt(Integer autoReplyId,Integer id) {
		WechatAutoreply wechatAutoreply=wechatAutoReplyService.findById(autoReplyId);
		if(wechatAutoreply==null) {
			
		}
		Integer mpId=wechatAutoreply.getMpId();
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {return fail("关联微信公众平台不存在");}
		String append="所属公众平台：["+wechatMpinfo.getName()+"] 规则:["+wechatAutoreply.getType()+"]["+wechatAutoreply.getName()+"]";
		if(isOk(id)) {
			WechatKeywords wechatKeywords=findById(id);
			if(wechatKeywords==null) {
				return fail(Msg.DATA_NOT_EXIST);
			}
			if(wechatKeywords.getMpId().intValue()!=mpId.intValue()) {return fail("参数异常:公众平台mpId");}
			if(wechatKeywords.getAutoReplyId().intValue()!=autoReplyId.intValue()) {return fail("参数异常:所属规则 autoReplyId");}
		}
		return success(Kv.by("mpId", mpId).set("append",append),Msg.SUCCESS);
	}
	/**
	 * 提交
	 * @param userId
	 * @param autoReplyId
	 * @param wechatKeywords
	 * @param systemLogType
	 * @return
	 */
	private Ret submit(Integer userId,Integer autoReplyId, WechatKeywords wechatKeywords, boolean update) {
		if(notOk(autoReplyId)||wechatKeywords==null||notOk(wechatKeywords.getName())||notOk(wechatKeywords.getAutoReplyId())||notOk(wechatKeywords.getType())
				||(update==false&&isOk(wechatKeywords.getId()))||(update&&notOk(wechatKeywords.getId()))
				||autoReplyId.intValue()!=wechatKeywords.getAutoReplyId().intValue()
				) {
			return fail(Msg.PARAM_ERROR);
		}
		//校验参数和数据
		Ret checkRet=checkCanOpt(autoReplyId);
		if(checkRet.isFail()) {
			return checkRet;
		}
		Integer mpId=wechatKeywords.getMpId();
		if(mpId.intValue()!=wechatKeywords.getMpId().intValue()) {
			return fail(Msg.PARAM_ERROR);
		}
		//判断和执行操作
		boolean success=false;
		wechatKeywords.setAutoReplyId(autoReplyId);
		String name=wechatKeywords.getName().trim();
		if(update) {
			if(checkNameExist(mpId,name,wechatKeywords.getId())) {
				return fail(Msg.DATA_SAME_NAME_EXIST); 
			}
		}else {
			if(checkNameExist(mpId,name,-1)) {
				return fail(Msg.DATA_SAME_NAME_EXIST); 
			}
		}
		
		wechatKeywords.setName(name);
		if(update) {
			success=wechatKeywords.update();
		}else {
			success=wechatKeywords.save();
		}
		
		if(success) {
			//添加日志
			Kv checkResultKv=checkRet.getAs("data");
			String append=checkResultKv.getStr("append");
			addSystemLog(wechatKeywords.getId(), userId, update?SystemLog.TYPE_UPDATE:SystemLog.TYPE_SAVE, SystemLog.TARGETTYPE_WECHAT_KEYWORDS, wechatKeywords.getName(), append);
		}
		return ret(success);
	}
	/**
	 * 检测数据name是否存在重名
	 * @param mpId
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean checkNameExist(Integer mpId, String name, Integer id) {
		Sql sql=selectSql().selectId().eqQM("mp_id","name").idNoteqQM().first();
		Integer existId=queryInt(sql,mpId,name,id);
		return isOk(existId);
	}
	/**
	 * 删除
	 * @param userId
	 * @param autoReplyId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId, Integer autoReplyId, Integer id) {
		Ret checkRet=checkCanOpt(autoReplyId,id);
		if(checkRet.isFail()) {
			return checkRet;
		}
		Ret ret=deleteById(id);
		if(ret.isOk()) {
			Kv result=checkRet.getAs("data");
			addSystemLog(id, userId, SystemLog.TYPE_DELETE, SystemLog.TARGETTYPE_WECHAT_REPLYCONTENT, "ID:"+id, result.getStr("append"));
		}
		return ret;
	}
	/**
	 * 刪除一个规则下的关联子表 关键词回复设置的关键词
	 * @param autoReplyId
	 */
	public void deleteByAutoReplyId(Integer autoReplyId) {
		if(isOk(autoReplyId)) {
			deleteBy(Kv.by("auto_reply_id", autoReplyId));
		}
	}
	/**
	 * 找到公众平台里配置的全等关键词
	 * @param mpId
	 * @param keywords
	 * @return
	 */
	public WechatKeywords getEqualsTypeKeywords(Integer mpId, String keywords) {
		if(notOk(mpId)||notOk(keywords)) {return null;}
		return findFirst(Kv.by("mp_id", mpId).set("type",WechatKeywords.TYPE_EQUALS).set("name",keywords.trim()));
	}
	/**
	 * 随机一个公众平台里配置的模糊匹配关键词
	 * @param mpId
	 * @param keywords
	 * @return
	 */
	public WechatKeywords getRandLikeTypeKeywords(Integer mpId, String keywords) {
		if(notOk(mpId)||notOk(keywords)) {return null;}
		String sql="select * from #(table) where mp_id=#(mpId) and type=#(type) and name like '%#(keywords)%' order by rand()";
		Kv data=Kv.create();
		data.set("mpId", mpId)
		.set("table",table())
		.set("type",WechatKeywords.TYPE_LIKE)
		.set("keywords",keywords.trim());
		return dao().templateByString(sql, data).findFirst();
	}
	 
}
