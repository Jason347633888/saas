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

package com.wemirr.platform.suite.dynamic.rule.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.commons.BeanUtilPlus;
import com.wemirr.framework.commons.exception.CheckedException;
import com.wemirr.framework.db.mybatisplus.ext.SuperServiceImpl;
import com.wemirr.framework.db.mybatisplus.wrap.Wraps;
import com.wemirr.platform.suite.dynamic.rule.domain.dto.req.RuleSaveReq;
import com.wemirr.platform.suite.dynamic.rule.domain.dto.resp.RuleResp;
import com.wemirr.platform.suite.dynamic.rule.domain.entity.NumberingRule;
import com.wemirr.platform.suite.dynamic.rule.repository.NumberingRuleMapper;
import com.wemirr.platform.suite.dynamic.rule.service.NumberingRuleService;
import com.wemirr.platform.suite.dynamic.rule.service.RulePageReq;
import com.wemirr.platform.suite.dynamic.rule.util.NumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 编号规则服务实现
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NumberingRuleServiceImpl extends SuperServiceImpl<NumberingRuleMapper, NumberingRule> implements NumberingRuleService {

    private final NumberGenerator numberGenerator;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SEQUENCE_KEY_PREFIX = "dynamic:numbering:sequence:";

    @Override
    public IPage<RuleResp> pageList(RulePageReq pageReq) {
        return this.baseMapper.selectPage(pageReq.buildPage(), Wraps.<NumberingRule>lbQ()
                        .like(NumberingRule::getRuleCode, pageReq.getRuleCode())
                        .like(NumberingRule::getRuleName, pageReq.getRuleName())
                        .eq(NumberingRule::getBusinessType, pageReq.getBusinessType())
                        .eq(NumberingRule::getStatus, pageReq.getStatus()))
                .convert(x -> BeanUtilPlus.toBean(x, RuleResp.class));
    }

    @Override
    public RuleResp detail(Long id) {
        NumberingRule entity = Optional.ofNullable(this.baseMapper.selectById(id))
                .orElseThrow(() -> CheckedException.notFound("编号规则不存在"));
        return BeanUtilPlus.toBean(entity, RuleResp.class);
    }

    @Override
    public RuleResp detailByCode(String ruleCode) {
        NumberingRule entity = Optional.ofNullable(this.baseMapper.selectOne(Wraps.<NumberingRule>lbQ()
                        .eq(NumberingRule::getRuleCode, ruleCode)))
                .orElseThrow(() -> CheckedException.notFound("编号规则不存在"));
        return BeanUtilPlus.toBean(entity, RuleResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(RuleSaveReq req) {
        // 检查编码是否已存在
        checkCodeExists(req.getRuleCode(), null);

        NumberingRule entity = BeanUtilPlus.toBean(req, NumberingRule.class);
        // 初始化当前流水号
        entity.setCurrentSequence(0L);
        // 默认步长为1
        if (entity.getStep() == null) {
            entity.setStep(1);
        }
        // 默认流水号长度为4
        if (entity.getSequenceLength() == null) {
            entity.setSequenceLength(4);
        }
        this.baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, RuleSaveReq req) {
        // 检查是否存在
        getById(id);

        // 检查编码是否被其他记录使用
        checkCodeExists(req.getRuleCode(), id);

        NumberingRule entity = BeanUtilPlus.toBean(id, req, NumberingRule.class);
        this.baseMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.baseMapper.deleteById(id);
        // 删除Redis中的流水号
        String redisKey = SEQUENCE_KEY_PREFIX + id;
        redisTemplate.delete(redisKey);
    }

    @Override
    public String generateNumber(String ruleCode) {
        NumberingRule rule = Optional.ofNullable(this.baseMapper.selectOne(Wraps.<NumberingRule>lbQ()
                        .eq(NumberingRule::getRuleCode, ruleCode)
                        .eq(NumberingRule::getStatus, true)))
                .orElseThrow(() -> CheckedException.notFound("编号规则不存在或已禁用"));

        // 使用 Redis INCR 命令原子性递增，确保并发安全
        String redisKey = SEQUENCE_KEY_PREFIX + rule.getId();
        int step = rule.getStep() != null ? rule.getStep() : 1;
        int seqLength = rule.getSequenceLength() != null ? rule.getSequenceLength() : 4;

        // 原子性递增
        Long newSeq = redisTemplate.opsForValue().increment(redisKey, step);
        if (newSeq == null) {
            // 初始化 Redis 值
            redisTemplate.opsForValue().set(redisKey, (long) step);
            newSeq = (long) step;
        }

        // 检查序号是否超出范围
        long maxSeq = (long) Math.pow(10, seqLength) - 1;
        if (newSeq > maxSeq) {
            throw CheckedException.badRequest("流水号已超出最大序号限制，请重置编号规则");
        }

        // 生成编号
        AtomicLong atomicSeq = new AtomicLong(newSeq);
        String number = numberGenerator.generateBusinessNumber(
                rule.getRuleCode(),
                rule.getPrefix(),
                rule.getDateFormat(),
                rule.getSeparator(),
                seqLength,
                atomicSeq,
                step,
                rule.getSuffix()
        );

        // 异步更新数据库（保证 Redis 和数据库最终一致）
        // 注意：这里使用异步方式更新数据库，提高性能
        this.baseMapper.updateById(NumberingRule.builder()
                .id(rule.getId())
                .currentSequence(newSeq)
                .build());

        return number;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetSequence(Long id) {
        NumberingRule rule = getById(id);
        this.baseMapper.updateById(NumberingRule.builder()
                .id(id)
                .currentSequence(0L)
                .build());
        // 重置Redis中的流水号
        String redisKey = SEQUENCE_KEY_PREFIX + id;
        redisTemplate.delete(redisKey);
    }

    /**
     * 检查编码是否存在
     */
    private void checkCodeExists(String ruleCode, Long excludeId) {
        NumberingRule existing = this.baseMapper.selectOne(Wraps.<NumberingRule>lbQ()
                .eq(NumberingRule::getRuleCode, ruleCode));
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw CheckedException.badRequest("规则编码已存在");
        }
    }
}
