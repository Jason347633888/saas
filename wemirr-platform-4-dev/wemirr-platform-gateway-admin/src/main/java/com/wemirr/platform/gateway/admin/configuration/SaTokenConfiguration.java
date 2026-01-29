/*
 * Copyright (c) 2023 WEMIRR-PLATFORM Authors. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wemirr.platform.gateway.admin.configuration;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Sa-Token 配置
 *
 * @author Levin
 */
@Slf4j
@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {

    /**
     * Sa-Token 权限认证接口扩展
     */
    @Bean
    public StpInterface stpInterface() {
        return new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                return List.of("gateway:route:list", "gateway:route:create", "gateway:route:modify",
                        "gateway:route:publish", "gateway:route:remove",
                        "gateway:blacklist:list", "gateway:blacklist:create", "gateway:blacklist:modify", "gateway:blacklist:remove",
                        "gateway:limit:list", "gateway:limit:create", "gateway:limit:modify", "gateway:limit:remove",
                        "gateway:discovery:list");
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                return List.of("admin");
            }
        };
    }

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器 - 先检查登录
        registry.addInterceptor(new SaInterceptor(handler -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/health",
                        "/actuator/info",
                        "/doc.html",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/error"
                );
    }

    /**
     * 健康检查端点（无需认证）
     */
    @RestController
    @RequestMapping("/actuator")
    @SaIgnore
    public static class HealthController {
        @GetMapping("/health")
        public String health() {
            return "UP";
        }

        @GetMapping("/info")
        public String info() {
            return "Gateway Admin Service";
        }
    }
}
