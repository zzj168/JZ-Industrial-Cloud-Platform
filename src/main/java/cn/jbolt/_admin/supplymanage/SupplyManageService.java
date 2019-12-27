package cn.jbolt._admin.supplymanage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Supplymanage;
import cn.jbolt.common.model.SystemLog;


public class SupplyManageService extends BaseService<Supplymanage> {
	
	private Supplymanage dao = new Supplymanage().dao();
	@Override
	protected Supplymanage dao() {
		return dao;
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

	/**
	 * 查询数据
	 */
	public List<Map<String,Object>> selectAllMapList() {
		List<Supplymanage> list = dao.findAll();
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		for (Supplymanage supplymanage : list) {
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("id",supplymanage.getId());
			map.put("supplierName", supplymanage.getSupplierName());
			map.put("receiptDate", supplymanage.getReceiptDate());
			map.put("model", supplymanage.getModel());
			map.put("num", supplymanage.getNum());
			map.put("numPerBox", supplymanage.getNumPerBox());
			map.put("totalNum", supplymanage.getTotalNum());
			map.put("pice", supplymanage.getPrice());
			map.put("totalMoney", supplymanage.getTotalMoney());
			map.put("payMoney", supplymanage.getPayMoney());
			map.put("payDate", supplymanage.getPayDate());
			map.put("waitPay", supplymanage.getWaitPay());
			map.put("waitReceipt", supplymanage.getWaitReceipt());
			map.put("createTime", supplymanage.getCreateTime());
			dataList.add(map);
		}
		return dataList;
	}
	/**
	 * 按编号和名称查询
	 * @return 
	 */
	public Integer selectByIdAndName(int supplierld,String supplierName) {
		String sql = "select count(*) from supplymanage where supplierId=? and supplierName=?";
		return Db.queryInt(sql,supplierld,supplierName);
	}
}
