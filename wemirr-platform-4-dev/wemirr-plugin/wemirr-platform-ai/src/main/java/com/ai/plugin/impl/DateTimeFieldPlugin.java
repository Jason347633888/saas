package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * 日期时间字段插件
 * 支持日期时间选择，日期时间范围校验
 */
@Slf4j
@Component
public class DateTimeFieldPlugin implements FieldTypePlugin {

    private static final String TYPE = "datetime";
    private static final String NAME = "日期时间";
    private static final String ICON = "clock-circle";
    private static final String COMPONENT_TYPE = "DatePicker";

    private static final String DEFAULT_FORMAT = "YYYY-MM-DD HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                "minDateTime", Map.of(
                        "type", "string",
                        "title", "最小日期时间",
                        "format", "datetime"
                ),
                "maxDateTime", Map.of(
                        "type", "string",
                        "title", "最大日期时间",
                        "format", "datetime"
                ),
                "format", Map.of(
                        "type", "string",
                        "title", "日期时间格式",
                        "default", DEFAULT_FORMAT
                ),
                "showTime", Map.of(
                        "type", "boolean",
                        "title", "显示时间选择",
                        "default", true
                ),
                "placeholder", Map.of(
                        "type", "string",
                        "title", "占位符",
                        "default", "请选择日期时间"
                )
        );
    }

    @Override
    public Map<String, Object> getDefaultConfig() {
        return Map.of(
                "format", DEFAULT_FORMAT,
                "showTime", true
        );
    }

    @Override
    public ValidationResult validate(Object value, Map<String, Object> config) {
        // 空值通过校验
        if (value == null) {
            return ValidationResult.success();
        }

        LocalDateTime dateTimeValue;

        // 解析日期时间
        if (value instanceof LocalDateTime) {
            dateTimeValue = (LocalDateTime) value;
        } else if (value instanceof String) {
            try {
                dateTimeValue = LocalDateTime.parse((String) value, FORMATTER);
            } catch (DateTimeParseException e) {
                return ValidationResult.failure("日期时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 格式")
                        .withData("value", value);
            }
        } else {
            return ValidationResult.failure("不支持的日期时间格式")
                    .withData("value", value);
        }

        // 获取范围配置
        LocalDateTime minDateTime = parseDateTime(config.get("minDateTime"));
        LocalDateTime maxDateTime = parseDateTime(config.get("maxDateTime"));

        // 校验最小日期时间
        if (minDateTime != null && dateTimeValue.isBefore(minDateTime)) {
            return ValidationResult.failure(String.format("日期时间不能早于 %s，当前日期时间：%s",
                            minDateTime.format(FORMATTER), dateTimeValue.format(FORMATTER)))
                    .withData("minDateTime", minDateTime.toString())
                    .withData("actualDateTime", dateTimeValue.toString());
        }

        // 校验最大日期时间
        if (maxDateTime != null && dateTimeValue.isAfter(maxDateTime)) {
            return ValidationResult.failure(String.format("日期时间不能晚于 %s，当前日期时间：%s",
                            maxDateTime.format(FORMATTER), dateTimeValue.format(FORMATTER)))
                    .withData("maxDateTime", maxDateTime.toString())
                    .withData("actualDateTime", dateTimeValue.toString());
        }

        return ValidationResult.success();
    }

    @Override
    public Object processValue(Object value, Map<String, Object> config) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime) {
            return value;
        }

        if (value instanceof String) {
            try {
                return LocalDateTime.parse((String) value, FORMATTER);
            } catch (DateTimeParseException e) {
                return value;
            }
        }

        return value;
    }

    private LocalDateTime parseDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof String) {
            try {
                return LocalDateTime.parse((String) value, FORMATTER);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }
}
