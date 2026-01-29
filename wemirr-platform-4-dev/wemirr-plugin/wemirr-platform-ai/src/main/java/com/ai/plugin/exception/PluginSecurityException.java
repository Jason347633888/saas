package com.ai.plugin.exception;

import com.wemirr.framework.commons.exception.CheckedException;

/**
 * 插件安全异常
 */
public class PluginSecurityException extends CheckedException {

    public PluginSecurityException(String message, Object... args) {
        super(message, args);
    }

    public PluginSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
