<script setup lang="ts">
/**
 * 字段渲染器
 * 根据插件类型动态渲染对应的字段组件
 */
import type { DynamicField } from '../types';

import { computed, shallowRef } from 'vue';

import TextField from './field-types/TextField.vue';
import NumberField from './field-types/NumberField.vue';

interface Props {
  field: DynamicField;
  value: any;
  disabled?: boolean;
  readonly?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  readonly: false,
});

const emit = defineEmits<{
  (e: 'update:value', value: any): void;
  (e: 'change', value: any, field: DynamicField): void;
  (e: 'validate', result: { valid: boolean; message?: string }): void;
}>();

// 插件组件映射
const componentMap: Record<string, any> = {
  text: TextField,
  number: NumberField,
};

// 动态获取组件
const component = computed(() => {
  return componentMap[props.field.pluginType] || TextField;
});

// 监听值变化
function handleChange(value: any) {
  emit('update:value', value);
  emit('change', value, props.field);
}

// 监听验证结果
function handleValidate(result: { valid: boolean; message?: string }) {
  emit('validate', result);
}
</script>

<template>
  <component
    :is="component"
    :field="field"
    :value="value"
    :disabled="disabled"
    :readonly="readonly"
    @update:value="handleChange"
    @change="(val) => emit('change', val, field)"
    @validate="handleValidate"
  />
</template>
