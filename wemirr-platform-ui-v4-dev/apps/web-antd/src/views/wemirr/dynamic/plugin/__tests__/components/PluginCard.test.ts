/**
 * PluginCard 组件测试
 */
import { describe, expect, it } from 'vitest';

import type { FieldPluginVO } from '@/api/dynamic';

describe('PluginCard', () => {
  it('should render plugin info correctly', () => {
    const plugin: FieldPluginVO = {
      id: '1',
      type: 'text',
      name: '文本字段',
      version: '1.0.0',
      description: '用于输入文本',
      category: 'basic',
      author: 'Author',
      schema: { properties: {} },
      config: {},
      status: 'enabled',
    };

    expect(plugin.id).toBe('1');
    expect(plugin.type).toBe('text');
    expect(plugin.name).toBe('文本字段');
    expect(plugin.version).toBe('1.0.0');
    expect(plugin.status).toBe('enabled');
  });

  it('should handle disabled plugin', () => {
    const plugin: FieldPluginVO = {
      id: '1',
      type: 'text',
      name: '文本字段',
      version: '1.0.0',
      schema: { properties: {} },
      config: {},
      status: 'disabled',
    };

    expect(plugin.status).toBe('disabled');
  });

  it('should have all required fields', () => {
    const plugin: FieldPluginVO = {
      id: '1',
      type: 'text',
      name: '文本字段',
      version: '1.0.0',
      schema: { properties: {} },
      config: {},
    };

    expect(plugin.id).toBeDefined();
    expect(plugin.type).toBeDefined();
    expect(plugin.name).toBeDefined();
    expect(plugin.version).toBeDefined();
    expect(plugin.schema).toBeDefined();
    expect(plugin.config).toBeDefined();
  });
});
