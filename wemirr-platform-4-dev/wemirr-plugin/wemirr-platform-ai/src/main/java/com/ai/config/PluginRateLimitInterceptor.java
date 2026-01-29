package com.ai.config;

import com.ai.config.PluginSecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 插件操作速率限制拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PluginRateLimitInterceptor implements HandlerInterceptor {

    private final PluginSecurityProperties securityProperties;

    /**
     * 按IP地址的限流桶
     */
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果未启用速率限制，直接放行
        if (!securityProperties.getRateLimit().isEnabled()) {
            return true;
        }

        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        Bucket bucket = ipBuckets.computeIfAbsent(clientIp, k -> createBucket());

        int limit = determineLimit(path);
        Bucket limitedBucket = bucket.toBuilder()
                .addLimit(Bandwidth.classic(limit, Refill.greedy(limit, TimeUnit.MINUTES)))
                .build();

        if (!limitedBucket.tryConsume(1)) {
            log.warn("速率限制触发 - IP: {}, 路径: {}", clientIp, path);
            response.setStatus(429);
            response.setContentType("application/json");
            response.setHeader("Retry-After", "60");
            try {
                response.getWriter().write("{\"error\":\"请求过于频繁，请稍后再试\",\"code\":\"RATE_LIMIT_EXCEEDED\"}");
            } catch (Exception e) {
                log.error("响应速率限制错误", e);
            }
            return false;
        }

        return true;
    }

    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(
                        securityProperties.getRateLimit().getGeneralPerMinute(),
                        Refill.greedy(securityProperties.getRateLimit().getGeneralPerMinute(), TimeUnit.MINUTES)
                ))
                .build();
    }

    private int determineLimit(String path) {
        if (path.contains("/register")) {
            return securityProperties.getRateLimit().getRegisterPerMinute();
        } else if (path.contains("/unregister") || path.contains("/delete")) {
            return securityProperties.getRateLimit().getUnregisterPerMinute();
        }
        return securityProperties.getRateLimit().getGeneralPerMinute();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    /**
     * 清空限流桶（管理功能）
     */
    public void clearBuckets() {
        ipBuckets.clear();
        log.info("已清空所有速率限制桶");
    }

    /**
     * 简化的限流桶实现（不依赖外部库）
     */
    private static class Bucket {
        private final int capacity;
        private final long refillPerMinute;
        private volatile long tokens;
        private volatile long lastRefillTime;

        Bucket(int capacity, int refillPerMinute) {
            this.capacity = capacity;
            this.refillPerMinute = refillPerMinute;
            this.tokens = capacity;
            this.lastRefillTime = System.currentTimeMillis();
        }

        boolean tryConsume(int count) {
            refillIfNeeded();
            if (tokens >= count) {
                tokens -= count;
                return true;
            }
            return false;
        }

        private void refillIfNeeded() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            if (elapsed >= TimeUnit.MINUTES.toMillis(1)) {
                tokens = Math.min(capacity, tokens + refillPerMinute);
                lastRefillTime = now;
            }
        }

        Bucket toBuilder() {
            return new Bucket(capacity, (int) refillPerMinute);
        }
    }

    /**
     * 简化的带宽限制实现
     */
    private static class Bandwidth {
        private final int capacity;
        private final Refill refill;

        Bandwidth(int capacity, Refill refill) {
            this.capacity = capacity;
            this.refill = refill;
        }

        static Bandwidth classic(int capacity, Refill refill) {
            return new Bandwidth(capacity, refill);
        }
    }

    /**
     * 简化的填充策略实现
     */
    private static class Refill {
        private final int tokens;
        private final long period;
        private final TimeUnit unit;

        Refill(int tokens, long period, TimeUnit unit) {
            this.tokens = tokens;
            this.period = period;
            this.unit = unit;
        }

        static Refill greedy(int tokens, TimeUnit unit) {
            return new Refill(tokens, 1, unit);
        }
    }
}
