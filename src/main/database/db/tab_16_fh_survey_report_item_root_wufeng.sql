CREATE TABLE `fh_survey_report_item` (
  `created_by` varchar(32) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建日期',
  `updated_by` varchar(32) NOT NULL COMMENT '修改人',
  `updated_date` datetime NOT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `survey_id` bigint(20) NOT NULL COMMENT '查勘ID',
  `code` varchar(10) NOT NULL COMMENT '项目代码',
  `name` varchar(200) NOT NULL COMMENT '项目名称',
  `value` varchar(10) DEFAULT NULL COMMENT '项目值 1:是 2:否 3:不确定',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_sry_rpt_sryid` (`survey_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='查勘报告项目';