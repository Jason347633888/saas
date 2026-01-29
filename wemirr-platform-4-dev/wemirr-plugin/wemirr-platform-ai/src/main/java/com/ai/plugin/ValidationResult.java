package com.ai.plugin;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 校验结果
 * 用于表示字段校验的结果状态
 */
@Getter
@Setter
public class ValidationResult {

    /**
     * 是否校验通过
     */
    private boolean valid;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 错误码
     */
    private int errorCode;

    /**
     * 附加数据
     */
    private Map<String, Object> data;

    private ValidationResult() {
        this.valid = true;
        this.errorMessage = null;
        this.errorCode = 0;
        this.data = new HashMap<>();
    }

    private ValidationResult(boolean valid, String errorMessage, int errorCode) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.data = new HashMap<>();
    }

    /**
     * 创建成功的校验结果
     */
    public static ValidationResult success() {
        return new ValidationResult();
    }

    /**
     * 创建失败的校验结果
     *
     * @param errorMessage 错误信息
     */
    public static ValidationResult failure(String errorMessage) {
        return new ValidationResult(false, errorMessage, -1);
    }

    /**
     * 创建失败的校验结果
     *
     * @param errorMessage 错误信息
     * @param errorCode    错误码
     */
    public static ValidationResult failure(String errorMessage, int errorCode) {
        return new ValidationResult(false, errorMessage, errorCode);
    }

    /**
     * 添加附加数据
     *
     * @param key   键
     * @param value 值
     * @return this
     */
    public ValidationResult withData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * 添加附加数据
     *
     * @param map 数据映射
     * @return this
     */
    public ValidationResult withData(Map<String, Object> map) {
        if (map != null) {
            this.data.putAll(map);
        }
        return this;
    }

    /**
     * 添加附加数据
     *
     * @param key   键
     * @param value 值
     */
    public void putData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
    }

    /**
     * 检查是否有错误
     */
    public boolean hasErrors() {
        return !this.valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return valid == that.valid &&
                errorCode == that.errorCode &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, errorMessage, errorCode, data);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorCode=" + errorCode +
                ", data=" + data +
                '}';
    }
}
