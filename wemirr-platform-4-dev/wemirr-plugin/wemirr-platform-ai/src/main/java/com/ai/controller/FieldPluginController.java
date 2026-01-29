package com.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ai.plugin.FieldTypePlugin;
import com.ai.plugin.ValidationResult;
import com.ai.plugin.dto.FieldConfigDTO;
import com.ai.plugin.dto.PluginDefinitionDTO;
import com.ai.plugin.dto.PluginRegisterRequest;
import com.ai.service.FieldPluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 字段插件控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/field-plugin")
@Tag(name = "字段插件管理", description = "动态表单字段插件相关接口")
@RequiredArgsConstructor
@Validated
public class FieldPluginController {

    private final FieldPluginService fieldPluginService;

    /**
     * 获取所有插件 - 需要认证
     */
    @GetMapping("/list")
    @SaCheckPermission("plugin:field:list")
    @Operation(summary = "获取所有字段插件")
    public ResponseEntity<List<PluginDefinitionDTO>> getAllPlugins() {
        List<PluginDefinitionDTO> plugins = fieldPluginService.getAllPlugins();
        return ResponseEntity.ok(plugins);
    }

    /**
     * 根据类型获取插件 - 需要认证
     */
    @GetMapping("/{type}")
    @SaCheckPermission("plugin:field:query")
    @Operation(summary = "根据类型获取字段插件")
    public ResponseEntity<PluginDefinitionDTO> getPluginByType(@PathVariable String type) {
        return fieldPluginService.getPluginByType(type)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * 校验字段值 - 需要认证
     */
    @PostMapping("/validate")
    @SaCheckPermission("plugin:field:validate")
    @Operation(summary = "校验字段值")
    public ResponseEntity<ValidationResult> validateField(@Valid @RequestBody FieldConfigDTO config) {
        ValidationResult result = fieldPluginService.validateField(config);
        return ResponseEntity.ok(result);
    }

    /**
     * 处理字段值 - 需要认证
     */
    @PostMapping("/process")
    @SaCheckPermission("plugin:field:process")
    @Operation(summary = "处理字段值")
    public ResponseEntity<Map<String, Object>> processFieldValue(@Valid @RequestBody FieldConfigDTO config) {
        Object processedValue = fieldPluginService.processFieldValue(config);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "value", processedValue
        ));
    }

    /**
     * 注册插件 - 需要管理员权限
     */
    @PostMapping("/register")
    @SaCheckPermission("plugin:field:register")
    @Operation(summary = "注册字段插件")
    public ResponseEntity<Void> registerPlugin(@Valid @RequestBody PluginRegisterRequest request) {
        FieldTypePlugin plugin = new DynamicFieldPlugin(
                request.getType(),
                request.getName(),
                request.getIcon(),
                request.getComponentType(),
                request.getConfigSchema()
        );
        fieldPluginService.registerPlugin(plugin);
        return ResponseEntity.ok().build();
    }

    /**
     * 注销插件 - 需要管理员权限
     */
    @DeleteMapping("/{type}")
    @SaCheckPermission("plugin:field:unregister")
    @Operation(summary = "注销字段插件")
    public ResponseEntity<Void> unregisterPlugin(@PathVariable String type) {
        boolean removed = fieldPluginService.unregisterPlugin(type);
        if (!removed) {
            log.warn("尝试注销插件失败或被拒绝: {}", type);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 动态字段插件实现
     */
    private static class DynamicFieldPlugin implements FieldTypePlugin {

        private final String type;
        private final String name;
        private final String icon;
        private final String componentType;
        private final Map<String, Object> configSchema;

        public DynamicFieldPlugin(String type, String name, String icon,
                                  String componentType, Map<String, Object> configSchema) {
            this.type = type;
            this.name = name;
            this.icon = icon;
            this.componentType = componentType;
            this.configSchema = configSchema != null ? Map.copyOf(configSchema) : Map.of();
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getIcon() {
            return icon;
        }

        @Override
        public String getComponentType() {
            return componentType;
        }

        @Override
        public Map<String, Object> getConfigSchema() {
            return configSchema;
        }

        @Override
        public Map<String, Object> getDefaultConfig() {
            return Map.of();
        }

        @Override
        public ValidationResult validate(Object value, Map<String, Object> config) {
            return ValidationResult.success();
        }

        @Override
        public Object processValue(Object value, Map<String, Object> config) {
            return value;
        }
    }
}
