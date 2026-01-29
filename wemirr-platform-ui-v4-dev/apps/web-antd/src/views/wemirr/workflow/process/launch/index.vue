<script lang="ts" setup>
import type { PageSchema } from 'epic-designer';

import { onMounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import { FsIcon, useUi } from '@fast-crud/fast-crud';
import { notification } from 'ant-design-vue';
import { EBuilder } from 'epic-designer';

import * as api from './api';

const router = useRouter();
const { ui } = useUi();

// 待审批入口（带数字）
const pendingAction = {
  key: 'pending',
  title: '待审批的',
  icon: 'mdi:bell-outline',
  color: '#1890ff',
  bgColor: '#e6f4ff',
  route: '/workflow/task/todo',
  count: 999,
};

// 其他快捷入口
const otherActions = [
  {
    key: 'mine',
    title: '我发起的',
    icon: 'mdi:file-document-edit-outline',
    color: '#52c41a',
    route: '/workflow/task/me-start',
  },
  {
    key: 'done',
    title: '我的待办',
    icon: 'mdi:checkbox-marked-circle-outline',
    color: '#13c2c2',
    route: '/workflow/task/todo',
  },
  {
    key: 'done',
    title: '我的已办',
    icon: 'mdi:checkbox-marked-circle-outline',
    color: '#13c2c2',
    route: '/workflow/task/done',
  },
];

// 分类折叠状态
const collapsedCategories = ref<Record<string, boolean>>({});

const toggleCategory = (categoryId: string) => {
  collapsedCategories.value[categoryId] =
    !collapsedCategories.value[categoryId];
};

const state = reactive({
  generateFormRenderKey: '',
  previewOpen: false,
  title: null as null | string,
  modelId: '',
  generateFormRef: null,
  pageSchema: ref<PageSchema>({
    schemas: [],
    script: '',
  }),
});

watch(
  () => state.previewOpen,
  () => {
    if (state.previewOpen) {
      state.generateFormRenderKey = Date.now().toString();
    }
  },
  { deep: true },
);

const initProcessForm = (item: any) => {
  api.GetFormConfigByModelId(item.id).then((ret) => {
    if (!ret) {
      notification.error({ message: '未配置表单', duration: 2 });
      return;
    }
    state.title = item.diagramName;
    state.previewOpen = true;
    state.modelId = item.id;
    state.pageSchema = { schemas: ret.schemas, script: ret.script };
  });
};

const ebRef = ref<any>(null);

async function handleReset() {
  const form = await ebRef.value?.getFormInstance();
  form?.resetFields();
}

const submitProcessInstance = () => {
  ebRef.value.validate().then((data: any) => {
    api
      .startProcessInstance(state.modelId, {
        formData: data,
        businessKey: Date.now(),
        businessGroup: 'DEFAULT',
        instanceName: state.title,
      })
      .then(() => {
        ui.notification.success('流程发布成功');
        state.previewOpen = false;
      });
  });
};

const groupList = ref<any[]>([]);

// 为每个流程生成随机颜色
const categoryColors: Record<string, string> = {};
const getRandomColor = (id: string): string => {
  if (!categoryColors[id]) {
    const colors = [
      '#1890ff',
      '#52c41a',
      '#faad14',
      '#f5222d',
      '#722ed1',
      '#13c2c2',
      '#eb2f96',
    ];
    categoryColors[id] = colors[Math.floor(Math.random() * colors.length)]!;
  }
  return categoryColors[id]!;
};

const navigateTo = (route: string) => {
  router.push(route);
};

onMounted(async () => {
  groupList.value = await api.ProcessModelGroupList();
});
</script>
<template>
  <div class="workflow-create-page">
    <!-- 快捷入口区域 -->
    <div class="quick-actions-row">
      <!-- 待审批的（单独一块，带数字） -->
      <div class="pending-card" @click="navigateTo(pendingAction.route)">
        <FsIcon
          :icon="pendingAction.icon"
          :style="{ color: pendingAction.color, fontSize: '28px' }"
        />
        <div class="pending-info">
          <span class="pending-count" :style="{ color: pendingAction.color }">
            {{ pendingAction.count }}
          </span>
          <span class="pending-title">{{ pendingAction.title }}</span>
        </div>
      </div>

      <!-- 其他入口（一组） -->
      <div class="other-actions-group">
        <div
          v-for="action in otherActions"
          :key="action.key"
          class="other-action-item"
          @click="navigateTo(action.route)"
        >
          <FsIcon
            :icon="action.icon"
            :style="{ color: action.color, fontSize: '22px' }"
          />
          <span class="other-action-title">{{ action.title }}</span>
        </div>
      </div>
    </div>

    <!-- 流程分类列表 -->
    <template v-for="groupItem in groupList" :key="groupItem.categoryId">
      <div v-if="groupItem.modelList?.length > 0" class="category-section">
        <!-- 分类标题栏 -->
        <div
          class="category-header"
          @click="toggleCategory(groupItem.categoryId)"
        >
          <div class="category-title">
            <span class="category-indicator"></span>
            <span>{{ groupItem.categoryName }}</span>
          </div>
          <FsIcon
            :icon="
              collapsedCategories[groupItem.categoryId]
                ? 'mdi:chevron-down'
                : 'mdi:chevron-up'
            "
            class="collapse-icon"
          />
        </div>

        <!-- 流程卡片列表 -->
        <div
          v-show="!collapsedCategories[groupItem.categoryId]"
          class="process-grid"
        >
          <div
            v-for="item in groupItem.modelList"
            :key="item.id"
            class="process-card"
            @click="initProcessForm(item)"
          >
            <div
              class="process-icon"
              :style="{ backgroundColor: getRandomColor(item.id) }"
            >
              <FsIcon
                :icon="item.diagramIcon || 'mdi:file-document-outline'"
                style="font-size: 20px; color: #fff"
              />
            </div>
            <div class="process-info">
              <div class="process-name">{{ item.diagramName }}</div>
              <div class="process-key">{{ item.definitionId || item.id }}</div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 表单弹窗 -->
    <a-modal
      v-model:open="state.previewOpen"
      :destroy-on-close="true"
      :title="state.title"
      width="800px"
      wrap-class-name="preview-modal-style"
    >
      <EBuilder ref="ebRef" :page-schema="state.pageSchema" />
      <template #footer>
        <a-button @click="handleReset">重置</a-button>
        <a-button type="primary" @click="submitProcessInstance">
          提交审批
        </a-button>
      </template>
    </a-modal>
  </div>
</template>
<style lang="less" scoped>
.workflow-create-page {
  min-height: 100%;
  padding: 16px;
  background-color: #f5f7fa;
}

// 快捷入口区域
.quick-actions-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

// 待审批卡片（左侧单独一块）
.pending-card {
  display: flex;
  gap: 12px;
  align-items: center;
  min-width: 240px;
  height: 90px;
  padding: 16px 28px;
  cursor: pointer;
  background: #fff;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background-color: #fafafa;
  }
}

