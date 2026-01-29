<script lang="ts" setup>
import { reactive, ref } from 'vue';

import { useUi } from '@fast-crud/ui-interface';
import { Button } from 'ant-design-vue';

import FormDesign from './form-design.vue';

const { ui } = useUi();
const formDesignRef = ref<InstanceType<typeof FormDesign>>();

const state = reactive({
  defId: '' as string,
  dialogTitle: '表单设计',
  dialogShow: false,
});

/**
 * 打开弹窗
 */
function open(row: { flowName?: string; id: string }) {
  state.defId = row.id;
  state.dialogTitle = row.flowName ? `表单设计 - ${row.flowName}` : '表单设计';
  state.dialogShow = true;
}

/**
 * 保存成功回调
 */
function handleSaved() {
  state.dialogShow = false;
}

/**
 * 点击保存按钮
 */
function handleSave() {
  formDesignRef.value?.save();
}

/**
 * 点击取消按钮
 */
function handleCancel() {
  state.dialogShow = false;
}

defineExpose({
  open,
});
</script>

<template>
  <component
    :is="ui.drawer.name"
    v-if="state.dialogShow"
    v-model:[ui.dialog.visible]="state.dialogShow"
    :title="state.dialogTitle"
    width="100%"
  >
    <div class="form-design-dialog-container">
      <div class="form-design-content">
        <FormDesign
          ref="formDesignRef"
          :def-id="state.defId"
          @saved="handleSaved"
        />
      </div>
      <div class="form-design-footer">
        <Button @click="handleCancel">取消</Button>
        <Button type="primary" @click="handleSave">保存</Button>
      </div>
    </div>
  </component>
</template>
<style lang="scss">
.ant-drawer-body {
  padding: 0 !important;
}

.form-design-dialog-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.form-design-content {
  flex: 1;
  overflow: hidden;
}

.form-design-footer {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: flex-end;
  height: 60px;
  padding: 0 24px;
  border-top: 1px solid #f0f0f0;
}
</style>
