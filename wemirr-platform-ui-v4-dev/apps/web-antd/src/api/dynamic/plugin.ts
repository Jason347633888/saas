/**
 * 动态表单插件 API 接口
 */
import type {
  FieldPluginVO,
  PluginPageQuery,
  PluginPageResult,
  PluginProcessDTO,
  PluginProcessVO,
  PluginRegisterDTO,
  PluginValidateDTO,
  PluginValidateVO,
} from './model';

import { requestClient } from '#/api/request';

enum Api {
  root = '/dynamic/plugins',
  pluginDetail = '/dynamic/plugins/{type}',
  pluginValidate = '/dynamic/plugins/validate',
  pluginProcess = '/dynamic/plugins/process',
  pluginRegister = '/dynamic/plugins/register',
  pluginUnregister = '/dynamic/plugins/{type}',
}

/**
 * 获取插件列表
 * @param params 查询参数
 * @returns FieldPluginVO[]
 */
export function getPluginList(params?: PluginPageQuery) {
  return requestClient.get<FieldPluginVO[] | PluginPageResult>(
    Api.root,
    { params },
  );
}

/**
 * 获取插件详情
 * @param type 插件类型
 * @returns FieldPluginVO
 */
export function getPluginDetail(type: string) {
  return requestClient.get<FieldPluginVO>(Api.pluginDetail.replace('{type}', type));
}

/**
 * 验证插件值
 * @param data 验证数据
 * @returns PluginValidateVO
 */
export function validatePluginValue(data: PluginValidateDTO) {
  return requestClient.post<PluginValidateVO>(Api.pluginValidate, data);
}

/**
 * 处理插件值
 * @param data 处理数据
 * @returns PluginProcessVO
 */
export function processPluginValue(data: PluginProcessDTO) {
  return requestClient.post<PluginProcessVO>(Api.pluginProcess, data);
}

/**
 * 注册插件
 * @param data 插件数据
 * @returns void
 */
export function registerPlugin(data: PluginRegisterDTO) {
  return requestClient.post(Api.pluginRegister, data);
}

/**
 * 注销插件
 * @param type 插件类型
 * @returns void
 */
export function unregisterPlugin(type: string) {
  return requestClient.delete(Api.pluginUnregister.replace('{type}', type));
}
