<script setup lang="ts">
/**
 * 动态表单入口组件
 * 根据 Schema 动态渲染表单字段
 */
import type { DynamicFormSchema } from './types';
import { useDynamicForm } from './useDynamicForm';

import { computed } from 'vue';

import DynamicField from '../dynamic-field/index.vue';

interface Props {
  schema: DynamicFormSchema;
  initialValues?: Record<string, any>;
  disabled?: boolean;
  readonly?: boolean;
  labelCol?: Record<string, any>;
  wrapperCol?: Record<string, any>;
}

const props = withDefaults(defineProps<Props>(), {
  initialValues: () => ({}),
  disabled: false,
  readonly: false,
});

const emit = defineEmits<{
  (e: 'update:values', values: Record<string, any>): void;
  (e: 'change', fieldName: string, value: any): void;
  (e: 'submit', values: Record<string, any>): void;
  (e: 'validate', result: { valid: boolean; errors: Record<string, string> }): void;
}>();

// 使用动态表单 Hook
const { formState, validate, resetFields, setFieldsValue, getValues, submit } =
  useDynamicForm({
    schema: props.schema,
    initialValues: props.initialValues,
    validateOnChange: true,
  });

// 布局方式
const layout = computed(() => props.schema.layout ?? 'horizontal');

// 提交按钮配置
const showSubmitButton = computed(() => {
  return props.schema.submitButton !== false;
});

const submitButtonText = computed(() => {
  const btnConfig = props.schema.submitButton;
  if (typeof btnConfig === 'object') {
    return btnConfig.text ?? '提交';
  }
  return '提交';
});

const submitButtonLoading = computed(() => {
  const btnConfig = props.schema.submitButton;
  if (typeof btnConfig === 'object') {
    return btnConfig.loading ?? false;
  }
  return false;
});

// 重置按钮配置
const showResetButton = computed(() => {
  return props.schema.resetButton !== false;
});

const resetButtonText = computed(() => {
  const btnConfig = props.schema.resetButton;
  if (typeof btnConfig === 'object') {
    return btnConfig.text ?? '重置';
  }
  return '重置';
});

// 处理字段值变化
function handleFieldChange(fieldName: string, value: any) {
  emit('update:values', formState.values);
  emit('change', fieldName, value);
}

// 处理表单提交
async function handleSubmit() {
  const result = await validate();
  emit('validate', result);

  if (result.valid) {
    const values = getValues();
    emit('submit', values);
  }
}

// 处理重置
function handleReset() {
  resetFields();
  emit('update:values', formState.values);
}

// 标签栅格配置
const labelCol = computed(() => {
  return props.labelCol ?? props.schema.labelCol ?? { span: 4 };
});

// 内容栅格配置
const wrapperCol = computed(() => {
  return props.wrapperCol ?? props.schema.wrapperCol ?? { span: 20 };
});
</script>

<template>
  <a-form
    :layout="layout"
    :label-col="labelCol"
    :wrapper-col="wrapperCol"
    @finish="handleSubmit"
  >
    <!-- 动态字段列表 -->
    <template v-for="field in schema.fields" :key="field.fieldName">
      <a-form-item
        v-if="!field.hidden"
        :label="field.label"
        :name="field.fieldName"
        :required="field.required"
        :help="formState.errors[field.fieldName]"
        :validate-status="
          formState.errors[field.fieldName] && formState.touched[field.fieldName]
            ? 'error'
            : ''
        "
        :label-col="field.colProps?.labelCol"
        :wrapper-col="field.colProps?.wrapperCol"
      >
        <DynamicField
          :field="field"
          :value="formState.values[field.fieldName]"
          :disabled="disabled || field.disabled"
          :readonly="readonly"
          :touched="formState.touched[field.fieldName]"
          :error="formState.errors[field.fieldName]"
          :label-col="field.colProps?.labelCol"
          :wrapper-col="field.colProps?.wrapperCol"
          @update:value="(val) => setFieldValue(field.fieldName, val)"
          @change="(val) => handleFieldChange(field.fieldName, val)"
        />
      </a-form-item>
    </template>

    <!-- 操作按钮 -->
    <a-form-item :wrapper-col="{ offset: labelCol?.span ?? 4 }">
      <a-space>
        <a-button
          v-if="showSubmitButton"
          type="primary"
          html-type="submit"
          :loading="submitButtonLoading"
        >
          {{ submitButtonText }}
        </a-button>
        <a-button v-if="showResetButton" @click="handleReset">
          {{ resetButtonText }}
        </a-button>
      </a-space>
    </a-form-item>
  </a-form>
</template>
