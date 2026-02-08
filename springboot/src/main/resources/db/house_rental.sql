-- 房屋租赁表
CREATE TABLE IF NOT EXISTS house_rental (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rental_no VARCHAR(32) NOT NULL COMMENT '租赁单号',
  user_id BIGINT NOT NULL COMMENT '租客ID',
  house_id BIGINT NOT NULL COMMENT '房源ID',
  landlord_id BIGINT NOT NULL COMMENT '房东ID',
  start_date DATE NOT NULL COMMENT '租赁开始日期',
  end_date DATE NOT NULL COMMENT '租赁结束日期',
  monthly_rent DECIMAL(10,2) NOT NULL COMMENT '月租金',
  deposit DECIMAL(10,2) COMMENT '押金',
  total_amount DECIMAL(10,2) COMMENT '总金额',
  contact_name VARCHAR(50) COMMENT '联系人姓名',
  contact_phone VARCHAR(20) COMMENT '联系电话',
  remark TEXT COMMENT '备注',
  status INT DEFAULT 0 COMMENT '状态: 0-待确认, 1-租赁中, 2-已完成, 3-已取消, 4-已拒绝',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT DEFAULT 0,
  INDEX idx_user_id (user_id),
  INDEX idx_landlord_id (landlord_id),
  INDEX idx_house_id (house_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋租赁表';
