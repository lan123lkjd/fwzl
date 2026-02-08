-- 房屋租赁系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS house_rental DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE house_rental;

-- 1. 用户表
DROP TABLE IF EXISTS `user_browse_history`;
DROP TABLE IF EXISTS `user_collect`;
DROP TABLE IF EXISTS `user_area`;
DROP TABLE IF EXISTS `flow_index`;
DROP TABLE IF EXISTS `evaluations_upvote`;
DROP TABLE IF EXISTS `evaluations`;
DROP TABLE IF EXISTS `house_order_evaluations`;
DROP TABLE IF EXISTS `house_order_status`;
DROP TABLE IF EXISTS `house_order_info`;
DROP TABLE IF EXISTS `house_news`;
DROP TABLE IF EXISTS `house`;
DROP TABLE IF EXISTS `community`;
DROP TABLE IF EXISTS `area`;
DROP TABLE IF EXISTS `landlord`;
DROP TABLE IF EXISTS `notice`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` TINYINT DEFAULT 1 COMMENT '角色: 0-管理员, 1-普通用户, 2-房东',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 房东信息表
CREATE TABLE IF NOT EXISTS `landlord` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房东ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `id_card` VARCHAR(20) NOT NULL COMMENT '身份证号',
    `contact` VARCHAR(20) COMMENT '联系方式',
    `address` VARCHAR(255) COMMENT '联系地址',
    `id_card_front` VARCHAR(255) COMMENT '身份证正面照片',
    `id_card_back` VARCHAR(255) COMMENT '身份证背面照片',
    `verify_status` TINYINT DEFAULT 0 COMMENT '认证状态: 0-待审核, 1-已认证, 2-认证失败',
    `verify_remark` VARCHAR(255) COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房东信息表';

-- 3. 地区表
CREATE TABLE IF NOT EXISTS `area` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地区ID',
    `name` VARCHAR(50) NOT NULL COMMENT '地区名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID',
    `level` TINYINT DEFAULT 1 COMMENT '层级: 1-省, 2-市, 3-区/县',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地区表';

-- 4. 小区信息表
CREATE TABLE IF NOT EXISTS `community` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '小区ID',
    `name` VARCHAR(100) NOT NULL COMMENT '小区名称',
    `area_id` BIGINT NOT NULL COMMENT '所属地区ID',
    `address` VARCHAR(255) COMMENT '详细地址',
    `description` TEXT COMMENT '小区描述',
    `build_year` INT COMMENT '建成年份',
    `property_company` VARCHAR(100) COMMENT '物业公司',
    `property_fee` DECIMAL(10,2) COMMENT '物业费(元/月/平米)',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_area_id` (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小区信息表';

-- 5. 房源信息表
CREATE TABLE IF NOT EXISTS `house` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房源ID',
    `title` VARCHAR(200) NOT NULL COMMENT '房源标题',
    `landlord_id` BIGINT NOT NULL COMMENT '房东ID',
    `community_id` BIGINT COMMENT '小区ID',
    `area_id` BIGINT COMMENT '所属地区ID',
    `address` VARCHAR(255) COMMENT '详细地址',
    `price` DECIMAL(10,2) NOT NULL COMMENT '月租金(元)',
    `area` DECIMAL(10,2) COMMENT '面积(平方米)',
    `rooms` TINYINT COMMENT '房间数',
    `halls` TINYINT COMMENT '客厅数',
    `bathrooms` TINYINT COMMENT '卫生间数',
    `floor` INT COMMENT '楼层',
    `total_floor` INT COMMENT '总楼层',
    `orientation` VARCHAR(20) COMMENT '朝向',
    `decoration` VARCHAR(20) COMMENT '装修情况',
    `house_type` TINYINT DEFAULT 1 COMMENT '房屋类型: 1-整租, 2-合租',
    `description` TEXT COMMENT '房源描述',
    `facilities` VARCHAR(500) COMMENT '配套设施(JSON)',
    `images` TEXT COMMENT '房源图片(JSON数组)',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审核, 1-已上架, 2-已下架, 3-已出租',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `collect_count` INT DEFAULT 0 COMMENT '收藏次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_landlord_id` (`landlord_id`),
    INDEX `idx_community_id` (`community_id`),
    INDEX `idx_area_id` (`area_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源信息表';

-- 6. 房屋资讯表
CREATE TABLE IF NOT EXISTS `house_news` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资讯ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `summary` VARCHAR(500) COMMENT '摘要',
    `cover_image` VARCHAR(255) COMMENT '封面图片',
    `author_id` BIGINT COMMENT '作者ID',
    `category` VARCHAR(50) COMMENT '分类',
    `tags` VARCHAR(200) COMMENT '标签',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_author_id` (`author_id`),
    INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋资讯表';

-- 7. 预约看房信息表
CREATE TABLE IF NOT EXISTS `house_order_info` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '预约单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `house_id` BIGINT NOT NULL COMMENT '房源ID',
    `landlord_id` BIGINT NOT NULL COMMENT '房东ID',
    `order_time` DATETIME NOT NULL COMMENT '预约时间',
    `contact_name` VARCHAR(50) COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `remark` VARCHAR(500) COMMENT '备注',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待确认, 1-已确认, 2-已完成, 3-已取消, 4-已拒绝',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_house_id` (`house_id`),
    INDEX `idx_landlord_id` (`landlord_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约看房信息表';

-- 8. 预约看房状态流转表
CREATE TABLE IF NOT EXISTS `house_order_status` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `order_id` BIGINT NOT NULL COMMENT '预约ID',
    `status` TINYINT NOT NULL COMMENT '状态',
    `operator_id` BIGINT COMMENT '操作人ID',
    `operator_type` TINYINT COMMENT '操作人类型: 1-用户, 2-房东, 3-管理员',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约看房状态流转表';

-- 9. 预约看房服务评价表
CREATE TABLE IF NOT EXISTS `house_order_evaluations` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT NOT NULL COMMENT '预约ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `landlord_id` BIGINT NOT NULL COMMENT '房东ID',
    `score` TINYINT DEFAULT 5 COMMENT '评分: 1-5',
    `content` VARCHAR(500) COMMENT '评价内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE INDEX `idx_order_id` (`order_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_landlord_id` (`landlord_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约看房服务评价表';

-- 10. 评论表
CREATE TABLE IF NOT EXISTS `evaluations` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `house_id` BIGINT NOT NULL COMMENT '房源ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` TEXT COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID(0表示顶级评论)',
    `reply_user_id` BIGINT COMMENT '回复的用户ID',
    `upvote_count` INT DEFAULT 0 COMMENT '点赞数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_house_id` (`house_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 11. 评论点赞表
CREATE TABLE IF NOT EXISTS `evaluations_upvote` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `evaluation_id` BIGINT NOT NULL COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX `idx_evaluation_user` (`evaluation_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

-- 12. 公告表
CREATE TABLE IF NOT EXISTS `notice` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `type` TINYINT DEFAULT 1 COMMENT '类型: 1-系统公告, 2-活动公告, 3-政策公告',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布',
    `top` TINYINT DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 13. 流量指标信息表
CREATE TABLE IF NOT EXISTS `flow_index` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `date` DATE NOT NULL UNIQUE COMMENT '日期',
    `pv` INT DEFAULT 0 COMMENT '页面浏览量',
    `uv` INT DEFAULT 0 COMMENT '独立访客数',
    `new_users` INT DEFAULT 0 COMMENT '新增用户数',
    `house_views` INT DEFAULT 0 COMMENT '房源浏览量',
    `order_count` INT DEFAULT 0 COMMENT '预约数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流量指标信息表';

-- 14. 用户常居住地信息表
CREATE TABLE IF NOT EXISTS `user_area` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `area_id` BIGINT NOT NULL COMMENT '地区ID',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认: 0-否, 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_area_id` (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户常居住地信息表';

-- 15. 用户收藏表
CREATE TABLE IF NOT EXISTS `user_collect` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `house_id` BIGINT NOT NULL COMMENT '房源ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX `idx_user_house` (`user_id`, `house_id`),
    INDEX `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 16. 用户浏览历史表
CREATE TABLE IF NOT EXISTS `user_browse_history` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `house_id` BIGINT NOT NULL COMMENT '房源ID',
    `browse_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_house_id` (`house_id`),
    INDEX `idx_browse_time` (`browse_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览历史表';

-- ================== 测试数据 ==================

-- 用户数据 (密码都是123456的MD5)
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `email`, `role`, `status`) VALUES 
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000000', 'admin@rental.com', 0, 1),
(2, 'landlord1', 'e10adc3949ba59abbe56e057f20f883e', '王房东', '13800000001', 'wang@rental.com', 2, 1),
(3, 'landlord2', 'e10adc3949ba59abbe56e057f20f883e', '李房东', '13800000002', 'li@rental.com', 2, 1),
(4, 'user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800000003', 'zhangsan@rental.com', 1, 1),
(5, 'user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800000004', 'lisi@rental.com', 1, 1);

-- 房东信息
INSERT INTO `landlord` (`id`, `user_id`, `real_name`, `id_card`, `contact`, `verify_status`) VALUES 
(1, 2, '王大明', '110101199001011234', '13800000001', 1),
(2, 3, '李小红', '310101199002022345', '13800000002', 1);

-- 地区数据
INSERT INTO `area` (`id`, `name`, `parent_id`, `level`, `sort`) VALUES 
(1, '北京市', 0, 1, 1),
(2, '上海市', 0, 1, 2),
(3, '广东省', 0, 1, 3),
(4, '朝阳区', 1, 2, 1),
(5, '海淀区', 1, 2, 2),
(6, '西城区', 1, 2, 3),
(7, '浦东新区', 2, 2, 1),
(8, '黄浦区', 2, 2, 2),
(9, '徐汇区', 2, 2, 3),
(10, '深圳市', 3, 2, 1),
(11, '广州市', 3, 2, 2);

-- 小区数据
INSERT INTO `community` (`id`, `name`, `area_id`, `address`, `build_year`, `property_company`, `property_fee`) VALUES 
(1, '阳光花园', 4, '北京市朝阳区建国路88号', 2015, '万科物业', 3.50),
(2, '绿城小区', 4, '北京市朝阳区望京西路66号', 2018, '绿城物业', 4.00),
(3, '万科城市花园', 5, '北京市海淀区中关村大街100号', 2016, '万科物业', 3.80),
(4, '陆家嘴公寓', 7, '上海市浦东新区陆家嘴环路1000号', 2019, '绿城物业', 5.00),
(5, '世纪公园小区', 7, '上海市浦东新区世纪大道2000号', 2017, '万科物业', 4.50);

-- 房源数据
INSERT INTO `house` (`id`, `title`, `landlord_id`, `community_id`, `area_id`, `address`, `price`, `area`, `rooms`, `halls`, `bathrooms`, `floor`, `total_floor`, `orientation`, `decoration`, `house_type`, `description`, `status`, `view_count`, `collect_count`, `cover_image`) VALUES 
(1, '朝阳区精装两居室 近地铁 拎包入住', 1, 1, 4, '阳光花园3号楼1单元801室', 6500.00, 85.00, 2, 1, 1, 8, 18, '朝南', '精装修', 1, '房屋位于阳光花园小区，交通便利，周边配套设施齐全。精装修两居室，家电家具齐全，可拎包入住。小区环境优美，物业管理完善。', 1, 256, 35, 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800'),
(2, '海淀区温馨一居室 适合情侣/单身', 1, 3, 5, '万科城市花园5号楼2单元402室', 4500.00, 55.00, 1, 1, 1, 4, 12, '朝南', '精装修', 1, '位于海淀区核心地段，靠近中关村，适合在附近工作的白领。一居室布局合理，采光良好。', 1, 189, 28, 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800'),
(3, '望京豪华三居室 全新装修 家电齐全', 2, 2, 4, '绿城小区8号楼1单元1502室', 9800.00, 120.00, 3, 2, 2, 15, 20, '朝南', '豪华装修', 1, '望京核心区域，三居室大户型，全新豪华装修。主卧带独立卫生间，客厅超大。适合家庭居住。', 1, 320, 48, 'https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800'),
(4, '浦东陆家嘴精品公寓 高层江景房', 2, 4, 7, '陆家嘴公寓A座2301室', 12000.00, 90.00, 2, 1, 1, 23, 35, '朝东', '豪华装修', 1, '地处浦东金融中心，坐拥黄浦江美景。高端精装公寓，24小时安保。步行可达陆家嘴地铁站。', 1, 412, 62, 'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800'),
(5, '世纪公园旁温馨两居室 采光极佳', 1, 5, 7, '世纪公园小区6号楼902室', 7500.00, 78.00, 2, 1, 1, 9, 16, '朝南', '精装修', 1, '紧邻世纪公园，环境优美空气清新。两居室格局方正，客厅开阔。周边商业配套完善。', 1, 178, 25, 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800'),
(6, '北京西城舒适单间 交通便利', 1, NULL, 6, '北京市西城区西单北大街50号', 3200.00, 25.00, 1, 0, 1, 5, 8, '朝北', '简单装修', 2, '西单核心地段，交通极其便利。单间配独立卫生间，适合单身上班族。', 1, 98, 12, 'https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=800');

-- 房屋资讯
INSERT INTO `house_news` (`id`, `title`, `content`, `summary`, `category`, `view_count`, `status`, `author_id`) VALUES 
(1, '2024年租房市场趋势分析', '根据最新数据显示，2024年一线城市租房市场呈现稳中有升的态势。北京、上海、深圳等城市的核心区域租金保持稳定，而新兴区域则有小幅上涨。\n\n专家建议租房者应该关注以下几点：\n1. 选择交通便利的区域\n2. 关注周边配套设施\n3. 提前做好预算规划\n4. 注意合同条款\n\n总体来看，市场将继续保持健康发展态势。', '2024年一线城市租房市场呈现稳中有升态势，核心区域租金稳定，新兴区域小幅上涨。', '市场分析', 1256, 1, 1),
(2, '租房注意事项完全指南', '租房时需要注意以下几个关键点：\n\n一、看房阶段\n- 检查房屋结构和设施\n- 确认水电燃气正常\n- 了解周边环境和配套\n\n二、签约阶段\n- 核实房东身份\n- 明确租金支付方式\n- 确认押金退还条件\n\n三、入住阶段\n- 拍照记录房屋现状\n- 保存好各类凭证\n- 及时报修问题设施', '从看房、签约到入住，全面介绍租房过程中需要注意的各类事项。', '租房指南', 2345, 1, 1),
(3, '如何选择适合自己的房源', '选择房源时，除了价格因素外，还需要综合考虑以下方面：\n\n1. 通勤距离：尽量选择距离工作地点30分钟以内的房源\n2. 周边配套：超市、医院、银行等基本设施\n3. 小区环境：物业管理、安全系数\n4. 房屋条件：采光、通风、装修情况\n5. 未来规划：是否有地铁或商业规划', '综合考虑通勤距离、周边配套、小区环境、房屋条件等因素选择房源。', '租房指南', 1876, 1, 1),
(4, '北京各区租金参考价格', '北京各主要区域租金参考：\n\n朝阳区：\n- 一居室：4500-6500元/月\n- 两居室：6000-9000元/月\n- 三居室：8000-15000元/月\n\n海淀区：\n- 一居室：4000-6000元/月\n- 两居室：5500-8500元/月\n\n西城区：\n- 一居室：5000-7000元/月\n- 两居室：7000-10000元/月', '北京各主要区域最新租金参考价格，帮助租房者做好预算。', '市场分析', 3421, 1, 1),
(5, '租房合同签订注意事项', '签订租房合同时，请务必注意以下几点：\n\n1. 确认房东身份，查看房产证原件\n2. 明确租赁期限、租金及支付方式\n3. 约定押金退还条件和时间\n4. 列明房屋内家具家电清单\n5. 明确维修责任和费用承担\n6. 约定违约责任和解约条件\n\n建议使用正规租房合同模板，避免霸王条款。', '租房合同签订时的关键注意事项，保障租客合法权益。', '法律知识', 1567, 1, 1);

-- 公告数据
INSERT INTO `notice` (`id`, `title`, `content`, `type`, `status`, `top`) VALUES 
(1, '欢迎使用房屋租赁系统', '欢迎使用房屋租赁系统！本平台致力于为您提供安全、便捷、高效的房屋租赁服务。如有任何问题，请联系客服。', 1, 1, 1),
(2, '平台服务升级通知', '为了给您提供更好的服务体验，我们将于本周六凌晨2:00-6:00进行系统升级维护，届时部分功能可能暂时无法使用，敬请谅解。', 1, 1, 0),
(3, '新用户注册优惠活动', '即日起至本月底，新用户注册即可获得100元租房优惠券，可在预约看房时使用。活动期间还有更多惊喜等你发现！', 2, 1, 1),
(4, '关于规范房源发布的通知', '为维护良好的平台秩序，请各位房东在发布房源时确保信息真实准确，禁止发布虚假房源信息。违规者将被限制发布权限。', 3, 1, 0);

-- 评论数据
INSERT INTO `evaluations` (`id`, `house_id`, `user_id`, `content`, `upvote_count`) VALUES 
(1, 1, 4, '房子很不错，装修风格我很喜欢，房东人也很好说话。', 15),
(2, 1, 5, '位置很方便，出门就是地铁站，周边吃饭购物都很方便。', 8),
(3, 2, 4, '一居室很温馨，适合我们小两口住，就是面积稍微小了点。', 5),
(4, 3, 5, '豪华三居室名不虚传，家具家电都是品牌的，值这个价！', 22),
(5, 4, 4, '陆家嘴的房子就是贵，但是真的很方便，适合金融行业的朋友。', 18);

-- 流量统计数据
INSERT INTO `flow_index` (`date`, `pv`, `uv`, `new_users`, `house_views`, `order_count`) VALUES 
(DATE_SUB(CURDATE(), INTERVAL 6 DAY), 1200, 350, 25, 458, 12),
(DATE_SUB(CURDATE(), INTERVAL 5 DAY), 1450, 420, 32, 523, 18),
(DATE_SUB(CURDATE(), INTERVAL 4 DAY), 1680, 480, 28, 612, 15),
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 1320, 390, 20, 445, 10),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 1560, 445, 35, 578, 22),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1890, 520, 42, 689, 28),
(CURDATE(), 856, 245, 18, 312, 8);

-- 预约数据
INSERT INTO `house_order_info` (`id`, `order_no`, `user_id`, `house_id`, `landlord_id`, `order_time`, `contact_name`, `contact_phone`, `status`) VALUES 
(1, 'ORD20260112001', 4, 1, 1, '2026-01-15 14:00:00', '张三', '13800000003', 1),
(2, 'ORD20260112002', 5, 3, 2, '2026-01-16 10:00:00', '李四', '13800000004', 0),
(3, 'ORD20260112003', 4, 4, 2, '2026-01-14 16:00:00', '张三', '13800000003', 2);
