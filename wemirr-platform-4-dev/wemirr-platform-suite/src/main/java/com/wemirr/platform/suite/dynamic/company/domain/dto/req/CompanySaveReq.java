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

package com.wemirr.platform.suite.dynamic.company.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公司配置保存请求
 *
 * @author Levin
 */
@Data
public class CompanySaveReq {

    @NotBlank(message = "公司名称不能为空")
    @Schema(description = "公司名称")
    private String companyName;

    @NotBlank(message = "公司编码不能为空")
    @Schema(description = "公司编码")
    private String companyCode;

    @Schema(description = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "联系地址")
    private String address;

    @Schema(description = "LOGO图片地址")
    private String logoUrl;

    @Schema(description = "公司简介")
    private String description;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "是否为默认公司")
    private Boolean isDefault;

    @Schema(description = "扩展配置(JSON)")
    private String extConfig;
}
