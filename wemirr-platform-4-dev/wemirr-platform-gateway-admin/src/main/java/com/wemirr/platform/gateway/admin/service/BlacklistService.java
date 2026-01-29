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
import com.wemirr.platform.gateway.admin.domain.BlacklistRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wemirr.platform.gateway.admin.service.GatewayRuleConstants.GATEWAY_RULE_BLACKLIST;
import static com.wemirr.platform.gateway.admin.service.GatewayRuleConstants.GATEWAY_BLACKLIST_VISITS;

/**
 * 黑名单规则管理服务
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 查询所有黑名单规则
     */
    public List<BlacklistRule> query() {
        final Set<Object> keys = stringRedisTemplate.opsForHash().keys(GATEWAY_RULE_BLACKLIST);
        if (CollectionUtil.isEmpty(keys)) {
            return Lists.newArrayList();
        }
        return stringRedisTemplate.opsForHash().multiGet(GATEWAY_RULE_BLACKLIST, keys).stream()
                .map(object -> {
                    BlacklistRule rule = JacksonUtils.toBean(object.toString(), BlacklistRule.class);
                    if (rule != null) {
                        final Object visits = Optional.ofNullable(stringRedisTemplate.opsForHash()
                                .get(GATEWAY_BLACKLIST_VISITS, rule.getId())).orElse("0");
                        rule.setVisits(Long.parseLong(visits.toString()));
                    }
                    return rule;
                }).collect(Collectors.toList());
    }

    /**
     * 根据ID获取黑名单规则
     */
    public BlacklistRule getById(String id) {
        final Object object = stringRedisTemplate.opsForHash().get(GATEWAY_RULE_BLACKLIST, id);
        if (object == null) {
            return null;
        }
        return JacksonUtils.toBean(object.toString(), BlacklistRule.class);
    }

    /**
     * 保存或更新黑名单规则
     */
    public void saveOrUpdate(BlacklistRule rule) {
        if (rule == null) {
            return;
        }
        if (rule.getId() == null) {
            rule.setId(IdUtil.fastSimpleUUID());
        }
        if (rule.getCreateTime() == null) {
            rule.setCreateTime(Instant.now());
        }
        final String content = JacksonUtils.toJson(rule);
        stringRedisTemplate.opsForHash().put(GATEWAY_RULE_BLACKLIST, rule.getId(), content);
    }

    /**
     * 删除黑名单规则
     */
    public void delete(String id) {
        stringRedisTemplate.opsForHash().delete(GATEWAY_RULE_BLACKLIST, id);
    }

    /**
     * 将IP加入黑名单（默认封禁1小时）
     */
    public void addToBlacklist(String ip, String path, String method) {
        BlacklistRule record = new BlacklistRule();
        record.setId(IdUtil.fastSimpleUUID());
        record.setDescription("触发限流规则" + path + "拉入黑名单1小时");
        record.setStatus(true);
        final Instant now = Instant.now();
        record.setStartTime(now);
        record.setEndTime(now.plus(1, ChronoUnit.HOURS));
        record.setIp(ip);
        record.setMethod(method);
        record.setPath(path);
        saveOrUpdate(record);
    }
}
