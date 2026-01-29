package com.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 插件安全配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "plugin.security")
public class PluginSecurityProperties {

    /**
     * 速率限制配置
     */
    private RateLimit rateLimit = new RateLimit();

    @Data
    public static class RateLimit {
        /**
         * 是否启用速率限制
         */
        private boolean enabled = true;

        /**
         * 注册操作限流 - 每分钟次数
         */
        private int registerPerMinute = 10;

        /**
         * 注销操作限流 - 每分钟次数
         */
        private int unregisterPerMinute = 5;

        /**
         * 通用操作限流 - 每分钟次数
         */
        private int generalPerMinute = 60;
    }
}
