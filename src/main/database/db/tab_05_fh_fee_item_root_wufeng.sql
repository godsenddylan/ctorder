CREATE TABLE `fh_fee_item` (
  `created_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建日期',
  `updated_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `updated_date` datetime NOT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `loss_id` bigint(20) NOT NULL COMMENT '定损ID',
  `insure_code` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '险种名称',
  `fee_type` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '费用类型',
  `loss_amount` decimal(16,2) DEFAULT NULL COMMENT '费用金额',
  `audit_amount` decimal(16,2) DEFAULT NULL COMMENT '核损金额',
  `first_amount` decimal(16,2) DEFAULT NULL COMMENT '第一次定损总额',
  PRIMARY KEY (`id`),
  KEY `idx_fee_lossid` (`loss_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='定损费用信息';