package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 数值字段插件
 * 支持数字输入，范围校验
 */
@Slf4j
@Component
public class NumberFieldPlugin implements FieldTypePlugin {

    private static final String TYPE = "number";
    private static final String NAME = "数值";
    private static final String ICON = "number";
    private static final String COMPONENT_TYPE = "InputNumber";

    private static final double DEFAULT_MIN = Double.NEGATIVE_INFINITY;
    private static final double DEFAULT_MAX = Double.POSITIVE_INFINITY;
    private static final int DEFAULT_PRECISION = 2;

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
                "min", Map.of(
                        "type", "number",
                        "title", "最小值",
                        "default", DEFAULT_MIN
                ),
                "max", Map.of(
                        "type", "number",
                        "title", "最大值",
                        "default", DEFAULT_MAX
                ),
                "precision", Map.of(
                        "type", "number",
                        "title", "小数位数",
                        "default", DEFAULT_PRECISION,
                        "minimum", 0,
                        "maximum", 10
                ),
                "step", Map.of(
                        "type", "number",
                        "title", "步进值",
                        "default", 1
                ),
                "prefix", Map.of(
                        "type", "string",
                        "title", "前缀",
                        "default", ""
                ),
                "suffix", Map.of(
                        "type", "string",
                        "title", "后缀",
                        "default", ""
                )
        );
    }

    @Override
    public Map<String, Object> getDefaultConfig() {
        return Map.of(
                "min", DEFAULT_MIN,
                "max", DEFAULT_MAX,
                "precision", DEFAULT_PRECISION
        );
    }

    @Override
    public ValidationResult validate(Object value, Map<String, Object> config) {
        // 空值通过校验
        if (value == null) {
            return ValidationResult.success();
        }

        double numericValue;

        // 解析数值
        if (value instanceof Number) {
            numericValue = ((Number) value).doubleValue();
        } else {
            try {
                numericValue = Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                return ValidationResult.failure("必须是有效的数字")
                        .withData("value", value);
            }
        }

        // 获取范围配置
        Double minValue = getConfigDouble(config, "min", null);
        Double maxValue = getConfigDouble(config, "max", null);

        // 校验最小值
        if (minValue != null && numericValue < minValue) {
            return ValidationResult.failure(String.format("值不能小于最小值 %s，当前值：%s", formatNumber(minValue), formatNumber(numericValue)))
                    .withData("min", minValue)
                    .withData("actual", numericValue);
        }

        // 校验最大值
        if (maxValue != null && numericValue > maxValue) {
            return ValidationResult.failure(String.format("值不能大于最大值 %s，当前值：%s", formatNumber(maxValue), formatNumber(numericValue)))
                    .withData("max", maxValue)
                    .withData("actual", numericValue);
        }

        return ValidationResult.success();
    }

    @Override
    public Object processValue(Object value, Map<String, Object> config) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private Double getConfigDouble(Map<String, Object> config, String key, Double defaultValue) {
        if (config == null || !config.containsKey(key)) {
            return defaultValue;
        }
        Object val = config.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        try {
            return Double.parseDouble(val.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String formatNumber(Double value) {
        if (value == null) {
            return "";
        }
        if (value == value.longValue()) {
            return String.valueOf(value.longValue());
        }
        return String.valueOf(value);
    }
}
