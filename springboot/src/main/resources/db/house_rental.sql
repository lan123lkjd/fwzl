/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : house_rental

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 07/04/2026 11:12:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for area
-- ----------------------------
DROP TABLE IF EXISTS `area`;
CREATE TABLE `area`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地区ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地区名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of area
-- ----------------------------
INSERT INTO `area` VALUES (4, '青秀区', '2026-01-12 21:53:03');
INSERT INTO `area` VALUES (5, '兴宁区', '2026-01-12 21:53:03');
INSERT INTO `area` VALUES (6, '江南区', '2026-01-12 21:53:03');
INSERT INTO `area` VALUES (7, '西乡塘区', '2026-01-12 21:53:03');
INSERT INTO `area` VALUES (8, '良庆区', '2026-01-12 21:53:03');
INSERT INTO `area` VALUES (9, '邕宁区', '2026-01-12 21:53:03');
INSERT INTO `area` VALUES (10, '武鸣区', '2026-01-12 21:53:03');

-- ----------------------------
-- Table structure for evaluations
-- ----------------------------
DROP TABLE IF EXISTS `evaluations`;
CREATE TABLE `evaluations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论内容',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父评论ID(0表示顶级评论)',
  `upvote_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_house_id`(`house_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluations
-- ----------------------------
INSERT INTO `evaluations` VALUES (1, 1, 4, '房子很不错，装修风格我很喜欢，房东人也很好说话。', 0, 16, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `evaluations` VALUES (2, 1, 5, '位置很方便，出门就是地铁站，周边吃饭购物都很方便。', 0, 9, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `evaluations` VALUES (3, 2, 4, '一居室很温馨，适合我们小两口住，就是面积稍微小了点。', 0, 5, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `evaluations` VALUES (4, 3, 5, '豪华三居室名不虚传，家具家电都是品牌的，值这个价！', 0, 22, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `evaluations` VALUES (5, 4, 4, '这的房子就是贵，但是真的很方便，适合金融行业的朋友。', 0, 18, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `evaluations` VALUES (6, 4, 1, 'dada', 0, 2, '2026-02-02 23:49:36', '2026-02-08 22:36:33', 1);

-- ----------------------------
-- Table structure for evaluations_upvote
-- ----------------------------
DROP TABLE IF EXISTS `evaluations_upvote`;
CREATE TABLE `evaluations_upvote`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `evaluation_id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_evaluation_user`(`evaluation_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluations_upvote
-- ----------------------------
INSERT INTO `evaluations_upvote` VALUES (1, 6, 1, '2026-02-02 23:49:38');
INSERT INTO `evaluations_upvote` VALUES (2, 6, 4, '2026-02-02 23:51:17');
INSERT INTO `evaluations_upvote` VALUES (5, 1, 1, '2026-04-03 11:39:30');
INSERT INTO `evaluations_upvote` VALUES (6, 2, 1, '2026-04-03 11:39:33');

-- ----------------------------
-- Table structure for flow_index
-- ----------------------------
DROP TABLE IF EXISTS `flow_index`;
CREATE TABLE `flow_index`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `date` date NOT NULL COMMENT '日期',
  `pv` int NULL DEFAULT 0 COMMENT '页面浏览量',
  `uv` int NULL DEFAULT 0 COMMENT '独立访客数',
  `new_users` int NULL DEFAULT 0 COMMENT '新增用户数',
  `house_views` int NULL DEFAULT 0 COMMENT '房源浏览量',
  `order_count` int NULL DEFAULT 0 COMMENT '预约数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `date`(`date` ASC) USING BTREE,
  INDEX `idx_date`(`date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '流量指标信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flow_index
-- ----------------------------
INSERT INTO `flow_index` VALUES (1, '2026-01-06', 1200, 350, 25, 458, 12, '2026-01-12 21:53:03', '2026-01-12 21:53:03');
INSERT INTO `flow_index` VALUES (2, '2026-01-07', 1450, 420, 32, 523, 18, '2026-01-12 21:53:03', '2026-01-12 21:53:03');
INSERT INTO `flow_index` VALUES (3, '2026-01-08', 1680, 480, 28, 612, 15, '2026-01-12 21:53:03', '2026-01-12 21:53:03');
INSERT INTO `flow_index` VALUES (4, '2026-01-09', 1320, 390, 20, 445, 10, '2026-01-12 21:53:03', '2026-01-12 21:53:03');
INSERT INTO `flow_index` VALUES (5, '2026-01-10', 1560, 445, 35, 578, 22, '2026-01-12 21:53:03', '2026-01-12 21:53:03');
INSERT INTO `flow_index` VALUES (6, '2026-01-11', 1890, 520, 42, 689, 28, '2026-01-12 21:53:03', '2026-01-12 21:53:03');
INSERT INTO `flow_index` VALUES (7, '2026-01-12', 856, 245, 18, 314, 9, '2026-01-12 21:53:03', '2026-01-12 21:53:03');
INSERT INTO `flow_index` VALUES (8, '2026-02-02', 0, 0, 0, 6, 0, '2026-02-02 23:49:12', '2026-02-02 23:49:12');
INSERT INTO `flow_index` VALUES (9, '2026-02-04', 0, 0, 0, 0, 0, '2026-02-04 23:27:41', '2026-02-04 23:27:41');
INSERT INTO `flow_index` VALUES (10, '2026-02-08', 0, 0, 0, 5, 0, '2026-02-08 22:36:15', '2026-02-08 22:36:15');
INSERT INTO `flow_index` VALUES (11, '2026-02-09', 0, 0, 0, 7, 0, '2026-02-09 00:07:32', '2026-02-09 00:07:32');
INSERT INTO `flow_index` VALUES (12, '2026-02-20', 0, 0, 0, 1, 0, '2026-02-20 19:27:28', '2026-02-20 19:27:28');
INSERT INTO `flow_index` VALUES (13, '2026-03-15', 0, 0, 0, 2, 0, '2026-03-15 15:36:19', '2026-03-15 15:36:19');
INSERT INTO `flow_index` VALUES (14, '2026-03-16', 0, 0, 0, 5, 0, '2026-03-16 08:21:44', '2026-03-16 08:21:44');
INSERT INTO `flow_index` VALUES (15, '2026-03-18', 0, 0, 0, 1, 0, '2026-03-18 00:25:38', '2026-03-18 00:25:38');
INSERT INTO `flow_index` VALUES (16, '2026-03-23', 0, 0, 0, 8, 0, '2026-03-23 19:17:28', '2026-03-23 19:17:28');
INSERT INTO `flow_index` VALUES (17, '2026-04-02', 0, 0, 0, 1, 0, '2026-04-02 15:53:45', '2026-04-02 15:53:45');
INSERT INTO `flow_index` VALUES (18, '2026-04-03', 0, 0, 0, 23, 0, '2026-04-03 10:24:19', '2026-04-03 10:24:19');
INSERT INTO `flow_index` VALUES (19, '2026-04-06', 0, 0, 0, 12, 0, '2026-04-06 18:30:25', '2026-04-06 18:30:25');

-- ----------------------------
-- Table structure for house
-- ----------------------------
DROP TABLE IF EXISTS `house`;
CREATE TABLE `house`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房源ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房源标题',
  `landlord_id` bigint NOT NULL COMMENT '房东ID',
  `area_id` bigint NULL DEFAULT NULL COMMENT '所属地区ID',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `price` decimal(10, 2) NOT NULL COMMENT '月租金(元)',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '面积(平方米)',
  `rooms` tinyint NULL DEFAULT NULL COMMENT '房间数',
  `halls` tinyint NULL DEFAULT NULL COMMENT '客厅数',
  `bathrooms` tinyint NULL DEFAULT NULL COMMENT '卫生间数',
  `floor` int NULL DEFAULT NULL COMMENT '楼层',
  `total_floor` int NULL DEFAULT NULL COMMENT '总楼层',
  `orientation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '朝向',
  `decoration` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '装修情况',
  `house_type` tinyint NULL DEFAULT 1 COMMENT '房屋类型: 1-整租, 2-合租',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '房源描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-待审核, 1-已上架, 2-已下架, 3-已出租',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_landlord_id`(`landlord_id` ASC) USING BTREE,
  INDEX `idx_area_id`(`area_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_price`(`price` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房源信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of house
-- ----------------------------
INSERT INTO `house` VALUES (1, '青秀区精装两居室 近地铁 拎包入住', 1, 4, '东盟商务区万象城旁3号楼1单元801室', 2500.00, 85.00, 2, 1, 1, 8, 18, '朝南', '精装修', 1, '房屋位于东盟商务区核心地段，交通便利，周边配套设施齐全。精装修两居室，家电家具齐全，可拎包入住。小区环境优美，物业管理完善。', 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800', 1, 274, 35, '2026-01-12 21:53:03', '2026-02-04 23:27:58', 0);
INSERT INTO `house` VALUES (2, '兴宁区温馨一居室 适合情侣/单身', 1, 5, '朝阳广场附近5号楼2单元402室', 1800.00, 55.00, 1, 1, 1, 4, 12, '朝南', '精装修', 1, '位于兴宁区核心地段，靠近朝阳广场，适合在附近工作的白领。一居室布局合理，采光良好。', 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800', 1, 194, 28, '2026-01-12 21:53:03', '2026-03-23 19:24:00', 0);
INSERT INTO `house` VALUES (3, '青秀区豪华三居室 全新装修 家电齐全', 2, 4, '凤岭北路8号楼1单元1502室', 3500.00, 120.00, 3, 2, 2, 15, 20, '朝南', '豪华装修', 1, '凤岭核心区域，三居室大户型，全新豪华装修。主卧带独立卫生间，客厅超大。适合家庭居住。', 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800', 1, 336, 48, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house` VALUES (4, '西乡塘区精品公寓 近地铁站', 2, 7, '广西大学附近A座2301室', 2000.00, 90.00, 2, 1, 1, 23, 35, '朝东', '豪华装修', 1, '地处西乡塘区核心地段，靠近广西大学，交通便利。高端精装公寓，周边配套完善。', 'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800', 3, 432, 62, '2026-01-12 21:53:03', '2026-03-16 09:54:23', 0);
INSERT INTO `house` VALUES (5, '江南区温馨两居室 采光极佳', 1, 6, '星光大道旁6号楼902室', 1500.00, 78.00, 2, 1, 1, 9, 16, '朝南', '精装修', 1, '位于江南区核心地段，环境优美空气清新。两居室格局方正，客厅开阔。周边商业配套完善。', 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800', 1, 181, 25, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house` VALUES (6, '良庆区舒适单间 交通便利', 1, 8, '五象大道50号', 1200.00, 25.00, 1, 0, 1, 5, 8, '朝北', '简单装修', 2, '五象新区核心地段，交通极其便利。单间配独立卫生间，适合单身上班族。', 'https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=800', 1, 108, 13, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);

-- ----------------------------
-- Table structure for house_news
-- ----------------------------
DROP TABLE IF EXISTS `house_news`;
CREATE TABLE `house_news`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资讯ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '摘要',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `author_id` bigint NULL DEFAULT NULL COMMENT '作者ID',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_author_id`(`author_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房屋资讯表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of house_news
-- ----------------------------
INSERT INTO `house_news` VALUES (1, '2025年租房市场趋势分析', '根据最新数据显示，2024年一线城市租房市场呈现稳中有升的态势。北京、上海、深圳等城市的核心区域租金保持稳定，而新兴区域则有小幅上涨。\n\n专家建议租房者应该关注以下几点：\n1. 选择交通便利的区域\n2. 关注周边配套设施\n3. 提前做好预算规划\n4. 注意合同条款\n\n总体来看，市场将继续保持健康发展态势。', '2025年一线城市租房市场呈现稳中有升态势，核心区域租金稳定，新兴区域小幅上涨。', '/uploads/6fdfe091-44ef-4def-b3a4-849cec8a66ff.png', 1, '市场分析', NULL, 1259, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house_news` VALUES (2, '租房注意事项完全指南', '租房时需要注意以下几个关键点：\n\n一、看房阶段\n- 检查房屋结构和设施\n- 确认水电燃气正常\n- 了解周边环境和配套\n\n二、签约阶段\n- 核实房东身份\n- 明确租金支付方式\n- 确认押金退还条件\n\n三、入住阶段\n- 拍照记录房屋现状\n- 保存好各类凭证\n- 及时报修问题设施', '从看房、签约到入住，全面介绍租房过程中需要注意的各类事项。', '/uploads/94891137-77b3-4816-b93d-a07c2518672d.png', 1, '租房指南', NULL, 2350, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house_news` VALUES (3, '如何选择适合自己的房源', '选择房源时，除了价格因素外，还需要综合考虑以下方面：\n\n1. 通勤距离：尽量选择距离工作地点30分钟以内的房源\n2. 周边配套：超市、医院、银行等基本设施\n3. 小区环境：物业管理、安全系数\n4. 房屋条件：采光、通风、装修情况\n5. 未来规划：是否有地铁或商业规划', '综合考虑通勤距离、周边配套、小区环境、房屋条件等因素选择房源。', '/uploads/372187f2-523b-4f7b-8500-738559a0fc56.png', 1, '租房指南', NULL, 1877, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house_news` VALUES (4, '北京各区租金参考价格', '北京各主要区域租金参考：\n\n朝阳区：\n- 一居室：4500-6500元/月\n- 两居室：6000-9000元/月\n- 三居室：8000-15000元/月\n\n海淀区：\n- 一居室：4000-6000元/月\n- 两居室：5500-8500元/月\n\n西城区：\n- 一居室：5000-7000元/月\n- 两居室：7000-10000元/月', '北京各主要区域最新租金参考价格，帮助租房者做好预算。', '/uploads/fa797a93-1790-4e5a-860b-ff7851daf9a4.png', 1, '市场分析', NULL, 3428, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house_news` VALUES (5, '租房合同签订注意事项', '签订租房合同时，请务必注意以下几点：\n\n1. 确认房东身份，查看房产证原件\n2. 明确租赁期限、租金及支付方式\n3. 约定押金退还条件和时间\n4. 列明房屋内家具家电清单\n5. 明确维修责任和费用承担\n6. 约定违约责任和解约条件\n\n建议使用正规租房合同模板，避免霸王条款。', '租房合同签订时的关键注意事项，保障租客合法权益。', '/uploads/e1ba2c1c-b482-4434-83a7-202206901e78.png', 1, '法律知识', NULL, 1568, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);

-- ----------------------------
-- Table structure for house_order_info
-- ----------------------------
DROP TABLE IF EXISTS `house_order_info`;
CREATE TABLE `house_order_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `landlord_id` bigint NOT NULL COMMENT '房东ID',
  `order_time` datetime NOT NULL COMMENT '预约时间',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-待确认, 1-已确认, 2-已完成, 3-已取消, 4-已拒绝',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_house_id`(`house_id` ASC) USING BTREE,
  INDEX `idx_landlord_id`(`landlord_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约看房信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of house_order_info
-- ----------------------------
INSERT INTO `house_order_info` VALUES (1, 'ORD20260112001', 4, 1, 1, '2026-01-15 14:00:00', '张三', '13800000003', NULL, 3, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house_order_info` VALUES (2, 'ORD20260112002', 5, 3, 2, '2026-01-16 10:00:00', '李四', '13800000004', NULL, 4, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house_order_info` VALUES (3, 'ORD20260112003', 4, 4, 2, '2026-01-14 16:00:00', '张三', '13800000003', NULL, 2, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `house_order_info` VALUES (4, 'ORD2010713460249329664', 4, 4, 2, '2026-01-12 16:00:00', '梁兴均', '13471521113', '', 4, '2026-01-12 22:00:21', '2026-01-12 22:00:21', 0);

-- ----------------------------
-- Table structure for house_order_status
-- ----------------------------
DROP TABLE IF EXISTS `house_order_status`;
CREATE TABLE `house_order_status`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint NOT NULL COMMENT '预约ID',
  `status` tinyint NOT NULL COMMENT '状态',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operator_type` tinyint NULL DEFAULT NULL COMMENT '操作人类型: 1-用户, 2-房东, 3-管理员',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约看房状态流转表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of house_order_status
-- ----------------------------
INSERT INTO `house_order_status` VALUES (1, 4, 0, 4, 1, '用户创建预约', '2026-01-12 22:00:21');
INSERT INTO `house_order_status` VALUES (2, 4, 4, 2, 2, '时间不合适', '2026-03-16 09:41:43');
INSERT INTO `house_order_status` VALUES (3, 2, 4, 2, 2, '时间不合适', '2026-03-16 09:41:44');
INSERT INTO `house_order_status` VALUES (4, 1, 3, 4, 1, '用户取消预约', '2026-03-16 09:45:25');

-- ----------------------------
-- Table structure for house_rental
-- ----------------------------
DROP TABLE IF EXISTS `house_rental`;
CREATE TABLE `house_rental`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rental_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租赁单号',
  `user_id` bigint NOT NULL COMMENT '租客ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `landlord_id` bigint NOT NULL COMMENT '房东ID',
  `start_date` date NOT NULL COMMENT '租赁开始日期',
  `end_date` date NOT NULL COMMENT '租赁结束日期',
  `monthly_rent` decimal(10, 2) NOT NULL COMMENT '月租金',
  `deposit` decimal(10, 2) NULL DEFAULT NULL COMMENT '押金',
  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '总金额',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0-待确认, 1-待支付, 2-租赁中, 3-已完成, 4-已取消, 5-已拒绝',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `rental_no`(`rental_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_house_id`(`house_id` ASC) USING BTREE,
  INDEX `idx_landlord_id`(`landlord_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租赁订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of house_rental
-- ----------------------------
INSERT INTO `house_rental` VALUES (1, 'R2020532211056533504', 1, 3, 2, '2026-02-08', '2026-02-26', 9800.00, 9800.00, 19600.00, 'liang', '13471521113', 'hhhh', 3, NULL, '2026-02-09 00:16:33', '2026-03-16 09:41:14', 0);
INSERT INTO `house_rental` VALUES (2, 'R2020532921085091840', 4, 3, 2, '2026-02-08', '2026-02-27', 9800.00, 9800.00, 19600.00, 'liang', '13471521113', '', 4, NULL, '2026-02-09 00:19:23', '2026-02-09 00:19:30', 0);
INSERT INTO `house_rental` VALUES (3, 'R2033357648292319232', 4, 6, 1, '2026-03-15', '2026-03-18', 3200.00, 3200.00, 6400.00, '梁兴均', '13471521113', '', 4, NULL, '2026-03-16 09:40:16', '2026-03-16 09:45:29', 0);
INSERT INTO `house_rental` VALUES (4, 'R2033359097617289216', 4, 4, 2, '2026-03-15', '2026-03-17', 12000.00, 12000.00, 24000.00, '张三', '13471521113', '', 2, '2026-03-16 09:46:23', '2026-03-16 09:46:02', '2026-03-16 09:46:23', 0);

-- ----------------------------
-- Table structure for house_rental_status
-- ----------------------------
DROP TABLE IF EXISTS `house_rental_status`;
CREATE TABLE `house_rental_status`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `rental_id` bigint NOT NULL COMMENT '租赁ID',
  `status` tinyint NOT NULL COMMENT '状态',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operator_type` tinyint NULL DEFAULT NULL COMMENT '操作人类型: 1-用户, 2-房东, 3-管理员',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rental_id`(`rental_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '租赁状态流转表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for landlord
-- ----------------------------
DROP TABLE IF EXISTS `landlord`;
CREATE TABLE `landlord`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '房东ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `id_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `contact` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `id_card_front` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证正面照片',
  `id_card_back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证背面照片',
  `verify_status` tinyint NULL DEFAULT 0 COMMENT '认证状态: 0-待审核, 1-已认证, 2-认证失败',
  `verify_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房东信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of landlord
-- ----------------------------
INSERT INTO `landlord` VALUES (1, 2, '王大明', '110101199001011234', '13800000001', NULL, NULL, 1, NULL, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `landlord` VALUES (2, 3, '李小红', '310101199002022345', '13800000002', NULL, NULL, 1, NULL, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `landlord` VALUES (3, 6, '梁兴均', '480924200211244911', '13471521113', '/uploads/idcard/2026/04/06/5cdc6302-2860-477b-a1aa-6bac53c074e0.png', '/uploads/idcard/2026/04/06/2f1d5122-5ebf-4daa-99a2-f25bc5ce7d3f.png', 1, '', '2026-04-06 21:27:38', '2026-04-06 21:27:38', 0);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `type` tinyint NULL DEFAULT 1 COMMENT '类型: 1-系统公告, 2-活动公告, 3-政策公告',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布',
  `top` tinyint NULL DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '欢迎使用房屋租赁系统', '欢迎使用房屋租赁系统！本平台致力于为您提供安全、便捷、高效的房屋租赁服务。如有任何问题，请联系客服。', 1, 1, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `notice` VALUES (2, '平台服务升级通知', '为了给您提供更好的服务体验，我们将于本周六凌晨2:00-6:00进行系统升级维护，届时部分功能可能暂时无法使用，敬请谅解。', 1, 1, 0, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `notice` VALUES (3, '新用户注册优惠活动', '即日起至本月底，新用户注册即可获得100元租房优惠券，可在预约看房时使用。活动期间还有更多惊喜等你发现！', 2, 1, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `notice` VALUES (4, '关于规范房源发布的通知', '为维护良好的平台秩序，请各位房东在发布房源时确保信息真实准确，禁止发布虚假房源信息。违规者将被限制发布权限。', 3, 1, 0, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` tinyint NULL DEFAULT 1 COMMENT '角色: 0-管理员, 1-普通用户, 2-房东',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000000', 'admin@rental.com', NULL, 0, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `user` VALUES (2, 'landlord1', 'e10adc3949ba59abbe56e057f20f883e', '王房东', '13800000001', 'wang@rental.com', NULL, 2, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `user` VALUES (3, 'landlord2', 'e10adc3949ba59abbe56e057f20f883e', '李房东', '13800000002', 'li@rental.com', NULL, 2, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `user` VALUES (4, 'user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800000003', 'zhangsan@rental.com', '/uploads/26931e3a-435f-4ef9-9574-830b5900f8cc.jpg', 1, 1, '2026-01-12 21:53:03', '2026-01-12 21:53:03', 0);
INSERT INTO `user` VALUES (5, 'user2', 'e10adc3949ba59abbe56e057f20f883e', '李四1', '13800000004', 'lisi@rental.com', NULL, 1, 1, '2026-01-12 21:53:03', '2026-04-03 10:40:30', 0);
INSERT INTO `user` VALUES (6, 'testuser', 'e10adc3949ba59abbe56e057f20f883e', 'liang1124', '13471521113', NULL, NULL, 2, 1, '2026-04-03 17:03:29', '2026-04-06 21:27:53', 0);

-- ----------------------------
-- Table structure for user_browse_history
-- ----------------------------
DROP TABLE IF EXISTS `user_browse_history`;
CREATE TABLE `user_browse_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `browse_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_house_id`(`house_id` ASC) USING BTREE,
  INDEX `idx_browse_time`(`browse_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户浏览历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_browse_history
-- ----------------------------
INSERT INTO `user_browse_history` VALUES (1, 1, 3, '2026-04-06 23:58:53');

-- ----------------------------
-- Table structure for user_collect
-- ----------------------------
DROP TABLE IF EXISTS `user_collect`;
CREATE TABLE `user_collect`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_house`(`user_id` ASC, `house_id` ASC) USING BTREE,
  INDEX `idx_house_id`(`house_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_collect
-- ----------------------------
INSERT INTO `user_collect` VALUES (3, 1, 6, '2026-03-23 19:27:50');

-- ----------------------------
-- Foreign Keys
-- ----------------------------
ALTER TABLE `house` ADD CONSTRAINT `fk_house_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `landlord` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `house` ADD CONSTRAINT `fk_house_area` FOREIGN KEY (`area_id`) REFERENCES `area` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE `evaluations` ADD CONSTRAINT `fk_evaluations_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `evaluations` ADD CONSTRAINT `fk_evaluations_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `evaluations_upvote` ADD CONSTRAINT `fk_upvote_evaluation` FOREIGN KEY (`evaluation_id`) REFERENCES `evaluations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `evaluations_upvote` ADD CONSTRAINT `fk_upvote_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `house_order_info` ADD CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `house_order_info` ADD CONSTRAINT `fk_order_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `house_order_status` ADD CONSTRAINT `fk_order_status_order` FOREIGN KEY (`order_id`) REFERENCES `house_order_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `house_rental` ADD CONSTRAINT `fk_rental_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `house_rental` ADD CONSTRAINT `fk_rental_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `house_rental_status` ADD CONSTRAINT `fk_rental_status_rental` FOREIGN KEY (`rental_id`) REFERENCES `house_rental` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `landlord` ADD CONSTRAINT `fk_landlord_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE `user_browse_history` ADD CONSTRAINT `fk_browse_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `user_browse_history` ADD CONSTRAINT `fk_browse_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `user_collect` ADD CONSTRAINT `fk_collect_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `user_collect` ADD CONSTRAINT `fk_collect_house` FOREIGN KEY (`house_id`) REFERENCES `house` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;
