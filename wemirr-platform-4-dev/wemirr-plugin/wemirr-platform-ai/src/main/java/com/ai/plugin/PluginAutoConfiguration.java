package com.ai.plugin;

import com.ai.plugin.impl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * 插件自动配置类
 * 注册内置的字段类型插件
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
public class PluginAutoConfiguration {

    private final PluginRegistry pluginRegistry;

    /**
     * 注册文本插件
     */
    @Bean
    @ConditionalOnMissingBean(name = "textFieldPlugin")
    public TextFieldPlugin textFieldPlugin() {
        return new TextFieldPlugin();
    }

    /**
     * 注册长文本插件
     */
    @Bean
    @ConditionalOnMissingBean(name = "textareaFieldPlugin")
    public TextareaFieldPlugin textareaFieldPlugin() {
        return new TextareaFieldPlugin();
    }

    /**
     * 注册数值插件
     */
    @Bean
    @ConditionalOnMissingBean(name = "numberFieldPlugin")
    public NumberFieldPlugin numberFieldPlugin() {
        return new NumberFieldPlugin();
    }

    /**
     * 注册日期插件
     */
    @Bean
    @ConditionalOnMissingBean(name = "dateFieldPlugin")
    public DateFieldPlugin dateFieldPlugin() {
        return new DateFieldPlugin();
    }

    /**
     * 注册日期时间插件
     */
    @Bean
    @ConditionalOnMissingBean(name = "dateTimeFieldPlugin")
    public DateTimeFieldPlugin dateTimeFieldPlugin() {
        return new DateTimeFieldPlugin();
    }

    /**
     * 注册下拉选择插件
     */
    @Bean
    @ConditionalOnMissingBean(name = "selectFieldPlugin")
    public SelectFieldPlugin selectFieldPlugin() {
        return new SelectFieldPlugin();
    }

    /**
     * 注册关联选择插件
     */
    @Bean
    @ConditionalOnMissingBean(name = "relationFieldPlugin")
    public RelationFieldPlugin relationFieldPlugin() {
        return new RelationFieldPlugin();
    }

    /**
     * 注册是/否插件
     */
    @Bean
    @ConditionalOnMissingBean(name = "booleanFieldPlugin")
    public BooleanFieldPlugin booleanFieldPlugin() {
        return new BooleanFieldPlugin();
    }

    /**
     * 注册所有内置插件到注册中心
     */
    @Bean
    @DependsOn({"textFieldPlugin", "textareaFieldPlugin", "numberFieldPlugin",
            "dateFieldPlugin", "dateTimeFieldPlugin", "selectFieldPlugin",
            "relationFieldPlugin", "booleanFieldPlugin"})
    public PluginRegister pluginRegister(
            TextFieldPlugin textFieldPlugin,
            TextareaFieldPlugin textareaFieldPlugin,
            NumberFieldPlugin numberFieldPlugin,
            DateFieldPlugin dateFieldPlugin,
            DateTimeFieldPlugin dateTimeFieldPlugin,
            SelectFieldPlugin selectFieldPlugin,
            RelationFieldPlugin relationFieldPlugin,
            BooleanFieldPlugin booleanFieldPlugin) {

        log.info("开始注册内置字段类型插件...");

        pluginRegistry.register(textFieldPlugin);
        pluginRegistry.register(textareaFieldPlugin);
        pluginRegistry.register(numberFieldPlugin);
        pluginRegistry.register(dateFieldPlugin);
        pluginRegistry.register(dateTimeFieldPlugin);
        pluginRegistry.register(selectFieldPlugin);
        pluginRegistry.register(relationFieldPlugin);
        pluginRegistry.register(booleanFieldPlugin);

        log.info("内置字段类型插件注册完成，共注册 {} 个插件", pluginRegistry.getPluginCount());

        return new PluginRegister();
    }

    /**
     * 空实现类，用于依赖管理
     */
    public static class PluginRegister {
    }
}
