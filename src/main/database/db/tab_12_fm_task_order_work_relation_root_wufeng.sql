CREATE TABLE `fm_task_order_work_relation` (
  `created_by` VARCHAR(32) NOT NULL COMMENT '创建人',
  `created_date` DATETIME NOT NULL COMMENT '创建日期',
  `updated_by` VARCHAR(32) NOT NULL COMMENT '修改人',
  `updated_date` DATETIME NOT NULL COMMENT '修改日期',
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `task_id` VARCHAR(32) NOT NULL COMMENT '任务ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `work_id` BIGINT(20) DEFAULT NULL COMMENT '作业ID(查勘、定损、物损等等)',
  `work_type` VARCHAR(10) DEFAULT NULL COMMENT '作业类型 0:查勘 1:定损',
  PRIMARY KEY (`id`),
  KEY `idx_towr_allid` (`task_id`,`order_no`)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='作业任务订单关系表';
