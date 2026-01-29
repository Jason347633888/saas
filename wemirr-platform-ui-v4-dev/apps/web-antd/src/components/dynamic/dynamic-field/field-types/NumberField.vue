<script setup lang="ts">
/**
 * 数值字段组件
 */
import type { FieldRendererProps } from '../../types';

import { computed } from 'vue';

interface Props extends FieldRendererProps {
  field: {
    fieldName: string;
    label: string;
    pluginType: string;
    required?: boolean;
    disabled?: boolean;
    config?: {
      placeholder?: string;
      min?: number;
      max?: number;
      step?: number;
      precision?: number;
      allowClear?: boolean;
      readonly?: boolean;
      size?: 'small' | 'middle' | 'large';
      stringMode?: boolean;
    };
    validation?: {
      required?: boolean;
      min?: number;
      max?: number;
      message?: string;
    };
  };
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  readonly: false,
});

const emit = defineEmits<{
  (e: 'update:value', value: number | string): void;
  (e: 'change', value: number | string): void;
  (e: 'validate', result: { valid: boolean; message?: string }): void;
}>();

const inputValue = computed({
  get: () => props.value,
  set: (val) => {
    const numVal = typeof val === 'string' ? (val === '' ? undefined : Number(val)) : val;
    emit('update:value', numVal);
    emit('change', numVal);
  },
});

const min = computed(() => props.field.config?.min ?? props.field.validation?.min);
const max = computed(() => props.field.config?.max ?? props.field.validation?.max);
const step = computed(() => props.field.config?.step ?? 1);
const precision = computed(() => props.field.config?.precision ?? undefined);
const stringMode = computed(() => props.field.config?.stringMode ?? false);
const allowClear = computed(() => props.field.config?.allowClear ?? false);
</script>

<template>
  <a-input-number
    v-model:value="inputValue"
    :disabled="disabled || field.disabled"
    :max="max"
    :min="min"
    :placeholder="field.config?.placeholder"
    :precision="precision"
    :readonly="readonly || field.config?.readonly"
    :size="field.config?.size"
    :step="step"
    :string-mode="stringMode"
    :allow-clear="allowClear"
    style="width: 100%"
  />
</template>
