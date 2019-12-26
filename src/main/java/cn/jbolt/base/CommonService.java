package cn.jbolt.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.config.PageSize;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.db.sql.SqlUtil;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.CACHE;

public abstract class CommonService implements IJBoltParaValidator{
	protected static final Log LOG=LogFactory.get();
	@Inject
	protected SystemLogService systeLogService;
	/**
	 * 成功消息默认返回值
	 */
	protected static final Ret SUCCESS=Ret.ok("msg", Msg.SUCCESS);
	/**
	 * 失败消息默认返回值
	 */
	protected static final Ret FAIL=Ret.fail("msg", Msg.FAIL);
	/**
	 * 空分页数据返回值
	 */
	protected static final Page EMPTY_PAGE=new Page(Collections.EMPTY_LIST, 1, PageSize.PAGESIZE_ADMIN_LIST, 0, 0);
	/**
	 * 设置失败返回消息
	 * @param msg
	 * @return
	 */
	protected Ret fail(String msg){
		return Ret.fail("msg", msg);
	}
	/**
	 * 自动判断返回值
	 * @param success
	 * @return
	 */
	protected Ret ret(boolean success){
		return success?SUCCESS:FAIL;
	}
	/**
	 * 设置成功返回消息
	 * @param msg
	 * @return
	 */
	protected Ret success(String msg){
		return Ret.ok("msg", msg);
	}
	/**
	 * 设置成功返回消息 设置返回data
	 * @param data
	 * @return
	 */
	protected Ret successWithData(Object data){
		return Ret.ok("data", data);
	}
	/**
	 * 设置成功返回值带着数据和消息
	 * @param data
	 * @param msg
	 * @return
	 */
	protected Ret success(Object data,String msg){
		return Ret.ok("msg", msg).set("data", data);
	}
	
	/**
	 * 调用日志服务 添加日志信息 操作类型是Save
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 */
	protected void addSaveSystemLog(Object targetId, Object userId,int targetType,String modelName) {
		addSystemLog(targetId, userId, SystemLog.TYPE_SAVE, targetType, modelName, null);
	}
	/**
	 * 调用日志服务 添加日志信息 操作类型是Update
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 */
	protected void addUpdateSystemLog(Object targetId, Object userId,int targetType,String modelName) {
		addSystemLog(targetId, userId, SystemLog.TYPE_UPDATE, targetType, modelName, null);
	}
	/**
	 * 调用日志服务 添加日志信息 操作类型是Update
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 * @param append  额外信息
	 */
	protected void addUpdateSystemLog(Object targetId, Object userId,int targetType,String modelName,String append) {
		addSystemLog(targetId, userId, SystemLog.TYPE_UPDATE, targetType, modelName, append);
	}
	/**
	 * 调用日志服务 添加日志信息 操作类型是Delete
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 */
	protected void addDeleteSystemLog(Object targetId, Object userId,int targetType,String modelName) {
		addSystemLog(targetId, userId, SystemLog.TYPE_DELETE, targetType, modelName, null);
	}
	/**
	 * 调用日志服务 添加日志信息
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param type   操作类型
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 */
	protected void addSystemLog(Object targetId, Object userId, int type,int targetType,String modelName) {
		addSystemLog(targetId, userId, type, targetType, modelName, null);
	}
	/**
	 * 调用日志服务 添加日志信息
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param type   操作类型
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 * @param append  额外信息
	 */
	protected void addSystemLog(Object targetId, Object userId, int type,int targetType,String modelName,String append) {
		String userName=CACHE.me.getUserName(userId);
		StringBuilder title=new StringBuilder();
		title.append("<span class='text-danger'>[").append(userName).append("]</span>")
		.append(SystemLogService.typeName(type))
		.append(SystemLogService.targetTypeName(targetType))
		.append("<span class='text-danger'>[").append(modelName).append("]</span>");
		if(StrKit.notBlank(append)){
			title.append(append);
		}
		//调用底层方法
		addSystemLogWithTitle(targetId, userId, type, targetType, title.toString());
	}
	/**
	 * 调用日志服务 添加日志信息
	 * @param targetId
	 * @param userId
	 * @param type
	 * @param targetType
	 * @param title
	 */
	protected void addSystemLogWithTitle(Object targetId, Object userId, int type,int targetType,String title) {
		String userName=CACHE.me.getUserName(userId);
		systeLogService.saveLog(type, targetType, targetId, title.toString(), 0, userId,userName);
	}
	
