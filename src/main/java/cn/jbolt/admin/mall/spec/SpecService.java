package cn.jbolt.admin.mall.spec;

import com.jfinal.kit.Ret;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Spec;
import cn.jbolt.common.util.CACHE;

/**   
 * 系统商品规格管理Service
 * @ClassName:  GoodsTypeService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午1:01:46   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class SpecService extends BaseService<Spec> {
	private Spec dao = new Spec().dao();
	@Override
	protected Spec dao() {
		return dao;
	}
	/**
	 * 保存
	 * @param userId
	 * @param Spec
	 * @return
	 */
	public Ret save(Integer userId, Spec Spec) {
		if(Spec==null||isOk(Spec.getId())||notOk(Spec.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		if(existsName(Spec.getName())){
			return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		boolean success=Spec.save();
		if(success){
			//TODO 处理添加日志
		}
		return ret(success);
	}
	
	/**
	 * 修改
	 * @param userId
	 * @param spec
	 * @return
	 */
	public Ret update(Integer userId, Spec spec) {
		if(spec==null||notOk(spec.getId())||notOk(spec.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		if(existsName(spec.getName(),spec.getId())){
			return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		boolean success=spec.update();
		if(success){
			//TODO 处理添加日志
		}
		return ret(success);
	}
	/**
	 * 修改
	 * @param userId
	 * @param goodsType
	 * @return
	 */
	public Ret delete(Integer userId, Integer id) {
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			//TODO 处理添加日志
		}
		return ret;
	}

	@Override
	public String checkInUse(Spec m) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
