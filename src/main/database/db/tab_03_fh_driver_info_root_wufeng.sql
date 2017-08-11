CREATE TABLE `fh_driver_info` (
  `created_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建日期',
  `updated_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `updated_date` datetime NOT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `car_id` bigint(20) DEFAULT NULL COMMENT '车ID',
  `driver_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '驾驶员名字',
  `driver_phone` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '驾驶员电话',
  `driver_card` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '驾驶证',
  `permit_model` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '准驾车型',
  `is_driver` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '是否指定驾驶员',
  `driver_type` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '驾驶证类型',
  `remark` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '备注说明',
  PRIMARY KEY (`id`),
  KEY `idx_carid` (`car_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='驾驶员信息表';