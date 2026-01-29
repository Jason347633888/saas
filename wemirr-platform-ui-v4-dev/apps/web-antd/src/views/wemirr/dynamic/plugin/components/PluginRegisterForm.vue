<script setup lang="ts">
/**
 * 插件注册表单组件
 */
import type { PluginRegisterDTO } from '@/api/dynamic';

import { reactive, ref } from 'vue';

import { validatePluginValue } from '@/api/dynamic';

interface Props {
  initialData?: Partial<PluginRegisterDTO>;
  mode?: 'create' | 'edit';
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'create',
});

const emit = defineEmits<{
  (e: 'submit', data: PluginRegisterDTO): void;
  (e: 'cancel'): void;
}>();

// 表单数据
const formData = reactive<PluginRegisterDTO>({
  type: props.initialData?.type || '',
  name: props.initialData?.name || '',
  version: props.initialData?.version || '1.0.0',
  description: props.initialData?.description || '',
  category: props.initialData?.category || '',
  schema: props.initialData?.schema || { properties: {} },
  config: props.initialData?.config || {},
  validation: props.initialData?.validation || {},
  props: props.initialData?.props || {},
});

// 表单 ref
const formRef = ref();

// 加载状态
const loading = ref(false);

// 验证规则
const rules = {
  type: [
    { required: true, message: '请输入插件类型', trigger: 'blur' },
    { pattern: /^[a-z][a-z0-9_]*$/, message: '类型只能包含小写字母、数字和下划线', trigger: 'blur' },
  ],
  name: [
    { required: true, message: '请输入插件名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度应在2-50个字符之间', trigger: 'blur' },
  ],
  version: [
    { required: true, message: '请输入版本号', trigger: 'blur' },
    { pattern: /^\d+\.\d+\.\d+$/, message: '版本号格式应为 x.y.z', trigger: 'blur' },
  ],
};

// Schema 编辑器展开
const schemaEditorVisible = ref(false);

// 验证表单
async function validateForm(): Promise<boolean> {
  try {
    await formRef.value?.validate();
    return true;
  } catch {
    return false;
  }
}

// 验证插件值
async function validateValue() {
  if (!formData.type) {
    return { valid: false, message: '请先填写插件类型' };
  }

  try {
    const result = await validatePluginValue({
      type: formData.type,
      value: {},
    });
    return result;
  } catch {
    return { valid: false, message: '验证失败' };
  }
}

// 提交表单
async function handleSubmit() {
  loading.value = true;
  try {
    const isValid = await validateForm();
    if (isValid) {
      emit('submit', { ...formData });
    }
  } finally {
    loading.value = false;
  }
}

// 取消
function handleCancel() {
  emit('cancel');
}

// 重置表单
function handleReset() {
  formData.type = '';
  formData.name = '';
  formData.version = '1.0.0';
  formData.description = '';
  formData.category = '';
  formData.schema = { properties: {} };
  formData.config = {};
  formData.validation = {};
  formData.props = {};
}
</script>

<template>
  <a-form
    ref="formRef"
    :model="formData"
    :rules="rules"
    layout="vertical"
  >
    <a-row :gutter="16">
      <a-col :span="12">
        <a-form-item label="插件类型" name="type" required>
          <a-input
            v-model:value="formData.type"
            placeholder="小写字母、数字和下划线"
            :disabled="mode === 'edit'"
          />
        </a-form-item>
      </a-col>
      <a-col :span="12">
        <a-form-item label="插件名称" name="name" required>
          <a-input
            v-model:value="formData.name"
            placeholder="请输入插件名称"
          />
        </a-form-item>
      </a-col>
    </a-row>

    <a-row :gutter="16">
      <a-col :span="12">
        <a-form-item label="版本号" name="version" required>
          <a-input
            v-model:value="formData.version"
            placeholder="x.y.z 格式"
          />
        </a-form-item>
      </a-col>
      <a-col :span="12">
        <a-form-item label="分类">
          <a-select
            v-model:value="formData.category"
            placeholder="请选择分类"
            allow-clear
          >
            <a-select-option value="basic">基础字段</a-select-option>
            <a-select-option value="advanced">高级字段</a-select-option>
            <a-select-option value="layout">布局字段</a-select-option>
            <a-select-option value="custom">自定义字段</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
    </a-row>

    <a-form-item label="描述">
      <a-textarea
        v-model:value="formData.description"
        placeholder="请输入插件描述"
        :rows="3"
        :maxlength="200"
        show-count
      />
    </a-form-item>

    <a-collapse>
      <a-collapse-panel key="1" header="Schema 配置">
        <code-editor
          v-model:value="JSON.stringify(formData.schema, null, 2)"
          language="json"
          height="200px"
        />
      </a-collapse-panel>
      <a-collapse-panel key="2" header="验证规则">
        <a-form-item label="必填">
          <a-switch v-model:checked="(formData.validation as any).required" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="最小值/最小长度">
              <a-input-number v-model:value="(formData.validation as any).min" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="最大值/最大长度">
              <a-input-number v-model:value="(formData.validation as any).max" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="正则表达式">
          <a-input
            v-model:value="(formData.validation as any).pattern"
            placeholder="请输入正则表达式"
          />
        </a-form-item>
      </a-collapse-panel>
      <a-collapse-panel key="3" title="其他配置">
        <a-form-item label="默认配置">
          <code-editor
            v-model:value="JSON.stringify(formData.config, null, 2)"
            language="json"
            height="150px"
          />
        </a-form-item>
      </a-collapse-panel>
    </a-collapse>

    <a-form-item style="margin-top: 16px; margin-bottom: 0">
      <a-space>
        <a-button type="primary" :loading="loading" @click="handleSubmit">
          {{ mode === 'create' ? '注册' : '保存' }}
        </a-button>
        <a-button @click="handleReset">重置</a-button>
        <a-button @click="handleCancel">取消</a-button>
      </a-space>
    </a-form-item>
  </a-form>
</template>
