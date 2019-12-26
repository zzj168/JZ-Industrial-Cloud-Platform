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
	 * @throws IOException 
	 * @throws IORuntimeException 
	 * @throws Exception 
	 */
	public void export() throws IORuntimeException, IOException{
		//表名
//		String excelName = "bom_title";
//		//表头
//		String[] headList = new String[] {"编号","材质","类型"};
//		String[] fieldList = new String[] {"id","materialName","materialType"};
//		List<Map<String,Object>> dataList = new ArrayList<>();
//		List<Bom> list = service.dao().findAll();
//		for (Bom bom : list) {
//			Map<String, Object> m = new HashMap<>();
//			m.put("id",bom.getId());
//			m.put("materialName",bom.getMaterialName());
//			m.put("materialType", bom.getMaterialType());
//			dataList.add(m);
//		}
//		try {
//			ExcelUtils.createExcel(this.getResponse(), excelName, headList, fieldList, dataList);
//		} catch (Exception e) {
//			set("msg", "数据为空！");
//			e.printStackTrace();
//		}
		List<Bom> rows = service.findAll();
		System.out.println(rows);
		HttpServletResponse response = getResponse();
		ServletOutputStream out = response.getOutputStream();
		// 通过工具类创建writer，默认创建xls格式
		ExcelWriter writer = ExcelUtil.getWriter();
		// 一次性写出内容，使用默认样式，强制输出标题
		writer.write(rows);
		//out为OutputStream，需要写出到的目标流
		//response为HttpServletResponse对象
		response.setContentType("application/vnd.ms-excel;charset=utf-8"); 
		//test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
		response.setHeader("Content-Disposition","attachment;filename=test.xls"); 
		writer.flush(out);
		// 关闭writer，释放内存
		writer.close();
		//此处记得关闭输出Servlet流
		IoUtil.close(out);
	}
	/**
	 * 上传表格
	 * @throws Exception 
	 */
	public void uploadFile() throws Exception{
		UploadFile file=getFile("file","bom");
		if(file==null) {
			renderJsonFail("上传失败");
			return;
		}
		//System.out.println("文件路径"+file.getUploadPath()+"/"+file.getFileName());
		ExcelReader reader = ExcelUtil.getReader(file.getUploadPath()+"/"+file.getFileName());
		List<Map<String, Object>> list = reader.readAll();
		service.saveBomExcel(list);
		//System.out.println("excel集合"+list);
		for (Map<String, Object> map : list) {
			Bom mapToBean = BeanUtil.mapToBean(map, Bom.class, true);
			Bom bom = new Bom();
			bom.setMaterialName(map.get("materialName")+"");
			bom.setMaterialType(Integer.parseInt(map.get("materialType").toString()));
			bom.setMaterialModel(Integer.parseInt(map.get("materialModel").toString()));
			bom.setProductionProcesses(Integer.parseInt(map.get("productionProcesses").toString()));
			bom.save();
		}
		Kv kv=Kv.create();
		kv.set("fileUrl", JFinal.me().getConstants().getBaseUploadPath()+"/bom/"+file.getFileName());
		kv.set("fileName",file.getFileName());
		renderJsonData(kv);
	}
}
