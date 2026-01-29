package com.ai.controller;

import com.ai.plugin.PluginRegistry;
import com.ai.plugin.ValidationResult;
import com.ai.plugin.dto.FieldConfigDTO;
import com.ai.plugin.dto.PluginDefinitionDTO;
import com.ai.plugin.impl.NumberFieldPlugin;
import com.ai.plugin.impl.TextFieldPlugin;
import com.ai.service.FieldPluginService;
import com.ai.service.impl.FieldPluginServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FieldPluginController 测试类
 */
@DisplayName("FieldPluginController 测试")
class FieldPluginControllerTest {

    private FieldPluginController controller;
    private FieldPluginService service;
    private PluginRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new PluginRegistry();
        registry.register(new TextFieldPlugin());
        registry.register(new NumberFieldPlugin());
        service = new FieldPluginServiceImpl(registry);
        controller = new FieldPluginController(service);
    }

    @Test
    @DisplayName("获取所有插件应该返回200和插件列表")
    void getAllPlugins_shouldReturn200WithList() {
        ResponseEntity<List<PluginDefinitionDTO>> response = controller.getAllPlugins();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    @DisplayName("根据类型获取插件应该返回200和插件信息")
    void getPluginByType_shouldReturn200WithPlugin() {
        ResponseEntity<PluginDefinitionDTO> response = controller.getPluginByType("text");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("text", response.getBody().getType());
        assertEquals("文本", response.getBody().getName());
    }

    @Test
    @DisplayName("获取不存在的插件应该返回404")
    void getPluginByType_notExists_shouldReturn404() {
        ResponseEntity<PluginDefinitionDTO> response = controller.getPluginByType("not_exists");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("校验字段应该返回200和校验结果")
    void validateField_shouldReturn200WithResult() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("text");
        config.setValue("test");
        config.setConfig(Map.of("maxLength", 100));

        ResponseEntity<ValidationResult> response = controller.validateField(config);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
    }

    @Test
    @DisplayName("校验失败的字段应该返回200和错误信息")
    void validateField_failed_shouldReturn200WithError() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("text");
        config.setValue("a".repeat(501));
        config.setConfig(Map.of("maxLength", 500));

        ResponseEntity<ValidationResult> response = controller.validateField(config);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
    }

    @Test
    @DisplayName("处理字段值应该返回200和处理结果")
    void processFieldValue_shouldReturn200WithResult() {
        FieldConfigDTO config = new FieldConfigDTO();
        config.setFieldType("number");
        config.setValue("123");

        ResponseEntity<?> response = controller.processFieldValue(config);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("注册插件应该返回200")
    void registerPlugin_shouldReturn200() {
        TextFieldPlugin newPlugin = new TextFieldPlugin() {
            @Override
            public String getType() {
                return "custom_text";
            }

            @Override
            public String getName() {
                return "自定义文本";
            }
        };

        ResponseEntity<Void> response = controller.registerPlugin(newPlugin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("注销插件应该返回200")
    void unregisterPlugin_shouldReturn200() {
        ResponseEntity<Void> response = controller.unregisterPlugin("text");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("注销不存在的插件应该返回200（幂等）")
    void unregisterPlugin_notExists_shouldReturn200() {
        ResponseEntity<Void> response = controller.unregisterPlugin("not_exists");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("插件列表应该包含类型、名称、图标和配置模式")
    void getAllPlugins_shouldContainRequiredFields() {
        ResponseEntity<List<PluginDefinitionDTO>> response = controller.getAllPlugins();
        List<PluginDefinitionDTO> plugins = response.getBody();

        assertNotNull(plugins);
        for (PluginDefinitionDTO plugin : plugins) {
            assertNotNull(plugin.getType());
            assertNotNull(plugin.getName());
            assertNotNull(plugin.getIcon());
            assertNotNull(plugin.getComponentType());
            assertNotNull(plugin.getConfigSchema());
        }
    }
}
