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

package com.wemirr.platform.suite.dynamic.field.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.annotation.AccessLog;
import com.wemirr.platform.suite.dynamic.field.domain.dto.req.FieldSaveReq;
import com.wemirr.platform.suite.dynamic.field.domain.dto.resp.FieldResp;
import com.wemirr.platform.suite.dynamic.field.service.DynamicFieldService;
import com.wemirr.platform.suite.dynamic.field.service.FieldPageReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 动态字段控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dynamic/field")
@RequiredArgsConstructor
@Tag(name = "字段配置", description = "动态表单字段配置管理")
public class DynamicFieldController {

    private final DynamicFieldService dynamicFieldService;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:field:page')")
    public IPage<FieldResp> pageList(@RequestBody FieldPageReq req) {
        return dynamicFieldService.pageList(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:field:detail')")
    public FieldResp detail(@PathVariable Long id) {
        return dynamicFieldService.detail(id);
    }

    @PostMapping
    @Operation(summary = "新增")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:field:add')")
    public void add(@Validated @RequestBody FieldSaveReq req) {
        dynamicFieldService.add(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:field:edit')")
    public void edit(@PathVariable Long id, @Validated @RequestBody FieldSaveReq req) {
        dynamicFieldService.edit(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:field:delete')")
    public void delete(@PathVariable Long id) {
        dynamicFieldService.delete(id);
    }

    @GetMapping("/template/{templateId}")
    @Operation(summary = "根据模板获取字段列表")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:field:list')")
    public List<FieldResp> getFieldsByTemplateId(@PathVariable Long templateId) {
        return dynamicFieldService.getFieldsByTemplateId(templateId);
    }

    @PostMapping("/batch")
    @Operation(summary = "批量保存字段")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:field:batch')")
    public void batchSave(@RequestParam Long templateId, @RequestBody List<FieldSaveReq> fields) {
        dynamicFieldService.batchSave(templateId, fields);
    }

    @PutMapping("/{id}/order")
    @Operation(summary = "更新字段顺序")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:field:order')")
    public void updateOrder(@PathVariable Long id, @RequestParam Integer fieldOrder) {
        dynamicFieldService.updateOrder(id, fieldOrder);
    }
}
