package com.ai.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PluginRegistry 测试类
 */
@DisplayName("PluginRegistry 测试")
class PluginRegistryTest {

    private PluginRegistry registry;
    private FieldTypePlugin mockPlugin1;
    private FieldTypePlugin mockPlugin2;

    @BeforeEach
    void setUp() {
        registry = new PluginRegistry();

        // 创建模拟插件1
        mockPlugin1 = new FieldTypePlugin() {
            @Override
            public String getType() {
                return "text";
            }

            @Override
            public String getName() {
                return "文本";
            }

            @Override
            public String getIcon() {
                return "form";
            }

            @Override
            public String getComponentType() {
                return "Input";
            }

            @Override
            public Map<String, Object> getConfigSchema() {
                return Map.of(
                        "maxLength", Map.of("type", "number", "default", 500),
                        "minLength", Map.of("type", "number", "default", 0)
                );
            }
        };

        // 创建模拟插件2
        mockPlugin2 = new FieldTypePlugin() {
            @Override
            public String getType() {
                return "number";
            }

            @Override
            public String getName() {
                return "数值";
            }

            @Override
            public String getIcon() {
                return "number";
            }

            @Override
            public String getComponentType() {
                return "InputNumber";
            }

            @Override
            public Map<String, Object> getConfigSchema() {
                return Map.of(
                        "min", Map.of("type", "number", "default", 0),
                        "max", Map.of("type", "number", "default", 999999)
                );
            }

            @Override
            public ValidationResult validate(Object value, Map<String, Object> config) {
                if (value == null) {
                    return ValidationResult.success();
                }
                try {
                    double num = Double.parseDouble(value.toString());
                    if (config.containsKey("min") && num < (double) config.get("min")) {
                        return ValidationResult.failure("值不能小于最小值");
                    }
                    if (config.containsKey("max") && num > (double) config.get("max")) {
                        return ValidationResult.failure("值不能大于最大值");
                    }
                    return ValidationResult.success();
                } catch (NumberFormatException e) {
                    return ValidationResult.failure("必须是有效的数字");
                }
            }
        };
    }

    @Test
    @DisplayName("注册插件后应该能够获取到该插件")
    void registerPlugin_shouldBe retrievable() {
        registry.register(mockPlugin1);

        Optional<FieldTypePlugin> result = registry.getPlugin("text");

        assertTrue(result.isPresent());
        assertEquals("text", result.get().getType());
        assertEquals("文本", result.get().getName());
    }

    @Test
    @DisplayName("获取不存在的插件应该返回空")
    void getPlugin_notExists_shouldReturnEmpty() {
        Optional<FieldTypePlugin> result = registry.getPlugin("not_exists");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("注销插件后应该无法获取")
    void unregisterPlugin_shouldNotBeRetrievable() {
        registry.register(mockPlugin1);
        registry.unregister("text");

        Optional<FieldTypePlugin> result = registry.getPlugin("text");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("注销不存在的插件不应该抛出异常")
    void unregister_notExists_shouldNotThrowException() {
        assertDoesNotThrow(() -> registry.unregister("not_exists"));
    }

    @Test
    @DisplayName("获取所有插件应该返回已注册的所有插件")
    void getAllPlugins_shouldReturnAllRegistered() {
        registry.register(mockPlugin1);
        registry.register(mockPlugin2);

        List<FieldTypePlugin> plugins = registry.getAllPlugins();

        assertEquals(2, plugins.size());
    }

    @Test
    @DisplayName("重复注册相同类型应该覆盖旧插件")
    void registerDuplicateType_shouldOverwriteOld() {
        FieldTypePlugin newPlugin = new FieldTypePlugin() {
            @Override
            public String getType() {
                return "text";
            }

            @Override
            public String getName() {
                return "新文本插件";
            }

            @Override
            public String getIcon() {
                return "edit";
            }

            @Override
            public String getComponentType() {
                return "TextArea";
            }
        };

        registry.register(mockPlugin1);
        registry.register(newPlugin);

        Optional<FieldTypePlugin> result = registry.getPlugin("text");

        assertTrue(result.isPresent());
        assertEquals("新文本插件", result.get().getName());
    }

    @Test
    @DisplayName("获取所有插件应该在空注册表时返回空列表")
    void getAllPlugins_emptyRegistry_shouldReturnEmptyList() {
        List<FieldTypePlugin> plugins = registry.getAllPlugins();

        assertTrue(plugins.isEmpty());
    }

    @Test
    @DisplayName("注册多个插件后注销其中一个不应该影响其他插件")
    void unregisterOne_shouldNotAffectOthers() {
        registry.register(mockPlugin1);
        registry.register(mockPlugin2);
        registry.unregister("text");

        Optional<FieldTypePlugin> textPlugin = registry.getPlugin("text");
        Optional<FieldTypePlugin> numberPlugin = registry.getPlugin("number");
        List<FieldTypePlugin> allPlugins = registry.getAllPlugins();

        assertTrue(textPlugin.isEmpty());
        assertTrue(numberPlugin.isPresent());
        assertEquals(1, allPlugins.size());
    }

    @Test
    @DisplayName("注册表应该支持并发访问")
    void register_shouldSupportConcurrentAccess() throws InterruptedException {
        Thread thread1 = new Thread(() -> registry.register(mockPlugin1));
        Thread thread2 = new Thread(() -> registry.register(mockPlugin2));

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        List<FieldTypePlugin> plugins = registry.getAllPlugins();
        assertEquals(2, plugins.size());
    }
}
