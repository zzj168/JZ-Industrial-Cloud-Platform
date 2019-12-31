package cn.jbolt.admin.customer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.PageSize;
import cn.jbolt.common.model.Customer;
import cn.jbolt.common.util.ExcelUtils;

@CheckPermission(PermissionKey.CUSTOMER)
public class CustomerController extends BaseController {
	@Inject
	private CustomerService service;
	@Inject
	private DictionaryService dictionaryService;
	public void index(){
		//Page<Supplier> pageData=service.paginateAdminData(getPageNumber(),PageSize.PAGESIZE_ADMIN_LIST_20);
		set("pageData",service.paginateAdminData(getPageNumber(),PageSize.PAGESIZE_ADMIN_LIST_30));
		render("index.html");
	}
	
	/**
	 * 新增
	 */
	public void add(){
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		set("customer", service.findById(getInt(0)));
		render("edit.html");
	}
	

	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(Customer.class, "customer")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(Customer.class, "customer")));
	}
	/**
	 * 删除
	 */
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt(0)));
	}
	
	/**
	 * 前端组件数据源Demo
	 */
	public void dictionary(){
		renderJsonData(dictionaryService.getOptionListByType(get("key")));
	}
	/**
	 * 导出客户信息为excel
	 * @throws Exception 
	 */
	public void downloadExcel() throws Exception {
		String excelName = "customer";
		String[] headList= {"编号","客户名称","区域/地区","等级","添加时间"};
		String[] fieldList= {"id","customerName","customerArea","customerLevel","createTime"};
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Customer> list = service.findAll();
		for (Customer customer : list) {
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("id", customer.getId());
			map.put("customerName",customer.getCustomerName());
			map.put("customerArea",dictionaryService.findById(customer.getCustomerArea()).getName());
			map.put("customerLevel",dictionaryService.findById(customer.getCustomerLevel()).getName());
			map.put("createTime",customer.getCreateTime());
			dataList.add(map);
		}
		ExcelUtils.createExcel(getResponse(), excelName, headList, fieldList, dataList);
	}
}
