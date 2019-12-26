### 后台管理分页多条件查询
#sql("paginateAdminList")
select * from #(table) where 1=1 
#if(onSale!=null)
 and on_sale=#(SqlUtil.toSqlBool(onSale))
#end
#if(isHot!=null)
 and is_hot=#(SqlUtil.toSqlBool(isHot))
#end
#if(isDelete!=null)
 and is_delete=#(SqlUtil.toSqlBool(isDelete))
#end
#if(isRecommend!=null)
 and is_recommend=#(SqlUtil.toSqlBool(isRecommend))
#end
#if(bcategoryId!=null&&bcategoryId>0)
 and ( (bcategory_id=#sqlValue(bcategoryId)) or (concat_ws('_','',bcategory_key,'') like  concat_ws('_','%','#sqlValue(bcategoryId)','%') ) )
#end
#if(fcategoryId!=null&&fcategoryId>0)
 and ( (fcategory_id=#sqlValue(fcategoryId)) or  (concat_ws('_','',fcategory_key,'') like  concat_ws('_','%','#sqlValue(fcategoryId)','%') ) )
#end
#if(keywords??)
#setLocal(kw=SqlUtil.likeValue(keywords))
 and ((name like '%#(kw)%') or (sub_title like '%#(kw)%'))
#end
 order by update_time desc
#end

###检测商品分类是否已经被商品使用
#sql("checkGoodsBackCategoryInUse")
select id from #(table) where (bcategory_id=#sqlValue(bcategoryId)) or (concat_ws('_','',bcategory_key,'') like  concat_ws('_','%','#sqlValue(bcategoryId)','%'))
#end