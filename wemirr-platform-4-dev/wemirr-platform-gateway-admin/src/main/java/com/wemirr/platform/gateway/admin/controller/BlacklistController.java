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

package com.wemirr.platform.gateway.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.wemirr.framework.commons.annotation.log.AccessLog;
import com.wemirr.framework.commons.entity.Result;
import com.wemirr.platform.gateway.admin.domain.BlacklistRule;
import com.wemirr.platform.gateway.admin.service.BlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网关黑名单管理控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway/admin/blacklist")
@Tag(name = "网关黑名单管理", description = "网关黑名单规则管理")
public class BlacklistController {

    private final BlacklistService blacklistService;

    @GetMapping
    @AccessLog(module = "网关黑名单", description = "分页查询")
    @SaCheckPermission("gateway:blacklist:list")
    @Operation(summary = "查询黑名单规则")
    public Result<Map<String, Object>> query() {
        List<BlacklistRule> rules = blacklistService.query();
        Map<String, Object> data = new HashMap<>(5);
        data.put("total", rules.size());
        data.put("records", rules);
        data.put("current", 1);
        data.put("size", 20);
        data.put("pages", 1);
        return Result.success(data);
    }

    @PostMapping
    @AccessLog(module = "网关黑名单", description = "新增规则")
    @SaCheckPermission("gateway:blacklist:create")
    @Operation(summary = "新增黑名单规则")
    public Result<Void> save(@Valid @RequestBody BlacklistRule rule) {
        blacklistService.saveOrUpdate(rule);
        return Result.success();
    }

    @PutMapping("/{id}")
    @AccessLog(module = "网关黑名单", description = "修改规则")
    @SaCheckPermission("gateway:blacklist:modify")
    @Operation(summary = "修改黑名单规则")
    public Result<Void> modify(@PathVariable String id, @Valid @RequestBody BlacklistRule rule) {
        rule.setId(id);
        blacklistService.saveOrUpdate(rule);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @AccessLog(module = "网关黑名单", description = "删除规则")
    @SaCheckPermission("gateway:blacklist:remove")
    @Operation(summary = "删除黑名单规则")
    public Result<Void> delete(@PathVariable String id) {
        blacklistService.delete(id);
        return Result.success();
    }
}
