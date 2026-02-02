/*
 电影推荐系统数据库设计
 数据库名：movie_recommendation
 字符集：utf8mb4
 排序规则：utf8mb4_unicode_ci
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==================== 用户相关表 ====================

-- 1. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名（邮箱）',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `tel` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别：male/female/unknown',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive/banned',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 管理员表（已存在，保持兼容）
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（加密）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `tel` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) DEFAULT 'admin' COMMENT '角色：admin/super_admin',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 3. 用户兴趣标签表
DROP TABLE IF EXISTS `user_interest_tag`;
CREATE TABLE `user_interest_tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称（如：科幻、喜剧、悬疑）',
  `weight` decimal(5,2) DEFAULT 1.00 COMMENT '权重（0-1之间）',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户兴趣标签表';

-- ==================== 电影相关表 ====================

-- 4. 电影类型表
DROP TABLE IF EXISTS `movie_type`;
CREATE TABLE `movie_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `name` varchar(50) NOT NULL COMMENT '类型名称（如：动作、喜剧、科幻）',
  `description` varchar(500) DEFAULT NULL COMMENT '类型描述',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电影类型表';

-- 5. 电影信息表
DROP TABLE IF EXISTS `movie`;
CREATE TABLE `movie` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '电影ID',
  `name` varchar(200) NOT NULL COMMENT '电影名称',
  `english_name` varchar(200) DEFAULT NULL COMMENT '英文名称',
  `director` varchar(100) DEFAULT NULL COMMENT '导演',
  `description` text COMMENT '剧情简介',
  `poster_url` varchar(500) DEFAULT NULL COMMENT '海报URL',
  `trailer_url` varchar(500) DEFAULT NULL COMMENT '预告片URL',
  `duration` int DEFAULT NULL COMMENT '时长（分钟）',
  `release_date` date DEFAULT NULL COMMENT '上映日期',
  `country` varchar(100) DEFAULT NULL COMMENT '制片国家',
  `language` varchar(50) DEFAULT NULL COMMENT '语言',
  `rating` decimal(3,1) DEFAULT 0.0 COMMENT '平均评分（0-10）',
  `rating_count` int DEFAULT 0 COMMENT '评分人数',
  `box_office` decimal(15,2) DEFAULT 0.00 COMMENT '票房（元）',
  `view_count` int DEFAULT 0 COMMENT '观看次数',
  `favorite_count` int DEFAULT 0 COMMENT '收藏次数',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending/active/inactive',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_release_date` (`release_date`),
  KEY `idx_rating` (`rating`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电影信息表';

-- 6. 电影类型关联表（多对多）
DROP TABLE IF EXISTS `movie_type_relation`;
CREATE TABLE `movie_type_relation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `movie_id` int NOT NULL COMMENT '电影ID',
  `type_id` int NOT NULL COMMENT '类型ID',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_movie_type` (`movie_id`, `type_id`),
  KEY `idx_movie_id` (`movie_id`),
  KEY `idx_type_id` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电影类型关联表';

-- 7. 电影标签表
DROP TABLE IF EXISTS `movie_tag`;
CREATE TABLE `movie_tag` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `category` varchar(50) DEFAULT NULL COMMENT '标签分类（如：genre/theme/mood）',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电影标签表';

-- 8. 电影标签关联表
DROP TABLE IF EXISTS `movie_tag_relation`;
CREATE TABLE `movie_tag_relation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `movie_id` int NOT NULL COMMENT '电影ID',
  `tag_id` int NOT NULL COMMENT '标签ID',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_movie_tag` (`movie_id`, `tag_id`),
  KEY `idx_movie_id` (`movie_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电影标签关联表';

-- 9. 演职人员表
DROP TABLE IF EXISTS `movie_cast`;
CREATE TABLE `movie_cast` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `movie_id` int NOT NULL COMMENT '电影ID',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `role` varchar(50) NOT NULL COMMENT '角色：actor/director/writer/producer',
  `character_name` varchar(100) DEFAULT NULL COMMENT '饰演角色名（仅演员）',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_movie_id` (`movie_id`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演职人员表';

-- 10. 电影评分表
DROP TABLE IF EXISTS `movie_rating`;
CREATE TABLE `movie_rating` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `movie_id` int NOT NULL COMMENT '电影ID',
  `rating` decimal(3,1) NOT NULL COMMENT '评分（0-10）',
  `comment` text COMMENT '评论内容',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_movie_id` (`movie_id`),
  KEY `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电影评分表';

-- 11. 用户收藏表
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `movie_id` int NOT NULL COMMENT '电影ID',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_movie` (`user_id`, `movie_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_movie_id` (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- ==================== 影院相关表 ====================

-- 12. 影院表
DROP TABLE IF EXISTS `cinema`;
CREATE TABLE `cinema` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '影院ID',
  `name` varchar(200) NOT NULL COMMENT '影院名称',
  `address` varchar(500) DEFAULT NULL COMMENT '地址',
  `tel` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending/active/inactive（pending需要管理员认证）',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='影院表';

-- 13. 影院房间表
DROP TABLE IF EXISTS `cinema_room`;
CREATE TABLE `cinema_room` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `cinema_id` int NOT NULL COMMENT '影院ID',
  `name` varchar(100) NOT NULL COMMENT '房间名称（如：1号厅）',
  `row_count` int NOT NULL COMMENT '行数',
  `col_count` int NOT NULL COMMENT '列数',
  `description` varchar(500) DEFAULT NULL COMMENT '房间描述',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_cinema_id` (`cinema_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='影院房间表';

-- 14. 座位表
DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '座位ID',
  `room_id` int NOT NULL COMMENT '房间ID',
  `row_num` int NOT NULL COMMENT '行号',
  `col_num` int NOT NULL COMMENT '列号',
  `seat_type` varchar(20) DEFAULT 'normal' COMMENT '座位类型：normal/vip/love_seat',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_seat` (`room_id`, `row_num`, `col_num`),
  KEY `idx_room_id` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='座位表';

-- 15. 放映场次表
DROP TABLE IF EXISTS `screening`;
CREATE TABLE `screening` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '场次ID',
  `movie_id` int NOT NULL COMMENT '电影ID',
  `cinema_id` int NOT NULL COMMENT '影院ID',
  `room_id` int NOT NULL COMMENT '房间ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `price` decimal(10,2) NOT NULL COMMENT '票价（元）',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending/active/cancelled（pending需要管理员审批）',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_movie_id` (`movie_id`),
  KEY `idx_cinema_id` (`cinema_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='放映场次表';

-- 16. 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` int NOT NULL COMMENT '用户ID',
  `screening_id` int NOT NULL COMMENT '场次ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `seat_count` int NOT NULL COMMENT '座位数量',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending/paid/cancelled/completed',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `ticket_time` timestamp NULL DEFAULT NULL COMMENT '取票时间',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_screening_id` (`screening_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 17. 订单明细表（座位信息）
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` int NOT NULL COMMENT '订单ID',
  `seat_id` int NOT NULL COMMENT '座位ID',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_seat_id` (`seat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- ==================== 推荐相关表 ====================

-- 18. 用户行为表（核心推荐数据源）
DROP TABLE IF EXISTS `user_behavior`;
CREATE TABLE `user_behavior` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `behavior_type` varchar(20) NOT NULL COMMENT '行为类型：browse/rate/favorite/comment/search/order',
  `movie_id` int DEFAULT NULL COMMENT '电影ID（非搜索行为）',
  `search_keyword` varchar(200) DEFAULT NULL COMMENT '搜索关键词（仅搜索行为）',
  `rating` decimal(3,1) DEFAULT NULL COMMENT '评分（仅评分行为）',
  `duration` int DEFAULT NULL COMMENT '浏览时长（秒，仅浏览行为）',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型：web/mobile/app',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_behavior_type` (`behavior_type`),
  KEY `idx_movie_id` (`movie_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_user_behavior_time` (`user_id`, `behavior_type`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';

-- 19. 推荐结果表（缓存推荐列表）
DROP TABLE IF EXISTS `recommendation_result`;
CREATE TABLE `recommendation_result` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `recommendation_type` varchar(50) NOT NULL COMMENT '推荐类型：collaborative/content/popular/realtime/hybrid',
  `movie_ids` text NOT NULL COMMENT '推荐电影ID列表（JSON数组）',
  `scores` text DEFAULT NULL COMMENT '推荐分数列表（JSON数组，与movie_ids对应）',
  `strategy` varchar(100) DEFAULT NULL COMMENT '使用的推荐策略',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type` (`user_id`, `recommendation_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐结果表';

-- 20. 推荐效果评估表
DROP TABLE IF EXISTS `recommendation_evaluation`;
CREATE TABLE `recommendation_evaluation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `evaluation_date` date NOT NULL COMMENT '评估日期',
  `recommendation_type` varchar(50) NOT NULL COMMENT '推荐类型',
  `precision_at_n` decimal(5,4) DEFAULT NULL COMMENT 'Precision@N',
  `recall_at_n` decimal(5,4) DEFAULT NULL COMMENT 'Recall@N',
  `diversity` decimal(5,4) DEFAULT NULL COMMENT '多样性指标',
  `user_satisfaction` decimal(5,4) DEFAULT NULL COMMENT '用户满意度',
  `click_through_rate` decimal(5,4) DEFAULT NULL COMMENT '点击率',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_date` (`evaluation_date`),
  KEY `idx_recommendation_type` (`recommendation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐效果评估表';

-- ==================== 其他表 ====================

-- 21. 公告表
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` varchar(50) DEFAULT 'general' COMMENT '类型：general/important/activity',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态：active/inactive',
  `publish_time` timestamp NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ==================== 初始化数据 ====================

-- 插入默认管理员
INSERT INTO `admin` (`username`, `password`, `nickname`, `email`, `role`, `status`) 
VALUES ('admin', '123456', '系统管理员', 'admin@movie.com', 'super_admin', 'active');

-- 插入电影类型
INSERT INTO `movie_type` (`name`, `description`) VALUES
('动作', '动作片'),
('喜剧', '喜剧片'),
('科幻', '科幻片'),
('悬疑', '悬疑片'),
('爱情', '爱情片'),
('恐怖', '恐怖片'),
('战争', '战争片'),
('历史', '历史片'),
('动画', '动画片'),
('纪录片', '纪录片');

-- 插入电影标签
INSERT INTO `movie_tag` (`name`, `category`) VALUES
('高评分', 'quality'),
('热门', 'popularity'),
('新上映', 'time'),
('经典', 'time'),
('烧脑', 'theme'),
('治愈', 'theme'),
('励志', 'theme'),
('感人', 'mood'),
('搞笑', 'mood'),
('紧张刺激', 'mood');

SET FOREIGN_KEY_CHECKS = 1;
