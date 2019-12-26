### 后台管理分页查询
#sql("paginateAdminList")
select * from #(table) where 1=1 
#if(keywords??)
#setLocal(kw=SqlUtil.likeValue(keywords))
 and ((name like '%#(kw)%') or (english_name like '%#(kw)%') or (remark like '%#(kw)%'))
#end
 order by id desc
#end


### 查询goodsType关联的Brand
#sql("getBrandsByGoodsType")
select * from #(table) where id in(select gt.brand_id from jb_goods_type_brand as gt where gt.goods_type_id=#(goodsTypeId))
#end

### 查询goodsType关联的Brand 2
#sql("getBrandsByGoodsType2")
select this.* from brand as this left join jb_goods_type_brand as gt on gt.brand_id=this.id where gt.goods_type_id=#(goodsTypeId)
#end
 
### 查询goodsType未关联的Brand
#sql("getBrandsWithoutGoodsType")
select * from #(table) where id not in(select gt.brand_id from jb_goods_type_brand as gt where gt.goods_type_id=#(goodsTypeId))
#end