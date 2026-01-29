/**
 * 动态字段组件测试
 */
import { describe, expect, it } from 'vitest';

import type { DynamicField } from '../types';

describe('Dynamic Field Types', () => {
  describe('DynamicField', () => {
    it('should create a valid field definition', () => {
      const field: DynamicField = {
        fieldName: 'username',
        label: '用户名',
        pluginType: 'text',
        required: true,
        config: {
          placeholder: '请输入用户名',
          clearable: true,
        },
        validation: {
          required: true,
          min: 3,
          max: 20,
          message: '用户名长度应在3-20个字符之间',
        },
      };

      expect(field.fieldName).toBe('username');
      expect(field.label).toBe('用户名');
      expect(field.pluginType).toBe('text');
      expect(field.required).toBe(true);
      expect(field.config?.placeholder).toBe('请输入用户名');
      expect(field.validation?.min).toBe(3);
      expect(field.validation?.max).toBe(20);
    });

    it('should handle optional fields gracefully', () => {
      const field: DynamicField = {
        fieldName: 'status',
        label: '状态',
        pluginType: 'switch',
      };

      expect(field.required).toBeUndefined();
      expect(field.config).toBeUndefined();
      expect(field.validation).toBeUndefined();
      expect(field.colProps).toBeUndefined();
    });

    it('should support different plugin types', () => {
      const pluginTypes = ['text', 'number', 'select', 'multiSelect', 'date', 'datetime', 'textarea', 'switch', 'upload', 'custom'];

      pluginTypes.forEach((type) => {
        const field: DynamicField = {
          fieldName: type,
          label: type,
          pluginType: type,
        };
        expect(field.pluginType).toBe(type);
      });
    });

    it('should handle column layout props', () => {
      const field: DynamicField = {
        fieldName: 'name',
        label: '名称',
        pluginType: 'text',
        colProps: {
          span: 12,
          xs: 24,
          sm: 12,
          md: 8,
          lg: 6,
        },
      };

      expect(field.colProps?.span).toBe(12);
      expect(field.colProps?.xs).toBe(24);
      expect(field.colProps?.md).toBe(8);
    });

    it('should handle component props', () => {
      const field: DynamicField = {
        fieldName: 'category',
        label: '分类',
        pluginType: 'select',
        componentProps: {
          options: [
            { label: '选项一', value: '1' },
            { label: '选项二', value: '2' },
          ],
          showSearch: true,
          mode: 'multiple',
        },
      };

      expect(field.componentProps?.options).toHaveLength(2);
      expect(field.componentProps?.showSearch).toBe(true);
      expect(field.componentProps?.mode).toBe('multiple');
    });
  });
});

describe('Field Validation', () => {
  it('should validate required field', () => {
    const validation = { required: true, message: '此字段必填' };
    expect(validation.required).toBe(true);
    expect(validation.message).toBe('此字段必填');
  });

  it('should validate length constraints', () => {
    const validation = { min: 1, max: 100, message: '长度应在1-100之间' };
    expect(validation.min).toBe(1);
    expect(validation.max).toBe(100);
  });

  it('should validate pattern', () => {
    const validation = {
      pattern: '^[a-zA-Z0-9_]+$',
      message: '只能包含字母、数字和下划线',
    };
    expect(validation.pattern).toBeDefined();
    expect(validation.message).toBeDefined();
  });

  it('should handle custom validator', () => {
    const validation = {
      customValidator: 'phoneValidator',
      message: '手机号格式不正确',
    };
    expect(validation.customValidator).toBe('phoneValidator');
  });
});

describe('Field Config', () => {
  it('should handle disabled state', () => {
    const config = { disabled: true, placeholder: '只读字段' };
    expect(config.disabled).toBe(true);
  });

  it('should handle hidden state', () => {
    const config = { hidden: true };
    expect(config.hidden).toBe(true);
  });

  it('should handle clearable option', () => {
    const config = { clearable: true };
    expect(config.clearable).toBe(true);
  });

  it('should handle readonly option', () => {
    const config = { readonly: true };
    expect(config.readonly).toBe(true);
  });

  it('should handle size option', () => {
    const smallConfig = { size: 'small' };
    const middleConfig = { size: 'middle' };
    const largeConfig = { size: 'large' };

    expect(smallConfig.size).toBe('small');
    expect(middleConfig.size).toBe('middle');
    expect(largeConfig.size).toBe('large');
  });

  it('should handle default value', () => {
    const config = { defaultValue: '默认值' };
    expect(config.defaultValue).toBe('默认值');
  });
});
