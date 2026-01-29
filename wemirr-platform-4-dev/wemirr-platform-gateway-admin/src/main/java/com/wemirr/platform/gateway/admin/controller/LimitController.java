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
import com.wemirr.platform.gateway.admin.domain.LimitRule;
import com.wemirr.platform.gateway.admin.service.LimitService;
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
 * 网关限流规则管理控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway/admin/limits")
@Tag(name = "网关限流管理", description = "网关限流规则管理")
public class LimitController {

    private final LimitService limitService;

    @GetMapping
    @AccessLog(module = "网关限流", description = "分页查询")
    @SaCheckPermission("gateway:limit:list")
    @Operation(summary = "查询限流规则")
    public Result<Map<String, Object>> query() {
        List<LimitRule> rules = limitService.query();
        Map<String, Object> data = new HashMap<>(5);
        data.put("total", rules.size());
        data.put("records", rules);
        data.put("current", 1);
        data.put("size", 20);
        data.put("pages", 1);
        return Result.success(data);
    }

    @PostMapping
    @AccessLog(module = "网关限流", description = "新增规则")
    @SaCheckPermission("gateway:limit:create")
    @Operation(summary = "新增限流规则")
    public Result<Void> add(@Valid @RequestBody LimitRule rule) {
        limitService.saveOrUpdate(rule);
        return Result.success();
    }

    @PutMapping("/{id}")
    @AccessLog(module = "网关限流", description = "修改规则")
    @SaCheckPermission("gateway:limit:modify")
    @Operation(summary = "修改限流规则")
    public Result<Void> modify(@PathVariable String id, @Valid @RequestBody LimitRule rule) {
        rule.setId(id);
        limitService.saveOrUpdate(rule);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @AccessLog(module = "网关限流", description = "删除规则")
    @SaCheckPermission("gateway:limit:remove")
    @Operation(summary = "删除限流规则")
    public Result<Void> delete(@PathVariable String id) {
        limitService.delete(id);
        return Result.success();
    }
}
