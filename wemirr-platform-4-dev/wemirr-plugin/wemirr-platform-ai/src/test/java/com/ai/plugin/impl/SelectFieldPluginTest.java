package com.ai.plugin.impl;

import com.ai.plugin.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SelectFieldPlugin 测试类
 */
@DisplayName("SelectFieldPlugin 测试")
class SelectFieldPluginTest {

    private SelectFieldPlugin plugin;

    @BeforeEach
    void setUp() {
        plugin = new SelectFieldPlugin();
    }

    @Test
    @DisplayName("插件类型应该是 select")
    void getType_shouldReturnSelect() {
        assertEquals("select", plugin.getType());
    }

    @Test
    @DisplayName("插件名称应该是下拉选择")
    void getName_shouldReturnChineseSelect() {
        assertEquals("下拉选择", plugin.getName());
    }

    @Test
    @DisplayName("插件图标应该是 select")
    void getIcon_shouldReturnSelect() {
        assertEquals("select", plugin.getIcon());
    }

    @Test
    @DisplayName("组件类型应该是 Select")
    void getComponentType_shouldReturnSelect() {
        assertEquals("Select", plugin.getComponentType());
    }

    @Test
    @DisplayName("配置模式应该包含 options")
    void getConfigSchema_shouldContainOptions() {
        Map<String, Object> schema = plugin.getConfigSchema();

        assertTrue(schema.containsKey("options"));
    }

    @Test
    @DisplayName("空值应该校验通过（非必填）")
    void validate_nullValue_shouldPass() {
        ValidationResult result = plugin.validate(null, Map.of());

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("选择有效选项应该校验通过")
    void validate_validOption_shouldPass() {
        List<Map<String, String>> options = List.of(
                Map.of("label", "选项A", "value", "A"),
                Map.of("label", "选项B", "value", "B")
        );
        Map<String, Object> config = Map.of("options", options);

        ValidationResult result = plugin.validate("A", config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("选择不在选项中的值应该校验失败")
    void validate_invalidOption_shouldFail() {
        List<Map<String, String>> options = List.of(
                Map.of("label", "选项A", "value", "A"),
                Map.of("label", "选项B", "value", "B")
        );
        Map<String, Object> config = Map.of("options", options);

        ValidationResult result = plugin.validate("C", config);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("必须是有效的选项"));
    }

    @Test
    @DisplayName("单选模式下选择单个有效值应该通过")
    void validate_singleSelect_validValue_shouldPass() {
        List<Map<String, String>> options = List.of(
                Map.of("label", "红", "value", "red"),
                Map.of("label", "绿", "value", "green"),
                Map.of("label", "蓝", "value", "blue")
        );
        Map<String, Object> config = Map.of("options", options, "multiple", false);

        ValidationResult result = plugin.validate("green", config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("多选模式下选择多个有效值应该通过")
    void validate_multipleSelect_validValues_shouldPass() {
        List<Map<String, String>> options = List.of(
                Map.of("label", "红", "value", "red"),
                Map.of("label", "绿", "value", "green"),
                Map.of("label", "蓝", "value", "blue")
        );
        Map<String, Object> config = Map.of("options", options, "multiple", true);

        ValidationResult result = plugin.validate(List.of("red", "blue"), config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("多选模式下包含无效值应该校验失败")
    void validate_multipleSelect_withInvalidValue_shouldFail() {
        List<Map<String, String>> options = List.of(
                Map.of("label", "红", "value", "red"),
                Map.of("label", "绿", "value", "green")
        );
        Map<String, Object> config = Map.of("options", options, "multiple", true);

        ValidationResult result = plugin.validate(List.of("red", "yellow"), config);

        assertFalse(result.isValid());
    }

    @Test
    @DisplayName("processValue 应该直接返回原值")
    void processValue_shouldReturnOriginalValue() {
        Object original = "test";
        Object result = plugin.processValue(original, Map.of());

        assertEquals(original, result);
    }

    @Test
    @DisplayName("processValue 处理 null 值")
    void processValue_null_shouldReturnNull() {
        Object result = plugin.processValue(null, Map.of());

        assertNull(result);
    }

    @Test
    @DisplayName("processValue 处理列表值")
    void processValue_list_shouldReturnList() {
        List<String> values = List.of("A", "B", "C");
        Object result = plugin.processValue(values, Map.of());

        assertEquals(values, result);
    }

    @Test
    @DisplayName("选项配置应该能获取到")
    void getOptions_shouldBeRetrievable() {
        List<Map<String, String>> options = List.of(
                Map.of("label", "测试", "value", "test")
        );
        Map<String, Object> config = Map.of("options", options);

        assertNotNull(plugin.getOptions(config));
    }

    @Test
    @DisplayName("空选项配置应该返回空列表")
    void getOptions_emptyConfig_shouldReturnEmptyList() {
        List<String> options = plugin.getOptions(Map.of());

        assertTrue(options.isEmpty());
    }
}
