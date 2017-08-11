CREATE TABLE `fh_send_data_info` (
  `created_by` VARCHAR(32) NOT NULL COMMENT '创建人',
  `created_date` DATETIME NOT NULL COMMENT '创建时间',
  `updated_by` VARCHAR(32) NOT NULL COMMENT '更新人',
  `updated_date` DATETIME NOT NULL COMMENT '更新时间',
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `report_no` VARCHAR(64) NOT NULL COMMENT '报案号',
  `relation_id` BIGINT(20) NOT NULL COMMENT '关联表ID',
  `relation_type` CHAR(2) NOT NULL COMMENT '关联类型:1=fm_task_info永诚作业 2=hy_image永诚图片',
  `send_state` VARCHAR(1) NOT NULL COMMENT '发送状态: 0未发送 1发送成功 2发送失败',
  `content` TEXT COMMENT '发送类容',
  `send_date` DATETIME NOT NULL COMMENT '发送时间',
  `back_result` TEXT COMMENT '返回结果',
  `remark` TEXT COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_sdi_rltidtype` (`relation_id`,`relation_type`),
  KEY `idx_sdi_rptno` (`report_no`),
  KEY `idx_sdi_state` (`send_state`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='数据发送记录表';
