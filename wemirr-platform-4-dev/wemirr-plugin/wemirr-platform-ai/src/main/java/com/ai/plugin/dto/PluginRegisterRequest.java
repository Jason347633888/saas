package com.ai.plugin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * 插件注册请求DTO
 */
@Data
public class PluginRegisterRequest {

    @NotBlank(message = "插件类型不能为空")
    @Size(max = 50, message = "插件类型不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]*$", message = "插件类型必须以字母开头，只能包含字母、数字、下划线和连字符")
    @Schema(description = "插件类型标识")
    private String type;

    @NotBlank(message = "插件名称不能为空")
    @Size(max = 100, message = "插件名称不能超过100个字符")
    @Schema(description = "插件显示名称")
    private String name;

    @Size(max = 100, message = "图标名称不能超过100个字符")
    @Schema(description = "图标标识")
    private String icon;

    @NotBlank(message = "组件类型不能为空")
    @Size(max = 50, message = "组件类型不能超过50个字符")
    @Schema(description = "前端组件类型")
    private String componentType;

    @Size(max = 5000, message = "配置模式大小不能超过5KB")
    @Schema(description = "插件配置模式")
    private Map<String, Object> configSchema;
}
