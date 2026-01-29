/**
 * 插件管理页面逻辑 Hook
 */
import type { FieldPluginVO, PluginPageQuery, PluginRegisterDTO } from '@/api/dynamic/model';
import {
  getPluginDetail,
  getPluginList,
  registerPlugin,
  unregisterPlugin,
} from '@/api/dynamic/plugin';

import { ref, reactive } from 'vue';

/**
 * 分页配置
 */
interface Pagination {
  current: number;
  pageSize: number;
  total: number;
}

/**
 * 搜索表单
 */
interface SearchForm {
  name?: string;
  type?: string;
  category?: string;
  status?: string;
}

/**
 * 插件管理页面 Hook 返回值
 */
interface UsePluginPageReturn {
  // 状态
  loading: ref<boolean>;
  plugins: ref<FieldPluginVO[]>;
  pagination: reactive<Pagination>;
  searchForm: reactive<SearchForm>;
  currentPlugin: ref<FieldPluginVO | null>;
  error: ref<string | null>;

  // 方法
  fetchPlugins: () => Promise<void>;
  changePage: (page: number, pageSize?: number) => Promise<void>;
  changeSearch: () => Promise<void>;
  resetSearch: () => Promise<void>;
  fetchPluginDetail: (type: string) => Promise<FieldPluginVO | null>;
  registerNewPlugin: (data: PluginRegisterDTO) => Promise<{ success: boolean }>;
  unregisterByType: (type: string) => Promise<{ success: boolean }>;
  clearCurrentPlugin: () => void;
}

/**
 * 插件管理页面 Hook
 * @returns 页面状态和方法
 */
export function usePluginPage(): UsePluginPageReturn {
  // 加载状态
  const loading = ref(false);

  // 插件列表
  const plugins = ref<FieldPluginVO[]>([]);

  // 分页配置
  const pagination = reactive<Pagination>({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  // 搜索表单
  const searchForm = reactive<SearchForm>({
    name: undefined,
    type: undefined,
    category: undefined,
    status: undefined,
  });

  // 当前选中的插件
  const currentPlugin = ref<FieldPluginVO | null>(null);

  // 错误信息
  const error = ref<string | null>(null);

  // 获取插件列表
  async function fetchPlugins() {
    loading.value = true;
    error.value = null;

    try {
      const params: PluginPageQuery = {
        current: pagination.current,
        size: pagination.pageSize,
        ...searchForm,
      };

      const response = await getPluginList(params);

      if (Array.isArray(response)) {
        plugins.value = response;
        pagination.total = response.length;
      } else if (response && 'records' in response) {
        plugins.value = (response as any).records;
        pagination.total = (response as any).total;
      }
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取插件列表失败';
      plugins.value = [];
    } finally {
      loading.value = false;
    }
  }

  // 切换分页
  async function changePage(page: number, pageSize?: number) {
    pagination.current = page;
    if (pageSize) {
      pagination.pageSize = pageSize;
    }
    await fetchPlugins();
  }

  // 搜索
  async function changeSearch() {
    pagination.current = 1;
    await fetchPlugins();
  }

  // 重置搜索
  async function resetSearch() {
    searchForm.name = undefined;
    searchForm.type = undefined;
    searchForm.category = undefined;
    searchForm.status = undefined;
    pagination.current = 1;
    await fetchPlugins();
  }

  // 获取插件详情
  async function fetchPluginDetail(type: string): Promise<FieldPluginVO | null> {
    try {
      const result = await getPluginDetail(type);
      currentPlugin.value = result;
      return result;
    } catch (e) {
      error.value = e instanceof Error ? e.message : '获取插件详情失败';
      return null;
    }
  }

  // 注册插件
  async function registerNewPlugin(data: PluginRegisterDTO): Promise<{ success: boolean }> {
    try {
      await registerPlugin(data);
      await fetchPlugins();
      return { success: true };
    } catch (e) {
      return { success: false };
    }
  }

  // 注销插件
  async function unregisterByType(type: string): Promise<{ success: boolean }> {
    try {
      await unregisterPlugin(type);
      await fetchPlugins();
      return { success: true };
    } catch (e) {
      return { success: false };
    }
  }

  // 清空当前插件
  function clearCurrentPlugin() {
    currentPlugin.value = null;
  }

  return {
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
    clearCurrentPlugin,
  };
}
