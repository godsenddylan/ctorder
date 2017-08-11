
INSERT INTO sys_resources(resource_id,parent_id,app_id,node_level,resource_name,resource_desc,resource_type,resource_string,sort_order,enabled)
VALUES('10802','0','1','1','监控平台' ,'监控平台','2','','15','1');

INSERT INTO sys_resources(resource_id,parent_id,app_id,node_level,resource_name,resource_desc,resource_type,resource_string,sort_order,enabled)
VALUES('10803','10802','1','2','任务监控' ,'任务监控','2','/admin/monitor/monitor_task_query.jsp','1','1');