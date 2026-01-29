/**
 * 字段混合逻辑
 * 提供字段的通用功能和状态管理
 */
import type { DynamicField, FormState } from '../types';
import { getFieldDefaultValue, validateField } from '../validators';

import { reactive, ref, watch } from 'vue';

/**
 * 字段混合器选项
 */
interface FieldMixinOptions {
  field: DynamicField;
  initialValue?: any;
  immediateValidate?: boolean;
}

/**
 * 字段混合器返回值
 */
interface FieldMixinReturn {
  value: ref<any>;
  error: ref<string | undefined>;
  touched: ref<boolean>;
  disabled: ref<boolean>;
  readonly: ref<boolean>;
  validate: () => { valid: boolean; message?: string };
  reset: () => void;
  setValue: (val: any) => void;
  setDisabled: (val: boolean) => void;
  setReadonly: (val: boolean) => void;
}

/**
 * 字段混合器
 * 为字段提供统一的状态管理和验证功能
 * @param options 混合器选项
 * @returns 字段状态和操作方法
 */
export function useFieldMixin(options: FieldMixinOptions): FieldMixinReturn {
  const { field, initialValue, immediateValidate = false } = options;

  // 字段值
  const value = ref(initialValue ?? getFieldDefaultValue(field));

  // 错误信息
  const error = ref<string | undefined>();

  // 是否已触摸
  const touched = ref(false);

  // 禁用状态
  const disabled = ref(field.disabled ?? false);

  // 只读状态
  const readonly = ref(false);

  // 验证字段
  function validate(): { valid: boolean; message?: string } {
    const result = validateField(value.value, field);
    error.value = result.message;
    return result;
  }

  // 重置字段
  function reset() {
    value.value = getFieldDefaultValue(field);
    error.value = undefined;
    touched.value = false;
  }

  // 设置值
  function setValue(val: any) {
    value.value = val;
    if (immediateValidate) {
      validate();
    }
  }

  // 设置禁用状态
  function setDisabled(val: boolean) {
    disabled.value = val;
  }

  // 设置只读状态
  function setReadonly(val: boolean) {
    readonly.value = val;
  }

  // 监听值变化，触摸时验证
  watch(value, () => {
    if (touched.value) {
      validate();
    }
  });

  return {
    value,
    error,
    touched,
    disabled,
    readonly,
    validate,
    reset,
    setValue,
    setDisabled,
    setReadonly,
  };
}

/**
 * 表单字段混合器
 * 管理表单中所有字段的状态
 * @param schema 表单 Schema
 * @returns 表单状态和操作方法
 */
export function useFormFieldMixin(schema: { fields: DynamicField[] }) {
  // 表单状态
  const formState = reactive<FormState>({
    values: {},
    errors: {},
    touched: {},
    validating: {},
  });

  // 初始化字段状态
  function initFields() {
    for (const field of schema.fields) {
      formState.values[field.fieldName] = getFieldDefaultValue(field);
      formState.errors[field.fieldName] = undefined;
      formState.touched[field.fieldName] = false;
      formState.validating[field.fieldName] = false;
    }
  }

  // 验证所有字段
  function validateAll(): { valid: boolean; errors: Record<string, string> } {
    let isValid = true;
    const errors: Record<string, string> = {};

    for (const field of schema.fields) {
      if (field.hidden) continue;

      const result = validateField(formState.values[field.fieldName], field);
      formState.errors[field.fieldName] = result.message;

      if (!result.valid) {
        isValid = false;
        errors[field.fieldName] = result.message ?? '';
      }
    }

    return { valid: isValid, errors };
  }

  // 重置所有字段
  function resetAll() {
    for (const field of schema.fields) {
      formState.values[field.fieldName] = getFieldDefaultValue(field);
      formState.errors[field.fieldName] = undefined;
      formState.touched[field.fieldName] = false;
    }
  }

  // 获取表单数据
  function getValues(): Record<string, any> {
    return { ...formState.values };
  }

  // 设置表单数据
  function setValues(values: Record<string, any>) {
    for (const field of schema.fields) {
      if (field.fieldName in values) {
        formState.values[field.fieldName] = values[field.fieldName];
      }
    }
  }

  // 初始化
  initFields();

  return {
    formState,
    initFields,
    validateAll,
    resetAll,
    getValues,
    setValues,
  };
}
