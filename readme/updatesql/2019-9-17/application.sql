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

 Date: 17/09/2019 17:36:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for application
-- ----------------------------
DROP TABLE IF EXISTS `application`;
CREATE TABLE `application`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用名称',
  `briefInfo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用简介',
  `appId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用ID',
  `appSecret` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用秘钥 用于token加密',
  `enable` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否启用',
  `createTime` datetime(0) NOT NULL COMMENT '创建时间',
  `updateTime` datetime(0) NOT NULL COMMENT '更新时间',
  `userId` int(11) NOT NULL COMMENT '创建用户',
  `updateUserId` int(11) NOT NULL COMMENT '更新用户',
  `type` int(11) NOT NULL COMMENT 'app类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'API应用中心的应用APP' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of application
-- ----------------------------
INSERT INTO `application` VALUES (2, 'JFinal学院电商小程序', 'JFinal学院电商小程序', 'jbi9u06u8n6aolt', 'MnRhbWlxazh1YXpzenQ1N3RjN216MmRnZXh6Nmc3NnU=', b'1', '2019-09-16 12:32:51', '2019-09-17 06:47:22', 1, 1, 4);

SET FOREIGN_KEY_CHECKS = 1;
