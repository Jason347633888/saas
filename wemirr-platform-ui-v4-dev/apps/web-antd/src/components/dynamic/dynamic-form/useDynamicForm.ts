/**
 * 动态表单逻辑 Hook
 */
import type { DynamicField, DynamicFormSchema, FormState } from './types';
import { getDefaultConfigByPluginType, getFieldDefaultValue, validateField } from '../dynamic-field/validators';

import { reactive, ref, watch } from 'vue';

/**
 * 动态表单选项
 */
interface UseDynamicFormOptions {
  schema: DynamicFormSchema;
  initialValues?: Record<string, any>;
  validateOnChange?: boolean;
  validateOnBlur?: boolean;
}

/**
 * 动态表单返回值
 */
interface UseDynamicFormReturn {
  // 状态
  formState: FormState;
  loading: ref<boolean>;
  submitting: ref<boolean>;

  // 方法
  validate: () => Promise<{ valid: boolean; errors: Record<string, string> }>;
  validateField: (fieldName: string) => { valid: boolean; message?: string };
  resetFields: () => void;
  clearErrors: () => void;
  setFieldsValue: (values: Record<string, any>) => void;
  setFieldValue: (fieldName: string, value: any) => void;
  getValues: () => Record<string, any>;
  submit: () => Promise<void>;

  // 事件
  onValuesChange: (fn: (fieldName: string, value: any) => void) => void;
  onSubmit: (fn: (values: Record<string, any>) => void) => void;
}

/**
 * 动态表单 Hook
 * @param options 表单选项
 * @returns 表单方法和状态
 */
export function useDynamicForm(options: UseDynamicFormOptions): UseDynamicFormReturn {
  const { schema, initialValues = {}, validateOnChange = true, validateOnBlur = true } = options;

  // 表单状态
  const formState = reactive<FormState>({
    values: {},
    errors: {},
    touched: {},
    validating: {},
  });

  // 加载状态
  const loading = ref(false);

  // 提交状态
  const submitting = ref(false);

  // 值变化回调
  const valueChangeCallbacks: Array<(fieldName: string, value: any) => void> = [];

  // 提交回调
  const submitCallbacks: Array<(values: Record<string, any>) => void> = [];

  // 初始化字段值
  function initFieldValues() {
    for (const field of schema.fields) {
      if (!(field.fieldName in formState.values)) {
        formState.values[field.fieldName] =
          initialValues[field.fieldName] ?? getFieldDefaultValue(field);
      }
      formState.errors[field.fieldName] = undefined;
      formState.touched[field.fieldName] = false;
      formState.validating[field.fieldName] = false;
    }
  }

  // 验证单个字段
  function validateField(fieldName: string): { valid: boolean; message?: string } {
    const field = schema.fields.find((f) => f.fieldName === fieldName);
    if (!field) {
      return { valid: true };
    }

    const result = validateField(formState.values[fieldName], field);
    formState.errors[fieldName] = result.message;
    return result;
  }

  // 验证所有字段
  async function validate(): Promise<{ valid: boolean; errors: Record<string, string> }> {
    let isValid = true;
    const errors: Record<string, string> = {};

    for (const field of schema.fields) {
      if (field.hidden) continue;

      const result = validateField(field.fieldName);
      if (!result.valid) {
        isValid = false;
        errors[field.fieldName] = result.message ?? '';
      }
    }

    return { valid: isValid, errors };
  }

  // 重置字段
  function resetFields() {
    for (const field of schema.fields) {
      formState.values[field.fieldName] = getFieldDefaultValue(field);
      formState.errors[field.fieldName] = undefined;
      formState.touched[field.fieldName] = false;
    }
  }

  // 清除错误
  function clearErrors() {
    for (const field of schema.fields) {
      formState.errors[field.fieldName] = undefined;
    }
  }

  // 设置字段值
  function setFieldValue(fieldName: string, value: any) {
    formState.values[fieldName] = value;

    // 触发值变化回调
    for (const callback of valueChangeCallbacks) {
      callback(fieldName, value);
    }

    // 验证
    if (validateOnChange) {
      validateField(fieldName);
    }
  }

  // 设置多个字段值
  function setFieldsValue(values: Record<string, any>) {
    for (const [key, value] of Object.entries(values)) {
      if (key in formState.values) {
        setFieldValue(key, value);
      }
    }
  }

  // 获取表单值
  function getValues(): Record<string, any> {
    return { ...formState.values };
  }

  // 提交表单
  async function submit() {
    submitting.value = true;
    try {
      const result = await validate();

      if (result.valid) {
        // 触发提交回调
        for (const callback of submitCallbacks) {
          callback(formState.values);
        }
      }
    } finally {
      submitting.value = false;
    }
  }

  // 标记字段已触摸
  function markFieldTouched(fieldName: string) {
    formState.touched[fieldName] = true;
  }

  // 注册值变化回调
  function onValuesChange(fn: (fieldName: string, value: any) => void) {
    valueChangeCallbacks.push(fn);
  }

  // 注册提交回调
  function onSubmit(fn: (values: Record<string, any>) => void) {
    submitCallbacks.push(fn);
  }

  // 监听值变化
  watch(
    () => formState.values,
    () => {
      // 值变化时的处理
    },
    { deep: true }
  );

  // 初始化
  initFieldValues();

  return {
    formState,
    loading,
    submitting,
    validate,
    validateField,
    resetFields,
    clearErrors,
    setFieldsValue,
    setFieldValue,
    getValues,
    submit,
    onValuesChange,
    onSubmit,
  };
}

/**
 * 插件注册 Hook
 * 管理插件的注册和注销
 */
export function usePluginRegistry() {
  const plugins = reactive<Record<string, DynamicField>>({});

  // 注册插件
  function registerPlugin(plugin: DynamicField) {
    plugins[plugin.pluginType] = plugin;
  }

  // 注销插件
  function unregisterPlugin(pluginType: string) {
    delete plugins[pluginType];
  }

  // 获取插件
  function getPlugin(pluginType: string): DynamicField | undefined {
    return plugins[pluginType];
  }

  // 获取所有插件
  function getAllPlugins(): DynamicField[] {
    return Object.values(plugins);
  }

  return {
    plugins,
    registerPlugin,
    unregisterPlugin,
    getPlugin,
    getAllPlugins,
  };
}
