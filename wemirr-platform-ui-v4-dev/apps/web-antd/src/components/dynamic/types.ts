/**
 * 动态表单类型定义
 */

import type { FieldPluginConfig, FieldValidationRule } from '@/api/dynamic';

/**
 * 动态字段定义
 */
export interface DynamicField {
  /** 字段名称 */
  fieldName: string;
  /** 字段标签 */
  label: string;
  /** 插件类型 */
  pluginType: string;
  /** 是否必填 */
  required?: boolean;
  /** 是否禁用 */
  disabled?: boolean;
  /** 是否隐藏 */
  hidden?: boolean;
  /** 插件配置 */
  config?: FieldPluginConfig;
  /** 验证规则 */
  validation?: FieldValidationRule;
  /** 栅格布局配置 */
  colProps?: Record<string, any>;
  /** 组件额外属性 */
  componentProps?: Record<string, any>;
  /** 标签自定义渲染 */
  labelRender?: (value: any, field: DynamicField) => any;
  /** 插槽名称 */
  slotName?: string;
}

/**
 * 动态表单 Schema
 */
export interface DynamicFormSchema {
  /** 字段列表 */
  fields: DynamicField[];
  /** 布局方式 */
  layout?: 'horizontal' | 'vertical' | 'inline';
  /** 标签栅格配置 */
  labelCol?: Record<string, any>;
  /** 内容栅格配置 */
  wrapperCol?: Record<string, any>;
  /** 提交按钮配置 */
  submitButton?: boolean | { text?: string; loading?: boolean };
  /** 重置按钮配置 */
  resetButton?: boolean | { text?: string };
  /** 表单样式 */
  formStyle?: Record<string, any>;
}

/**
 * 表单值变化事件
 */
export interface FormValueChangeEvent {
  fieldName: string;
  oldValue: any;
  newValue: any;
  field: DynamicField;
}

/**
 * 表单提交事件
 */
export interface FormSubmitEvent {
  values: Record<string, any>;
  errors: Record<string, any>;
}

/**
 * 字段渲染器属性
 */
export interface FieldRendererProps {
  field: DynamicField;
  value: any;
  onChange: (value: any) => void;
  onBlur?: () => void;
  disabled?: boolean;
  readonly?: boolean;
}

/**
 * 插件组件映射
 */
export interface PluginComponentMap {
  [pluginType: string]: {
    name: string;
    component: any;
    defaultConfig?: FieldPluginConfig;
  };
}

/**
 * 表单状态
 */
export interface FormState {
  values: Record<string, any>;
  errors: Record<string, any>;
  touched: Record<string, boolean>;
  validating: Record<string, boolean>;
}
