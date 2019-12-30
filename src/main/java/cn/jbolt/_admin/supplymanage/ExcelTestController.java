package cn.jbolt._admin.supplymanage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.aop.Inject;
import com.jfinal.core.NotAction;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Bom;
import cn.jbolt.common.model.base.BaseBom;
import cn.jbolt.common.util.ExcelUtils;

/**
 * @author zunhui E-mail:qiangzunhui@126.com
 * @version 创建时间：2019年12月18日 下午5:21:31
 * 
 */
@CheckPermission(PermissionKey.TEST)
@UnCheckIfSystemAdmin
public class ExcelTestController extends BaseController {


	public void index() {
		render("index.html");
	}

	/**
	 * 上传表格
	 * 
	 * @throws Exception
	 */
	public void uploadFile() {
//		try {
			// 获取上传文件
//			UploadFile file = getFile("file");
//			String[] fileName = {"materialName","materialType","materialModel","productionProcesses"};
			//List<Bom> list = ExcelUtils.uploadExcel(file, fileName, Bom.class);
			//System.out.println("-------------->"+list);
//			File upfile = file.getFile();
//			// 判断文件格式是否正确
//			if (checkExcel(file, upfile)) {
//				renderJsonFail("文件格式不正确，请上传Excel文件(后缀为*.xls或*.xlsx)");
//				return;
//			}
//			// 解析工作簿
//			System.out.println("解析工作簿");
//			Workbook workbook = WorkbookFactory.create(upfile);
//			Sheet sheet;
//			Row row;
//			// 解析工作表
//			int size = workbook.getNumberOfSheets();
//			System.out.println("有" + size + "个工作表");
//			// 创建对象集合 存原始数据 防止并发访问出现问题 使用CopyOnWriteArrayList
//			List<Bom> list = new CopyOnWriteArrayList<Bom>();
//			///Class<? extends Bom> bom = Bom.class.newInstance().getClass();
//			//Method method = bom.getDeclaredMethod("setId",BaseBom.class);
//			Bom bom = null;
//			// 循环读取工作表的数据
//			for (int i = 0; i < size; i++) {
//				// 拿到具体的工作表
//				sheet = workbook.getSheetAt(i);
//				System.out.println("当前工作表是" + sheet.getSheetName());
//				// 读取数据
//				// 得到有效行数
//				int rowNumber = sheet.getPhysicalNumberOfRows();
//				System.out.println("有效行数" + rowNumber);
//				for (int rowIndex = 0; rowIndex < rowNumber; rowIndex++) {
//					System.out.println("正在读取第" + (rowIndex + 1) + "行");
//					// 跳过表头
//					if (rowIndex == 0) {
//						continue;
//					}
//					// 拿到每一行
//					row = sheet.getRow(rowIndex);
//					// 每一行是一个对象
//					//bom = new Bom();
//					bom = Bom.class.newInstance();
//					// 设置物料名称
//					//bom.setMaterialName(row.getCell(0).getStringCellValue());
//					// 设置类型
//					Cell cell0 = row.getCell(0);
//					Cell cell1 = row.getCell(1);
//					Cell cell2 = row.getCell(2);
//					Cell cell3 = row.getCell(3);
//					Object name = isCellType(cell0);
//					Object setMaterialType = isCellType(cell1);
//					Object setMaterialModel = isCellType(cell1);
//					Object setProductionProcesses = isCellType(cell1);
//					bom.set("materialName", name);
//					bom.set("materialType",setMaterialType);
//					bom.set("materialModel",setMaterialModel);
//					bom.set("productionProcesses",setProductionProcesses);
//					//list.add(bom);
//					System.out.println(bom);
//				}
//			if (list.size() > 0) {
//				System.out.println("原始数据" + list);
//				// 用来记录错误数据
//				List<Bom> filterList = new ArrayList<Bom>();
//				List<Integer> exList = null;
//				System.out.println("原始数据大小：" + list.size());
//				// for (int i = 0; i < list.size(); i++) {
//				Iterator<Bom> iterator = list.iterator();
//				while (iterator.hasNext()) {
//					Bom b = iterator.next();
//					// 校验数据去重
//					System.out.println("即将被检验的数据---------->" + b.getMaterialName());
//					if (checkExistDb(b)) {
//						// 将重复数据存入filterlist
//						filterList.add(b);
//						// 从list中移除重复数据
//						// list.remove(b);
//						list.remove(b);
//					}
//				}
//
//				if (filterList.size() > 0) {
//					// 有多少个数据不符合要求 或者是已经存在 不能重复导入
//					System.out.println("去除重复数据" + filterList.size() + "个");
//					System.out.println("被去除的数据：" + filterList);
//				}
//				if (list.size() > 0) {
//					// 入库
//					System.out.println("入库文件个数--->" + list.size());
//					System.out.println("准备入库的数据：" + list);
//					exList = new LinkedList<Integer>();
//					for (int i=0;i<list.size();i++) {
//						try {
//							 list.get(i).save();
//						} catch (Exception e) {
//							//保存出错
//							System.out.println("错误数据"+list.get(i));
//							exList.add(i+2);
//						}
//					}
//					
//				}
//				renderJsonFail("上传成功"+list.size()+"个，去除重复文件"+filterList.size()+"个,第"+exList+"行信息错误");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			renderJsonFail("文件解析失败，请上传Excel文件");
//			return;
//		}
	}
	/**
	 * 判断表格值类型
	 */
	private static Object isCellType(Cell cell) {
		int type = cell.getCellType();
		switch (type) {
		case 0:
			return cell.getNumericCellValue();
		case 1:
			return cell.getStringCellValue();
		case 4:
			return cell.getBooleanCellValue();
		default:
			return "";
		}
	}
	/**
	 * 数据校验
	 * 
	 * @param b
	 * @return
	 */
	@NotAction
	private boolean checkExistDb(Bom b) {
		// 判断是不是存在
		System.out.println("查询添加中的name" + b.getMaterialName());
		return false;
	}

