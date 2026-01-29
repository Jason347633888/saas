package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 关联选择字段插件
 * 支持从其他模板选择数据
 */
@Slf4j
@Component
public class RelationFieldPlugin implements FieldTypePlugin {

    private static final String TYPE = "relation";
    private static final String NAME = "关联选择";
    private static final String ICON = "link";
    private static final String COMPONENT_TYPE = "RelationSelect";

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
                "relationTemplateId", Map.of(
                        "type", "number",
                        "title", "关联模板ID",
                        "required", true
                ),
                "relationTemplateName", Map.of(
                        "type", "string",
                        "title", "关联模板名称"
                ),
                "relationFieldKey", Map.of(
                        "type", "string",
                        "title", "关联显示字段"
                ),
                "multiple", Map.of(
                        "type", "boolean",
                        "title", "是否多选",
                        "default", false
                ),
                "placeholder", Map.of(
                        "type", "string",
                        "title", "占位符",
                        "default", "请选择"
                ),
                "filterFields", Map.of(
                        "type", "array",
                        "title", "过滤字段"
                ),
                "allowClear", Map.of(
                        "type", "boolean",
                        "title", "是否允许清除",
                        "default", true
                )
        );
    }

    @Override
    public Map<String, Object> getDefaultConfig() {
        return Map.of(
                "multiple", false,
                "allowClear", true
        );
    }

    @Override
    public ValidationResult validate(Object value, Map<String, Object> config) {
        // 空值通过校验
        if (value == null) {
            return ValidationResult.success();
        }

        // 检查是否配置了关联模板
        if (!config.containsKey("relationTemplateId") || config.get("relationTemplateId") == null) {
            return ValidationResult.failure("关联选择字段必须配置关联模板")
                    .withData("error", "relationTemplateId required");
        }

        // 验证值的格式
        // 单选时值为单个ID，多选时值为ID列表
        boolean multiple = getConfigBoolean(config, "multiple", false);

        if (multiple) {
            return validateMultiple(value, config);
        } else {
            return validateSingle(value, config);
        }
    }

    @SuppressWarnings("unchecked")
    private ValidationResult validateSingle(Object value, Map<String, Object> config) {
        String strValue = value.toString();

        // 验证是否为有效的ID格式
        if (strValue.isEmpty()) {
            return ValidationResult.success();
        }

        // 应该是数字ID
        try {
            Long.parseLong(strValue);
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.failure("关联选择的值必须是有效的ID")
                    .withData("value", strValue);
        }
    }

    @SuppressWarnings("unchecked")
    private ValidationResult validateMultiple(Object value, Map<String, Object> config) {
        if (value instanceof Iterable) {
            for (Object item : (Iterable<?>) value) {
                if (item != null) {
                    try {
                        Long.parseLong(item.toString());
                    } catch (NumberFormatException e) {
                        return ValidationResult.failure("关联选择的值必须是有效的ID列表")
                                .withData("value", item.toString());
                    }
                }
            }
        }

        return ValidationResult.success();
    }

    @Override
    public Object processValue(Object value, Map<String, Object> config) {
        return value;
    }

    @Override
    public boolean isMultipleSupported() {
        return true;
    }

    private boolean getConfigBoolean(Map<String, Object> config, String key, boolean defaultValue) {
        if (config == null || !config.containsKey(key)) {
            return defaultValue;
        }
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
}
