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

package com.wemirr.platform.suite.dynamic.company.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wemirr.framework.db.mybatisplus.ext.SuperService;
import com.wemirr.platform.suite.dynamic.company.domain.dto.req.CompanySaveReq;
import com.wemirr.platform.suite.dynamic.company.domain.dto.resp.CompanyResp;
import com.wemirr.platform.suite.dynamic.company.domain.entity.CompanyConfig;

/**
 * 公司配置服务接口
 *
 * @author Levin
 */
public interface CompanyConfigService extends SuperService<CompanyConfig> {

    /**
     * 分页查询公司配置
     *
     * @param pageReq 分页参数
     * @return 分页结果
     */
    IPage<CompanyResp> pageList(CompanyConfigPageReq pageReq);

    /**
     * 获取公司配置详情
     *
     * @param id ID
     * @return 公司配置详情
     */
    CompanyResp detail(Long id);

    /**
     * 根据公司编码获取公司配置
     *
     * @param companyCode 公司编码
     * @return 公司配置详情
     */
    CompanyResp detailByCode(String companyCode);

    /**
     * 创建公司配置
     *
     * @param req 保存请求
     */
    void add(CompanySaveReq req);

    /**
     * 修改公司配置
     *
     * @param id  ID
     * @param req 保存请求
     */
    void edit(Long id, CompanySaveReq req);

    /**
     * 删除公司配置
     *
     * @param id ID
     */
    void delete(Long id);

    /**
     * 设置默认公司
     *
     * @param id ID
     */
    void setDefault(Long id);
}
