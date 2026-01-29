package com.ai.plugin;

import com.ai.plugin.exception.PluginSecurityException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件注册中心
 * 管理所有已注册的字段类型插件
 */
@Slf4j
@Component
public class PluginRegistry {

    /**
     * 插件缓存
     */
    private final Map<String, FieldTypePlugin> plugins = new ConcurrentHashMap<>();

    /**
     * 内置插件白名单 - 这些插件不能被删除或覆盖
     */
    private static final Set<String> BUILT_IN_PLUGIN_TYPES = Set.of(
            "text",           // 文本输入
            "textarea",       // 多行文本
            "number",         // 数字
            "date",           // 日期
            "datetime",       // 日期时间
            "select",         // 下拉选择
            "boolean",        // 布尔值
            "relation"        // 关联字段
    );

    /**
     * 插件类型最大长度
     */
    private static final int MAX_TYPE_LENGTH = 100;

    /**
     * 注册插件
     *
     * @param plugin 字段类型插件
     * @return 注册是否成功
     */
    public boolean register(FieldTypePlugin plugin) {
        if (plugin == null || plugin.getType() == null) {
            log.warn("尝试注册无效插件：插件或类型为空");
            return false;
        }

        String type = plugin.getType();

        // 类型长度验证
        if (type.length() > MAX_TYPE_LENGTH) {
            log.warn("尝试注册插件，类型长度超限: {} (最大{}字符)", type, MAX_TYPE_LENGTH);
            return false;
        }

        // 类型格式验证
        if (!type.matches("^[a-zA-Z][a-zA-Z0-9_-]*$")) {
            log.warn("尝试注册插件，类型格式非法: {}", type);
            return false;
        }

        // 检查是否为内置插件
        if (BUILT_IN_PLUGIN_TYPES.contains(type)) {
            log.warn("禁止覆盖内置插件: {}", type);
            throw new PluginSecurityException("禁止覆盖内置插件: {}", type);
        }

        FieldTypePlugin existing = plugins.get(type);
        if (existing != null) {
            log.info("动态插件将被覆盖: {}", type);
        }

        plugins.put(type, plugin);
        log.debug("注册插件成功: {} - {}", type, plugin.getName());
        return true;
    }

    /**
     * 注销插件
     *
     * @param type 插件类型
     * @return 是否成功注销
     */
    public boolean unregister(String type) {
        if (type == null || type.isEmpty()) {
            return false;
        }

        // 检查是否为内置插件
        if (BUILT_IN_PLUGIN_TYPES.contains(type)) {
            log.warn("禁止注销内置插件: {}", type);
            throw new PluginSecurityException("禁止注销内置插件: {}", type);
        }

        FieldTypePlugin removed = plugins.remove(type);
        if (removed != null) {
            log.info("注销插件成功: {}", type);
            return true;
        }
        return false;
    }

    /**
     * 获取插件
     *
     * @param type 插件类型
     * @return 插件实例，不存在返回空
     */
    public Optional<FieldTypePlugin> getPlugin(String type) {
        if (type == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(plugins.get(type));
    }

    /**
     * 获取所有动态插件（排除内置插件）
     *
     * @return 动态插件列表
     */
    public List<FieldTypePlugin> getDynamicPlugins() {
        List<FieldTypePlugin> result = new ArrayList<>();
        for (Map.Entry<String, FieldTypePlugin> entry : plugins.entrySet()) {
            if (!BUILT_IN_PLUGIN_TYPES.contains(entry.getKey())) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    /**
     * 获取所有插件
     *
     * @return 插件列表
     */
    public List<FieldTypePlugin> getAllPlugins() {
        return new ArrayList<>(plugins.values());
    }

    /**
     * 检查插件是否已注册
     *
     * @param type 插件类型
     * @return 是否已注册
     */
    public boolean isRegistered(String type) {
        return type != null && plugins.containsKey(type);
    }

    /**
     * 检查是否为内置插件
     *
     * @param type 插件类型
     * @return 是否为内置插件
     */
    public boolean isBuiltIn(String type) {
        return type != null && BUILT_IN_PLUGIN_TYPES.contains(type);
    }

    /**
     * 获取已注册插件数量
     *
     * @return 插件数量
     */
    public int getPluginCount() {
        return plugins.size();
    }

    /**
     * 获取动态插件数量
     *
     * @return 动态插件数量
     */
    public int getDynamicPluginCount() {
        int count = 0;
        for (String type : plugins.keySet()) {
            if (!BUILT_IN_PLUGIN_TYPES.contains(type)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 清空所有动态插件（不清除内置插件）
     *
     * @return 被清空的插件数量
     */
    public int clearDynamicPlugins() {
        int removed = 0;
        List<String> toRemove = new ArrayList<>();

        for (String type : plugins.keySet()) {
            if (!BUILT_IN_PLUGIN_TYPES.contains(type)) {
                toRemove.add(type);
            }
        }

        for (String type : toRemove) {
            plugins.remove(type);
            removed++;
        }

        if (removed > 0) {
            log.info("已清空 {} 个动态插件", removed);
        }
        return removed;
    }

    /**
     * 获取内置插件白名单
     *
     * @return 内置插件类型集合
     */
    public static Set<String> getBuiltInPluginTypes() {
        return Collections.unmodifiableSet(BUILT_IN_PLUGIN_TYPES);
    }

    /**
     * 初始化注册内置插件
     * 由Spring调用
     */
    @PostConstruct
    public void init() {
        log.info("PluginRegistry 初始化完成，共注册 {} 个内置插件", plugins.size());
    }
}
