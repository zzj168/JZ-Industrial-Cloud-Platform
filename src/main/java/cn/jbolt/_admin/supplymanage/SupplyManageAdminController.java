package cn.jbolt._admin.supplymanage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.jfinal.aop.Inject;
import com.jfinal.core.NotAction;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.PageSize;
import cn.jbolt.common.model.Supplymanage;
import cn.jbolt.common.util.ExcelBean;
import cn.jbolt.common.util.ExcelUtils;

@CheckPermission(PermissionKey.SUPPLYMANAGE)
public class SupplyManageAdminController extends BaseController {
	@Inject
	private SupplyManageService service;
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
		set("supplymanage", service.findById(getInt(0)));
		render("edit.html");
	}
	
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(Supplymanage.class, "supplymanage")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(Supplymanage.class, "supplymanage")));
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
	 * 下载
	 * @throws Exception 
	 */
	public void downloadToExcel() throws Exception {
		String[] headList= {"供应商编号","供应商名称","收货日期","型号","箱数","每箱数量","总数量","单价","总金额","付款金额",
				"付款日期","待付款","待收款"};
		String[] fieldList= {"id","supplierName","receiptDate","model","num","numPerBox","totalNum","pice","totalMoney","payMoney",
				"payDate","waitPay","waitReceipt"};
		List<Map<String, Object>> dataList = service.selectAllMapList();
		//ExcelUtils.downLoadExcel(getResponse(),"supplierexcel",headList, fieldList, dataList);
		ExcelUtils.createExcel(getResponse(),"supplierexcel", headList, fieldList, dataList);
	}
	/**
	 * 上传
	 * @throws Exception 
	 */
	public void uploadExcel() throws Exception{
		UploadFile file = getFile("file");
		String[] fileName = {"supplierId","supplierName","receiptDate","model","num","numPerBox","totalNum","price","payMoney",
				"payDate","waitPay"};
		ExcelBean<Supplymanage> excelBean = ExcelUtils.uploadExcel(file, fileName,Supplymanage.class);
		List<Supplymanage> list = excelBean.getCorrectList();
		List<Integer> errorList = excelBean.getErrorList();
		//去重 记录错误数据
		int count = 0;
		for (Supplymanage supplymanage : list) {
			if (checkExistDb(supplymanage)) {
				count++;
				//去除重复对象
				list.remove(supplymanage);
			}else {
					supplymanage.setCreateTime(new Date());
					supplymanage.save();
			}
		renderJsonFail("上传成功，去掉重复文件"+count+"个,错误数据行数"+errorList);
		}
	}
	/**
	 * 数据校验
	 * 
	 * @param supplymanage
	 * @return
	 */
	@NotAction
	private boolean checkExistDb(Supplymanage supplymanage) {
		// 判断是不是存在
		Integer id = (int) Double.parseDouble(supplymanage.get("supplierId").toString());
		Integer count = service.selectByIdAndName(id,supplymanage.getSupplierName());
		if (count != 0) {
			return true;
		}
		return false;
	}
}
