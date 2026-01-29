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

package com.wemirr.platform.suite.online.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.commons.BeanUtilPlus;
import com.wemirr.framework.commons.exception.CheckedException;
import com.wemirr.framework.db.mybatisplus.ext.SuperServiceImpl;
import com.wemirr.framework.security.context.AuthenticationContext;
import com.wemirr.framework.security.context.AuthenticationContextHolder;
import com.wemirr.platform.suite.online.domain.entity.OnlineFormData;
import com.wemirr.platform.suite.online.domain.req.OnlineFormDataSaveReq;
import com.wemirr.platform.suite.online.domain.req.OnlineFormDesignerPageReq;
import com.wemirr.platform.suite.online.repository.OnlineFormDataMapper;
import com.wemirr.platform.suite.online.service.OnlineFormDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Levin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineFormDataServiceImpl extends SuperServiceImpl<OnlineFormDataMapper, OnlineFormData> implements OnlineFormDataService {

    /**
     * 获取当前用户的租户ID
     */
    private Long getCurrentTenantId() {
        AuthenticationContext context = AuthenticationContextHolder.getContext();
        if (context != null && context.tenantId() != null) {
            return context.tenantId();
        }
        throw CheckedException.unauthorized("用户未登录或租户信息缺失");
    }

    @Override
    public IPage<Map<String, Object>> pageList(OnlineFormDesignerPageReq req) {
        // 添加租户隔离
        Long tenantId = getCurrentTenantId();
        return this.baseMapper.pageList(req.buildPage(), req, tenantId).convert(x -> new HashMap<>() {

            {
                put("id", x.getId());
                put("definitionKey", x.getDefinitionKey());
                put("tenantId", x.getTenantId());
                put("createName", x.getCreateName());
                put("createTime", x.getCreateTime());
                putAll(x.getFormData());
            }
        });
    }

    @Override
    public void create(OnlineFormDataSaveReq req) {
        Long tenantId = getCurrentTenantId();
        var bean = BeanUtilPlus.toBean(req, OnlineFormData.class);
        bean.setTenantId(tenantId);
        this.baseMapper.insert(bean);
    }

    @Override
    public void modify(Long id, OnlineFormDataSaveReq req) {
        // 验证数据属于当前租户
        getDataByIdWithTenant(id);
        var bean = BeanUtilPlus.toBean(id, req, OnlineFormData.class);
        this.baseMapper.updateById(bean);
    }

    /**
     * 根据ID获取数据（带租户隔离）
     */
    private OnlineFormData getDataByIdWithTenant(Long id) {
        Long tenantId = getCurrentTenantId();
        OnlineFormData data = this.baseMapper.selectById(id);
        if (data == null || !tenantId.equals(data.getTenantId())) {
            throw CheckedException.notFound("数据不存在或无权限访问");
        }
        return data;
    }
}
