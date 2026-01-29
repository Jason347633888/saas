package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 长文本字段插件
 * 支持多行文本输入
 */
@Slf4j
@Component
public class TextareaFieldPlugin implements FieldTypePlugin {

    private static final String TYPE = "textarea";
    private static final String NAME = "长文本";
    private static final String ICON = "file-text";
    private static final String COMPONENT_TYPE = "InputTextArea";

    private static final int DEFAULT_MAX_LENGTH = 5000;
    private static final int DEFAULT_MIN_LENGTH = 0;
    private static final int DEFAULT_ROWS = 4;

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
                        "maximum", 50000
                ),
                "minLength", Map.of(
                        "type", "number",
                        "title", "最小长度",
                        "default", DEFAULT_MIN_LENGTH,
                        "minimum", 0
                ),
                "rows", Map.of(
                        "type", "number",
                        "title", "行数",
                        "default", DEFAULT_ROWS,
                        "minimum", 2,
                        "maximum", 20
                ),
                "showCount", Map.of(
                        "type", "boolean",
                        "title", "显示字数统计",
                        "default", true
                ),
                "autoSize", Map.of(
                        "type", "boolean",
                        "title", "自动高度",
                        "default", false
                )
        );
    }

    @Override
    public Map<String, Object> getDefaultConfig() {
        return Map.of(
                "maxLength", DEFAULT_MAX_LENGTH,
                "minLength", DEFAULT_MIN_LENGTH,
                "rows", DEFAULT_ROWS
        );
    }

    @Override
    public ValidationResult validate(Object value, Map<String, Object> config) {
        // 空值通过校验
        if (value == null) {
            return ValidationResult.success();
        }

        String strValue = value.toString();

        if (strValue.isEmpty()) {
            return ValidationResult.success();
        }

        int maxLength = getConfigInt(config, "maxLength", DEFAULT_MAX_LENGTH);
        int minLength = getConfigInt(config, "minLength", DEFAULT_MIN_LENGTH);

        if (strValue.length() > maxLength) {
            return ValidationResult.failure(String.format("文本长度不能超过%d个字符，当前长度：%d", maxLength, strValue.length()))
                    .withData("maxLength", maxLength)
                    .withData("actualLength", strValue.length());
        }

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

    private int getConfigInt(Map<String, Object> config, String key, int defaultValue) {
        if (config == null || !config.containsKey(key)) {
            return defaultValue;
        }
        Object val = config.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
