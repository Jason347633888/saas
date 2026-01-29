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

package com.wemirr.platform.suite.dynamic.company.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.commons.BeanUtilPlus;
import com.wemirr.framework.commons.exception.CheckedException;
import com.wemirr.framework.db.mybatisplus.ext.SuperServiceImpl;
import com.wemirr.framework.db.mybatisplus.wrap.Wraps;
import com.wemirr.platform.suite.dynamic.company.domain.dto.req.CompanySaveReq;
import com.wemirr.platform.suite.dynamic.company.domain.dto.resp.CompanyResp;
import com.wemirr.platform.suite.dynamic.company.domain.entity.CompanyConfig;
import com.wemirr.platform.suite.dynamic.company.repository.CompanyConfigMapper;
import com.wemirr.platform.suite.dynamic.company.service.CompanyConfigService;
import com.wemirr.platform.suite.dynamic.company.service.CompanyConfigPageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 公司配置服务实现
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyConfigServiceImpl extends SuperServiceImpl<CompanyConfigMapper, CompanyConfig> implements CompanyConfigService {

    @Override
    public IPage<CompanyResp> pageList(CompanyConfigPageReq pageReq) {
        return this.baseMapper.selectPage(pageReq.buildPage(), Wraps.<CompanyConfig>lbQ()
                        .like(CompanyConfig::getCompanyName, pageReq.getCompanyName())
                        .eq(CompanyConfig::getCompanyCode, pageReq.getCompanyCode())
                        .eq(CompanyConfig::getStatus, pageReq.getStatus()))
                .convert(x -> BeanUtilPlus.toBean(x, CompanyResp.class));
    }

    @Override
    public CompanyResp detail(Long id) {
        CompanyConfig entity = Optional.ofNullable(this.baseMapper.selectById(id))
                .orElseThrow(() -> CheckedException.notFound("公司配置不存在"));
        return BeanUtilPlus.toBean(entity, CompanyResp.class);
    }

    @Override
    public CompanyResp detailByCode(String companyCode) {
        CompanyConfig entity = Optional.ofNullable(this.baseMapper.selectOne(Wraps.<CompanyConfig>lbQ()
                        .eq(CompanyConfig::getCompanyCode, companyCode)))
                .orElseThrow(() -> CheckedException.notFound("公司配置不存在"));
        return BeanUtilPlus.toBean(entity, CompanyResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(CompanySaveReq req) {
        // 检查编码是否已存在
        checkCodeExists(req.getCompanyCode(), null);

        CompanyConfig entity = BeanUtilPlus.toBean(req, CompanyConfig.class);
        this.baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, CompanySaveReq req) {
        // 检查是否存在
        getById(id);

        // 检查编码是否被其他记录使用
        checkCodeExists(req.getCompanyCode(), id);

        CompanyConfig entity = BeanUtilPlus.toBean(id, req, CompanyConfig.class);
        this.baseMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id) {
        // 先取消所有默认
        this.baseMapper.update(null, Wraps.<CompanyConfig>lbQ()
                .eq(CompanyConfig::getIsDefault, true));

        // 设置当前为默认
        this.baseMapper.updateById(CompanyConfig.builder()
                .id(id)
                .isDefault(true)
                .build());
    }

    /**
     * 检查编码是否存在
     */
    private void checkCodeExists(String companyCode, Long excludeId) {
        CompanyConfig existing = this.baseMapper.selectOne(Wraps.<CompanyConfig>lbQ()
                .eq(CompanyConfig::getCompanyCode, companyCode));
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw CheckedException.badRequest("公司编码已存在");
        }
    }
}
