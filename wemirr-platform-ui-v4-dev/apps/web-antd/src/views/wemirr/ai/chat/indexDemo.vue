<script setup lang="ts" name="AiChatPage">
import type { AskReq, ModelConfig } from './api';

import { nextTick, onMounted, ref } from 'vue';

import { useAccessStore } from '@vben/stores';

import {
  ClearOutlined,
  RobotOutlined,
  SendOutlined,
  UserOutlined,
} from '@ant-design/icons-vue';
import {
  Avatar,
  Button,
  Card,
  Input,
  message,
  Select,
  Spin,
  Switch,
  Typography,
} from 'ant-design-vue';
import {
  MarkdownCodeBlockNode,
  MarkdownRender,
  setCustomComponents,
} from 'markstream-vue';
import { SSE } from 'sse.js';

import { ConversationType, getChatStreamUrl, getModelList } from './api';

import 'markstream-vue/index.css';

// 注册自定义代码块渲染器（使用 Shiki 高亮）
setCustomComponents('ai-chat', {
  code_block: MarkdownCodeBlockNode,
});

// ==================== 类型定义 ====================

interface Message {
  id: string;
  role: 'assistant' | 'user';
  content: string;
  thinking?: string;
  loading?: boolean;
}

// ==================== 状态管理 ====================

const messages = ref<Message[]>([]);
const messageListRef = ref<HTMLElement | null>(null);
const inputValue = ref('');
const isStreaming = ref(false);

// 模型选择
const models = ref<ModelConfig[]>([]);
const selectedModelId = ref<number | undefined>(undefined);

// 模式切换：true=真实SSE，false=模拟
const useRealApi = ref(true);

// SSE 实例引用（用于停止）
let sseSource: null | SSE = null;

// 流式输出状态（参考官方示例，使用单独的 ref）
const streamingMsgId = ref<null | string>(null);
const streamingContent = ref('');
const streamingThinking = ref('');

// ==================== 生命周期 ====================

onMounted(async () => {
  await loadModels();
});

/** 加载模型列表 */
async function loadModels() {
  try {
    models.value = await getModelList();
    if (models.value.length > 0 && !selectedModelId.value) {
      selectedModelId.value = models.value[0]?.id;
    }
  } catch {
    console.error('加载模型列表失败，将使用模拟模式');
    useRealApi.value = false;
  }
}

// ==================== 模拟流式响应 ====================

/** 模拟 Markdown 内容 */
const mockMarkdownContent = `## markstream-vue 渲染测试

这是一个**流式渲染**的 Markdown 内容测试。

### 特性展示

1. **代码高亮** - 支持多种语言
2. **表格渲染** - 结构化数据展示
3. **列表支持** - 有序和无序列表

\`\`\`typescript
// TypeScript 代码示例
interface User {
  id: number;
  name: string;
  email: string;
}

function greet(user: User): string {
  return \`Hello, \${user.name}!\`;
}
\`\`\`

### 表格示例

| 功能 | 状态 | 说明 |
|------|------|------|
| 流式渲染 | ✅ | 逐字符输出 |
| 代码高亮 | ✅ | Shiki/Prism |
| 数学公式 | ✅ | KaTeX 支持 |

> 💡 **提示**: markstream-vue 专为 AI 流式输出设计，能够平滑渲染不完整的 Markdown 文本。

行内代码测试: \`const x = 1;\`


\`\`\`mermaid
graph TD
  A-->B
\`\`\`
---

渲染完成！🎉
`;

/** 模拟流式打字效果 */
async function mockStreamResponse() {
  const chunks = mockMarkdownContent.split('');

  for (const char of chunks) {
    if (!isStreaming.value) break;
    await new Promise((r) => setTimeout(r, 15));
    // 使用单独的 ref 更新，确保响应式生效
    streamingContent.value += char;
    scrollToBottom();
  }
}

// ==================== SSE 流式请求 ====================

/** SSE 流式对话请求 */
async function sseStreamResponse(prompt: string) {
  return new Promise<void>((resolve) => {
    const accessStore = useAccessStore();
    const token = accessStore.accessToken;
    const baseUrl = import.meta.env.VITE_GLOB_API_URL || '';

    const payload: AskReq = {
      chatType: ConversationType.NORMAL,
      modelId: selectedModelId.value,
      prompt,
      returnThinking: true,
      conversationId: '2002228403977297921',
    };

    sseSource = new SSE(`${baseUrl}${getChatStreamUrl()}`, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      payload: JSON.stringify(payload),
      method: 'POST',
    });

    sseSource.addEventListener('message', (event: MessageEvent) => {
      const rawData = event.data;
      if (!rawData) return;

      // 将字面 \n 转换为真正的换行符
      const processText = (text: string) =>
        text.replaceAll(String.raw`\n`, '\n');

      try {
        // 尝试解析 JSON 格式
        const data = JSON.parse(rawData);

        // 处理思考内容
        if (data.thinking) {
          streamingThinking.value += processText(data.thinking);
        }
        // 处理回复内容
        if (data.content) {
          streamingContent.value += processText(data.content);
          scrollToBottom();
        }
      } catch {
        // JSON 解析失败，说明是纯文本格式，直接追加
        streamingContent.value += processText(rawData);
        scrollToBottom();
      }
    });

    sseSource.addEventListener('error', () => {
      sseSource?.close();
      sseSource = null;
      resolve();
    });

    sseSource.addEventListener('readystatechange', (e: any) => {
      if (e.readyState === 2) {
        sseSource = null;
        resolve();
      }
    });

    sseSource.stream();
  });
}

