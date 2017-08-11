CREATE TABLE `fh_third_car_info` (
  `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created_date` datetime DEFAULT NULL COMMENT '创建日期',
  `updated_by` varchar(32) DEFAULT NULL COMMENT '修改人',
  `updated_date` datetime DEFAULT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `car_mark` varchar(10) DEFAULT NULL COMMENT '车牌号',
  `report_no` varchar(32) DEFAULT NULL COMMENT '报案号',
  `company_code` varchar(10) DEFAULT NULL COMMENT '保险公司代码',
  `policy_no` varchar(32) DEFAULT NULL COMMENT '保单号',
  `claim_amount` decimal(16,2) DEFAULT NULL COMMENT '损失金额',
  `driver_name` varchar(32) DEFAULT NULL COMMENT '驾驶员姓名',
  `driver_phone` varchar(11) DEFAULT NULL COMMENT '驾驶员电话',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_thd_car_mark` (`car_mark`),
  KEY `idx_thd_car_rptno` (`report_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='三者车信息';