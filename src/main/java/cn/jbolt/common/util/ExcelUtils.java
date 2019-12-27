package cn.jbolt.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.upload.UploadFile;

import cn.jbolt.common.model.Bom;

/**
* @author zunhui qq:1442182361
* @version 2019年12月26日
* 操作数据库表格的工具类
*/
public class ExcelUtils {

	/**
	 * 下载数据库表到excel文件
	 * @throws IOException 
	 */
	public static void downLoadExcel(HttpServletResponse response,String fileName,String[] headList,String[] fieldList,List<Map<String, Object>> dataList) throws IOException {
		//获得输出流
		ServletOutputStream os = response.getOutputStream();
		//设置头信息告知浏览器用什么样的应用打开文件
		// 告诉浏览器用什么软件可以打开此文件
		response.setHeader("Content-Type", "application/vnd.ms-excel");
		// 下载文件的默认名称
		response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName+".xlsx", "utf-8"));
		// 创建可写入的Excel工作薄，且内容将写入到输出流，并通过输出流输出给客户端浏览
		// 创建新的Excel 工作簿
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 创建一个工作表
		XSSFSheet sheet = workbook.createSheet();
		// 在索引0的位置创建行（最顶端的行）创建第一行
		XSSFRow row = sheet.createRow(0);
		//设置表头
		for (int i = 0; i < headList.length; i++) {
			// 创建列并且给列赋值
			row.createCell(i).setCellValue(headList[i]);
		}
		//从第二列开始存放真实数据
		for (int i = 0; i < dataList.size(); i++) {
			//创建数据行
			XSSFRow dataRow = sheet.createRow(i+1);
			//每一行数据都是一个map
			Map<String, Object> dataMap = dataList.get(i);
			//遍历要取的数据库字段列
			for (int j = 0; j < fieldList.length; j++) {
				//创建列并存放数据
				dataRow.createCell(j).setCellValue((dataMap.get(fieldList[i]))!=null?(dataMap.get(fieldList[i])).toString():"");
			}
		}
		workbook.write(os);
		os.flush();
		// 操作结束，关闭文件
		os.close();
		// 关闭workbook
		workbook.close();
	}
	/**
	 * 导出excel
	 * 
	 * @param response
	 *          	响应对象
	 * @param excelName
	 * 			  excel标题
	 * @param headList
	 *            excel的标题备注名称
	 * @param fieldList
	 *            excel的标题字段（与数据中map中键值对应）
	 * @param dataList
	 *            excel数据
	 * @throws Exception
	 */
	public static void createExcel(HttpServletResponse response, String excelName,String[] headList, String[] fieldList,
								   List<Map<String, Object>> dataList) throws Exception {
		try {
			// 创建新的Excel 工作簿
			XSSFWorkbook workbook = new XSSFWorkbook();
			// 告诉浏览器用什么软件可以打开此文件
			response.setHeader("Content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(excelName+".xlsx", "utf-8"));
			OutputStream os = response.getOutputStream();
			try{

				// 在Excel工作簿中建一工作表，其名为缺省值
				XSSFSheet sheet = workbook.createSheet();
				// 在索引0的位置创建行（最顶端的行）
				XSSFRow row = sheet.createRow(0);
				// 设置excel头（第一行）的头名称
				for (int i = 0; i < headList.length; i++) {

					// 在索引0的位置创建单元格（左上端）
					XSSFCell cell = row.createCell(i);
					// 定义单元格为字符串类型
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					// 在单元格中输入一些内容
					cell.setCellValue(headList[i]);
				}
				// ===============================================================
				// 添加数据
				for (int n = 0; n < dataList.size(); n++) {
					// 在索引1的位置创建行（最顶端的行）
					XSSFRow row_value = sheet.createRow(n + 1);
					Map<String, Object> dataMap = dataList.get(n);
					// ===============================================================
					for (int i = 0; i < fieldList.length; i++) {

						// 在索引0的位置创建单元格（左上端）
						XSSFCell cell = row_value.createCell(i);
						// 定义单元格为字符串类型
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
						// 在单元格中输入一些内容
						cell.setCellValue((dataMap.get(fieldList[i]))!=null?(dataMap.get(fieldList[i])).toString():"");
					}
					// ===============================================================
				}
				// 新建一输出文件流
				//FileOutputStream fos = new FileOutputStream(excel_name);
				// 把相应的Excel 工作簿存盘
				workbook.write(os);

			}finally {
				os.flush();
				// 操作结束，关闭文件
				os.close();
				// 关闭workbook
				workbook.close();
			}
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 上传excel
	 * @throws Exception 
	 */
	@SuppressWarnings("null")
	public static <T extends Model<T>> List<T> uploadExcel(UploadFile file,String[] fileName,Class<T> tClass) throws Exception {
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
		List<T> list = new CopyOnWriteArrayList<T>();
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
				T t = tClass.newInstance();
				if (t==null) {
					throw new Exception("对象不能为空");
				}
				for (int j = 0; j < fileName.length; j++) {
					//获得列
					Cell cell = row.getCell(j);
					//判断类型
					Object value = isCellType(cell);
					//给属性赋值
					t.set(fileName[j], value!=""?value:null);
				}
				list.add(t);
			}
		}
		// 关闭工作簿
		workbook.close();
		// 操作完毕删除临时文件
		boolean delete = upfile.delete();
		return list;
	}
	/**
	 * 判断表格值类型相应转换
	 */
	private static Object isCellType(Cell cell) {
		if (cell==null) {
			return null;
		}
		int type = cell.getCellType();
		switch (type) {
		case 0:
			return cell.getNumericCellValue();
		case 1:
			return cell.getStringCellValue();
		case 4:
			return cell.getBooleanCellValue();
		default:
			return null;
		}
	}
	/**
	 * 校验文件格式
	 * 
	 * @param upfile
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
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
}
