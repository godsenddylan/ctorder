CREATE TABLE `fh_repair_factory_info` (
  `created_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建日期',
  `updated_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `updated_date` datetime NOT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `loss_id` bigint(20) NOT NULL COMMENT '定损ID',
  `is_push_repair` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '是否推送修',
  `no_push_reason` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '未推送修备注',
  `channel_factory` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '渠道修理厂',
  `factory_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '修理厂名称',
  `factory_type` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '修理厂类型',
  `organization_no` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '组织机构代码证号码',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_rep_fac_lossid` (`loss_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='修理厂信息';