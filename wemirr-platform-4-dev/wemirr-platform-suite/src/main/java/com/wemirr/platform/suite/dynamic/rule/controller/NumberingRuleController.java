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

package com.wemirr.platform.suite.dynamic.rule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.annotation.AccessLog;
import com.wemirr.platform.suite.dynamic.rule.domain.dto.req.RuleSaveReq;
import com.wemirr.platform.suite.dynamic.rule.domain.dto.resp.RuleResp;
import com.wemirr.platform.suite.dynamic.rule.service.NumberingRuleService;
import com.wemirr.platform.suite.dynamic.rule.service.RulePageReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 编号规则控制器
 *
 * @author Levin
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dynamic/rule")
@RequiredArgsConstructor
@Tag(name = "编号规则", description = "编号规则配置管理")
public class NumberingRuleController {

    private final NumberingRuleService numberingRuleService;

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:rule:page')")
    public IPage<RuleResp> pageList(@RequestBody RulePageReq req) {
        return numberingRuleService.pageList(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:rule:detail')")
    public RuleResp detail(@PathVariable Long id) {
        return numberingRuleService.detail(id);
    }

    @GetMapping("/code/{ruleCode}")
    @Operation(summary = "根据编码获取详情")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:rule:detail')")
    public RuleResp detailByCode(@PathVariable String ruleCode) {
        return numberingRuleService.detailByCode(ruleCode);
    }

    @PostMapping
    @Operation(summary = "新增")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:rule:add')")
    public void add(@Validated @RequestBody RuleSaveReq req) {
        numberingRuleService.add(req);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:rule:edit')")
    public void edit(@PathVariable Long id, @Validated @RequestBody RuleSaveReq req) {
        numberingRuleService.edit(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:rule:delete')")
    public void delete(@PathVariable Long id) {
        numberingRuleService.delete(id);
    }

    @PostMapping("/{ruleCode}/generate")
    @Operation(summary = "生成编号")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:rule:generate')")
    public String generateNumber(@PathVariable String ruleCode) {
        return numberingRuleService.generateNumber(ruleCode);
    }

    @PutMapping("/{id}/reset-sequence")
    @Operation(summary = "重置流水号")
    @AccessLog
    @PreAuthorize("hasAuthority('dynamic:rule:reset')")
    public void resetSequence(@PathVariable Long id) {
        numberingRuleService.resetSequence(id);
    }
}
