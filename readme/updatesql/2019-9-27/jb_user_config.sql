/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost:3306
 Source Schema         : jbolt

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 27/09/2019 18:05:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for jb_user_config
-- ----------------------------
DROP TABLE IF EXISTS `jb_user_config`;
CREATE TABLE `jb_user_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置名',
  `config_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置KEY',
  `config_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置值',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `value_type` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '取值类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户系统样式自定义设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of jb_user_config
-- ----------------------------
INSERT INTO `jb_user_config` VALUES (1, '系统Admin后台样式', 'JBOLT_ADMIN_STYLE', 'jbolt_style_1', 1, '2019-09-26 00:40:59', '2019-09-27 14:35:54', 'string');
INSERT INTO `jb_user_config` VALUES (2, '系统Admin后台是否启用多选项卡', 'JBOLT_ADMIN_WITH_TABS', 'false', 1, '2019-09-26 00:40:59', '2019-09-27 03:58:13', 'boolean');
INSERT INTO `jb_user_config` VALUES (3, '系统登录页面是否启用透明玻璃风格', 'JBOLT_LOGIN_FORM_STYLE_GLASS', 'false', 1, '2019-09-26 00:40:59', '2019-09-27 18:04:30', 'boolean');
INSERT INTO `jb_user_config` VALUES (4, '系统登录页面背景图是否启用模糊风格', 'JBOLT_LOGIN_BGIMG_BLUR', 'true', 1, '2019-09-26 00:40:59', '2019-09-27 13:30:23', 'boolean');

SET FOREIGN_KEY_CHECKS = 1;
