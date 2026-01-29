package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 是/否字段插件
 * 支持布尔值选择
 */
@Slf4j
@Component
public class BooleanFieldPlugin implements FieldTypePlugin {

    private static final String TYPE = "boolean";
    private static final String NAME = "是/否";
    private static final String ICON = "check-square";
    private static final String COMPONENT_TYPE = "Switch";

    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";

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
                "defaultValue", Map.of(
                        "type", "boolean",
                        "title", "默认值",
                        "default", false
                ),
                "checkedChildren", Map.of(
                        "type", "string",
                        "title", "选中时文本",
                        "default", "是"
                ),
                "unCheckedChildren", Map.of(
                        "type", "string",
                        "title", "未选中时文本",
                        "default", "否"
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
                "defaultValue", false,
                "checkedChildren", "是",
                "unCheckedChildren", "否"
        );
    }

    @Override
    public ValidationResult validate(Object value, Map<String, Object> config) {
        // 空值通过校验（非必填）
        if (value == null) {
            return ValidationResult.success();
        }

        // 转换为布尔值
        Boolean boolValue = toBoolean(value);

        if (boolValue == null) {
            return ValidationResult.failure("必须是有效的布尔值（true/false/0/1）")
                    .withData("value", value);
        }

        return ValidationResult.success();
    }

    @Override
    public Object processValue(Object value, Map<String, Object> config) {
        Boolean boolValue = toBoolean(value);
        return boolValue != null ? boolValue : value;
    }

    /**
     * 将各种类型的值转换为布尔值
     */
    private Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }

        String strValue = value.toString().trim().toLowerCase();

        switch (strValue) {
            case "true":
            case "1":
            case "yes":
            case "y":
                return true;
            case "false":
            case "0":
            case "no":
            case "n":
                return false;
            default:
                return null;
        }
    }
}
