package cn.jbolt.admin.customer;

import java.util.Date;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Customer;
import cn.jbolt.common.model.SystemLog;

public class CustomerService extends BaseService<Customer>{
	
	private Customer dao = new Customer().dao();
	
	@Override
	protected Customer dao() {
		return dao;
	}
	
	public Page<Customer> paginateAdminData(Integer pageNumber, int pageSize) {
		return paginate("id", "desc", pageNumber, pageSize);
	}

	/**
	 * 保存
	 * @param userId
	 * @param supplier
	 * @return
	 */
	public Ret save(Integer userId, Customer customer) {
		if(customer==null||isOk(customer.getId())||notOk(customer.getCustomerName())){
			return fail(Msg.PARAM_ERROR);
		}
		String customerName=customer.getCustomerName().trim();
		if(exists("customerName",customerName)){
			return fail("客户【"+customer.getCustomerName()+"】已经存在，请输入其它名称");
			//return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		customer.setCustomerName(customerName);
		customer.setCustomerArea(customer.getCustomerArea());
		customer.setCustomerLevel(customer.getCustomerLevel());
		customer.setCreateTime(new Date());
		boolean success=customer.save();
		if(success){
			//添加日志
			addSaveSystemLog(customer.getId(), userId,SystemLog.TARGETTYPE_CUSTOMER_INFO, customerName);
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 更新
	 * @param supplier
	 * @return
	 */
	public Ret update(Integer userId,Customer customer) {
		if(customer==null||notOk(customer.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=customer.getCustomerName().trim();
		if (exists("customerName",name,customer.getId())) {
			return fail("客户【"+customer.getCustomerName()+"】已经存在，请输入其它名称");
		}
		customer.setCustomerName(name);
		customer.setCustomerArea(customer.getCustomerArea());
		customer.setCustomerLevel(customer.getCustomerLevel());		
		boolean success=customer.update();
		if(success){
			addUpdateSystemLog(customer.getId(), userId, SystemLog.TARGETTYPE_CUSTOMER_INFO, customer.getCustomerName());
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
			Customer customer=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_CUSTOMER_INFO, customer.getCustomerName());
		}
		return ret;
	}
	
}
