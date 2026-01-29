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

package com.wemirr.platform.suite.dynamic.rule.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

/**
 * 编号规则响应
 *
 * @author Levin
 */
@Data
public class RuleResp {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "前缀")
    private String prefix;

    @Schema(description = "日期格式")
    private String dateFormat;

    @Schema(description = "流水号长度")
    private Integer sequenceLength;

    @Schema(description = "当前流水号")
    private Long currentSequence;

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

    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "创建人")
    private String createName;

    @Schema(description = "创建时间")
    private Instant createTime;
}
