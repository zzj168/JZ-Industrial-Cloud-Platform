package cn.jbolt.common.gen;

import java.util.function.Predicate;

import cn.jbolt.common.util.ArrayUtil;

/**
 * JBolt项目生成器配置
 * @ClassName:  JBoltProjectGenConfig   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月10日   
 */
public class JBoltProjectGenConfig {
	public static final String rootPath=System.getProperty("user.dir");
	public static final String projectType="maven";//项目类型 normalormaven
	public static String dbType=null;//数据库类型 需要读取文件
	public static String jdbcUrl=null;//jdbcURl 需要读取文件
	public static final String mainPkg="cn.jbolt";//主包
	public static final String modelPackageName="cn.jbolt.common.model";//model生成的包
	public static final String baseModelPackageName="cn.jbolt.common.model.base";//basemodel生成的包
	public static final boolean generateDaoInModel=false;//是否dao生成在Model中
	public static final boolean generateChainSetterInBaseModel=true;
	public static final boolean generateDataDictionary=true;
	public static final String removedTableNamePrefixes="jb_";//生成的Model 去掉前缀
	public static final boolean charToBoolean=true;//生成时 char(1)转Boolean
	public static final boolean baseModelExtendsJBoltBaseModel=true;//继承JBoltBaseModel
	public static final String modelOutputDir=rootPath+"/src/main/java/cn/jbolt/common/model";//model输出路径
	public static final String baseModelOutputDir=rootPath+"/src/main/java/cn/jbolt/common/model/base";//base model输出路径
	/**
	 * 过滤规则
	 */
	public static Predicate<String> filterPredicate=new Predicate<String>() {
		@Override
		public boolean test(String tableName) {
			tableName=tableName.toLowerCase();
			return tableNamesInFiterList(tableName)||tableNamesIndexOf(tableName)||tableNamesMatch(tableName);
		}
	};
	/**
	 * 初始化数据库配置
	 * @param dbType
	 * @param jdbcUrl
	 */
	public static void initDbConfig(String dbType,String jdbcUrl) {
		JBoltProjectGenConfig.dbType=dbType;
		JBoltProjectGenConfig.jdbcUrl=jdbcUrl;
	}
	/**
	 * 判断表名是否包含指定自定义字符串
	 * @param tableName
	 * @return
	 */
	public static boolean tableNamesIndexOf(String tableName) {
		boolean in=false;
		for(String str:tableNamesIndexOfStr) {
			if(tableName.indexOf(str)!=-1) {
				in=true;
				break;
			}
		}
		return in;
	}
	/**
	 * 判断表名符合正则
	 * @param tableName
	 * @return
	 */
	public static boolean tableNamesMatch(String tableName) {
		boolean in=false;
		for(String p:tableNamesPatterns) {
			if(tableName.matches(p)) {
				in=true;
				break;
			}
		}
		return in;
	}
	/**
	 * 判断是否在完整过滤表名中存在
	 * @param tableName
	 * @return
	 */
	public static boolean tableNamesInFiterList(String tableName) {
		return ArrayUtil.contains(filterTableNames, tableName);
	}
	/**
	 * 不需要生成的表名（全部小写），直接完整的表名，一般都是一些数据库内置表什么的
	 * 这里默认给了几个，需要的自己加
	 */
	public static final String[] filterTableNames=new String[] {
			"dept","emp","salgrade","bonus","dtproperties"
	};
	/**
	 * 这里是判断表名中有包含以下字符串的需要过滤掉
	 * 一些分表不需要生成 例如jb_wechat_user_
	 * 一些sqlite中的临时表和内置不需要
	 */
	public static final String[] tableNamesIndexOfStr=new String[] {
			"sqlite_","_old_"	
	};
	/**
	 * 这里是判断表名符合的正则表达式
	 */
	public static final String[] tableNamesPatterns=new String[] {
			"jb_wechat_user_-?[1-9]\\d*"
	};
}
