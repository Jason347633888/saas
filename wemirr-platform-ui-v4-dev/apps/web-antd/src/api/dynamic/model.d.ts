/**
 * 动态表单插件类型定义
 */

import type { BaseEntity, PageQuery, PageResult } from '#/api/common';

/**
 * 字段插件类型枚举
 */
export enum FieldPluginType {
  TEXT = 'text',
  NUMBER = 'number',
  SELECT = 'select',
  MULTI_SELECT = 'multiSelect',
  DATE = 'date',
  DATETIME = 'datetime',
  TEXTAREA = 'textarea',
  SWITCH = 'switch',
  UPLOAD = 'upload',
  CUSTOM = 'custom',
}

/**
 * 字段插件验证规则
 */
export interface FieldValidationRule {
  required?: boolean;
  min?: number;
  max?: number;
  pattern?: string;
  message?: string;
  customValidator?: string;
}

/**
 * 字段插件 Schema 定义
 */
export interface FieldPluginSchema {
  properties: Record<string, any>;
  required?: string[];
}

/**
 * 字段插件配置
 */
export interface FieldPluginConfig {
  defaultValue?: any;
  placeholder?: string;
  disabled?: boolean;
  hidden?: boolean;
  clearable?: boolean;
  readonly?: boolean;
  size?: 'small' | 'middle' | 'large';
}

/**
 * 字段插件元数据
 */
export interface FieldPluginVO extends BaseEntity {
  id: string;
  type: string;
  name: string;
  version: string;
  description?: string;
  category?: string;
  author?: string;
  icon?: string;
  schema: FieldPluginSchema;
  config: FieldPluginConfig;
  validation?: FieldValidationRule;
  props?: Record<string, any>;
  status?: 'enabled' | 'disabled';
}

/**
 * 插件分页查询参数
 */
export interface PluginPageQuery extends PageQuery {
  type?: string;
  name?: string;
  category?: string;
  status?: string;
}

/**
 * 插件分页结果
 */
export interface PluginPageResult extends PageResult<FieldPluginVO> {}

/**
 * 插件注册参数
 */
export interface PluginRegisterDTO {
  type: string;
  name: string;
  version: string;
  description?: string;
  category?: string;
  schema: FieldPluginSchema;
  config?: FieldPluginConfig;
  validation?: FieldValidationRule;
  props?: Record<string, any>;
}

/**
 * 插件值验证请求
 */
export interface PluginValidateDTO {
  type: string;
  value: any;
  context?: Record<string, any>;
}

/**
 * 插件值验证响应
 */
export interface PluginValidateVO {
  valid: boolean;
  message?: string;
  errors?: Array<{ field: string; message: string }>;
}

/**
 * 插件值处理请求
 */
export interface PluginProcessDTO {
  type: string;
  value: any;
  direction: 'input' | 'output';
  context?: Record<string, any>;
}

/**
 * 插件值处理响应
 */
export interface PluginProcessVO {
  value: any;
  meta?: Record<string, any>;
}

/**
 * 插件字段定义
 */
export interface DynamicField {
  fieldName: string;
  label: string;
  pluginType: string;
  required?: boolean;
  disabled?: boolean;
  hidden?: boolean;
  config?: FieldPluginConfig;
  validation?: FieldValidationRule;
  colProps?: Record<string, any>;
  componentProps?: Record<string, any>;
}

/**
 * 动态表单 Schema
 */
export interface DynamicFormSchema {
  fields: DynamicField[];
  layout?: 'horizontal' | 'vertical' | 'inline';
  labelCol?: Record<string, any>;
  wrapperCol?: Record<string, any>;
  submitButton?: boolean | Record<string, any>;
  resetButton?: boolean | Record<string, any>;
}
