package cn.jbolt._admin.dictionary;


import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Dictionary;
import cn.jbolt.common.model.DictionaryType;
/**
 * 字典管理Controller
 * @ClassName:  DictionaryAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission({PermissionKey.DICTIONARY})
@UnCheckIfSystemAdmin
public class DictionaryAdminController extends BaseController {
	@Inject
	private DictionaryService service;
	@Inject
	private DictionaryTypeService dictionaryTypeService;
	/**
	 * options
	 */
	@UnCheck
	public void options(){
		renderJsonData(service.getOptionListByType(get("key")));
	}
	/**
	 * 根据获取一级options
	 */
	@UnCheck
	public void poptions(){
		renderJsonData(service.getRootOptionListByType(get("key")));
	}
	/**
	 * 子类级别数据 根据父类ID获取数据
	 */
	@UnCheck
	public void soptions(){
		renderJsonData(service.getSonOptionListByType(get("key"),getInt("pid")));
	}
	/**
	 * 加载管理portal
	 */
	public void mgr(){
		Integer typeId=getInt(0);
		if(notOk(typeId)){
			renderErrorPortal("选择左侧分类查询数据");
			return;
		}else{
			DictionaryType type=dictionaryTypeService.findById(typeId);
			if(type==null){
				renderErrorPortal("参数异常，加载失败");
				return;
			}
			initMgr(type);
		}
		
		render("mgrportal.html");
	}
	
	private void initMgr(DictionaryType type){
		set("dictionaryType", type);
		List<Dictionary> dictionaries=service.getListByType(type.getId());
		set("dictionaries",dictionaries);
		if(type.getModeLevel()==DictionaryType.MODE_LEVEL_MUTIL){
			set("dataTotalCount", service.getCountByType(type.getId()));
		}else{
			set("dataTotalCount", dictionaries.size());
		}
	}
	/**
	 * 管理首页
	 */
	public void index(){
		render("index.html");
	}
	/**
	 * 从日志过来的显示一个
	 */
	public void show(){
		Integer dictionaryId=getInt(0);
		if(notOk(dictionaryId)){
			renderErrorPjax("数据不存在或已被删除");
		}else{
			Dictionary dictionary=service.findById(dictionaryId);
			if(dictionary==null){
				renderErrorPjax("数据不存在或已被删除");
			}else{
				Integer typeId=dictionary.getTypeId();
				set("typeId", typeId);
				set("dataList",service.getListByType(typeId));
				set("dataTotalCount", service.getCountByType(typeId));
				set("showId", dictionaryId);
				//TODO #mmm 前端页面实现show效果
				render("index.html");
			}
			
		}
	}
	/**
	 * 除了自己以外的其它所有数据
	 */
	public void select(){
		renderJsonData(service.getListByType(getInt(0)));
	}
	
	/**
	 * 新增
	 */
	public void add(){
		Integer typeId=getInt(0);
		if(notOk(typeId)) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		set("typeId", typeId);
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType.getModeLevel()==DictionaryType.MODE_LEVEL_MUTIL) {
			set("needPidSelect",true);
		}
		render("add.html");
	}
	/**
	 * 新增
	 */
	public void addItem(){
		Integer typeId=getInt(0);
		Integer pid=getInt(1);
		if(notOk(typeId)||notOk(pid)) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType==null) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		if(dictionaryType.getModeLevel()==DictionaryType.MODE_LEVEL_MUTIL) {
			set("needPidSelect",true);
		}
		set("typeId",typeId);
		set("pid", pid);
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		Integer typeId=getInt(0);
		Integer id=getInt(1);
		if(notOk(typeId)||notOk(id)) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		Dictionary dictionary=service.findById(id);
		if(dictionary==null) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType==null) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		if(dictionaryType.getModeLevel()==DictionaryType.MODE_LEVEL_MUTIL) {
			set("needPidSelect",true);
		}
		set("dictionary",dictionary );
		set("typeId", typeId);
		set("pid", dictionary.getPid());
		render("edit.html");
	}
	/**
	 * 编辑
	 */
	public void editItem(){
		Integer typeId=getInt(0);
		Integer id=getInt(1);
		if(notOk(typeId)||notOk(id)) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		Dictionary dictionary=service.findById(id);
		if(dictionary==null) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType==null) {
			renderFormError(Msg.PARAM_ERROR);
			return;
		}
		if(dictionaryType.getModeLevel()==DictionaryType.MODE_LEVEL_MUTIL) {
			set("needPidSelect",true);
		}
		set("typeId", typeId);
		set("dictionary",dictionary );
		set("pid", dictionary.getPid());
		render("edit.html");
	}
	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save(){
		Dictionary dictionary=getModel(Dictionary.class, "dictionary");
		renderJson(service.save(getSessionAdminUserId(),dictionary));
	}
	/**
	 * 更新
	 */
	public void update(){
		Dictionary dictionary=getModel(Dictionary.class, "dictionary");
		renderJson(service.update(getSessionAdminUserId(),dictionary));
	}
	/**
	 * 删除
	 */
	public void delete(){
		renderJson(service.deleteDictionaryById(getSessionAdminUserId(),getInt()));
	}
	/**
	 * 上移
	 */
	public void up(){
		renderJson(service.doUp(getSessionAdminUserId(),getInt()));
	}
	/**
	 * 下移
	 */
	public void down(){
		renderJson(service.doDown(getSessionAdminUserId(),getInt()));
	}
	/**
	 * 初始化顺序
	 */
	public void initRank(){
		renderJson(service.doInitRank(getSessionAdminUserId(),getInt()));
	}
}
