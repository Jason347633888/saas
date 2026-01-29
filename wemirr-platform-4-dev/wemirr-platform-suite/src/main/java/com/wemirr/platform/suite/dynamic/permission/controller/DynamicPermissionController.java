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

package com.wemirr.platform.suite.dynamic.permission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.annotation.AccessLog;
import com.wemirr.platform.suite.dynamic.permission.domain.dto.req.PermissionSaveReq;
import com.wemirr.platform.suite.dynamic.permission.domain.dto.resp.PermissionResp;
import com.wemirr.platform.suite.dynamic.permission.service.DynamicPermissionService;
import com.wemirr.platform.suite.dynamic.permission.service.PermissionPageReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据权限控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dynamic/permission")
@RequiredArgsConstructor
@Tag(name = "数据权限", description = "动态表单数据权限配置管理")
public class DynamicPermissionController {

    private final DynamicPermissionService dynamicPermissionService;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:permission:page')")
    public IPage<PermissionResp> pageList(@RequestBody PermissionPageReq req) {
        return dynamicPermissionService.pageList(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:permission:detail')")
    public PermissionResp detail(@PathVariable Long id) {
        return dynamicPermissionService.detail(id);
    }

    @PostMapping
    @Operation(summary = "新增")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:permission:add')")
    public void add(@Validated @RequestBody PermissionSaveReq req) {
        dynamicPermissionService.add(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:permission:edit')")
    public void edit(@PathVariable Long id, @Validated @RequestBody PermissionSaveReq req) {
        dynamicPermissionService.edit(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:permission:delete')")
    public void delete(@PathVariable Long id) {
        dynamicPermissionService.delete(id);
    }

    @GetMapping("/template/{templateId}")
    @Operation(summary = "根据模板获取权限列表")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:permission:list')")
    public List<PermissionResp> getPermissionsByTemplateId(@PathVariable Long templateId) {
        return dynamicPermissionService.getPermissionsByTemplateId(templateId);
    }

    @GetMapping("/check")
    @Operation(summary = "检查权限")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:permission:check')")
    public boolean checkPermission(@RequestParam Long templateId,
                                   @RequestParam(required = false) Long dataId) {
        // userId 从当前登录用户上下文中获取，防止越权
        return dynamicPermissionService.checkPermission(null, templateId, dataId);
    }

    @GetMapping("/accessible")
    @Operation(summary = "获取可访问数据范围")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:permission:accessible')")
    public List<Long> getAccessibleDataIds(@RequestParam Long templateId) {
        // userId 从当前登录用户上下文中获取，防止越权
        return dynamicPermissionService.getAccessibleDataIds(null, templateId);
    }
}
