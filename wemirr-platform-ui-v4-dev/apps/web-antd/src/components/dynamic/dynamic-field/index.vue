<script setup lang="ts">
/**
 * 动态字段包装组件
 * 提供字段的标签、错误提示、栅格布局等功能
 */
import type { DynamicField } from '../types';

import { computed } from 'vue';

import FieldRenderer from './FieldRenderer.vue';

interface Props {
  field: DynamicField;
  value: any;
  labelCol?: Record<string, any>;
  wrapperCol?: Record<string, any>;
  showLabel?: boolean;
  required?: boolean;
  disabled?: boolean;
  readonly?: boolean;
  error?: string;
  touched?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  showLabel: true,
  required: false,
  disabled: false,
  readonly: false,
  touched: false,
});

const emit = defineEmits<{
  (e: 'update:value', value: any): void;
  (e: 'change', value: any, field: DynamicField): void;
  (e: 'blur'): void;
  (e: 'validate', result: { valid: boolean; message?: string }): void;
}>();

// 是否显示错误提示
const showError = computed(() => {
  return props.touched && props.error;
});

// 标签
const label = computed(() => {
  return props.showLabel ? props.field.label : undefined;
});

// 处理值变化
function handleChange(value: any) {
  emit('update:value', value);
  emit('change', value, props.field);
}

// 处理验证结果
function handleValidate(result: { valid: boolean; message?: string }) {
  emit('validate', result);
}

// 处理失焦
function handleBlur() {
  emit('blur');
}
</script>

<template>
  <a-form-item
    :label="label"
    :required="required || field.required"
    :help="showError ? error : undefined"
    :validate-status="showError ? 'error' : undefined"
    :label-col="labelCol"
    :wrapper-col="wrapperCol"
  >
    <FieldRenderer
      :field="field"
      :value="value"
      :disabled="disabled || field.disabled"
      :readonly="readonly"
      @update:value="handleChange"
      @change="handleChange"
      @validate="handleValidate"
      @blur="handleBlur"
    />
  </a-form-item>
</template>
