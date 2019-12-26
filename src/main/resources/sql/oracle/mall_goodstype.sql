### 后台管理分页查询
#sql("paginateAdminList")
select * from #(table) where 1=1 
#if(keywords??)
#setLocal(kw=SqlUtil.likeValue(keywords))
 and ((name like '%#(kw)%') or (pinyin like '%#(kw)%') or (remark like '%#(kw)%'))
#end
 order by sort_rank asc
#end