// ==================== 消息处理 ====================

/** 发送消息 */
async function handleSend() {
  const value = inputValue.value.trim();
  if (!value || isStreaming.value) return;

  // 添加用户消息
  messages.value.push({
    id: `user-${Date.now()}`,
    role: 'user',
    content: value,
  });
  inputValue.value = '';
  scrollToBottom();

  // 准备 AI 响应 - 重置流式状态
  const msgId = `ai-${Date.now()}`;
  streamingMsgId.value = msgId;
  streamingContent.value = '';
  streamingThinking.value = '';

  const aiMsg: Message = {
    id: msgId,
    role: 'assistant',
    content: '',
    thinking: '',
    loading: true,
  };
  messages.value.push(aiMsg);
  const aiMsgIndex = messages.value.length - 1;
  scrollToBottom();

  // 发起流式响应
  isStreaming.value = true;

  try {
    await (useRealApi.value ? sseStreamResponse(value) : mockStreamResponse());
  } catch (error) {
    console.error('流式响应错误:', error);
    message.error('请求失败，请重试');
  }

  // 流式结束，将内容同步到消息对象
  messages.value[aiMsgIndex]!.content = streamingContent.value;
  messages.value[aiMsgIndex]!.thinking = streamingThinking.value;
  messages.value[aiMsgIndex]!.loading = false;
  streamingMsgId.value = null;
  isStreaming.value = false;
}

/** 清空消息 */
function handleClear() {
  messages.value = [];
  handleStop();
}

/** 停止生成 */
function handleStop() {
  isStreaming.value = false;
  if (sseSource) {
    sseSource.close();
    sseSource = null;
  }
}

// ==================== 工具函数 ====================

/** 滚动到底部（节流优化，避免频繁触发） */
let scrollTimer: null | ReturnType<typeof setTimeout> = null;
function scrollToBottom() {
  if (scrollTimer) return;
  scrollTimer = setTimeout(() => {
    scrollTimer = null;
    nextTick(() => {
      if (messageListRef.value) {
        messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
      }
    });
  }, 100); // 100ms 节流
}

/** 按键处理 */
function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    handleSend();
  }
}
</script>

<template>
  <div class="chat-container">
    <!-- 顶部标题栏 -->
    <header class="chat-header">
      <Typography.Title :level="4" style="margin: 0">
        <RobotOutlined /> AI 对话 + markstream-vue
      </Typography.Title>
      <div class="header-actions">
        <!-- 模式切换 -->
        <div class="mode-switch">
          <Typography.Text type="secondary">模拟</Typography.Text>
          <Switch v-model:checked="useRealApi" size="small" />
          <Typography.Text type="secondary">SSE</Typography.Text>
        </div>
        <!-- 模型选择 -->
        <Select
          v-if="useRealApi && models.length > 0"
          v-model:value="selectedModelId"
          :options="models.map((m) => ({ label: m.modelName, value: m.id }))"
          placeholder="选择模型"
          style="width: 180px"
          size="small"
        />
        <Button
          size="small"
          @click="handleClear"
          :disabled="messages.length === 0"
        >
          <ClearOutlined /> 清空
        </Button>
      </div>
    </header>

    <!-- 消息列表 -->
    <div ref="messageListRef" class="message-list">
      <!-- 空状态 -->
      <div v-if="messages.length === 0" class="empty-state">
        <RobotOutlined class="empty-icon" />
        <Typography.Text type="secondary">
          发送任意消息测试 markstream-vue 流式渲染
        </Typography.Text>
      </div>

      <!-- 消息气泡 -->
      <div
        v-for="msg in messages"
        :key="msg.id"
        class="message-item"
        :class="msg.role"
      >
        <Avatar :size="36" class="message-avatar" :class="msg.role">
          <template #icon>
            <UserOutlined v-if="msg.role === 'user'" />
            <RobotOutlined v-else />
          </template>
        </Avatar>

        <Card class="message-bubble" :class="msg.role" :bordered="false">
          <!-- 用户消息：纯文本 -->
          <template v-if="msg.role === 'user'">
            {{ msg.content }}
          </template>

          <!-- AI 消息：Markdown 渲染 -->
          <template v-else>
            <!-- 正在流式输出的消息 -->
            <template v-if="msg.id === streamingMsgId">
              <Spin v-if="!streamingContent" size="small" />
              <template v-else>
                <details v-if="streamingThinking" class="thinking-block">
                  <summary>💭 思考过程</summary>
                  <div class="thinking-content">{{ streamingThinking }}</div>
                </details>
                <MarkdownRender
                  custom-id="ai-chat"
                  :content="streamingContent"
                />
              </template>
            </template>
            <!-- 已完成的消息 -->
            <template v-else>
              <details v-if="msg.thinking" class="thinking-block">
                <summary>💭 思考过程</summary>
                <div class="thinking-content">{{ msg.thinking }}</div>
              </details>
              <MarkdownRender custom-id="ai-chat" :content="msg.content" />
            </template>
          </template>
        </Card>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <Input.TextArea
        v-model:value="inputValue"
        :auto-size="{ minRows: 1, maxRows: 4 }"
        placeholder="输入消息，按 Enter 发送..."
        :disabled="isStreaming"
        @keydown="handleKeydown"
      />
      <Button v-if="isStreaming" type="default" danger @click="handleStop">
        停止
      </Button>
      <Button
        v-else
        type="primary"
        :disabled="!inputValue.trim()"
        @click="handleSend"
      >
        <SendOutlined /> 发送
      </Button>
    </div>
  </div>
