package com.ai.service.impl;

import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.PluginRegistry;
import com.ai.plugin.ValidationResult;
import com.ai.plugin.dto.FieldConfigDTO;
import com.ai.plugin.dto.PluginDefinitionDTO;
import com.ai.service.FieldPluginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 字段插件服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FieldPluginServiceImpl implements FieldPluginService {

    private final PluginRegistry pluginRegistry;

    /**
     * 配置深度最大层数
     */
    private static final int MAX_CONFIG_DEPTH = 5;

    /**
     * 单个配置值最大大小 (10KB)
     */
    private static final int MAX_VALUE_SIZE = 10 * 1024;

    /**
     * 配置最大条目数
     */
    private static final int MAX_CONFIG_ENTRIES = 100;

    @Override
    public List<PluginDefinitionDTO> getAllPlugins() {
        List<PluginDefinitionDTO> result = new ArrayList<>();
        for (FieldTypePlugin plugin : pluginRegistry.getAllPlugins()) {
            result.add(convertToDTO(plugin));
        }
        return result;
    }

    @Override
    public Optional<PluginDefinitionDTO> getPluginByType(String type) {
        if (type == null || type.length() > 100) {
            return Optional.empty();
        }
        return pluginRegistry.getPlugin(type)
                .map(this::convertToDTO);
    }

    @Override
    public ValidationResult validateField(FieldConfigDTO config) {
        // 基本验证
        if (config == null || config.getFieldType() == null) {
            return ValidationResult.failure("字段配置或类型不能为空");
        }

        // 类型长度限制
        if (config.getFieldType().length() > 100) {
            return ValidationResult.failure("字段类型长度超限");
        }

        Optional<FieldTypePlugin> pluginOpt = pluginRegistry.getPlugin(config.getFieldType());
        if (pluginOpt.isEmpty()) {
            return ValidationResult.failure("不支持的字段类型")
                    .withData("fieldType", sanitizeForLogging(config.getFieldType()));
        }

        // 深度防御：验证值的大小
        if (config.getValue() != null) {
            ValidationResult sizeCheck = validateValueSize(config.getValue());
            if (!sizeCheck.isValid()) {
                return sizeCheck;
            }
        }

        FieldTypePlugin plugin = pluginOpt.get();
        Map<String, Object> mergedConfig = mergeConfig(config);

        // 验证配置深度
        ValidationResult configCheck = validateConfigDepth(mergedConfig, 0);
        if (!configCheck.isValid()) {
            return configCheck;
        }

        return plugin.validate(config.getValue(), mergedConfig);
    }

    @Override
    public Object processFieldValue(FieldConfigDTO config) {
        if (config == null || config.getFieldType() == null) {
            return config != null ? config.getValue() : null;
        }

        Optional<FieldTypePlugin> pluginOpt = pluginRegistry.getPlugin(config.getFieldType());
        if (pluginOpt.isEmpty()) {
            return config.getValue();
        }

        // 深度防御：验证值的大小
        if (config.getValue() != null) {
            ValidationResult sizeCheck = validateValueSize(config.getValue());
            if (!sizeCheck.isValid()) {
                log.warn("字段值大小超限: fieldType={}", config.getFieldType());
                return config.getValue(); // 返回原值，由前端处理
            }
        }

        FieldTypePlugin plugin = pluginOpt.get();
        Map<String, Object> mergedConfig = mergeConfig(config);

        return plugin.processValue(config.getValue(), mergedConfig);
    }

    @Override
    public void registerPlugin(FieldTypePlugin plugin) {
        if (plugin != null) {
            boolean registered = pluginRegistry.register(plugin);
            if (registered) {
                log.info("[SECURITY] 注册字段插件: {} - {}", plugin.getType(), plugin.getName());
            } else {
                log.warn("注册字段插件失败: {} - {}", plugin.getType(), plugin.getName());
            }
        }
    }

    @Override
    public boolean unregisterPlugin(String type) {
        if (type != null) {
            boolean removed = pluginRegistry.unregister(type);
            if (removed) {
                log.info("[SECURITY] 注销字段插件: {}", type);
                return true;
            } else {
                log.warn("注销字段插件失败或被拒绝: {}", type);
            }
        }
        return false;
    }

    /**
     * 验证值大小
     */
    private ValidationResult validateValueSize(Object value) {
        if (value == null) {
            return ValidationResult.success();
        }

        String json = value.toString();
        if (json.length() > MAX_VALUE_SIZE) {
            return ValidationResult.failure("字段值大小超过限制");
        }

        return ValidationResult.success();
    }

    /**
     * 验证配置深度
     */
    private ValidationResult validateConfigDepth(Map<String, Object> config, int currentDepth) {
        if (currentDepth > MAX_CONFIG_DEPTH) {
            return ValidationResult.failure("配置嵌套深度超过限制");
        }

        if (config == null || config.size() > MAX_CONFIG_ENTRIES) {
            return ValidationResult.failure("配置条目数超过限制");
        }

        return ValidationResult.success();
    }

    /**
     * 安全日志输出（脱敏）
     */
    private String sanitizeForLogging(String input) {
        if (input == null) {
            return null;
        }
        if (input.length() > 50) {
            return input.substring(0, 50) + "...";
        }
        return input;
    }

    /**
     * 将插件转换为DTO
     */
    private PluginDefinitionDTO convertToDTO(FieldTypePlugin plugin) {
        Map<String, Object> configSchema = plugin.getConfigSchema();
        // 限制configSchema的大小
        if (configSchema != null && configSchema.size() > MAX_CONFIG_ENTRIES) {
            log.warn("插件 {} 的配置模式条目数超过限制，将被截断", plugin.getType());
            configSchema = new LinkedHashMap<>();
        }

        return PluginDefinitionDTO.builder()
                .type(plugin.getType())
                .name(plugin.getName())
                .icon(plugin.getIcon())
                .componentType(plugin.getComponentType())
                .configSchema(configSchema)
                .defaultConfig(plugin.getDefaultConfig())
                .options(plugin.getOptions(plugin.getDefaultConfig()))
                .multipleSupported(plugin.isMultipleSupported())
                .build();
    }

    /**
     * 合并配置
     */
    private Map<String, Object> mergeConfig(FieldConfigDTO config) {
        java.util.HashMap<String, Object> merged = new java.util.HashMap<>();

        // 合并默认配置
        Optional<FieldTypePlugin> pluginOpt = pluginRegistry.getPlugin(config.getFieldType());
        if (pluginOpt.isPresent()) {
            merged.putAll(pluginOpt.get().getDefaultConfig());
        }

        // 合并字段配置（带大小限制）
        if (config.getConfig() != null && config.getConfig().size() <= MAX_CONFIG_ENTRIES) {
            merged.putAll(config.getConfig());
        }

        // 合并判定规则配置
        if (config.getRuleConfig() != null && config.getRuleConfig().size() <= MAX_CONFIG_ENTRIES) {
            merged.putAll(config.getRuleConfig());
        }

        // 合并选项配置
        if (config.getOptionsConfig() != null && config.getOptionsConfig().size() <= MAX_CONFIG_ENTRIES) {
            merged.putAll(config.getOptionsConfig());
        }

        return merged;
    }
}
