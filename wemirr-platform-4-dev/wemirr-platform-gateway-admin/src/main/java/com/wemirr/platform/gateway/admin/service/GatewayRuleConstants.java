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

/**
 * Gateway 规则常量定义
 *
 * @author Levin
 */
public interface GatewayRuleConstants {

    /**
     * 路由规则 Redis Key
     */
    String GATEWAY_RULE_ROUTE = "gateway:rule:route";

    /**
     * 限流规则 Redis Key
     */
    String GATEWAY_RULE_LIMIT = "gateway:rule:limit";

    /**
     * 限流规则访问次数 Redis Key
     */
    String GATEWAY_RULE_LIMIT_VISITS = "gateway:rule:limit:visits";

    /**
     * 黑名单规则 Redis Key
     */
    String GATEWAY_RULE_BLACKLIST = "gateway:rule:blacklist";

    /**
     * 黑名单访问次数 Redis Key
     */
    String GATEWAY_BLACKLIST_VISITS = "gateway:blacklist:visits";

    /**
     * 默认限流总量 Key
     */
    String DEFAULT_RULE_LIMIT_TOTAL = "gateway:rule:limit:total";

    /**
     * 全局限流范围
     */
    int GLOBAL_RANGE = 0;

    /**
     * IP 限流范围
     */
    int IP_RANGE = 1;
}
