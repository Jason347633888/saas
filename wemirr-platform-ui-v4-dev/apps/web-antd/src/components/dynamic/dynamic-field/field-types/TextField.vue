<script setup lang="ts">
/**
 * 文本字段组件
 */
import type { FieldRendererProps } from '../../types';

import { computed } from 'vue';

import { validateField } from '../validators';

interface Props extends FieldRendererProps {
  field: {
    fieldName: string;
    label: string;
    pluginType: string;
    required?: boolean;
    disabled?: boolean;
    config?: {
      placeholder?: string;
      maxLength?: number;
      minLength?: number;
      showCount?: boolean;
      allowClear?: boolean;
      readonly?: boolean;
      size?: 'small' | 'middle' | 'large';
    };
    validation?: {
      required?: boolean;
      minLength?: number;
      maxLength?: number;
      pattern?: string;
      message?: string;
    };
  };
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  readonly: false,
});

const emit = defineEmits<{
  (e: 'update:value', value: string): void;
  (e: 'change', value: string): void;
  (e: 'validate', result: { valid: boolean; message?: string }): void;
}>();

const inputValue = computed({
  get: () => props.value ?? '',
  set: (val) => {
    emit('update:value', val);
    emit('change', val);

    // Validate on change
    const validation = validateField(val, props.field);
    emit('validate', validation);
  },
});

const maxLength = computed(() => props.field.config?.maxLength ?? props.field.validation?.maxLength);
const showCount = computed(() => props.field.config?.showCount ?? false);
const allowClear = computed(() => props.field.config?.allowClear ?? true);
</script>

<template>
  <a-input
    v-model:value="inputValue"
    :disabled="disabled || field.disabled"
    :maxlength="maxLength"
    :placeholder="field.config?.placeholder"
    :readonly="readonly || field.config?.readonly"
    :show-count="showCount"
    :size="field.config?.size"
    :allow-clear="allowClear"
  />
</template>