	/**
	 * 校验文件格式
	 * 
	 * @param upfile
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private boolean checkExcel(UploadFile file, File upfile) throws IOException {
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

	// 导出表格
	public void expExcel() throws Exception {
		// 获得bom数据
		List<Bom> list = new Bom().findAll();
		// 获得输出流
		HttpServletResponse response = getResponse();
		ServletOutputStream os = response.getOutputStream();
		response.reset();
		response.setHeader("Content-disposition", "attachment;filename=test.xls");
		response.setContentType("application/msexcel");
		// 创建可写入的Excel工作薄，且内容将写入到输出流，并通过输出流输出给客户端浏览
		// 创建新的Excel 工作簿
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 创建一个工作表
		XSSFSheet sheet = workbook.createSheet();
		// 在索引0的位置创建行（最顶端的行）创建第一行
		XSSFRow row = sheet.createRow(0);
		// 创建列
		row.createCell(0).setCellValue("编号");
		row.createCell(1).setCellValue("物料名称");
		row.createCell(2).setCellValue("物料型号");
		row.createCell(3).setCellValue("模板型号");
		row.createCell(4).setCellValue("生产工序");
		for (int i = 0; i < list.size(); i++) {
			XSSFRow newRow = sheet.createRow(i + 1);
			newRow.createCell(0).setCellValue(list.get(i).getId());
			newRow.createCell(1).setCellValue(list.get(i).getMaterialName());
			newRow.createCell(2).setCellValue(list.get(i).getMaterialType());
			newRow.createCell(3).setCellValue(list.get(i).getMaterialModel());
			newRow.createCell(4).setCellValue(list.get(i).getProductionProcesses());
		}
		workbook.write(os);
		os.flush();
		// 操作结束，关闭文件
		os.close();
		// 关闭workbook
		workbook.close();
	}
}
