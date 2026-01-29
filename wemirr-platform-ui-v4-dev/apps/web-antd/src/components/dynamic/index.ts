/**
 * 动态表单模块统一导出
 */

// 动态表单
export { default as DynamicForm } from './dynamic-form/index.vue';
export { useDynamicForm, usePluginRegistry } from './dynamic-form/useDynamicForm';
export * from './dynamic-form/types';

// 动态字段
export { default as DynamicField } from './dynamic-field/index.vue';
export { default as FieldRenderer } from './dynamic-field/FieldRenderer.vue';
export * from './dynamic-field/types';
export * from './dynamic-field/validators';
