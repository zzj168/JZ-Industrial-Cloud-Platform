package cn.jbolt.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbTemplate;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.util.ArrayUtil;

/**
 * 采用Db+record模式实现
 * @ClassName:  BaseRecordService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年7月20日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public abstract class BaseRecordService<M extends Model<M>> extends CommonService{
	protected abstract Class<M> mainTableModelClass();
	private String mainTableName;
	/**
	 *  获取表名
	 * @return
	 */
	private String getMainTableName(){
		if(mainTableName==null){
			mainTableName=TableMapping.me().getTable(mainTableModelClass()).getName();
		}
		return mainTableName;
	}
	 
	/**
	 *  获取Sql模板 Db模式
	 * @param _id 多表模式指定后缀ID
	 * @param key
	 * @param data
	 * @return
	 */
	public DbTemplate dbTemplate(Object _id,String key,Kv data) {
		data.setIfNotBlank("table", table(_id));
		return Db.template(key, data);
	}

	/**
	 * 得到下拉列表数据
	 * @param _id 多表模式指定后缀ID
	 * @param textColumn
	 * @param valueColumn
	 * @param paras
	 * @return
	 */
	public List<Record> getOptionList(Object _id,String textColumn,String valueColumn,Kv paras){
		Kv conf=Kv.by("value",valueColumn).set("text",textColumn).set("myparas", paras).set("customCompare",false);
		return dbTemplate(_id,"common.optionlist", conf).find();
	}
	

	/**
	 * 得到下拉列表数据
	 * @param _id 多表模式指定后缀ID
	 * @param textColumn
	 * @param valueColumn
	 * @return
	 */
	public List<Record> getOptionList(Object _id,String textColumn,String valueColumn){
		Kv conf=Kv.by("value",valueColumn).set("text",textColumn).set("customCompare",false);
		return dbTemplate(_id,"common.optionlist", conf).find();
	}
	/**
	 * 得到下拉列表数据
	 * @param _id 多表模式指定后缀ID
	 * @return
	 */
	public List<Record> getOptionList(Object _id){
		Kv conf=Kv.by("value","id").set("text","name").set("customCompare",false);
		return dbTemplate(_id,"common.optionlist", conf).find();
	}
	 
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 自定义参数compare
	 * @param _id 多表模式指定后缀ID
	 * @param columns
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public List<Record> getCommonList(Object _id,String columns,Kv paras,boolean customCompare){
		return dbTemplate(_id,"common.list", 
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
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public List<Record> getCommonList(Object _id,Kv paras,boolean customCompare){
		return getCommonList(_id,"*",paras, customCompare);
	}
	
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 不能自定义参数compare 默认=
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @return
	 */
	public List<Record> getCommonList(Object _id,Kv paras){
		return getCommonList(_id,"*",paras, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 不能自定义参数compare 默认=
	 * @param _id 多表模式指定后缀ID
	 * @param columns 指定查询的列
	 * @param paras
	 * @return
	 */
	public List<Record> getCommonList(Object _id,String columns,Kv paras){
		return getCommonList(_id,columns,paras,false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 默认正序 
	 * 不能自定义参数compare 默认=
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param orderColums
	 * @return
	 */
	public List<Record> getCommonList(Object _id,Kv paras,String orderColums){
		int count=StrUtil.count(orderColums, ",");
		String orderTypes="";
		for(int i=0;i<=count;i++){
			if(i==0){
				orderTypes="asc";
			}else{
				orderTypes=orderTypes+","+"asc";
			}
		}
		return getCommonList(_id,"*",paras, orderColums, orderTypes, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 不能自定义参数compare 默认=
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @return
	 */
	public List<Record> getCommonList(Object _id,Kv paras,String orderColumns,String orderTypes){
		return getCommonList(_id,"*",paras, orderColumns, orderTypes, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 不能自定义参数compare 默认=
	 * @param _id 多表模式指定后缀ID
	 * @param columns
	 * @param paras
	 * @param sort
	 * @param orderType
	 * @return
	 */
	public List<Record> getCommonList(Object _id,String columns,Kv paras,String orderColumns,String orderTypes){
		return getCommonList(_id,columns,paras, orderColumns, orderTypes, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 自定义参数compare
	 * @param _id 多表模式指定后缀ID
	 * @param columns
	 * @param paras
	 * @param orderColumn
	 * @param orderType
	 * @param customCompare
	 * @return
	 */
	public List<Record> getCommonList(Object _id,String columns,Kv paras,String orderColumns,String orderTypes,boolean customCompare){
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
		return dbTemplate(_id,"common.list",conf).find();
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 自定义参数compare
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
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
	public Page<Record> paginate(Object _id,String columns,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare,boolean or){
		Kv conf=Kv.by("myparas", paras).set("customCompare",customCompare);
		conf.set("or",or);
		conf.setIfNotBlank("columns",columns);
		if(isOk(orderColumns)){
			conf.set("orderColumns",ArrayUtil.from(orderColumns, ","));
		}
		if(isOk(orderTypes)){
			conf.set("orderTypes",ArrayUtil.from(orderTypes, ","));
		}
		return dbTemplate(_id,"common.list",conf).paginate(pageNumber, pageSize);
	}
	
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param columns
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> paginate(Object _id,String columns,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate(_id,columns, paras, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @param or
	 * @return
	 */
	public Page<Record> paginate(Object _id,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare,boolean or){
		return paginate(_id,"*", paras, orderColumns, orderTypes, pageNumber, pageSize, customCompare,or);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param orderColumns
	 * @param orderType
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<Record> paginate(Object _id,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare){
		return paginate(_id,"*", paras, orderColumns, orderTypes, pageNumber, pageSize, customCompare,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> paginate(Object _id,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate(_id,"*", paras, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> paginate(Object _id,String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate(_id,"*", null, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不尅一可以排序
	 * 条件自定义 customCompare
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<Record> paginate(Object _id,String columns,Kv paras,int pageNumber, int pageSize,boolean customCompare){
		Kv conf=Kv.by("myparas", paras).set("customCompare",customCompare);
		if(isOk(columns)){
			conf.set("columns",columns);
		}
		return dbTemplate(_id,"common.list",conf).paginate(pageNumber, pageSize);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 条件自定义 customCompare
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<Record> paginate(Object _id,Kv paras,int pageNumber, int pageSize,boolean customCompare){
		return paginate(_id,"*", paras, pageNumber, pageSize, customCompare);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<Record> paginate(Object _id,Kv paras,int pageNumber, int pageSize){
		return paginate(_id,"*", paras, pageNumber, pageSize, false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<Record> paginate(Object _id,Kv paras,int pageSize,boolean customCompare){
		return paginate(_id,"*", paras, 1, pageSize, customCompare);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> paginate(Object _id,int pageNumber,int pageSize){
		return paginate(_id,"*", null, pageNumber, pageSize, false);
	}
	/**
	 * 按照sql模板分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param key
	 * @param data
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> paginateBySqlTemplate(Object _id,String key,Kv data,int pageNumber,int pageSize){
		return dbTemplate(_id,key, data).paginate(pageNumber, pageSize);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param pageSize
	 * @return
	 */
	public Page<Record> paginate(Object _id,Kv paras,int pageSize){
		return paginate(_id,"*", paras, 1, pageSize, false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param _id 多表模式指定后缀ID
	 * @param pageSize
	 * @return
	 */
	public Page<Record> paginate(Object _id,int pageSize){
		return paginate(_id,"*", null, 1, pageSize, false);
	}
	
	
	/**
	 * 通用根据ID删除数据
	 * @param _id 多表模式指定后缀ID
	 * @param id
	 * @return
	 */
	public Ret deleteById(Object _id,Object id){
		return deleteById(_id,id, false);
	}
	
	/**
	 * 通用根据ID删除数据 需要先检查是否被其他地方使用
	 * @param _id 多表模式指定后缀ID
	 * @param id
	 * @param checkInUse
	 * @return
	 */
	public Ret deleteById(Object _id,Object id,boolean checkInUse){
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		if(notOk(id)){
			return fail(Msg.PARAM_ERROR);
		}
		String tableName=table(_id);
		Record record=Db.findById(tableName,id);
		if(record==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		if(checkInUse){
			String msg=checkInUse(_id,record);
			if(StrKit.notBlank(msg)){return fail(msg);}
		}
		
		boolean success=Db.delete(tableName, record);
		return success?success(record,Msg.SUCCESS):FAIL;
	}
	/**
	 * 检测数据是否被其它数据外键引用
	 * @param _id 多表模式指定后缀ID
	 * @param record
	 * @return
	 */
	public String checkInUse(Object _id,Record record){
		return null;
	}
	/**
	 * 检测数据是否字段是否可以执行切换true false
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param record
	 * @param column
	 * @return
	 */
	public String checkCanToggle(Object _id,Kv paras,Record record,String column){
		return null;
	}
	/**
	 * 检测数据是否字段是否可以执行切换true false
	 * @param _id 多表模式指定后缀ID
	 * @param record
	 * @param column
	 * @return
	 */
	public String checkCanToggle(Object _id,Record record,String column){
		return null;
	}
	/**
	 * 额外需要处理toggle操作
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param record
	 * @param column
	 * @return
	 */
	public String toggleExtra(Object _id,Kv paras,Record record,String column){
		return null;
	}
	
	/**
	 * 额外需要处理toggle操作
	 * @param _id 多表模式指定后缀ID
	 * @param record
	 * @param column
	 * @return
	 */
	public String toggleExtra(Object _id,Record record,String column){
		return null;
	}
	
	/**
	 * 判断字典名是否存在 排除指定ID
	 * @param _id 多表模式指定后缀ID
	 * @param name
	 * @param id
	 * @return
	 */
	public boolean existsName(Object _id,String name,Object id) {
		name = name.toLowerCase().trim();
		Sql sql=sql(_id).selectId().eqQM("name").idNoteqQM().first();
		Object existId = queryColumn(sql, name, id);
		return isOk(existId);
	}

	/**
	 * 判断是否存在
	 * @param _id 多表模式指定后缀ID
	 * @param columnName
	 * @param value
	 * @param id
	 * @return
	 */
	public boolean exists(Object _id,String columnName,Serializable value,Object id) {
		value = value.toString().trim();
		Sql sql=sql(_id).selectId().eqQM(columnName).idNoteqQM().first();
		Object existId = queryColumn(sql, value,id);
		return isOk(existId);
	}
	/**
	 * 判断是否存在
	 * @param _id 多表模式指定后缀ID
	 * @param columnName
	 * @param value
	 * @return
	 */
	public boolean exists(Object _id,String columnName,Serializable value) {
		return exists(_id,columnName, value, -1);
	}
	/**
	 * 判断字典名是否存在
	 * @param _id 多表模式指定后缀ID
	 * @param name
	 * @return
	 */
	public boolean existsName(Object _id,String name) {
		return existsName(_id,name, -1);
	}
	/**
	 * 根据ID获得一条数据
	 * @param _id 多表模式指定后缀ID
	 * @param id
	 * @return
	 */
	public Record findById(Object _id,Object id) {
		if(notOk(id)){return null;}
		return Db.findById(table(_id),id);
	}
	/**
	 * 得到符合条件的第一个
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @return
	 */
	public Record findFirst(Object _id,Kv paras) {
		return findFirst(_id,paras,false);
	}
	/**
	 * 得到符合条件的第一个
	 * @param _id 多表模式指定后缀ID
	 * @return
	 */
	public Record findFirst(Object _id) {
		return findFirst(_id,null,false);
	}
	/**
	 * 得到符合条件的第一个
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public Record findFirst(Object _id,Kv paras,boolean customCompare) {
		return dbTemplate(_id,"common.first", Kv.by("customCompare",customCompare).setIfNotNull("myparas", paras)).findFirst();
	}
	/**
	 * 随机得到符合条件的第一个
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @return
	 */
	public Record getRandomOne(Object _id,Kv paras) {
		return getRandomOne(_id,paras, false);
	}
	/**
	 * 随机得到符合条件的第一个
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public Record getRandomOne(Object _id,Kv paras,boolean customCompare) {
		Kv conf=Kv.by("customCompare",customCompare).setIfNotNull("myparas", paras);
		conf.set("orderColumns",new String[] {"rand()"});
		conf.set("orderTypes",new String[] {"asc"});
		return dbTemplate(_id,"common.firstrand", conf).findFirst();
	}
	/**
	 * 根据条件删除数据
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public Ret deleteBy(Object _id,Kv paras,boolean customCompare) {
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		dbTemplate(_id,"common.delete", Kv.by("customCompare",customCompare).setIfNotNull("myparas", paras)).delete();
		return SUCCESS;
	}
	/**
	 * 根据条件删除数据
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @return
	 */
	public Ret deleteBy(Object _id,Kv paras) {
		return deleteBy(_id,paras, false);
	}


	/**
	 * 切换Boolean类型字段
	 * @param _id 多表模式指定后缀ID
	 * @param id 需要切换的数据ID
	 * @param columns 需要切换的字段列表
	 * @return
	 */
	public Ret toggleBoolean(Object _id,Object id,String... columns) {
		return toggleBoolean(_id,null, id, columns);
	}
	/**
	 * 保存
	 * @param _id
	 * @param record
	 * @return
	 */
	public Ret save(Object _id,Record record) {
		if(record==null||isOk(record.getInt("id"))) {
			return FAIL;
		}
		boolean success=Db.save(table(_id), record);
		return ret(success);
	}
	/**
	 * 更新
	 * @param _id
	 * @param record
	 * @return
	 */
	protected Ret update(Object _id,Record record) {
		if(record==null||notOk(record.getInt("id"))) {
			return FAIL;
		}
		boolean success=Db.update(table(_id), record);
		return ret(success);
	}
	
	protected void batchUpdate(Object _id,List<Record> records,int batchSize) {
		Db.batchUpdate(table(_id), records, batchSize);
	}
	/**
	 * 切换Boolean类型字段值
	 * @param _id 多表模式指定后缀ID
	 * @param kv 额外传入的参数 用于 toggleExtra里用
	 * @param id 需要切换的数据ID
	 * @param columns 需要切换的字段列表
	 * @return
	 */
	protected Ret toggleBoolean(Object _id,Kv kv,Object id,String... columns) {
		if(notOk(id)){
			return fail(Msg.PARAM_ERROR);
		}
		String tableName=table(_id);
		Record record=Db.findById(tableName,id);
		if(record==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		
			Boolean value;
			for(String column:columns){
				String msg=checkCanToggle(_id,kv,record,column);
				if(StrKit.notBlank(msg)){
					return fail(msg);
				}
				value=record.getBoolean(column);
				record.set(column, value==null?true:!value);
				//处理完指定这个字段 还需要额外处理什么？
				if(kv==null){
					msg=toggleExtra(_id,record,column);
				}else{
					msg=toggleExtra(_id,kv,record,column);
				}
				if(StrKit.notBlank(msg)){
					return fail(msg);
				}
			}
			boolean success=Db.update(tableName, record);
			return success?success(record,Msg.SUCCESS):FAIL;
		
	}
	
	/**
	 * 常用的得到列表数据数量
	 * 自定义参数compare
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	protected int getCount(Object _id,Kv paras,boolean customCompare){
		return dbTemplate(_id,"common.count",Kv.by("customCompare",customCompare).setIfNotNull("myparas", paras)).queryInt();
	}
	/**
	 * 常用的得到列表数据数量
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @return
	 */
	protected int getCount(Object _id,Kv paras){
		return getCount(_id,paras, false);
	}
	

	/**
	 * 得到新数据的排序Rank值 默认从1开始 不带任何查询条件
	 * @param _id 多表模式指定后缀ID
	 * @return
	 */
	protected int getNextSortRank(Object _id){
		return getNextSortRank(_id,null, false);
	}
	/**
	 * 得到新数据的排序Rank值 从0开始 不带任何查询条件
	 * @param _id 多表模式指定后缀ID
	 * @return
	 */
	protected int getNextRankFromZero(Object _id){
		return getNextSortRank(_id,null, true);
	}
	/**
	 * 得到新数据的排序Rank值 从0开始 带查询条件
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @return
	 */
	protected int getNextRankFromZero(Object _id,Kv paras){
		return getNextSortRank(_id,paras, true);
	}
	/**
	 * 得到新数据的排序Rank值 自带简单条件查询默认从1开始
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @return
	 */
	protected int getNextSortRank(Object _id,Kv paras){
		return getNextSortRank(_id,paras, false);
	}
	/**
	 * 得到新数据的排序Rank值 自带简单条件查询 可以自定义是否从零开始
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param fromZero
	 * @return
	 */
	protected int getNextSortRank(Object _id,Kv paras,boolean  fromZero){
		int count=getCount(_id,paras);
		if(fromZero){
			return count;
		}
		return count+1;
	}
	

	/**
	 * 得到新数据的排序Rank值 自带简单条件查询 可以自定义是否从零开始
	 * 条件可定制版
	 * @param _id 多表模式指定后缀ID
	 * @param paras
	 * @param customCompare
	 * @param fromZero
	 * @return
	 */
	protected int getNextSortRank(Object _id,Kv paras,Boolean customCompare,boolean  fromZero){
		int count=getCount(_id,paras, customCompare);
		if(fromZero){
			return count;
		}
		return count+1;
	}
	
	/**
	 * 常用的得到列表数据数量
	 * @param _id 多表模式指定后缀ID
	 * @return
	 */
	protected int getCount(Object _id){
		return getCount(_id,null, false);
	}
	
	
	

	/**
	 * 创建一个新表 如果存在 直接返回TRUE
	 * @param _id 多表模式指定后缀ID
	 * @return
	 */
	public boolean createTable(Object _id) {
		String newTableName=table(_id);
		boolean ret=tableExist(newTableName);
		if(!ret) {
			ret=createTheTable(newTableName,mainTableName);
		}
		return ret;
	}
	
	public boolean tableExist(Object _id) {
		String newTableName=table(_id);
		return tableExist(newTableName);
	}
	/**
	 * 获取表名
	 * @param _id 多表模式指定后缀ID
	 * @return
	 */
	protected String table(Object _id){
		String tableName=getMainTableName();
		if(notOk(_id)) {return tableName;}
		return tableName+"_"+_id;
	}
	
	/**
	 * 更新同级删除数据之后数据的排序
	 * @param _id 多表模式指定后缀ID
	 * @param sortRank
	 */
	protected void updateSortRankAfterDelete(Object _id,Integer sortRank) {
		updateSortRankAfterDelete(table(_id),sortRank);
	}
	
	/**
	 * 更新同级删除数据之后数据的排序
	 * @param _id 多表模式指定后缀ID
	 * @param params
	 * @param sortRank
	 */
	protected void updateSortRankAfterDelete(Object _id,Kv params, Integer sortRank) {
		updateSortRankAfterDelete(table(_id), params, sortRank);
	}
	/**
	 * 删除关联子数据
	 * @param _id 多表模式指定后缀ID
	 * @param pid
	 * @return
	 */
	protected Ret deleteByPid(Object _id,Object pid) {
		return deleteBy(_id,Kv.by("pid", pid));
	}
	
	/**
	 * 关键词查询指定返回个数的数据 默认返回所有字段
	 * 关键词为空的时候 返回空list
	 * @param _id 多表模式指定后缀ID
	 * @param keywords 关键词
	 * @param limitCount  返回个数
	 * @param matchColumns 关键词去匹配哪些字段 可以一个 可以多个 逗号隔开
	 * @return
	 */
	public List<Record> getAutocompleteList(Object _id,String keywords, Integer limitCount,String matchColumns) {
		return getAutocompleteList(_id,keywords, limitCount, false, Sql.KEY_STAR, matchColumns);
	}
	/**
	 * 关键词查询指定返回个数的数据 默认返回所有字段
	 * @param _id 多表模式指定后缀ID
	 * @param keywords 关键词
	 * @param limitCount  返回个数
	 * @param always  当关键词空的时候 是否需要查询所有数据返回指定个数
	 * @param matchColumns 关键词去匹配哪些字段 可以一个 可以多个 逗号隔开
	 * @return
	 */
	public List<Record> getAutocompleteList(Object _id,String keywords, Integer limitCount,Boolean always,String matchColumns) {
		return getAutocompleteList(_id,keywords, limitCount, always, Sql.KEY_STAR, matchColumns);
	}
	/**
	 * 关键词查询指定返回个数的数据
	 * @param _id 多表模式指定后缀ID
	 * @param keywords 关键词
	 * @param limitCount  返回个数
	 * @param always  当关键词空的时候 是否需要查询所有数据返回指定个数
	 * @param returnColumns 查询字段 可以是* 也可以是 id,name这种逗号隔开
	 * @param matchColumns 关键词去匹配哪些字段 可以一个 可以多个 逗号隔开
	 * @return
	 */
	public List<Record> getAutocompleteList(Object _id,String keywords, Integer limitCount,Boolean always,String returnColumns,String matchColumns) {
		if((notOk(keywords)&&(always==null||always==false))||notOk(matchColumns)) {
			return Collections.emptyList();
		}
		Sql sql=sql(_id).select(returnColumns).firstPage(limitCount);
		//如果关键词为空 默认是返回空数据
			//但是如果指定了关键词是空 就按照指定个数返回数据的话
		if(notOk(keywords)&&always!=null&&always==true) {
			return Db.find(sql.toSql());
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
		return Db.find(sql.toSql());
	}
	/**
	 * 快速获取分表操作Sql
	 * @param _id
	 * @return
	 */
	public Sql sql(Object _id) {
		return Sql.me(MainConfig.DB_TYPE).select().from(table(_id));
	}
	
	/**
	 * 得到表中的字段字符拼接，除了指定的withoutColumns
	 * @param withoutColumns
	 * @return
	 */
	public String getTableSelectColumnsWithout(String... withoutColumns) {
		if(withoutColumns==null||withoutColumns.length==0) {return Sql.KEY_STAR;}
		Set<String> tableColumns=TableMapping.me().getTable(mainTableModelClass()).getColumnNameSet();
		List<String> selectColumns=new ArrayList<String>(tableColumns);
		for(String col:withoutColumns) {
			selectColumns.remove(col.trim().toLowerCase());
		}
		return selectColumns.size()==0?Sql.KEY_STAR:CollectionUtil.join(selectColumns, ",");
	}
	
	/**
	 * 得到不包含指定列的数据
	 * @param _id
	 * @param withoutColumns
	 * @return
	 */
	public List<Record> getCommonListWithoutColumns(Object _id,String withoutColumns) {
		return getCommonListWithoutColumns(_id,null, false,withoutColumns);
	}
	
	/**
	 * 得到不包含指定列的数据
	 * @param _id
	 * @param paras
	 * @param withoutColumns
	 * @param customCompare
	 * @return
	 */
	public List<Record> getCommonListWithoutColumns(Object _id,Kv paras,boolean customCompare,String... withoutColumns) {
		return dbTemplate(_id,"common.list", 
				Kv.by("customCompare",customCompare)
				.setIfNotNull("myparas", paras)
				.setIfNotBlank("columns",getTableSelectColumnsWithout(withoutColumns)))
				.find();
	}
	
	/**
	  *根据关键词分页查询 
	  * 默认倒序
	 * @param _id
	 * @param orderColumn
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param matchColumns
	 * @param otherParas
	 * @return
	 */
	public Page<Record> paginateByKeywords(Object _id,String orderColumn,int pageNumber,int pageSize,String keywords,String matchColumns,Kv otherParas){
		return paginateByKeywords(_id,orderColumn,"desc",pageNumber,pageSize,keywords,matchColumns,otherParas);
	}
	/**
	   * 根据关键词分页查询 
	 * @param _id
	 * @param orderColumn
	 * @param orderType
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param matchColumns
	 * @param otherParas
	 * @return
	 */
	public Page<Record> paginateByKeywords(Object _id,String orderColumn,String orderType,int pageNumber,int pageSize,String keywords,String matchColumns,Kv otherParas){
		return paginateByKeywords(_id,Sql.KEY_STAR,orderColumn,orderType,pageNumber,pageSize,keywords,matchColumns,otherParas);
	}
	
	
	/**
	 * 根据关键词匹配分页查询
	 * @param _id
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
	public Page<Record> paginateByKeywords(Object _id,String returnColumns,String orderColumn,String orderType,int pageNumber,int pageSize,String keywords,String matchColumns,Kv otherParas){
		if(notOk(matchColumns)) {
			return EMPTY_PAGE;
		}
		Sql sql=selectSql(_id).select(returnColumns).page(pageNumber, pageSize);
		Sql totalCountSql=selectSql(_id).count();
		if(isOk(orderColumn)&&isOk(orderType)) {
			sql.orderBy(orderColumn,orderType.equals("desc"));
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
	
	private Page<Record> processPaginate(Sql sql, Sql totalCountSql, int pageNumber, int pageSize) {
		Integer totalRow=queryInt(totalCountSql);
		if(totalRow==null||totalRow==0) {
			return EMPTY_PAGE;
		}
		List<Record> list=find(sql);
		int totalPage=(totalRow/pageSize)+(totalRow%pageSize>0?1:0);
		return new Page<Record>(list, pageNumber, pageSize, totalPage, totalRow);
	}
	
	/**
	 * 快速获取sql 默认是select sql
	 * @param _id
	 * @return
	 */
	protected Sql selectSql(Object _id) {
		return Sql.me(MainConfig.DB_TYPE).select().from(table(_id));
	}
	/**
	 * 快速获取update sql
	 * @param _id
	 * @return
	 */
	protected Sql updateSql(Object _id) {
		return Sql.me(MainConfig.DB_TYPE).update(table(_id));
	}
	/**
	 * 快速获取delete sql
	 * @param _id
	 * @return
	 */
	protected Sql deleteSql(Object _id) {
		return Sql.me(MainConfig.DB_TYPE).delete().from(table(_id));
	}
	
	
	/**
	 * 执行查询
	 * @param sql
	 * @return
	 */
	protected List<Record> find(Sql sql){
		return find(sql.toSql());
	}
	/**
	 * 执行查询
	 * @param sql
	 * @param paras
	 * @return
	 */
	protected List<Record> find(Sql sql,Object... paras){
		return find(sql.toSql(),paras);
	}
	
	/**
	 * 执行查询
	 * @param sql
	 * @return
	 */
	protected List<Record> find(String sql){
		return Db.find(sql);
	}
	/**
	 * 执行查询
	 * @param sql
	 * @param paras
	 * @return
	 */
	protected List<Record> find(String sql,Object... paras){
		return Db.find(sql, paras);
	}
 
}
