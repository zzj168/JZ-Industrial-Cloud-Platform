### 后台管理分页多条件查询
#sql("paginateAdminList")
select * from #(table) where 1=1 
#if(onSale!=null)
 and on_sale=#(onSale)
#end
#if(isHot!=null)
 and is_hot=#(isHot)
#end
#if(isDelete!=null)
 and is_delete=#(isDelete)
#end
#if(isRecommend!=null)
 and is_recommend=#(isRecommend)
#end
#if(bcategoryId!=null&&bcategoryId>0)
 and ( bcategory_id=#(bcategoryId) or  ( concat('_',bcategory_key,'_')  regexp concat('_(',replace(#sqlValue(bcategoryId),'_','|'),')_') ))
#end
#if(fcategoryId!=null&&fcategoryId>0)
 and ( fcategory_id=#(fcategoryId) or  ( concat('_',fcategory_key,'_')  regexp concat('_(',replace(#sqlValue(fcategoryId),'_','|'),')_') ))
#end
#if(keywords??)
#setLocal(kw=SqlUtil.likeValue(keywords))
 and ((name like '%#(kw)%') or (sub_title like '%#(kw)%'))
#end
 order by update_time desc
#end

###检测商品分类是否已经被商品使用
#sql("checkGoodsBackCategoryInUse")
select id from #(table) where (bcategory_id=#(bcategoryId) or  ( concat('_',bcategory_key,'_')  regexp concat('_(',replace(#sqlValue(bcategoryId),'_','|'),')_') ))
#end