package cn.jbolt._admin.supplymanage;

import java.util.Date;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Supplymanage;
import cn.jbolt.common.model.SystemLog;


public class SupplyManageService extends BaseService<Supplymanage> {
	
	
	@Override
	protected Supplymanage dao() {
		return Supplymanage.dao;
	}
	
	public Page<Supplymanage> paginateAdminData(Integer pageNumber, int pageSize) {
		return paginate("id", "desc", pageNumber, pageSize);
	}
	
	/**
	 * 保存
	 * @param userId
	 * @param supplymanage
	 * @return
	 */
	public Ret save(Integer userId, Supplymanage supplymanage) {
		if(supplymanage==null||isOk(supplymanage.getId())||notOk(supplymanage.getSupplierName())){
			return fail(Msg.PARAM_ERROR);
		}
		supplymanage.setSupplierName(supplymanage.getSupplierName());
		supplymanage.setReceiptDate(supplymanage.getReceiptDate());
		supplymanage.setModel(supplymanage.getModel());
		supplymanage.setNum(supplymanage.getNum());
		supplymanage.setNumPerBox(supplymanage.getNumPerBox());
		supplymanage.setTotalNum(supplymanage.getTotalNum());
		supplymanage.setPrice(supplymanage.getPrice());
		supplymanage.setTotalMoney(supplymanage.getTotalMoney());
		supplymanage.setPayMoney(supplymanage.getPayMoney());
		supplymanage.setPayDate(supplymanage.getPayDate());
		supplymanage.setWaitPay(supplymanage.getWaitPay());
		supplymanage.setCreateTime(new Date());
		boolean success=supplymanage.save();
		if(success){
			//添加日志
			addSaveSystemLog(supplymanage.getId(), userId,SystemLog.TARGETTYPE_SUPPLYMANAGE_INFO, supplymanage.getSupplierName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 更新
	 * @param supplymanage
	 * @return
	 */
	public Ret update(Integer userId,Supplymanage supplymanage) {
		if(supplymanage==null||notOk(supplymanage.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		supplymanage.setSupplierName(supplymanage.getSupplierName());
		supplymanage.setReceiptDate(supplymanage.getReceiptDate());
		supplymanage.setModel(supplymanage.getModel());
		supplymanage.setNum(supplymanage.getNum());
		supplymanage.setNumPerBox(supplymanage.getNumPerBox());
		//supplymanage.setTotalNum(supplymanage.getTotalNum());
		supplymanage.setPrice(supplymanage.getPrice());
		//supplymanage.setTotalMoney(supplymanage.getTotalMoney());
		supplymanage.setPayMoney(supplymanage.getPayMoney());
		supplymanage.setPayDate(supplymanage.getPayDate());
		supplymanage.setWaitPay(supplymanage.getWaitPay());
		boolean success=supplymanage.update();
		if(success){
			addUpdateSystemLog(supplymanage.getId(), userId, SystemLog.TARGETTYPE_SUPPLYMANAGE_INFO, supplymanage.getSupplierName());
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
			Supplymanage supplymanage=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_SUPPLYMANAGE_INFO, supplymanage.getSupplierName());
		}
		return ret;
	}

}
