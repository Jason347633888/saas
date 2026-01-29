<script setup lang="ts">
import type { VbenFormSchema } from '#/adapter/form';

import { computed, ref } from 'vue';

import { ProfilePasswordSetting, z } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { defHttp } from '#/api/request';

const profilePasswordSettingRef = ref();

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      fieldName: 'oldPassword',
      label: '当前密码',
      component: 'VbenInputPassword',
      componentProps: {
        placeholder: '请输入当前密码',
      },
      rules: z.string().min(1, '请输入当前密码'),
    },
    {
      fieldName: 'newPassword',
      label: '新密码',
      component: 'VbenInputPassword',
      componentProps: {
        passwordStrength: true,
        placeholder: '请输入新密码（至少6位）',
      },
      rules: z.string().min(6, '密码至少6位'),
    },
    {
      fieldName: 'confirmPassword',
      label: '确认密码',
      component: 'VbenInputPassword',
      componentProps: {
        placeholder: '请再次输入新密码',
      },
      rules: z.string().min(1, '请再次输入新密码'),
      dependencies: {
        rules(values) {
          const { newPassword } = values;
          return z
            .string()
            .min(1, '请再次输入新密码')
            .refine((value) => value === newPassword, {
              message: '两次输入的密码不一致',
            });
        },
        triggerFields: ['newPassword'],
      },
    },
  ];
});

/** 提交修改密码 */
async function handleSubmit(values: Record<string, any>) {
  try {
    await defHttp.put('/iam/users/password', {
      oldPassword: values.oldPassword,
      newPassword: values.newPassword,
    });
    message.success('密码修改成功，请重新登录');
    // 清空表单
    profilePasswordSettingRef.value?.getFormApi()?.resetForm();
  } catch {
    message.error('密码修改失败');
  }
}
</script>

<template>
  <ProfilePasswordSetting
    ref="profilePasswordSettingRef"
    class="w-full min-w-[420px] max-w-[820px]"
    :form-schema="formSchema"
    @submit="handleSubmit"
  />
</template>
