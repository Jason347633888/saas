<script setup lang="ts">
import type { Flow } from '#/api/workflow/instance/model';

import { computed, h, onMounted, ref } from 'vue';

import { VbenAvatar } from '@vben/common-ui';

import {
  ClockCircleOutlined,
  LoadingOutlined,
  MessageOutlined,
  UsergroupAddOutlined,
  UserOutlined,
} from '@ant-design/icons-vue';
import { Avatar, TimelineItem, Tooltip } from 'ant-design-vue';

import { LocalDictCode } from '#/api';
import { ossInfo } from '#/api/system/oss';
import { renderLocalDict } from '#/utils/render';
import { formatTime } from '#/utils/util.time';

defineOptions({
  name: 'ApprovalTimelineItem',
});

const props = defineProps<{ item: Flow }>();

interface AttachmentInfo {
  ossId: string;
  url: string;
  name: string;
}

/**
 * 处理附件信息
 */
const attachmentInfo = ref<AttachmentInfo[]>([]);
onMounted(async () => {
  if (!props.item.ext) {
    return null;
  }
  const resp = await ossInfo(props.item.ext.split(','));
  attachmentInfo.value = resp.map((item) => ({
    ossId: item.ossId,
    url: item.url,
    name: item.originalName,
  }));
});

/**
 * 这里无法处理昵称中带,的情况
 */
const isMultiplePerson = computed(
  () => props.item.approverName?.split(',').length > 1,
);

/**
 * 是否为待审批状态 (flowStatus: 1=待审批, 2=审批通过, 4=已终止)
 */
const isPending = computed(() => props.item.flowStatus === '1');
</script>

<template>
  <TimelineItem>
    <template #dot>
      <div class="timeline-dot" :class="{ 'is-pending': isPending }">
        <Avatar
          v-if="isMultiplePerson"
          class="bg-primary-400"
          :size="40"
          :icon="h(UsergroupAddOutlined)"
        />
        <VbenAvatar
          v-else
          :alt="item?.approverName ?? 'unknown'"
          class="size-[40px] rounded-full bg-primary text-white"
          src=""
        />
        <!-- 待审批 loading 动画 -->
        <div v-if="isPending" class="pending-indicator">
          <LoadingOutlined spin class="text-primary" />
        </div>
      </div>
    </template>

    <!-- 卡片式布局 -->
    <div class="timeline-card" :class="{ 'is-pending': isPending }">
      <!-- 头部：节点名称 + 状态 + 审批人 + 时间 -->
      <div class="timeline-card-header">
        <div class="header-left">
          <span class="node-name">{{ item.nodeName }}</span>
          <component
            :is="renderLocalDict(item.flowStatus, LocalDictCode.WF_TASK_STATUS)"
          />
        </div>
        <div class="header-right">
          <!-- 待审批状态显示 loading -->
          <div v-if="isPending" class="pending-status">
            <LoadingOutlined spin class="text-primary" />
            <span class="text-muted-foreground">等待审批</span>
          </div>
          <!-- 已处理状态显示审批人信息 -->
          <template v-else>
            <div v-if="isMultiplePerson" class="approver-badges">
              <Tooltip
                v-for="(name, index) in item.approverName
                  .split(',')
                  .slice(0, 3)"
                :key="index"
                :title="name"
              >
                <Avatar
                  class="approver-avatar"
                  :size="24"
                  :icon="h(UserOutlined)"
                />
              </Tooltip>
              <span
                v-if="item.approverName.split(',').length > 3"
                class="approver-more"
              >
                +{{ item.approverName.split(',').length - 3 }}
              </span>
            </div>
            <div v-else class="approver-info">
              <UserOutlined class="text-muted-foreground" />
              <span>{{ item.approverName }}</span>
            </div>
          </template>
          <!-- 时间显示 -->
          <Tooltip
            v-if="item.approvalTime"
            :title="formatTime(item.approvalTime)"
          >
            <div class="timeline-time">
              <ClockCircleOutlined class="text-xs" />
              <span>{{ formatTime(item.approvalTime) }}</span>
            </div>
          </Tooltip>
        </div>
      </div>

      <!-- 内容区域：只显示审批意见和附件 -->
      <div
        v-if="item.message || attachmentInfo.length > 0"
        class="timeline-card-body"
      >
        <!-- 审批意见 -->
        <div v-if="item.message" class="message-box">
          <div class="message-icon">
            <MessageOutlined />
          </div>
          <div class="message-content">{{ item.message }}</div>
        </div>

        <!-- 附件列表 -->
        <div v-if="attachmentInfo.length > 0" class="attachment-list">
          <a
            v-for="attachment in attachmentInfo"
            :key="attachment.ossId"
            :href="attachment.url"
            class="attachment-item"
            target="_blank"
          >
            <span class="icon-[mingcute--attachment-line] size-[16px]"></span>
            <span class="attachment-name">{{ attachment.name }}</span>
          </a>
        </div>
      </div>
    </div>
  </TimelineItem>
