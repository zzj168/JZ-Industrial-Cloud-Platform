package cn.jbolt.admin.bom;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Bom;
import cn.jbolt.common.model.Dictionary;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.CACHE;

public class BomService extends BaseService<Bom>{
	
	private Bom dao = new Bom().dao();
	private DictionaryService dictionaryService=new DictionaryService();
 	@Override
	protected Bom dao() {
		return dao;
	}
	
	public Page<Bom> paginateAdminData(Integer pageNumber, int pageSize) {
		return paginate("id", "desc", pageNumber, pageSize);
	}
	
	/**
	 * 保存
	 * @param userId
	 * @param bom
	 * @return
	 */
	public Ret save(Integer userId, Bom bom) {
		if(bom==null||isOk(bom.getId())||notOk(bom.getMaterialName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=bom.getMaterialName().trim();
		if(exists("materialName",name)){
			return fail("物料【"+bom.getMaterialName()+"】已经存在，请输入其它名称");
			//return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		bom.setMaterialName(name);
		bom.setMaterialType(bom.getMaterialType());		
		bom.setMaterialModel(bom.getMaterialModel());
		bom.setProductionProcesses(bom.getProductionProcesses());
		boolean success=bom.save();
		if(success){
			//添加日志
			addSaveSystemLog(bom.getId(), userId,SystemLog.TARGETTYPE_BOM_INFO, name);
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 更新
	 * @param supplier
	 * @return
	 */
	public Ret update(Integer userId,Bom bom) {
		if(bom==null||notOk(bom.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=bom.getMaterialName().trim();
		if (exists("materialName",name,bom.getId())) {
			return fail("物料【"+bom.getMaterialName()+"】已经存在，请输入其它名称");
		}
		bom.setMaterialName(name);
		bom.setMaterialType(bom.getMaterialType());		
		bom.setMaterialModel(bom.getMaterialModel());
		bom.setProductionProcesses(bom.getProductionProcesses());	
		boolean success=bom.update();
		if(success){
			addUpdateSystemLog(bom.getId(), userId, SystemLog.TARGETTYPE_BOM_INFO, bom.getMaterialName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 删除
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId,Integer id) {
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			//添加日志
			Bom bom=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_BOM_INFO, bom.getMaterialName());
		}
		return ret;
	}
	/**
	 * 保存excel表数据
	 */
	public void saveBomExcel(List<Map<String, Object>> list) {
	}
}
