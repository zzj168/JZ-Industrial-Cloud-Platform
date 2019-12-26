alter table jb_permission add column `is_system_admin_default` bit(1) DEFAULT b'0' COMMENT '是否系统超级管理员默认拥有的权限';
alter table jb_user add column `is_system_admin` bit(1) DEFAULT b'0' COMMENT '是否系统超级管理员';
alter table jb_user add column `create_user_id` int(11) DEFAULT NULL COMMENT '创建人';
update jb_user set sex='1' where sex="男";
update jb_user set sex='2' where sex="女";
alter table jb_user modify column `sex` int(11) DEFAULT '0' COMMENT '性别';
update jb_user set is_system_admin=true where id=1;
