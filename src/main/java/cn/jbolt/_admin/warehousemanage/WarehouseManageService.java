package cn.jbolt._admin.warehousemanage;

import java.util.Date;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.WarehouseManage;


public class WarehouseManageService extends BaseService<WarehouseManage>{
	@Override
	protected WarehouseManage dao() {
		return WarehouseManage.dao;
	}
	
	public Page<WarehouseManage> paginateAdminData(Integer pageNumber, int pageSize) {
		return paginate("id", "desc", pageNumber, pageSize);
	}
	/**
	 * 保存
	 * @param userId
	 * @param warehousemanage
	 * @return
	 */
	public Ret save(Integer userId, WarehouseManage warehousemanage) {
		if(warehousemanage==null||isOk(warehousemanage.getId())||notOk(warehousemanage.getMaterialName())){
			return fail(Msg.PARAM_ERROR);
		}
		warehousemanage.setBomId(warehousemanage.getBomId());
		warehousemanage.setMaterialName(warehousemanage.getMaterialName());
		warehousemanage.setMaterialType(warehousemanage.getMaterialType());
		warehousemanage.setMaterialModel(warehousemanage.getMaterialModel());
		warehousemanage.setProductionProcesses(warehousemanage.getProductionProcesses());
		warehousemanage.setNum(warehousemanage.getNum());
		warehousemanage.setInDate(warehousemanage.getInDate());
		warehousemanage.setOutDate(warehousemanage.getOutDate());
		warehousemanage.setLocationNum(warehousemanage.getLocationNum());	
		warehousemanage.setCreateTime(new Date());
		boolean success=warehousemanage.save();
		if(success){
			//添加日志
			addSaveSystemLog(warehousemanage.getId(), userId,SystemLog.TARGETTYPE_WAREHOUSEMANAGE_INFO, warehousemanage.getMaterialName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 更新
	 * @param warehousemanage
	 * @return
	 */
	public Ret update(Integer userId,WarehouseManage warehousemanage) {
		if(warehousemanage==null||notOk(warehousemanage.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		warehousemanage.setBomId(warehousemanage.getBomId());
		warehousemanage.setMaterialName(warehousemanage.getMaterialName());
		warehousemanage.setMaterialType(warehousemanage.getMaterialType());
		warehousemanage.setMaterialModel(warehousemanage.getMaterialModel());
		warehousemanage.setProductionProcesses(warehousemanage.getProductionProcesses());
		warehousemanage.setNum(warehousemanage.getNum());
		warehousemanage.setInDate(warehousemanage.getInDate());
		warehousemanage.setOutDate(warehousemanage.getOutDate());
		warehousemanage.setLocationNum(warehousemanage.getLocationNum());
		boolean success=warehousemanage.update();
		if(success){
			addUpdateSystemLog(warehousemanage.getId(), userId, SystemLog.TARGETTYPE_SALEMANAGE_INFO, warehousemanage.getMaterialName());
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
			WarehouseManage warehousemanage=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_WAREHOUSEMANAGE_INFO, warehousemanage.getMaterialName());
		}
		return ret;
	}


}
