package com.ai.plugin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidationResult 测试类
 */
@DisplayName("ValidationResult 测试")
class ValidationResultTest {

    @Test
    @DisplayName("成功校验结果应该返回成功状态")
    void successResult_shouldReturnValidStatus() {
        ValidationResult result = ValidationResult.success();

        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
        assertEquals(0, result.getErrorCode());
    }

    @Test
    @DisplayName("成功校验结果应该可以添加额外数据")
    void successResult_withData_shouldContainData() {
        Map<String, Object> data = Map.of("field", "value", "count", 5);
        ValidationResult result = ValidationResult.success().withData(data);

        assertTrue(result.isValid());
        assertEquals("value", result.getData().get("field"));
        assertEquals(5, result.getData().get("count"));
    }

    @Test
    @DisplayName("失败校验结果应该返回失败状态")
    void failureResult_shouldReturnInvalidStatus() {
        ValidationResult result = ValidationResult.failure("字段不能为空");

        assertFalse(result.isValid());
        assertEquals("字段不能为空", result.getErrorMessage());
        assertEquals(-1, result.getErrorCode());
    }

    @Test
    @DisplayName("失败校验结果应该支持自定义错误码")
    void failureResult_withErrorCode_shouldReturnCorrectCode() {
        ValidationResult result = ValidationResult.failure("超出范围", 1001);

        assertFalse(result.isValid());
        assertEquals("超出范围", result.getErrorMessage());
        assertEquals(1001, result.getErrorCode());
    }

    @Test
    @DisplayName("失败校验结果应该可以添加额外数据")
    void failureResult_withData_shouldContainData() {
        Map<String, Object> data = Map.of("min", 1, "max", 100, "actual", 200);
        ValidationResult result = ValidationResult.failure("超出范围")
                .withData(data);

        assertFalse(result.isValid());
        assertEquals(100, result.getData().get("max"));
        assertEquals(200, result.getData().get("actual"));
    }

    @Test
    @DisplayName("链式调用应该正常工作")
    void chainCalls_shouldWork() {
        ValidationResult result = ValidationResult.failure("错误")
                .withData(Map.of("key", "value"))
                .withData(Map.of("key2", "value2"));

        assertFalse(result.isValid());
        assertEquals("错误", result.getErrorMessage());
        assertEquals("value", result.getData().get("key"));
        assertEquals("value2", result.getData().get("key2"));
    }

    @Test
    @DisplayName("合并数据应该正确合并多个数据")
    void mergeData_shouldCombineAllData() {
        ValidationResult result = ValidationResult.success();
        result.putData("a", 1);
        result.putData("b", 2);

        assertEquals(1, result.getData().get("a"));
        assertEquals(2, result.getData().get("b"));
    }

    @Test
    @DisplayName("默认错误码应该是-1")
    void defaultErrorCode_shouldBeNegativeOne() {
        ValidationResult result = ValidationResult.failure("测试错误");

        assertEquals(-1, result.getErrorCode());
    }
}
