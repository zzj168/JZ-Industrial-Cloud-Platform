package cn.jbolt.admin.warehouse;

import java.util.Date;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.Warehouse;

public class WarehouseService extends BaseService<Warehouse>{
	
	private Warehouse dao = new Warehouse().dao();
	
	@Override
	protected Warehouse dao() {
		return dao;
	}
	
	public Page<Warehouse> paginateAdminData(Integer pageNumber, int pageSize) {
		return paginate("id", "desc", pageNumber, pageSize);
	}
	
	/**
	 * 保存
	 * @param userId
	 * @param warehouse
	 * @return
	 */
	public Ret save(Integer userId, Warehouse warehouse) {
		if(warehouse==null||isOk(warehouse.getId())||notOk(warehouse.getWarehouseName())){
			return fail(Msg.PARAM_ERROR);
		}
		String warehouseName=warehouse.getWarehouseName().trim();
		if(exists("warehouseName",warehouseName)){
			return fail("仓库【"+warehouse.getWarehouseName()+"】已经存在，请输入其它名称");
			//return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		warehouse.setWarehouseName(warehouseName);		
		warehouse.setWarehousePosition(warehouse.getWarehousePosition());
		warehouse.setWarehouseType(warehouse.getWarehouseType());
		warehouse.setNum(warehouse.getNum());		
		warehouse.setCreateTime(new Date());
		boolean success=warehouse.save();
		if(success){
			//添加日志
			addSaveSystemLog(warehouse.getId(), userId,SystemLog.TARGETTYPE_WAREHOUSE_INFO, warehouseName);
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 更新
	 * @param supplier
	 * @return
	 */
	public Ret update(Integer userId,Warehouse warehouse) {
		if(warehouse==null||notOk(warehouse.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=warehouse.getWarehouseName().trim();
		if (exists("warehouseName",name,warehouse.getId())) {
			return fail("仓库【"+name+"】已经存在，请输入其它名称");
		}
		warehouse.setWarehouseName(name);
		warehouse.setWarehousePosition(warehouse.getWarehousePosition());
		warehouse.setWarehouseType(warehouse.getWarehouseType());
		warehouse.setNum(warehouse.getNum());				
		boolean success=warehouse.update();
		if(success){
			addUpdateSystemLog(warehouse.getId(), userId, SystemLog.TARGETTYPE_WAREHOUSE_INFO, warehouse.getWarehouseName());
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
			Warehouse warehouse=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_WAREHOUSE_INFO, warehouse.getWarehouseName());
		}
		return ret;
	}
	
	
	
	
	
	
	
	
	

}
