alter table jb_application add column `need_check_sign` bit(1) DEFAULT b'1' COMMENT '是否需要接口校验SIGN'
alter table jb_role add column `pid` int(11) DEFAULT '0' COMMENT '父级角色ID'