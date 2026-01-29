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

package com.wemirr.platform.suite.dynamic.template.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

/**
 * 版本历史响应
 *
 * @author Levin
 */
@Data
public class VersionHistoryResp {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "变更内容(JSON)")
    private String changeContent;

    @Schema(description = "变更原因")
    private String changeReason;

    @Schema(description = "变更人")
    private String changeBy;

    @Schema(description = "创建时间")
    private Instant createTime;
}