.pending-info {
  display: flex;
  flex-direction: column;
}

.pending-count {
  font-size: 24px;
  font-weight: 600;
  line-height: 1.2;
}

.pending-title {
  font-size: 13px;
  color: #666;
  white-space: nowrap;
}

// 其他入口组（右侧）
.other-actions-group {
  display: flex;
  flex: 1;
  background: #fff;
  border-radius: 8px;
}

.other-action-item {
  display: flex;
  flex: 1;
  gap: 8px;
  align-items: center;
  justify-content: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #fafafa;
  }
}

.other-action-title {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
}

// 分类区域
.category-section {
  margin-bottom: 16px;
  overflow: hidden;
  background: #fff;
  border-radius: 8px;
}

.category-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  cursor: pointer;
  user-select: none;

  &:hover {
    background-color: #fafafa;
  }
}

.category-title {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.category-indicator {
  display: inline-block;
  width: 4px;
  height: 16px;
  background: #1890ff;
  border-radius: 2px;
}

.collapse-icon {
  font-size: 20px;
  color: #999;
}

// 流程卡片网格
.process-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  padding: 16px;

  @media (width <= 1400px) {
    grid-template-columns: repeat(4, 1fr);
  }

  @media (width <= 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (width <= 900px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (width <= 600px) {
    grid-template-columns: 1fr;
  }
}

.process-card {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    border-color: #1890ff;
    box-shadow: 0 2px 12px rgb(24 144 255 / 12%);
  }
}

.process-icon {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.process-info {
  flex: 1;
  min-width: 0;
}

.process-name {
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.process-key {
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}
</style>
