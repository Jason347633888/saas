package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * 日期字段插件
 * 支持日期选择，日期范围校验
 */
@Slf4j
@Component
public class DateFieldPlugin implements FieldTypePlugin {

    private static final String TYPE = "date";
    private static final String NAME = "日期";
    private static final String ICON = "calendar";
    private static final String COMPONENT_TYPE = "DatePicker";

    private static final String DEFAULT_FORMAT = "YYYY-MM-DD";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
                "minDate", Map.of(
                        "type", "string",
                        "title", "最小日期",
                        "format", "date"
                ),
                "maxDate", Map.of(
                        "type", "string",
                        "title", "最大日期",
                        "format", "date"
                ),
                "format", Map.of(
                        "type", "string",
                        "title", "日期格式",
                        "default", DEFAULT_FORMAT
                ),
                "disabledDate", Map.of(
                        "type", "function",
                        "title", "禁用日期函数"
                ),
                "placeholder", Map.of(
                        "type", "string",
                        "title", "占位符",
                        "default", "请选择日期"
                )
        );
    }

    @Override
    public Map<String, Object> getDefaultConfig() {
        return Map.of("format", DEFAULT_FORMAT);
    }

    @Override
    public ValidationResult validate(Object value, Map<String, Object> config) {
        // 空值通过校验
        if (value == null) {
            return ValidationResult.success();
        }

        LocalDate dateValue;

        // 解析日期
        if (value instanceof LocalDate) {
            dateValue = (LocalDate) value;
        } else if (value instanceof String) {
            try {
                dateValue = LocalDate.parse((String) value, FORMATTER);
            } catch (DateTimeParseException e) {
                return ValidationResult.failure("日期格式不正确，请使用 yyyy-MM-dd 格式")
                        .withData("value", value);
            }
        } else {
            return ValidationResult.failure("不支持的日期格式")
                    .withData("value", value);
        }

        // 获取范围配置
        LocalDate minDate = parseDate(config.get("minDate"));
        LocalDate maxDate = parseDate(config.get("maxDate"));

        // 校验最小日期
        if (minDate != null && dateValue.isBefore(minDate)) {
            return ValidationResult.failure(String.format("日期不能早于 %s，当前日期：%s",
                            minDate.format(FORMATTER), dateValue.format(FORMATTER)))
                    .withData("minDate", minDate.toString())
                    .withData("actualDate", dateValue.toString());
        }

        // 校验最大日期
        if (maxDate != null && dateValue.isAfter(maxDate)) {
            return ValidationResult.failure(String.format("日期不能晚于 %s，当前日期：%s",
                            maxDate.format(FORMATTER), dateValue.format(FORMATTER)))
                    .withData("maxDate", maxDate.toString())
                    .withData("actualDate", dateValue.toString());
        }

        return ValidationResult.success();
    }

    @Override
    public Object processValue(Object value, Map<String, Object> config) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDate) {
            return value;
        }

        if (value instanceof String) {
            try {
                return LocalDate.parse((String) value, FORMATTER);
            } catch (DateTimeParseException e) {
                return value;
            }
        }

        return value;
    }

    private LocalDate parseDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof String) {
            try {
                return LocalDate.parse((String) value, FORMATTER);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }
}
