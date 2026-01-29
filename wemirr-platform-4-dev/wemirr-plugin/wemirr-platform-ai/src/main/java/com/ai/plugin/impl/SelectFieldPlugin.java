package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 下拉选择字段插件
 * 支持单选和多选
 */
@Slf4j
@Component
public class SelectFieldPlugin implements FieldTypePlugin {

    private static final String TYPE = "select";
    private static final String NAME = "下拉选择";
    private static final String ICON = "select";
    private static final String COMPONENT_TYPE = "Select";

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
                "options", Map.of(
                        "type", "array",
                        "title", "选项列表",
                        "items", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "label", Map.of("type", "string", "title", "显示标签"),
                                        "value", Map.of("type", "string", "title", "值"),
                                        "disabled", Map.of("type", "boolean", "title", "是否禁用")
                                )
                        )
                ),
                "multiple", Map.of(
                        "type", "boolean",
                        "title", "是否多选",
                        "default", false
                ),
                "allowClear", Map.of(
                        "type", "boolean",
                        "title", "是否允许清除",
                        "default", true
                ),
                "placeholder", Map.of(
                        "type", "string",
                        "title", "占位符",
                        "default", "请选择"
                ),
                "showSearch", Map.of(
                        "type", "boolean",
                        "title", "是否支持搜索",
                        "default", false
                )
        );
    }

    @Override
    public Map<String, Object> getDefaultConfig() {
        return Map.of(
                "multiple", false,
                "allowClear", true,
                "showSearch", false
        );
    }

    @Override
    public ValidationResult validate(Object value, Map<String, Object> config) {
        // 空值通过校验
        if (value == null) {
            return ValidationResult.success();
        }

        // 获取选项配置
        List<Map<String, String>> options = getOptions(config);
        Set<String> validValues = new HashSet<>();
        for (Map<String, String> option : options) {
            String optionValue = option.get("value");
            if (optionValue != null) {
                validValues.add(optionValue);
                // 兼容数字类型的值
                validValues.add(optionValue.toLowerCase());
            }
        }

        boolean multiple = getConfigBoolean(config, "multiple", false);

        if (multiple) {
            // 多选模式
            return validateMultiple(value, validValues);
        } else {
            // 单选模式
            return validateSingle(value, validValues);
        }
    }

    @SuppressWarnings("unchecked")
    private ValidationResult validateSingle(Object value, Set<String> validValues) {
        String strValue = value.toString();

        if (validValues.isEmpty()) {
            // 没有配置选项时，直接通过
            return ValidationResult.success();
        }

        if (!validValues.contains(strValue) && !validValues.contains(strValue.toLowerCase())) {
            return ValidationResult.failure("必须是有效的选项")
                    .withData("value", strValue)
                    .withData("validValues", new ArrayList<>(validValues));
        }

        return ValidationResult.success();
    }

    @SuppressWarnings("unchecked")
    private ValidationResult validateMultiple(Object value, Set<String> validValues) {
        List<String> values;

        if (value instanceof List) {
            values = (List<String>) value;
        } else if (value instanceof Collection) {
            values = new ArrayList<>((Collection<String>) value);
        } else {
            // 尝试解析为逗号分隔的字符串
            String strValue = value.toString();
            values = Arrays.asList(strValue.split(","));
        }

        if (validValues.isEmpty()) {
            return ValidationResult.success();
        }

        List<String> invalidValues = new ArrayList<>();
        for (String v : values) {
            String trimmed = v.trim();
            if (!validValues.contains(trimmed) && !validValues.contains(trimmed.toLowerCase())) {
                invalidValues.add(trimmed);
            }
        }

        if (!invalidValues.isEmpty()) {
            return ValidationResult.failure("包含无效选项: " + String.join(", ", invalidValues))
                    .withData("invalidValues", invalidValues);
        }

        return ValidationResult.success();
    }

    @Override
    public Object processValue(Object value, Map<String, Object> config) {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getOptions(Map<String, Object> config) {
        if (config == null || !config.containsKey("options")) {
            return List.of();
        }

        Object optionsObj = config.get("options");
        if (optionsObj instanceof List) {
            List<Map<String, String>> options = new ArrayList<>();
            for (Object item : (List<?>) optionsObj) {
                if (item instanceof Map) {
                    options.add((Map<String, String>) item);
                }
            }
            return options;
        }

        return List.of();
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
