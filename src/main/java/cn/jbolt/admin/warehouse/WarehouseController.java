package cn.jbolt.admin.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.warehouse.WarehouseService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.PageSize;
import cn.jbolt.common.model.Warehouse;
import cn.jbolt.common.util.ExcelUtils;

@CheckPermission(PermissionKey.WAREHOUSE)
public class WarehouseController extends BaseController {
	@Inject
	private WarehouseService service;
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
		set("warehouse", service.findById(getInt(0)));
		render("edit.html");
	}
	

	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(Warehouse.class, "warehouse")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(Warehouse.class, "warehouse")));
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
	 * 导出仓库信息为excel
	 * @throws Exception 
	 */
	public void downloadExcel() throws Exception {
		
		String excelName = "warehouse";
		String[] headList = {"编号","仓库名称","位置","类型","仓位数量","添加时间"};
		String[] fieldList = {"id","warehouseName","warehousePosition","warehouseType","num","createTime"};
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Warehouse> list = service.findAll();
		for (Warehouse warehouse : list) {
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("id", warehouse.getId());
			map.put("warehouseName", warehouse.getWarehouseName());
			map.put("warehousePosition",dictionaryService.findById(warehouse.getWarehousePosition()).getName());
			map.put("warehouseType",dictionaryService.findById(warehouse.getWarehouseType()).getName());
			map.put("num",warehouse.getNum());
			map.put("createTime",warehouse.getCreateTime());
			dataList.add(map);
		}
		ExcelUtils.createExcel(getResponse(), excelName, headList, fieldList, dataList);
	}

}
