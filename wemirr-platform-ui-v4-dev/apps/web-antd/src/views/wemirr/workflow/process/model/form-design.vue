<script setup lang="ts">
import type { PageSchema } from 'epic-designer';

import { computed, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { useTabs } from '@vben/hooks';

import { useUi } from '@fast-crud/fast-crud';
import { EDesigner } from 'epic-designer';

import * as api from './api';

const props = defineProps<{
  /** 流程定义ID，弹窗模式使用 */
  defId?: string;
}>();

const emit = defineEmits<{
  /** 保存成功回调 */
  saved: [];
}>();

const { ui } = useUi();
const { closeCurrentTab } = useTabs();
const route = useRoute();

// 优先使用 props.defId，否则从路由获取
const currentDefId = computed(
  () => props.defId || (route.query.defId as string),
);
const designerRef = ref<InstanceType<typeof EDesigner>>();

/**
 * 加载表单数据
 */
async function loadFormData(id: string) {
  if (!id) return;
  const data = await api.getFormByModelId(id);
  if (data && designerRef.value) {
    designerRef.value.setData(data);
  }
}

onMounted(async () => {
  if (currentDefId.value) {
    await loadFormData(currentDefId.value);
  }
});

// 监听 props.defId 变化，用于弹窗模式
watch(
  () => props.defId,
  async (newId) => {
    if (newId) {
      await loadFormData(newId);
    }
  },
);

/**
 * 点击保存按钮操作
 * @param e
 */
function handleSubmit(e: PageSchema) {
  api.saveFormDesign(currentDefId.value, e).then(() => {
    ui.notification.success('表单设计成功');
    // 弹窗模式：触发事件；路由模式：关闭标签页
    if (props.defId) {
      emit('saved');
    } else {
      closeCurrentTab();
    }
  });
}

/**
 * 外部调用保存方法
 */
function save() {
  if (designerRef.value) {
    const data = designerRef.value.getData();
    handleSubmit(data);
  }
}

defineExpose({
  save,
});
</script>
<template>
  <div class="form-designer-wrapper">
    <EDesigner
      ref="designerRef"
      :disabled-zoom="true"
      :lock-default-schema-edit="true"
      :hidden-header="true"
      title=""
      @save="handleSubmit"
    >
      <template #header-prefix>
        <div>WP 设计器</div>
      </template>
    </EDesigner>
  </div>
</template>

<style lang="scss">
// ========== 表单设计器容器 ==========
.form-designer-wrapper {
  width: 100%;
  height: 100%;
}

// ========== 表单设计器整体布局 ==========
.epic-designer-main {
  // height: calc(100vh - 143px);

  .epic-header-container {
    border: 0;
  }

  .epic-split-view-container {
    border-top: none !important;
    border-bottom: none !important;
  }
}

// ========== 关键：左侧容器改为纵向布局 ==========
// 这是包含 action-bar 和 left-sidebar 的父容器
.designer-container .relative.flex,
.epic-designer-main > .relative.flex,
div.relative.flex:has(> .epic-action-bar) {
  flex-direction: column !important;
}

// ========== 组件/源码/大纲 - 横向标签栏 ==========
.epic-action-bar {
  width: 100% !important;
  background: #fff;
}

ul.epic-actions-container {
  display: flex !important;
  flex-direction: row !important;
  gap: 0 !important;
  justify-content: flex-start !important;
  width: 100% !important;
  padding: 0 !important;
  margin: 0 !important;
  list-style: none !important;
  background: #fff;
}

li.epic-action-item {
  display: flex !important;
  flex: 1 !important;
  flex-direction: row !important;
  gap: 8px !important;
  align-items: center !important;
  justify-content: center !important;
  height: 48px !important;
  padding: 0 !important;
  font-size: 14px !important;
  color: #8c8c8c !important;
  cursor: pointer !important;
  background: transparent !important;

  .iconfont {
    display: inline-block !important;
    font-size: 18px !important;
  }

  &:hover {
    color: #1890ff !important;
  }

  &.checked {
    color: #1890ff !important;
  }
}

// ========== 左侧边栏容器 ==========
.epic-left-sidebar {
  flex: 1 !important;
  width: 100% !important;
  overflow: hidden !important;
}

// ========== 组件视图内部布局 ==========
.epic-component-view {
  // tabs-box 和组件列表的父容器，改为纵向
  > .flex.flex-1.overflow-auto {
    flex-direction: column !important;
  }
}

// ========== 搜索框 ==========
.epic-search-box {
  padding: 12px 16px !important;
  background: #fff;
  border-bottom: 1px solid #f5f5f5;

  .n-input,
  .ant-input-affix-wrapper {
    border-radius: 6px !important;
  }
}

// ========== 全部/表单/布局/预设 选项卡 ==========
.epic-tabs-box {
  display: flex !important;
  flex-flow: row nowrap !important;
  gap: 0 !important;
  align-items: center !important;
  justify-content: flex-start !important;
  width: 100% !important;
  height: 44px !important;
  padding: 0 !important;
  margin-top: 12px !important;
  background: #fff !important;
  border-bottom: 1px solid #f0f0f0 !important;
}

.epic-tabs-box .epic-tab,
.epic-tabs-box > .epic-tab,
div.epic-tabs-box div.epic-tab {
  display: flex !important;
  flex: 1 !important;
  align-items: center !important;
  justify-content: center !important;
  height: 32px !important;
  padding: 0 !important;
  margin: 6px 4px !important;
  font-size: 14px !important;
  line-height: 32px !important;
  color: #595959 !important;
  cursor: pointer !important;
  background: transparent !important;
  border: none !important;
  border-top: none !important;
  border-right: none !important;
  border-bottom: none !important;
  border-left: none !important;
  border-radius: 6px !important;
  box-shadow: none !important;
}

.epic-tabs-box .epic-tab:hover,
.epic-tabs-box > .epic-tab:hover,
div.epic-tabs-box div.epic-tab:hover {
  color: #1890ff !important;
  background: rgb(24 144 255 / 8%) !important;
  border: none !important;
  border-bottom: none !important;
}

.epic-tabs-box .epic-tab.checked,
.epic-tabs-box > .epic-tab.checked,
div.epic-tabs-box div.epic-tab.checked {
  color: #1890ff !important;
  background: rgb(24 144 255 / 10%) !important;
  border: none !important;
  border-top: none !important;
  border-right: none !important;
  border-bottom: none !important;
  border-left: none !important;
  box-shadow: none !important;
}

// ========== 组件列表项 ==========
.epic-componet-item {
  display: flex !important;
  gap: 10px !important;
  align-items: center !important;
  padding: 12px 16px !important;
  cursor: move !important;
  background: #fff !important;
  border: 1px solid #e8e8e8 !important;
  border-radius: 6px !important;
  transition: all 0.15s ease !important;

  .iconfont {
    font-size: 16px !important;
    color: #8c8c8c !important;
  }

  .epic-componet-label {
    display: block !important;
    flex: 1 !important;
    overflow: hidden !important;
    text-overflow: ellipsis !important;
    font-size: 14px !important;
    color: #262626 !important;
    white-space: nowrap !important;
  }

  &:hover {
    border-color: #1890ff !important;
    box-shadow: 0 2px 8px rgb(0 0 0 / 8%) !important;

    .iconfont {
      color: #1890ff !important;
    }
  }
}
</style>
