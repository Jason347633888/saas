package com.ai.plugin.impl;

import com.ai.plugin.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateFieldPlugin 测试类
 */
@DisplayName("DateFieldPlugin 测试")
class DateFieldPluginTest {

    private DateFieldPlugin plugin;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        plugin = new DateFieldPlugin();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    @Test
    @DisplayName("插件类型应该是 date")
    void getType_shouldReturnDate() {
        assertEquals("date", plugin.getType());
    }

    @Test
    @DisplayName("插件名称应该是日期")
    void getName_shouldReturnChineseDate() {
        assertEquals("日期", plugin.getName());
    }

    @Test
    @DisplayName("插件图标应该是 calendar")
    void getIcon_shouldReturnCalendar() {
        assertEquals("calendar", plugin.getIcon());
    }

    @Test
    @DisplayName("组件类型应该是 DatePicker")
    void getComponentType_shouldReturnDatePicker() {
        assertEquals("DatePicker", plugin.getComponentType());
    }

    @Test
    @DisplayName("配置模式应该包含 minDate 和 maxDate")
    void getConfigSchema_shouldContainDateRangeConfig() {
        Map<String, Object> schema = plugin.getConfigSchema();

        assertTrue(schema.containsKey("minDate"));
        assertTrue(schema.containsKey("maxDate"));
    }

    @Test
    @DisplayName("空值应该校验通过")
    void validate_nullValue_shouldPass() {
        ValidationResult result = plugin.validate(null, Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("正常日期应该校验通过")
    void validate_normalDate_shouldPass() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        ValidationResult result = plugin.validate(date, Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("超过最大日期应该校验失败")
    void validate_afterMaxDate_shouldFail() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        Map<String, Object> config = Map.of("maxDate", today.format(formatter));

        ValidationResult result = plugin.validate(tomorrow, config);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("不能晚于"));
    }

    @Test
    @DisplayName("小于最小日期应该校验失败")
    void validate_beforeMinDate_shouldFail() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        Map<String, Object> config = Map.of("minDate", today.format(formatter));

        ValidationResult result = plugin.validate(yesterday, config);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("不能早于"));
    }

    @Test
    @DisplayName("在日期范围内应该校验通过")
    void validate_withinRange_shouldPass() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);
        Map<String, Object> config = Map.of(
                "minDate", yesterday.format(formatter),
                "maxDate", tomorrow.format(formatter)
        );

        ValidationResult result = plugin.validate(today, config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("等于边界值应该校验通过")
    void validate_atBoundary_shouldPass() {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(formatter);

        assertTrue(plugin.validate(today, Map.of("minDate", todayStr)).isValid());
        assertTrue(plugin.validate(today, Map.of("maxDate", todayStr)).isValid());
    }

    @Test
    @DisplayName("字符串日期应该能正确解析和校验")
    void validate_stringDate_shouldParseAndValidate() {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(formatter);

        ValidationResult result = plugin.validate(todayStr, Map.of("minDate", todayStr));

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("processValue 应该解析字符串日期")
    void processValue_string_shouldParseToLocalDate() {
        String dateStr = "2025-01-15";
        Object result = plugin.processValue(dateStr, Map.of());

        assertTrue(result instanceof LocalDate);
        assertEquals(LocalDate.of(2025, 1, 15), result);
    }

    @Test
    @DisplayName("processValue 处理 LocalDate 应该直接返回")
    void processValue_localDate_shouldReturnSame() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        Object result = plugin.processValue(date, Map.of());

        assertEquals(date, result);
    }

    @Test
    @DisplayName("processValue 处理 null 值")
    void processValue_null_shouldReturnNull() {
        Object result = plugin.processValue(null, Map.of());

        assertNull(result);
    }

    @Test
    @DisplayName("日期范围组合测试")
    void validate_combinedRange_shouldWorkCorrectly() {
        LocalDate today = LocalDate.now();
        String yesterday = today.minusDays(1).format(formatter);
        String tomorrow = today.plusDays(1).format(formatter);
        Map<String, Object> config = Map.of("minDate", yesterday, "maxDate", tomorrow);

        // 早于最小日期
        assertFalse(plugin.validate(today.minusDays(2), config).isValid());
        // 等于最小日期
        assertTrue(plugin.validate(today.minusDays(1), config).isValid());
        // 在范围内
        assertTrue(plugin.validate(today, config).isValid());
        // 等于最大日期
        assertTrue(plugin.validate(today.plusDays(1), config).isValid());
        // 晚于最大日期
        assertFalse(plugin.validate(today.plusDays(2), config).isValid());
    }
}
