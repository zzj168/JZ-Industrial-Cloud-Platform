package cn.jbolt.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.upload.UploadFile;

/**
 * @author zunhui qq:1442182361
 * @version 2019年12月26日 操作数据库表格的工具类
 */
public class ExcelUtils {
	/**
	 * 导出excel
	 * 
	 * @param response  响应对象
	 * @param excelName excel标题
	 * @param headList  excel的标题备注名称
	 * @param fieldList excel的标题字段（与数据中map中键值对应）
	 * @param dataList  excel数据
	 * @throws Exception
	 */
	public static void createExcel(HttpServletResponse response, String excelName, String[] headList,
			String[] fieldList, List<Map<String, Object>> dataList) throws Exception {
		try {
			// 创建新的Excel 工作簿
			XSSFWorkbook workbook = new XSSFWorkbook();
			// 告诉浏览器用什么软件可以打开此文件
			response.setHeader("Content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition",
					"attachment;filename=" + URLEncoder.encode(excelName + ".xlsx", "utf-8"));
			OutputStream os = response.getOutputStream();
			try {

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
						cell.setCellValue(
								(dataMap.get(fieldList[i])) != null ? (dataMap.get(fieldList[i])).toString() : "");
					}
					// ===============================================================
				}
				// 新建一输出文件流
				// FileOutputStream fos = new FileOutputStream(excel_name);
				// 把相应的Excel 工作簿存盘
				workbook.write(os);

			} finally {
				os.flush();
				// 操作结束，关闭文件
				os.close();
				// 关闭workbook
				workbook.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 上传excel
	 * file:文件
	 * fileName:上传文件对应的字段名数组
	 *tClass：表对应的java模型
	 * @throws Exception
	 */
	public static <T extends Model<T>> ExcelBean<T> uploadExcel(UploadFile file, String[] fileName, Class<T> tClass) throws Exception
			{
		// 判断文件格式是否正确
		File upfile = file.getFile();
		if (checkExcel(file, upfile)) {
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
		ExcelBean<T> excelBean = new ExcelBean<T>();
		//记录正确数据
		excelBean.setCorrectList(new CopyOnWriteArrayList<T>());
		//记录错误数据
		excelBean.setErrorList(new ArrayList<Integer>());
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
				// 每一行就是一个对象
				T t = tClass.newInstance();
				Table table = TableMapping.me().getTable(tClass);
				//记录错误
				try {
					for (int j = 0; j < fileName.length; j++) {
						// 获得列
						Cell cell = row.getCell(j);
						// 获取列值
						Class<?> columnType = table.getColumnType(fileName[j]);
						// System.out.println("字段类型 " + columnType);
						String cellValue = getCellValue(cell);
						if (cellValue == null || "".equals(cellValue)) {
							// 给属性赋值
							t.set(fileName[j], null);
						} else {
								//数据库字段类型为int
							if (columnType == Integer.class&&StringUtil.isNumeric(cellValue)) {
								t.set(fileName[j], Integer.parseInt(cellValue));
								//数据库字段类型为varchar
							} else if (columnType == String.class&&!StringUtil.isNumeric(cellValue)) {
								t.set(fileName[j], cellValue);
								//数据库字段类型为timestamp
							} else if (columnType == Timestamp.class) {
								t.set(fileName[j], new SimpleDateFormat("yyyy-MM-dd").parse(cellValue));
								//数据库字段类型为bigdecimal
							} else if (columnType == BigDecimal.class) {
								t.set(fileName[j], new BigDecimal(cellValue));
							}else {
								throw new Exception("错误数据");
							}
							}
						}
						excelBean.getCorrectList().add(t);
					} catch (Exception e) {
						excelBean.getErrorList().add(rowIndex+1);
						System.out.println("错误数据"+t);
					}
				}
			}
		// 关闭工作簿
		workbook.close();
		// 操作完毕删除临时文件
		upfile.delete();
		return excelBean;
	}

	/**
	 * 判断表格值类型相应转换
	 * 
	 * @throws Exception
	 */
	public static String getCellValue(Cell cell) {
		String cellValue = "";
		if (cell == null) {
			return cellValue;
		}
		// 判断数据的类型
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: // 数字
			// short s = cell.getCellStyle().getDataFormat();
			if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = cell.getDateCellValue();
				cellValue = sdf.format(date);
			} else if (cell.getCellStyle().getDataFormat() == 0) {// 处理数值格式
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cellValue = String.valueOf(cell.getRichStringCellValue().getString());
			}
			break;
		case Cell.CELL_TYPE_STRING: // 字符串
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BOOLEAN: // Boolean
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA: // 公式
			cellValue = String.valueOf(cell.getCellFormula());
			break;
		case Cell.CELL_TYPE_BLANK: // 空值
			cellValue = null;
			break;
		case Cell.CELL_TYPE_ERROR: // 故障
			cellValue = "非法字符";
			break;
		default:
			cellValue = "未知类型";
			break;
		}
		return cellValue;
	}

	/**
	 * 校验文件格式是不是excel
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
