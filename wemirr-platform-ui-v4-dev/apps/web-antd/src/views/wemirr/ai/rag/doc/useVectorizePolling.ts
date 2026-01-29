import { onUnmounted, ref } from 'vue';

import { message } from 'ant-design-vue';

import * as documentApi from './api';

export function useVectorizePolling(refreshCallback?: () => void) {
  const pollingMap = ref<Map<number, ReturnType<typeof setTimeout>>>(new Map());
  const pollingDocuments = ref<Set<number>>(new Set());

  const startVectorizeStatusPolling = (documentId: number) => {
    // 如果已经在轮询中，先清除
    const existingTimer = pollingMap.value.get(documentId);
    if (existingTimer) {
      clearTimeout(existingTimer);
    }

    pollingDocuments.value.add(documentId);

    const poll = async () => {
      try {
        const status = await documentApi.GetVectorizeStatus(documentId);

        // 如果状态已完成（成功或失败），停止轮询并刷新列表
        if (status.vectorized === 1 || status.vectorized === -1) {
          pollingDocuments.value.delete(documentId);
          pollingMap.value.delete(documentId);

          // 刷新列表
          refreshCallback?.();

          // 显示结果消息
          if (status.vectorized === 1) {
            message.success('文档向量化完成');
          } else {
            message.error(
              `文档向量化失败${status.message ? `: ${status.message}` : ''}`,
            );
          }

          return;
        }

        // 如果仍在处理中，继续轮询
        if (status.vectorized === 2) {
          const timer = setTimeout(poll, 10_000); // 10秒后再次查询
          pollingMap.value.set(documentId, timer);
        }
      } catch (error) {
        console.error('查询向量化状态失败:', error);
        // 出错时也停止轮询
        pollingDocuments.value.delete(documentId);
        pollingMap.value.delete(documentId);
      }
    };

    // 立即执行一次查询
    poll();
  };

  const stopAllPolling = () => {
    pollingMap.value.forEach((timer) => {
      clearTimeout(timer);
    });
    pollingMap.value.clear();
    pollingDocuments.value.clear();
  };

  // 组件卸载时自动清理
  onUnmounted(() => {
    stopAllPolling();
  });

  return {
    pollingDocuments,
    startVectorizeStatusPolling,
    stopAllPolling,
  };
}
