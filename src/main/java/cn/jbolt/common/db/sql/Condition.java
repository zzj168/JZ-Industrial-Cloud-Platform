package cn.jbolt.common.db.sql;

import com.alibaba.druid.util.JdbcConstants;

import cn.jbolt.common.util.ArrayUtil;
/**
  * 查询条件 用于构造sql语句中的查询条件
 * @ClassName:  Condition   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月5日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class Condition {
	
	public static final String EQ="=";
	public static final String NOT_EQ="!=";
	public static final String GT=">";
	public static final String LT="<";
	public static final String GE=">=";
	public static final String LE="<=";

	public static final String LIKE="like";
	public static final String NOTLIKE="not like";
	public static final String STARTWITH="startwith";
	public static final String NOT_STARTWITH="not_startwith";
	public static final String ENDWITH="endwith";
	public static final String NOT_ENDWITH="not_endwith";
	public static final String KEY_WHITESPACE = " ";
	public static final String KEY_PERCENT = "%";
	public static final String KEY_SINGLE_QUOTATION_MARK = "'";
	public static final String KEY_IS_NULL = " is null";

	public static final String OR="or";
	public static final String AND="and";

	public static final String BRACKET_LEFT="(";
	public static final String BRACKET_RIGHT=")";
	
	public static final int TYPE_COMPARE=1;//普通比较
	public static final int TYPE_COMPARE_IN=2;//in
	public static final int TYPE_LINK=3;//普通字符连接
	public static final int TYPE_COMPARE_FINDINSET=4;//普通字符连接
	public static final int TYPE_COMPARE_ISNULL=5;//比较 is null
	
	private String key;//
	private Object value1;//左边
	private Object value2;//右边
	private String likeValue;
	private Object[] inValues;
	private String inSql;
	private String compareState;
	private int type;
	private String dbType;
	private boolean findInSetValueIsTableColumn;

	public Condition() {

	}

	public Condition(String key, Object value,
			String compareState) {
	    setKey(key);
		this.value1 = SqlUtil.processBoolean(value);
		this.compareState = compareState;
		this.type=TYPE_COMPARE;
	}

	public Condition(String key, Object value1,
			Object value2, String compareState) {
		setKey(key);
		this.value1 = value1;
		this.value2 = value2;
		this.compareState = compareState;
		this.type=TYPE_COMPARE;
	}
	/**
	 * 转换
	 * @param dbType
	 * @return
	 */
	public String toSql(String dbType){
		return toSql(dbType, false);
	}
	/**
	 * 转换
	 * @param dbType
	 * @param prepared
	 * @return
	 */
	public String toSql(String dbType,boolean prepared){
		this.dbType=dbType;
		String sql=null;
		if(type==TYPE_COMPARE){
			//判断处理特殊查询条件
			switch (compareState) {
			case LIKE:
				sql = processLikeConditions(prepared,false);
				break;
			case STARTWITH:
				sql = processStartWithConditions(prepared,false);
				break;
			case ENDWITH:
				sql = processEndWithConditions(prepared,false);
				break;
			case NOTLIKE:
				sql = processLikeConditions(prepared,true);
				break;
			case NOT_STARTWITH:
				sql = processStartWithConditions(prepared,true);
				break;
			case NOT_ENDWITH:
				sql = processEndWithConditions(prepared,true);
				break;
			default:
				if(value1 instanceof String){
					sql=key+compareState+(prepared?"?":(value1.equals("?")?"?":safeValue(value1.toString())));
				}else if(value1 instanceof Boolean){
					sql=key+compareState+(prepared?"?":SqlUtil.processBooleanValueToChar((Boolean)value1));
				}else{
					sql=key+compareState+(prepared?"?":value1);
				}
				break;
			}
		}else if(type==TYPE_LINK){
			sql=KEY_WHITESPACE+value1+KEY_WHITESPACE;
		}else if(type==TYPE_COMPARE_FINDINSET){
			sql=processFindInSetSql();
		}else if(type==TYPE_COMPARE_ISNULL){
			sql=processIsNullSql();
		}else if(type==TYPE_COMPARE_IN){
			if(inSql!=null){
				sql=KEY_WHITESPACE+key+KEY_WHITESPACE+"in("+inSql+") ";
			}else{
				sql=KEY_WHITESPACE+key+KEY_WHITESPACE+"in(";
				Object inv;
				int len=inValues.length;
				for(int i=0;i<len;i++){
					inv=inValues[i];
					if(inv instanceof SqlExpress) {
						sql+=inv;
					}else {
						sql+="'"+inv+"'";
					}
					
					if(i!=inValues.length-1){
						sql+=",";
					}
				}
				sql+=")"+KEY_WHITESPACE;
			}
			
		}
		return sql;
	}
	
	private String safeValue(String value) {
		if(value.indexOf("'")!=-1) {
			value=value.replace("'", "''");
		}
		return "'"+value+"'";
	}

	/**
	 * 处理IS NULL 语句
	 * @return
	 */
	private String processIsNullSql() {
		return value1+KEY_IS_NULL;
	}
	/**
	 * 处理FIND_IN_SET 语句
	 * @return
	 */
	private String processFindInSetSql() {
		return " find_in_set("+key+","+(findInSetValueIsTableColumn?value1:("'"+value1+"'"))+")>0 ";
	}
	/**
	 * 处理 like
	 * @param prepared
	 * @return
	 */
	private String processLikeConditions(boolean prepared,boolean not) {
		processLikeNot(not);
		//根据数据库类型判断
		if(prepared) {
			switch (dbType) {
			case JdbcConstants.MYSQL:
				likeValue="concat('%',?,'%')";
				break;
			case JdbcConstants.POSTGRESQL:
				likeValue="'%'|| ? ||'%'";
				break;
			case JdbcConstants.ORACLE:
				likeValue="'%'|| ? ||'%'";
				break;
			case JdbcConstants.SQL_SERVER:
				likeValue="'%' + ? + '%'";
				break;
			}
		}else {
			likeValue="'%"+value1+"%'";
		}
		return KEY_WHITESPACE+key+KEY_WHITESPACE+compareState+KEY_WHITESPACE+likeValue+KEY_WHITESPACE;
	}
	/**
	 * 处理 startWith
	 * @param prepared
	 * @return
	 */
	private String processStartWithConditions(boolean prepared,boolean not) {
		processLikeNot(not);
		//根据数据库类型判断
		if(prepared) {
			switch (dbType) {
			case JdbcConstants.MYSQL:
				likeValue="concat(?,'%')";
				break;
			case JdbcConstants.POSTGRESQL:
				likeValue=" ? ||'%'";
				break;
			case JdbcConstants.ORACLE:
				likeValue=" ? ||'%'";
				break;
			case JdbcConstants.SQL_SERVER:
				likeValue=" ? + '%'";
				break;
			}
		}else {
			likeValue= "'"+value1+"%'";
		}
		return KEY_WHITESPACE+key+KEY_WHITESPACE+compareState+KEY_WHITESPACE+likeValue+KEY_WHITESPACE;
	}
	/**
	 * 处理 endWith
	 * @param prepared
	 * @return
	 */
	private String processEndWithConditions(boolean prepared,boolean not) {
		processLikeNot(not);
		//根据数据库类型判断
		if(prepared) {
			switch (dbType) {
			case JdbcConstants.MYSQL:
				likeValue="concat('%',?)";
				break;
			case JdbcConstants.POSTGRESQL:
				likeValue="'%'|| ?";
				break;
			case JdbcConstants.ORACLE:
				likeValue="'%'|| ?";
				break;
			case JdbcConstants.SQL_SERVER:
				likeValue="'%'+ ?";
				break;
			}
		}else {
			likeValue= "'%"+value1+"'";
		}
		return KEY_WHITESPACE+key+KEY_WHITESPACE+compareState+KEY_WHITESPACE+likeValue+KEY_WHITESPACE;
	}

	private void processLikeNot(boolean not) {
		if(not) {
			compareState=NOTLIKE;
		}else {
			compareState=LIKE;
		}
	}
	public Object getValue1() {
		return value1;
	}

	public void setValue1(Object value1) {
		this.value1 = value1;
	}

	public Object getValue2() {
		return value2;
	}

	public void setValue2(Object value2) {
		this.value2 = value2;
	}

	public String getCompareState() {
		return compareState;
	}

	public void setCompareState(String compareState) {
		this.compareState = compareState;
	}


	/**
	 * column is null
	 * @return
	 */
	public Condition isNull(String columnName){
		this.value1=columnName;
		this.type=TYPE_COMPARE_ISNULL;
		return this;
	}
	/**
	 * 左括号
	 * @return
	 */
	public Condition bracketLeft(){
		this.value1=BRACKET_LEFT;
		this.type=TYPE_LINK;
		return this;
	}
	/**
	 * 右括号
	 * @return
	 */
	public Condition bracketRight(){
		this.value1=BRACKET_RIGHT;
		this.type=TYPE_LINK;
		return this;
	}

	public Condition or(){
		this.value1=OR;
		this.type=TYPE_LINK;
		return this;
	}
	public Condition and(){
		this.value1=AND;
		this.type=TYPE_LINK;
		return this;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Condition findInSet(Object key, String value,boolean valueIsTableColumn) {
		setKey("'"+key+"'");
		this.type=TYPE_COMPARE_FINDINSET;
		this.value1=value;
		this.findInSetValueIsTableColumn=valueIsTableColumn;
		return this;
	}
	public Condition in(String key, Object... inValues) {
		setKey(key);
		this.type=TYPE_COMPARE_IN;
		this.inValues=inValues;
		return this;
	}
	public Condition in(String key, String inValues) {
		setKey(key);
		this.type=TYPE_COMPARE_IN;
		this.inValues=ArrayUtil.from(inValues, ",");
		return this;
	}
	public Condition inSql(String key, String inSql) {
		setKey(key);
		this.type=TYPE_COMPARE_IN;
		this.inSql=inSql;
		return this;
	}

	public Object[] getInValues() {
		return inValues;
	}

	public void setInValues(Object[] inValues) {
		this.inValues = inValues;
	}

	public String getInSql() {
		return inSql;
	}

	public void setInSql(String inSql) {
		this.inSql = inSql;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}




}
