package com.ai.plugin.impl;

import com.ai.plugin.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NumberFieldPlugin 测试类
 */
@DisplayName("NumberFieldPlugin 测试")
class NumberFieldPluginTest {

    private NumberFieldPlugin plugin;

    @BeforeEach
    void setUp() {
        plugin = new NumberFieldPlugin();
    }

    @Test
    @DisplayName("插件类型应该是 number")
    void getType_shouldReturnNumber() {
        assertEquals("number", plugin.getType());
    }

    @Test
    @DisplayName("插件名称应该是数值")
    void getName_shouldReturnChineseNumber() {
        assertEquals("数值", plugin.getName());
    }

    @Test
    @DisplayName("插件图标应该是 number")
    void getIcon_shouldReturnNumber() {
        assertEquals("number", plugin.getIcon());
    }

    @Test
    @DisplayName("组件类型应该是 InputNumber")
    void getComponentType_shouldReturnInputNumber() {
        assertEquals("InputNumber", plugin.getComponentType());
    }

    @Test
    @DisplayName("配置模式应该包含 min 和 max")
    void getConfigSchema_shouldContainRangeConfig() {
        Map<String, Object> schema = plugin.getConfigSchema();

        assertTrue(schema.containsKey("min"));
        assertTrue(schema.containsKey("max"));
    }

    @Test
    @DisplayName("空值应该校验通过")
    void validate_nullValue_shouldPass() {
        ValidationResult result = plugin.validate(null, Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("正常整数应该校验通过")
    void validate_normalInteger_shouldPass() {
        ValidationResult result = plugin.validate(100, Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("正常小数应该校验通过")
    void validate_normalDecimal_shouldPass() {
        ValidationResult result = plugin.validate(99.5, Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("负数应该校验通过")
    void validate_negativeNumber_shouldPass() {
        ValidationResult result = plugin.validate(-50, Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("超过最大值应该校验失败")
    void validate_exceedMax_shouldFail() {
        ValidationResult result = plugin.validate(200, Map.of("max", 100));

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("不能大于"));
    }

    @Test
    @DisplayName("小于最小值应该校验失败")
    void validate_belowMin_shouldFail() {
        ValidationResult result = plugin.validate(5, Map.of("min", 10));

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("不能小于"));
    }

    @Test
    @DisplayName("在范围内应该校验通过")
    void validate_withinRange_shouldPass() {
        ValidationResult result = plugin.validate(50, Map.of("min", 10, "max", 100));

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("等于边界值应该校验通过")
    void validate_atBoundary_shouldPass() {
        assertTrue(plugin.validate(10, Map.of("min", 10)).isValid());
        assertTrue(plugin.validate(100, Map.of("max", 100)).isValid());
    }

    @Test
    @DisplayName("字符串数字应该能正确校验")
    void validate_stringNumber_shouldParseAndValidate() {
        ValidationResult result = plugin.validate("50", Map.of("min", 10, "max", 100));

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("无效字符串应该校验失败")
    void validate_invalidString_shouldFail() {
        ValidationResult result = plugin.validate("abc", Map.of());

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("有效的数字"));
    }

    @Test
    @DisplayName("processValue 应该转换字符串为数字")
    void processValue_string_shouldConvertToNumber() {
        Object result = plugin.processValue("123.45", Map.of());

        assertTrue(result instanceof Number);
        assertEquals(123.45, ((Number) result).doubleValue());
    }

    @Test
    @DisplayName("processValue 处理 null 值")
    void processValue_null_shouldReturnNull() {
        Object result = plugin.processValue(null, Map.of());

        assertNull(result);
    }

    @Test
    @DisplayName("processValue 处理整数")
    void processValue_integer_shouldReturnSame() {
        Object result = plugin.processValue(100, Map.of());

        assertEquals(100, result);
    }

    @Test
    @DisplayName("组合 min 和 max 边界测试")
    void validate_combinedRange_shouldWorkCorrectly() {
        // min=0, max=100
        assertFalse(plugin.validate(-1, Map.of("min", 0, "max", 100)).isValid());
        assertTrue(plugin.validate(0, Map.of("min", 0, "max", 100)).isValid());
        assertTrue(plugin.validate(50, Map.of("min", 0, "max", 100)).isValid());
        assertTrue(plugin.validate(100, Map.of("min", 0, "max", 100)).isValid());
        assertFalse(plugin.validate(101, Map.of("min", 0, "max", 100)).isValid());
    }
}
