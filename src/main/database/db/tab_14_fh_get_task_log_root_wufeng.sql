CREATE TABLE `fh_get_task_log` (
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created_date` datetime DEFAULT NULL COMMENT '创建日期',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '修改人',
  `updated_date` datetime DEFAULT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `insert_date` datetime DEFAULT NULL COMMENT '调用接口时间',
  `parameters` varchar(1000) DEFAULT NULL COMMENT '参数',
  `result` text COMMENT '结果',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
