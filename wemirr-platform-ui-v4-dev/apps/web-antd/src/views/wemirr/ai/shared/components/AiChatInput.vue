<script setup lang="ts">
/**
 * AI 对话输入组件
 * 通用的消息输入框，支持模型选择、深度思考、联网搜索等选项
 */
import { GlobalOutlined, ThunderboltOutlined } from '@ant-design/icons-vue';
import { Select } from 'ant-design-vue';
import { Sender } from 'ant-design-x-vue';

// ==================== 类型定义 ====================
export interface ModelOption {
  id: number;
  modelName: string;
}

// ==================== Props ====================
defineProps<{
  loading?: boolean;
  models?: ModelOption[];
  placeholder?: string;
  /** 是否显示深度思考选项 */
  showDeepThinking?: boolean;
  /** 是否显示模型选择 */
  showModelSelect?: boolean;
  /** 是否显示联网搜索选项 */
  showWebSearch?: boolean;
}>();

// ==================== Emits ====================
const emit = defineEmits<{
  cancel: [];
  submit: [value: string];
}>();
// ==================== Model ====================
const inputValue = defineModel<string>('value', { default: '' });
const modelId = defineModel<number | undefined>('modelId');
const deepThinking = defineModel<boolean>('deepThinking', { default: false });
const webSearch = defineModel<boolean>('webSearch', { default: false });

// ==================== 事件处理 ====================
function handleSubmit(value: string) {
  emit('submit', value);
}

function handleCancel() {
  emit('cancel');
}

function toggleDeepThinking() {
  deepThinking.value = !deepThinking.value;
}

function toggleWebSearch() {
  webSearch.value = !webSearch.value;
}
</script>

<template>
  <div class="ai-chat-input-wrapper">
    <div class="ai-chat-input-container">
      <div class="sender-box">
        <!-- 输入框 -->
        <Sender
          v-model:value="inputValue"
          :loading="loading"
          :placeholder="placeholder || '给 AI 发送消息'"
          @submit="handleSubmit"
          @cancel="handleCancel"
        />

        <!-- 底部选项栏 -->
        <div class="input-footer">
          <div class="input-options">
            <button
              v-if="showDeepThinking !== false"
              class="option-btn"
              :class="{ active: deepThinking }"
              @click="toggleDeepThinking"
            >
              <ThunderboltOutlined />
              <span>深度思考</span>
            </button>
            <button
              v-if="showWebSearch !== false"
              class="option-btn"
              :class="{ active: webSearch }"
              @click="toggleWebSearch"
            >
              <GlobalOutlined />
              <span>联网搜索</span>
            </button>
          </div>

          <!-- 模型选择 -->
          <Select
            v-if="showModelSelect !== false && models && models.length > 0"
            v-model:value="modelId"
            :options="models.map((m) => ({ label: m.modelName, value: m.id }))"
            placeholder="选择模型"
            :bordered="false"
            size="small"
            class="model-select"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.ai-chat-input-wrapper {
  flex-shrink: 0;
  padding: 8px 0 12px;
  background: var(--component-background, #fff);
}

.ai-chat-input-container {
  padding: 0 24px;
  margin: 0 auto;
}

.sender-box {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgb(0 0 0 / 3%);

  :deep(.ant-sender) {
    border: none !important;
    box-shadow: none !important;

    .ant-sender-input {
      padding: 10px 14px;
      font-size: 14px;

      textarea {
        &::placeholder {
          color: #bbb;
        }
      }
    }

    .ant-sender-actions {
      padding: 4px 10px;
    }
  }
}

.input-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 12px;
  border-top: 1px solid #f5f5f5;
}

.input-options {
  display: flex;
  gap: 8px;
}

.option-btn {
  display: flex;
  gap: 6px;
  align-items: center;
  padding: 6px 12px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 20px;
  transition: all 0.2s;

  .anticon {
    font-size: 14px;
  }

  &:hover {
    color: #1890ff;
    border-color: #1890ff;
  }

  &.active {
    color: #1890ff;
    background: #e6f7ff;
    border-color: #1890ff;
  }
}

.model-select {
  :deep(.ant-select-selector) {
    font-size: 13px;
    color: #666;
  }
}
</style>
