package cn.jbolt._admin.supplier;

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
import cn.jbolt.common.model.Supplier;
import cn.jbolt.common.util.ExcelUtils;

@CheckPermission(PermissionKey.SUPPLIER)
public class SupplierAdminController extends BaseController {
	@Inject
	private SupplierService service;
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
		set("supplier", service.findById(getInt(0)));
		render("edit.html");
	}
	
	/**
	 * 取供应商信息
	 */
	public void getsupplier() {
		renderJson(service.getsupplier(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(Supplier.class, "supplier")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(Supplier.class, "supplier")));
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
	 * 导出供应商信息表
	 * @throws Exception 
	 */
	public void downloadExcel() throws Exception {
		String excelName = "supplier";
		String[] headList= {"编号","供应商名称","区域/地区","等级","添加时间"};
		String[] fieldList= {"id","supplierName","supplierArea","supplierLevel","createTime"};
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Supplier> list = service.findAll();
		for (Supplier supplier : list) {
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("id", supplier.getId());
			map.put("supplierName", supplier.getSupplierName());
			map.put("supplierArea", dictionaryService.findById(supplier.getSupplierArea()).getName());
			map.put("supplierLevel", dictionaryService.findById(supplier.getSupplierLevel()).getName());
			map.put("createTime", supplier.getCreateTime());
			dataList.add(map);
		}
		ExcelUtils.createExcel(getResponse(), excelName, headList, fieldList, dataList);
	}
}
