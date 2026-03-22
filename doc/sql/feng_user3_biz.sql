-- =============================================
-- 数据库：feng_user3_biz
-- 描述：用户中心数据库脚本
-- mysql版本：8.4.7
-- 创建时间：2026-01-29
-- =============================================

DROP DATABASE IF EXISTS `feng_user3_biz`;

CREATE DATABASE  `feng_user3_biz` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `feng_user3_biz`;

--  1.独立字典表，性别等小记录或者不那么重要的字典放在sys_dict

-- 人员照片类型代码表
DROP TABLE IF EXISTS `dict_person_photo_type`;
CREATE TABLE `dict_person_photo_type` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID' ,
  `name` varchar(256) NOT NULL COMMENT '照片类型名称',
  `code` varchar(32) NOT NULL COMMENT '照片类型代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_photo_type_code` (`code`)
) ENGINE=InnoDB  COMMENT='人员照片类型代码表';

-- 初始化数据（test）
INSERT INTO `dict_person_photo_type` 
(`name`, `code`, `create_by`, `update_by`) 
VALUES
('人员正面照片', '11', 'admin', 'admin'),
('人员左侧面照片', '12', 'admin', 'admin'),
('人员右侧面照片', '13', 'admin', 'admin'),
('机读身份证照片', '21', 'admin', 'admin');


-- 汉语方言代码表
DROP TABLE IF EXISTS `dict_chinese_dialect`;
CREATE TABLE `dict_chinese_dialect` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `name` varchar(256) NOT NULL COMMENT '方言名称',
  `code` varchar(32) NOT NULL COMMENT '方言代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_dialect_code` (`code`)
) ENGINE=InnoDB  COMMENT='汉语方言代码表';

-- 初始化数据（test）
INSERT INTO `dict_chinese_dialect` (`name`, `code`, `create_by`, `update_by`) VALUES
('普通话', '00', 'admin', 'admin'),
('北方方言', '01', 'admin', 'admin'),
('吴方言', '02', 'admin', 'admin'),
('湘方言', '03', 'admin', 'admin'),
('赣方言', '04', 'admin', 'admin'),
('客家话', '05', 'admin', 'admin'),
('闽方言', '06', 'admin', 'admin'),
('粤方言', '07', 'admin', 'admin'),
('其他方言', '99', 'admin', 'admin');


-- 足迹部位代码表
DROP TABLE IF EXISTS `dict_footprint_position`;
CREATE TABLE `dict_footprint_position` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `name` varchar(256) NOT NULL COMMENT '足迹部位名称',
  `code` varchar(32) NOT NULL COMMENT '足迹部位代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_footprint_position_code` (`code`)
) ENGINE=InnoDB  COMMENT='足迹部位代码表';

-- 初始化数据（test）
INSERT INTO `dict_footprint_position` (`name`, `code`, `create_by`, `update_by`) VALUES
('左足', '0', 'admin', 'admin'),
('右足', '1', 'admin', 'admin'),
('未知', '9', 'admin', 'admin');

-- 足迹类型代码表
DROP TABLE IF EXISTS `dict_footprint_type`;
CREATE TABLE `dict_footprint_type` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `name` varchar(256) NOT NULL COMMENT '足迹类型名称',
  `code` varchar(32) NOT NULL COMMENT '足迹类型代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_footprint_type_code` (`code`)
) ENGINE=InnoDB  COMMENT='足迹类型代码表';

-- 初始化数据（test）
INSERT INTO `dict_footprint_type` (`name`, `code`, `create_by`, `update_by`) VALUES
('赤足', '0', 'admin', 'admin'),
('穿鞋', '1', 'admin', 'admin'),
('鞋面', '2', 'admin', 'admin'),
('未知', '9', 'admin', 'admin');

-- 常用证件代码表
DROP TABLE IF EXISTS `dict_common_certificate`;
CREATE TABLE `dict_common_certificate` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `name` varchar(256) NOT NULL COMMENT '证件类型名称',
  `code` varchar(3) NOT NULL COMMENT '证件类型代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_common_certificate_code` (`code`)
) ENGINE=InnoDB  COMMENT='常用证件代码表';

-- 初始化数据（test）
INSERT INTO `dict_common_certificate` (`name`, `code`, `create_by`, `update_by`) VALUES
('居民身份证', '111', 'admin', 'admin'),
('临时居民身份证', '112', 'admin', 'admin'),
('护照', '414', 'admin', 'admin'),
('港澳居民来往内地通行证', '113', 'admin', 'admin'),
('台湾居民来往大陆通行证', '114', 'admin', 'admin');

-- 国籍代码表
DROP TABLE IF EXISTS `dict_nationality`;
CREATE TABLE `dict_nationality` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `name` varchar(256) NOT NULL COMMENT '国籍名称',
  `code` varchar(3) NOT NULL COMMENT '国籍代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_nationality_code` (`code`)
) ENGINE=InnoDB  COMMENT='国籍代码表(GB/T 2659-2000)';