</template>

<style lang="less" scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--component-background, #fff);
}

.chat-header {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-bottom: 1px solid var(--border-color, #f0f0f0);

  .header-actions {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .mode-switch {
    display: flex;
    gap: 6px;
    align-items: center;
    padding: 4px 8px;
    background: var(--hover-color, #f5f5f5);
    border-radius: 6px;
  }
}

.message-list {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  overflow-y: auto;
}

.empty-state {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  justify-content: center;
  height: 100%;

  .empty-icon {
    font-size: 64px;
    color: var(--text-color-secondary, #999);
    opacity: 0.3;
  }
}

.message-item {
  display: flex;
  gap: 12px;
  max-width: 85%;

  &.user {
    flex-direction: row-reverse;
    align-self: flex-end;
  }

  &.assistant {
    align-self: flex-start;
  }
}

.message-avatar {
  flex-shrink: 0;

  &.user {
    background: var(--primary-color, #1890ff);
  }

  &.assistant {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
}

.message-bubble {
  border-radius: 12px;

  :deep(.ant-card-body) {
    padding: 12px 16px;
  }

  &.user {
    color: #fff;
    background: var(--primary-color, #1890ff);
  }

  &.assistant {
    background: var(--hover-color, #f5f5f5);
  }
}

.input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color, #f0f0f0);

  :deep(.ant-input) {
    flex: 1;
  }
}

.thinking-block {
  padding: 8px 12px;
  margin-bottom: 12px;
  font-size: 13px;
  background: rgb(102 126 234 / 8%);
  border: 1px dashed rgb(102 126 234 / 30%);
  border-radius: 8px;

  summary {
    font-weight: 500;
    color: #667eea;
    cursor: pointer;
    user-select: none;

    &:hover {
      opacity: 0.8;
    }
  }

  .thinking-content {
    max-height: 200px;
    padding-top: 8px;
    margin-top: 8px;
    overflow-y: auto;
    font-size: 12px;
    color: var(--text-color-secondary, #666);
    white-space: pre-wrap;
    border-top: 1px dashed rgb(102 126 234 / 20%);
  }
}

// ==================== Markdown 样式适配 ====================
:deep(.markstream-vue) {
  background: transparent !important;

  pre {
    margin: 12px 0;
    overflow-x: auto;
    border-radius: 8px;
  }

  code {
    font-family: 'Fira Code', Monaco, Consolas, monospace;
    font-size: 13px;
  }

  table {
    width: 100%;
    margin: 12px 0;
    border-collapse: collapse;

    th,
    td {
      padding: 8px 12px;
      text-align: left;
      border: 1px solid var(--border-color, #e8e8e8);
    }

    th {
      font-weight: 600;
      background: var(--hover-color, #fafafa);
    }
  }

  blockquote {
    padding: 8px 16px;
    margin: 12px 0;
    background: var(--hover-color, #f9f9f9);
    border-left: 4px solid var(--primary-color, #1890ff);
    border-radius: 0 8px 8px 0;
  }

  p:last-child {
    margin-bottom: 0;
  }

  h2,
  h3,
  h4 {
    margin-top: 16px;
    margin-bottom: 8px;
  }

  ul,
  ol {
    padding-left: 20px;
    margin: 8px 0;
  }

  hr {
    margin: 16px 0;
    border: none;
    border-top: 1px solid var(--border-color, #e8e8e8);
  }
}
</style>