</template>

<style lang="scss" scoped>
.timeline-dot {
  position: relative;

  &:not(.is-pending)::after {
    position: absolute;
    right: -2px;
    bottom: 2px;
    width: 12px;
    height: 12px;
    content: '';
    background: #52c41a;
    border: 2px solid white;
    border-radius: 50%;
  }

  &.is-pending {
    .pending-indicator {
      position: absolute;
      right: -4px;
      bottom: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 18px;
      height: 18px;
      font-size: 12px;
      background: white;
      border-radius: 50%;
      box-shadow: 0 2px 4px rgb(0 0 0 / 10%);
    }
  }
}

.timeline-card {
  margin-bottom: 16px;
  margin-left: 12px;
  overflow: hidden;
  background: var(--component-background, #fff);
  border: 1px solid var(--border-color, #f0f0f0);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgb(0 0 0 / 4%);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgb(0 0 0 / 8%);
  }
}

.timeline-card-header {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: var(--accent, #fafafa);

  .header-left {
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .header-right {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .node-name {
    font-size: 14px;
    font-weight: 600;
    color: var(--foreground);
  }
}

.approver-badges {
  display: flex;
  align-items: center;

  .approver-avatar {
    margin-left: -8px;
    background: var(--primary, #1890ff);
    border: 2px solid white;

    &:first-child {
      margin-left: 0;
    }
  }

  .approver-more {
    margin-left: 4px;
    font-size: 12px;
    color: var(--muted-foreground);
  }
}

.approver-info {
  display: flex;
  gap: 4px;
  align-items: center;
  font-size: 13px;
  color: var(--muted-foreground);
}

.pending-status {
  display: flex;
  gap: 6px;
  align-items: center;
  padding: 4px 12px;
  font-size: 13px;
  background: rgb(24 144 255 / 8%);
  border-radius: 16px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.6;
  }
}

.timeline-time {
  display: flex;
  gap: 4px;
  align-items: center;
  font-size: 12px;
  color: var(--muted-foreground, #999);
}

.timeline-card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px 16px;
  border-top: 1px solid var(--border-color, #f0f0f0);
}

.message-box {
  display: flex;
  gap: 10px;
  padding: 10px 12px;
  background: linear-gradient(
    135deg,
    rgb(249 250 251) 0%,
    rgb(243 244 246) 100%
  );
  border-radius: 8px;

  .message-icon {
    flex-shrink: 0;
    color: var(--primary, #1890ff);
  }

  .message-content {
    font-size: 13px;
    line-height: 1.6;
    color: var(--foreground);
    word-break: break-all;
  }
}

.attachment-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.attachment-item {
  display: flex;
  gap: 4px;
  align-items: center;
  padding: 4px 10px;
  font-size: 12px;
  color: var(--primary, #1890ff);
  background: rgb(24 144 255 / 5%);
  border-radius: 4px;
  transition: all 0.2s;

  &:hover {
    background: rgb(24 144 255 / 10%);
  }

  .attachment-name {
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