-- 初始化数据（test）
INSERT INTO `dict_nationality` VALUES (1, '阿富汗  ', '004', '-', NULL, '2025-11-18 15:32:03', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (2, '阿尔巴尼亚    ', '008', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (3, '南极洲  ', '010', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (4, '阿尔及利亚    ', '012', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (5, '美属萨摩亚    ', '016', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (6, '安道尔  ', '020', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (7, '安哥拉  ', '024', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (8, '安提瓜与巴布达  ', '028', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (9, '阿塞拜疆 ', '031', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (10, '阿根廷 ', '032', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (11, '澳大利亚    ', '036', '-', NULL, '2025-11-18 15:32:04', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (12, '奥地利 ', '040', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (13, '巴哈马 ', '044', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (14, '巴林  ', '048', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (15, '孟加拉国    ', '050', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (16, '亚美尼亚    ', '051', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (17, '巴巴多斯    ', '052', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (18, '比利时 ', '056', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (19, '百慕大 ', '060', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (20, '不丹  ', '064', '-', NULL, '2025-11-18 15:32:05', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (21, '玻利维亚    ', '068', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (22, '波黑  ', '070', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (23, '博茨瓦纳    ', '072', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (24, '布维岛 ', '074', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (25, '巴西  ', '076', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (26, '伯利兹 ', '084', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (27, '英属印度洋领地 ', '086', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (28, '所罗门群岛   ', '090', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (29, '英属维尔京群岛 ', '092', '-', NULL, '2025-11-18 15:32:06', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (30, '文莱  ', '096', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (31, '保加利亚    ', '100', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (32, '缅甸  ', '104', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (33, '布隆迪 ', '108', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (34, '白俄罗斯    ', '112', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (35, '柬埔寨 ', '116', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (36, '喀麦隆 ', '120', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (37, '加拿大 ', '124', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (38, '佛得角 ', '132', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (39, '开曼群岛    ', '136', '-', NULL, '2025-11-18 15:32:07', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (40, '中非  ', '140', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (41, '斯里兰卡    ', '144', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (42, '乍得  ', '148', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (43, '智利  ', '152', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (44, '中国  ', '156', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (45, '台湾  ', '158', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (46, '圣诞岛 ', '162', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (47, '科科斯(基林)群岛   ', '166', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (48, '哥伦比亚    ', '170', '-', NULL, '2025-11-18 15:32:08', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (49, '科摩罗 ', '174', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (50, '马约特 ', '175', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (51, '刚果(布)   ', '178', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (52, '刚果(金)   ', '180', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (53, '库克群岛    ', '184', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (54, '哥斯达黎加   ', '188', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (55, '克罗地亚    ', '191', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (56, '古巴  ', '192', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (57, '塞浦路斯    ', '196', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (58, '捷克  ', '203', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (59, '贝宁  ', '204', '-', NULL, '2025-11-18 15:32:09', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (60, '丹麦  ', '208', '-', NULL, '2025-11-18 15:32:10', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (61, '多米尼克    ', '212', '-', NULL, '2025-11-18 15:32:10', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (62, '多米尼加    ', '214', '-', NULL, '2025-11-18 15:32:10', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (63, '厄瓜多尔    ', '218', '-', NULL, '2025-11-18 15:32:10', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (64, '萨尔瓦多    ', '222', '-', NULL, '2025-11-18 15:32:10', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (65, '赤道几内亚   ', '226', '-', NULL, '2025-11-18 15:32:10', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (66, '埃塞俄比亚   ', '231', '-', NULL, '2025-11-18 15:32:10', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (67, '厄立特里亚   ', '232', '-', NULL, '2025-11-18 15:32:10', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (68, '爱沙尼亚    ', '233', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (69, '法罗群岛    ', '234', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (70, '福克兰群岛(马尔维纳斯', '238', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (71, '南乔治亚岛和南桑德韦奇', '239', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (72, '斐济  ', '242', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (73, '芬兰  ', '246', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (74, '法国  ', '250', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (75, '法属圭亚那   ', '254', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (76, '法属波利尼西亚 ', '258', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (77, '法属南部领地  ', '260', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (78, '吉布提 ', '262', '-', NULL, '2025-11-18 15:32:11', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (79, '加蓬  ', '266', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (80, '格鲁吉亚    ', '268', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (81, '冈比亚 ', '270', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (82, '巴勒斯坦    ', '275', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (83, '德国  ', '276', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (84, '加纳  ', '288', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (85, '直布罗陀    ', '292', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (86, '基里巴斯    ', '296', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (87, '希腊  ', '300', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (88, '格陵兰 ', '304', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (89, '格林纳达    ', '308', '-', NULL, '2025-11-18 15:32:12', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (90, '瓜德罗普    ', '312', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (91, '关岛  ', '316', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (92, '危地马拉    ', '320', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (93, '几内亚 ', '324', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (94, '圭亚那 ', '328', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (95, '海地  ', '332', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (96, '赫德岛和麦克唐纳岛   ', '334', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (97, '梵蒂冈 ', '336', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (98, '洪都拉斯    ', '340', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (99, '香港  ', '344', '-', NULL, '2025-11-18 15:32:13', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (100, '匈牙利    ', '348', '-', NULL, '2025-11-18 15:32:14', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (101, '冰岛 ', '352', '-', NULL, '2025-11-18 15:32:14', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (102, '印度 ', '356', '-', NULL, '2025-11-18 15:32:14', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (103, '印度尼西亚  ', '360', '-', NULL, '2025-11-18 15:32:14', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (104, '伊朗 ', '364', '-', NULL, '2025-11-18 15:32:14', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (105, '伊拉克    ', '368', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (106, '爱尔兰    ', '372', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (107, '以色列    ', '376', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (108, '意大利    ', '380', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (109, '科特迪瓦   ', '384', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (110, '牙买加    ', '388', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (111, '日本 ', '392', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (112, '哈萨克斯坦  ', '398', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (113, '约旦 ', '400', '-', NULL, '2025-11-18 15:32:15', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (114, '肯尼亚    ', '404', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (115, '朝鲜 ', '408', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (116, '韩国 ', '410', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (117, '科威特    ', '414', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (118, '吉尔吉斯斯坦 ', '417', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (119, '老挝 ', '418', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (120, '黎巴嫩    ', '422', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (121, '莱索托    ', '426', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (122, '拉脱维亚   ', '428', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (123, '利比里亚   ', '430', '-', NULL, '2025-11-18 15:32:16', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (124, '利比亚    ', '434', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (125, '列支敦士登  ', '438', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (126, '立陶宛    ', '440', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (127, '卢森堡    ', '442', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (128, '澳门 ', '446', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (129, '马达加斯加  ', '450', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (130, '马拉维    ', '454', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (131, '马来西亚   ', '458', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (132, '马尔代夫   ', '462', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (133, '马里 ', '466', '-', NULL, '2025-11-18 15:32:17', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (134, '马耳他    ', '470', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (135, '马提尼克   ', '474', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (136, '毛里塔尼亚  ', '478', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (137, '毛里求斯   ', '480', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (138, '墨西哥    ', '484', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (139, '摩纳哥    ', '492', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (140, '蒙古 ', '496', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (141, '摩尔多瓦   ', '498', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (142, '蒙特塞拉特  ', '500', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (143, '摩洛哥    ', '504', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (144, '莫桑比克   ', '508', '-', NULL, '2025-11-18 15:32:18', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (145, '阿曼 ', '512', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (146, '纳米比亚   ', '516', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (147, '瑙鲁 ', '520', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (148, '尼泊尔    ', '524', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (149, '荷兰 ', '528', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (150, '荷属安的列斯 ', '530', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (151, '阿鲁巴    ', '533', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (152, '新喀里多尼亚 ', '540', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (153, '瓦努阿图   ', '548', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (154, '新西兰    ', '554', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (155, '尼加拉瓜   ', '558', '-', NULL, '2025-11-18 15:32:19', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (156, '尼日尔    ', '562', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (157, '尼日利亚   ', '566', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (158, '纽埃 ', '570', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (159, '诺福克岛   ', '574', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (160, '挪威 ', '578', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (161, '北马里亚纳  ', '580', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (162, '美国本土外小岛屿   ', '581', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (163, '密克罗尼西亚联邦   ', '583', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (164, '马绍尔群岛  ', '584', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (165, '帕劳 ', '585', '-', NULL, '2025-11-18 15:32:20', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (166, '巴基斯坦   ', '586', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (167, '巴拿马    ', '591', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (168, '巴布亚新几内亚    ', '598', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (169, '巴拉圭    ', '600', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (170, '秘鲁 ', '604', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (171, '菲律宾    ', '608', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (172, '皮特凯恩   ', '612', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (173, '波兰 ', '616', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (174, '葡萄牙    ', '620', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (175, '几内亚比绍  ', '624', '-', NULL, '2025-11-18 15:32:21', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (176, '东帝汉    ', '626', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (177, '波多黎各   ', '630', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (178, '卡塔尔    ', '634', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (179, '留尼汪    ', '638', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (180, '罗马尼亚   ', '642', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (181, '俄罗斯联邦  ', '643', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (182, '卢旺达    ', '646', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (183, '圣赫勒拿   ', '654', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (184, '圣基茨和尼维斯    ', '659', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (185, '安圭拉    ', '660', '-', NULL, '2025-11-18 15:32:22', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (186, '圣卢西亚   ', '662', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (187, '圣皮埃尔和密克隆   ', '666', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (188, '圣文森特和格林纳丁斯', '670', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (189, '圣马力诺   ', '674', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (190, '圣多美和普林西比   ', '678', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (191, '沙特阿拉伯  ', '682', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (192, '塞内加尔   ', '686', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (193, '塞舌尔    ', '690', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (194, '塞拉利昂   ', '694', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (195, '新加坡    ', '702', '-', NULL, '2025-11-18 15:32:23', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (196, '斯洛伐克   ', '703', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (197, '越南 ', '704', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (198, '斯洛文尼亚  ', '705', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (199, '索马里    ', '706', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (200, '南非 ', '710', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (201, '津巴布韦   ', '716', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (202, '西班牙    ', '724', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (203, '西撒哈拉   ', '732', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (204, '苏丹 ', '736', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (205, '苏里南    ', '740', '-', NULL, '2025-11-18 15:32:24', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (206, '斯瓦尔巴岛和扬马延岛', '744', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (207, '斯威士兰   ', '748', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (208, '瑞典 ', '752', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (209, '瑞士 ', '756', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (210, '叙利亚    ', '760', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (211, '塔吉克斯坦  ', '762', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (212, '泰国 ', '764', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (213, '多哥 ', '768', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (214, '托克劳    ', '772', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (215, '汤加 ', '776', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (216, '特立尼达和多巴哥   ', '780', '-', NULL, '2025-11-18 15:32:25', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (217, '阿联酋    ', '784', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (218, '突尼斯    ', '788', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (219, '土耳其    ', '792', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (220, '土库曼斯坦  ', '795', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (221, '特克斯和凯科斯群岛  ', '796', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (222, '图瓦卢    ', '798', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (223, '乌干达    ', '800', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (224, '乌克兰    ', '804', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (225, '前南马其顿  ', '807', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (226, '埃及 ', '818', '-', NULL, '2025-11-18 15:32:26', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (227, '英国 ', '826', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (228, '坦桑尼亚   ', '834', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (229, '美国 ', '840', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (230, '美属维尔京群岛    ', '850', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (231, '布基纳法索  ', '854', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (232, '乌拉圭    ', '858', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (233, '乌兹别克斯坦 ', '860', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (234, '委内瑞拉   ', '862', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (235, '瓦利斯和富图纳    ', '876', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (236, '萨摩亚    ', '882', '-', NULL, '2025-11-18 15:32:27', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (237, '也门 ', '887', '-', NULL, '2025-11-18 15:32:28', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (238, '南斯拉夫   ', '891', '-', NULL, '2025-11-18 15:32:28', NULL, NULL, '0');
INSERT INTO `dict_nationality` VALUES (239, '赞比亚    ', '894', '-', NULL, '2025-11-18 15:32:28', NULL, NULL, '0');

-- 民族代码表
DROP TABLE IF EXISTS `dict_ethnic_group`;
CREATE TABLE `dict_ethnic_group` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `name` varchar(256) NOT NULL COMMENT '民族名称',
  `code` char(2) NOT NULL COMMENT '民族代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_ethnic_group_code` (`code`)
) ENGINE=InnoDB  COMMENT='民族代码表(GB/T 3304-1991)';

-- 初始化数据（test）
INSERT INTO `dict_ethnic_group` VALUES (1, '1 - 汉族  ', '1', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (2, '2 - 蒙古族 ', '2', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (3, '3 - 回族  ', '3', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (4, '4 - 藏族  ', '4', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (5, '5 - 维吾尔族    ', '5', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (6, '6 - 苗族  ', '6', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (7, '7 - 彝族  ', '7', '-', 'admin', '2025-11-18 15:24:11', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (8, '8 - 壮族  ', '8', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:48', '0');
INSERT INTO `dict_ethnic_group` VALUES (9, '9 - 布依族 ', '9', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (10, '10 - 朝鲜族   ', '10', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (11, '11 - 满族    ', '11', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (12, '12 - 侗族    ', '12', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (13, '13 - 瑶族    ', '13', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (14, '14 - 白族    ', '14', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (15, '15 - 土家族   ', '15', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (16, '16 - 哈尼族   ', '16', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (17, '17 - 哈萨克族  ', '17', '-', 'admin', '2025-11-18 15:24:12', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (18, '18 - 傣族    ', '18', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:49', '0');
INSERT INTO `dict_ethnic_group` VALUES (19, '19 - 黎族    ', '19', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (20, '20 - 傈僳族   ', '20', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (21, '21 - 佤族    ', '21', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (22, '22 - 畲族    ', '22', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (23, '23 - 高山族   ', '23', '-', 'admin', '2025-11-18 15:24:13', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (24, '24 - 拉祜族   ', '24', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (25, '25 - 水族    ', '25', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (26, '26 - 东乡族   ', '26', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (27, '27 - 纳西族   ', '27', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (28, '28 - 景颇族   ', '28', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (29, '29 - 柯尔克孜族 ', '29', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:50', '0');
INSERT INTO `dict_ethnic_group` VALUES (30, '30 - 土族    ', '30', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (31, '31 - 达斡尔族  ', '31', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (32, '32 - 仫佬族   ', '32', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (33, '33 - 羌族    ', '33', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (34, '34 - 布朗族   ', '34', '-', 'admin', '2025-11-18 15:24:14', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (35, '35 - 撒拉族   ', '35', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (36, '36 - 毛难族   ', '36', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (37, '37 - 仡佬族   ', '37', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (38, '38 - 锡伯族   ', '38', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (39, '39 - 阿昌族   ', '39', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:51', '0');
INSERT INTO `dict_ethnic_group` VALUES (40, '40 - 普米族   ', '40', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (41, '41 - 塔吉克族  ', '41', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (42, '42 - 怒族    ', '42', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (43, '43 - 乌孜别克族 ', '43', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (44, '44 - 俄罗斯族  ', '44', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (45, '45 - 鄂温克族  ', '45', '-', 'admin', '2025-11-18 15:24:15', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (46, '46 - 德昂族   ', '46', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (47, '47 - 保安族   ', '47', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (48, '48 - 裕固族   ', '48', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (49, '49 - 京族    ', '49', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (50, '50 - 塔塔尔族  ', '50', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:52', '0');
INSERT INTO `dict_ethnic_group` VALUES (51, '51 - 独龙族   ', '51', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:53', '0');
INSERT INTO `dict_ethnic_group` VALUES (52, '52 - 鄂伦春族  ', '52', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:53', '0');
INSERT INTO `dict_ethnic_group` VALUES (53, '53 - 赫哲族   ', '53', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:53', '0');
INSERT INTO `dict_ethnic_group` VALUES (54, '54 - 门巴族   ', '54', '-', 'admin', '2025-11-18 15:24:16', NULL, '2025-11-18 15:24:53', '0');
INSERT INTO `dict_ethnic_group` VALUES (55, '55 - 珞巴族   ', '55', '-', 'admin', '2025-11-18 15:24:16', NULL, NULL, '0');
INSERT INTO `dict_ethnic_group` VALUES (56, '56 - 基诺族   ', '56', '-', 'admin', '2025-11-18 15:24:17', NULL, NULL, '0');
INSERT INTO `dict_ethnic_group` VALUES (57, '66 - 其他    ', '66', '-', 'admin', '2025-11-18 15:24:17', NULL, NULL, '0');
INSERT INTO `dict_ethnic_group` VALUES (58, '99 - 外籍人士  ', '99', '-', 'admin', '2025-11-18 15:24:17', NULL, NULL, '0');

-- 行政区划代码表 初始记录见dict_administrative_division.sql
DROP TABLE IF EXISTS `dict_administrative_division`;
CREATE TABLE `dict_administrative_division` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `name` varchar(256) NOT NULL COMMENT '行政区划名称',
  `code` varchar(32) NOT NULL COMMENT '行政区划代码',
  `level` tinyint(1) NOT NULL COMMENT '行政级别(1:省级,2:市级,3:县级)',
  `parent_code` varchar(32) DEFAULT NULL COMMENT '上级行政区划代码',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_division_code` (`code`),
  KEY `idx_division_parent` (`parent_code`)
) ENGINE=InnoDB  COMMENT='行政区划代码表(GB/T 2260-2013)';

-- 机关代码 （政府单位、民间组织等），映射到sys_dept
DROP TABLE IF EXISTS `gov_agency`;
CREATE TABLE `gov_agency` (
  `id` varchar(64) NOT NULL COMMENT '主键UUID',
  `name` varchar(256) NOT NULL COMMENT '机构名称',
  `code` varchar(64) NOT NULL COMMENT '机构代码',
  `parent_code` varchar(64) DEFAULT NULL COMMENT '上级机构代码',
  `level` tinyint(1) NOT NULL COMMENT '机构级别',
  `remark` varchar(1024) DEFAULT '-' COMMENT '备注说明',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_police_agency_code` (`code`),
  KEY `idx_police_agency_parent` (`parent_code`)
) ENGINE=InnoDB  COMMENT='机关代码表（政府单位、民间组织等）';

INSERT INTO `gov_agency` (`id`, `name`, `code`, `parent_code`, `level`, `remark`)
VALUES (UUID(), '河北省应急管理厅', 'HEB_YJGLT', NULL, 1, '河北省应急管理主管机构');
INSERT INTO `gov_agency` (`id`, `name`, `code`, `parent_code`, `level`, `remark`)
VALUES (UUID(), '石家庄市应急管理局', 'SJZ_YJGLJ', 'HEB_YJGLT', 2, '石家庄市应急管理主管机构');

-- 统一机构信息表，对接统一认证中心
DROP TABLE IF EXISTS `unique_org`;
CREATE TABLE `unique_org` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `org_id` varchar(64) NOT NULL COMMENT '统一机构编号',
  `org_name` varchar(256) NOT NULL COMMENT '统一机构名称',
  `org_code` varchar(64) NOT NULL COMMENT '统一机构代码',
  `sname` varchar(128) DEFAULT NULL COMMENT '简称',
  `fname` varchar(512) DEFAULT NULL COMMENT '全称',
  `division_code` varchar(64) DEFAULT NULL COMMENT '区域代码',
  `parent_id` varchar(64) DEFAULT NULL COMMENT '上级机构编号',
  `address` varchar(512) DEFAULT NULL COMMENT '地址',
  `office_tel` varchar(32) DEFAULT NULL COMMENT '办公电话',
  `email` varchar(128) DEFAULT NULL COMMENT '电子邮件',
  `order_id` varchar(64) DEFAULT NULL COMMENT '排序Id',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  KEY `idx_org_code` (`org_code`),
  KEY `idx_org_parent` (`parent_id`),
  KEY `idx_org_region` (`division_code`)
) ENGINE=InnoDB  COMMENT='统一机构信息表';

INSERT INTO `unique_org` (`org_id`, `org_name`, `org_code`, `sname`, `fname`, `division_code`, `parent_id`, `address`, `office_tel`, `email`, `order_id`)
VALUES ('ORG001', '石家庄市消防救援支队', '130100000000', '石家庄消防', '石家庄市消防救援支队', '130100', NULL, '石家庄市长安区北二环东路111号', '0311-12345678', 'sjzxf@example.com', '10');
INSERT INTO `unique_org` (`org_id`, `org_name`, `org_code`, `sname`, `fname`, `division_code`, `parent_id`, `address`, `office_tel`, `email`, `order_id`)
VALUES ('ORG002', '石家庄市消防救援支队桥西区大队', '130104000000', '桥西消防', '石家庄市消防救援支队桥西区大队', '130104', 'ORG001', '石家庄市桥西区槐安西路100号', '0311-87654321', 'qxfd@example.com', '20');

-- 统一用户信息表，对接统一认证中心，映射到sys_user
DROP TABLE IF EXISTS `unique_user`;
CREATE TABLE `unique_user` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `app_key` varchar(64) DEFAULT NULL COMMENT '应用代码',
  `login_id` varchar(64) DEFAULT NULL COMMENT '用户账号',
  `sys_user_id` varchar(64) DEFAULT NULL COMMENT '用户记录ID，统一消息中心账号的ID，开通后台账户时回写',
  `nickname` varchar(128) DEFAULT NULL COMMENT '用户昵称',
  `name` varchar(128) DEFAULT NULL COMMENT '用户姓名',
  `type` char(1) NOT NULL COMMENT '用户类型(0-个人用户 1-单位用户 2-其他)',
  `unique_roles` varchar(1024) DEFAULT NULL COMMENT '用户拥有的公共角色以及在当前应用下拥有的角色，多个角色逗号隔开',
  `sys_role_id` varchar(64) DEFAULT NULL COMMENT '用户角色记录ID，统一消息中心角色的ID，开通后台账户时回写',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `division_code` varchar(64) DEFAULT NULL COMMENT '区域代码',
  `agency_code` varchar(64) DEFAULT NULL COMMENT '所属单位代码',
  `unique_org_code` varchar(64) DEFAULT NULL COMMENT '所属机构代码',
  `email` varchar(128) DEFAULT NULL COMMENT '用户邮箱',
  `mobile` varchar(128) DEFAULT NULL COMMENT '手机号码',
  `sex` int(3) DEFAULT NULL COMMENT '用户性别',
  `avatar` varchar(128) DEFAULT NULL COMMENT '用户头像',
  `login_ip` varchar(128) DEFAULT NULL COMMENT '最后登录IP',
  `access_token` varchar(64) NOT NULL COMMENT '访问令牌',
  `refresh_token` varchar(64) DEFAULT NULL COMMENT '刷新令牌',
  `expires_time` datetime DEFAULT NULL COMMENT 'token到期时间',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`login_id`),
  KEY `idx_user_type` (`type`),
  KEY `idx_user_region` (`division_code`),
  KEY `idx_id_card` (`id_card`)
) ENGINE=InnoDB  COMMENT='统一用户信息表';

INSERT INTO `unique_user` (`login_id`, `name`, `type`, `id_card`, `access_token`, `agency_code`, `unique_roles`, `mobile`) 
VALUES ('zhangsan', '张三', '0', '110101199001011234', 'init_token', 'SJZ_YJGLJ', 'ROLE_USER', '13585823603');
INSERT INTO `unique_user` (`login_id`, `name`, `type`, `id_card`, `access_token`, `agency_code`, `unique_roles`, `mobile`) 
VALUES ('lisi', '李四', '0', '110101199001011235', 'init_token', 'HEB_YJGLT', 'ROLE_USER,ROLE_ADMIN', '18696777215');

-- 统一角色信息表，对接统一认证中心，映射到sys_role
DROP TABLE IF EXISTS `unique_role`;
CREATE TABLE `unique_role` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '主键UUID',
  `role_id` varchar(64) NOT NULL COMMENT '统一角色编号',
  `name` varchar(128) NOT NULL COMMENT '角色名称',
  `code` varchar(64) DEFAULT NULL COMMENT '角色代码',
  `sort` int(11) NOT NULL COMMENT '显示顺序',
  `status` int(2) NOT NULL COMMENT '状态(0-禁用 1-启用)',
  `type` int(2) NOT NULL COMMENT '角色类型(参见dict_item -- RoleType)',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '逻辑删除标记(0:正常,1:删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_role_name` (`name`),
  UNIQUE KEY `idx_role_code` (`code`),
  KEY `idx_role_type` (`type`),
  KEY `idx_role_status` (`status`),
  KEY `idx_role_sort` (`sort`)
) ENGINE=InnoDB  COMMENT='统一角色信息表';

INSERT INTO `unique_role` (`role_id`, `name`, `code`, `sort`, `status`, `type`, `remark`) 
VALUES ('ROLE_USER', '普通用户', 'user', 1, 1, 1, '拥有基本消息发送和接收权限');

INSERT INTO `unique_role` (`role_id`, `name`, `code`, `sort`, `status`, `type`, `remark`) 
VALUES ('ROLE_ADMIN', '管理员', 'admin', 2, 1, 2, '拥有所有消息管理权限');

-- 2. 系统表

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '部门ID',
  `name` varchar(50)  DEFAULT NULL COMMENT '部门名称',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标志',
  `parent_id` bigint DEFAULT NULL COMMENT '父级部门ID',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB  COMMENT='部门管理';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` VALUES (1, '总裁办', 1, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:07:49', '0', 0);
INSERT INTO `sys_dept` VALUES (2, '技术部', 2, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 1);
INSERT INTO `sys_dept` VALUES (3, '市场部', 3, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 1);
INSERT INTO `sys_dept` VALUES (4, '销售部', 4, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 1);
INSERT INTO `sys_dept` VALUES (5, '财务部', 5, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 1);
INSERT INTO `sys_dept` VALUES (6, '人事行政部', 6, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:53:36', '1', 1);
INSERT INTO `sys_dept` VALUES (7, '研发部', 7, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 2);
INSERT INTO `sys_dept` VALUES (8, 'UI设计部', 11, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 7);
INSERT INTO `sys_dept` VALUES (9, '产品部', 12, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 2);
INSERT INTO `sys_dept` VALUES (10, '渠道部', 13, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 3);
INSERT INTO `sys_dept` VALUES (11, '推广部', 14, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 3);
INSERT INTO `sys_dept` VALUES (12, '客服部', 15, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 4);
INSERT INTO `sys_dept` VALUES (13, '财务会计部', 16, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 13:04:47', '0', 5);
INSERT INTO `sys_dept` VALUES (14, '审计风控部', 17, 'admin', 'admin', '2023-04-03 13:04:47', '2023-04-03 14:06:57', '0', 5);
COMMIT;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '编号',
  `dict_type` varchar(100)  DEFAULT NULL COMMENT '字典类型',
  `description` varchar(100)  DEFAULT NULL COMMENT '描述',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255)  DEFAULT NULL COMMENT '备注信息',
  `system_flag` char(1)  DEFAULT '0' COMMENT '系统标志',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sys_dict_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB  COMMENT='字典表';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict` VALUES (1, 'log_type', '日志类型', ' ', ' ', '2019-03-19 11:06:44', '2019-03-19 11:06:44', '异常、正常', '1', '0');
INSERT INTO `sys_dict` VALUES (2, 'social_type', '社交登录', ' ', ' ', '2019-03-19 11:09:44', '2019-03-19 11:09:44', '微信、QQ', '1', '0');
INSERT INTO `sys_dict` VALUES (3, 'job_type', '定时任务类型', ' ', ' ', '2019-03-19 11:22:21', '2019-03-19 11:22:21', 'quartz', '1', '0');
INSERT INTO `sys_dict` VALUES (4, 'job_status', '定时任务状态', ' ', ' ', '2019-03-19 11:24:57', '2019-03-19 11:24:57', '发布状态、运行状态', '1', '0');
INSERT INTO `sys_dict` VALUES (5, 'job_execute_status', '定时任务执行状态', ' ', ' ', '2019-03-19 11:26:15', '2019-03-19 11:26:15', '正常、异常', '1', '0');
INSERT INTO `sys_dict` VALUES (6, 'misfire_policy', '定时任务错失执行策略', ' ', ' ', '2019-03-19 11:27:19', '2019-03-19 11:27:19', '周期', '1', '0');
INSERT INTO `sys_dict` VALUES (7, 'gender', '性别', ' ', ' ', '2019-03-27 13:44:06', '2019-03-27 13:44:06', '微信用户性别', '1', '0');
INSERT INTO `sys_dict` VALUES (8, 'subscribe', '订阅状态', ' ', ' ', '2019-03-27 13:48:33', '2019-03-27 13:48:33', '公众号订阅状态', '1', '0');
INSERT INTO `sys_dict` VALUES (9, 'response_type', '回复', ' ', ' ', '2019-03-28 21:29:21', '2019-03-28 21:29:21', '微信消息是否已回复', '1', '0');
INSERT INTO `sys_dict` VALUES (10, 'param_type', '参数配置', ' ', ' ', '2019-04-29 18:20:47', '2019-04-29 18:20:47', '检索、原文、报表、安全、文档、消息、其他', '1', '0');
INSERT INTO `sys_dict` VALUES (11, 'status_type', '租户状态', ' ', ' ', '2019-05-15 16:31:08', '2019-05-15 16:31:08', '租户状态', '1', '0');
INSERT INTO `sys_dict` VALUES (12, 'dict_type', '字典类型', ' ', ' ', '2019-05-16 14:16:20', '2019-05-16 14:20:16', '系统类不能修改', '1', '0');
INSERT INTO `sys_dict` VALUES (13, 'channel_type', '支付类型', ' ', ' ', '2019-05-16 14:16:20', '2019-05-16 14:20:16', '系统类不能修改', '1', '0');
INSERT INTO `sys_dict` VALUES (14, 'grant_types', '授权类型', ' ', ' ', '2019-08-13 07:34:10', '2019-08-13 07:34:10', NULL, '1', '0');
INSERT INTO `sys_dict` VALUES (15, 'style_type', '前端风格', ' ', ' ', '2020-02-07 03:49:28', '2020-02-07 03:50:40', '0-Avue 1-element', '1', '0');
INSERT INTO `sys_dict` VALUES (16, 'captcha_flag_types', '验证码开关', ' ', ' ', '2020-11-18 06:53:25', '2020-11-18 06:53:25', '是否校验验证码', '1', '0');
INSERT INTO `sys_dict` VALUES (17, 'enc_flag_types', '前端密码加密', ' ', ' ', '2020-11-18 06:54:44', '2020-11-18 06:54:44', '前端密码是否加密传输', '1', '0');
INSERT INTO `sys_dict` VALUES (18, 'lock_flag', '用户状态', 'admin', ' ', '2023-02-01 16:55:31', NULL, NULL, '1', '0');
INSERT INTO `sys_dict` VALUES (19, 'ds_config_type', '数据连接类型', 'admin', ' ', '2023-02-06 18:36:59', NULL, NULL, '1', '0');
INSERT INTO `sys_dict` VALUES (20, 'common_status', '通用状态', 'admin', ' ', '2023-02-09 11:02:08', NULL, NULL, '1', '0');
INSERT INTO `sys_dict` VALUES (21, 'app_social_type', 'app社交登录', 'admin', ' ', '2023-02-10 11:11:06', NULL, 'app社交登录', '1', '0');
INSERT INTO `sys_dict` VALUES (22, 'yes_no_type', '是否', 'admin', ' ', '2023-02-20 23:25:04', NULL, NULL, '1', '0');
INSERT INTO `sys_dict` VALUES (23, 'repType', '微信消息类型', 'admin', ' ', '2023-02-24 15:08:25', NULL, NULL, '0', '0');
INSERT INTO `sys_dict` VALUES (24, 'leave_status', '请假状态', 'admin', ' ', '2023-03-02 22:50:15', NULL, NULL, '0', '0');
INSERT INTO `sys_dict` VALUES (25, 'schedule_type', '日程类型', 'admin', ' ', '2023-03-06 14:49:18', NULL, NULL, '0', '0');
INSERT INTO `sys_dict` VALUES (26, 'schedule_status', '日程状态', 'admin', ' ', '2023-03-06 14:52:57', NULL, NULL, '0', '0');
INSERT INTO `sys_dict` VALUES (27, 'ds_type', '代码生成器支持的数据库类型', 'admin', ' ', '2023-03-12 09:57:59', NULL, NULL, '1', '0');
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '编号',
  `dict_id` varchar(64) NOT NULL COMMENT '字典ID',
  `item_value` varchar(100)  DEFAULT NULL COMMENT '字典项值',
  `label` varchar(100)  DEFAULT NULL COMMENT '字典项名称',
  `dict_type` varchar(100)  DEFAULT NULL COMMENT '字典类型',
  `description` varchar(100)  DEFAULT NULL COMMENT '字典项描述',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序（升序）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(255)  DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sys_dict_value` (`item_value`) USING BTREE,
  KEY `sys_dict_label` (`label`) USING BTREE,
  KEY `sys_dict_item_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB  COMMENT='字典项';

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_item` VALUES (1, 1, '9', '异常', 'log_type', '日志异常', 1, ' ', ' ', '2019-03-19 11:08:59', '2019-03-25 12:49:13', '', '0');
INSERT INTO `sys_dict_item` VALUES (2, 1, '0', '正常', 'log_type', '日志正常', 0, ' ', ' ', '2019-03-19 11:09:17', '2019-03-25 12:49:18', '', '0');
INSERT INTO `sys_dict_item` VALUES (3, 2, 'WX', '微信', 'social_type', '微信登录', 0, ' ', ' ', '2019-03-19 11:10:02', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (4, 2, 'QQ', 'QQ', 'social_type', 'QQ登录', 1, ' ', ' ', '2019-03-19 11:10:14', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (5, 3, '1', 'java类', 'job_type', 'java类', 1, ' ', ' ', '2019-03-19 11:22:37', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (6, 3, '2', 'spring bean', 'job_type', 'spring bean容器实例', 2, ' ', ' ', '2019-03-19 11:23:05', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (7, 3, '9', '其他', 'job_type', '其他类型', 9, ' ', ' ', '2019-03-19 11:23:31', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (8, 3, '3', 'Rest 调用', 'job_type', 'Rest 调用', 3, ' ', ' ', '2019-03-19 11:23:57', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (9, 3, '4', 'jar', 'job_type', 'jar类型', 4, ' ', ' ', '2019-03-19 11:24:20', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (10, 4, '1', '未发布', 'job_status', '未发布', 1, ' ', ' ', '2019-03-19 11:25:18', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (11, 4, '2', '运行中', 'job_status', '运行中', 2, ' ', ' ', '2019-03-19 11:25:31', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (12, 4, '3', '暂停', 'job_status', '暂停', 3, ' ', ' ', '2019-03-19 11:25:42', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (13, 5, '0', '正常', 'job_execute_status', '正常', 0, ' ', ' ', '2019-03-19 11:26:27', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (14, 5, '1', '异常', 'job_execute_status', '异常', 1, ' ', ' ', '2019-03-19 11:26:41', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (15, 6, '1', '错失周期立即执行', 'misfire_policy', '错失周期立即执行', 1, ' ', ' ', '2019-03-19 11:27:45', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (16, 6, '2', '错失周期执行一次', 'misfire_policy', '错失周期执行一次', 2, ' ', ' ', '2019-03-19 11:27:57', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (17, 6, '3', '下周期执行', 'misfire_policy', '下周期执行', 3, ' ', ' ', '2019-03-19 11:28:08', '2019-03-25 12:49:36', '', '0');
INSERT INTO `sys_dict_item` VALUES (18, 7, '1', '男', 'gender', '微信-男', 0, ' ', ' ', '2019-03-27 13:45:13', '2019-03-27 13:45:13', '微信-男', '0');
INSERT INTO `sys_dict_item` VALUES (19, 7, '2', '女', 'gender', '女-微信', 1, ' ', ' ', '2019-03-27 13:45:34', '2019-03-27 13:45:34', '女-微信', '0');
INSERT INTO `sys_dict_item` VALUES (20, 7, '0', '未知', 'gender', 'x性别未知', 3, ' ', ' ', '2019-03-27 13:45:57', '2019-03-27 13:45:57', 'x性别未知', '0');
INSERT INTO `sys_dict_item` VALUES (21, 8, '0', '未关注', 'subscribe', '公众号-未关注', 0, ' ', ' ', '2019-03-27 13:49:07', '2019-03-27 13:49:07', '公众号-未关注', '0');
INSERT INTO `sys_dict_item` VALUES (22, 8, '1', '已关注', 'subscribe', '公众号-已关注', 1, ' ', ' ', '2019-03-27 13:49:26', '2019-03-27 13:49:26', '公众号-已关注', '0');
INSERT INTO `sys_dict_item` VALUES (23, 9, '0', '未回复', 'response_type', '微信消息-未回复', 0, ' ', ' ', '2019-03-28 21:29:47', '2019-03-28 21:29:47', '微信消息-未回复', '0');
INSERT INTO `sys_dict_item` VALUES (24, 9, '1', '已回复', 'response_type', '微信消息-已回复', 1, ' ', ' ', '2019-03-28 21:30:08', '2019-03-28 21:30:08', '微信消息-已回复', '0');
INSERT INTO `sys_dict_item` VALUES (25, 10, '1', '检索', 'param_type', '检索', 0, ' ', ' ', '2019-04-29 18:22:17', '2019-04-29 18:22:17', '检索', '0');
INSERT INTO `sys_dict_item` VALUES (26, 10, '2', '原文', 'param_type', '原文', 0, ' ', ' ', '2019-04-29 18:22:27', '2019-04-29 18:22:27', '原文', '0');
INSERT INTO `sys_dict_item` VALUES (27, 10, '3', '报表', 'param_type', '报表', 0, ' ', ' ', '2019-04-29 18:22:36', '2019-04-29 18:22:36', '报表', '0');
INSERT INTO `sys_dict_item` VALUES (28, 10, '4', '安全', 'param_type', '安全', 0, ' ', ' ', '2019-04-29 18:22:46', '2019-04-29 18:22:46', '安全', '0');
INSERT INTO `sys_dict_item` VALUES (29, 10, '5', '文档', 'param_type', '文档', 0, ' ', ' ', '2019-04-29 18:22:56', '2019-04-29 18:22:56', '文档', '0');
INSERT INTO `sys_dict_item` VALUES (30, 10, '6', '消息', 'param_type', '消息', 0, ' ', ' ', '2019-04-29 18:23:05', '2019-04-29 18:23:05', '消息', '0');
INSERT INTO `sys_dict_item` VALUES (31, 10, '9', '其他', 'param_type', '其他', 0, ' ', ' ', '2019-04-29 18:23:16', '2019-04-29 18:23:16', '其他', '0');
INSERT INTO `sys_dict_item` VALUES (32, 10, '0', '默认', 'param_type', '默认', 0, ' ', ' ', '2019-04-29 18:23:30', '2019-04-29 18:23:30', '默认', '0');
INSERT INTO `sys_dict_item` VALUES (33, 11, '0', '正常', 'status_type', '状态正常', 0, ' ', ' ', '2019-05-15 16:31:34', '2019-05-16 22:30:46', '状态正常', '0');
INSERT INTO `sys_dict_item` VALUES (34, 11, '9', '冻结', 'status_type', '状态冻结', 1, ' ', ' ', '2019-05-15 16:31:56', '2019-05-16 22:30:50', '状态冻结', '0');
INSERT INTO `sys_dict_item` VALUES (35, 12, '1', '系统类', 'dict_type', '系统类字典', 0, ' ', ' ', '2019-05-16 14:20:40', '2019-05-16 14:20:40', '不能修改删除', '0');
INSERT INTO `sys_dict_item` VALUES (36, 12, '0', '业务类', 'dict_type', '业务类字典', 0, ' ', ' ', '2019-05-16 14:20:59', '2019-05-16 14:20:59', '可以修改', '0');
INSERT INTO `sys_dict_item` VALUES (37, 2, 'GITEE', '码云', 'social_type', '码云', 2, ' ', ' ', '2019-06-28 09:59:12', '2019-06-28 09:59:12', '码云', '0');
INSERT INTO `sys_dict_item` VALUES (38, 2, 'OSC', '开源中国', 'social_type', '开源中国登录', 2, ' ', ' ', '2019-06-28 10:04:32', '2019-06-28 10:04:32', '', '0');
INSERT INTO `sys_dict_item` VALUES (39, 14, 'password', '密码模式', 'grant_types', '支持oauth密码模式', 0, ' ', ' ', '2019-08-13 07:35:28', '2019-08-13 07:35:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES (40, 14, 'authorization_code', '授权码模式', 'grant_types', 'oauth2 授权码模式', 1, ' ', ' ', '2019-08-13 07:36:07', '2019-08-13 07:36:07', NULL, '0');
INSERT INTO `sys_dict_item` VALUES (41, 14, 'client_credentials', '客户端模式', 'grant_types', 'oauth2 客户端模式', 2, ' ', ' ', '2019-08-13 07:36:30', '2019-08-13 07:36:30', NULL, '0');
INSERT INTO `sys_dict_item` VALUES (42, 14, 'refresh_token', '刷新模式', 'grant_types', 'oauth2 刷新token', 3, ' ', ' ', '2019-08-13 07:36:54', '2019-08-13 07:36:54', NULL, '0');
INSERT INTO `sys_dict_item` VALUES (43, 14, 'implicit', '简化模式', 'grant_types', 'oauth2 简化模式', 4, ' ', ' ', '2019-08-13 07:39:32', '2019-08-13 07:39:32', NULL, '0');
INSERT INTO `sys_dict_item` VALUES (44, 15, '0', 'Avue', 'style_type', 'Avue风格', 0, ' ', ' ', '2020-02-07 03:52:52', '2020-02-07 03:52:52', '', '0');
INSERT INTO `sys_dict_item` VALUES (45, 15, '1', 'element', 'style_type', 'element-ui', 1, ' ', ' ', '2020-02-07 03:53:12', '2020-02-07 03:53:12', '', '0');
INSERT INTO `sys_dict_item` VALUES (46, 16, '0', '关', 'captcha_flag_types', '不校验验证码', 0, ' ', ' ', '2020-11-18 06:53:58', '2020-11-18 06:53:58', '不校验验证码 -0', '0');
INSERT INTO `sys_dict_item` VALUES (47, 16, '1', '开', 'captcha_flag_types', '校验验证码', 1, ' ', ' ', '2020-11-18 06:54:15', '2020-11-18 06:54:15', '不校验验证码-1', '0');
INSERT INTO `sys_dict_item` VALUES (48, 17, '0', '否', 'enc_flag_types', '不加密', 0, ' ', ' ', '2020-11-18 06:55:31', '2020-11-18 06:55:31', '不加密-0', '0');
INSERT INTO `sys_dict_item` VALUES (49, 17, '1', '是', 'enc_flag_types', '加密', 1, ' ', ' ', '2020-11-18 06:55:51', '2020-11-18 06:55:51', '加密-1', '0');
INSERT INTO `sys_dict_item` VALUES (50, 13, 'MERGE_PAY', '聚合支付', 'channel_type', '聚合支付', 1, ' ', ' ', '2019-05-30 19:08:08', '2019-06-18 13:51:53', '聚合支付', '0');
INSERT INTO `sys_dict_item` VALUES (51, 2, 'CAS', 'CAS登录', 'social_type', 'CAS 单点登录系统', 3, ' ', ' ', '2022-02-18 13:56:25', '2022-02-18 13:56:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES (52, 2, 'DINGTALK', '钉钉', 'social_type', '钉钉', 3, ' ', ' ', '2022-02-18 13:56:25', '2022-02-18 13:56:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES (53, 2, 'WEIXIN_CP', '企业微信', 'social_type', '企业微信', 3, ' ', ' ', '2022-02-18 13:56:25', '2022-02-18 13:56:28', NULL, '0');
INSERT INTO `sys_dict_item` VALUES (54, 15, '2', 'APP', 'style_type', 'uview风格', 1, ' ', ' ', '2020-02-07 03:53:12', '2020-02-07 03:53:12', '', '0');
INSERT INTO `sys_dict_item` VALUES (55, 13, 'ALIPAY_WAP', '支付宝支付', 'channel_type', '支付宝支付', 1, ' ', ' ', '2019-05-30 19:08:08', '2019-06-18 13:51:53', '聚合支付', '0');
INSERT INTO `sys_dict_item` VALUES (56, 13, 'WEIXIN_MP', '微信支付', 'channel_type', '微信支付', 1, ' ', ' ', '2019-05-30 19:08:08', '2019-06-18 13:51:53', '聚合支付', '0');
INSERT INTO `sys_dict_item` VALUES (57, 14, 'mobile', 'mobile', 'grant_types', '移动端登录', 5, 'admin', ' ', '2023-01-29 17:21:42', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (58, 18, '0', '有效', 'lock_flag', '有效', 0, 'admin', ' ', '2023-02-01 16:56:00', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (59, 18, '9', '禁用', 'lock_flag', '禁用', 1, 'admin', ' ', '2023-02-01 16:56:09', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (60, 15, '4', 'vue3', 'style_type', 'element-plus', 4, 'admin', ' ', '2023-02-06 13:52:43', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (61, 19, '0', '主机', 'ds_config_type', '主机', 0, 'admin', ' ', '2023-02-06 18:37:23', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (62, 19, '1', 'JDBC', 'ds_config_type', 'jdbc', 2, 'admin', ' ', '2023-02-06 18:37:34', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (63, 20, 'false', '否', 'common_status', '否', 1, 'admin', ' ', '2023-02-09 11:02:39', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (64, 20, 'true', '是', 'common_status', '是', 2, 'admin', ' ', '2023-02-09 11:02:52', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (65, 21, 'MINI', '小程序', 'app_social_type', '小程序登录', 0, 'admin', ' ', '2023-02-10 11:11:41', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (66, 22, '0', '否', 'yes_no_type', '0', 0, 'admin', ' ', '2023-02-20 23:35:23', NULL, '0', '0');
INSERT INTO `sys_dict_item` VALUES (67, 22, '1', '是', 'yes_no_type', '1', 0, 'admin', ' ', '2023-02-20 23:35:37', NULL, '1', '0');
INSERT INTO `sys_dict_item` VALUES (69, 23, 'text', '文本', 'repType', '文本', 0, 'admin', ' ', '2023-02-24 15:08:45', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (70, 23, 'image', '图片', 'repType', '图片', 0, 'admin', ' ', '2023-02-24 15:08:56', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (71, 23, 'voice', '语音', 'repType', '语音', 0, 'admin', ' ', '2023-02-24 15:09:08', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (72, 23, 'video', '视频', 'repType', '视频', 0, 'admin', ' ', '2023-02-24 15:09:18', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (73, 23, 'shortvideo', '小视频', 'repType', '小视频', 0, 'admin', ' ', '2023-02-24 15:09:29', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (74, 23, 'location', '地理位置', 'repType', '地理位置', 0, 'admin', ' ', '2023-02-24 15:09:41', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (75, 23, 'link', '链接消息', 'repType', '链接消息', 0, 'admin', ' ', '2023-02-24 15:09:49', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (76, 23, 'event', '事件推送', 'repType', '事件推送', 0, 'admin', ' ', '2023-02-24 15:09:57', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (77, 24, '0', '未提交', 'leave_status', '未提交', 0, 'admin', ' ', '2023-03-02 22:50:45', NULL, '未提交', '0');
INSERT INTO `sys_dict_item` VALUES (78, 24, '1', '审批中', 'leave_status', '审批中', 0, 'admin', ' ', '2023-03-02 22:50:57', NULL, '审批中', '0');
INSERT INTO `sys_dict_item` VALUES (79, 24, '2', '完成', 'leave_status', '完成', 0, 'admin', ' ', '2023-03-02 22:51:06', NULL, '完成', '0');
INSERT INTO `sys_dict_item` VALUES (80, 24, '9', '驳回', 'leave_status', '驳回', 0, 'admin', ' ', '2023-03-02 22:51:20', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (81, 25, 'record', '日程记录', 'schedule_type', '日程记录', 0, 'admin', ' ', '2023-03-06 14:50:01', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (82, 25, 'plan', '计划', 'schedule_type', '计划类型', 0, 'admin', ' ', '2023-03-06 14:50:29', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (83, 26, '0', '计划中', 'schedule_status', '日程状态', 0, 'admin', ' ', '2023-03-06 14:53:18', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (84, 26, '1', '已开始', 'schedule_status', '已开始', 0, 'admin', ' ', '2023-03-06 14:53:33', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (85, 26, '3', '已结束', 'schedule_status', '已结束', 0, 'admin', ' ', '2023-03-06 14:53:41', NULL, NULL, '0');
INSERT INTO `sys_dict_item` VALUES (86, 27, 'mysql', 'mysql', 'ds_type', 'mysql', 0, 'admin', ' ', '2023-03-12 09:58:11', NULL, NULL, '0');
COMMIT;

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '编号',
  `file_name` varchar(100)  DEFAULT NULL COMMENT '文件名',
  `bucket_name` varchar(200)  DEFAULT NULL COMMENT '文件存储桶名称',
  `original` varchar(100)  DEFAULT NULL COMMENT '原始文件名',
  `type` varchar(50)  DEFAULT NULL COMMENT '文件类型',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '上传时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  COMMENT='文件管理表';

-- ----------------------------
-- Records of sys_file
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '编号',
  `log_type` char(1)  DEFAULT '0' COMMENT '日志类型',
  `title` varchar(255)  DEFAULT NULL COMMENT '日志标题',
  `service_id` varchar(32)  DEFAULT NULL COMMENT '服务ID',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remote_addr` varchar(255)  DEFAULT NULL COMMENT '远程地址',
  `user_agent` varchar(1000)  DEFAULT NULL COMMENT '用户代理',
  `request_uri` varchar(255)  DEFAULT NULL COMMENT '请求URI',
  `method` varchar(10)  DEFAULT NULL COMMENT '请求方法',
  `params` text  COMMENT '请求参数',
  `time` bigint DEFAULT NULL COMMENT '执行时间',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标志',
  `exception` text  COMMENT '异常信息',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sys_log_request_uri` (`request_uri`) USING BTREE,
  KEY `sys_log_type` (`log_type`) USING BTREE,
  KEY `sys_log_create_date` (`create_time`) USING BTREE
) ENGINE=InnoDB  COMMENT='日志表';


-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '菜单ID',
  `name` varchar(32)  DEFAULT NULL COMMENT '菜单名称',
  `en_name` varchar(128)  DEFAULT NULL COMMENT '英文名称',
  `permission` varchar(32)  DEFAULT NULL COMMENT '权限标识',
  `path` varchar(128)  DEFAULT NULL COMMENT '路由路径',
  `parent_id` varchar(64) DEFAULT NULL COMMENT '父菜单ID',
  `icon` varchar(64)  DEFAULT NULL COMMENT '菜单图标',
  `visible` char(1)  DEFAULT '1' COMMENT '是否可见，0隐藏，1显示',
  `sort_order` int DEFAULT '1' COMMENT '排序值，越小越靠前',
  `keep_alive` char(1)  DEFAULT '0' COMMENT '是否缓存，0否，1是',
  `embedded` char(1)  DEFAULT NULL COMMENT '是否内嵌，0否，1是',
  `menu_type` char(1)  DEFAULT '0' COMMENT '菜单类型，0目录，1菜单，2按钮',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标志，0未删除，1已删除',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB  COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` VALUES (1000, '权限管理', 'authorization', NULL, '/admin', -1, 'iconfont icon-icon-', '1', 0, '0', '0', '0', '', '2018-09-28 08:29:53', 'admin', '2023-03-12 22:32:52', '0');
INSERT INTO `sys_menu` VALUES (1100, '用户管理', 'user', NULL, '/admin/user/index', 1000, 'ele-User', '1', 1, '0', '0', '0', '', '2017-11-02 22:24:37', 'admin', '2023-07-05 10:28:22', '0');
INSERT INTO `sys_menu` VALUES (1101, '用户新增', NULL, 'sys_user_add', NULL, 1100, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 09:52:09', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1102, '用户修改', NULL, 'sys_user_edit', NULL, 1100, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 09:52:48', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1103, '用户删除', NULL, 'sys_user_del', NULL, 1100, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 09:54:01', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1104, '导入导出', NULL, 'sys_user_export', NULL, 1100, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 09:54:01', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1200, '菜单管理', 'menu', NULL, '/admin/menu/index', 1000, 'iconfont icon-caidan', '1', 2, '0', '0', '0', '', '2017-11-08 09:57:27', 'admin', '2023-07-05 10:28:17', '0');
INSERT INTO `sys_menu` VALUES (1201, '菜单新增', NULL, 'sys_menu_add', NULL, 1200, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 10:15:53', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1202, '菜单修改', NULL, 'sys_menu_edit', NULL, 1200, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 10:16:23', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1203, '菜单删除', NULL, 'sys_menu_del', NULL, 1200, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 10:16:43', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1300, '角色管理', 'role', NULL, '/admin/role/index', 1000, 'iconfont icon-gerenzhongxin', '1', 3, '0', NULL, '0', '', '2017-11-08 10:13:37', 'admin', '2023-07-05 10:28:13', '0');
INSERT INTO `sys_menu` VALUES (1301, '角色新增', NULL, 'sys_role_add', NULL, 1300, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 10:14:18', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1302, '角色修改', NULL, 'sys_role_edit', NULL, 1300, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 10:14:41', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1303, '角色删除', NULL, 'sys_role_del', NULL, 1300, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 10:14:59', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1304, '分配权限', NULL, 'sys_role_perm', NULL, 1300, NULL, '1', 1, '0', NULL, '1', ' ', '2018-04-20 07:22:55', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1305, '角色导入导出', NULL, 'sys_role_export', NULL, 1300, NULL, '1', 4, '0', NULL, '1', ' ', '2022-03-26 15:54:34', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (1400, '部门管理', 'dept', NULL, '/admin/dept/index', 1000, 'iconfont icon-zidingyibuju', '1', 4, '0', NULL, '0', '', '2018-01-20 13:17:19', 'admin', '2023-07-05 10:28:07', '0');
INSERT INTO `sys_menu` VALUES (1401, '部门新增', NULL, 'sys_dept_add', NULL, 1400, NULL, '1', 1, '0', NULL, '1', ' ', '2018-01-20 14:56:16', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1402, '部门修改', NULL, 'sys_dept_edit', NULL, 1400, NULL, '1', 1, '0', NULL, '1', ' ', '2018-01-20 14:56:59', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1403, '部门删除', NULL, 'sys_dept_del', NULL, 1400, NULL, '1', 1, '0', NULL, '1', ' ', '2018-01-20 14:57:28', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (1600, '岗位管理', 'post', NULL, '/admin/post/index', 1000, 'iconfont icon--chaifenhang', '1', 5, '1', '0', '0', '', '2022-03-26 13:04:14', 'admin', '2023-07-05 10:28:03', '0');
INSERT INTO `sys_menu` VALUES (1601, '岗位信息查看', NULL, 'sys_post_view', NULL, 1600, NULL, '1', 0, '0', NULL, '1', ' ', '2022-03-26 13:05:34', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (1602, '岗位信息新增', NULL, 'sys_post_add', NULL, 1600, NULL, '1', 1, '0', NULL, '1', ' ', '2022-03-26 13:06:00', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (1603, '岗位信息修改', NULL, 'sys_post_edit', NULL, 1600, NULL, '1', 2, '0', NULL, '1', ' ', '2022-03-26 13:06:31', ' ', '2022-03-26 13:06:38', '0');
INSERT INTO `sys_menu` VALUES (1604, '岗位信息删除', NULL, 'sys_post_del', NULL, 1600, NULL, '1', 3, '0', NULL, '1', ' ', '2022-03-26 13:06:31', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (1605, '岗位导入导出', NULL, 'sys_post_export', NULL, 1600, NULL, '1', 4, '0', NULL, '1', ' ', '2022-03-26 13:06:31', ' ', '2022-03-26 06:32:02', '0');
INSERT INTO `sys_menu` VALUES (2000, '系统管理', 'system', NULL, '/system', -1, 'iconfont icon-quanjushezhi_o', '1', 1, '0', NULL, '0', '', '2017-11-07 20:56:00', 'admin', '2023-07-05 10:27:58', '0');
INSERT INTO `sys_menu` VALUES (2001, '日志管理', 'log', NULL, '/admin/logs', 2000, 'ele-Cloudy', '1', 0, '0', '0', '0', 'admin', '2023-03-02 12:26:42', 'admin', '2023-07-05 10:27:53', '0');
INSERT INTO `sys_menu` VALUES (2100, '操作日志', 'operation', NULL, '/admin/log/index', 2001, 'iconfont icon-jinridaiban', '1', 2, '0', '0', '0', '', '2017-11-20 14:06:22', 'admin', '2023-07-05 10:27:49', '0');
INSERT INTO `sys_menu` VALUES (2101, '日志删除', NULL, 'sys_log_del', NULL, 2100, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-20 20:37:37', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (2102, '导入导出', NULL, 'sys_log_export', NULL, 2100, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-08 09:54:01', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (2200, '字典管理', 'dict', NULL, '/admin/dict/index', 2000, 'iconfont icon-zhongduancanshuchaxun', '1', 6, '0', NULL, '0', '', '2017-11-29 11:30:52', 'admin', '2023-07-05 10:27:37', '0');
INSERT INTO `sys_menu` VALUES (2201, '字典删除', NULL, 'sys_dict_del', NULL, 2200, NULL, '1', 1, '0', NULL, '1', ' ', '2017-11-29 11:30:11', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (2202, '字典新增', NULL, 'sys_dict_add', NULL, 2200, NULL, '1', 1, '0', NULL, '1', ' ', '2018-05-11 22:34:55', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (2203, '字典修改', NULL, 'sys_dict_edit', NULL, 2200, NULL, '1', 1, '0', NULL, '1', ' ', '2018-05-11 22:36:03', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (2210, '参数管理', 'parameter', NULL, '/admin/param/index', 2000, 'iconfont icon-wenducanshu-05', '1', 7, '1', NULL, '0', '', '2019-04-29 22:16:50', 'admin', '2023-02-16 15:24:51', '0');
INSERT INTO `sys_menu` VALUES (2211, '参数新增', NULL, 'sys_syspublicparam_add', NULL, 2210, NULL, '1', 1, '0', NULL, '1', ' ', '2019-04-29 22:17:36', ' ', '2020-03-24 08:57:11', '0');
INSERT INTO `sys_menu` VALUES (2212, '参数删除', NULL, 'sys_syspublicparam_del', NULL, 2210, NULL, '1', 1, '0', NULL, '1', ' ', '2019-04-29 22:17:55', ' ', '2020-03-24 08:57:12', '0');
INSERT INTO `sys_menu` VALUES (2213, '参数编辑', NULL, 'sys_syspublicparam_edit', NULL, 2210, NULL, '1', 1, '0', NULL, '1', ' ', '2019-04-29 22:18:14', ' ', '2020-03-24 08:57:13', '0');
INSERT INTO `sys_menu` VALUES (2300, '代码生成', 'code', NULL, '/gen/table/index', 9000, 'iconfont icon-zhongduancanshu', '1', 1, '0', '0', '0', '', '2018-01-20 13:17:19', 'admin', '2023-02-20 13:54:35', '0');
INSERT INTO `sys_menu` VALUES (2400, '终端管理', 'client', NULL, '/admin/client/index', 2000, 'iconfont icon-gongju', '1', 9, '1', NULL, '0', '', '2018-01-20 13:17:19', 'admin', '2023-02-16 15:25:28', '0');
INSERT INTO `sys_menu` VALUES (2401, '客户端新增', NULL, 'sys_client_add', NULL, 2400, '1', '1', 1, '0', NULL, '1', ' ', '2018-05-15 21:35:18', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (2402, '客户端修改', NULL, 'sys_client_edit', NULL, 2400, NULL, '1', 1, '0', NULL, '1', ' ', '2018-05-15 21:37:06', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (2403, '客户端删除', NULL, 'sys_client_del', NULL, 2400, NULL, '1', 1, '0', NULL, '1', ' ', '2018-05-15 21:39:16', ' ', '2021-05-25 03:12:55', '0');
INSERT INTO `sys_menu` VALUES (2600, '令牌管理', 'token', NULL, '/admin/token/index', 2000, 'ele-Key', '1', 11, '0', NULL, '0', '', '2018-09-04 05:58:41', 'admin', '2023-02-16 15:28:28', '0');
INSERT INTO `sys_menu` VALUES (2601, '令牌删除', NULL, 'sys_token_del', NULL, 2600, NULL, '1', 1, '0', NULL, '1', ' ', '2018-09-04 05:59:50', ' ', '2020-03-24 08:57:24', '0');
INSERT INTO `sys_menu` VALUES (2800, 'Quartz管理', 'quartz', NULL, '/daemon/job-manage/index', 2000, 'ele-AlarmClock', '1', 8, '0', NULL, '0', '', '2018-01-20 13:17:19', 'admin', '2023-02-16 15:25:06', '0');
INSERT INTO `sys_menu` VALUES (2810, '任务新增', NULL, 'job_sys_job_add', NULL, 2800, '1', '1', 0, '0', NULL, '1', ' ', '2018-05-15 21:35:18', ' ', '2020-03-24 08:57:26', '0');
INSERT INTO `sys_menu` VALUES (2820, '任务修改', NULL, 'job_sys_job_edit', NULL, 2800, '1', '1', 0, '0', NULL, '1', ' ', '2018-05-15 21:35:18', ' ', '2020-03-24 08:57:27', '0');
INSERT INTO `sys_menu` VALUES (2830, '任务删除', NULL, 'job_sys_job_del', NULL, 2800, '1', '1', 0, '0', NULL, '1', ' ', '2018-05-15 21:35:18', ' ', '2020-03-24 08:57:28', '0');
INSERT INTO `sys_menu` VALUES (2840, '任务暂停', NULL, 'job_sys_job_shutdown_job', NULL, 2800, '1', '1', 0, '0', NULL, '1', ' ', '2018-05-15 21:35:18', ' ', '2020-03-24 08:57:28', '0');
INSERT INTO `sys_menu` VALUES (2850, '任务开始', NULL, 'job_sys_job_start_job', NULL, 2800, '1', '1', 0, '0', NULL, '1', ' ', '2018-05-15 21:35:18', ' ', '2020-03-24 08:57:29', '0');
INSERT INTO `sys_menu` VALUES (2860, '任务刷新', NULL, 'job_sys_job_refresh_job', NULL, 2800, '1', '1', 0, '0', NULL, '1', ' ', '2018-05-15 21:35:18', ' ', '2020-03-24 08:57:30', '0');
INSERT INTO `sys_menu` VALUES (2870, '执行任务', NULL, 'job_sys_job_run_job', NULL, 2800, '1', '1', 0, '0', NULL, '1', ' ', '2019-08-08 15:35:18', ' ', '2020-03-24 08:57:31', '0');
INSERT INTO `sys_menu` VALUES (2871, '导出', NULL, 'job_sys_job_export', NULL, 2800, NULL, '1', 0, '0', '0', '1', 'admin', '2023-03-06 15:26:13', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (2906, '文件管理', 'file', NULL, '/admin/file/index', 2000, 'ele-Files', '1', 6, '0', NULL, '0', '', '2019-06-25 12:44:46', 'admin', '2023-02-16 15:24:42', '0');
INSERT INTO `sys_menu` VALUES (2907, '删除文件', NULL, 'sys_file_del', NULL, 2906, NULL, '1', 1, '0', NULL, '1', ' ', '2019-06-25 13:41:41', ' ', '2020-03-24 08:58:42', '0');
INSERT INTO `sys_menu` VALUES (4000, '系统监控', 'monitor', NULL, '/daemon', -1, 'iconfont icon-shuju', '1', 3, '0', '0', '0', 'admin', '2023-02-06 20:20:47', 'admin', '2023-02-23 20:01:07', '0');
INSERT INTO `sys_menu` VALUES (4001, '文档扩展', 'doc', NULL, 'http://feng-gateway3:9999/swagger-ui.html', 4000, 'iconfont icon-biaodan', '1', 2, '0', '1', '0', '', '2018-06-26 10:50:32', 'admin', '2023-02-23 20:01:29', '0');
INSERT INTO `sys_menu` VALUES (4002, '缓存监控', 'cache', NULL, '/ext/cache', 4000, 'iconfont icon-shuju', '1', 1, '0', '0', '0', 'admin', '2023-05-29 15:12:59', 'admin', '2023-06-06 11:58:41', '0');
INSERT INTO `sys_menu` VALUES (9000, '开发平台', 'develop', NULL, '/gen', -1, 'iconfont icon-shuxingtu', '1', 9, '0', '0', '0', '', '2019-08-12 09:35:16', 'admin', '2023-07-05 10:25:27', '0');
INSERT INTO `sys_menu` VALUES (9005, '数据源管理', 'datasource', NULL, '/gen/datasource/index', 9000, 'ele-Coin', '1', 0, '0', NULL, '0', '', '2019-08-12 09:42:11', 'admin', '2023-07-05 10:26:56', '0');
INSERT INTO `sys_menu` VALUES (9006, '表单设计', 'Form Design', NULL, '/gen/design/index', 9000, 'iconfont icon-AIshiyanshi', '0', 2, '0', '0', '0', '', '2019-08-16 10:08:56', 'admin', '2023-02-23 14:06:50', '0');
INSERT INTO `sys_menu` VALUES (9007, '生成页面', 'generation', NULL, '/gen/gener/index', 9000, 'iconfont icon-tongzhi4', '0', 0, '0', '0', '0', 'admin', '2023-02-20 09:58:23', 'admin', '2023-07-05 10:27:06', '0');
INSERT INTO `sys_menu` VALUES (9050, '元数据管理', 'metadata', NULL, '/gen/metadata', 9000, 'iconfont icon--chaifenhang', '1', 9, '0', '0', '0', '', '2018-07-27 01:13:21', 'admin', '2023-07-05 10:27:13', '0');
INSERT INTO `sys_menu` VALUES (9051, '模板管理', 'template', NULL, '/gen/template/index', 9050, 'iconfont icon--chaifenhang', '1', 5, '0', '0', '0', 'admin', '2023-02-21 11:22:54', 'admin', '2023-07-05 10:27:18', '0');
INSERT INTO `sys_menu` VALUES (9052, '查询', NULL, 'codegen_template_view', NULL, 9051, NULL, '0', 0, '0', '0', '1', 'admin', '2023-02-21 12:33:03', 'admin', '2023-02-21 13:50:54', '0');
INSERT INTO `sys_menu` VALUES (9053, '增加', NULL, 'codegen_template_add', NULL, 9051, NULL, '1', 0, '0', '0', '1', 'admin', '2023-02-21 13:34:10', 'admin', '2023-02-21 13:39:49', '0');
INSERT INTO `sys_menu` VALUES (9054, '新增', NULL, 'codegen_template_add', NULL, 9051, NULL, '0', 1, '0', '0', '1', 'admin', '2023-02-21 13:51:32', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9055, '导出', NULL, 'codegen_template_export', NULL, 9051, NULL, '0', 2, '0', '0', '1', 'admin', '2023-02-21 13:51:58', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9056, '删除', NULL, 'codegen_template_del', NULL, 9051, NULL, '0', 3, '0', '0', '1', 'admin', '2023-02-21 13:52:16', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9057, '编辑', NULL, 'codegen_template_edit', NULL, 9051, NULL, '0', 4, '0', '0', '1', 'admin', '2023-02-21 13:52:58', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9059, '模板分组', 'group', NULL, '/gen/group/index', 9050, 'iconfont icon-shuxingtu', '1', 6, '0', '0', '0', 'admin', '2023-02-21 15:06:50', 'admin', '2023-07-05 10:27:22', '0');
INSERT INTO `sys_menu` VALUES (9060, '查询', NULL, 'codegen_group_view', NULL, 9059, NULL, '0', 0, '0', '0', '1', 'admin', '2023-02-21 15:08:07', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9061, '新增', NULL, 'codegen_group_add', NULL, 9059, NULL, '0', 0, '0', '0', '1', 'admin', '2023-02-21 15:08:28', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9062, '修改', NULL, 'codegen_group_edit', NULL, 9059, NULL, '0', 0, '0', '0', '1', 'admin', '2023-02-21 15:08:43', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9063, '删除', NULL, 'codegen_group_del', NULL, 9059, NULL, '0', 0, '0', '0', '1', 'admin', '2023-02-21 15:09:02', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9064, '导出', NULL, 'codegen_group_export', NULL, 9059, NULL, '0', 0, '0', '0', '1', 'admin', '2023-02-21 15:09:22', ' ', NULL, '0');
INSERT INTO `sys_menu` VALUES (9065, '字段管理', 'field', NULL, '/gen/field-type/index', 9050, 'iconfont icon-fuwenben', '1', 0, '0', '0', '0', 'admin', '2023-02-23 20:05:09', 'admin', '2023-07-05 10:27:31', '0');
COMMIT;

-- ----------------------------
-- Table structure for sys_oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `sys_oauth_client_details`;
CREATE TABLE `sys_oauth_client_details` (
  `id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT 'ID',
  `client_id` varchar(32)  NOT NULL COMMENT '客户端ID',
  `resource_ids` varchar(256)  DEFAULT NULL COMMENT '资源ID集合',
  `client_secret` varchar(256)  DEFAULT NULL COMMENT '客户端秘钥',
  `scope` varchar(256)  DEFAULT NULL COMMENT '授权范围',
  `authorized_grant_types` varchar(256)  DEFAULT NULL COMMENT '授权类型',
  `web_server_redirect_uri` varchar(256)  DEFAULT NULL COMMENT '回调地址',
  `authorities` varchar(256)  DEFAULT NULL COMMENT '权限集合',
  `access_token_validity` int DEFAULT NULL COMMENT '访问令牌有效期（秒）',
  `refresh_token_validity` int DEFAULT NULL COMMENT '刷新令牌有效期（秒）',
  `additional_information` varchar(4096)  DEFAULT NULL COMMENT '附加信息',
  `autoapprove` varchar(256)  DEFAULT NULL COMMENT '自动授权',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标记，0未删除，1已删除',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  COMMENT='终端信息表';

-- ----------------------------
-- Records of sys_oauth_client_details
-- ----------------------------
BEGIN;
INSERT INTO `sys_oauth_client_details` VALUES (1, 'app', NULL, 'app', 'server', 'password,refresh_token,authorization_code,client_credentials,mobile', 'http://localhost:4040/sso1/login,http://localhost:4041/sso1/login,http://localhost:8080/renren-admin/sys/oauth2-sso,http://localhost:8090/sys/oauth2-sso', NULL, 43200, 2592001, '{\"enc_flag\":\"1\",\"captcha_flag\":\"1\",\"online_quantity\":\"1\"}', 'true', '0', '', 'admin', NULL, '2023-02-09 13:54:54');
INSERT INTO `sys_oauth_client_details` VALUES (2, 'daemon', NULL, 'daemon', 'server', 'password,refresh_token', NULL, NULL, 43200, 2592001, '{\"enc_flag\":\"1\",\"captcha_flag\":\"1\"}', 'true', '0', ' ', ' ', NULL, NULL);
INSERT INTO `sys_oauth_client_details` VALUES (3, 'gen', NULL, 'gen', 'server', 'password,refresh_token', NULL, NULL, 43200, 2592001, '{\"enc_flag\":\"1\",\"captcha_flag\":\"1\"}', 'true', '0', ' ', ' ', NULL, NULL);
INSERT INTO `sys_oauth_client_details` VALUES (4, 'mp', NULL, 'mp', 'server', 'password,refresh_token', NULL, NULL, 43200, 2592001, '{\"enc_flag\":\"1\",\"captcha_flag\":\"1\"}', 'true', '0', ' ', ' ', NULL, NULL);
INSERT INTO `sys_oauth_client_details` VALUES (5, 'feng', NULL, 'feng', 'server', 'password,refresh_token,authorization_code,client_credentials,mobile', 'http://localhost:4040/sso1/login,http://localhost:4041/sso1/login,http://localhost:8080/renren-admin/sys/oauth2-sso,http://localhost:8090/sys/oauth2-sso', NULL, 43200, 2592001, '{\"enc_flag\":\"1\",\"captcha_flag\":\"1\",\"online_quantity\":\"1\"}', 'false', '0', '', 'admin', NULL, '2023-03-08 11:32:41');
INSERT INTO `sys_oauth_client_details` VALUES (6, 'test', NULL, 'test', 'server', 'password,refresh_token', NULL, NULL, 43200, 2592001, '{ \"enc_flag\":\"1\",\"captcha_flag\":\"0\"}', 'true', '0', ' ', ' ', NULL, NULL);
INSERT INTO `sys_oauth_client_details` VALUES (7, 'social', NULL, 'social', 'server', 'password,refresh_token,mobile', NULL, NULL, 43200, 2592001, '{ \"enc_flag\":\"0\",\"captcha_flag\":\"0\"}', 'true', '0', ' ', ' ', NULL, NULL);
INSERT INTO `sys_oauth_client_details` (`client_id`, `client_secret`, `scope`, `authorized_grant_types`, `access_token_validity`, `refresh_token_validity`, `autoapprove`) VALUES ('DEVICE_MGMT_PLATFORM', 'DMPe7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2', 'server', 'app_key,refresh_token', 43200, 2592001, 'true');
COMMIT;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '岗位ID',
  `post_code` varchar(64)  NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50)  NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '岗位排序',
  `remark` varchar(500)  DEFAULT NULL COMMENT '岗位描述',
  `del_flag` char(1)  NOT NULL DEFAULT '0' COMMENT '是否删除  -1：已删除  0：正常',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE=InnoDB  COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
BEGIN;
INSERT INTO `sys_post` VALUES (1, 'CTO', 'CTO', 0, 'CTOOO', '0', '2022-03-26 13:48:17', '', '2023-03-08 16:03:35', 'admin');
COMMIT;

-- ----------------------------
-- Table structure for sys_public_param
-- ----------------------------
DROP TABLE IF EXISTS `sys_public_param`;
CREATE TABLE `sys_public_param` (
  `public_id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '编号',
  `public_name` varchar(128)  DEFAULT NULL COMMENT '名称',
  `public_key` varchar(128)  DEFAULT NULL COMMENT '键',
  `public_value` varchar(128)  DEFAULT NULL COMMENT '值',
  `status` char(1)  DEFAULT '0' COMMENT '状态，0禁用，1启用',
  `validate_code` varchar(64)  DEFAULT NULL COMMENT '校验码',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `public_type` char(1)  DEFAULT '0' COMMENT '类型，0未知，1系统，2业务',
  `system_flag` char(1)  DEFAULT '0' COMMENT '系统标识，0非系统，1系统',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标记，0未删除，1已删除',
  PRIMARY KEY (`public_id`) USING BTREE
) ENGINE=InnoDB  COMMENT='公共参数配置表';

-- ----------------------------
-- Records of sys_public_param
-- ----------------------------
BEGIN;
INSERT INTO `sys_public_param` VALUES (1, '租户默认来源', 'TENANT_DEFAULT_ID', '1', '0', '', ' ', ' ', '2020-05-12 04:03:46', '2020-06-20 08:56:30', '2', '0', '1');
INSERT INTO `sys_public_param` VALUES (2, '租户默认部门名称', 'TENANT_DEFAULT_DEPTNAME', '租户默认部门', '0', '', ' ', ' ', '2020-05-12 03:36:32', NULL, '2', '1', '0');
INSERT INTO `sys_public_param` VALUES (3, '租户默认账户', 'TENANT_DEFAULT_USERNAME', 'admin', '0', '', ' ', ' ', '2020-05-12 04:05:04', NULL, '2', '1', '0');
INSERT INTO `sys_public_param` VALUES (4, '租户默认密码', 'TENANT_DEFAULT_PASSWORD', '123456', '0', '', ' ', ' ', '2020-05-12 04:05:24', NULL, '2', '1', '0');
INSERT INTO `sys_public_param` VALUES (5, '租户默认角色编码', 'TENANT_DEFAULT_ROLECODE', 'ROLE_ADMIN', '0', '', ' ', ' ', '2020-05-12 04:05:57', NULL, '2', '1', '0');
INSERT INTO `sys_public_param` VALUES (6, '租户默认角色名称', 'TENANT_DEFAULT_ROLENAME', '租户默认角色', '0', '', ' ', ' ', '2020-05-12 04:06:19', NULL, '2', '1', '0');
INSERT INTO `sys_public_param` VALUES (7, '表前缀', 'GEN_TABLE_PREFIX', 'tb_', '0', '', ' ', ' ', '2020-05-12 04:23:04', NULL, '9', '1', '0');
INSERT INTO `sys_public_param` VALUES (8, '接口文档不显示的字段', 'GEN_HIDDEN_COLUMNS', 'tenant_id', '0', '', ' ', ' ', '2020-05-12 04:25:19', NULL, '9', '1', '0');
INSERT INTO `sys_public_param` VALUES (9, '注册用户默认角色', 'USER_DEFAULT_ROLE', 'GENERAL_USER', '0', NULL, ' ', ' ', '2022-03-31 16:52:24', NULL, '2', '1', '0');
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '角色ID',
  `role_name` varchar(64)  DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(64)  DEFAULT NULL COMMENT '角色编码',
  `role_desc` varchar(255)  DEFAULT NULL COMMENT '角色描述',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标记，0未删除，1已删除',
  PRIMARY KEY (`role_id`) USING BTREE,
  KEY `role_idx1_role_code` (`role_code`) USING BTREE
) ENGINE=InnoDB  COMMENT='系统角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES (1, '管理员', 'ROLE_ADMIN', '管理员', '', 'admin', '2017-10-29 15:45:51', '2023-07-07 14:55:07', '0');
INSERT INTO `sys_role` VALUES (2, '普通用户', 'GENERAL_USER', '普通用户', '', 'admin', '2022-03-31 17:03:15', '2023-04-03 02:28:51', '0');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` varchar(64) NOT NULL COMMENT '角色ID',
  `menu_id` varchar(64) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB  COMMENT='角色菜单表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` VALUES (1, 1000);
INSERT INTO `sys_role_menu` VALUES (1, 1100);
INSERT INTO `sys_role_menu` VALUES (1, 1101);
INSERT INTO `sys_role_menu` VALUES (1, 1102);
INSERT INTO `sys_role_menu` VALUES (1, 1103);
INSERT INTO `sys_role_menu` VALUES (1, 1104);
INSERT INTO `sys_role_menu` VALUES (1, 1200);
INSERT INTO `sys_role_menu` VALUES (1, 1201);
INSERT INTO `sys_role_menu` VALUES (1, 1202);
INSERT INTO `sys_role_menu` VALUES (1, 1203);
INSERT INTO `sys_role_menu` VALUES (1, 1300);
INSERT INTO `sys_role_menu` VALUES (1, 1301);
INSERT INTO `sys_role_menu` VALUES (1, 1302);
INSERT INTO `sys_role_menu` VALUES (1, 1303);
INSERT INTO `sys_role_menu` VALUES (1, 1304);
INSERT INTO `sys_role_menu` VALUES (1, 1305);
INSERT INTO `sys_role_menu` VALUES (1, 1400);
INSERT INTO `sys_role_menu` VALUES (1, 1401);
INSERT INTO `sys_role_menu` VALUES (1, 1402);
INSERT INTO `sys_role_menu` VALUES (1, 1403);
INSERT INTO `sys_role_menu` VALUES (1, 1600);
INSERT INTO `sys_role_menu` VALUES (1, 1601);
INSERT INTO `sys_role_menu` VALUES (1, 1602);
INSERT INTO `sys_role_menu` VALUES (1, 1603);
INSERT INTO `sys_role_menu` VALUES (1, 1604);
INSERT INTO `sys_role_menu` VALUES (1, 1605);
INSERT INTO `sys_role_menu` VALUES (1, 2000);
INSERT INTO `sys_role_menu` VALUES (1, 2001);
INSERT INTO `sys_role_menu` VALUES (1, 2100);
INSERT INTO `sys_role_menu` VALUES (1, 2101);
INSERT INTO `sys_role_menu` VALUES (1, 2102);
INSERT INTO `sys_role_menu` VALUES (1, 2200);
INSERT INTO `sys_role_menu` VALUES (1, 2201);
INSERT INTO `sys_role_menu` VALUES (1, 2202);
INSERT INTO `sys_role_menu` VALUES (1, 2203);
INSERT INTO `sys_role_menu` VALUES (1, 2210);
INSERT INTO `sys_role_menu` VALUES (1, 2211);
INSERT INTO `sys_role_menu` VALUES (1, 2212);
INSERT INTO `sys_role_menu` VALUES (1, 2213);
INSERT INTO `sys_role_menu` VALUES (1, 2300);
INSERT INTO `sys_role_menu` VALUES (1, 2400);
INSERT INTO `sys_role_menu` VALUES (1, 2401);
INSERT INTO `sys_role_menu` VALUES (1, 2402);
INSERT INTO `sys_role_menu` VALUES (1, 2403);
INSERT INTO `sys_role_menu` VALUES (1, 2600);
INSERT INTO `sys_role_menu` VALUES (1, 2601);
INSERT INTO `sys_role_menu` VALUES (1, 2800);
INSERT INTO `sys_role_menu` VALUES (1, 2810);
INSERT INTO `sys_role_menu` VALUES (1, 2820);
INSERT INTO `sys_role_menu` VALUES (1, 2830);
INSERT INTO `sys_role_menu` VALUES (1, 2840);
INSERT INTO `sys_role_menu` VALUES (1, 2850);
INSERT INTO `sys_role_menu` VALUES (1, 2860);
INSERT INTO `sys_role_menu` VALUES (1, 2870);
INSERT INTO `sys_role_menu` VALUES (1, 2871);
INSERT INTO `sys_role_menu` VALUES (1, 2906);
INSERT INTO `sys_role_menu` VALUES (1, 2907);
INSERT INTO `sys_role_menu` VALUES (1, 4000);
INSERT INTO `sys_role_menu` VALUES (1, 4001);
INSERT INTO `sys_role_menu` VALUES (1, 4002);
INSERT INTO `sys_role_menu` VALUES (1, 9000);
INSERT INTO `sys_role_menu` VALUES (1, 9005);
INSERT INTO `sys_role_menu` VALUES (1, 9006);
INSERT INTO `sys_role_menu` VALUES (1, 9007);
INSERT INTO `sys_role_menu` VALUES (1, 9050);
INSERT INTO `sys_role_menu` VALUES (1, 9051);
INSERT INTO `sys_role_menu` VALUES (1, 9052);
INSERT INTO `sys_role_menu` VALUES (1, 9053);
INSERT INTO `sys_role_menu` VALUES (1, 9054);
INSERT INTO `sys_role_menu` VALUES (1, 9055);
INSERT INTO `sys_role_menu` VALUES (1, 9056);
INSERT INTO `sys_role_menu` VALUES (1, 9057);
INSERT INTO `sys_role_menu` VALUES (1, 9059);
INSERT INTO `sys_role_menu` VALUES (1, 9060);
INSERT INTO `sys_role_menu` VALUES (1, 9061);
INSERT INTO `sys_role_menu` VALUES (1, 9062);
INSERT INTO `sys_role_menu` VALUES (1, 9063);
INSERT INTO `sys_role_menu` VALUES (1, 9064);
INSERT INTO `sys_role_menu` VALUES (1, 9065);
INSERT INTO `sys_role_menu` VALUES (2, 4000);
INSERT INTO `sys_role_menu` VALUES (2, 4001);
INSERT INTO `sys_role_menu` VALUES (2, 4002);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '用户ID',
  `username` varchar(64)  DEFAULT NULL COMMENT '用户名',
  `password` varchar(255)  DEFAULT NULL COMMENT '密码',
  `salt` varchar(255)  DEFAULT NULL COMMENT '盐值',
  `phone` varchar(20)  DEFAULT NULL COMMENT '电话号码',
  `avatar` varchar(255)  DEFAULT NULL COMMENT '头像',
  `nickname` varchar(64)  DEFAULT NULL COMMENT '昵称',
  `name` varchar(64)  DEFAULT NULL COMMENT '姓名',
  `email` varchar(128)  DEFAULT NULL COMMENT '邮箱地址',
  `dept_id` varchar(64) DEFAULT NULL COMMENT '所属部门ID',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `lock_flag` char(1)  DEFAULT '0' COMMENT '锁定标记，0未锁定，9已锁定',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标记，0未删除，1已删除',
  `wx_openid` varchar(32)  DEFAULT NULL COMMENT '微信登录openId',
  `mini_openid` varchar(32)  DEFAULT NULL COMMENT '小程序openId',
  `qq_openid` varchar(32)  DEFAULT NULL COMMENT 'QQ openId',
  `gitee_login` varchar(100)  DEFAULT NULL COMMENT '码云标识',
  `osc_id` varchar(100)  DEFAULT NULL COMMENT '开源中国标识',
  PRIMARY KEY (`user_id`) USING BTREE,
  KEY `user_wx_openid` (`wx_openid`) USING BTREE,
  KEY `user_qq_openid` (`qq_openid`) USING BTREE,
  KEY `user_idx1_username` (`username`) USING BTREE
) ENGINE=InnoDB  COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$c/Ae0pRjJtMZg3BnvVpO.eIK6WYWVbKTzqgdy3afR7w.vd.xi3Mgy', '', '17034642999', '/admin/sys-file/s3demo/7ff4ca6b7bf446f3a5a13ac016dc21af.png', '管理员', '管理员', 'test@qq.com', 4, ' ', 'admin', '2018-04-20 07:15:18', '2023-07-07 14:55:40', '0', '0', NULL, 'oBxPy5E-v82xWGsfzZVzkD3wEX64', NULL, 'log4j', NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `post_id` varchar(64) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`) USING BTREE
) ENGINE=InnoDB  ROW_FORMAT=DYNAMIC COMMENT='用户与岗位关联表';

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_post` VALUES (1, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `role_id` varchar(64) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB  COMMENT='用户角色表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (1676492190299299842, 2);
COMMIT;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '任务id',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `job_order` char(1) DEFAULT '1' COMMENT '组内执行顺利，值越大执行优先级越高，最大值9，最小值1',
  `job_type` char(1) NOT NULL DEFAULT '1' COMMENT '1、java类;2、spring bean名称;3、rest调用;4、jar调用;9其他',
  `execute_path` varchar(500) DEFAULT NULL COMMENT 'job_type=3时，rest调用地址，仅支持rest get协议,需要增加String返回值，0成功，1失败;job_type=4时，jar路径;其它值为空',
  `class_name` varchar(500) DEFAULT NULL COMMENT 'job_type=1时，类完整路径;job_type=2时，spring bean名称;其它值为空',
  `method_name` varchar(500) DEFAULT NULL COMMENT '任务方法',
  `method_params_value` varchar(2000) DEFAULT NULL COMMENT '参数值',
  `cron_expression` varchar(255) DEFAULT NULL COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '错失执行策略（1错失周期立即执行 2错失周期执行一次 3下周期执行）',
  `job_tenant_type` char(1) DEFAULT '1' COMMENT '1、多租户任务;2、非多租户任务',
  `job_status` char(1) DEFAULT '0' COMMENT '状态（1、未发布;2、运行中;3、暂停;4、删除;）',
  `job_execute_status` char(1) DEFAULT '0' COMMENT '状态（0正常 1异常）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '初次执行时间',
  `previous_time` timestamp NULL DEFAULT NULL COMMENT '上次执行时间',
  `next_time` timestamp NULL DEFAULT NULL COMMENT '下次执行时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`) USING BTREE,
  UNIQUE KEY `job_name_group_idx` (`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB  COMMENT='定时任务调度表';

-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `job_log_id` varchar(64) NOT NULL DEFAULT (REPLACE(UUID(), '-', '')) COMMENT '任务日志ID',
  `job_id` varchar(64) NOT NULL COMMENT '任务id',
  `job_name` varchar(64)  DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(64)  DEFAULT NULL COMMENT '任务组名',
  `job_order` char(1)  DEFAULT NULL COMMENT '组内执行顺利，值越大执行优先级越高，最大值9，最小值1',
  `job_type` char(1)  NOT NULL DEFAULT '1' COMMENT '1、java类;2、spring bean名称;3、rest调用;4、jar调用;9其他',
  `execute_path` varchar(500)  DEFAULT NULL COMMENT 'job_type=3时，rest调用地址，仅支持post协议;job_type=4时，jar路径;其它值为空',
  `class_name` varchar(500)  DEFAULT NULL COMMENT 'job_type=1时，类完整路径;job_type=2时，spring bean名称;其它值为空',
  `method_name` varchar(500)  DEFAULT NULL COMMENT '任务方法',
  `method_params_value` varchar(2000)  DEFAULT NULL COMMENT '参数值',
  `cron_expression` varchar(255)  DEFAULT NULL COMMENT 'cron执行表达式',
  `job_message` varchar(500)  DEFAULT NULL COMMENT '日志信息',
  `job_log_status` char(1)  DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `execute_time` varchar(30)  DEFAULT NULL COMMENT '执行时间',
  `exception_info` varchar(2000)  DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE=InnoDB  COMMENT='定时任务执行日志表';


-- 3. Quartz表
--
-- Quartz seems to work best with the driver mm.mysql-2.0.7-bin.jar
--
-- PLEASE consider using mysql with innodb tables to avoid locking issues
--
-- In your Quartz properties file, you'll need to set
-- org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
--

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;


CREATE TABLE QRTZ_JOB_DETAILS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_NONCONCURRENT VARCHAR(1) NOT NULL,
    IS_UPDATE_DATA VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(10) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(200) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    CALENDAR_NAME  VARCHAR(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR(200) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    SCHED_TIME BIGINT(13) NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_NONCONCURRENT VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
  (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

-- ----------------------------
-- Table structure for gen_datasource_conf
-- ----------------------------
DROP TABLE IF EXISTS `gen_datasource_conf`;
CREATE TABLE `gen_datasource_conf` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(64)  DEFAULT NULL COMMENT '别名',
  `url` varchar(255)  DEFAULT NULL COMMENT 'jdbcurl',
  `username` varchar(64)  DEFAULT NULL COMMENT '用户名',
  `password` varchar(64)  DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标记',
  `ds_type` varchar(64)  DEFAULT NULL COMMENT '数据库类型',
  `conf_type` char(1)  DEFAULT NULL COMMENT '配置类型',
  `ds_name` varchar(64)  DEFAULT NULL COMMENT '数据库名称',
  `instance` varchar(64)  DEFAULT NULL COMMENT '实例',
  `port` int DEFAULT NULL COMMENT '端口',
  `host` varchar(128)  DEFAULT NULL COMMENT '主机',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  COMMENT='数据源表';

-- ----------------------------
-- Records of gen_datasource_conf
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for gen_field_type
-- ----------------------------
DROP TABLE IF EXISTS `gen_field_type`;
CREATE TABLE `gen_field_type` (
  `id` bigint NOT NULL COMMENT '主键',
  `column_type` varchar(200)  DEFAULT NULL COMMENT '字段类型',
  `attr_type` varchar(200)  DEFAULT NULL COMMENT '属性类型',
  `package_name` varchar(200)  DEFAULT NULL COMMENT '属性包名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64)  DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(64)  DEFAULT NULL COMMENT '修改人',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `column_type` (`column_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1634915190321451010  COMMENT='字段类型管理';

-- ----------------------------
-- Records of gen_field_type
-- ----------------------------
BEGIN;
INSERT INTO `gen_field_type` VALUES (1, 'datetime', 'LocalDateTime', 'java.time.LocalDateTime', '2023-02-06 08:45:10', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (2, 'date', 'LocalDate', 'java.time.LocalDate', '2023-02-06 08:45:10', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (3, 'tinyint', 'Integer', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (4, 'smallint', 'Integer', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (5, 'mediumint', 'Integer', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (6, 'int', 'Integer', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (7, 'integer', 'Integer', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (8, 'bigint', 'Long', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (9, 'float', 'Float', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (10, 'double', 'Double', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (11, 'decimal', 'BigDecimal', 'java.math.BigDecimal', '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (12, 'bit', 'Boolean', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (13, 'char', 'String', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (14, 'varchar', 'String', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (15, 'tinytext', 'String', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (16, 'text', 'String', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (17, 'mediumtext', 'String', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (18, 'longtext', 'String', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (19, 'timestamp', 'LocalDateTime', 'java.time.LocalDateTime', '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (20, 'NUMBER', 'Integer', NULL, '2023-02-06 08:45:11', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (21, 'BINARY_INTEGER', 'Integer', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (22, 'BINARY_FLOAT', 'Float', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (23, 'BINARY_DOUBLE', 'Double', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (24, 'VARCHAR2', 'String', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (25, 'NVARCHAR', 'String', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (26, 'NVARCHAR2', 'String', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (27, 'CLOB', 'String', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (28, 'int8', 'Long', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (29, 'int4', 'Integer', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (30, 'int2', 'Integer', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (31, 'numeric', 'BigDecimal', 'java.math.BigDecimal', '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
INSERT INTO `gen_field_type` VALUES (32, 'json', 'String', NULL, '2023-02-06 08:45:12', NULL, NULL, NULL, '0');
COMMIT;

-- ----------------------------
-- Table structure for gen_group
-- ----------------------------
DROP TABLE IF EXISTS `gen_group`;
CREATE TABLE `gen_group` (
  `id` bigint NOT NULL,
  `group_name` varchar(255)  DEFAULT NULL COMMENT '分组名称',
  `group_desc` varchar(255)  DEFAULT NULL COMMENT '分组描述',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  `create_time` datetime DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改人',
  `del_flag` char(1)  DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  COMMENT='模板分组';


-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `id` bigint NOT NULL,
  `table_name` varchar(200)  DEFAULT NULL COMMENT '表名',
  `class_name` varchar(200)  DEFAULT NULL COMMENT '类名',
  `db_type` varchar(200)  DEFAULT NULL COMMENT '数据库类型',
  `table_comment` varchar(200)  DEFAULT NULL COMMENT '说明',
  `author` varchar(200)  DEFAULT NULL COMMENT '作者',
  `email` varchar(200)  DEFAULT NULL COMMENT '邮箱',
  `package_name` varchar(200)  DEFAULT NULL COMMENT '项目包名',
  `version` varchar(200)  DEFAULT NULL COMMENT '项目版本号',
  `i18n` char(1)  DEFAULT '0' COMMENT '是否生成带有i18n 0 不带有 1带有',
  `style`  bigint DEFAULT NULL COMMENT '代码风格',
  `child_table_name` varchar(200)  DEFAULT NULL COMMENT '子表名称',
  `main_field` varchar(200)  DEFAULT NULL COMMENT '主表关联键',
  `child_field` varchar(200)  DEFAULT NULL COMMENT '子表关联键',
  `generator_type` char(1)  DEFAULT '0' COMMENT '生成方式  0：zip压缩包   1：自定义目录',
  `backend_path` varchar(500)  DEFAULT NULL COMMENT '后端生成路径',
  `frontend_path` varchar(500)  DEFAULT NULL COMMENT '前端生成路径',
  `module_name` varchar(200)  DEFAULT NULL COMMENT '模块名',
  `function_name` varchar(200)  DEFAULT NULL COMMENT '功能名',
  `form_layout` tinyint DEFAULT NULL COMMENT '表单布局  1：一列   2：两列',
  `ds_name` varchar(200)  DEFAULT NULL COMMENT '数据源ID',
  `baseclass_id` bigint DEFAULT NULL COMMENT '基类ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `table_name` (`table_name`,`ds_name`) USING BTREE
) ENGINE=InnoDB  COMMENT='代码生成表';

-- ----------------------------
-- Records of gen_table
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `id` bigint NOT NULL,
  `ds_name` varchar(200)  DEFAULT NULL COMMENT '数据源名称',
  `table_name` varchar(200)  DEFAULT NULL COMMENT '表名称',
  `field_name` varchar(200)  DEFAULT NULL COMMENT '字段名称',
  `field_type` varchar(200)  DEFAULT NULL COMMENT '字段类型',
  `field_comment` varchar(200)  DEFAULT NULL COMMENT '字段说明',
  `attr_name` varchar(200)  DEFAULT NULL COMMENT '属性名',
  `attr_type` varchar(200)  DEFAULT NULL COMMENT '属性类型',
  `package_name` varchar(200)  DEFAULT NULL COMMENT '属性包名',
  `sort` int DEFAULT NULL COMMENT '排序',
  `auto_fill` varchar(20)  DEFAULT NULL COMMENT '自动填充  DEFAULT、INSERT、UPDATE、INSERT_UPDATE',
  `primary_pk` char(1)  DEFAULT '0' COMMENT '主键 0：否  1：是',
  `base_field` char(1)  DEFAULT '0' COMMENT '基类字段 0：否  1：是',
  `form_item` char(1)  DEFAULT '0' COMMENT '表单项 0：否  1：是',
  `form_required` char(1)  DEFAULT '0' COMMENT '表单必填 0：否  1：是',
  `form_type` varchar(200)  DEFAULT NULL COMMENT '表单类型',
  `form_validator` varchar(200)  DEFAULT NULL COMMENT '表单效验',
  `grid_item` char(1)  DEFAULT '0' COMMENT '列表项 0：否  1：是',
  `grid_sort` char(1)  DEFAULT '0' COMMENT '列表排序 0：否  1：是',
  `query_item` char(1)  DEFAULT '0' COMMENT '查询项 0：否  1：是',
  `query_type` varchar(200)  DEFAULT NULL COMMENT '查询方式',
  `query_form_type` varchar(200)  DEFAULT NULL COMMENT '查询表单类型',
  `field_dict` varchar(200)  DEFAULT NULL COMMENT '字典类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  COMMENT='代码生成表字段';

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for gen_template
-- ----------------------------
DROP TABLE IF EXISTS `gen_template`;
CREATE TABLE `gen_template` (
  `id` bigint NOT NULL COMMENT '主键',
  `template_name` varchar(255)  NOT NULL COMMENT '模板名称',
  `generator_path` varchar(255)  NOT NULL COMMENT '模板路径',
  `template_desc` varchar(255)  NOT NULL COMMENT '模板描述',
  `template_code` text  NOT NULL COMMENT '模板代码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新',
  `del_flag` char(1)  NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  COMMENT='模板';


-- ----------------------------
-- Table structure for gen_template_group
-- ----------------------------
DROP TABLE IF EXISTS `gen_template_group`;
CREATE TABLE `gen_template_group` (
  `group_id` bigint NOT NULL COMMENT '分组id',
  `template_id` bigint NOT NULL COMMENT '模板id',
  PRIMARY KEY (`group_id`,`template_id`)
) ENGINE=InnoDB  COMMENT='模板分组关联表';

SET FOREIGN_KEY_CHECKS = 1;
