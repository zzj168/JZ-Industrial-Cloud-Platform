package cn.jbolt.admin.bom;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Kv;
import com.jfinal.upload.UploadFile;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.PageSize;
import cn.jbolt.common.config.UploadFolder;
import cn.jbolt.common.model.Bom;
import cn.jbolt.common.util.ExcelUtils;

@CheckPermission(PermissionKey.BOM)
public class BomController extends BaseController {
	@Inject
	private BomService service;
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
		set("bom", service.findById(getInt(0)));
		render("edit.html");
	}
	

	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(Bom.class, "bom")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(Bom.class, "bom")));
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
	 * 导出为excel
	 * @throws Exception 
	 */
	public void downloadExcel() throws Exception{
		String excelName="bom";
		String[] headList= {"物料编号","物料名称","种类","型号","产品规格"};
		String[] fieldList= {"id","materialName","materialType","materialModel","productionProcesses"};
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		List<Bom> list = service.findAll();
		for (Bom bom : list) {
			HashMap<String,Object> map = new HashMap<String,Object>();
			map.put("id", bom.getId());
			map.put("materialName", bom.getMaterialName());
			map.put("materialType", dictionaryService.findById(bom.getMaterialType()).getName());
			map.put("materialModel", dictionaryService.findById(bom.getMaterialModel()).getName());
			map.put("productionProcesses", dictionaryService.findById(bom.getProductionProcesses()).getName());
			dataList.add(map);
		}
		//导出物料信息
		ExcelUtils.createExcel(getResponse(), excelName, headList, fieldList, dataList);
	}
	
}
