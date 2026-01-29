import type {
  AddReq,
  CreateCrudOptionsProps,
  CreateCrudOptionsRet,
  DelReq,
  EditReq,
  ValueBuilderContext,
  ValueResolveContext,
} from '@fast-crud/fast-crud';

import { dict } from '@fast-crud/fast-crud';

import {
  createTimeColumn,
  hiddenIdColumn,
} from '#/plugin/fast-crud/shared';

import * as api from './api';

// ==================== 字典定义 ====================

/** 模型提供商 */
const providerDict = dict({
  data: [
    { value: 'OpenAI', label: 'OpenAI' },
    { value: 'DeepSeek', label: '深度求索' },
    { value: 'Anthropic', label: 'Anthropic' },
    { value: 'Google', label: 'Google' },
    { value: 'Azure', label: 'Azure' },
    { value: 'Ollama', label: 'Ollama' },
  ],
});

/** 模型类型 */
const typeDict = dict({
  data: [
    { value: 'TEXT', label: '文本' },
    { value: 'EMBEDDING', label: '向量' },
    { value: 'IMAGE', label: '图像' },
    { value: 'AUDIO', label: '音频' },
  ],
});

// ==================== CRUD 配置 ====================

export default function crud(
  _props: CreateCrudOptionsProps,
): CreateCrudOptionsRet {
  return {
    crudOptions: {
      request: {
        pageRequest: async (query: any) => await api.PageList(query),
        addRequest: async ({ form }: AddReq) => await api.AddObj(form),
        editRequest: async ({ form }: EditReq) =>
          await api.UpdateObj(form.id, form),
        delRequest: async ({ row }: DelReq) => await api.DelObj(row.id),
      },
      columns: {
        id: hiddenIdColumn,
        name: {
          title: '模型名称',
          type: 'text',
          search: { show: true },
          column: { width: 200, ellipsis: true },
          form: { rules: [{ required: true, message: '请输入模型名称' }] },
        },
        provider: {
          title: '提供商',
          type: 'dict-select',
          dict: providerDict,
          search: { show: true },
          column: { width: 150, ellipsis: true },
          form: { rules: [{ required: true, message: '请选择提供商' }] },
        },
        type: {
          title: '模型类型',
          type: 'dict-select',
          dict: typeDict,
          search: { show: true },
          column: { width: 120, ellipsis: true },
          form: { rules: [{ required: true, message: '请选择模型类型' }] },
        },
        apiKey: {
          title: 'API密钥',
          type: 'textarea',
          column: { width: 120, ellipsis: true },
          form: {
            col: { span: 24 },
            rules: [{ required: true, message: '请输入API密钥' }],
          },
        },
        baseUrl: {
          title: '基础URL',
          type: 'textarea',
          column: { width: 120, ellipsis: true },
          form: { col: { span: 24 } },
        },
        extraConfig: {
          title: '扩展配置',
          type: 'json',
          column: { show: false },
          form: {
            col: { span: 24 },
            helper: '可选，JSON 格式，如 {"temperature": 0.7}',
            valueBuilder({ form }: ValueBuilderContext) {
              if (form.extraConfig === null || form.extraConfig === undefined)
                return;
              try {
                if (typeof form.extraConfig === 'string') {
                  form.extraConfig = JSON.parse(form.extraConfig);
                }
              } catch (e) {
                console.error('JSON parse error', e);
              }
            },
            valueResolve({ form }: ValueResolveContext) {
              if (form.extraConfig === null || form.extraConfig === undefined)
                return;
              try {
                if (typeof form.extraConfig !== 'string') {
                  form.extraConfig = JSON.stringify(form.extraConfig);
                }
              } catch (e) {
                console.error('JSON stringify error', e);
              }
            },
          },
        },
        createName: {
          title: '创建人',
          type: 'text',
          column: { width: 120, ellipsis: true },
          form: { show: false },
        },
        createTime: createTimeColumn,
      },
      rowHandle: {
        fixed: 'right',
        width: 160,
        buttons: {
          edit: { show: true },
          remove: { show: true },
        },
      },
    },
  };
}
