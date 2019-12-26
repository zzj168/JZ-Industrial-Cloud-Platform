package cn.jbolt.admin.mall.goodscategory.back;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.admin.mall.goods.GoodsService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.bean.JsTreeBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.GoodsBackCategory;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;

/**   
 * 商品类目管理
 * @ClassName:  GoodsBackCategoryService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午3:19:29   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GoodsBackCategoryService extends BaseService<GoodsBackCategory> {
	private GoodsBackCategory dao = new GoodsBackCategory().dao();
	@Inject
	private GoodsService goodsService;
	@Override
	protected GoodsBackCategory dao() {
		return dao;
	}
	
	/**
	 * 检测上配类型有没有被使用
	 * @param typeId
	 * @return
	 */
	public boolean checkTypeInUse(Integer typeId) {
		return exists("type_id", typeId);
	}
	/**
	 * 检测自身是否被其他模块使用
	 */
	@Override
	public String checkInUse(GoodsBackCategory category) {
		boolean hasItems=exists("pid", category.getId());
		if(hasItems){return "当前分类下有子类目";}
		
		boolean goodsInUse=goodsService.checkGoodsBackCategoryInUse(category.getId());
		if(goodsInUse){return "当前分类下已经存在商品信息";}
		return null;
	}
	/**
	 * 根据PID获取分类数据
	 * @param pid
	 * @param enable 启用禁用状态
	 * @return
	 */
	public List<GoodsBackCategory> getCategorysByPid(Integer pid,Boolean enable) {
		Kv kv=Kv.by("pid", pid);
		if(enable!=null){
			kv.set("enable",enable);
		}
		return getCommonList(kv,"sort_rank","asc");
	}	
	/**
	 * 根据PID获取分类数据 后端启用的数据
	 * @param pid
	 * @return
	 */
	public List<GoodsBackCategory> getEnableListByPid(Integer pid) {
		return getCategorysByPid(pid,true);
	}	
	/**
	 * 获取分类数据
	 * @param enable 启用禁用状态
	 * @return
	 */
	public List<GoodsBackCategory> getCategorys(Boolean enable) {
		Kv kv=Kv.create();
		if(enable!=null){
			kv.set("enable",enable);
		}
		return getCommonList(kv,"sort_rank","asc");
	}	
	
	/**
	 * 获取分类数据中启用的后端分类数据
	 * @return
	 */
	public List<GoodsBackCategory> getEnableList() {
		return getCategorys(true);
	}
	/**
	 * 获取分类数据中的所有后端分类数据
	 * @return
	 */
	public List<GoodsBackCategory> getMgrList() {
		return getCategorys(null);
	}
	
	
	  

	/**
	 * 得到后台分类启动后的数据树
	 * @param checkedId 默认选中节点
	 * @return
	 */
	public List<JsTreeBean> getEnableTree(Integer checkedId) {
		List<GoodsBackCategory> goodsCategories=getEnableList();
		return convertJsTreeDatas(goodsCategories,checkedId);
	}
	/**
	 * 得到后台分类数据树 包含所有数据
	 * @param checkedId 默认选中节点
	 * @return
	 */
	public List<JsTreeBean> getMgrTree(Integer checkedId) {
		List<GoodsBackCategory> goodsCategories=getMgrList();
		return convertJsTreeDatas(goodsCategories,checkedId);
	}
	
	
	private List<JsTreeBean> convertJsTreeDatas(List<GoodsBackCategory> goodsBackCategories,Integer selectedId) {
		List<JsTreeBean> treeBeans=new ArrayList<JsTreeBean>();
		treeBeans.add(new JsTreeBean(0, 0, "全部分类", true,"defaults"));
		if(goodsBackCategories.size()>0){
			String[] keys=null;
			if(isOk(selectedId)){
				GoodsBackCategory checkedCategory=findById(selectedId);
				String key=checkedCategory.getCategoryKey();
				keys=ArrayUtil.from(key, "_");
			}
			for(GoodsBackCategory goodsCategory:goodsBackCategories){
				boolean opened=false;
				boolean selected=false;
				String type="default";
				
				if(ArrayUtil.contains(keys, goodsCategory.getId())){
					selected=false;
					opened=true;
				}
				if(selectedId!=null&&selectedId>0&&goodsCategory.getId().intValue()==selectedId.intValue()){
					selected=true;
					opened=true;
				}
				treeBeans.add(convertToJsTreeBean(goodsCategory,opened,selected,type));
			}
		}
		return treeBeans;
	}
	
	private JsTreeBean convertToJsTreeBean(GoodsBackCategory goodsBackCategory,boolean opened,boolean selected,String type) {
		return new JsTreeBean(goodsBackCategory.getId(), goodsBackCategory.getPid(), goodsBackCategory.getEnableName(),opened,selected,type);
	}
	/**
	 * 保存
	 * @param userId
	 * @param category
	 * @return
	 */
	public Ret save(Integer userId, GoodsBackCategory category) {
		if(category==null||isOk(category.getId())||notOk(category.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		//如果不是一级分类 就得判断TypeId
		if(category.getPid()!=null&&category.getPid()==0&&isOk(category.getTypeId())){
			return fail("非叶子节点无需设置商品类型");
		}
		boolean exist=existsName(category.getName());
		if(exist){return fail(Msg.DATA_SAME_NAME_EXIST);}
		
		if(notOk(category.getEnable())){
			category.setEnable(false);
		}
		category.setSortRank(getNextRankFromZero(Kv.by("pid", category.getPid())));
		boolean success=category.save();
		if(success){
			Ret ret=processCategoryKey(category);
			if(ret.isOk()){
				//添加日志
				addSaveSystemLog(category.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_BACK_CATEGORY, category.getName());
				return success(category.getId(),Msg.SUCCESS);
			}
		}
		return FAIL;
	}
 

	/**
	 * 单独更新处理KEY字段
	 * @param category
	 * @return
	 */
	private Ret processCategoryKey(GoodsBackCategory category) {
		String categoryKey=""+category.getId();
		if(isOk(category.getPid())){
			String pkey=CACHE.me.getGoodsBackCategoryKey(category.getPid());
			if(StrKit.notBlank(pkey)){
				categoryKey=pkey+"_"+categoryKey;
			}
		}
		category.setCategoryKey(categoryKey);
		boolean success=category.update();
		return success?SUCCESS:fail("更新分类的KEY标识 失败");
	}

	/**
	 * 修改
	 * @param userId
	 * @param category
	 * @return
	 */
	public Ret update(Integer userId, GoodsBackCategory category) {
		if(category==null||notOk(category.getId())||notOk(category.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		//如果不是一级分类 就得判断TypeId
		if(category.getPid()!=null&&category.getPid()==0&&isOk(category.getTypeId())){
			return fail("非叶子节点无需设置商品类型");
		}
		boolean exist=existsName(category.getName(),category.getId());
		if(exist){return fail(Msg.DATA_SAME_NAME_EXIST);}
		
		if(notOk(category.getEnable())){
			category.setEnable(false);
		}
		GoodsBackCategory dbData=findById(category.getId());
		if(dbData==null){return fail(Msg.DATA_NOT_EXIST);}
		if(dbData.getEnable()!=null&&dbData.getEnable()==true&&category.getEnable()==false){
			//说明这次修改是可能禁用 需要检测是否正在被使用
			String msg=checkInUse(category);
			if(StrKit.notBlank(msg)){
				return fail(msg);
			}
		}
		boolean success=category.update();
		if(success){
			Ret ret=processCategoryKey(category);
			if(ret.isOk()){
				//添加日志
				addUpdateSystemLog(category.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_BACK_CATEGORY, category.getName());
				return success(category.getId(),Msg.SUCCESS);
			}
		
		}
		return FAIL;
	}
	
	/**
	 * 删除
	 * @param userId
	 * @param category
	 * @return
	 */
	public Ret delete(Integer userId, Integer id) {
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			//添加日志
			GoodsBackCategory category=ret.getAs("data");
			addDeleteSystemLog(category.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_BACK_CATEGORY, category.getName());
		}
		return ret;
	}


	/**
	 * 移动位置
	 * @param userId
	 * @param id
	 * @param newPid
	 * @param newRank
	 * @return
	 */
	public Ret move(Integer userId,Integer id, Integer newPid, Integer newRank) {
		GoodsBackCategory old=findById(id);
		if(old==null){return fail(Msg.DATA_NOT_EXIST);}
		Integer oldPid=old.getPid();
		Integer oldRank=old.getSortRank();
		if(oldPid.intValue()==newPid.intValue()){
			Kv paras=Kv.by("pid =", oldPid).set("sort_rank >=", newRank).set("id <>", id);
			List<GoodsBackCategory> others=getCommonList("*", paras, "sort_rank", "asc", true);
			//如果没有改变父亲 只调整了顺序
			if(newRank.intValue()<oldRank){
				//向左调整顺序
				doUpdateRightRanks(others,newRank+1);
			}else{
				//向右调整顺序
				doUpdateRanks(others);
			}
			
		}else{
			//如果不等于原来的pid
			//1、重新排列原来的PID下的数据
			Kv allparas=Kv.by("pid =", oldPid).set("id <>", id);
			List<GoodsBackCategory> all=getCommonList("*", allparas, "sort_rank", "asc", true);
			doUpdateRanks(all);
			//2、新的里面从新位置往后排列
			//新位置右侧调整顺序
			Kv otherparas=Kv.by("pid =", newPid).set("sort_rank >=", newRank).set("id <>", id);
			List<GoodsBackCategory> others=getCommonList("*", otherparas, "sort_rank", "asc", true);
			doUpdateRightRanks(others,newRank+1);
		}
		//最后更细自己
		old.setPid(newPid);
		old.setSortRank(newRank);
		old.update();
		//处理Key
		processCategoryKeyWithLeafNode(old.getId());
		//添加日志
		addUpdateSystemLog(old.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_BACK_CATEGORY, old.getName(),"位置和顺序");
		return SUCCESS;
	}
 
	/**
	 * 递归处理
	 * @param id
	 */
	private void processCategoryKeyWithLeafNode(Integer id) {
		GoodsBackCategory goodsBackCategory=findById(id);
		processCategoryKey(goodsBackCategory);
		List<GoodsBackCategory> sons=getCategorysByPid(id, null);
		for(GoodsBackCategory son:sons){
			processCategoryKeyWithLeafNode(son.getId());
			//缓存
			son.deleteIdCache();
		}
	}

	private void doUpdateRanks(List<GoodsBackCategory> others) {
		
		for(int i=0;i<others.size();i++){
			others.get(i).setSortRank(i);
			//缓存
			others.get(i).deleteIdCache();
		}
		Db.batchUpdate(others, others.size());
		
	}

	/**
	 * 处理更新右侧的rank
	 * @param others
	 * @param startRank
	 */
	private void doUpdateRightRanks(List<GoodsBackCategory> others, int startRank) {
		if(others.size()>0){
			for(GoodsBackCategory category:others){
				category.setSortRank(startRank);
				startRank=startRank+1;
				//缓存
				category.deleteIdCache();
			}
			Db.batchUpdate(others, others.size());
		}
		
	}
	/**
	 * 修改一个商品的后端分类 得到已经选择的分类数据
	 * @param bcategoryId
	 * @return
	 *//*
	public List<List<OptionBean>> getEditDatas(Integer bcategoryId) {
		GoodsBackCategory goodsBackCategory=findById(bcategoryId);
		if(goodsBackCategory==null){return Collections.emptyList();}
		String key=goodsBackCategory.getCategoryKey();
		Integer ids[]=ArrayUtil.toInt(key, "_");
		if(ids==null||ids.length==0){return Collections.emptyList();}
		List<List<OptionBean>> optionList=new ArrayList<List<OptionBean>>(ids.length);
		List<OptionBean> options=null;
		for(Integer pid:ids){
			options=getOptionList("name", "id", paras);
		}
		return null;
	}*/

}
