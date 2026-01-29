/**
 * AI 模块公共组件和工具
 * 提供 AI 对话相关的通用组件，可在不同场景复用
 */

// ==================== 组件导出 ====================
export { default as AiChatInput } from './components/AiChatInput.vue';
export { default as AiChatMessages } from './components/AiChatMessages.vue';
export { default as AiSidebar } from './components/AiSidebar.vue';

// ==================== 类型导出 ====================
export type { ChatMessage } from './components/AiChatMessages.vue';
export type { SidebarGroup, SidebarItem } from './components/AiSidebar.vue';

// ==================== Composables 导出 ====================
export { useSseStream } from './composables/useSseStream';
export type {
  SseStreamOptions,
  SseStreamResult,
} from './composables/useSseStream';
