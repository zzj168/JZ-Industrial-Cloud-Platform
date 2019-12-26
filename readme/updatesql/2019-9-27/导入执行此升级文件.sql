-- ----------------------------
-- 创建jb_user_config表
-- ----------------------------
CREATE TABLE `jb_user_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) NOT NULL COMMENT '配置名',
  `config_key` varchar(255) NOT NULL COMMENT '配置KEY',
  `config_value` varchar(255) NOT NULL COMMENT '配置值',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `value_type` varchar(40) DEFAULT NULL COMMENT '取值类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='用户系统样式自定义设置表';


-- ----------------------------
-- 创建jb_order表
-- 原来的order表，如果没有数据请自行删除，如果有数据就拷贝到新表jb_order中
-- ----------------------------
CREATE TABLE `jb_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(255) DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime DEFAULT NULL COMMENT '下单时间',
  `buyer_id` int(11) DEFAULT NULL COMMENT '买家ID',
  `buyer_name` varchar(255) DEFAULT NULL COMMENT '冗余买家名字',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `payment_time` datetime DEFAULT NULL COMMENT '付款时间',
  `consign_time` datetime DEFAULT NULL COMMENT '发货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '交易完成时间',
  `close_time` datetime DEFAULT NULL COMMENT '订单关闭时间',
  `buyer_message` varchar(255) DEFAULT NULL COMMENT '卖家留言',
  `buyer_rate` bit(1) DEFAULT NULL COMMENT '买家是否已经评价',
  `state` int(11) DEFAULT NULL COMMENT '订单状态 待付款 已付款 已发货 订单完成 订单关闭',
  `goods_amount` decimal(10,2) DEFAULT NULL COMMENT '总额',
  `post_fee` decimal(10,2) DEFAULT NULL COMMENT '运费',
  `payment_amount` decimal(10,2) DEFAULT NULL COMMENT '应付总额',
  `payment_type` int(11) DEFAULT NULL COMMENT '支付类型 在线支付 货到付款',
  `online_payment_type` int(11) DEFAULT NULL COMMENT '在线支付选择了谁',
  `close_type` int(11) DEFAULT NULL COMMENT '通过什么方式关闭 后台管理 or 客户自身',
  `close_uesr_id` int(11) DEFAULT NULL COMMENT '关闭订单用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商城订单';

-- ----------------------------
-- 删除SKU 新建 jb_sku
-- ----------------------------
DROP TABLE IF EXISTS `sku`;
CREATE TABLE `jb_sku` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sku_key` varchar(255) DEFAULT NULL COMMENT '规格编码',
  `sku_name` varchar(255) DEFAULT NULL COMMENT '规格名称',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品ID',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `type_id` int(11) DEFAULT NULL COMMENT '类型ID',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock_count` varchar(255) DEFAULT NULL COMMENT '库存量',
  `sub_title` varchar(255) DEFAULT NULL COMMENT '营销语',
  `on_sale` bit(1) DEFAULT b'0' COMMENT '是否上架',
  `sellout` bit(1) DEFAULT b'0' COMMENT '售罄',
  `real_sale_count` int(11) DEFAULT NULL COMMENT '真实销量',
  `show_sale_count` int(11) DEFAULT NULL COMMENT '展示营销销量',
  `main_image` varchar(255) DEFAULT NULL COMMENT '主图',
  `extraImages` text DEFAULT NULL COMMENT '附图',
  `is_hot` bit(1) DEFAULT b'0' COMMENT '热销',
  `is_recommend` bit(1) DEFAULT b'0' COMMENT '推荐',
  `need_content` bit(1) DEFAULT b'0' COMMENT '是否需要详情描述',
  `content_type` int(11) DEFAULT NULL COMMENT '描述类型 是富文本还是分开的图片 文本段数据',
  `under_time` datetime DEFAULT NULL COMMENT '下架时间',
  `onSale_time` datetime DEFAULT NULL COMMENT '上架时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `update_user_id` int(11) DEFAULT NULL COMMENT '最后更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SKU';



