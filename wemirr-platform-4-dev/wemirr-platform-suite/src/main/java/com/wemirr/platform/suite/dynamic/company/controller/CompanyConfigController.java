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

package com.wemirr.platform.suite.dynamic.company.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.annotation.AccessLog;
import com.wemirr.platform.suite.dynamic.company.domain.dto.req.CompanySaveReq;
import com.wemirr.platform.suite.dynamic.company.domain.dto.resp.CompanyResp;
import com.wemirr.platform.suite.dynamic.company.service.CompanyConfigPageReq;
import com.wemirr.platform.suite.dynamic.company.service.CompanyConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公司配置控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dynamic/company")
@RequiredArgsConstructor
@Tag(name = "公司配置", description = "公司配置管理")
public class CompanyConfigController {

    private final CompanyConfigService companyConfigService;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:company:page')")
    public IPage<CompanyResp> pageList(@RequestBody CompanyConfigPageReq req) {
        return companyConfigService.pageList(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:company:detail')")
    public CompanyResp detail(@PathVariable Long id) {
        return companyConfigService.detail(id);
    }

    @GetMapping("/code/{companyCode}")
    @Operation(summary = "根据编码获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:company:detail')")
    public CompanyResp detailByCode(@PathVariable String companyCode) {
        return companyConfigService.detailByCode(companyCode);
    }

    @PostMapping
    @Operation(summary = "新增")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:company:add')")
    public void add(@Validated @RequestBody CompanySaveReq req) {
        companyConfigService.add(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:company:edit')")
    public void edit(@PathVariable Long id, @Validated @RequestBody CompanySaveReq req) {
        companyConfigService.edit(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:company:delete')")
    public void delete(@PathVariable Long id) {
        companyConfigService.delete(id);
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "设为默认")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:company:default')")
    public void setDefault(@PathVariable Long id) {
        companyConfigService.setDefault(id);
    }
}
