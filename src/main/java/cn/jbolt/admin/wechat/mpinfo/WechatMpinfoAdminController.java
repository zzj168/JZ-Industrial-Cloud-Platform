package cn.jbolt.admin.wechat.mpinfo;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.config.UploadFolder;
import cn.jbolt.common.model.WechatMpinfo;

/**   
 * 微信公众平台管理
 * @ClassName:  WechatMpinfoAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月7日 下午5:15:36   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.WECHAT_MPINFO)
public class WechatMpinfoAdminController extends BaseController {
	@Inject
	private WechatMpinfoService service;
	public void index(){
		keepPara();
		setAttr("pageData", service.paginateAdminList(getPageNumber(),getPageSize(),getKeywords(),getEnable(),getType(),getInt("subjectType"),getBoolean("isAuthenticated")));
		render("index.html");
	}
	
	/**
	 * 上传公众平台相关图片
	 */
	public void uploadImage(){
		//上传到今天的文件夹下
		String todayFolder=UploadFolder.todayFolder();
		String uploadPath=UploadFolder.WECHAT_MPINFO+"/"+todayFolder;
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJsonData(JFinal.me().getConstants().getBaseUploadPath()+"/"+uploadPath+"/"+file.getFileName());
	}
	/**
	 * 进入新增表单
	 */
	public void add(){
		render("add.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(WechatMpinfo.class,"wechatMpinfo")));
	}
	/**
	 * 编辑
	 */
	public void edit(){
		WechatMpinfo wechatMpinfo=service.findById(getInt(0));
		if(wechatMpinfo==null){
			renderFormError(Msg.DATA_NOT_EXIST);
		}else{
			set("wechatMpinfo",wechatMpinfo);
			render("edit.html");
		}
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(WechatMpinfo.class,"wechatMpinfo")));
	}
	/**
	 * 删除一个公众平台
	 */
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 清空所有配置 慎用
	 */
	public void clearAllConfigs(){
		renderJson(service.clearAllConfigs(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换启用状态
	 */
	public void toggleEnable(){
		renderJson(service.toggleEnable(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换认证状态
	 */
	public void toggleAuthenticated(){
		renderJson(service.toggleAuthenticated(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * enable 启用状态数据源
	 */
	public void enableOptions(){
		List<OptionBean> optionBeans=new ArrayList<OptionBean>();
		optionBeans.add(new OptionBean("已启用", true));
		optionBeans.add(new OptionBean("已禁用", false));
		renderJsonData(optionBeans);
	}
	/**
	 * isAuthenticated  是否认证数据源
	 */
	public void isAuthenticatedOptions(){
		List<OptionBean> optionBeans=new ArrayList<OptionBean>();
		optionBeans.add(new OptionBean("已认证", true));
		optionBeans.add(new OptionBean("未认证", false));
		renderJsonData(optionBeans);
	}
	/**
	 * 类型数据源
	 */
	public void typeOptions(){
		List<OptionBean> optionBeans=new ArrayList<OptionBean>();
		optionBeans.add(new OptionBean("订阅号", WechatMpinfo.TYPE_DYH));
		optionBeans.add(new OptionBean("服务号", WechatMpinfo.TYPE_FWH));
		optionBeans.add(new OptionBean("企业微信", WechatMpinfo.TYPE_QYWX));
		optionBeans.add(new OptionBean("小程序", WechatMpinfo.TYPE_XCX));
		renderJsonData(optionBeans);
	}
	/**
	 * 主体类型数据源
	 */
	public void subjectTypeOptions(){
		List<OptionBean> optionBeans=new ArrayList<OptionBean>();
		optionBeans.add(new OptionBean("个人", WechatMpinfo.SUBJECT_TYPE_PERSONAL));
		optionBeans.add(new OptionBean("个体工商户", WechatMpinfo.SUBJECT_TYPE_INDIVIDUAL_BUSINESS));
		optionBeans.add(new OptionBean("企业", WechatMpinfo.SUBJECT_TYPE_COMPANY));
		optionBeans.add(new OptionBean("媒体", WechatMpinfo.SUBJECT_TYPE_MEDIA));
		optionBeans.add(new OptionBean("社团组织", WechatMpinfo.SUBJECT_TYPE_ORG));
		optionBeans.add(new OptionBean("政府机关", WechatMpinfo.SUBJECT_TYPE_GOV));
		optionBeans.add(new OptionBean("事业单位", WechatMpinfo.SUBJECT_TYPE_GOV_SPONSORED_INSTITUTION));
		renderJsonData(optionBeans);
	}
}
