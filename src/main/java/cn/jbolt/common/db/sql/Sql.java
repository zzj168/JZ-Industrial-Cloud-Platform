package cn.jbolt.common.db.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.druid.util.JdbcConstants;

import cn.hutool.core.util.CharUtil;

/**
 * Sql语句拼接工具 面向对象方式
 * 
 * @ClassName: Sql
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年9月5日
 * 
 *        注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class Sql {
	/**
	 * 内置常量 可用于其他数据库标识boolean true
	 */
	public static final Integer TRUE=1;
	/**
	 * 内置常量 可用于其他数据库标识boolean false
	 */
	public static final Integer FALSE=0;
	/**
	 * 数据库默认类型
	 */
	protected String dbType = JdbcConstants.MYSQL;
	/**
	 * 查询搜索 select
	 */
	public static final int TYPE_SELECT = 1;
	/**
	 * 插入数据 sigle
	 */
	public static final int TYPE_INSERT = 2;
	/**
	 * 插入数据 batch
	 */
	public static final int TYPE_INSERT_BATCH = 3;
	/**
	 * 更新数据 update
	 */
	public static final int TYPE_UPDATE = 4;
	/**
	 * 删除数据 delete
	 */
	public static final int TYPE_DELETE = 5;

	/************** 数据库关键字 *****************/

	public static final String KEY_SELECT = "select ";
	public static final String KEY_STAR = "*";
	public static final String KEY_QUESTION_MARK = "?";
	public static final String KEY_INSERT = "insert into ";
	public static final String KEY_UPDATE = "update ";
	public static final String KEY_DELETE = "delete ";
	public static final String KEY_LIMIT = " limit ";
	public static final String KEY_OFFSET = " offset ";
	public static final String KEY_ROWNUM_ORACLE = " rownum ";
	public static final String KEY_FROM = " from ";
	public static final String KEY_SET = " set ";
	public static final String KEY_WHERE = " where ";
	public static final String KEY_ORDERBY = " order by ";
	public static final String KEY_GROUPBY = " group by ";
	public static final String KEY_WHITESPACE = " ";
	public static final String KEY_COMMA = ",";
	public static final String KEY_AS = " as ";
	public static final String KEY_ON = " on ";
	public static final String KEY_ASC = " asc";
	public static final String KEY_DESC = " desc";
	public static final String KEY_COUNT_START = "count(*)";
	public static final String KEY_COUNT_COLUMN = "count(%s)";
	public static final String KEY_DISTINCT = "distinct ";
	public static final String KEY_COUNT_DISTINCT = "count(distinct %s)";
	public static final String KEY_INSERT_VALUES = " values(%s) ";
	public static final String KEY_NULLSTRING = "";
	public static final String KEY_RAND = "rand()";
	public static final String KEY_AND = " and ";
	public static final String KEY_LEFT_JOIN = " left join ";
	public static final String KEY_RIGHT_JOIN = " right join ";
	public static final String KEY_INNER_JOIN = " inner join ";
	public static final String KEY_ID = "id";

	// 语句操作类型
	private int type;
	// 表名
	private String table;
	// 搜索返回字段
	private String returnColumns;
	// 查询单列列表
	private boolean queryColumnList;
	// 查询数量
	private boolean queryCount;
	// 查询最大值
	private boolean queryMax;
	// 查询distinct数量
	private boolean queryDistinctCount;
	// where后的条件
	private List<Condition> conditions;
	// insert values
	private Object[] insertValues;
	// 需要更新的字段map
	private Map<String, Object> updateColumnsMap;
	// 分组查询
	private String groupBy;
	// 排序查询
	private String orderBy;
	// 关联查询语句
	private String joinSql;
	// 查询数量 字段
	private String countColumns;
	// prepared sql需要的值
	private List<Object> whereValues;
	// 是否倒叙
	private boolean desc;
	// 是否支持问号占位方式
	private boolean prepared;
	// 是否启用分页
	private boolean hasPage;
	// 分页参数 第几页
	private int pageNumber;
	// 分页参数 每页几个
	private int pageSize;

	/**
	 * 只能从这里获得Sql 指定数据类型
	 * 
	 * @return
	 */
	public static Sql me(String dbType) {
		return new Sql().setDbType(dbType);
	}

	/**
	 * 支持问号占位sql
	 * 
	 * @return
	 */
	public Sql prepared() {
		this.prepared = true;
		return this;
	}
	/**
	 * 首页
	 * @param pageSize
	 * @return
	 */
	public Sql firstPage(int pageSize) {
		return page(1, pageSize);
	}
	/**
	 * 分页查询
	 * 
	 * @return
	 */
	public Sql page(int pageNumber, int pageSize) {
		this.hasPage = true;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		if(pageNumber==1&&pageSize==1&&dbType.equals(JdbcConstants.SQL_SERVER)) {
			this.returnColumns="top 1 "+returnColumns;
		}
		return this;
	}

	/**
	 * 分页查询 符合条件第一个
	 * 
	 * @return
	 */
	public Sql first() {
		return page(1, 1);
	}

	/**
	 * 随机一个
	 * 
	 * @return
	 */
	public Sql randomOne() {
		orderBy(KEY_RAND);
		return page(1, 1);
	}

	/**
	 * 构造函数
	 */
	private Sql() {
		this.returnColumns = KEY_STAR;
		this.conditions = new ArrayList<>();
	}

	/**
	 * 查询表
	 * 
	 * @param table
	 * @param asName
	 * @return
	 */
	public Sql from(String table, String asName) {
		this.table = table + KEY_WHITESPACE + asName;
		return this;
	}
	/**
	 * 判断是Mysql
	 * @return
	 */
	public boolean isMysql() {
		return dbType.equals(JdbcConstants.MYSQL);
	}
	/**
	 * 判断是Postgresql
	 * @return
	 */
	public boolean isPostgresql() {
		return dbType.equals(JdbcConstants.POSTGRESQL);
	}
	/**
	 * 判断是oracle
	 * @return
	 */
	public boolean isOracle() {
		return dbType.equals(JdbcConstants.ORACLE);
	}
	/**
	 * 判断是oracle
	 * @return
	 */
	public boolean isSqlServer() {
		return dbType.equals(JdbcConstants.SQL_SERVER);
	}

	

	/**
	 * 查询表
	 * 
	 * @param table
	 * @return
	 */
	public Sql from(String table) {
		this.table = table;
		return this;
	}

	/**
	 * columnName is null
	 * 
	 * @return
	 */
	public Sql isNull(String columnName) {
		processAnd();
		conditions.add(new Condition().isNull(columnName));
		return this;
	}

	/**
	 * 查询表
	 * 
	 * @param Sql
	 * @return
	 */
	public Sql fromSql(Sql sql) {
		this.table = Condition.BRACKET_LEFT + sql.toSql() + Condition.BRACKET_RIGHT;
		return this;
	}

	/**
	 * 查询表
	 * 
	 * @param Sql
	 * @param as
	 * @return
	 */
	public Sql fromSql(Sql sql, String as) {
		this.table = Condition.BRACKET_LEFT + sql.toSql() + Condition.BRACKET_RIGHT + KEY_WHITESPACE + as
				+ KEY_WHITESPACE;
		return this;
	}

	/**
	 * 设置
	 * 
	 * @return
	 */
	public Sql values(Object... values) {
		this.insertValues = values;
		return this;
	}

	/**
	 * 更新表
	 * 
	 * @param table
	 * @return
	 */
	public Sql update(String table) {
		this.table = table;
		this.type = TYPE_UPDATE;
		return this;
	}

	
	/**
	 * 插入数据
	 * 
	 * @param tableAndColumns
	 * @return
	 */
	public Sql insert(String tableAndColumns) {
		this.table = tableAndColumns;
		this.type = TYPE_INSERT;
		return this;
	}
	/**
	 * 插入数据
	 * 
	 * @param table
	 * @param insertColumns
	 * @return
	 */
	public Sql insert(String table,String insertColumns) {
		this.table = table+"("+insertColumns+")";
		this.type = TYPE_INSERT;
		return this;
	}
	/**
	 * 插入数据
	 * 
	 * @param table
	 * @param insertColumns
	 * @param values
	 * @return
	 */
	public Sql insert(String table,String insertColumns,Object... values) {
		this.table = table+"("+insertColumns+")";
		this.type = TYPE_INSERT;
		values(values);
		return this;
	}

	/**
	 * 删除表数据
	 * 
	 * @param table
	 * @return
	 */
	public Sql delete() {
		this.type = TYPE_DELETE;
		return this;
	}
	/**
	 * 查询ID
	 * @return
	 */
	public Sql selectId() {
		return select(KEY_ID);
	}

	/**
	 * 搜索表中的指定列
	 * 
	 * @param table
	 * @param columns
	 * @return
	 */
	public Sql select(String... columns) {
		this.type = TYPE_SELECT;
		// 如果是null 直接按照* 查询
		if (columns == null) {
			this.queryColumnList = false;
			returnColumns = KEY_STAR;
			return this;
		}
		// 判断查询columns个数
		int size = columns.length;
		if (size == 0) {
			// 没有就按照星
			this.queryColumnList = false;
			returnColumns = KEY_STAR;
		} else if (size == 1) {
			// 一个column 可能是单个列查询列表 如果带着表达式 可能是查询表达式结果
			this.queryColumnList = true;
			if (columns[0].indexOf("max(") != -1) {
				this.queryMax = true;
			}
			returnColumns = columns[0];
		} else {
			this.queryColumnList = false;
			// 多个肯定就是分开了
			returnColumns = "";
			for (int i = 0; i < size; i++) {
				returnColumns += columns[i];
				if (i != size - 1) {
					returnColumns += ",";
				}
			}
		}

		return this;
	}

	/**
	 * 条件 or
	 * 
	 * @return
	 */
	public Sql or() {
		conditions.add(new Condition().or());
		return this;
	}

	/**
	 * 条件 and
	 * 
	 * @return
	 */
	public Sql and() {
		conditions.add(new Condition().and());
		return this;
	}

	/**
	 * 条件 in(数组值)
	 * 
	 * @param key
	 * @param inValues
	 * @return
	 */
	public Sql in(String key, Object... inValues) {
		processAnd();
		conditions.add(new Condition().in(key, inValues));
		return this;
	}
	
	/**
	 * 条件 in(数组字符串)
	 * 
	 * @param key
	 * @param inValues
	 * @return
	 */
	public Sql in(String key, String inValues) {
		processAnd();
		conditions.add(new Condition().in(key, inValues));
		return this;
	}

	/**
	 * 条件 find_in_set
	 * 
	 * @param value
	 * @param values 
	 * @param valuesIsTableColumn values参数是否是数据库表中的列
	 * @return
	 */
	public Sql findInSet(Object value, String values, boolean valuesIsTableColumn) {
		if(isOracle()) {
			return like("(',' || "+(valuesIsTableColumn?values:"'"+values+"'")+" ||',')", ","+value+",");
		}
		if(isPostgresql()) {
			return like("(',' || "+(valuesIsTableColumn?values:"'"+values+"'")+" ||',')", ","+value+",");
		}
		if(isSqlServer()) {
			return like("(',' + "+values+" + ',')", ","+value+",");
		}
		
		if(isMysql()) {
			processAnd();
			conditions.add(new Condition().findInSet(value, values, valuesIsTableColumn));
		}
		return this;
	}

	/**
	 * 条件 in(返回数组值的sql对象)
	 * 
	 * @param key
	 * @param sql
	 * @return
	 */
	public Sql inSql(String key, Sql sql) {
		return inSql(key, sql.toSql());
	}

	/**
	 * 条件 in(返回数组值的sql语句)
	 * 
	 * @param key
	 * @param sql
	 * @return
	 */
	public Sql inSql(String key, String sql) {
		processAnd();
		conditions.add(new Condition().inSql(key, sql));
		return this;
	}

	/**
	 * 条件 key=value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql eq(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.EQ));
		return this;
	}
	/**
	 * 条件 id=value
	 * @param value
	 * @return
	 */
	public Sql eqId(Object value) {
		return eq(KEY_ID, value);
	}
	/**
	 * 条件 key=? 可批量处理
	 * 
	 * @param keys
	 * @return
	 */
	public Sql eqQM(String... keys) {
		if(keys!=null&&keys.length>0) {
			for(String key:keys) {
				eq(key, KEY_QUESTION_MARK);
			}
		}
		return this;
	}
	/**
	 * 条件 id=?
	 * 
	 * @param keys
	 * @return
	 */
	public Sql idEqQM() {
		return eqQM(KEY_ID);
	}

	/**
	 * 条件 key like value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql like(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.LIKE));
		return this;
	}

	/**
	 * 条件 key not like value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql notLike(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.NOTLIKE));
		return this;
	}

	/**
	 * 条件 value以key开头模糊查询
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql startWith(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.STARTWITH));
		return this;
	}

	/**
	 * 条件 value不以key开头模糊查询
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql notStartWith(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.STARTWITH));
		return this;
	}

	/**
	 * 条件 value以key结尾模糊查询
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql endWith(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.ENDWITH));
		return this;
	}

	/**
	 * 分组查询
	 * 
	 * @param key
	 * @return
	 */
	public Sql groupBy(String key) {
		this.groupBy = key;
		return this;
	}

	/**
	 * 排序查询
	 * 
	 * @param key
	 * @param desc
	 * @return
	 */
	public Sql orderBy(String key, boolean desc) {
		this.orderBy = key;
		this.desc = desc;
		return this;
	}

	/**
	 * 排序查询 默认正序
	 * 
	 * @param key
	 * @return
	 */
	public Sql orderBy(String key) {
		return orderBy(key, false);
	}
	/**
	 * 按照ID排序 默认正序
	 * @return
	 */
	public Sql orderById() {
		return orderById(false);
	}
	/**
	 * 按照ID排序 指定是否倒序
	 * @return
	 */
	public Sql orderById(boolean desc) {
		return orderBy("id", desc);
	}

	/**
	 * 处理条件前加and
	 */
	private void processAnd() {
		if (conditions.size() > 0) {
			Condition condition = conditions.get(conditions.size() - 1);
			if (condition.getType() != Condition.TYPE_LINK || (condition.getType() == Condition.TYPE_LINK
					&& condition.getValue1().equals(Condition.BRACKET_RIGHT))) {
				and();
			}
		}
	}

	/**
	 * 关联查询 左联接
	 * 
	 * @param joinSql
	 * @return
	 */
	public Sql leftJoin(String joinSql) {
		this.joinSql = KEY_LEFT_JOIN + joinSql;
		return this;
	}
	/**
	 * 关联查询 左联接
	 * 
	 * @param joinTableName
	 * @param asName
	 * @param onSql
	 * @return
	 */
	public Sql leftJoin(String joinTableName,String asName,String onSql) {
		this.joinSql = KEY_LEFT_JOIN + joinTableName+KEY_AS+asName+KEY_ON+onSql;
		return this;
	}

	/**
	 * 关联查询 右联接
	 * 
	 * @param joinSql
	 * @return
	 */
	public Sql rightJoin(String joinSql) {
		this.joinSql = KEY_RIGHT_JOIN + joinSql;
		return this;
	}
	
	
	/**
	 * 关联查询 右联接
	 * 
	 * @param joinTableName
	 * @param asName
	 * @param onSql
	 * @return
	 */
	public Sql rightJoin(String joinTableName,String asName,String onSql) {
		this.joinSql = KEY_RIGHT_JOIN + joinTableName+KEY_AS+asName+KEY_ON+onSql;
		return this;
	}

	/**
	 * 关联查询 内联接
	 * 
	 * @param joinSql
	 * @return
	 */
	public Sql innerJoin(String joinSql) {
		this.joinSql = KEY_INNER_JOIN + joinSql;
		return this;
	}
	
	/**
	 * 关联查询 左联接
	 * 
	 * @param joinTableName
	 * @param asName
	 * @param onSql
	 * @return
	 */
	public Sql innerJoin(String joinTableName,String asName,String onSql) {
		this.joinSql = KEY_INNER_JOIN + joinTableName+KEY_AS+asName+KEY_ON+onSql;
		return this;
	}

	/**
	 * 左括号
	 * 
	 * @return
	 */
	public Sql bracketLeft() {
		processAnd();
		conditions.add(new Condition().bracketLeft());
		return this;
	}

	/**
	 * 右括号
	 * 
	 * @return
	 */
	public Sql bracketRight() {
		conditions.add(new Condition().bracketRight());
		return this;
	}

	/**
	 * 大于 比较 key>value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql gt(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.GT));
		return this;
	}
	/**
	 * 大于 比较 key>? 可多个
	 * 
	 * @param keys
	 * @return
	 */
	public Sql gtQM(String... keys) {
		if(keys!=null&&keys.length>0) {
			for(String key:keys) {
				gt(key, KEY_QUESTION_MARK);
			}
		}
		return this;
	}

	/**
	 * 小于 比较 key<value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql lt(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.LT));
		return this;
	}
	/**
	 * 小于 比较 key<? 可多个
	 * 
	 * @param keys
	 * @return
	 */
	public Sql ltQM(String... keys) {
		if(keys!=null&&keys.length>0) {
			for(String key:keys) {
				lt(key, KEY_QUESTION_MARK);
			}
		}
		return this;
	}

	/**
	 * 大于等于 比较 key>=value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql ge(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.GE));
		return this;
	}
	/**
	 * 大于等于 比较 key>=? 可多个
	 * 
	 * @param keys
	 * @return
	 */
	public Sql geQM(String... keys) {
		if(keys!=null&&keys.length>0) {
			for(String key:keys) {
				ge(key, KEY_QUESTION_MARK);
			}
		}
		return this;
	}

	/**
	 * 小于等于 比较 key<=value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql le(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.LE));
		return this;
	}
	/**
	 * 小于等于 比较 key<=? 可多个
	 * 
	 * @param keys
	 * @return
	 */
	public Sql leQM(String... keys) {
		if(keys!=null&&keys.length>0) {
			for(String key:keys) {
				le(key, KEY_QUESTION_MARK);
			}
		}
		return this;
	}

	/**
	 * 不等于 比较 key!=value
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql noteq(String key, Object value) {
		processAnd();
		conditions.add(new Condition(key, value, Condition.NOT_EQ));
		return this;
	}
	/**
	 * 不等于 比较 id!=value
	 * @param value
	 * @return
	 */
	public Sql noteqId(Object value) {
		return noteq(KEY_ID, value);
	}
	
	/**
	 * 不等于 比较  key!=?
	 * 
	 * @param keys
	 * @return
	 */
	public Sql noteqQM(String... keys) {
		if(keys!=null&&keys.length>0) {
			for(String key:keys) {
				noteq(key, KEY_QUESTION_MARK);
			}
		}
		return this;
	}
	/**
	 * 不等于比较 id!=? 
	 * @return
	 */
	public Sql idNoteqQM() {
		return noteqQM(KEY_ID);
	}

	/**
	 * 更新sql专用 set字段值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Sql set(String key, Object value) {
		if (updateColumnsMap == null) {
			updateColumnsMap = new HashMap<String, Object>();
		}
		updateColumnsMap.put(key, SqlUtil.processBoolean(value));
		return this;
	}
	

	/**
	 * 将对象转sql
	 * 
	 * @return
	 */
	public String toSql() {
		StringBuilder sql = new StringBuilder();
		switch (type) {
		case TYPE_SELECT:
			sql.append(KEY_SELECT);
			break;
		case TYPE_INSERT:
			sql.append(KEY_INSERT);
			break;
		case TYPE_INSERT_BATCH:
			sql.append(KEY_INSERT);
			break;
		case TYPE_UPDATE:
			sql.append(KEY_UPDATE);
			break;
		case TYPE_DELETE:
			sql.append(KEY_DELETE).append(KEY_FROM);
			break;
		}
		builderSql(sql, false);
		return sql.toString();
	}

	/**
	 * 内部底层函数 将对象属性判断拼接 转为最终sql
	 * 
	 * @param sql
	 * @param withoutSelect 是否生成不带select的sql
	 */
	private void builderSql(StringBuilder sql, boolean withoutSelect) {
		if (prepared) {
			whereValues = new ArrayList<Object>();
		}
		// 首先判断是不是查询类型语句 是的话 加 from关键词
		if (type == TYPE_SELECT) {
			if (withoutSelect == false) {
				sql.append(returnColumns);
			}
			sql.append(KEY_FROM);
		}
		// 拼接tableName
		sql.append(table);
		if (type == TYPE_SELECT && joinSql != null) {
			sql.append(joinSql).append(KEY_WHITESPACE);
		}
		// 判断更新类型的语句 更新字段是否存在设置 如果存在 拼接set
		else if (type == TYPE_UPDATE && updateColumnsMap != null) {
			int size = updateColumnsMap.size();
			int index = 0;
			sql.append(KEY_SET);
			Object v;
			for (Entry<String, Object> e : updateColumnsMap.entrySet()) {
				sql.append(e.getKey()).append("=");
				v=e.getValue();
				if (v instanceof String) {
					sql.append("'").append(v).append("'");
				} else if (v instanceof Boolean) {
					sql.append(SqlUtil.processBooleanValueToChar((Boolean)v));
				} else if (v instanceof SqlExpress) {
					sql.append(v);
				} else {
					sql.append(v);
				}
				if (index != size - 1) {
					sql.append(",");
				}
				index++;
			}
		} else if (this.type == TYPE_INSERT) {
			// 插入语句设置
			sql.append(KEY_WHITESPACE).append(String.format(KEY_INSERT_VALUES, processInsertValuesToString()));
		}
		// 判断吃否存在where过滤条件 拼接where后面的查询条件
		if (conditions.size() > 0) {
			sql.append(KEY_WHERE);
			// 循环查询条件
			Condition condition = null;
			for (int i = 0; i < conditions.size(); i++) {
				condition = conditions.get(i);
				sql.append(condition.toSql(dbType, prepared));
				// 判断当前设置是否使用问号占位参数
				if (prepared && condition.getType() == Condition.TYPE_COMPARE
						&& condition.getCompareState().indexOf("in") == -1) {
					whereValues.add(SqlUtil.processBoolean(condition.getValue1()));
				}
			}

		}
		// 判断排序
		if (orderBy != null) {
			sql.append(KEY_ORDERBY).append(orderBy);
			if (desc) {
				sql.append(KEY_DESC);
			}else {
				sql.append(KEY_ASC);
			}
		}
		// 判断分组查询
		if (groupBy != null) {
			sql.append(KEY_GROUPBY);
			sql.append(groupBy);
		}
		// 判断limit
		if (hasPage) {
			sql.append(KEY_WHITESPACE);
			switch (dbType) {
			case JdbcConstants.MYSQL:
				processMysqlPage(sql);
				break;
			case JdbcConstants.ORACLE:
				processOraclePage(sql);
				break;
			case JdbcConstants.SQL_SERVER:
				processSqlserverPage(sql);
				break;
			case JdbcConstants.POSTGRESQL:
				processPostgresqlPage(sql);
				break;
			}
		}
	}


	/**
	 * 处理sqlServer分页
	 * 
	 * @param sql
	 */
	private void processSqlserverPage(StringBuilder sql) {
		if(pageNumber==1&&pageSize==1) {
			return;
		}
		int end = pageNumber * pageSize;
		if (end <= 0) {
			end = pageSize;
		}
		int begin = (pageNumber - 1) * pageSize;
		if (begin < 0) {
			begin = 0;
		}
		String findSql = sql.toString();
		sql.setLength(0);
		sql.append("SELECT ").append(returnColumns).append(" FROM ( SELECT row_number() over (order by tempcolumn) temprownumber, * FROM ");
		sql.append(" ( SELECT TOP ").append(end).append(" tempcolumn=0,");
		sql.append(findSql.replaceFirst("(?i)select", ""));
		sql.append(")vip)mvp where temprownumber>").append(begin);
	}

	/**
	 * 处理oracle分页
	 * 
	 * @param sql
	 */
	private void processOraclePage(StringBuilder sql) {
		int start = (pageNumber - 1) * pageSize;
		int end = pageNumber * pageSize;
		String findSql = sql.toString();
		sql.setLength(0);
		sql.append("select ").append(returnColumns).append(" from ( select row_.*, rownum rownum_ from (  ");
		sql.append(findSql);
		sql.append(" ) row_ where rownum <= ").append(end).append(") table_alias");
		sql.append(" where table_alias.rownum_ > ").append(start);
	}

	/**
	 * 处理mysql分页
	 */
	private void processMysqlPage(StringBuilder sql) {
		int offset = (pageNumber - 1) * pageSize;
		sql.append(KEY_LIMIT).append(offset).append(KEY_COMMA).append(pageSize).append(KEY_WHITESPACE);
	}
	/**
	 * 处理postgresql分页
	 */
	private void processPostgresqlPage(StringBuilder sql) {
		int offset = (pageNumber - 1) * pageSize;
		sql.append(KEY_LIMIT).append(pageSize).append(KEY_OFFSET).append(offset).append(KEY_WHITESPACE);
	}

	/**
	 * 将insertValues转为string 字符串
	 * 
	 * @return
	 */
	private String processInsertValuesToString() {
		if (insertValues == null || insertValues.length == 0) {
			return KEY_NULLSTRING;
		}
		StringBuilder sb = new StringBuilder();
		int len = insertValues.length;
		Object value;
		for (int i = 0; i < len; i++) {
			value = insertValues[i];
			if (value instanceof String) {
				sb.append("'").append(value).append("'");
			} else {
				sb.append(value);
			}
			if (i < len - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return toSql();
	}

	/**
	 * 计数
	 * 
	 * @return
	 */
	public Sql count() {
		this.queryCount = true;
		processReturnColumnsBySelectCount();
		this.select(returnColumns);
		return this;
	}

	private void processReturnColumnsBySelectCount(String column) {
		if(this.queryDistinctCount) {
			returnColumns = returnColumns+","+String.format(KEY_COUNT_COLUMN, column);
		}else {
			returnColumns = String.format(KEY_COUNT_COLUMN, column);
		}
	}
	private void processReturnColumnsBySelectCount() {
		if(this.queryDistinctCount) {
			returnColumns = returnColumns+","+KEY_COUNT_START;
		}else {
			returnColumns = KEY_COUNT_START;
		}
	}

	/**
	 * 对指定列计数
	 * 
	 * @param column
	 * @return
	 */
	public Sql count(String column) {
		this.queryCount = true;
		processReturnColumnsBySelectCount(column);
		this.select(returnColumns);
		return this;
	}

	public Sql distinct(String column) {
		returnColumns = KEY_DISTINCT + column;
		this.select(returnColumns);
		this.orderBy = null;
		return this;
	}

	/**
	 * 不重复列技术
	 * 
	 * @param column
	 * @return
	 */
	public Sql distinctCount(String column) {
		this.queryDistinctCount = true;
		processReturnColumnsBySelectDistinctCount(column);
		this.select(returnColumns);
		return this;
	}

	private void processReturnColumnsBySelectDistinctCount(String column) {
		if(this.queryCount) {
			returnColumns=returnColumns+","+String.format(KEY_COUNT_DISTINCT, column);
		}else {
			returnColumns=String.format(KEY_COUNT_DISTINCT, column);
		}
	}

	public static void main(String[] args) {
		/*
		 * String sql=Sql.oracle().select("jb_user.*").fromSql(Sql.oracle().
		 * select("jb_user2.*,rownum rn").fromSql(Sql.oracle().select("jb_user.*").
		 * from("JB_USER jb_user").gt("jb_user.AGE", 40).lt("jb_user.AGE",
		 * 49).orderBy(KEY_ID,true),"jb_user2"),"jb_user").eq("jb_user.rn", 1).toSql();
		 * System.out.println(sql); System.out.println(
		 * Sql.oracle().select().distinct(KEY_ID).from("goods_category").eq("a",1)
		 * .bracketLeft() .eq("state", 1) .or() .bracketLeft() .eq("age", 10)
		 * .eq("name","alkdj") .gt(KEY_ID, 1) .bracketRight() .like("name", "张三")
		 * .bracketRight() .orderBy("age", true) .groupBy("deptId").page(1, 10) );
		 * Sql.mysql().select().from("goods_category");
		 * System.out.println(Sql.mysql().delete().from("table").eq(KEY_ID, 10));
		 * 
		 * System.out.println(Sql.oracle().select().from("jb_user").like("name",
		 * "user").startWith("name", "u").endWith("name","2").toSql());
		 * 
		 */

		System.out.println(Sql.oracle().select().distinctCount("name").from("jb_user").page(2, 10));
		System.out.println(Sql.oracle().select().count().from("jb_user"));
		System.out.println(Sql.oracle().select().count("name").from("jb_user"));
		System.out.println(Sql.oracle().update("user").set("age", 10).eq("age", 20).noteq(KEY_ID, 2));

		System.out.println("mysql分页查询：" + Sql.mysql().select().from("jb_user").firstPage(1).orderBy("age"));
		System.out.println("oracle分页查询：" + Sql.oracle().select("ju.id,ju.name,ju.age").from("jb_user", "ju")
				.leftJoin("jb_change_log jl on jl.user_id=ju.id").ge("age", 40).orderBy("ju.age", true).page(1, 10));
		System.out.println("sqlserver分页查询：" + Sql.sqlserver().select().from("jb_user").page(1, 1));
		System.out.println(Sql.oracle().count().from("jb_user","c").ge("age", 10));
		System.out.println(Sql.oracle().count(KEY_ID).from("jb_user").ge("age", 10));
		System.out.println(Sql.oracle().distinctCount("age").count(KEY_ID).from("jb_user").ge("age", 10));
		System.out.println(Sql.oracle().selectId().from("jb_user").eqQM("name","age").noteqQM(KEY_ID));
		System.out.println(Sql.oracle().selectId().from("jb_user").eq("viewCount", "viewCount+1"));
		System.out.println(Sql.oracle().selectId().from("jb_user").eq("age", 10).eq("viewCount",new SqlExpress("viewCount+1")).orderBy("id"));
		System.out.println("Mysql IN查询：" + Sql.mysql().select().from("jb_user").in("id", 1,2,3));
		System.out.println("Mysql IN查询：" + Sql.mysql().select().from("jb_user").in("id", "1",2,"3"));
		System.out.println("Mysql IN查询：" + Sql.mysql().select().from("jb_user").in("id", "1,2,3,4"));
		System.out.println("Mysql IN查询：" + Sql.mysql().select().from("jb_user").in("id", new SqlExpress("1,2,3,4")));
		System.out.println("Mysql IN查询：" + Sql.mysql().select().from("jb_user").in("id", "1,2,3,4","4,5"));
		
		
		System.out.println(Sql.oracle().insert("jb_user(id,name,age)").values(1, "张三", 10));
		System.out.println(Sql.oracle().insert("jb_user","id,name,age").values(1, "张三", 10));
		System.out.println(Sql.oracle().insert("jb_user","id,name,age",1, "张三", 10));
		System.out.println(Sql.mysql().update("jb_user").set("is_new", false).eq("is_new", true));
		System.out.println(Sql.oracle().update("jb_user").set("is_new", false).eq("is_new", true));
		System.out.println("Mysql find_in_set:"+Sql.mysql().select().from("jb_user").findInSet(1, "roles", true));
		System.out.println("Oracle find_in_set:"+Sql.oracle().select().from("jb_user").findInSet(1, "roles", true));
		System.out.println("Sqlserver find_in_set:"+Sql.sqlserver().select().from("jb_user").findInSet(1, "roles", true));
		
		System.out.println("sqlserver分页查询：" + Sql.sqlserver().select().from("jb_user").first());
		System.out.println("sqlserver分页查询：" + Sql.sqlserver().select().from("jb_user").firstPage(20));
		System.out.println("sqlserver分页查询：" + Sql.sqlserver().select().from("jb_user").page(2, 100));
		System.out.println("=====================分隔=============================");
		System.out.println(Sql.mysql().select().from("jb_user").eq("id", 10).prepared());
		System.out.println(Sql.oracle().select().from("jb_user").eq("id", 10).eq("is_new",true));
		System.out.println(Sql.mysql().select().from("jb_user").eq("id", 10).eq("is_new",true));
		System.out.println(Sql.sqlserver().select().from("jb_user").eq("id", 10).eq("is_new",true));
		System.out.println(Sql.postgresql().select().from("jb_user").eq("id", 10).eq("is_new",true));
		System.out.println("pgsql分页"+Sql.postgresql().select().from("jb_user").eq("id", 10).eq("is_new",true).page(2, 4));
		
		
		System.out.println("pgsql分页+like"+Sql.postgresql().select().distinct(KEY_ID).from("goods_category").eq("a",1)
		 .bracketLeft() .eq("state", 1) .or() .bracketLeft() .eq("age", 10)
		 .eq("name","alkdj") .gt(KEY_ID, 1) .bracketRight() .like("name", "张三")
		 .bracketRight() .orderBy("age", true) .groupBy("deptId").page(1, 10).prepared() );
		System.out.println("=====================sql注入测试=============================");
		String sqlvalue="a' or id>'0";
		System.out.println(Sql.postgresql().select().from("jb_user").eq("id", sqlvalue).eq("is_new",true));
		System.out.println(Sql.oracle().count().from("jb_user","c").ge("age", sqlvalue));
	}

	public String getCountColumns() {

		return countColumns;
	}

	public String getReturnColumns() {

		return returnColumns;
	}

	public void setCountColumns(String countColumns) {
		this.countColumns = countColumns;
	}

	public boolean hasTable() {
		return table != null && table.trim() != "";
	}

	/**
	 * 得到参数集合
	 * 
	 * @return
	 */
	public Object[] getWhereValues() {
		if (prepared && whereValues != null && whereValues.size() > 0) {
			return whereValues.toArray();
		}
		return Collections.EMPTY_LIST.toArray();
	}

	public boolean isPrepared() {
		return prepared;
	}

	public boolean isQueryColumnList() {
		return queryColumnList;
	}

	public boolean isQueryCount() {
		return queryCount;
	}

	public boolean isQueryMax() {
		return queryMax;
	}

	public boolean isDelete() {
		return type == TYPE_DELETE;
	}

	public boolean isQuery() {
		return type == TYPE_SELECT;
	}

	public boolean isUpdate() {
		return type == TYPE_UPDATE;
	}

	public boolean isQueryDistinctCount() {
		return queryDistinctCount;
	}

	/**
	 * 生成不带select的sql
	 * 
	 * @return
	 */
	public String toSqlExceptSelect() {
		StringBuilder sql = new StringBuilder();
		builderSql(sql, true);
		return sql.toString();
	}

	/**
	 * 得到select部分
	 * 
	 * @return
	 */
	public String getSelect() {
		StringBuilder select = new StringBuilder();
		select.append(KEY_SELECT).append(KEY_WHITESPACE).append(returnColumns);
		return select.toString();
	}

	/**
	 * 切换dbType为mysql
	 * 
	 * @return
	 */
	public static Sql mysql() {
		return me(JdbcConstants.MYSQL);
	}
	/**
	 * 切换dbType为postgresql
	 * 
	 * @return
	 */
	public static Sql postgresql() {
		return me(JdbcConstants.POSTGRESQL);
	}

	/**
	 * 切换dbType为oracle
	 * 
	 * @return
	 */
	public static Sql oracle() {
		return me(JdbcConstants.ORACLE);
	}

	/**
	 * 切换dbType为sqlserver
	 * 
	 * @return
	 */
	public static Sql sqlserver() {
		return me(JdbcConstants.SQL_SERVER);
	}

	/**
	 * 设置数据库类型
	 * 
	 * @param dbType
	 * @return
	 */
	public Sql setDbType(String dbType) {
		this.dbType = dbType;
		return this;
	}

	public Object[] getInsertValues() {
		return insertValues;
	}

	public void setInsertValues(Object[] insertValues) {
		this.insertValues = insertValues;
	}
	/**
	 * 如果是pgsql 处理id正序排序
	 * @return
	 */
	public Sql orderByIdAscIfPgSql() {
		if(dbType.equals(JdbcConstants.POSTGRESQL)) {
			orderById();
		}
		return this;
	}
	/**
	 * 如果是pgsql 处理id倒序排序
	 * @return
	 */
	public Sql orderByIdDescIfPgSql() {
		if(dbType.equals(JdbcConstants.POSTGRESQL)) {
			orderById(true);
		}
		return this;
	}

}
