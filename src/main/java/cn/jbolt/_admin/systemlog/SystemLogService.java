package cn.jbolt._admin.systemlog;

import java.util.Date;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.DateUtil;

public class SystemLogService extends BaseService<SystemLog> {
	private SystemLog dao = new SystemLog().dao();
	@Override
	protected SystemLog dao() {
		return dao;
	}
	
	/**
	 * 根据操作类型获取操作名称
	 * @param type
	 * @return
	 */
	public static String typeName(int type){
		String name="未指定";
		switch (type) {
		case SystemLog.TYPE_SAVE:
			name="新增";
			break;
		case SystemLog.TYPE_UPDATE:
			name="更新";
			break;
		case SystemLog.TYPE_DELETE:
			name="删除";
			break;
		case SystemLog.TYPE_LINK_DELETE:
			name="删除子项";
			break;
		case SystemLog.TYPE_LINK_UPDATE:
			name="更新子项";
			break;
		case SystemLog.TYPE_LINK_SAVE:
			name="新增子项";
			break;
		}
		return name;
	}
	/**
	 * 根据操作目标类型获取操作目标名称
	 * @param targetType
	 * @return
	 */
	public static String targetTypeName(int targetType){
		String name="未指定";
		switch (targetType) {
		case SystemLog.TARGETTYPE_DICTIONARY_TYPE:
			name="字典数据类型";
			break;
		case SystemLog.TARGETTYPE_DICTIONARY:
			name="字典数据项";
			break;
		case SystemLog.TARGETTYPE_PERMISSION:
			name="权限定义";
			break;
		case SystemLog.TARGETTYPE_ROLE:
			name="用户角色";
			break;
		case SystemLog.TARGETTYPE_USER:
			name="系统用户";
			break;
		case SystemLog.TARGETTYPE_GLOBAL_CONFIG:
			name="全局配置";
			break;
		case SystemLog.TARGETTYPE_JBOLT_VERSION:
			name="JBolt版本信息";
			break;
		case SystemLog.TARGETTYPE_JBOLT_UPDATE_LIBS:
			name="JBolt第三方Libs";
			break;
		case SystemLog.TARGETTYPE_JBOLT_VERSION_FILE:
			name="JBolt版本更新文件";
			break;
		case SystemLog.TARGETTYPE_MALL_GOODS:
			name="商品";
			break;
		case SystemLog.TARGETTYPE_MALL_BRAND:
			name="品牌";
			break;
		case SystemLog.TARGETTYPE_MALL_GOODS_BACK_CATEGORY:
			name="商品分类_后端";
			break;
		case SystemLog.TARGETTYPE_MALL_GOODS_TYPE:
			name="商品类型";
			break;
		case SystemLog.TARGETTYPE_JBOLT_FILE:
			name="系统文件库文件";
			break;
		case SystemLog.TARGETTYPE_WECHAT_MPINFO:
			name="微信公众平台";
			break;
		case SystemLog.TARGETTYPE_WECHAT_AUTOREPLY:
			name="微信消息回复规则";
			break;
		case SystemLog.TARGETTYPE_WECHAT_REPLYCONTENT:
			name="微信消息回复内容";
			break;
		case SystemLog.TARGETTYPE_WECHAT_KEYWORDS:
			name="微信消息回复触发关键词";
			break;
		case SystemLog.TARGETTYPE_APPLICATION:
			name="API应用开发中心-应用";
			break;
		case SystemLog.TARGETTYPE_USER_CONFIG:
			name="用户配置";
			break;
		}
		return name;
	
	}
	
	/**
	 * 分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Page<SystemLog> paginateSystemLog(int pageNumber,Integer pageSize,String keywords, Date startTime, Date endTime) {
		Kv paras=Kv.create();
		if(StrKit.notBlank(keywords)){
			keywords=keywords.trim();
			paras.set("title",columnLike(keywords));
		}
		if(isOk(startTime)){
			paras.set("create_time >=",DateUtil.HHmmssTo000000Str(startTime));
		}
		if(isOk(endTime)){
			paras.set("create_time <=",DateUtil.HHmmssTo235959Str(endTime));
		}
		
		return paginate(paras, "id", "desc", pageNumber, pageSize, true);
	}
	/**
	 * 添加系统日志
	 * @param type   操作类型
	 * @param targetType 关联目标类型
	 * @param targetId 关联目标ID
	 * @param title  标题内容
	 * @param dictionaryType 关联字典数据类型
	 * @param userId 操作人ID
	 * @param userName 操作人name
	 */
	public void saveLog(int type,int targetType,Object targetId,String title,int dictionaryType,Object userId,String userName){
		SystemLog log=new SystemLog();
		log.set("user_id",userId);
		log.setCreateTime(DateUtil.getNow());
		log.set("target_id",targetId);
		log.setTargetType(targetType);
		log.setType(type);
		log.setUserName(userName);
		log.setTitle(title);
		log.setOpenType(SystemLog.OPENTYPE_URL);
		if(type!=SystemLog.TYPE_DELETE){
			String url=null;
				switch (targetType) {
				case SystemLog.TARGETTYPE_DICTIONARY_TYPE:
					url="admin/dictionary/show/"+targetId;
					break;
				case SystemLog.TARGETTYPE_DICTIONARY:
					url="admin/dictionary/show/"+targetId;
					break;
				case SystemLog.TARGETTYPE_PERMISSION:
					url="admin/permission/show/"+targetId;
					break;
				case SystemLog.TARGETTYPE_ROLE:
					url="admin/role/show/"+targetId;
					break;
				case SystemLog.TARGETTYPE_USER:
					url="admin/user/show/"+targetId;
					break;
				case SystemLog.TARGETTYPE_GLOBAL_CONFIG:
					url="admin/globalconfig/show/"+targetId;
					break;
				case SystemLog.TARGETTYPE_JBOLT_VERSION:
					url="admin/jboltversion/show/"+targetId;
					break;
				case SystemLog.TARGETTYPE_MALL_GOODS:
					url="admin/mall/goods/show/"+targetId;
					break;
				case SystemLog.TARGETTYPE_MALL_BRAND:
					url="admin/mall/brand/show/"+targetId;
					break;
				}
				log.setUrl(url);
		}
		log.save();
	}

}
