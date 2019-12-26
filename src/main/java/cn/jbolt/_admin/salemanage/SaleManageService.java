package cn.jbolt._admin.salemanage;

import java.util.Date;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.SaleManage;
import cn.jbolt.common.model.SystemLog;


public class SaleManageService extends BaseService<SaleManage>{
	
	@Override
	protected SaleManage dao() {
		return SaleManage.dao;
	}
	
	public Page<SaleManage> paginateAdminData(Integer pageNumber, int pageSize) {
		return paginate("id", "desc", pageNumber, pageSize);
	}
	
	/**
	 * 保存
	 * @param userId
	 * @param supplymanage
	 * @return
	 */
	public Ret save(Integer userId, SaleManage salemanage) {
		if(salemanage==null||isOk(salemanage.getId())||notOk(salemanage.getSupplierName())){
			return fail(Msg.PARAM_ERROR);
		}
		salemanage.setSupplierName(salemanage.getSupplierName());
		salemanage.setShipDate(salemanage.getShipDate());		
		salemanage.setModel(salemanage.getModel());
		salemanage.setNum(salemanage.getNum());
		salemanage.setNumPerBox(salemanage.getNumPerBox());
		salemanage.setTotalNum(salemanage.getTotalNum());
		salemanage.setPrice(salemanage.getPrice());
		salemanage.setTotalMoney(salemanage.getTotalMoney());
		salemanage.setReceiptMoney(salemanage.getReceiptMoney());
		salemanage.setReceiptDate(salemanage.getReceiptDate());
		salemanage.setWaitPay(salemanage.getWaitPay());		
		salemanage.setCreateTime(new Date());
		boolean success=salemanage.save();
		if(success){
			//添加日志
			addSaveSystemLog(salemanage.getId(), userId,SystemLog.TARGETTYPE_SALEMANAGE_INFO, salemanage.getSupplierName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 更新
	 * @param supplymanage
	 * @return
	 */
	public Ret update(Integer userId,SaleManage salemanage) {
		if(salemanage==null||notOk(salemanage.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		salemanage.setSupplierName(salemanage.getSupplierName());
		salemanage.setShipDate(salemanage.getShipDate());		
		salemanage.setModel(salemanage.getModel());
		salemanage.setNum(salemanage.getNum());
		salemanage.setNumPerBox(salemanage.getNumPerBox());
		salemanage.setTotalNum(salemanage.getTotalNum());
		salemanage.setPrice(salemanage.getPrice());
		salemanage.setTotalMoney(salemanage.getTotalMoney());
		salemanage.setReceiptMoney(salemanage.getReceiptMoney());
		salemanage.setReceiptDate(salemanage.getReceiptDate());
		salemanage.setWaitPay(salemanage.getWaitPay());	
		boolean success=salemanage.update();
		if(success){
			addUpdateSystemLog(salemanage.getId(), userId, SystemLog.TARGETTYPE_SALEMANAGE_INFO, salemanage.getSupplierName());
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
			SaleManage salemanage=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_SALEMANAGE_INFO, salemanage.getSupplierName());
		}
		return ret;
	}

}
