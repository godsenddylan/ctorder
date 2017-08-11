ALTER TABLE `fm_task_info`   
  ADD COLUMN `is_show` VARCHAR(1) NULL  COMMENT '是否显示：0或null =调度不显示  1=不显示在调度中心' AFTER `work_address`;

ALTER TABLE `fm_task_info`   
  ADD COLUMN `company_user` VARCHAR(60) NULL  COMMENT '保险公司对应的作业工号' AFTER `is_show`;