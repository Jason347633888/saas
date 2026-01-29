package com.ai.service;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import com.ai.plugin.dto.FieldConfigDTO;
import com.ai.plugin.dto.PluginDefinitionDTO;

import java.util.List;
import java.util.Optional;

/**
 * 字段插件服务接口
 */
public interface FieldPluginService {

    /**
     * 获取所有插件
     *
     * @return 插件定义列表
     */
    List<PluginDefinitionDTO> getAllPlugins();

    /**
     * 根据类型获取插件
     *
     * @param type 插件类型
     * @return 插件定义
     */
    Optional<PluginDefinitionDTO> getPluginByType(String type);

    /**
     * 校验字段值
     *
     * @param config 字段配置
     * @return 校验结果
     */
    ValidationResult validateField(FieldConfigDTO config);

    /**
     * 处理字段值
     *
     * @param config 字段配置
     * @return 处理后的值
     */
    Object processFieldValue(FieldConfigDTO config);

    /**
     * 注册插件
     *
     * @param plugin 插件实例
     */
    void registerPlugin(FieldTypePlugin plugin);

    /**
     * 注销插件
     *
     * @param type 插件类型
     */
    void unregisterPlugin(String type);
}
