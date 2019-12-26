package cn.jbolt.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.DaoTemplate;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbTemplate;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.db.sql.SqlUtil;
import cn.jbolt.common.util.ArrayUtil;

/**
 * JBolt提供Service层的基础底层封装
  * 灵活 易用
 * @ClassName:  BaseService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月14日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public abstract class BaseService<M extends JBoltBaseModel<M>> extends CommonService {
	
	/**
	 *  获取Sql模板 model模式
	 * @param key
	 * @param data
	 * @return
	 */
	public DaoTemplate<M> daoTemplate(String key,Kv data) {
		data.setIfNotBlank("table", table());
		return dao().template(key, data);
	}
	private String tableName;
	/**
	 *  获取表名
	 * @return
	 */
	public String table(){
		if(tableName == null) {
			tableName=dao()._getTableName();
		}
		return tableName;
	}
	/**
	 *  获取Sql模板 Db模式
	 * @param key
	 * @param data
	 * @return
	 */
	public DbTemplate dbTemplate(String key,Kv data) {
		data.setIfNotBlank("table", table());
		return Db.template(key, data);
	}

	/**
	 * 得到下拉列表数据
	 * @param textColumn
	 * @param valueColumn
	 * @param paras
	 * @return
	 */
	public List<Record> getOptionList(String textColumn,String valueColumn,Kv paras){
		Kv conf=Kv.by("value",valueColumn).set("text",textColumn).set("myparas", paras).set("customCompare",false);
		return dbTemplate("common.optionlist", conf).find();
	}
	

	/**
	 * 得到下拉列表数据
	 * @param textColumn
	 * @param valueColumn
	 * @return
	 */
	public List<Record> getOptionList(String textColumn,String valueColumn){
		Kv conf=Kv.by("value",valueColumn).set("text",textColumn).set("customCompare",false);
		return dbTemplate("common.optionlist", conf).find();
	}
	/**
	 * 得到下拉列表数据
	 * @return
	 */
	public List<Record> getOptionList(){
		Kv conf=Kv.by("value","id").set("text","name").set("customCompare",false);
		return dbTemplate("common.optionlist", conf).find();
	}
	/**
	 * 得到所有数据
	 * @return
	 */
	 public List<M> findAll(){
		 return find(selectSql().orderByIdAscIfPgSql());
	 }
	/**
	 * 抽象方法定义 dao 让调用者自己实现
	 * @return
	 */
	protected abstract M dao();
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 自定义参数compare
	 * @param columns
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public List<M> getCommonList(String columns,Kv paras,boolean customCompare){
		return daoTemplate("common.list", 
				Kv.by("customCompare",customCompare)
				.setIfNotNull("myparas", paras)
				.setIfNotBlank("columns",columns))
				.find();
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 可以自定义参数compare 默认=
	 * @param paras
	 * @return
	 */
	public List<M> getCommonList(Kv paras,boolean customCompare){
		return getCommonList("*",paras, customCompare);
	}
	
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 不能自定义参数compare 默认=
	 * @param paras
	 * @return
	 */
	public List<M> getCommonList(Kv paras){
		return getCommonList("*",paras, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 不能自定义参数compare 默认=
	 * @param columns 指定查询的列
	 * @return
	 */
	public List<M> getCommonList(String columns){
		return getCommonList(columns,null,false);
	}
	
	/**
	 * 得到不包含指定列的数据
	 * @param paras
	 * @param withoutColumns
	 * @param customCompare
	 * @return
	 */
	public List<M> getCommonListWithoutColumns(Kv paras,boolean customCompare,String... withoutColumns) {
		return daoTemplate("common.list", 
				Kv.by("customCompare",customCompare)
				.setIfNotNull("myparas", paras)
				.setIfNotBlank("columns",getTableSelectColumnsWithout(withoutColumns)))
				.find();
	}
	/**
	 * 得到表中的字段字符拼接，除了指定的withoutColumns
	 * @param withoutColumns
	 * @return
	 */
	public String getTableSelectColumnsWithout(String... withoutColumns) {
		if(withoutColumns==null||withoutColumns.length==0) {return Sql.KEY_STAR;}
		Set<String> tableColumns=TableMapping.me().getTable(dao().getClass()).getColumnNameSet();
		List<String> selectColumns=new ArrayList<String>(tableColumns);
		for(String col:withoutColumns) {
			selectColumns.remove(col.trim().toLowerCase());
		}
		return selectColumns.size()==0?Sql.KEY_STAR:CollectionUtil.join(selectColumns, ",");
	}
	/**
	 * 得到不包含指定列的数据
	 * @param withoutColumns
	 * @return
	 */
	public List<M> getCommonListWithoutColumns(String withoutColumns) {
		return getCommonListWithoutColumns(null, false,withoutColumns);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 不能自定义参数compare 默认=
	 * @param columns 指定查询的列
	 * @param paras
	 * @return
	 */
	public List<M> getCommonList(String columns,Kv paras){
		return getCommonList(columns,paras,false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 默认正序 
	 * 不能自定义参数compare 默认=
	 * @param paras
	 * @param orderColums
	 * @return
	 */
	public List<M> getCommonList(Kv paras,String orderColums){
		int count=StrUtil.count(orderColums, ",");
		String orderTypes="";
		for(int i=0;i<=count;i++){
			if(i==0){
				orderTypes="asc";
			}else{
				orderTypes=orderTypes+","+"asc";
			}
		}
		return getCommonList("*",paras, orderColums, orderTypes, false);
	}
	
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 不能自定义参数compare 默认=
	 * @param orderColumns
	 * @param orderTypes
	 * @return
	 */
	public List<M> getCommonList(String orderColumns,String orderTypes){
		return getCommonList("*",null, orderColumns, orderTypes, false);
	}
	
	
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 不能自定义参数compare 默认=
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @return
	 */
	public List<M> getCommonList(Kv paras,String orderColumns,String orderTypes){
		return getCommonList("*",paras, orderColumns, orderTypes, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 不能自定义参数compare 默认=
	 * @param columns
	 * @param paras
	 * @param sort
	 * @param orderType
	 * @return
	 */
	public List<M> getCommonList(String columns,Kv paras,String orderColumns,String orderTypes){
		return getCommonList(columns,paras, orderColumns, orderTypes, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 自定义参数compare
	 * @param paras
	 * @param orderColumn
	 * @param orderType
	 * @param customCompare
	 * @return
	 */
	public List<M> getCommonList(Kv paras,String orderColumns,String orderTypes,boolean customCompare){
		return getCommonList("*", paras, orderColumns, orderTypes, customCompare);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 自定义参数compare
	 * @param columns
	 * @param paras
	 * @param orderColumn
	 * @param orderType
	 * @param customCompare
	 * @return
	 */
	public List<M> getCommonList(String columns,Kv paras,String orderColumns,String orderTypes,boolean customCompare){
		Kv conf=Kv.by("myparas", paras).set("customCompare",customCompare);
		if(isOk(columns)){
			conf.set("columns",columns);
		}
		if(isOk(orderColumns)){
			conf.set("orderColumns",ArrayUtil.from(orderColumns, ","));
		}
		if(isOk(orderTypes)){
			conf.set("orderTypes",ArrayUtil.from(orderTypes, ","));
		}
		return daoTemplate("common.list",conf).find();
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 自定义参数compare
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @param or
	 * @return
	 */
	public Page<M> paginate(String columns,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare,boolean or){
		Kv conf=Kv.by("myparas", paras).set("customCompare",customCompare);
		conf.set("or",or);
		conf.setIfNotBlank("columns",columns);
		if(isOk(orderColumns)){
			conf.set("orderColumns",ArrayUtil.from(orderColumns, ","));
		}
		if(isOk(orderTypes)){
			conf.set("orderTypes",ArrayUtil.from(orderTypes, ","));
		}
		return daoTemplate("common.list",conf).paginate(pageNumber, pageSize);
	}
	
	/**
	 *根据关键词分页查询 
	 * @param orderColumn
	 * @param orderType
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param matchColumns
	 * @return
	 */
	public Page<M> paginateByKeywords(String orderColumn,String orderType,int pageNumber,int pageSize,String keywords,String matchColumns){
		return paginateByKeywords(orderColumn,orderType,pageNumber,pageSize,keywords,matchColumns,null);
	}
	/**
	 *根据关键词分页查询 
	 * 默认倒序
	 * @param orderColumn
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param matchColumns
	 * @return
	 */
	public Page<M> paginateByKeywords(String orderColumn,int pageNumber,int pageSize,String keywords,String matchColumns){
		return paginateByKeywords(orderColumn,"desc",pageNumber,pageSize,keywords,matchColumns,null);
	}
	/**
	  *根据关键词分页查询 
	  * 默认倒序
	 * @param orderColumn
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param matchColumns
	 * @param otherParas
	 * @return
	 */
	public Page<M> paginateByKeywords(String orderColumn,int pageNumber,int pageSize,String keywords,String matchColumns,Kv otherParas){
		return paginateByKeywords(orderColumn,"desc",pageNumber,pageSize,keywords,matchColumns,otherParas);
	}
	/**
	   * 根据关键词分页查询 
	 * @param orderColumn
	 * @param orderType
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param matchColumns
	 * @param otherParas
	 * @return
	 */
	public Page<M> paginateByKeywords(String orderColumn,String orderType,int pageNumber,int pageSize,String keywords,String matchColumns,Kv otherParas){
		return paginateByKeywords(Sql.KEY_STAR,orderColumn,orderType,pageNumber,pageSize,keywords,matchColumns,otherParas);
	}
	
	
	/**
	 * 根据关键词匹配分页查询
	 * @param returnColumns
	 * @param orderColumn
	 * @param orderType
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param matchColumns
	 * @param otherParas
	 * @return
	 */
	public Page<M> paginateByKeywords(String returnColumns,String orderColumn,String orderType,int pageNumber,int pageSize,String keywords,String matchColumns,Kv otherParas){
		if(notOk(matchColumns)) {
			return EMPTY_PAGE;
		}
		Sql sql=selectSql().select(returnColumns).page(pageNumber, pageSize);
		Sql totalCountSql=selectSql().count();
		if(isOk(orderColumn)&&isOk(orderType)) {
			sql.orderBy(orderColumn,orderType.equals("desc"));
		}else {
			sql.orderByIdAscIfPgSql();
		}
		if(otherParas!=null&&otherParas.size()>0) {
			Set<String> keys=otherParas.keySet();
			sql.bracketLeft();
			totalCountSql.bracketLeft();
			for(String key:keys){
				sql.eq(key, otherParas.getAs(key));
				totalCountSql.eq(key, otherParas.getAs(key));
	        }
			sql.bracketRight();
			totalCountSql.bracketRight();
		}
		//如果没有给keywords字段
		if(notOk(keywords)) {
			return processPaginate(sql,totalCountSql,pageNumber,pageSize);
		}
		//如果给了Keywords
		String[] columns=ArrayUtil.from(matchColumns, ",");
		if(columns==null||columns.length==0) {
			return EMPTY_PAGE;
		}
		int size=columns.length;
		sql.bracketLeft();
		totalCountSql.bracketLeft();
		keywords=keywords.trim();
		for(int i=0;i<size;i++) {
			sql.like(columns[i],keywords);
			totalCountSql.like(columns[i],keywords);
			if(i<size-1) {
				sql.or();
				totalCountSql.or();
			}
		}
		sql.bracketRight();
		totalCountSql.bracketRight();
		return processPaginate(sql, totalCountSql, pageNumber, pageSize);
	}
	
	private Page<M> processPaginate(Sql sql, Sql totalCountSql, int pageNumber, int pageSize) {
		Integer totalRow=queryInt(totalCountSql);
		if(totalRow==null||totalRow==0) {
			return EMPTY_PAGE;
		}
		List<M> list=find(sql);
		int totalPage=(totalRow/pageSize)+(totalRow%pageSize>0?1:0);
		return new Page<M>(list, pageNumber, pageSize, totalPage, totalRow);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(String columns,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate(columns, paras, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @param or
	 * @return
	 */
	public Page<M> paginate(Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare,boolean or){
		return paginate(Sql.KEY_STAR, paras, orderColumns, orderTypes, pageNumber, pageSize, customCompare,or);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param paras
	 * @param orderColumns
	 * @param orderType
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare){
		return paginate("*", paras, orderColumns, orderTypes, pageNumber, pageSize, customCompare,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate("*", paras, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param orderColumns
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(String orderColumns,int pageNumber, int pageSize){
		return paginate("*", null, orderColumns, "asc", pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate("*", null, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法 返回指定列
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param columns
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(String columns,String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate(columns, null, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不尅一可以排序
	 * 条件自定义 customCompare
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(String columns,Kv paras,int pageNumber, int pageSize,boolean customCompare){
		Kv conf=Kv.by("myparas", paras).set("customCompare",customCompare);
		if(isOk(columns)){
			conf.set("columns",columns);
		}
		return daoTemplate("common.list",conf).paginate(pageNumber, pageSize);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 条件自定义 customCompare
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(Kv paras,int pageNumber, int pageSize,boolean customCompare){
		return paginate("*", paras, pageNumber, pageSize, customCompare);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(Kv paras,int pageNumber, int pageSize){
		return paginate("*", paras, pageNumber, pageSize, false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(Kv paras,int pageSize,boolean customCompare){
		return paginate("*", paras, 1, pageSize, customCompare);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(int pageNumber,int pageSize){
		return paginate("*", null, pageNumber, pageSize, false);
	}
	/**
	 * 按照sql模板分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginateBySqlTemplate(String key,Kv data,int pageNumber,int pageSize){
		return daoTemplate(key, data).paginate(pageNumber, pageSize);
	}
	/**
	 * 按照sql模板分页查询 返回Record
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> paginateByDbSqlTemplate(String key,Kv data,int pageNumber,int pageSize){
		return dbTemplate(key, data).paginate(pageNumber, pageSize);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param paras
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(Kv paras,int pageSize){
		return paginate("*", paras, 1, pageSize, false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(int pageSize){
		return paginate("*", null, 1, pageSize, false);
	}
	
	
	/**
	 * 通用根据ID删除数据
	 * @param id
	 * @return
	 */
	public Ret deleteById(Object id){
		return deleteById(id, false);
	}
	
	/**
	 * 通用根据ID删除数据 需要先检查是否被其他地方使用
	 * @param id
	 * @param checkInUse
	 * @return
	 */
	public Ret deleteById(Object id,boolean checkInUse){
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		if(notOk(id)){
			return fail(Msg.PARAM_ERROR);
		}
		M m=findById(id);
		if(m==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		if(checkInUse){
			String msg=checkInUse(m);
			if(isOk(msg)){return fail(msg);}
		}
		
		boolean success=m.deleteById(id);
		return success?success(m,Msg.SUCCESS):FAIL;
	}
	/**
	 * 检测数据是否被其它数据外键引用
	 * @param model
	 * @return
	 */
	public String checkInUse(M model){
		return null;
	}
	/**
	 * 检测数据是否字段是否可以执行切换true false
	 * @param kv
	 * @param model
	 * @param column
	 * @return
	 */
	public String checkCanToggle(Kv kv,M model,String column){
		return null;
	}

	/**
	 * 额外需要处理toggle操作
	 * @param kv
	 * @param model
	 * @param column
	 * @return
	 */
	public String toggleExtra(Kv kv,M model,String column){
		return null;
	}
	
	
	/**
	 * 判断name是否存在相同数据 排除指定ID
	 * @param name
	 * @param id
	 * @return
	 */
	public boolean existsName(String name,Object id) {
		name = name.trim();
		Sql sql=selectSql().selectId().eqQM("name").noteqQM("id").first();
		Object existId = queryColumn(sql, name, id);
		return isOk(existId);
	}

	/**
	 * 判断是否存在
	 * @param columnName
	 * @param value
	 * @param id
	 * @return
	 */
	public boolean exists(String columnName,Object value,Object id) {
		Sql sql=selectSql().selectId().eqQM(columnName).idNoteqQM().first();
		Object existId = queryColumn(sql, value,id);
		return isOk(existId);
	}
	/**
	 * 判断是否存在
	 * @param columnName
	 * @param value
	 * @return
	 */
	public boolean exists(String columnName,Object value) {
		return exists(columnName, value, -1);
	}
	/**
	 * 判断字典名是否存在
	 * @param name
	 * @return
	 */
	public boolean existsName(String name) {
		return existsName(name, -1);
	}
	/**
	 * 根据ID获得一条数据
	 * @param id
	 * @return
	 */
	public M findById(Object id) {
		if(notOk(id)){return null;}
		return dao().findById(id);
	}
	/**
	 * 根据ID获得一条数据 去掉指定的列
	 * @param id
	 * @param withoutColumns 不需要的列
	 * @return
	 */
	public M findByIdWithoutColumns(Object id,String... withoutColumns) {
		if(notOk(id)){return null;}
		return dao().findByIdLoadColumns(id, getTableSelectColumnsWithout(withoutColumns));
	}
	/**
	 * 得到符合条件的第一个
	 * @param paras
	 * @return
	 */
	public M findFirst(Kv paras) {
		return findFirst(paras,false);
	}
	/**
	 * 得到符合条件的第一个
	 * @return
	 */
	public M findFirst() {
		return findFirst(null,false);
	}
	/**
	 * 得到符合条件的第一个
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public M findFirst(Kv paras,boolean customCompare) {
		return daoTemplate("common.first", Kv.by("customCompare",customCompare).setIfNotNull("myparas", paras)).findFirst();
	}
	/**
	 * 随机得到符合条件的第一个
	 * @param paras
	 * @return
	 */
	public M getRandomOne(Kv paras) {
		return getRandomOne(paras, false);
	}
	/**
	 * 随机得到符合条件的第一个
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public M getRandomOne(Kv paras,boolean customCompare) {
		Kv conf=Kv.by("customCompare",customCompare).setIfNotNull("myparas", paras);
		conf.set("orderColumns",new String[] {"rand()"});
		conf.set("orderTypes",new String[] {"asc"});
		return daoTemplate("common.firstrand", conf).findFirst();
	}
	/**
	 * 根据条件删除数据
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public Ret deleteBy(Kv paras,boolean customCompare) {
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		dbTemplate("common.delete", Kv.by("customCompare",customCompare).setIfNotNull("myparas", paras)).delete();
		return SUCCESS;
	}
	/**
	 * 根据条件删除数据
	 * @param paras
	 * @return
	 */
	public Ret deleteBy(Kv paras) {
		return deleteBy(paras, false);
	}


	/**
	 * 切换Boolean类型字段
	 * @param id 需要切换的数据ID
	 * @param columns 需要切换的字段列表
	 * @return
	 */
	public Ret toggleBoolean(Object id,String... columns) {
		return toggleBoolean(null, id, columns);
	}
	/**
	 * 切换Boolean类型字段值
	 * @param kv 额外传入的参数 用于 toggleExtra里用
	 * @param id 需要切换的数据ID
	 * @param columns 需要切换的字段列表
	 * @return
	 */
	public Ret toggleBoolean(Kv kv,Object id,String... columns) {
		if(notOk(id)){
			return fail(Msg.PARAM_ERROR);
		}
		M model=findById(id);
		if(model==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		
		Table table =TableMapping.me().getTable(dao().getClass());
		if (table != null) {
			for(String column:columns){
				if(!table.hasColumnLabel(column)){
					throw new ActiveRecordException("The attribute name does not exist: \"" + column + "\"");
				}
			}
			Boolean value;
			for(String column:columns){
				String msg = checkCanToggle(kv,model,column);
				if(StrKit.notBlank(msg)){
					return fail(msg);
				}
				value=model.getBoolean(column);
				model.set(column, SqlUtil.boolToInt(value==null?true:!value));
				//处理完指定这个字段 还需要额外处理什么？
				msg=toggleExtra(kv,model,column);
				if(StrKit.notBlank(msg)){
					return fail(msg);
				}
			}
			boolean success=model.update();
			return success?success(model,Msg.SUCCESS):FAIL;
		}
		
		return FAIL;
	}
	
	/**
	 * 常用的得到列表数据数量
	 * 自定义参数compare
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public int getCount(Kv paras,boolean customCompare){
		return dbTemplate("common.count",Kv.by("customCompare",customCompare).setIfNotNull("myparas", paras)).queryInt();
	}
	/**
	 * 常用的得到列表数据数量
	 * @param paras
	 * @return
	 */
	public int getCount(Kv paras){
		return getCount(paras, false);
	}
	

	/**
	 * 得到新数据的排序Rank值 默认从1开始 不带任何查询条件
	 * @return
	 */
	public int getNextSortRank(){
		return getNextSortRank(null, false);
	}
	/**
	 * 得到新数据的排序Rank值 从0开始 不带任何查询条件
	 * @return
	 */
	public int getNextRankFromZero(){
		return getNextSortRank(null, true);
	}
	/**
	 * 得到新数据的排序Rank值 从0开始 带查询条件
	 * @param fromZero
	 * @return
	 */
	public int getNextRankFromZero(Kv paras){
		return getNextSortRank(paras, true);
	}
	/**
	 * 得到新数据的排序Rank值 自带简单条件查询默认从1开始
	 * @param paras
	 * @return
	 */
	public int getNextSortRank(Kv paras){
		return getNextSortRank(paras, false);
	}
	/**
	 * 得到新数据的排序Rank值 自带简单条件查询 可以自定义是否从零开始
	 * @param paras
	 * @param fromZero
	 * @return
	 */
	public int getNextSortRank(Kv paras,boolean  fromZero){
		int count=getCount(paras);
		if(fromZero){
			return count;
		}
		return count+1;
	}
	

	/**
	 * 得到新数据的排序Rank值 自带简单条件查询 可以自定义是否从零开始
	 * 条件可定制版
	 * @param kv
	 * @param fromZero
	 * @return
	 */
	public int getNextSortRank(Kv paras,Boolean customCompare,boolean  fromZero){
		int count=getCount(paras, customCompare);
		if(fromZero){
			return count;
		}
		return count+1;
	}
	
	/**
	 * 常用的得到列表数据数量
	 * @return
	 */
	public int getCount(){
		return getCount(null, false);
	}
	
	/**
	 * 更新同级删除数据之后数据的排序
	 * @param sortRank
	 */
	protected void updateSortRankAfterDelete(Integer sortRank) {
		updateSortRankAfterDelete(table(),sortRank);
	}
	
	/**
	 * 更新同级删除数据之后数据的排序
	 * @param params
	 * @param sortRank
	 */
	protected void updateSortRankAfterDelete(Kv params, Integer sortRank) {
		updateSortRankAfterDelete(table(), params, sortRank);
	}
	/**
	 * 删除关联子数据
	 * @param pid
	 * @return
	 */
	protected Ret deleteByPid(Object pid) {
		if(dao().isAutoCache()) {
			List<M> ms=getListByPid(pid);
			for(M m:ms) {
				m.deleteIdCache();
				m.deleteKeyCache();
			}
		}
		return deleteBy(Kv.by("pid", pid));
	}
	/**
	 * 根据PID获取子数据
	 * @param pid
	 * @return
	 */
	public List<M> getListByPid(Object pid){
		return getCommonList(Kv.by("pid", pid));
	}
	/**
	 * 检测判断表中是否存在一个指定字段是null的数据
	 * @param columnName
	 * @return
	 */
	public boolean existsColumnIsNull(String columnName) {
		columnName = columnName.toLowerCase().trim();
		Sql sql=selectSql().selectId().isNull(columnName).first();
		Object existId = queryColumn(sql);
		return isOk(existId);
	}
	/**
	 * 关键词查询指定返回个数的数据 默认返回所有字段
	 * 关键词为空的时候 返回空list
	 * @param keywords 关键词
	 * @param limitCount  返回个数
	 * @param matchColumns 关键词去匹配哪些字段 可以一个 可以多个 逗号隔开
	 * @return
	 */
	public List<M> getAutocompleteList(String keywords, Integer limitCount,String matchColumns) {
		return getAutocompleteList(keywords, limitCount, false, Sql.KEY_STAR, matchColumns);
	}
	/**
	 * 关键词查询指定返回个数的数据 默认返回所有字段
	 * @param keywords 关键词
	 * @param limitCount  返回个数
	 * @param always  当关键词空的时候 是否需要查询所有数据返回指定个数
	 * @param matchColumns 关键词去匹配哪些字段 可以一个 可以多个 逗号隔开
	 * @return
	 */
	public List<M> getAutocompleteList(String keywords, Integer limitCount,Boolean always,String matchColumns) {
		return getAutocompleteList(keywords, limitCount, always, Sql.KEY_STAR, matchColumns);
	}
	/**
	 * 关键词查询指定返回个数的数据
	 * @param keywords 关键词
	 * @param limitCount  返回个数
	 * @param always  当关键词空的时候 是否需要查询所有数据返回指定个数
	 * @param returnColumns 查询字段 可以是* 也可以是 id,name这种逗号隔开
	 * @param matchColumns 关键词去匹配哪些字段 可以一个 可以多个 逗号隔开
	 * @return
	 */
	public List<M> getAutocompleteList(String keywords, Integer limitCount,Boolean always,String returnColumns,String matchColumns) {
		if((notOk(keywords)&&(always==null||always==false))||notOk(matchColumns)) {
			return Collections.emptyList();
		}
		Sql sql=selectSql().select(returnColumns).firstPage(limitCount).orderByIdAscIfPgSql();
		//如果关键词为空 默认是返回空数据
			//但是如果指定了关键词是空 就按照指定个数返回数据的话
		if(notOk(keywords)&&always!=null&&always==true) {
			return find(sql);
		}
		
		String[] columns=ArrayUtil.from(matchColumns, ",");
		if(columns==null||columns.length==0) {
			return Collections.emptyList();
		}
		int size=columns.length;
		sql.bracketLeft();
		keywords=keywords.trim();
		for(int i=0;i<size;i++) {
			sql.like(columns[i],keywords);
			if(i<size-1) {
				sql.or();
			}
		}
		sql.bracketRight();
		return find(sql);
	}
	/**
	 * 快速获取sql 默认是select sql
	 * @return
	 */
	protected Sql selectSql() {
		return Sql.me(MainConfig.DB_TYPE).select().from(table());
	}
	/**
	 * 快速获取update sql
	 * @return
	 */
	protected Sql updateSql() {
		return Sql.me(MainConfig.DB_TYPE).update(table());
	}
	/**
	 * 快速获取delete sql
	 * @return
	 */
	protected Sql deleteSql() {
		return Sql.me(MainConfig.DB_TYPE).delete().from(table());
	}
	
	
	/**
	 * 执行查询
	 * @param sql
	 * @return
	 */
	protected List<M> find(Sql sql){
		if(sql.isPrepared()) {
			return find(sql.toSql(),sql.getWhereValues());
		}
		return find(sql.toSql());
	}
	/**
	 * 执行查询
	 * @param sql
	 * @param paras
	 * @return
	 */
	protected List<M> find(Sql sql,Object... paras){
		return find(sql.toSql(),paras);
	}
	
	/**
	 * 执行查询
	 * @param sql
	 * @return
	 */
	protected List<M> find(String sql){
		return dao().find(sql);
	}
	/**
	 * 执行查询
	 * @param sql
	 * @param paras
	 * @return
	 */
	protected List<M> find(String sql,Object... paras){
		return dao().find(sql, paras);
	}
	/**
	 * 通过关键词查询数据List
	 * @param keywords
	 * @param orderColumn
	 * @param orderType
	 * @param matchColumns
	 * @return
	 */
	public List<M> getCommonListByKeywords(String keywords,String orderColumn,String orderType,String matchColumns) {
		return getCommonListByKeywords(keywords, Sql.KEY_STAR, orderColumn, orderType, matchColumns, null);
	}
	/**
	 * 通过关键词查询数据LIst
	 * @param keywords
	 * @param orderColumn
	 * @param matchColumns
	 * @return
	 */
	public List<M> getCommonListByKeywords(String keywords,String orderColumn,String matchColumns) {
		return getCommonListByKeywords(keywords,orderColumn, "asc", matchColumns);
	}
	/**
	 *通过关键词查询数据List
	 * @param keywords
	 * @param orderColumn
	 * @param orderType
	 * @param matchColumns
	 * @param otherParas
	 * @return
	 */
	public List<M> getCommonListByKeywords(String keywords,String orderColumn,String orderType,String matchColumns,Kv otherParas) {
		return getCommonListByKeywords(keywords, Sql.KEY_STAR, orderColumn, orderType, matchColumns, otherParas);
	}
	/**
	 * 通过关键词查询数据List
	 * @param keywords
	 * @param returnColumns
	 * @param orderColumn
	 * @param orderType
	 * @param matchColumns
	 * @return
	 */
	public List<M> getCommonListByKeywords(String keywords,String returnColumns,String orderColumn,String orderType,String matchColumns) {
		return getCommonListByKeywords(keywords, returnColumns, orderColumn, orderType, matchColumns, null);
	}
	/**
	 *	通过关键词查询数据List底层封装 
	 * @param keywords
	 * @param returnColumns
	 * @param orderColumn
	 * @param orderType
	 * @param matchColumns
	 * @param otherParas
	 * @return
	 */
	public List<M> getCommonListByKeywords(String keywords,String returnColumns,String orderColumn,String orderType,String matchColumns,Kv otherParas) {
		if(notOk(matchColumns)) {
			return Collections.emptyList();
		}
		Sql sql=selectSql().select(returnColumns);
		if(isOk(orderColumn)&&isOk(orderType)) {
			sql.orderBy(orderColumn,orderType.equals("desc"));
		}else {
			sql.orderByIdAscIfPgSql();
		}
		if(otherParas!=null&&otherParas.size()>0) {
			Set<String> keys=otherParas.keySet();
			sql.bracketLeft();
			for(String key:keys){
				sql.eq(key, otherParas.getAs(key));
	        }
			sql.bracketRight();
		}
		//如果没有给keywords字段
		if(notOk(keywords)) {
			return find(sql);
		}
		//如果给了Keywords
		String[] columns=ArrayUtil.from(matchColumns, ",");
		if(columns==null||columns.length==0) {
			return Collections.emptyList();
		}
		int size=columns.length;
		sql.bracketLeft();
		keywords=keywords.trim();
		for(int i=0;i<size;i++) {
			sql.like(columns[i],keywords);
			if(i<size-1) {
				sql.or();
			}
		}
		sql.bracketRight();
		
		return find(sql);
		
	}
	/**
	 * 根据主键删除缓存
	 * @param id
	 */
	public void deleteCacheById(Object... ids) {
		dao().deleteCacheById(ids);
	}
	/**
	 * 根据指定列值删除缓存
	 * @param columnValue
	 */
	public void deleteCacheByKey(String columnValue) {
		dao().deleteCacheByKey(columnValue,null);
	}
	/**
	 * 根据指定列值删除缓存
	 * @param columnValue
	 * @param bindColumnValue
	 */
	public void deleteCacheByKey(String columnValue,Object bindColumnValue) {
		dao().deleteCacheByKey(columnValue,bindColumnValue);
	}
	/**
	 * 根据指定列值获得缓存
	 * @param columnValue
	 * @return
	 */
	public M getCacheByKey(String columnValue) {
		return dao().loadCacheByKey(columnValue);
	}
	/**
	 * 根据指定列值和绑定列值获得缓存
	 * @param columnValue
	 * @param bindColumnValue
	 * @return
	 */
	public M getCacheByKey(String columnValue,Object bindColumnValue) {
		return dao().loadCacheByKey(columnValue,bindColumnValue);
	}
 
}
