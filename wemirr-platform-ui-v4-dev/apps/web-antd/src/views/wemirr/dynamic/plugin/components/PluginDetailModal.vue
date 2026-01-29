<script setup lang="ts">
/**
 * 插件详情弹窗组件
 */
import type { FieldPluginVO } from '@/api/dynamic';

import { computed, ref, watch } from 'vue';

interface Props {
  open: boolean;
  plugin: FieldPluginVO | null;
}

const props = withDefaults(defineProps<Props>(), {
  open: false,
});

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'edit', plugin: FieldPluginVO): void;
  (e: 'close'): void;
}>();

// Tab 切换
const activeTab = ref('info');

// 弹窗显示控制
const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

// 监听插件变化
watch(
  () => props.plugin,
  (plugin) => {
    if (plugin) {
      activeTab.value = 'info';
    }
  }
);

// 关闭弹窗
function handleClose() {
  visible.value = false;
  emit('close');
}

// 编辑插件
function handleEdit() {
  if (props.plugin) {
    emit('edit', props.plugin);
    visible.value = false;
  }
}

// 复制 Schema
function copySchema() {
  if (props.plugin) {
    const schemaStr = JSON.stringify(props.plugin.schema, null, 2);
    navigator.clipboard.writeText(schemaStr);
  }
}
</script>

<template>
  <a-modal
    v-model:open="visible"
    :title="plugin?.name || '插件详情'"
    width="800px"
    :footer="null"
    destroy-on-close
    @cancel="handleClose"
  >
    <a-tabs v-model:activeKey="activeTab">
      <!-- 基本信息 -->
      <a-tab-pane key="info" tab="基本信息">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="类型">
            <code>{{ plugin?.type }}</code>
          </a-descriptions-item>
          <a-descriptions-item label="版本">
            <a-tag color="blue">v{{ plugin?.version }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="名称">
            {{ plugin?.name }}
          </a-descriptions-item>
          <a-descriptions-item label="分类">
            {{ plugin?.category || '未分类' }}
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="plugin?.status === 'enabled' ? 'success' : 'default'">
              {{ plugin?.status === 'enabled' ? '已启用' : '已禁用' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="作者">
            {{ plugin?.author || '未知' }}
          </a-descriptions-item>
          <a-descriptions-item label="创建时间" :span="2">
            {{ plugin?.createTime || '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="描述" :span="2">
            {{ plugin?.description || '暂无描述' }}
          </a-descriptions-item>
        </a-descriptions>
      </a-tab-pane>

      <!-- Schema 配置 -->
      <a-tab-pane key="schema" tab="Schema 配置">
        <a-space direction="vertical" style="width: 100%">
          <a-button type="primary" @click="copySchema">
            <template #icon><CopyOutlined /></template>
            复制 Schema
          </a-button>
          <code-editor
            :value="JSON.stringify(plugin?.schema || {}, null, 2)"
            language="json"
            height="300px"
            :readonly="true"
          />
        </a-space>
      </a-tab-pane>

      <!-- 默认配置 -->
      <a-tab-pane key="config" tab="默认配置">
        <code-editor
          :value="JSON.stringify(plugin?.config || {}, null, 2)"
          language="json"
          height="300px"
          :readonly="true"
        />
      </a-tab-pane>

      <!-- 验证规则 -->
      <a-tab-pane key="validation" tab="验证规则">
        <code-editor
          :value="JSON.stringify(plugin?.validation || {}, null, 2)"
          language="json"
          height="300px"
          :readonly="true"
        />
      </a-tab-pane>
    </a-tabs>

    <template #footer>
      <a-space>
        <a-button @click="handleClose-button>
        ">关闭</a<a-button type="primary" @click="handleEdit">
          <template #icon><EditOutlined /></template>
          编辑插件
        </a-button>
      </a-space>
    </template>
  </a-modal>
</template>
