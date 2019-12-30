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
		String[] fileName = {"supplierId","supplierName","receiptDate","model","num","numPerBox","totalNum","price","totalMoney","payMoney",
				"payDate","waitPay","waitReceipt","createTime"};
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
	//上传
	public void upFile() throws Exception {
		UploadFile file = getFile("file");
		String[] fileName = {"supplierId","supplierName","receiptDate","model","num","numPerBox","totalNum","price","totalMoney","payMoney",
				"payDate","waitPay","waitReceipt","createTime"};
		// 判断文件格式是否正确
		File upfile = file.getFile();
		if (checkExcel(file,upfile)) {
			throw new Exception("文件格式不正确，请上传Excel文件(后缀为*.xls或*.xlsx)");
		}
		// 解析工作簿
		System.out.println("解析工作簿");
		Workbook workbook = WorkbookFactory.create(upfile);
		Sheet sheet;
		Row row;
		// 解析工作表
		int size = workbook.getNumberOfSheets();
		System.out.println("有" + size + "个工作表");
		// 创建对象集合 存原始数据 防止并发访问出现问题 使用CopyOnWriteArrayList
		//List<T> list = new CopyOnWriteArrayList<T>();
		// 循环读取工作表的数据
		for (int i = 0; i < size; i++) {
			// 拿到具体的工作表
			sheet = workbook.getSheetAt(i);
			System.out.println("当前工作表是" + sheet.getSheetName());
			// 读取数据
			// 得到有效行数
			int rowNumber = sheet.getPhysicalNumberOfRows();
			System.out.println("有效行数" + rowNumber);
			for (int rowIndex = 0; rowIndex < rowNumber; rowIndex++) {
				System.out.println("正在读取第" + (rowIndex + 1) + "行");
				// 跳过表头
				if (rowIndex == 0) {
					continue;
				}
				// 拿到每一行
				row = sheet.getRow(rowIndex);
				//每一行就是一个对象
				Supplymanage supplymanage = new Supplymanage();
				for (int j = 0; j < fileName.length; j++) {
					// 获得列
					Cell cell = row.getCell(j);
					//获得列值
					String value = ExcelUtils.getCellValue(cell);
				}
			}
		}
		// 关闭工作簿
		workbook.close();
		// 操作完毕删除临时文件
		upfile.delete();
	}
	/**
	 * 校验文件格式
	 * 
	 * @param upfile
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@NotAction
	private static boolean checkExcel(UploadFile file, File upfile) throws IOException {
		String fileName = file.getFileName();
		boolean isExcel = fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
		if (isExcel == true) {
			return false;
		}
		InputStream in = new FileInputStream(upfile);
		// 判断数据流是不是xls
		if (POIFSFileSystem.hasPOIFSHeader(in) == false) {
			in.close();
			return false;
		}
		in.close();
		return true;
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
