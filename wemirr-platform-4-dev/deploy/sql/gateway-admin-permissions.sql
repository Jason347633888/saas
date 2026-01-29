-- Gateway Admin 权限配置 SQL
-- 执行方式: 在数据库中执行此 SQL
-- 说明: 为网关管理功能添加权限记录

-- 1. 添加网关管理菜单（归属开发平台50）
-- 网关路由管理
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (500501, 'pc-web', '路由规则', 'gateway:route:list', 5005, '/dev/gateway/routes', '/wemirr/gateway/routes/index', 500501, 'ant-design:gateway-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

-- 网关黑名单管理
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (500502, 'pc-web', '黑名单管理', 'gateway:blacklist:list', 5005, '/dev/gateway/blacklist', '/wemirr/gateway/blacklist/index', 500502, 'carbon:blocking-logic', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

-- 网关限流管理
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (500503, 'pc-web', '限流管理', 'gateway:limit:list', 5005, '/dev/gateway/limits', '/wemirr/gateway/limits/index', 500503, 'ant-design:thunderbolt-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

-- 服务发现
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (500504, 'pc-web', '服务发现', 'gateway:discovery:list', 5005, '/dev/gateway/discoveries', '/wemirr/gateway/discoveries/index', 500504, 'ant-design:cloud-server-outlined', 'menu', b'1', b'1', b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

-- 2. 添加按钮权限（挂载到对应的菜单下）

-- 路由规则按钮
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050101, 'pc-web', '新增路由', 'gateway:route:create', 500501, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050102, 'pc-web', '修改路由', 'gateway:route:modify', 500501, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050103, 'pc-web', '发布路由', 'gateway:route:publish', 500501, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050104, 'pc-web', '删除路由', 'gateway:route:remove', 500501, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

-- 黑名单管理按钮
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050201, 'pc-web', '新增黑名单', 'gateway:blacklist:create', 500502, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050202, 'pc-web', '修改黑名单', 'gateway:blacklist:modify', 500502, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050203, 'pc-web', '删除黑名单', 'gateway:blacklist:remove', 500502, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

-- 限流管理按钮
INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050301, 'pc-web', '新增限流', 'gateway:limit:create', 500503, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050302, 'pc-web', '修改限流', 'gateway:limit:modify', 500503, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

INSERT INTO `sys_resource` (`id`, `client_id`, `title`, `permission`, `parent_id`, `path`, `component`, `sequence`, `icon`, `type`, `status`, `keep_alive`, `readonly`, `shared`, `visible`, `meta`, `description`, `deleted`, `create_by`, `create_name`, `create_time`, `last_modify_by`, `last_modify_name`, `last_modify_time`)
VALUES (50050303, 'pc-web', '删除限流', 'gateway:limit:remove', 500503, NULL, NULL, 0, NULL, 'button', b'1', NULL, b'0', b'0', b'1', NULL, NULL, b'0', 1, '平台管理员', NOW(), NULL, NULL, NULL);

SELECT '网关管理权限配置完成!' AS result;
