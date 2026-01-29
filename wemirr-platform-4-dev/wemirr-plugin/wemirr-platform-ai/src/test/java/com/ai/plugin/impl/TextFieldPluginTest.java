package com.ai.plugin.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TextFieldPlugin 测试类
 */
@DisplayName("TextFieldPlugin 测试")
class TextFieldPluginTest {

    private TextFieldPlugin plugin;

    @BeforeEach
    void setUp() {
        plugin = new TextFieldPlugin();
    }

    @Test
    @DisplayName("插件类型应该是 text")
    void getType_shouldReturnText() {
        assertEquals("text", plugin.getType());
    }

    @Test
    @DisplayName("插件名称应该是文本")
    void getName_shouldReturnChineseText() {
        assertEquals("文本", plugin.getName());
    }

    @Test
    @DisplayName("插件图标应该是 form")
    void getIcon_shouldReturnForm() {
        assertEquals("form", plugin.getIcon());
    }

    @Test
    @DisplayName("组件类型应该是 Input")
    void getComponentType_shouldReturnInput() {
        assertEquals("Input", plugin.getComponentType());
    }

    @Test
    @DisplayName("配置模式应该包含 maxLength 和 minLength")
    void getConfigSchema_shouldContainLengthConfig() {
        Map<String, Object> schema = plugin.getConfigSchema();

        assertTrue(schema.containsKey("maxLength"));
        assertTrue(schema.containsKey("minLength"));
    }

    @Test
    @DisplayName("默认配置应该正确")
    void getDefaultConfig_shouldReturnCorrectDefaults() {
        Map<String, Object> defaults = plugin.getDefaultConfig();

        assertEquals(500, defaults.get("maxLength"));
        assertEquals(0, defaults.get("minLength"));
    }

    @Test
    @DisplayName("空值应该校验通过")
    void validate_nullValue_shouldPass() {
        ValidationResult result = plugin.validate(null, Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("空字符串应该校验通过")
    void validate_emptyString_shouldPass() {
        ValidationResult result = plugin.validate("", Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("正常文本应该校验通过")
    void validate_normalText_shouldPass() {
        ValidationResult result = plugin.validate("Hello World", Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("超过最大长度应该校验失败")
    void validate_exceedMaxLength_shouldFail() {
        String longText = "a".repeat(501);
        Map<String, Object> config = Map.of("maxLength", 500);

        ValidationResult result = plugin.validate(longText, config);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("最大长度"));
    }

    @Test
    @DisplayName("小于最小长度应该校验失败")
    void validate_belowMinLength_shouldFail() {
        String shortText = "a";
        Map<String, Object> config = Map.of("minLength", 5);

        ValidationResult result = plugin.validate(shortText, config);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("最小长度"));
    }

    @Test
    @DisplayName("在长度范围内应该校验通过")
    void validate_withinRange_shouldPass() {
        String text = "Hello";
        Map<String, Object> config = Map.of("minLength", 1, "maxLength", 100);

        ValidationResult result = plugin.validate(text, config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("processValue 应该直接返回原值")
    void processValue_shouldReturnOriginalValue() {
        Object original = "test value";
        Object result = plugin.processValue(original, Map.of());

        assertEquals(original, result);
        assertSame(original, result);
    }

    @Test
    @DisplayName("processValue 处理 null 值")
    void processValue_nullValue_shouldReturnNull() {
        Object result = plugin.processValue(null, Map.of());

        assertNull(result);
    }

    @Test
    @DisplayName("边界值测试：等于最大长度应该通过")
    void validate_atMaxLength_shouldPass() {
        String text = "a".repeat(500);
        Map<String, Object> config = Map.of("maxLength", 500);

        ValidationResult result = plugin.validate(text, config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("边界值测试：等于最小长度应该通过")
    void validate_atMinLength_shouldPass() {
        String text = "a".repeat(5);
        Map<String, Object> config = Map.of("minLength", 5);

        ValidationResult result = plugin.validate(text, config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("处理中文应该正确计算长度")
    void validate_chineseCharacters_shouldCalculateCorrectly() {
        // 中文每个字符占1个长度
        String chinese = "你好世界";
        Map<String, Object> config = Map.of("minLength", 2, "maxLength", 10);

        ValidationResult result = plugin.validate(chinese, config);

        assertTrue(result.isValid());
    }
}
