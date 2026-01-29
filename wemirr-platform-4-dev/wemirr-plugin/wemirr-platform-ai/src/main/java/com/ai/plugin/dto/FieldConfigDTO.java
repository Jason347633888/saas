package com.ai.plugin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 字段配置DTO
 * 用于API请求/响应的字段配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldConfigDTO {

    /**
     * 字段名称
     */
    @Size(max = 100, message = "字段名称不能超过100个字符")
    @Schema(description = "字段名称")
    private String fieldName;

    /**
     * 字段标识
     */
    @Size(max = 50, message = "字段标识不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "字段标识必须以字母开头，只能包含字母、数字和下划线")
    @Schema(description = "字段标识")
    private String fieldKey;

    /**
     * 字段类型
     */
    @NotBlank(message = "字段类型不能为空")
    @Size(max = 50, message = "字段类型不能超过50个字符")
    @Schema(description = "字段类型")
    private String fieldType;

    /**
     * 字段值
     */
    @Schema(description = "字段值")
    private Object value;

    /**
     * 是否必填
     */
    @Schema(description = "是否必填")
    private boolean required;

    /**
     * 字段配置
     */
    @Schema(description = "字段配置")
    private Map<String, Object> config;

    /**
     * 排序
     */
    @Max(value = 9999, message = "排序值不能超过9999")
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 判定规则配置
     */
    @Schema(description = "判定规则配置")
    private Map<String, Object> ruleConfig;

    /**
     * 下拉选项配置
     */
    @Schema(description = "下拉选项配置")
    private Map<String, Object> optionsConfig;

    /**
     * 关联模板ID
     */
    @Schema(description = "关联模板ID")
    private Long relationTemplateId;
}
