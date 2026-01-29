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

package com.wemirr.platform.suite.dynamic.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wemirr.framework.commons.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 动态数据实体
 *
 * @author Levin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_dynamic_data")
public class DynamicData extends SuperEntity<Long> {

    @Schema(description = "关联模板ID")
    private Long templateId;

    @Schema(description = "业务编号")
    private String businessNo;

    @Schema(description = "数据标题")
    private String dataTitle;

    @Schema(description = "数据状态")
    private String dataStatus;

    @Schema(description = "数据JSON")
    private String dataContent;

    @Schema(description = "附件JSON")
    private String attachments;

    @Schema(description = "提交人ID")
    private Long submitterId;

    @Schema(description = "提交人名称")
    private String submitterName;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "流程实例ID")
    private String processInstanceId;

    @Schema(description = "流程状态")
    private String processStatus;

    @Schema(description = "扩展字段1")
    private String ext1;

    @Schema(description = "扩展字段2")
    private String ext2;

    @Schema(description = "扩展字段3")
    private String ext3;
}
