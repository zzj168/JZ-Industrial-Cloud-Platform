package cn.jbolt._admin.supplier;


import java.util.Date;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Supplier;
import cn.jbolt.common.model.SystemLog;


public class SupplierService extends BaseService<Supplier>{
	
	private Supplier dao = new Supplier().dao();
	
	@Override
	protected Supplier dao() {
		return dao;
	}
	
	public Page<Supplier> paginateAdminData(Integer pageNumber, int pageSize) {
		return paginate("id", "desc", pageNumber, pageSize);
	}
	
	/**
	 * 保存
	 * @param userId
	 * @param supplier
	 * @return
	 */
	public Ret save(Integer userId, Supplier supplier) {
		if(supplier==null||isOk(supplier.getId())||notOk(supplier.getSupplierName())){
			return fail(Msg.PARAM_ERROR);
		}
		String supplierName=supplier.getSupplierName().trim();
		if(exists("supplierName",supplierName)){
			return fail("供应商【"+supplier.getSupplierName()+"】已经存在，请输入其它名称");
			//return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		supplier.setSupplierName(supplierName);
		supplier.setSupplierArea(supplier.getSupplierArea());
		supplier.setSupplierLevel(supplier.getSupplierLevel());
		supplier.setCreateTime(new Date());
		boolean success=supplier.save();
		if(success){
			//添加日志
			addSaveSystemLog(supplier.getId(), userId,SystemLog.TARGETTYPE_SUPPLIER_MPINFO, supplier.getSupplierName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 更新
	 * @param supplier
	 * @return
	 */
	public Ret update(Integer userId,Supplier supplier) {
		if(supplier==null||notOk(supplier.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=supplier.getSupplierName().trim();
		if (exists("supplierName",name,supplier.getId())) {
			return fail("供应商【"+supplier.getSupplierName()+"】已经存在，请输入其它名称");
		}
		supplier.setSupplierName(name);
		supplier.setSupplierArea(supplier.getSupplierArea());
		supplier.setSupplierLevel(supplier.getSupplierLevel());		
		boolean success=supplier.update();
		if(success){
			addUpdateSystemLog(supplier.getId(), userId, SystemLog.TARGETTYPE_SUPPLIER_MPINFO, supplier.getSupplierName());
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
			Supplier supplier=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_SUPPLIER_MPINFO, supplier.getSupplierName());
		}
		return ret;
	}
	
	/**
	 * 根据编号取得供应商信息
	 * @param userId
	 * @param id
	 * @return
	 */
	public Supplier getsupplier(Integer userId,Integer id) {
		return findById(id);
	}

}
