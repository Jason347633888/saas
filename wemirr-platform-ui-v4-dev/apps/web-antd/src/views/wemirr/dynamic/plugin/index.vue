<script setup lang="ts">
/**
 * 插件管理页面
 */
import type { FieldPluginVO, PluginRegisterDTO } from '@/api/dynamic';

import { ref } from 'vue';

import PluginCard from './components/PluginCard.vue';
import PluginDetailModal from './components/PluginDetailModal.vue';
import PluginRegisterForm from './components/PluginRegisterForm.vue';
import { usePluginPage } from './usePluginPage';

const {
  loading,
  plugins,
  pagination,
  searchForm,
  currentPlugin,
  error,
  fetchPlugins,
  changePage,
  changeSearch,
  resetSearch,
  fetchPluginDetail,
  registerNewPlugin,
  unregisterByType,
} = usePluginPage();

// 弹窗状态
const detailModalVisible = ref(false);
const registerModalVisible = ref(false);
const registerMode = ref<'create' | 'edit'>('create');

// 当前选中的插件
const selectedPlugin = ref<FieldPluginVO | null>(null);

// 搜索
async function handleSearch() {
  await changeSearch();
}

// 重置搜索
async function handleReset() {
  await resetSearch();
}

// 分页变化
async function handlePageChange(page: number, pageSize: number) {
  await changePage(page, pageSize);
}

// 查看插件详情
async function handleViewPlugin(plugin: FieldPluginVO) {
  selectedPlugin.value = plugin;
  detailModalVisible.value = true;
}

// 编辑插件
function handleEditPlugin(plugin: FieldPluginVO) {
  selectedPlugin.value = plugin;
  registerMode.value = 'edit';
  registerModalVisible.value = true;
}

// 删除插件
async function handleDeletePlugin(plugin: FieldPluginVO) {
  // 确认删除
  const confirmed = await import('ant-design-vue').then(
    ({ Modal }) =>
      new Promise<boolean>((resolve) => {
        Modal.confirm({
          title: '确认删除',
          content: `确定要删除插件 "${plugin.name}" 吗？此操作不可恢复。`,
          okText: '确认删除',
          okType: 'danger',
          cancelText: '取消',
          onOk: () => resolve(true),
          onCancel: () => resolve(false),
        });
      })
  );

  if (confirmed) {
    const result = await unregisterByType(plugin.type);
    if (!result.success) {
      import('ant-design-vue').then(({ message }) => {
        message.error('删除插件失败');
      });
    } else {
      import('ant-design-vue').then(({ message }) => {
        message.success('删除插件成功');
      });
    }
  }
}

// 注册新插件
async function handleRegisterPlugin(data: PluginRegisterDTO) {
  const result = await registerNewPlugin(data);
  if (result.success) {
    registerModalVisible.value = false;
    import('ant-design-vue').then(({ message }) => {
      message.success('注册插件成功');
    });
  } else {
    import('ant-design-vue').then(({ message }) => {
      message.error('注册插件失败');
    });
  }
}

// 关闭详情弹窗
function handleCloseDetail() {
  selectedPlugin.value = null;
}

// 打开注册弹窗
function openRegisterModal() {
  selectedPlugin.value = null;
  registerMode.value = 'create';
  registerModalVisible.value = true;
}

// 页面加载时获取数据
import { onMounted } from 'vue';
onMounted(() => {
  fetchPlugins();
});
</script>

<template>
  <div class="plugin-page">
    <!-- 页面头部 -->
    <a-page-header
      title="动态字段插件管理"
      sub-title="管理系统中的动态表单字段插件"
      @back="() => $router.back()"
    >
      <template #extra>
        <a-button type="primary" @click="openRegisterModal">
          <template #icon><PlusOutlined /></template>
          注册插件
        </a-button>
      </template>
    </a-page-header>

    <!-- 搜索区域 -->
    <a-card class="search-card">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="插件名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入插件名称"
            allow-clear
            @press-enter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="插件类型">
          <a-input
            v-model:value="searchForm.type"
            placeholder="请输入插件类型"
            allow-clear
            @press-enter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option value="enabled">已启用</a-select-option>
            <a-select-option value="disabled">已禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              搜索
            </a-button>
            <a-button @click="handleReset">
              <template #icon><ReloadOutlined /></template>
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 插件列表 -->
    <a-card title="插件列表">
      <template #extra>
        <span>共 {{ pagination.total }} 个插件</span>
      </template>

      <a-spin :spinning="loading">
        <a-empty v-if="plugins.length === 0 && !loading" description="暂无插件" />
        <a-row v-else :gutter="[16, 16]">
          <a-col
            v-for="plugin in plugins"
            :key="plugin.id"
            :xs="24"
            :sm="12"
            :md="12"
            :lg="8"
            :xl="6"
          >
            <PluginCard
              :plugin="plugin"
              @click="handleViewPlugin(plugin)"
              @view="handleViewPlugin(plugin)"
              @edit="handleEditPlugin(plugin)"
              @delete="handleDeletePlugin"
            />
          </a-col>
        </a-row>
      </a-spin>

      <!-- 分页 -->
      <a-pagination
        v-if="pagination.total > 0"
        v-model:current="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        show-size-changer
        show-quick-jumper
        show-total
        style="margin-top: 16px; text-align: right"
        @change="handlePageChange"
      />
    </a-card>

    <!-- 详情弹窗 -->
    <PluginDetailModal
      :open="detailModalVisible"
      :plugin="selectedPlugin"
      @update:open="(val) => (detailModalVisible = val)"
      @edit="handleEditPlugin"
      @close="handleCloseDetail"
    />

    <!-- 注册弹窗 -->
    <a-modal
      v-model:open="registerModalVisible"
      :title="registerMode === 'create' ? '注册插件' : '编辑插件'"
      width="700px"
      :footer="null"
      destroy-on-close
    >
      <PluginRegisterForm
        :initial-data="selectedPlugin || {}"
        :mode="registerMode"
        @submit="handleRegisterPlugin"
        @cancel="registerModalVisible = false"
      />
    </a-modal>
  </div>
</template>

<style lang="less" scoped>
.plugin-page {
  .search-card {
    margin: 16px 0;
  }

  .ant-card {
    margin-bottom: 16px;
  }
}
</style>