-- ----------------------------
-- 删除并重新创建 jb_goods_group
-- ----------------------------
DROP TABLE IF EXISTS `goods_group`;
CREATE TABLE `jb_goods_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `sort_rank` varchar(255) DEFAULT NULL COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `goods_count` int(11) DEFAULT NULL COMMENT '商品数量',
  `enable` bit(1) DEFAULT b'0' COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品分组';


-- ----------------------------
-- 重命名表加上jb_前缀
-- ----------------------------
rename table application to jb_application;
rename table brand to jb_brand;
rename table change_log to jb_change_log;
rename table demotable to jb_demotable;
rename table dictionary to jb_dictionary;
rename table dictionary_type to jb_dictionary_type;
rename table download_log to jb_download_log;
rename table global_config to jb_global_config;
rename table goods to jb_goods;
rename table goods_attr to jb_goods_attr;
rename table goods_back_category to jb_goods_back_category;
rename table goods_element_content to jb_goods_element_content;
rename table goods_html_content to jb_goods_html_content;
rename table goods_type to jb_goods_type;
rename table goods_type_brand to jb_goods_type_brand;
rename table jbolt_file to jb_jbolt_file;
rename table jbolt_version to jb_jbolt_version;
rename table jbolt_version_file to jb_jbolt_version_file;
rename table order_item to jb_order_item;
rename table order_shipping to jb_order_shipping;
rename table permission to jb_permission;
rename table role to jb_role;
rename table role_permission to jb_role_permission;
rename table shelf to jb_shelf;
rename table shelf_activity to jb_shelf_activity;
rename table shelf_carousel to jb_shelf_carousel;
rename table shelf_element to jb_shelf_element;
rename table shelf_goods_floor to jb_shelf_goods_floor;
rename table shelf_goods_group to jb_shelf_goods_group;
rename table sku_item to jb_sku_item;
rename table spec to jb_spec;
rename table spec_item to jb_spec_item;
rename table system_log to jb_system_log;
rename table update_libs to jb_update_libs;
rename table user to jb_user;
rename table wechat_article to jb_wechat_article;
rename table wechat_autoreply to jb_wechat_autoreply;
rename table wechat_config to jb_wechat_config;
rename table wechat_keywords to jb_wechat_keywords;
rename table wechat_media to jb_wechat_media;
rename table wechat_menu to jb_wechat_menu;
rename table wechat_mpinfo to jb_wechat_mpinfo;
rename table wechat_reply_content to jb_wechat_reply_content;
rename table wechat_user to jb_wechat_user;

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_application
-- ----------------------------
alter table jb_application change briefInfo brief_info varchar(255) DEFAULT NULL COMMENT '应用简介';
alter table jb_application change appId app_id varchar(255) DEFAULT NULL COMMENT '应用ID';
alter table jb_application change appSecret app_secret varchar(255) DEFAULT NULL COMMENT '应用密钥';
alter table jb_application change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_application change updateTime update_time datetime DEFAULT NULL COMMENT '更新时间';
alter table jb_application change userId user_id int(11) DEFAULT NULL COMMENT '创建用户ID';
alter table jb_application change updateUserId update_user_id int(11) DEFAULT NULL COMMENT '更新用户ID';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_brand
-- ----------------------------
alter table jb_brand change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';
alter table jb_brand change englishName english_name varchar(255) DEFAULT NULL COMMENT '英文名';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_change_log
-- ----------------------------
alter table jb_change_log change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_change_log change jboltVersionId jbolt_version_id int(11) DEFAULT NULL COMMENT 'jbolt版本ID';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_demotable
-- ----------------------------
alter table jb_demotable change demoDate demo_date date ;
alter table jb_demotable change demoTime demo_time time;
alter table jb_demotable change demoDateTime demo_date_time datetime;
alter table jb_demotable change demoWeek demo_week varchar(255);
alter table jb_demotable change demoMonth demo_month varchar(255);

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_dictionary
-- ----------------------------
alter table jb_dictionary change typeId type_id int(11) DEFAULT NULL COMMENT '字典类型ID';
alter table jb_dictionary change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_dictionary_type
-- ----------------------------
alter table jb_dictionary_type change modeLevel mode_level int(11) DEFAULT NULL COMMENT '模式层级';
alter table jb_dictionary_type change typeKey type_key varchar(255) DEFAULT NULL COMMENT '标识KEY';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_download_log
-- ----------------------------
alter table jb_download_log change downloadType download_type int(11) DEFAULT NULL COMMENT '下载类型';
alter table jb_download_log change downloadTime download_time datetime DEFAULT NULL COMMENT '下载时间';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_global_config
-- ----------------------------
alter table jb_global_config change configKey config_key varchar(255) DEFAULT NULL COMMENT '配置KEY';
alter table jb_global_config change configValue config_value varchar(255) DEFAULT NULL COMMENT '配置项值';
alter table jb_global_config change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_global_config change updateTime update_time datetime DEFAULT NULL COMMENT '更新时间';
alter table jb_global_config change userId user_id int(11) DEFAULT NULL COMMENT '创建用户ID';
alter table jb_global_config change updateUserId update_user_id int(11) DEFAULT NULL COMMENT '更新用户ID';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_goods
-- ----------------------------
alter table jb_goods change originalPrice original_price decimal(10,2) DEFAULT NULL COMMENT '原价';
alter table jb_goods change price price decimal(10,2) DEFAULT NULL COMMENT '单价';
alter table jb_goods change mainImage main_image varchar(255) DEFAULT NULL COMMENT '主图';
alter table jb_goods change extraImages extra_images text DEFAULT NULL COMMENT '附图';
alter table jb_goods change contentType content_type int(11) DEFAULT NULL COMMENT '商品描述类型';
alter table jb_goods change stockCount stock_count int(11) DEFAULT NULL COMMENT '库存量';
alter table jb_goods change subTitle sub_title varchar(255) DEFAULT NULL COMMENT '二级标题';
alter table jb_goods change isMultispec is_multispec bit(1) DEFAULT b'0' COMMENT '是否是多规格';
alter table jb_goods change limitCount limit_count int(11) DEFAULT '0' COMMENT '限购数量 0=不限制';
alter table jb_goods change locationLabel location_label varchar(255) DEFAULT NULL COMMENT '所在地';
alter table jb_goods change isProvideInvoices is_provide_invoices bit(1) DEFAULT b'0' COMMENT '是否提供发票';
alter table jb_goods change isGuarantee is_guarantee bit(1) DEFAULT b'0' COMMENT '是否保修';
alter table jb_goods change onSale on_sale bit(1) DEFAULT b'0' COMMENT '是否上架';
alter table jb_goods change underTime under_time datetime DEFAULT NULL COMMENT '下架时间';
alter table jb_goods change onSaleTime on_sale_time datetime DEFAULT NULL COMMENT '上架时间';
alter table jb_goods change onSaleUserId on_sale_user_id int(11) DEFAULT NULL COMMENT '上架处理人';
alter table jb_goods change underUserId under_user_id int(11) DEFAULT NULL COMMENT '下架处理人';
alter table jb_goods change createUserId create_user_id int(11) DEFAULT NULL COMMENT '创建人';
alter table jb_goods change updateUserId update_user_id int(11) DEFAULT NULL COMMENT '更新人';
alter table jb_goods change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_goods change updateTime update_time datetime DEFAULT NULL COMMENT '更新时间';
alter table jb_goods change goodsUnit goods_unit int(11) DEFAULT NULL COMMENT '商品单位';
alter table jb_goods change realSaleCount real_sale_count int(11) DEFAULT '0' COMMENT '真实销量';
alter table jb_goods change showSaleCount show_sale_count int(11) DEFAULT '0' COMMENT '展示营销销量';
alter table jb_goods change typeId type_id int(11) DEFAULT NULL COMMENT '商品类型';
alter table jb_goods change brandId brand_id int(11) DEFAULT NULL COMMENT '商品品牌';
alter table jb_goods change isHot is_hot bit(1) DEFAULT b'0' COMMENT '热销';
alter table jb_goods change isRecommend is_recommend bit(1) DEFAULT b'0' COMMENT '推荐';
alter table jb_goods change fcategoryKey fcategory_key varchar(255) DEFAULT NULL COMMENT '前台分类KEY';
alter table jb_goods change bcategoryKey bcategory_key varchar(255) DEFAULT NULL COMMENT '后台分类KEY';
alter table jb_goods change bcategoryId bcategory_id int(11) DEFAULT NULL COMMENT '后端分类ID';
alter table jb_goods change fcategoryId fcategory_id int(11) DEFAULT NULL COMMENT '前端分类ID';
alter table jb_goods change isDelete is_delete bit(1) DEFAULT b'0' COMMENT '是否已删除';
alter table jb_goods change goodsNo goods_no varchar(255) DEFAULT NULL COMMENT '商品编号';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_goods_attr
-- ----------------------------
alter table jb_goods_attr change goodsId goods_id int(11) DEFAULT NULL COMMENT '商品ID';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_goods_back_category
-- ----------------------------
alter table jb_goods_back_category change typeId type_id int(11) DEFAULT NULL COMMENT '商品类型';
alter table jb_goods_back_category change categoryKey category_key varchar(255) DEFAULT NULL COMMENT '所有上级和自身ID串联起来';
alter table jb_goods_back_category change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_goods_element_content
-- ----------------------------
alter table jb_goods_element_content change goodsId goods_id int(11) DEFAULT NULL COMMENT '商品ID';
alter table jb_goods_element_content change skuId sku_id int(11) DEFAULT NULL COMMENT 'SKUID';
alter table jb_goods_element_content change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_goods_html_content
-- ----------------------------
alter table jb_goods_html_content change goodsId goods_id int(11) DEFAULT NULL COMMENT '商品ID';
alter table jb_goods_html_content change skuId sku_id int(11) DEFAULT NULL COMMENT 'SKUID';
alter table jb_goods_html_content change updateUserId update_user_id int(11) DEFAULT NULL COMMENT '更新人';
alter table jb_goods_html_content change updateTime update_time datetime DEFAULT NULL COMMENT '更新时间';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_goods_type
-- ----------------------------
alter table jb_goods_type change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';
alter table jb_goods_type change specCount spec_count int(11) DEFAULT '0' COMMENT '规格数量';
alter table jb_goods_type change attrCount attr_count int(11) DEFAULT '0' COMMENT '属性数量';
alter table jb_goods_type change brandCount brand_count int(11) DEFAULT '0' COMMENT '品牌数量';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_goods_type_brand
-- ----------------------------
alter table jb_goods_type_brand change goodsTypeId goods_type_id int(11) DEFAULT NULL COMMENT '关联商品类型';
alter table jb_goods_type_brand change brandId brand_id int(11) DEFAULT NULL COMMENT '关联商品品牌';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_jbolt_file
-- ----------------------------
alter table jb_jbolt_file change localPath local_path varchar(255) DEFAULT NULL COMMENT '保存物理地址';
alter table jb_jbolt_file change localUrl local_url varchar(255) DEFAULT NULL COMMENT '本地可访问URL地址';
alter table jb_jbolt_file change cdnUrl cdn_url varchar(255) DEFAULT NULL COMMENT '外部CDN地址';
alter table jb_jbolt_file change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_jbolt_file change userId user_id int(11) DEFAULT NULL COMMENT '用户ID';
alter table jb_jbolt_file change fileName file_name varchar(255) DEFAULT NULL COMMENT '文件名';
alter table jb_jbolt_file change fileSuffix file_suffix varchar(255) DEFAULT NULL COMMENT '后缀名';
alter table jb_jbolt_file change fileType file_type int(11) DEFAULT NULL COMMENT '文件类型 图片 附件 视频 音频';
alter table jb_jbolt_file change fileSize file_size int(11) UNSIGNED NULL DEFAULT NULL COMMENT '文件大小';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_jbolt_version
-- ----------------------------
alter table jb_jbolt_version change publishTime publish_time datetime DEFAULT NULL COMMENT '发布时间';
alter table jb_jbolt_version change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_jbolt_version change userId user_id int(11) DEFAULT NULL COMMENT '用户ID';
alter table jb_jbolt_version change isNew is_new bit(1) DEFAULT b'0' COMMENT '是否最新版';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_jbolt_version_file
-- ----------------------------
alter table jb_jbolt_version_file change jboltVersionId jbolt_version_id int(11) DEFAULT NULL COMMENT 'jbolt版本ID';






-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_order_item
-- ----------------------------
alter table jb_order_item change orderId order_id int(11) NOT NULL COMMENT '订单ID';
alter table jb_order_item add column order_no varchar(255) NOT NULL COMMENT '订单编号';
alter table jb_order_item change goodsId goods_id int(11) NOT NULL COMMENT '商品ID';
alter table jb_order_item change goodsName goods_name varchar(255) DEFAULT NULL COMMENT '商品名称';
alter table jb_order_item change goodsSubTitle goods_sub_title varchar(255) DEFAULT NULL COMMENT '二级促销标题';
alter table jb_order_item change price price decimal(10,2) DEFAULT '0' COMMENT '单价';
alter table jb_order_item add column original_price decimal(10,2) DEFAULT NULL COMMENT '原价';
alter table jb_order_item change goodsCount goods_count int(11) DEFAULT '0' COMMENT '商品数量';
alter table jb_order_item change totalFee goods_amount decimal(10,2) DEFAULT '0' COMMENT '总额';
alter table jb_order_item add column save_price decimal(10,2) DEFAULT '0' COMMENT '单个节省价格';
alter table jb_order_item add column save_amount decimal(10,2) DEFAULT '0' COMMENT '一共节省多少钱';
alter table jb_order_item change goodsImage goods_image varchar(255) DEFAULT NULL COMMENT '商品图';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_order_shipping
-- ----------------------------
alter table jb_order_shipping change orderId order_id int(11) NOT NULL COMMENT '订单ID';
alter table jb_order_shipping add column order_no varchar(255) NOT NULL COMMENT '订单编号';
alter table jb_order_shipping add column buyer_id int(1) NOT NULL COMMENT '买方用户ID';
alter table jb_order_shipping change wxuserNickname buyer_nickname varchar(255) DEFAULT NULL COMMENT '买方昵称';
alter table jb_order_shipping change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_order_shipping change updateTime update_time datetime DEFAULT NULL COMMENT '更新时间';
alter table jb_order_shipping change updateUserId update_user_id int(11) DEFAULT NULL COMMENT '更新人ID';



-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_permission
-- ----------------------------
alter table jb_permission change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';
alter table jb_permission change permissionKey permission_key varchar(255) DEFAULT NULL COMMENT '权限资源KEY';
alter table jb_permission change level permission_level int(11) DEFAULT NULL COMMENT '层级';
alter table jb_permission change isMenu is_menu bit(1) DEFAULT NULL COMMENT '是否是菜单';
alter table jb_permission change isTargetBlank is_target_blank bit(1) DEFAULT NULL COMMENT '是否新窗口打开';




-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_role_permission
-- ----------------------------
alter table jb_role_permission change roleId role_id int(11) NOT NULL COMMENT '角色ID';
alter table jb_role_permission change permissionId permission_id int(11) NOT NULL COMMENT '权限ID';



-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_shelf
-- ----------------------------
alter table jb_shelf change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_shelf change publishTime publish_time datetime DEFAULT NULL COMMENT '发布时间';
alter table jb_shelf change updateTime update_time datetime DEFAULT NULL COMMENT '更新时间';
alter table jb_shelf change createUserId create_user_id int(11) DEFAULT NULL COMMENT '创建人ID';
alter table jb_shelf change publishUserId publish_user_id int(11) DEFAULT NULL COMMENT '上线发布人';
alter table jb_shelf change updateUserId update_user_id int(11) DEFAULT NULL COMMENT '更新人';
alter table jb_shelf change shareImg share_image varchar(255) DEFAULT NULL COMMENT '分享图';
alter table jb_shelf change subTitle share_title varchar(255) DEFAULT NULL COMMENT '分享标题';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_shelf_activity
-- ----------------------------
alter table jb_shelf_activity change image poster_image varchar(255) NOT NULL COMMENT '海报地址';
alter table jb_shelf_activity change url open_url varchar(255) NOT NULL COMMENT '打开地址';
alter table jb_shelf_activity change openType open_type int(11) NOT NULL COMMENT '打开方式 网页还是商品内页 还是分类 还是';
alter table jb_shelf_activity change shelfElementId shelf_element_id int(11) NOT NULL COMMENT '货架元素ID';
alter table jb_shelf_activity change shelfId shelf_id int(11) NOT NULL COMMENT '货架ID';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_shelf_carousel
-- ----------------------------
alter table jb_shelf_carousel change url open_url varchar(255) NOT NULL COMMENT '打开地址';
alter table jb_shelf_carousel change shelfElemetId shelf_element_id int(11) NOT NULL COMMENT '货架元素ID';
alter table jb_shelf_carousel change shelfId shelf_id int(11) NOT NULL COMMENT '货架ID';
alter table jb_shelf_carousel change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_shelf_element
-- ----------------------------
alter table jb_shelf_element change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_shelf_goods_floor
-- ----------------------------
alter table jb_shelf_goods_floor change url open_url varchar(255) NOT NULL COMMENT '打开地址';
alter table jb_shelf_goods_floor change shelfElementId shelf_element_id int(11) NOT NULL COMMENT '货架元素ID';
alter table jb_shelf_goods_floor change shelfId shelf_id int(11) NOT NULL COMMENT '货架ID';
alter table jb_shelf_goods_floor change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';
alter table jb_shelf_goods_floor change groupId group_id int(11) NOT NULL COMMENT '商品分组ID';
alter table jb_shelf_goods_floor change columnCount column_count int(11) NOT NULL COMMENT '显示几列布局';
alter table jb_shelf_goods_floor change goodsCount goods_count int(11) NOT NULL COMMENT '显示商品数量';



-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_shelf_goods_group
-- ----------------------------
alter table jb_shelf_goods_group change shelfId shelf_id int(11) NOT NULL COMMENT '货架ID';
alter table jb_shelf_goods_group change shelfElementId shelf_element_id int(11) NOT NULL COMMENT '货架元素ID';
alter table jb_shelf_goods_group change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';
alter table jb_shelf_goods_group change subTitle sub_title varchar(255) DEFAULT NULL COMMENT '二级标题';




-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_sku_item
-- ----------------------------
alter table jb_sku_item change skuId sku_id int(11) NOT NULL COMMENT 'SKU ID';
alter table jb_sku_item change goodsId goods_id int(11) NOT NULL COMMENT '商品 ID';
alter table jb_sku_item change specId spec_id int(11) NOT NULL COMMENT '规格 ID';
alter table jb_sku_item change specItemId spec_item_id int(11) NOT NULL COMMENT '规格项 ID';
alter table jb_sku_item change typeId type_id int(11) NOT NULL COMMENT '商品分类';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_update_libs
-- ----------------------------
alter table jb_update_libs change deleteAll delete_all bit(1) DEFAULT b'1' COMMENT '清空文件夹';
alter table jb_update_libs change must is_must bit(1) DEFAULT b'0' COMMENT '强制';



-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_system_log
-- ----------------------------
alter table jb_system_log change userId user_id int(11) DEFAULT NULL COMMENT '操作人ID';
alter table jb_system_log change userName user_name varchar(255) DEFAULT NULL COMMENT '操作人姓名';
alter table jb_system_log change targetType target_type int(11) DEFAULT NULL COMMENT '操作对象类型';
alter table jb_system_log change targetId target_id int(11) DEFAULT NULL COMMENT '操作对象ID';
alter table jb_system_log change createTime create_time datetime DEFAULT NULL COMMENT '记录创建时间';
alter table jb_system_log change openType open_type int(11) DEFAULT NULL COMMENT '打开类型URL还是Dialog';



-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_user
-- ----------------------------
alter table jb_user change createTime create_time datetime DEFAULT NULL COMMENT '记录创建时间';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_autoreply
-- ----------------------------
alter table jb_wechat_autoreply change mpId mp_id int(11) DEFAULT NULL COMMENT '微信 ID';
alter table jb_wechat_autoreply change userId user_id int(11) DEFAULT NULL COMMENT '用户 ID';
alter table jb_wechat_autoreply change replyType reply_type int(11) DEFAULT NULL COMMENT '回复类型 全部还是随机一条';
alter table jb_wechat_autoreply change createTime create_time datetime DEFAULT NULL COMMENT '记录创建时间';

-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_config
-- ----------------------------
alter table jb_wechat_config change mpId mp_id int(11) DEFAULT NULL COMMENT '微信 ID';
alter table jb_wechat_config change configKey config_key varchar(255) DEFAULT NULL COMMENT '配置key';
alter table jb_wechat_config change configValue config_value varchar(255) DEFAULT NULL COMMENT '配置值';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_keywords
-- ----------------------------
alter table jb_wechat_keywords change mpId mp_id int(11) DEFAULT NULL COMMENT '微信 ID';
alter table jb_wechat_keywords change autoReplyId auto_reply_id int(11) DEFAULT NULL COMMENT '回复规则ID';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_media
-- ----------------------------
alter table jb_wechat_media change mpId mp_id int(11) DEFAULT NULL COMMENT '微信 ID';
alter table jb_wechat_media change mediaId media_id varchar(255) DEFAULT NULL COMMENT '微信素材ID';
alter table jb_wechat_media change updateTime update_time datetime DEFAULT NULL COMMENT '更新时间';
alter table jb_wechat_media change serverUrl server_url varchar(255) DEFAULT NULL COMMENT '存服务器URL';


-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_menu
-- ----------------------------
alter table jb_wechat_menu change mpId mp_id int(11) DEFAULT NULL COMMENT '微信 ID';
alter table jb_wechat_menu change appId app_id varchar(255) DEFAULT NULL COMMENT '微信小程序APPID';
alter table jb_wechat_menu change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';
alter table jb_wechat_menu change pagepath page_path varchar(255) DEFAULT NULL COMMENT '微信小程序页面地址';





-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_mpinfo
-- ----------------------------
alter table jb_wechat_mpinfo change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_wechat_mpinfo change updateTime update_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_wechat_mpinfo change userId user_id int(11) DEFAULT NULL COMMENT '用户ID';
alter table jb_wechat_mpinfo change subjectType subject_type int(11) DEFAULT NULL COMMENT '申请认证主体的类型 个人还是企业';
alter table jb_wechat_mpinfo change updateUserId update_user_id int(11) DEFAULT NULL COMMENT '更新人ID';
alter table jb_wechat_mpinfo change brief brief_info varchar(255) DEFAULT NULL COMMENT '简介';
alter table jb_wechat_mpinfo change isAuthenticated is_authenticated bit(1) DEFAULT NULL COMMENT '是否已认证';
alter table jb_wechat_mpinfo change wechatId wechat_id varchar(255) DEFAULT NULL COMMENT '微信号';






-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_reply_content
-- ----------------------------
alter table jb_wechat_reply_content change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_wechat_reply_content change autoReplyId auto_reply_id int(11) DEFAULT NULL COMMENT '回复规则ID';
alter table jb_wechat_reply_content change userId user_id int(11) DEFAULT NULL COMMENT '用户 ID';
alter table jb_wechat_reply_content change mpId mp_id int(11) DEFAULT NULL COMMENT '微信 ID';
alter table jb_wechat_reply_content change sortRank sort_rank int(11) DEFAULT NULL COMMENT '排序';




-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_article
-- ----------------------------
alter table jb_wechat_article change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_wechat_article change userId user_id int(11) DEFAULT NULL COMMENT '用户 ID';
alter table jb_wechat_article change updateTime update_time datetime DEFAULT NULL COMMENT '更新时间';
alter table jb_wechat_article change updateUserId update_user_id int(11) DEFAULT NULL COMMENT '更新用户 ID';
alter table jb_wechat_article change mediaId media_id varchar(255) DEFAULT NULL COMMENT '微信素材 ID';
alter table jb_wechat_article change mpId mp_id int(11) DEFAULT NULL COMMENT '微信 ID';
alter table jb_wechat_article change viewCount view_count int(11) DEFAULT NULL COMMENT '阅读数';
alter table jb_wechat_article change subTitle brief_info varchar(120) DEFAULT NULL COMMENT '文章摘要';
alter table jb_wechat_article drop column remark;




-- ----------------------------
-- 修改表内字段名为下划线分隔 jb_wechat_user
-- ----------------------------
alter table jb_wechat_user change nickName nickname varchar(255) DEFAULT NULL COMMENT '用户昵称';
alter table jb_wechat_user change openId open_id varchar(255) DEFAULT NULL COMMENT 'openId';
alter table jb_wechat_user change unionID union_id varchar(255) DEFAULT NULL COMMENT 'unionID';
alter table jb_wechat_user change headImgurl head_img_url varchar(255) DEFAULT NULL COMMENT '头像';
alter table jb_wechat_user change subscribeTime subscribe_time datetime DEFAULT NULL COMMENT '关注时间';
alter table jb_wechat_user change groupId group_id int(11) DEFAULT NULL COMMENT '分组';
alter table jb_wechat_user change tagIds tag_ids varchar(255) DEFAULT NULL COMMENT '标签';
alter table jb_wechat_user change subscribeScene subscribe_scene varchar(255) DEFAULT NULL COMMENT '关注渠道';
alter table jb_wechat_user change qrScene qr_scene int(11) DEFAULT NULL COMMENT '二维码场景-开发者自定义';
alter table jb_wechat_user change qrSceneStr qr_scene_str varchar(255) DEFAULT NULL COMMENT '二维码扫码场景描述-开发者自定义';
alter table jb_wechat_user change realName realname varchar(255) DEFAULT NULL COMMENT '真实姓名';
alter table jb_wechat_user change checkCode check_code varchar(255) DEFAULT NULL COMMENT '手机验证码';
alter table jb_wechat_user change isChecked is_checked bit(1) DEFAULT b'0' COMMENT '是否已验证';
alter table jb_wechat_user change sessionKey session_key varchar(255) DEFAULT NULL COMMENT '小程序登录SessionKey';
alter table jb_wechat_user change loginToken login_token varchar(255) DEFAULT NULL COMMENT '登录TOKEN';
alter table jb_wechat_user change createTime create_time datetime DEFAULT NULL COMMENT '创建时间';
alter table jb_wechat_user change checkedTime checked_time datetime DEFAULT NULL COMMENT '验证绑定手机号时间';
alter table jb_wechat_user change mpId mp_id int(11) DEFAULT NULL COMMENT '所属公众平台ID';



