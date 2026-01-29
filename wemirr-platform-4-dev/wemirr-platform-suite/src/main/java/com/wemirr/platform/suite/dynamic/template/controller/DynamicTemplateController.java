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

package com.wemirr.platform.suite.dynamic.template.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.annotation.AccessLog;
import com.wemirr.platform.suite.dynamic.template.domain.dto.req.TemplatePageReq;
import com.wemirr.platform.suite.dynamic.template.domain.dto.req.TemplateSaveReq;
import com.wemirr.platform.suite.dynamic.template.domain.dto.resp.TemplateResp;
import com.wemirr.platform.suite.dynamic.template.domain.dto.resp.VersionHistoryResp;
import com.wemirr.platform.suite.dynamic.template.service.DynamicTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 动态模板控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dynamic/template")
@RequiredArgsConstructor
@Tag(name = "模板管理", description = "动态模板管理")
public class DynamicTemplateController {

    private final DynamicTemplateService dynamicTemplateService;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:page')")
    public IPage<TemplateResp> pageList(@RequestBody TemplatePageReq req) {
        return dynamicTemplateService.pageList(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:detail')")
    public TemplateResp detail(@PathVariable Long id) {
        return dynamicTemplateService.detail(id);
    }

    @GetMapping("/code/{templateCode}")
    @Operation(summary = "根据编码获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:detail')")
    public TemplateResp detailByCode(@PathVariable String templateCode) {
        return dynamicTemplateService.detailByCode(templateCode);
    }

    @PostMapping
    @Operation(summary = "新增")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:add')")
    public void add(@Validated @RequestBody TemplateSaveReq req) {
        dynamicTemplateService.add(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:edit')")
    public void edit(@PathVariable Long id, @Validated @RequestBody TemplateSaveReq req) {
        dynamicTemplateService.edit(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:delete')")
    public void delete(@PathVariable Long id) {
        dynamicTemplateService.delete(id);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布模板")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:publish')")
    public void publish(@PathVariable Long id, @RequestParam(required = false) String changeReason) {
        dynamicTemplateService.publish(id, changeReason);
    }

    @PostMapping("/{id}/offline")
    @Operation(summary = "下线模板")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:offline')")
    public void offline(@PathVariable Long id) {
        dynamicTemplateService.offline(id);
    }

    @PostMapping("/{id}/rollback")
    @Operation(summary = "回滚版本")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:rollback')")
    public void rollback(@PathVariable Long id,
                         @RequestParam Integer version,
                         @RequestParam(required = false) String reason) {
        dynamicTemplateService.rollback(id, version, reason);
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "获取版本历史")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:template:versions')")
    public List<VersionHistoryResp> getVersionHistory(@PathVariable Long id) {
        return dynamicTemplateService.getVersionHistory(id);
    }
}
