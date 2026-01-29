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

package com.wemirr.platform.gateway.admin.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wemirr.framework.commons.JacksonUtils;
import com.wemirr.framework.commons.exception.CheckedException;
import com.wemirr.platform.gateway.admin.domain.RouteRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.CompositeRouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wemirr.platform.gateway.admin.service.GatewayRuleConstants.GATEWAY_RULE_ROUTE;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * 路由规则管理服务
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final ApplicationContext applicationContext;
    private final StringRedisTemplate stringRedisTemplate;
    private final DiscoveryClient discoveryClient;

    /**
     * 查询所有路由规则
     */
    public List<RouteRule> query() {
        final CompositeRouteDefinitionLocator routeDefinitionLocator = applicationContext.getBean(CompositeRouteDefinitionLocator.class);
        List<RouteDefinition> routeDefinitions = Lists.newArrayList();
        routeDefinitionLocator.getRouteDefinitions().subscribe(routeDefinitions::add);
        final List<String> services = discoveryClient.getServices();
        List<RouteRule> routeRules = stringRedisTemplate.opsForHash().keys(GATEWAY_RULE_ROUTE).stream()
                .map(id -> {
                    Object object = stringRedisTemplate.opsForHash().get(GATEWAY_RULE_ROUTE, id);
                    if (object == null) {
                        return null;
                    }
                    RouteRule rule = JacksonUtils.toBean(object.toString(), RouteRule.class);
                    if (rule.getStatus() == null) {
                        rule.setStatus(false);
                    } else if (rule.getStatus()) {
                        rule.setStatus(services.contains(rule.getName()));
                    }
                    return rule;
                }).collect(toList());
        final List<String> idList = routeRules.stream().map(RouteRule::getId).toList();
        for (RouteDefinition routeDefinition : routeDefinitions) {
            if (idList.contains(routeDefinition.getId()) || StringUtils.contains(routeDefinition.getId(), "CompositeDiscoveryClient_")) {
                continue;
            }
            RouteRule rule = new RouteRule();
            rule.setId(routeDefinition.getId());
            rule.setStatus(true);
            rule.setUri(routeDefinition.getUri().toString());
            rule.setOrder(routeDefinition.getOrder());
            rule.setName(StrUtil.blankToDefault(routeDefinition.getUri().getHost(), rule.getUri().replace("lb:ws://", "").replace("lb:wss://", "")));
            rule.setDynamic(false);
            final List<RouteRule.Filter> filters = routeDefinition.getFilters().stream().map(filterDefinition -> {
                List<RouteRule.Filter.FilterArg> args = filterDefinition.getArgs().entrySet().stream().map(entry -> RouteRule.Filter.FilterArg.builder()
                        .key(entry.getKey()).value(entry.getValue()).build()).collect(toList());
                return RouteRule.Filter.builder().args(args).name(filterDefinition.getName()).build();
            }).collect(toList());
            rule.setFilters(filters);
            routeRules.add(rule);
        }
        return routeRules;
    }

    /**
     * 保存或更新路由规则（仅保存到Redis，由Gateway定时刷新）
     */
    public void saveOrUpdate(RouteRule rule) {
        if (rule == null) {
            throw CheckedException.notFound("rule 不能为空");
        }
        if (rule.getCreateTime() == null) {
            rule.setCreateTime(Instant.now());
        }
        if (rule.getStatus() == null) {
            rule.setStatus(false);
        }
        if (rule.getDynamic() == null) {
            rule.setDynamic(true);
        }
        log.debug("请求参数 - {}", JacksonUtils.toJson(rule));
        stringRedisTemplate.opsForHash().put(GATEWAY_RULE_ROUTE, rule.getId(), JacksonUtils.toJson(rule));
    }

    /**
     * 删除路由规则
     */
    public void delete(String id) {
        stringRedisTemplate.opsForHash().delete(GATEWAY_RULE_ROUTE, id);
    }

    /**
     * 发布路由规则（同步到Gateway内存）
     */
    public boolean publish(String id) {
        final List<String> services = discoveryClient.getServices();
        final Object object = stringRedisTemplate.opsForHash().get(GATEWAY_RULE_ROUTE, id);
        if (object == null) {
            return false;
        }
        RouteRule rule = JacksonUtils.toBean(object.toString(), RouteRule.class);
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(rule.getId());
        routeDefinition.setOrder(rule.getOrder());
        routeDefinition.setUri(URLUtil.toURI(rule.getUri()));
        if (CollectionUtil.isNotEmpty(rule.getPredicates())) {
            final List<PredicateDefinition> predicates = rule.getPredicates().stream().map(predicate -> {
                final HashMap<String, String> args = Maps.newHashMap();
                args.put(predicate.getName(), String.join(",", predicate.getArgs()));
                PredicateDefinition definition = new PredicateDefinition();
                definition.setArgs(args);
                definition.setName(predicate.getName());
                return definition;
            }).collect(toList());
            routeDefinition.setPredicates(predicates);
        }
        if (CollectionUtil.isNotEmpty(rule.getFilters())) {
            List<FilterDefinition> filters = rule.getFilters().stream().map(filter -> {
                FilterDefinition definition = new FilterDefinition();
                final Map<String, String> args = filter.getArgs().stream()
                        .collect(toMap(RouteRule.Filter.FilterArg::getKey, RouteRule.Filter.FilterArg::getValue));
                definition.setArgs(args);
                definition.setName(filter.getName());
                return definition;
            }).collect(toList());
            routeDefinition.setFilters(filters);
        }
        if (services.contains(rule.getName())) {
            rule.setStatus(true);
            stringRedisTemplate.opsForHash().put(GATEWAY_RULE_ROUTE, rule.getId(), JacksonUtils.toJson(rule));
            // 注意：这里不能直接操作Gateway的内存路由，因为不在同一个应用
            // 实际发布需要Gateway订阅Redis变化或调用刷新接口
            return true;
        }
        return false;
    }
}
