package com.ai.plugin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 插件定义DTO
 * 用于API返回插件信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginDefinitionDTO {

    /**
     * 字段类型标识
     */
    private String type;

    /**
     * 显示名称
     */
    private String name;

    /**
     * 图标名称
     */
    private String icon;

    /**
     * 前端组件类型
     */
    private String componentType;

    /**
     * 配置模式
     */
    private Map<String, Object> configSchema;

    /**
     * 默认配置
     */
    private Map<String, Object> defaultConfig;

    /**
     * 支持的选项
     */
    private List<Map<String, String>> options;

    /**
     * 是否支持多选
     */
    private boolean multipleSupported;
}
