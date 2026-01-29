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

package com.wemirr.platform.suite.dynamic.data.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.annotation.AccessLog;
import com.wemirr.platform.suite.dynamic.data.domain.dto.req.DataSaveReq;
import com.wemirr.platform.suite.dynamic.data.domain.dto.resp.DataResp;
import com.wemirr.platform.suite.dynamic.data.service.DataPageReq;
import com.wemirr.platform.suite.dynamic.data.service.DynamicDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 动态数据控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dynamic/data")
@RequiredArgsConstructor
@Tag(name = "数据填报", description = "动态表单数据填报管理")
public class DynamicDataController {

    private final DynamicDataService dynamicDataService;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:page')")
    public IPage<DataResp> pageList(@RequestBody DataPageReq req) {
        return dynamicDataService.pageList(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:detail')")
    public DataResp detail(@PathVariable Long id) {
        return dynamicDataService.detail(id);
    }

    @PostMapping
    @Operation(summary = "新增")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:add')")
    public Long add(@Validated @RequestBody DataSaveReq req) {
        return dynamicDataService.add(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:edit')")
    public void edit(@PathVariable Long id, @Validated @RequestBody DataSaveReq req) {
        dynamicDataService.edit(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:delete')")
    public void delete(@PathVariable Long id) {
        dynamicDataService.delete(id);
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "提交数据")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:submit')")
    public void submit(@PathVariable Long id) {
        dynamicDataService.submit(id);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "审批数据")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:approve')")
    public void approve(@PathVariable Long id,
                        @RequestParam boolean approved,
                        @RequestParam(required = false) String comment) {
        dynamicDataService.approve(id, approved, comment);
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "撤回数据")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:withdraw')")
    public void withdraw(@PathVariable Long id) {
        dynamicDataService.withdraw(id);
    }

    @GetMapping("/{id}/content")
    @Operation(summary = "获取数据内容")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:data:content')")
    public Map<String, Object> getDataContent(@PathVariable Long id) {
        return dynamicDataService.getDataContent(id);
    }
}