	/**
	 * 添加拼接like
	 * @param column
	 */
	protected String columnLike(String column){
		if(notOk(column)) {return " like ''";}
		if(column.indexOf("'")!=-1) {
			column=column.replace("'", "''");
		}
		return " like '%"+column+"%'";
	}
	
	
	/**
	 *  创建分表
	 * @param newTableName
	 * @param mainTableName
	 * @return
	 */
	protected boolean createTheTable(String newTableName,String mainTableName) {
		String sql="create table "+newTableName+" LIKE "+mainTableName;
		try {
			cn.hutool.db.Db.use(DbKit.getConfig().getDataSource()).execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return tableExist(newTableName);
	}
	/**
	 * 检测表是否存在
	 * @param tableName
	 * @return
	 */
	protected boolean tableExist(String tableName) {
			boolean flag = false;
			try {
				Connection connection=DbKit.getConfig().getConnection();
				DatabaseMetaData meta = connection.getMetaData();
				String type [] = {"TABLE"};
				ResultSet rs = meta.getTables(null, null, tableName, type);
				flag = rs.next();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return flag;
	}
	
	/**
	 * 更新同级删除数据之后数据的排序
	 * @param sortRank
	 */
	protected void updateSortRankAfterDelete(String table,Integer sortRank) {
		updateSortRankAfterDelete(table,null, sortRank);
	}
	/**
	 * 更新同级删除数据之后数据的排序
	 * @param params
	 * @param sortRank
	 */
	protected void updateSortRankAfterDelete(String table,Kv params, Integer sortRank) {
		if(params==null||params.isEmpty()) {
			Db.update("update "+table+" set sort_rank=sort_rank-1 where sort_rank>?",sortRank);
		}else {
			StringBuilder sb=new StringBuilder();
			sb.append(" 1=1 ");
			int size=params.size();
			Object[] paramArray=new Object[size+1];
			int i=0;
			for (Object key:params.keySet()) {
				sb.append(" and ").append(key.toString()).append("=? ");
				paramArray[i]=params.get(key.toString());
				i++;
			}
			paramArray[size]=sortRank;
			Db.update("update "+table+" set sort_rank=sort_rank-1 where "+sb.toString()+" and sort_rank>?",paramArray);
		}
	}
	/**
	 * 查询一个字段
	 * @param sql
	 * @return
	 */
	protected <T> T queryColumn(Sql sql) {
		if(sql.isPrepared()) {
			return queryColumn(sql,sql.getWhereValues());
		}
		return queryColumn(sql.toSql());
	}
	/**
	 * 查询一个字段
	 * @param <T>
	 * @param sql
	 * @param paras
	 * @return
	 */
	protected <T> T queryColumn(Sql sql,Object... paras) {
		return queryColumn(sql.toSql(),paras);
	}
	/**
	 * 查询一个字段
	 * @param <T>
	 * @param sql
	 * @return
	 */
	protected <T> T queryColumn(String sql) {
		return Db.queryColumn(sql);
	}
	/**
	 * 查询一个字段
	 * @param <T>
	 * @param sql
	 * @param paras
	 * @return
	 */
	protected <T> T queryColumn(String sql,Object... paras) {
		return Db.queryColumn(sql,paras);
	}
	
	protected Integer queryInt(Sql sql) {
		if(sql.isPrepared()) {
			return queryInt(sql,sql.getWhereValues());
		}
		return queryInt(sql.toSql());
	}
	
	protected Integer queryInt(Sql sql,Object... paras) {
		return queryInt(sql.toSql(),paras);
	}
	protected Integer queryInt(String sql) {
		return Db.queryInt(sql);
	}
	protected Integer queryInt(String sql,Object... paras) {
		return Db.queryInt(sql,paras);
	}
	
	protected void update(Sql sql) {
		if(sql.isPrepared()) {
			update(sql,sql.getWhereValues());
		}else {
			update(sql.toSql());
		}
	}
	protected void update(Sql sql,Object... paras) {
		update(sql.toSql(),paras);
	}
	protected int update(String sql) {
		return Db.update(sql);
	}
	protected int update(String sql,Object... paras) {
		return Db.update(sql,paras);
	}
	protected <T> List<T> query(String sql,Object... paras) {
		return Db.query(sql,paras);
	}
	protected <T> List<T> query(String sql) {
		return Db.query(sql);
	}
	protected <T> List<T> query(Sql sql) {
		if(sql.isPrepared()) {
			return query(sql.toSql(),sql.getWhereValues());
		}
		return query(sql.toSql());
	}
	protected <T> List<T> query(Sql sql,Object... paras) {
		return query(sql.toSql(),paras);
	}
	protected Object TRUE() {
		return SqlUtil.TRUE();
	}
	
	protected Object FALSE() {
		return SqlUtil.FALSE();
	}
	
	/**
	 * 判断Object参数有效性
	 * @param param
	 */
	public boolean isOk(Object param){
		return JBoltParaValidator.isOk(param);
	}
	/**
	 * 判断Object参数是否无效
	 */
	public boolean notOk(Object param){
		return JBoltParaValidator.notOk(param);
	}
	
	/**
	 * 判断上传文件是图片
	 * @param isImage
	 */
	public boolean isImage(String contentType){
		return JBoltParaValidator.isImage(contentType);
	}
	/**
	 * 判断上传文件不是图片
	 * @param notImage
	 */
	public boolean notImage(String contentType){
		return JBoltParaValidator.notImage(contentType);
	}
	/**
	 * 判断上传文件类型不是图片
	 * @param file
	 */
	public boolean notImage(UploadFile file){
		return JBoltParaValidator.notImage(file);
	}
	/**
	 * 判断上传文件类型是否为图片
	 * @param file
	 */
	public boolean isImage(UploadFile file){
		return JBoltParaValidator.isImage(file);
	}
	
	/**
	 * 判断Object[]数组类型数据是否正确
	 * @param param
	 * @return
	 */
	public boolean isOk(Object[] param){
		return JBoltParaValidator.isOk(param);
	}
	/**
	 * 判断Object[]数组类型数据不正确
	 * @param param
	 * @return
	 */
	public boolean notOk(Object[] param){
		return JBoltParaValidator.notOk(param);
	}
}
