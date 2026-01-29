<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { useFs } from '@fast-crud/fast-crud';

import DiagramPreview from '#/views/wemirr/workflow/task/complete/DiagramPreview.vue';

import createCrudOptions from './crud';
import FormDesignDialog from './FormDesignDialog.vue';

const diagramRef = ref();
const formDesignRef = ref();
const { crudRef, crudBinding, crudExpose } = useFs({
  createCrudOptions,
  context: { diagramRef, formDesignRef },
});

onMounted(async () => {
  await crudExpose.doRefresh();
});
</script>

<template>
  <fs-page class="page-layout-card">
    <fs-crud ref="crudRef" v-bind="crudBinding" />
    <DiagramPreview ref="diagramRef" />
    <FormDesignDialog ref="formDesignRef" />
  </fs-page>
</template>
