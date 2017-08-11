CREATE TABLE `fh_repair_item` (
  `created_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建日期',
  `updated_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `updated_date` datetime NOT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `loss_id` bigint(20) NOT NULL COMMENT '订单号',
  `insure_code` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '险种名称',
  `repair_name` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '修理项目',
  `repair_amount` decimal(16,2) DEFAULT NULL COMMENT '工时费报价',
  `audit_price` decimal(16,2) DEFAULT NULL COMMENT '工时费核损',
  `first_amount` decimal(16,2) DEFAULT NULL COMMENT '第一次定损总额',
  PRIMARY KEY (`id`),
  KEY `idx_repair_lossid` (`loss_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='定损维修项目信息';