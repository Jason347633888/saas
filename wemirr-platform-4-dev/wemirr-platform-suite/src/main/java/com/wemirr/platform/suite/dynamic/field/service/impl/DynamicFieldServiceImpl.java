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

package com.wemirr.platform.suite.dynamic.field.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.commons.BeanUtilPlus;
import com.wemirr.framework.commons.exception.CheckedException;
import com.wemirr.framework.db.mybatisplus.ext.SuperServiceImpl;
import com.wemirr.framework.db.mybatisplus.wrap.Wraps;
import com.wemirr.platform.suite.dynamic.field.domain.dto.req.FieldSaveReq;
import com.wemirr.platform.suite.dynamic.field.domain.dto.resp.FieldResp;
import com.wemirr.platform.suite.dynamic.field.domain.entity.DynamicField;
import com.wemirr.platform.suite.dynamic.field.repository.DynamicFieldMapper;
import com.wemirr.platform.suite.dynamic.field.service.DynamicFieldService;
import com.wemirr.platform.suite.dynamic.field.service.FieldPageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 动态字段服务实现
 *
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicFieldServiceImpl extends SuperServiceImpl<DynamicFieldMapper, DynamicField> implements DynamicFieldService {

    @Override
    public IPage<FieldResp> pageList(FieldPageReq pageReq) {
        return this.baseMapper.selectPage(pageReq.buildPage(), Wraps.<DynamicField>lbQ()
                        .like(DynamicField::getFieldName, pageReq.getFieldName())
                        .like(DynamicField::getFieldLabel, pageReq.getFieldLabel())
                        .eq(DynamicField::getFieldType, pageReq.getFieldType())
                        .eq(DynamicField::getTemplateId, pageReq.getTemplateId())
                        .eq(DynamicField::getPluginType, pageReq.getPluginType())
                        .eq(DynamicField::getStatus, pageReq.getStatus()))
                .convert(x -> BeanUtilPlus.toBean(x, FieldResp.class));
    }

    @Override
    public FieldResp detail(Long id) {
        DynamicField entity = Optional.ofNullable(this.baseMapper.selectById(id))
                .orElseThrow(() -> CheckedException.notFound("字段不存在"));
        return BeanUtilPlus.toBean(entity, FieldResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(FieldSaveReq req) {
        // 检查字段名称是否在模板内重复
        checkFieldNameExists(req.getFieldName(), req.getTemplateId(), null);

        DynamicField entity = BeanUtilPlus.toBean(req, DynamicField.class);
        this.baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, FieldSaveReq req) {
        // 检查是否存在
        getById(id);

        // 检查字段名称是否被其他字段使用
        checkFieldNameExists(req.getFieldName(), req.getTemplateId(), id);

        DynamicField entity = BeanUtilPlus.toBean(id, req, DynamicField.class);
        this.baseMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.baseMapper.deleteById(id);
    }

    @Override
    public List<FieldResp> getFieldsByTemplateId(Long templateId) {
        List<DynamicField> fields = this.baseMapper.selectList(Wraps.<DynamicField>lbQ()
                .eq(DynamicField::getTemplateId, templateId)
                .eq(DynamicField::getStatus, true)
                .orderByAsc(DynamicField::getFieldOrder));
        return BeanUtilPlus.toBean(fields, FieldResp.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(Long templateId, List<FieldSaveReq> fields) {
        // 删除该模板下所有现有字段
        this.baseMapper.delete(Wraps.<DynamicField>lbQ()
                .eq(DynamicField::getTemplateId, templateId));

        // 批量插入
        int order = 0;
        for (FieldSaveReq field : fields) {
            DynamicField entity = BeanUtilPlus.toBean(field, DynamicField.class);
            entity.setTemplateId(templateId);
            entity.setFieldOrder(order++);
            this.baseMapper.insert(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(Long id, Integer fieldOrder) {
        this.baseMapper.updateById(DynamicField.builder()
                .id(id)
                .fieldOrder(fieldOrder)
                .build());
    }

    /**
     * 检查字段名称是否在模板内重复
     */
    private void checkFieldNameExists(String fieldName, Long templateId, Long excludeId) {
        DynamicField existing = this.baseMapper.selectOne(Wraps.<DynamicField>lbQ()
                .eq(DynamicField::getFieldName, fieldName)
                .eq(DynamicField::getTemplateId, templateId));
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw CheckedException.badRequest("字段名称在模板内已存在");
        }
    }
}
