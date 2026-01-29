/**
 * 字段验证器
 */
import type { FieldValidationRule } from '@/api/dynamic';
import type { DynamicField } from '../types';

/**
 * 验证结果
 */
export interface ValidationResult {
  valid: boolean;
  message?: string;
  errors?: Array<{ field: string; message: string }>;
}

/**
 * 验证字段值
 * @param value 字段值
 * @param field 字段定义
 * @returns 验证结果
 */
export function validateField(value: any, field: DynamicField): ValidationResult {
  const validation = field.validation;
  const errors: Array<{ field: string; message: string }> = [];

  if (!validation) {
    return { valid: true };
  }

  // Required validation
  if (validation.required) {
    const isEmpty =
      value === undefined ||
      value === null ||
      value === '' ||
      (Array.isArray(value) && value.length === 0);

    if (isEmpty) {
      errors.push({
        field: field.fieldName,
        message: validation.message || `${field.label}不能为空`,
      });
    }
  }

  // Min/Max for numbers
  if (typeof value === 'number') {
    if (validation.min !== undefined && value < validation.min) {
      errors.push({
        field: field.fieldName,
        message: validation.message || `${field.label}不能小于${validation.min}`,
      });
    }
    if (validation.max !== undefined && value > validation.max) {
      errors.push({
        field: field.fieldName,
        message: validation.message || `${field.label}不能大于${validation.max}`,
      });
    }
  }

  // String length validation
  if (typeof value === 'string') {
    const minLength = (validation as any).minLength;
    const maxLength = (validation as any).maxLength;

    if (minLength !== undefined && value.length < minLength) {
      errors.push({
        field: field.fieldName,
        message: validation.message || `${field.label}长度不能小于${minLength}`,
      });
    }
    if (maxLength !== undefined && value.length > maxLength) {
      errors.push({
        field: field.fieldName,
        message: validation.message || `${field.label}长度不能大于${maxLength}`,
      });
    }
  }

  // Pattern validation
  if (validation.pattern && typeof value === 'string') {
    try {
      const regex = new RegExp(validation.pattern);
      if (!regex.test(value)) {
        errors.push({
          field: field.fieldName,
          message: validation.message || `${field.label}格式不正确`,
        });
      }
    } catch {
      // Invalid regex, skip pattern validation
    }
  }

  return {
    valid: errors.length === 0,
    message: errors.length > 0 ? errors[0].message : undefined,
    errors,
  };
}

/**
 * 验证整个表单
 * @param values 表单值
 * @param fields 字段列表
 * @returns 验证结果
 */
export function validateForm(
  values: Record<string, any>,
  fields: DynamicField[]
): ValidationResult {
  const allErrors: Array<{ field: string; message: string }> = [];

  for (const field of fields) {
    if (field.hidden) {
      continue;
    }

    const result = validateField(values[field.fieldName], field);
    if (!result.valid && result.errors) {
      allErrors.push(...result.errors);
    }
  }

  return {
    valid: allErrors.length === 0,
    errors: allErrors,
  };
}

/**
 * 获取字段的默认值
 * @param field 字段定义
 * @returns 默认值
 */
export function getFieldDefaultValue(field: DynamicField): any {
  return field.config?.defaultValue ?? undefined;
}

/**
 * 根据插件类型获取默认组件配置
 * @param pluginType 插件类型
 * @returns 默认配置
 */
export function getDefaultConfigByPluginType(pluginType: string): Record<string, any> {
  const configMap: Record<string, Record<string, any>> = {
    text: {
      placeholder: '请输入',
      allowClear: true,
      showCount: false,
    },
    number: {
      placeholder: '请输入',
      step: 1,
      allowClear: false,
    },
    select: {
      placeholder: '请选择',
      allowClear: true,
    },
    multiSelect: {
      placeholder: '请选择',
      mode: 'multiple',
    },
    date: {
      placeholder: '请选择日期',
      format: 'YYYY-MM-DD',
    },
    datetime: {
      placeholder: '请选择日期时间',
      format: 'YYYY-MM-DD HH:mm:ss',
      showTime: true,
    },
    textarea: {
      placeholder: '请输入',
      allowClear: true,
      autoSize: { minRows: 2, maxRows: 4 },
    },
    switch: {
      checkedChildren: '是',
      unCheckedChildren: '否',
    },
    upload: {
      listType: 'picture-card',
      maxCount: 1,
    },
  };

  return configMap[pluginType] || {};
}
