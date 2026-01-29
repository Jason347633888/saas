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

package com.wemirr.platform.suite.dynamic.rule.domain.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 编号规则保存请求
 *
 * @author Levin
 */
@Data
public class RuleSaveReq {

    @NotBlank(message = "规则编码不能为空")
    @Schema(description = "规则编码")
    private String ruleCode;

    @NotBlank(message = "规则名称不能为空")
    @Schema(description = "规则名称")
    private String ruleName;

    @NotBlank(message = "业务类型不能为空")
    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "前缀")
    private String prefix;

    @Schema(description = "日期格式")
    private String dateFormat;

    @Schema(description = "流水号长度")
    private Integer sequenceLength;

    @Schema(description = "分隔符")
    private String separator;

    @Schema(description = "后缀")
    private String suffix;

    @Schema(description = "步长")
    private Integer step;

    @Schema(description = "重置周期")
    private String resetCycle;

    @Schema(description = "规则描述")
    private String description;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "公司ID")
    private Long companyId;
}
