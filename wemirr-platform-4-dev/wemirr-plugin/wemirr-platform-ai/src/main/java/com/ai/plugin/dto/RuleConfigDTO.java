package com.ai.plugin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 判定规则DTO
 * 用于配置字段的合格判定规则
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleConfigDTO {

    /**
     * 规则类型
     */
    private String ruleType;

    /**
     * 最小值
     */
    private Object minValue;

    /**
     * 最大值
     */
    private Object maxValue;

    /**
     * 规则配置
     */
    private Map<String, Object> config;

    /**
     * 规则说明
     */
    private String description;

    /**
     * 是否启用该规则
     */
    private boolean enabled;

    /**
     * 获取判定结果
     *
     * @param value 待判定的值
     * @return 是否合格
     */
    public boolean evaluate(Object value) {
        if (!enabled || value == null) {
            return true;
        }

        return switch (ruleType) {
            case "range" -> evaluateRange(value);
            case "comparison" -> evaluateComparison(value);
            case "pattern" -> evaluatePattern(value);
            case "enum" -> evaluateEnum(value);
            default -> true;
        };
    }

    private boolean evaluateRange(Object value) {
        if (value instanceof Comparable<?> comparable) {
            if (minValue != null && comparable.compareTo(minValue) < 0) {
                return false;
            }
            if (maxValue != null && comparable.compareTo(maxValue) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateComparison(Object value) {
        // 比较规则实现
        return true;
    }

    private boolean evaluatePattern(Object value) {
        // 模式匹配规则实现
        return true;
    }

    private boolean evaluateEnum(Object value) {
        // 枚举规则实现
        return true;
    }
}
