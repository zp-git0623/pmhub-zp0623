-- 手工电表登记：每个区域每天维护一条电能用量记录。
CREATE TABLE IF NOT EXISTS `monitor_manual_electricity` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `record_date` date NOT NULL COMMENT '登记日期',
  `area_name` varchar(100) NOT NULL COMMENT '区域名称',
  `energy_kwh` decimal(18,3) NOT NULL COMMENT '电能用量（kWh）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '登记人',
  `create_time` datetime DEFAULT NULL COMMENT '登记时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_manual_electricity_date_area` (`record_date`, `area_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手工电表登记';

START TRANSACTION;

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '手工电表登记', 2, 7, 'manual-electricity', 'monitor/manualElectricity/index', '', 1, 0, 'C', '0', '0', 'monitor:manualElectricity:list', 'edit', 'admin', NOW(), '', NULL, '手工维护每日各区域电能用量'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'monitor:manualElectricity:list');

SET @manual_electricity_menu_id = (SELECT `menu_id` FROM `sys_menu` WHERE `perms` = 'monitor:manualElectricity:list' LIMIT 1);

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '查询', @manual_electricity_menu_id, 1, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:manualElectricity:query', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'monitor:manualElectricity:query');

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '新增', @manual_electricity_menu_id, 2, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:manualElectricity:add', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'monitor:manualElectricity:add');

INSERT INTO `sys_menu`
  (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
SELECT '编辑', @manual_electricity_menu_id, 3, '', NULL, NULL, 1, 0, 'F', '0', '0', 'monitor:manualElectricity:edit', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `perms` = 'monitor:manualElectricity:edit');

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT `role_id`, @manual_electricity_menu_id FROM `sys_role`
WHERE `role_key` = 'admin' AND `status` = '0' AND `del_flag` = '0';

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.`role_id`, m.`menu_id`
FROM `sys_role` r
JOIN `sys_menu` m ON m.`perms` IN (
  'monitor:manualElectricity:query',
  'monitor:manualElectricity:add',
  'monitor:manualElectricity:edit'
)
WHERE r.`role_key` = 'admin' AND r.`status` = '0' AND r.`del_flag` = '0';

COMMIT;