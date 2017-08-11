CREATE TABLE `sys_parameters` (
  `created_by` varchar(100) NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建时间',
  `updated_by` varchar(100) NOT NULL COMMENT '更新人',
  `updated_date` datetime NOT NULL COMMENT '更新时间',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `company_code` varchar(10) NOT NULL COMMENT '公司编码',
  `type_code` varchar(30) NOT NULL COMMENT '参数类型编码',
  `code` varchar(100) NOT NULL COMMENT '编码',
  `short_name` varchar(100) NOT NULL COMMENT '简称',
  `full_name` varchar(150) NOT NULL COMMENT '全称',
  `parent_code` varchar(100) NOT NULL COMMENT '父CODE',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注说明',
  PRIMARY KEY (`id`),
  KEY `idx_company_code` (`company_code`),
  KEY `idx_type_code` (`type_code`),
  KEY `idx_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='系统参数表';