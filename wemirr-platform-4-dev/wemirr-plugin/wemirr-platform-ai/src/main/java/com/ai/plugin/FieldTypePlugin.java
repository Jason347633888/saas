package com.ai.plugin;

import java.util.List;
import java.util.Map;

/**
 * 字段类型插件接口
 * 定义动态表单字段类型的统一接口
 */
public interface FieldTypePlugin {

    /**
     * 获取字段类型标识
     * 唯一标识字段类型，如：text、number、date等
     */
    String getType();

    /**
     * 获取显示名称
     * 如：文本、数值、日期等
     */
    String getName();

    /**
     * 获取图标名称
     * 用于前端显示图标
     */
    String getIcon();

    /**
     * 获取前端组件类型
     * 如：Input、InputNumber、DatePicker等
     */
    String getComponentType();

    /**
     * 获取配置模式
     * 定义该字段类型支持的配置项
     */
    Map<String, Object> getConfigSchema();

    /**
     * 获取默认配置
     * 默认配置值
     */
    default Map<String, Object> getDefaultConfig() {
        return Map.of();
    }

    /**
     * 校验字段值
     *
     * @param value  字段值
     * @param config 字段配置
     * @return 校验结果
     */
    default ValidationResult validate(Object value, Map<String, Object> config) {
        return ValidationResult.success();
    }

    /**
     * 处理字段值
     * 如类型转换、格式化等
     *
     * @param value  字段值
     * @param config 字段配置
     * @return 处理后的值
     */
    default Object processValue(Object value, Map<String, Object> config) {
        return value;
    }

    /**
     * 获取支持的选项
     * 适用于Select等支持选项的字段类型
     */
    default List<Map<String, String>> getOptions(Map<String, Object> config) {
        return List.of();
    }

    /**
     * 是否支持多选
     * 适用于Select等支持单选/多选的字段类型
     */
    default boolean isMultipleSupported() {
        return false;
    }
}
