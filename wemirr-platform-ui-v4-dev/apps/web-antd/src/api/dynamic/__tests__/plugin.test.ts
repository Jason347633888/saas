/**
 * 动态表单插件 API 测试
 */
import { beforeEach, describe, expect, it, vi } from 'vitest';

import * as pluginApi from '../plugin';

// Mock request client
vi.mock('#/api/request', () => ({
  requestClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe('Dynamic Plugin API', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getPluginList', () => {
    it('should fetch plugin list with default params', async () => {
      const mockPlugins = [
        { id: '1', type: 'text', name: '文本字段', version: '1.0.0' },
        { id: '2', type: 'number', name: '数值字段', version: '1.0.0' },
      ];

      const { requestClient } = await import('#/api/request');
      (requestClient.get as vi.Mock).mockResolvedValue(mockPlugins);

      const result = await pluginApi.getPluginList();

      expect(requestClient.get).toHaveBeenCalledWith(
        '/dynamic/plugins',
        { params: undefined }
      );
      expect(result).toEqual(mockPlugins);
    });

    it('should fetch plugin list with pagination params', async () => {
      const mockPlugins = {
        records: [{ id: '1', type: 'text', name: '文本字段' }],
        total: 1,
      };

      const { requestClient } = await import('#/api/request');
      (requestClient.get as vi.Mock).mockResolvedValue(mockPlugins);

      const params = { current: 1, size: 10 };
      const result = await pluginApi.getPluginList(params);

      expect(requestClient.get).toHaveBeenCalledWith(
        '/dynamic/plugins',
        { params }
      );
      expect(result).toEqual(mockPlugins);
    });

    it('should handle empty plugin list', async () => {
      const { requestClient } = await import('#/api/request');
      (requestClient.get as vi.Mock).mockResolvedValue([]);

      const result = await pluginApi.getPluginList();

      expect(result).toEqual([]);
    });
  });

  describe('getPluginDetail', () => {
    it('should fetch plugin detail by type', async () => {
      const mockPlugin = {
        id: '1',
        type: 'text',
        name: '文本字段',
        version: '1.0.0',
        schema: {
          properties: {
            maxLength: { type: 'number' },
          },
        },
      };

      const { requestClient } = await import('#/api/request');
      (requestClient.get as vi.Mock).mockResolvedValue(mockPlugin);

      const result = await pluginApi.getPluginDetail('text');

      expect(requestClient.get).toHaveBeenCalledWith(
        '/dynamic/plugins/text'
      );
      expect(result).toEqual(mockPlugin);
    });

    it('should throw error for invalid plugin type', async () => {
      const { requestClient } = await import('#/api/request');
      (requestClient.get as vi.Mock).mockRejectedValue(
        new Error('Plugin not found')
      );

      await expect(pluginApi.getPluginDetail('invalid')).rejects.toThrow(
        'Plugin not found'
      );
    });
  });

  describe('validatePluginValue', () => {
    it('should validate plugin value successfully', async () => {
      const mockResult = { valid: true };

      const { requestClient } = await import('#/api/request');
      (requestClient.post as vi.Mock).mockResolvedValue(mockResult);

      const data = { type: 'text', value: 'test' };
      const result = await pluginApi.validatePluginValue(data);

      expect(requestClient.post).toHaveBeenCalledWith(
        '/dynamic/plugins/validate',
        data
      );
      expect(result).toEqual(mockResult);
    });

    it('should return invalid result for invalid value', async () => {
      const mockResult = { valid: false, message: 'Value exceeds max length' };

      const { requestClient } = await import('#/api/request');
      (requestClient.post as vi.Mock).mockResolvedValue(mockResult);

      const data = { type: 'text', value: 'a'.repeat(1000) };
      const result = await pluginApi.validatePluginValue(data);

      expect(result.valid).toBe(false);
      expect(result.message).toBe('Value exceeds max length');
    });

    it('should handle validation error gracefully', async () => {
      const { requestClient } = await import('#/api/request');
      (requestClient.post as vi.Mock).mockRejectedValue(new Error('Network error'));

      const data = { type: 'text', value: 'test' };
      await expect(pluginApi.validatePluginValue(data)).rejects.toThrow('Network error');
    });
  });

  describe('processPluginValue', () => {
    it('should process plugin value and return transformed value', async () => {
      const mockResult = { value: 'processed_value' };

      const { requestClient } = await import('#/api/request');
      (requestClient.post as vi.Mock).mockResolvedValue(mockResult);

      const data = { type: 'text', value: 'raw_value' };
      const result = await pluginApi.processPluginValue(data);

      expect(requestClient.post).toHaveBeenCalledWith(
        '/dynamic/plugins/process',
        data
      );
      expect(result).toEqual(mockResult);
    });

    it('should handle complex data transformations', async () => {
      const mockResult = {
        value: { key: 'processed', nested: { data: true } }
      };

      const { requestClient } = await import('#/api/request');
      (requestClient.post as vi.Mock).mockResolvedValue(mockResult);

      const data = { type: 'object', value: { raw: true } };
      const result = await pluginApi.processPluginValue(data);

      expect(result.value).toEqual({ key: 'processed', nested: { data: true } });
    });
  });

  describe('registerPlugin', () => {
    it('should register a new plugin', async () => {
      const { requestClient } = await import('#/api/request');
      (requestClient.post as vi.Mock).mockResolvedValue({ success: true });

      const data = {
        type: 'custom',
        name: '自定义字段',
        version: '1.0.0',
        schema: {},
      };
      const result = await pluginApi.registerPlugin(data);

      expect(requestClient.post).toHaveBeenCalledWith(
        '/dynamic/plugins/register',
        data
      );
      expect(result).toEqual({ success: true });
    });

    it('should handle registration error', async () => {
      const { requestClient } = await import('#/api/request');
      (requestClient.post as vi.Mock).mockRejectedValue(
        new Error('Registration failed')
      );

      const data = { type: 'custom', name: '自定义字段' };
      await expect(pluginApi.registerPlugin(data)).rejects.toThrow(
        'Registration failed'
      );
    });
  });

  describe('unregisterPlugin', () => {
    it('should unregister a plugin by type', async () => {
      const { requestClient } = await import('#/api/request');
      (requestClient.delete as vi.Mock).mockResolvedValue({ success: true });

      const result = await pluginApi.unregisterPlugin('custom');

      expect(requestClient.delete).toHaveBeenCalledWith(
        '/dynamic/plugins/custom'
      );
      expect(result).toEqual({ success: true });
    });

    it('should handle unregistration error', async () => {
      const { requestClient } = await import('#/api/request');
      (requestClient.delete as vi.Mock).mockRejectedValue(
        new Error('Plugin in use')
      );

      await expect(pluginApi.unregisterPlugin('in-use')).rejects.toThrow(
        'Plugin in use'
      );
    });
  });
});
