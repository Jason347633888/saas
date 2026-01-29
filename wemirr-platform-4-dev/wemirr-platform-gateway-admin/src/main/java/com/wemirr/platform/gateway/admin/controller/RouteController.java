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
import com.wemirr.platform.gateway.admin.domain.RouteRule;
import com.wemirr.platform.gateway.admin.service.RouteService;
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
 * 网关路由规则管理控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway/admin/routes")
@Tag(name = "网关路由管理", description = "网关路由规则管理")
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @AccessLog(module = "网关路由", description = "分页查询")
    @SaCheckPermission("gateway:route:list")
    @Operation(summary = "查询路由规则")
    public Result<Map<String, Object>> query() {
        List<RouteRule> limitRules = routeService.query();
        Map<String, Object> data = new HashMap<>(5);
        data.put("total", limitRules.size());
        data.put("records", limitRules);
        data.put("current", 1);
        data.put("size", 20);
        data.put("pages", 1);
        return Result.success(data);
    }

    @PostMapping
    @AccessLog(module = "网关路由", description = "新增路由")
    @SaCheckPermission("gateway:route:create")
    @Operation(summary = "新增路由规则")
    public Result<Void> add(@Valid @RequestBody RouteRule rule) {
        routeService.saveOrUpdate(rule);
        return Result.success();
    }

    @PutMapping("/{id}")
    @AccessLog(module = "网关路由", description = "修改路由")
    @SaCheckPermission("gateway:route:modify")
    @Operation(summary = "修改路由规则")
    public Result<Void> modify(@PathVariable String id, @Valid @RequestBody RouteRule rule) {
        rule.setId(id);
        routeService.saveOrUpdate(rule);
        return Result.success();
    }

    @PostMapping("/{id}/publish")
    @AccessLog(module = "网关路由", description = "发布路由")
    @SaCheckPermission("gateway:route:publish")
    @Operation(summary = "发布路由规则")
    public Result<Boolean> publish(@PathVariable String id) {
        boolean success = routeService.publish(id);
        if (!success) {
            return Result.fail("发布失败，服务未注册");
        }
        return Result.success(true);
    }

    @DeleteMapping("/{id}")
    @AccessLog(module = "网关路由", description = "删除路由")
    @SaCheckPermission("gateway:route:remove")
    @Operation(summary = "删除路由规则")
    public Result<Void> delete(@PathVariable String id) {
        routeService.delete(id);
        return Result.success();
    }
}
