import { defHttp } from '#/api/request';
import {
  createTimeReadonlyColumn,
  hiddenIdColumn,
  statusDict,
} from '#/plugin/fast-crud/shared';

export default function crud() {
  return {
    crudOptions: {
      table: {},
      request: {
        pageRequest: async (query: any) =>
          await defHttp.get(`/workflow/flow-categories`, { params: query }),
        addRequest: async ({ form }: any) =>
          await defHttp.post(`/workflow/flow-categories`, form),
        editRequest: async ({ form }: any) =>
          await defHttp.put(`/workflow/flow-categories/${form.id}`, form),
        delRequest: async ({ row }: any) =>
          await defHttp.delete(`/workflow/flow-categories/${row.id}`),
      },
      toolbar: {},
      rowHandle: {},
      columns: {
        id: hiddenIdColumn,
        icon: {
          title: 'ICON',
          type: 'icon',
          column: {
            width: 100,
            align: 'center',
          },
          form: {
            component: {
              dialog: { title: 'ICON 选择' },
              tabs: { type: 'line' },
            },
            rules: [{ required: true, message: 'ICON 不能为空' }],
            helper: {
              render() {
                return (
                  <a
                    href={'https://iconify.design/icon-sets/ion/'}
                    target={'_blank'}
                  >
                    无满意的 ICON ? 请点我
                  </a>
                );
              },
            },
          },
        },
        name: {
          title: '名称',
          type: 'text',
          column: { width: 200 },
          search: { show: true },
          form: {
            rules: [{ required: true, message: '名称不能为空' }],
          },
        },
        status: {
          title: '状态',
          type: 'dict-radio',
          column: { width: 100, align: 'center' },
          search: { show: true },
          dict: statusDict(),
          addForm: { value: true },
        },
        description: {
          title: '描述',
          search: { show: false },
          column: { ellipsis: true },
          type: ['textarea'],
          form: {
            rules: [{ required: true, message: '描述不能为空' }],
            col: {
              span: 24,
            },
          },
        },
        createName: {
          title: '创建人',
          search: { show: false },
          type: 'text',
          addForm: { show: false },
          editForm: { show: false },
          column: { width: 150, ellipsis: true },
        },
        createTime: createTimeReadonlyColumn,
      },
    },
  };
}
