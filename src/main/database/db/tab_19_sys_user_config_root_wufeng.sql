CREATE TABLE `sys_user_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '表ID',
  `company_code` VARCHAR(10) DEFAULT NULL COMMENT '保险公司代码',
  `user_code` VARCHAR(60) DEFAULT NULL COMMENT '用户代码',
  `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名称',
  `dept_code` VARCHAR(30) DEFAULT NULL COMMENT '所属部门代码',
  `dept_name` VARCHAR(150) DEFAULT NULL COMMENT '所属部门名称',
  `relevance_code` VARCHAR(100) DEFAULT NULL COMMENT '与车童网管理账号',
  PRIMARY KEY (`id`)
) ENGINE=MYISAM DEFAULT CHARSET=utf8 COMMENT='保险公司账号关联配置表';

