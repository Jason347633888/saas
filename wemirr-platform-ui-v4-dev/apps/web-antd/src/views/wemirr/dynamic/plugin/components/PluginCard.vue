<script setup lang="ts">
/**
 * 插件卡片组件
 */
import type { FieldPluginVO } from '@/api/dynamic';

import { computed } from 'vue';

interface Props {
  plugin: FieldPluginVO;
  selectable?: boolean;
  selected?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  selectable: false,
  selected: false,
});

const emit = defineEmits<{
  (e: 'click', plugin: FieldPluginVO): void;
  (e: 'select', plugin: FieldPluginVO): void;
  (e: 'view', plugin: FieldPluginVO): void;
  (e: 'edit', plugin: FieldPluginVO): void;
  (e: 'delete', plugin: FieldPluginVO): void;
}>();

// 状态颜色
const statusColor = computed(() => {
  return props.plugin.status === 'enabled' ? 'success' : 'default';
});

// 状态文本
const statusText = computed(() => {
  return props.plugin.status === 'enabled' ? '已启用' : '已禁用';
});

// 插件图标
const pluginIcon = computed(() => {
  const iconMap: Record<string, string> = {
    text: 'Aa',
    number: '#',
    select: 'Dropdown',
    date: 'Calendar',
    textarea: 'Edit',
    switch: 'Switcher',
    upload: 'Upload',
    custom: 'Code',
  };
  return iconMap[props.plugin.type] || 'Code';
});

// 点击卡片
function handleClick() {
  emit('click', props.plugin);
}

// 点击选择框
function handleSelect(e: Event) {
  e?.stopPropagation();
  emit('select', props.plugin);
}

// 查看详情
function handleView() {
  emit('view', props.plugin);
}

// 编辑插件
function handleEdit() {
  emit('edit', props.plugin);
}

// 删除插件
function handleDelete() {
  emit('delete', props.plugin);
}
</script>

<template>
  <a-card
    :hoverable="true"
    :class="{ 'plugin-card--selected': selected }"
    @click="handleClick"
  >
    <template #title>
      <div class="plugin-card__header">
        <div class="plugin-card__icon">
          {{ pluginIcon }}
        </div>
        <div class="plugin-card__info">
          <span class="plugin-card__name">{{ plugin.name }}</span>
          <a-tag :color="statusColor">{{ statusText }}</a-tag>
        </div>
      </div>
    </template>

    <template #extra>
      <a-checkbox
        v-if="selectable"
        :checked="selected"
        @change="handleSelect"
      />
    </template>

    <div class="plugin-card__body">
      <div class="plugin-card__meta">
        <span class="plugin-card__type">类型: {{ plugin.type }}</span>
        <span class="plugin-card__version">v{{ plugin.version }}</span>
      </div>
      <div v-if="plugin.description" class="plugin-card__description">
        {{ plugin.description }}
      </div>
      <div v-if="plugin.author" class="plugin-card__author">
        作者: {{ plugin.author }}
      </div>
    </div>

    <template #actions>
      <span class="action-btn" @click="handleView">
        <EyeOutlined />
        查看
      </span>
      <span class="action-btn" @click="handleEdit">
        <EditOutlined />
        编辑
      </span>
      <span class="action-btn" @click="handleDelete">
        <DeleteOutlined />
        删除
      </span>
    </template>
  </a-card>
</template>

<style lang="less" scoped>
.plugin-card {
  &--selected {
    border-color: @primary-color;
  }

  &__header {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: @primary-color;
    color: #fff;
    border-radius: 8px;
    font-weight: bold;
    font-size: 16px;
  }

  &__info {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  &__name {
    font-weight: 500;
    font-size: 16px;
  }

  &__body {
    margin-top: 12px;
  }

  &__meta {
    display: flex;
    justify-content: space-between;
    color: @text-color-secondary;
    font-size: 12px;
    margin-bottom: 8px;
  }

  &__description {
    color: @text-color;
    font-size: 14px;
    margin-bottom: 8px;
    line-height: 1.5;
  }

  &__author {
    color: @text-color-secondary;
    font-size: 12px;
  }
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;

  &:hover {
    color: @primary-color;
  }
}
</style>
