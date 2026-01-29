package com.ai.service;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.PluginRegistry;
import com.ai.plugin.ValidationResult;
import com.ai.plugin.dto.FieldConfigDTO;
import com.ai.plugin.dto.PluginDefinitionDTO;
import com.ai.plugin.impl.NumberFieldPlugin;
import com.ai.plugin.impl.TextFieldPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FieldPluginService 测试类
 */
@DisplayName("FieldPluginService 测试")
class FieldPluginServiceTest {

    private FieldPluginServiceImpl service;
    private PluginRegistry registry;
    private TextFieldPlugin textPlugin;
    private NumberFieldPlugin numberPlugin;

    @BeforeEach
    void setUp() {
        registry = new PluginRegistry();
        textPlugin = new TextFieldPlugin();
        numberPlugin = new NumberFieldPlugin();

        registry.register(textPlugin);
        registry.register(numberPlugin);

        service = new FieldPluginServiceImpl(registry);
    }

    @Test
    @DisplayName("获取所有插件应该返回注册的所有插件")
    void getAllPlugins_shouldReturnAllRegistered() {
        List<PluginDefinitionDTO> plugins = service.getAllPlugins();

        assertEquals(2, plugins.size());
    }

    @Test
    @DisplayName("根据类型获取插件应该返回对应插件")
    void getPluginByType_shouldReturnCorrectPlugin() {
        Optional<PluginDefinitionDTO> result = service.getPluginByType("text");

        assertTrue(result.isPresent());
        assertEquals("text", result.get().getType());
        assertEquals("文本", result.get().getName());
    }

    @Test
    @DisplayName("获取不存在的插件类型应该返回空")
    void getPluginByType_notExists_shouldReturnEmpty() {
        Optional<PluginDefinitionDTO> result = service.getPluginByType("not_exists");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("校验文本字段应该正确执行")
    void validateField_text_shouldValidateCorrectly() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("text");
        config.setValue("Hello");
        config.setConfig(Map.of("maxLength", 100));

        ValidationResult result = service.validateField(config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("校验数值字段应该正确执行")
    void validateField_number_shouldValidateCorrectly() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("number");
        config.setValue(50);
        config.setConfig(Map.of("min", 0, "max", 100));

        ValidationResult result = service.validateField(config);

        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("校验失败的字段应该返回错误信息")
    void validateField_failed_shouldReturnErrorMessage() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("number");
        config.setFieldName("年龄");
        config.setValue(200);
        config.setConfig(Map.of("max", 100));

        ValidationResult result = service.validateField(config);

        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    @DisplayName("处理字段值应该正确执行")
    void processFieldValue_shouldProcessCorrectly() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("number");
        config.setValue("123");
        config.setConfig(Map.of());

        Object result = service.processFieldValue(config);

        assertEquals(123, result);
    }

    @Test
    @DisplayName("注册新插件应该成功")
    void registerPlugin_shouldSucceed() {
        FieldTypePlugin newPlugin = new FieldTypePlugin() {
            @Override
            public String getType() {
                return "date";
            }

            @Override
            public String getName() {
                return "日期";
            }

            @Override
            public String getIcon() {
                return "calendar";
            }

            @Override
            public String getComponentType() {
                return "DatePicker";
            }
        };

        service.registerPlugin(newPlugin);

        Optional<PluginDefinitionDTO> result = service.getPluginByType("date");
        assertTrue(result.isPresent());
        assertEquals("日期", result.get().getName());
    }

    @Test
    @DisplayName("注销插件应该成功")
    void unregisterPlugin_shouldSucceed() {
        service.unregisterPlugin("text");

        Optional<PluginDefinitionDTO> result = service.getPluginByType("text");
        assertTrue(result.isEmpty());
        assertEquals(1, service.getAllPlugins().size());
    }

    @Test
    @DisplayName("获取所有插件定义应该包含配置模式")
    void getAllPlugins_shouldIncludeConfigSchema() {
        List<PluginDefinitionDTO> plugins = service.getAllPlugins();

        for (PluginDefinitionDTO plugin : plugins) {
            assertNotNull(plugin.getConfigSchema());
            assertFalse(plugin.getConfigSchema().isEmpty());
        }
    }

    @Test
    @DisplayName("获取插件定义应该包含图标信息")
    void getPluginByType_shouldIncludeIcon() {
        Optional<PluginDefinitionDTO> result = service.getPluginByType("text");

        assertTrue(result.isPresent());
        assertEquals("form", result.get().getIcon());
    }

    @Test
    @DisplayName("校验不存在的字段类型应该失败")
    void validateField_notExistsType_shouldFail() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("not_exists");
        config.setValue("test");

        ValidationResult result = service.validateField(config);

        assertFalse(result.isValid());
    }

    @Test
    @DisplayName("处理不存在的字段类型应该返回原值")
    void processFieldValue_notExistsType_shouldReturnOriginal() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("not_exists");
        config.setValue("test");

        Object result = service.processFieldValue(config);

        assertEquals("test", result);
    }
}
