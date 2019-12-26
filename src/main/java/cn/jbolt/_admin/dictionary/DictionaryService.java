package cn.jbolt._admin.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.model.Dictionary;
import cn.jbolt.common.model.DictionaryType;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.ListMap;
/**
 * 字典Service
 * @ClassName:  DictionaryService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class DictionaryService extends BaseService<Dictionary>{
	@Inject
	private DictionaryTypeService dictionaryTypeService;
	private Dictionary dao=new Dictionary().dao();
	@Override
	protected Dictionary dao() {
		return dao;
	}
	/**
	 * 根据类型获得数据字典数据列表
	 * @param type
	 * @return
	 */
	public List<Dictionary> getListByType(Integer type) {
		return getListByType(type, true);
	}
	/**
	 * 根据类型获得数据字典数据列表
	 * @param type
	 * @param needSubItems
	 * @return
	 */
	public List<Dictionary> getListByType(Integer type,boolean needSubItems) {
		return getListByType(type, needSubItems, null);
	}
	/**
	 * 根据类型获得数据字典数据列表
	 * @param type
	 * @param needSubItems
	 * @param exceptIds 指定ID数组 排除
	 * @return
	 */
	public List<Dictionary> getListByType(Integer type,boolean needSubItems,int[] exceptIds) {
		if(notOk(type)){
			return Collections.emptyList();
		}
		Kv paras=Kv.by("type_id=", type);
		if(!needSubItems){
			//如果不需要下级 就只读取上级
			paras.set("pid=",0);
		}
		if(exceptIds!=null&&exceptIds.length>0) {
			for(int i=0;i<exceptIds.length;i++) {
				paras.set("id!=",exceptIds[i]);
			}
		}
		List<Dictionary> dictionarys=getCommonList(paras, "sort_rank", "asc", true);
		if(needSubItems){
			return processSubItems(dictionarys);
		}
		return dictionarys;
	}
	
	/**
	 * 根据类型和父id获得数据字典数据列表
	 * @param type
	 * @param pid
	 * @return
	 */
	public List<Dictionary> getSonListByType(Integer type,Integer pid) {
		if(notOk(type)||notOk(pid)){
			return Collections.emptyList();
		}
		Kv paras=Kv.by("type_id", type);
		paras.set("pid", pid);
		return getCommonList(paras);
	}
	/**
	 * 处理父子关系
	 * @param dictionarys
	 * @return
	 */
	private List<Dictionary> processSubItems(List<Dictionary> dictionarys) {
		List<Dictionary> submitItems=new ArrayList<Dictionary>();
		for(Dictionary dictionary:dictionarys){
			if(notOk(dictionary.getPid())){
				submitItems.add(dictionary);
			}
		}
		if(submitItems.size()>0){
			ListMap<String, Dictionary> map=new ListMap<String, Dictionary>();
			for(Dictionary dictionary:dictionarys){
				if(isOk(dictionary.getPid())){
					map.addItem("dic_"+dictionary.getPid(), dictionary);
				}
			}
			for(Dictionary dictionary:submitItems){
				List<Dictionary> items=map.get("dic_"+dictionary.getId());
				if(items!=null&&items.size()>0){
					dictionary.put("items",processSubItems(map,items));
				}
			}
		}
		return submitItems;
	}
	private List<Dictionary> processSubItems(ListMap<String, Dictionary> map,List<Dictionary> submitItems){
		for(Dictionary dictionary:submitItems){
			List<Dictionary> items=map.get("dic_"+dictionary.getId());
			if(items!=null&&items.size()>0){
				dictionary.put("items",processSubItems(map,items));
			}
		}
		return submitItems;
	}
	/**
	 * 根据ID删除字典数据
	 * @param id
	 * @return
	 */
	public Ret deleteDictionaryById(Integer userId,Integer id) {
		Ret ret=deleteById(id,true);
		if(ret.isOk()){
			//删除子数据以及缓存
			deleteByPid(id);
			Dictionary dictionary=ret.getAs("data");
			//删除后需要把此数据之后的数据更新顺序
			updateSortRankAfterDelete(Kv.by("type_id", dictionary.getTypeId()).set("pid",dictionary.getPid()),dictionary.getSortRank());
			//增加系统日志
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_DICTIONARY, dictionary.getName());
		}
		return ret;
	}
	
	
	
	/**
	 * 添加多个
	 * @param userId
	 * @param typeId
	 * @param pid
	 * @param names
	 * @param sns
	 * @return
	 */
	private Ret saveAll(Integer userId,Integer typeId,Integer pid, String[] names, String[] sns) {
		Ret ret=null; 
		int size=names.length;
		int snSize=0;
		if(isOk(sns)) {
			snSize=sns.length;
		}
		if(snSize>0&&snSize!=size) {
			return fail("名称和编号数量不一致，请检查");
		}
		Dictionary dictionary;
		for(int i=0;i<size;i++){
			dictionary=new Dictionary().setTypeId(typeId).setPid(pid).setName(names[i]);
			if(snSize==size) {
				dictionary.setSn(sns[i]);
			}
			ret=save(userId, dictionary);
			if(ret.isFail()){
				return ret;
			}
		 }
		return SUCCESS;
	}
	
	/**
	 * 保存字典数据
	 * @param dictionary
	 * @return
	 */
	public Ret save(Integer userId,Dictionary dictionary) {
		if(dictionary==null||isOk(dictionary.getId())||notOk(dictionary.getName())||notOk(dictionary.getTypeId())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=dictionary.getName().trim();
		String sn=dictionary.getSn();
		if(name.indexOf(" ")!=-1){
			if(sn!=null) {
				sn=sn.trim();
			}
			return saveAll(userId,dictionary.getTypeId(),dictionary.getPid(),ArrayUtil.from3(name, " "),ArrayUtil.from3(sn, " "));
		}
		if (existName(-1,name,dictionary.getTypeId())) {
			return fail("此名称["+name+"]已经存在，请更正");
		}
		if (isOk(sn)&&existSn(-1,sn,dictionary.getTypeId())) {
			return fail("此编号["+sn+"]已经存在，请更正");
		}
		dictionary.setName(name);
		if(isOk(sn)) {
			dictionary.setSn(sn);
		}
		if(dictionary.getPid()==null){
			dictionary.setPid(0);
		}
		dictionary.setSortRank(getNextSortRank(Kv.by("type_id", dictionary.getTypeId()).set("pid",dictionary.getPid())));
		boolean success=dictionary.save();
		if(success){
			//增加系统日志
			addSaveSystemLog(dictionary.getId(), userId, SystemLog.TARGETTYPE_DICTIONARY, dictionary.getName());
		}
		return ret(success);
	}
	
	/**
	 * 修改字典数据
	 * @param dictionary
	 * @return
	 */
	public Ret update(Integer userId,Dictionary dictionary) {
		if(dictionary==null||notOk(dictionary.getId())||notOk(dictionary.getName())||notOk(dictionary.getTypeId())){
			return fail(Msg.PARAM_ERROR);
		}
		if(dictionary.getPid()==null){
			dictionary.setPid(0);
		}else if(dictionary.getPid().intValue()==dictionary.getId()) {
			return fail("上级不能是自己，请更正");
		}
		String name=dictionary.getName().trim();
		if (existName(dictionary.getId(),name,dictionary.getTypeId())) {
			return fail("此名称["+name+"]已经存在，请更正");
		}
		String sn=dictionary.getSn().trim();
		if (isOk(sn)&&existSn(dictionary.getId(),sn,dictionary.getTypeId())) {
			return fail("此编号["+sn+"]已经存在，请更正");
		}
		dictionary.setName(name);
		dictionary.setSn(sn);
		boolean success=dictionary.update();
		if(success){
			//增加系统日志
			Boolean sort=dictionary.getBoolean("sort");
			if(isOk(sort)&&sort) {
				addUpdateSystemLog(dictionary.getId(), userId, SystemLog.TARGETTYPE_DICTIONARY, dictionary.getName(),"的顺序");
			}else {
				addUpdateSystemLog(dictionary.getId(), userId, SystemLog.TARGETTYPE_DICTIONARY, dictionary.getName());
			}
			
		}
		return ret(success);
	}
	
	
	/**
	 * 判断字典名是否存在
	 * @param id
	 * @param name
	 * @param type
	 * @return
	 */
	public boolean existName(Integer id,String name,Integer type) {
		name = name.toLowerCase().trim();
		Sql sql=selectSql().selectId().eqQM("name","type_id").idNoteqQM().first();
		Integer existId = queryInt(sql, name,type,id);
		return isOk(existId);
	}
	/**
	 * 判断字典sn编号是否存在
	 * @param id
	 * @param sn
	 * @param type
	 * @return
	 */
	public boolean existSn(Integer id,String sn,Integer type) {
		sn = sn.toLowerCase().trim();
		Sql sql=selectSql().selectId().eqQM("sn","type_id").idNoteqQM().first();
		Integer existId = queryInt(sql, sn,type,id);
		return isOk(existId);
	}
	/**
	 * 根据Type查询符合条件的个数
	 * @param type
	 * @return
	 */
	public int getCountByType(Integer type) {
		if(notOk(type)){return 0;}
		return getCount(Kv.by("type_id", type));
	}

	/**
	 * 根据类型获得字典数据 
	 * @return
	 */
	public List<Dictionary> getOptionListByType(Integer type,boolean needAll) {
		List<Dictionary> dictionaries=getListByType(type);
		if(needAll){
			dictionaries.add(0, new Dictionary().set("id", 0).set("name", "全部"));
		}
		return dictionaries;
	}
	public String getCacheNames(String ids) {
		if(notOk(ids)){return "未设置";}
		String[] idsArray=ArrayUtil.from(ids, ",");
		if(idsArray==null||idsArray.length==0){return "未设置";}
		StringBuilder sb=new StringBuilder();
		int index=0;
		for(String id:idsArray){
			if(isOk(id)){
				Dictionary dictionary=CACHE.me.getDictionary(Integer.parseInt(id));
				if(dictionary!=null){
					if(isOk(dictionary.getPid())&&CACHE.me.getDictionaryTypeModeLevel(dictionary.getTypeId())==DictionaryType.MODE_LEVEL_MUTIL){
						String pname=CACHE.me.getDictionaryName(dictionary.getPid());
						sb.append((StrKit.notBlank(pname)?(pname+"-"):"")+dictionary.getName());
					}else{
						sb.append(dictionary.getName());
					}
					if(index<idsArray.length-1){
						sb.append("，");
					}
					
				}
				 
			}
			index++;
		}
		return sb.toString();
	}
	public boolean checkTypeInUse(Integer typeId) {
		int count=getCountByType(typeId);
		return count>0;
	}
	/**
	 * 根据类型获得字典数据 
	 * @return
	 */
	public List<Dictionary> getOptionListByType(String typeKey) {
		DictionaryType dictionaryType=CACHE.me.getDictionaryType(typeKey);
		if(dictionaryType==null){return Collections.emptyList();}
		return getListByType(dictionaryType.getId());
	}
	/**
	 * 根据类型获得字典数据_只获得一级
	 * @param typeKey
	 * @return
	 */
	public List<Dictionary> getRootOptionListByType(String typeKey) {
		DictionaryType dictionaryType=CACHE.me.getDictionaryType(typeKey);
		if(dictionaryType==null){return Collections.emptyList();}
		return getListByType(dictionaryType.getId(),false);
	}
	
	/**
	 * 根据类型Key和PID获得字典数据 
	 * @param typeKey
	 * @param pid
	 * @return
	 */
	public List<Dictionary> getSonOptionListByType(String typeKey,Integer pid) {
		if(notOk(pid)) {return Collections.emptyList();}
		DictionaryType dictionaryType=CACHE.me.getDictionaryType(typeKey);
		if(dictionaryType==null){return Collections.emptyList();}
		return getSonListByType(dictionaryType.getId(),pid);
	}
	/**
	 * 检测是否被其他模块使用
	 */
	@Override
	public String checkInUse(Dictionary m) {
		//TODO 检测
		return null;
	}
	/**
	 * 上移
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret doUp(Integer userId, Integer id) {

		Dictionary dictionary=findById(id);
		if(dictionary==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		Integer rank=dictionary.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		if(rank==1){
			return fail("已经是第一个");
		}
		Dictionary upDictionary=findFirst(Kv.by("sort_rank", rank-1).set("type_id", dictionary.getTypeId()) .set("pid", dictionary.getPid()));
		if(upDictionary==null){
			return fail("顺序需要初始化");
		}
		upDictionary.setSortRank(rank);
		dictionary.setSortRank(rank-1);
		upDictionary.put("sort", true);
		dictionary.put("sort", true);
		update(userId,upDictionary);
		update(userId,dictionary);
		return SUCCESS;
	
	}
	
	/**
	 * 下移
	 * @param id
	 * @return
	 */
	public Ret doDown(Integer userId,Integer id) {
		Dictionary dictionary=findById(id);
		if(dictionary==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		Integer rank=dictionary.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		int max=getCount(Kv.by("pid",dictionary.getPid()));
		if(rank==max){
			return fail("已经是最后已一个");
		}
		Dictionary upDictionary=findFirst(Kv.by("sort_rank", rank+1).set("type_id", dictionary.getTypeId()).set("pid", dictionary.getPid()));
		if(upDictionary==null){
			return fail("顺序需要初始化");
		}
		upDictionary.setSortRank(rank);
		dictionary.setSortRank(rank+1);
		upDictionary.put("sort", true);
		dictionary.put("sort", true);
		update(userId,upDictionary);
		update(userId,dictionary);
		return SUCCESS;
	}
 
	/**
	 * 初始化排序
	 * @param userId 
	 */
	public Ret doInitRank(Integer userId,Integer typeId){
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType==null) {
			return fail(Msg.DATA_NOT_EXIST);
		}
		List<Dictionary> parents=getCommonList(Kv.by("type_id", typeId).set("pid",0));
		if(parents.size()>0){
			for(int i=0;i<parents.size();i++){
				parents.get(i).setSortRank(i+1);
				deleteCacheById(parents.get(i).getId());
				List<Dictionary> sons=getCommonList(Kv.by("type_id", typeId).set("pid",parents.get(i).getId()));
				if(sons.size()>0){
					for(int k=0;k<sons.size();k++){
						sons.get(k).setSortRank(k+1);
						deleteCacheById(sons.get(k).getId());
					}
					Db.batchUpdate(sons, sons.size());
				}
			}
			Db.batchUpdate(parents, parents.size());
			 
			//添加日志
			addUpdateSystemLog(null, userId, SystemLog.TARGETTYPE_DICTIONARY, "全部","初始化顺序");
		}
		return SUCCESS;
		
	}
	/**
	 * 获取不包含自己的列表
	 * @param typeId
	 * @param exceptId
	 * @return
	 */
	public List<Dictionary> getListByTypeWithoutSelf(Integer typeId, Integer exceptId) {
		if(isOk(exceptId)) {
			return getListByType(typeId, true,new int[] {exceptId});
		}
		return getListByType(typeId);
	}
	
	 
 

}
