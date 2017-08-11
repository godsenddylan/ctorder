CREATE TABLE `fh_work_send_log` (
  `created_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '创建人',
  `created_date` datetime NOT NULL COMMENT '创建日期',
  `updated_by` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `updated_date` datetime NOT NULL COMMENT '修改日期',
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `task_id` bigint(20) NOT NULL COMMENT '案件任务ID',
  `content` text COLLATE utf8_bin COMMENT '发送内容',
  `send_date` datetime DEFAULT NULL COMMENT '发送时间',
  `send_state` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '发送状态:0未发送  1发送成功 2发送失败',
  `back_result` text COLLATE utf8_bin COMMENT '返回结果',
  `remark` text COLLATE utf8_bin COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='作业信息异步调用日志表';