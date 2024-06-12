/*
 Navicat Premium Data Transfer

 Source Server         : 501 服务器
 Source Server Type    : MySQL
 Source Server Version : 80037
 Source Host           : 10.1.51.234:3306
 Source Schema         : ctd

 Target Server Type    : MySQL
 Target Server Version : 80037
 File Encoding         : 65001

 Date: 12/06/2024 16:11:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_ctd_data_records
-- ----------------------------
DROP TABLE IF EXISTS `t_ctd_data_records`;
CREATE TABLE `t_ctd_data_records`  (
  `data_set_sn` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `voyage_number` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ship_name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `platform_type` int(0) NOT NULL,
  `platform_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `station_num` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `start_time` bigint(0) NOT NULL,
  `finish_time` bigint(0) NOT NULL,
  `dive_num` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `longitude_layout` double(10, 6) NOT NULL,
  `latitude_layout` double(10, 6) NOT NULL,
  `depth_layout` double(10, 2) NOT NULL,
  `longitude_work` double(10, 6) DEFAULT NULL,
  `latitude_work` double(10, 6) DEFAULT NULL,
  `depth_work` double(10, 2) DEFAULT NULL,
  `dev_type` int(0) NOT NULL,
  `dev_model` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `dev_sn` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `data_format` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `data_status` int(0) NOT NULL,
  `data_file_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '全表唯一',
  `data_exist` bit(1) NOT NULL DEFAULT b'0',
  `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志',
  PRIMARY KEY (`data_set_sn`) USING BTREE,
  UNIQUE INDEX `unique_data_file_name`(`data_file_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_ctd_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_ctd_detail`;
CREATE TABLE `t_ctd_detail`  (
  `file_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'ctd文件名称',
  `temperature` double(20, 13) NOT NULL COMMENT '温度',
  `salinity` double(20, 13) NOT NULL COMMENT '盐度',
  `depth` double(20, 13) NOT NULL COMMENT '深度',
  `soundspeed` float NOT NULL COMMENT '声速',
  INDEX `index_file_name`(`file_name`, `depth`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log`  (
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户名',
  `request_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '请求url',
  `request_param` varchar(3000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '请求参数',
  `request_date` datetime(0) DEFAULT NULL COMMENT '请求时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_search_parameter
-- ----------------------------
DROP TABLE IF EXISTS `t_search_parameter`;
CREATE TABLE `t_search_parameter`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `search_type` int(0) NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `del_flag` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_search_type
-- ----------------------------
DROP TABLE IF EXISTS `t_search_type`;
CREATE TABLE `t_search_type`  (
  `id` int(0) NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户　ID，系统唯一',
  `username` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '名称',
  `password` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '加密后密码（AES 加密）',
  `role` int(0) NOT NULL COMMENT '角色类型',
  `sex` int(0) NOT NULL COMMENT '性别',
  `title` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '职业或职称',
  `icon` int(0) NOT NULL COMMENT '系统头像 ID',
  `reg` bigint(0) NOT NULL COMMENT '注册时间',
  `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_username`(`username`) USING BTREE COMMENT '用户名唯一'
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user_icon
-- ----------------------------
DROP TABLE IF EXISTS `t_user_icon`;
CREATE TABLE `t_user_icon`  (
  `id` int(0) NOT NULL,
  `icon` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`  (
  `id` int(0) NOT NULL COMMENT '角色 ID，系统唯一',
  `role` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '角色类型',
  `auth` bit(8) NOT NULL COMMENT '角色权限码',
  `desc` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user_sex
-- ----------------------------
DROP TABLE IF EXISTS `t_user_sex`;
CREATE TABLE `t_user_sex`  (
  `id` int(0) NOT NULL,
  `desc` varchar(12) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
