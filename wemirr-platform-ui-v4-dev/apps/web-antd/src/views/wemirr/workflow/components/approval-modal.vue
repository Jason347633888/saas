<!-- 审批同意的弹窗 -->
<script setup lang="ts">
import type { User } from '#/api/system/user/model';
import type {
  CompleteTaskReqData,
  NextNodeInfo,
} from '#/api/workflow/task/model';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';
import { cloneDeep } from '@vben/utils';

import {
  CheckCircleOutlined,
  TeamOutlined,
  UserSwitchOutlined,
} from '@ant-design/icons-vue';
import { Divider, message } from 'ant-design-vue';
import { omit } from 'lodash-es';

import { useVbenForm } from '#/adapter/form';
import { completeTask, getNextNodeList } from '#/api/workflow/task';

import { CopyComponent } from '.';

const emit = defineEmits<{ complete: [] }>();

const [BasicForm, formApi] = useVbenForm({
  commonConfig: {
    formItemClass: 'col-span-2',
    labelWidth: 100,
    componentProps: {
      class: 'w-full',
    },
  },
  schema: [
    {
      fieldName: 'taskId',
      component: 'Input',
      label: '任务ID',
      dependencies: {
        show: false,
        triggerFields: [''],
      },
    },
    {
      fieldName: 'messageType',
      component: 'CheckboxGroup',
      componentProps: {
        options: [
          { label: '站内信', value: '1', disabled: true },
          { label: '邮件', value: '2' },
          { label: '短信', value: '3' },
        ],
      },
      label: '通知方式',
      defaultValue: ['1'],
    },
    {
      fieldName: 'attachment',
      component: 'FileUpload',
      componentProps: {
        maxCount: 10,
        maxSize: 20,
        accept: 'png, jpg, jpeg, doc, docx, xlsx, xls, ppt, pdf',
      },
      defaultValue: [],
      label: '附件上传',
      formItemClass: 'items-start',
    },
    {
      fieldName: 'flowCopyList',
      component: 'Input',
      defaultValue: [],
      label: '抄送人',
    },
    {
      fieldName: 'assigneeMap',
      component: 'Input',
      label: '下一步审批人',
    },
    {
      fieldName: 'message',
      component: 'Textarea',
      componentProps: {
        rows: 4,
        placeholder: '请输入审批意见...',
        showCount: true,
        maxlength: 500,
      },
      label: '审批意见',
      formItemClass: 'items-start',
    },
  ],
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2',
});

interface ModalProps {
  taskId: string;
  // 是否具有抄送权限
  copyPermission: boolean;
  // 是有具有选人权限
  assignPermission: boolean;
}

// 自定义添加选人属性 给组件v-for绑定
const nextNodeInfo = ref<(NextNodeInfo & { selectUserList: User[] })[]>([]);
const [BasicModal, modalApi] = useVbenModal({
  title: '审批通过',
  fullscreenButton: false,
  class: 'approval-modal min-h-[420px]',
  onConfirm: handleSubmit,
  async onOpenChange(isOpen) {
    if (!isOpen) {
      await formApi.resetForm();
      return null;
    }
    modalApi.modalLoading(true);

    const { taskId, copyPermission, assignPermission } =
      modalApi.getData() as ModalProps;
    // 是否显示抄送选择
    formApi.updateSchema([
      {
        fieldName: 'flowCopyList',
        dependencies: {
          if: copyPermission,
          triggerFields: [''],
        },
      },
      {
        fieldName: 'assigneeMap',
        dependencies: {
          if: assignPermission,
          triggerFields: [''],
        },
      },
    ]);

    // 获取下一节点名称
    if (assignPermission) {
      const resp = await getNextNodeList({ taskId });
      nextNodeInfo.value = resp.map((item) => ({
        ...item,
        // 用于给组件绑定
        selectUserList: [],
      }));
    }

    await formApi.setFieldValue('taskId', taskId);

    modalApi.modalLoading(false);
  },
});

