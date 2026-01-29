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

package com.wemirr.platform.suite.dynamic.rule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.mybatisplus.ext.SuperService;
import com.wemirr.platform.suite.dynamic.rule.domain.dto.req.RuleSaveReq;
import com.wemirr.platform.suite.dynamic.rule.domain.dto.resp.RuleResp;
import com.wemirr.platform.suite.dynamic.rule.domain.entity.NumberingRule;

/**
 * 编号规则服务接口
 *
 * @author Levin
 */
public interface NumberingRuleService extends SuperService<NumberingRule> {

    /**
     * 分页查询编号规则
     *
     * @param pageReq 分页参数
     * @return 分页结果
     */
    IPage<RuleResp> pageList(RulePageReq pageReq);

    /**
     * 获取编号规则详情
     *
     * @param id ID
     * @return 编号规则详情
     */
    RuleResp detail(Long id);

    /**
     * 根据规则编码获取编号规则
     *
     * @param ruleCode 规则编码
     * @return 编号规则详情
     */
    RuleResp detailByCode(String ruleCode);

    /**
     * 创建编号规则
     *
     * @param req 保存请求
     */
    void add(RuleSaveReq req);

    /**
     * 修改编号规则
     *
     * @param id  ID
     * @param req 保存请求
     */
    void edit(Long id, RuleSaveReq req);

    /**
     * 删除编号规则
     *
     * @param id ID
     */
    void delete(Long id);

    /**
     * 生成编号
     *
     * @param ruleCode 规则编码
     * @return 生成的编号
     */
    String generateNumber(String ruleCode);

    /**
     * 重置流水号
     *
     * @param id ID
     */
    void resetSequence(Long id);
}
