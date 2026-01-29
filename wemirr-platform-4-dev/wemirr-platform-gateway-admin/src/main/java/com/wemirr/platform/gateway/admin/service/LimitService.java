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
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import com.wemirr.framework.commons.JacksonUtils;
import com.wemirr.platform.gateway.admin.domain.LimitRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wemirr.platform.gateway.admin.service.GatewayRuleConstants.*;

/**
 * 限流规则管理服务
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LimitService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 查询所有限流规则
     */
    public List<LimitRule> query() {
        final Set<Object> keys = stringRedisTemplate.opsForHash().keys(GATEWAY_RULE_LIMIT);
        if (CollectionUtil.isEmpty(keys)) {
            return Lists.newArrayList();
        }
        return stringRedisTemplate.opsForHash().multiGet(GATEWAY_RULE_LIMIT, keys).stream()
                .map(object -> {
                    LimitRule rule = JacksonUtils.toBean(object.toString(), LimitRule.class);
                    if (rule != null) {
                        final Object visits = Optional.ofNullable(stringRedisTemplate.opsForHash()
                                .get(GATEWAY_RULE_LIMIT_VISITS, rule.getId())).orElse("0");
                        rule.setVisits(Long.parseLong(visits.toString()));
                    }
                    return rule;
                }).collect(Collectors.toList());
    }

    /**
     * 保存或更新限流规则
     */
    public void saveOrUpdate(LimitRule rule) {
        if (rule == null) {
            return;
        }
        if (rule.getId() == null) {
            rule.setId(IdUtil.fastSimpleUUID());
        }
        if (rule.getCreateTime() == null) {
            rule.setCreateTime(Instant.now());
        }
        stringRedisTemplate.opsForHash().put(GATEWAY_RULE_LIMIT, rule.getId(), JacksonUtils.toJson(rule));
    }

    /**
     * 删除限流规则
     */
    public void delete(String id) {
        stringRedisTemplate.opsForHash().delete(GATEWAY_RULE_LIMIT, id);
    }
}