async function handleSubmit() {
  try {
    modalApi.modalLoading(true);
    const { valid } = await formApi.validate();
    if (!valid) {
      return;
    }
    const data = cloneDeep(await formApi.getValues());
    // 需要转换数据 抄送人员
    const flowCopyList = (data.flowCopyList as Array<any>).map((item) => ({
      userId: item.userId,
      userName: item.nickname,
    }));
    const requestData = {
      ...omit(data, ['attachment']),
      fileId: data.attachment.join(','),
      taskVariables: {},
      variables: {},
      flowCopyList,
    } as CompleteTaskReqData;

    // 选人
    if (modalApi.getData()?.assignPermission) {
      // 判断是否选中
      for (const item of nextNodeInfo.value) {
        if (item.selectUserList.length === 0) {
          message.warn(`未选择节点[${item.nodeName}]审批人`);
          return;
        }
      }

      const assigneeMap: { [key: string]: string } = {};
      nextNodeInfo.value.forEach((item) => {
        assigneeMap[item.nodeCode] = item.selectUserList
          .map((u) => u.userId)
          .join(',');
      });
      requestData.assigneeMap = assigneeMap;
    }

    await completeTask(requestData);
    modalApi.close();
    emit('complete');
  } catch (error) {
    console.error(error);
  } finally {
    modalApi.modalLoading(false);
  }
}
</script>

<template>
  <BasicModal>
    <div class="approval-modal-content">
      <!-- 顶部成功提示区域 -->
      <div class="approval-header">
        <div class="approval-header-icon">
          <CheckCircleOutlined class="text-2xl text-green-500" />
        </div>
        <div class="approval-header-text">
          <span class="text-base font-medium">确认通过此审批</span>
          <span class="mt-1 text-xs text-muted-foreground">
            审批通过后将流转至下一节点
          </span>
        </div>
      </div>

      <Divider class="my-4" />

      <!-- 表单区域 -->
      <div class="approval-section">
        <BasicForm>
          <!-- 抄送人区域 -->
          <template #flowCopyList="slotProps">
            <div class="copy-user-area">
              <div class="copy-user-hint">
                <TeamOutlined class="text-orange-400" />
                <span class="ml-2 text-xs text-muted-foreground">
                  选择需要抄送的人员，他们将收到审批结果通知
                </span>
              </div>
              <CopyComponent v-model:user-list="slotProps.modelValue" />
            </div>
          </template>

          <!-- 下一步审批人区域 -->
          <template #assigneeMap>
            <div class="next-approver-area">
              <div
                v-for="item in nextNodeInfo"
                :key="item.nodeCode"
                class="approver-item"
              >
                <template v-if="item.permissionFlag">
                  <div class="approver-node">
                    <UserSwitchOutlined class="text-purple-500" />
                    <span class="node-name">{{ item.nodeName }}</span>
                  </div>
                  <CopyComponent
                    v-model:user-list="item.selectUserList"
                    :allow-user-ids="item.permissionFlag"
                  />
                </template>
                <template v-else>
                  <div class="no-permission">
                    <span class="text-red-500"> 没有权限，请联系管理员 </span>
                  </div>
                </template>
              </div>
            </div>
          </template>
        </BasicForm>
      </div>
    </div>
  </BasicModal>
</template>

<style lang="scss" scoped>
.approval-modal-content {
  padding: 8px 4px;
}

.approval-header {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 16px;
  background: linear-gradient(
    135deg,
    rgb(240 253 244) 0%,
    rgb(236 253 245) 100%
  );
  border-radius: 12px;

  .approval-header-icon {
    display: flex;
    flex-shrink: 0;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    background: white;
    border-radius: 50%;
    box-shadow: 0 2px 8px rgb(34 197 94 / 20%);
  }

  .approval-header-text {
    display: flex;
    flex-direction: column;
  }
}

.approval-section {
  padding: 4px 0;
}

.copy-user-area {
  .copy-user-hint {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
  }
}

.next-approver-area {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .approver-item {
    display: flex;
    gap: 12px;
    align-items: center;
    padding: 12px;
    background: var(--accent);
    border-radius: 8px;
  }

  .approver-node {
    display: flex;
    gap: 8px;
    align-items: center;
    min-width: 120px;

    .node-name {
      font-size: 13px;
      font-weight: 500;
      color: var(--foreground);
    }
  }

  .no-permission {
    padding: 8px 12px;
    background: rgb(254 242 242);
    border-radius: 6px;
  }
}

:deep(.ant-divider) {
  margin: 16px 0;
}

:deep(.ant-form-item) {
  margin-bottom: 20px;
}

:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: var(--foreground);
}
</style>
