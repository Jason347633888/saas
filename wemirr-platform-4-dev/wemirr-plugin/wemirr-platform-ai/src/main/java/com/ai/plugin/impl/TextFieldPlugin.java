package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 文本字段插件
 * 支持短文本输入，最大长度限制
 */
@Slf4j
@Component
public class TextFieldPlugin implements FieldTypePlugin {

    private static final String TYPE = "text";
    private static final String NAME = "文本";
    private static final String ICON = "form";
    private static final String COMPONENT_TYPE = "Input";

    private static final int DEFAULT_MAX_LENGTH = 500;
    private static final int DEFAULT_MIN_LENGTH = 0;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getIcon() {
        return ICON;
    }

    @Override
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    @Override
    public Map<String, Object> getConfigSchema() {
        return Map.of(
                "maxLength", Map.of(
                        "type", "number",
                        "title", "最大长度",
                        "default", DEFAULT_MAX_LENGTH,
                        "minimum", 1,
                        "maximum", 5000
                ),
                "minLength", Map.of(
                        "type", "number",
                        "title", "最小长度",
                        "default", DEFAULT_MIN_LENGTH,
                        "minimum", 0
                ),
                "placeholder", Map.of(
                        "type", "string",
                        "title", "占位符",
                        "default", ""
                ),
                "disabled", Map.of(
                        "type", "boolean",
                        "title", "是否禁用",
                        "default", false
                )
        );
    }

    @Override
    public Map<String, Object> getDefaultConfig() {
        return Map.of(
                "maxLength", DEFAULT_MAX_LENGTH,
                "minLength", DEFAULT_MIN_LENGTH
        );
    }

    @Override
    public ValidationResult validate(Object value, Map<String, Object> config) {
        // 空值通过校验（非必填）
        if (value == null) {
            return ValidationResult.success();
        }

        String strValue = value.toString();

        // 空字符串通过校验
        if (strValue.isEmpty()) {
            return ValidationResult.success();
        }

        // 获取配置
        int maxLength = getConfigInt(config, "maxLength", DEFAULT_MAX_LENGTH);
        int minLength = getConfigInt(config, "minLength", DEFAULT_MIN_LENGTH);

        // 校验最大长度
        if (strValue.length() > maxLength) {
            return ValidationResult.failure(String.format("文本长度不能超过%d个字符，当前长度：%d", maxLength, strValue.length()))
                    .withData("maxLength", maxLength)
                    .withData("actualLength", strValue.length());
        }

        // 校验最小长度
        if (strValue.length() < minLength) {
            return ValidationResult.failure(String.format("文本长度不能少于%d个字符，当前长度：%d", minLength, strValue.length()))
                    .withData("minLength", minLength)
                    .withData("actualLength", strValue.length());
        }

        return ValidationResult.success();
    }

    @Override
    public Object processValue(Object value, Map<String, Object> config) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /**
     * 从配置中获取整数值
     */
    private int getConfigInt(Map<String, Object> config, String key, int defaultValue) {
        if (config == null || !config.containsKey(key)) {
            return defaultValue;
        }
        Object value = config.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
