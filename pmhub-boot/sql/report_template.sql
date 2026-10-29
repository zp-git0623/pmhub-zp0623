-- 可复用的 HiPrint 报表模板；不包含预置模板数据。
-- 手工电表打印按钮挂在现有登记菜单下，请先应用 monitor_manual_electricity.sql。
CREATE TABLE IF NOT EXISTS `report_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `report_code` varchar(64) NOT NULL COMMENT '报表编码',
  `template_json` longtext NOT NULL COMMENT 'HiPrint模板JSON',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`template_id`),
  KEY `idx_report_template_code_status` (`report_code`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表打印模板';

START TRANSACTION;

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '报表管理', 0, 8, 'report', NULL, '', 1, 0, 'M', '0', '0', '', 'documentation', 'admin', NOW(), '', NULL, '报表模板管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `parent_id` = 0 AND `path` = 'report' AND `menu_type` = 'M');

SET @report_menu_id = (SELECT `menu_id` FROM `sys_menu` WHERE `parent_id` = 0 AND `path` = 'report' AND `menu_type` = 'M' LIMIT 1);

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '报表模板', @report_menu_id, 1, 'template', 'report/template/index', '', 1, 0, 'C', '0', '0', 'report:template:list', 'form', 'admin', NOW(), '', NULL, '维护可复用的HiPrint打印模板'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'report:template:list');

SET @report_template_menu_id = (SELECT `menu_id` FROM `sys_menu` WHERE `perms` = 'report:template:list' LIMIT 1);

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '查询', @report_template_menu_id, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'report:template:query', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'report:template:query');

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '新增', @report_template_menu_id, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'report:template:add', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'report:template:add');

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '编辑', @report_template_menu_id, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'report:template:edit', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'report:template:edit');

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '删除', @report_template_menu_id, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'report:template:remove', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'report:template:remove');

SET @manual_electricity_menu_id = (SELECT `menu_id` FROM `sys_menu` WHERE `perms` = 'monitor:manualElectricity:list' LIMIT 1);

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '打印', @manual_electricity_menu_id, 4, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:manualElectricity:print', '#', 'admin', NOW(), '', NULL, '使用报表模板打印选中的登记记录'
FROM DUAL
WHERE @manual_electricity_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'monitor:manualElectricity:print');

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT `role_id`, @report_menu_id FROM `sys_role`
WHERE `role_key` = 'admin' AND `status` = '0' AND `del_flag` = '0';

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.`role_id`, m.`menu_id`
FROM `sys_role` r
JOIN `sys_menu` m ON m.`perms` IN (
  'report:template:list',
  'report:template:query',
  'report:template:add',
  'report:template:edit',
  'report:template:remove',
  'monitor:manualElectricity:print'
)
WHERE r.`role_key` = 'admin' AND r.`status` = '0' AND r.`del_flag` = '0';

COMMIT;
