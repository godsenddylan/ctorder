CREATE TABLE `fh_insure_data_info` (
  `created_by` varchar(32) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建日期',
  `updated_by` varchar(32) NOT NULL COMMENT '修改人',
  `updated_date` datetime NOT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `object_name` varchar(100) NOT NULL COMMENT '对象名称',
  `object_value` text COMMENT '对象值',
  `report_no` varchar(32) DEFAULT NULL COMMENT '报案号',
  `task_no` varchar(32) DEFAULT NULL COMMENT '保险公司任务号',
  `query_node` varchar(200) NOT NULL COMMENT '查询用的节点',
  PRIMARY KEY (`id`),
  KEY `idx_ins_data_rpt` (`report_no`),
  KEY `idx_ins_data_nm_qn` (`object_name`,`query_node`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='保险公司数据表';