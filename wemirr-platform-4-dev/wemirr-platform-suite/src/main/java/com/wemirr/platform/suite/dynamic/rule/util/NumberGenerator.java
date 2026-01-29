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

package com.wemirr.platform.suite.dynamic.rule.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 编号生成器工具类
 *
 * @author Levin
 */
@Slf4j
@Component
public class NumberGenerator {

    /**
     * 生成编号
     *
     * @param prefix          前缀
     * @param dateFormat      日期格式
     * @param separator       分隔符
     * @param sequenceLength  流水号长度
     * @param currentSequence 当前流水号
     * @param step            步长
     * @param suffix          后缀
     * @return 生成的编号
     */
    public String generate(String prefix, String dateFormat, String separator,
                           int sequenceLength, AtomicLong currentSequence,
                           int step, String suffix) {
        StringBuilder sb = new StringBuilder();

        // 添加前缀
        if (prefix != null && !prefix.isEmpty()) {
            sb.append(prefix);
        }

        // 添加日期
        if (dateFormat != null && !dateFormat.isEmpty()) {
            if (sb.length() > 0 && separator != null && !separator.isEmpty()) {
                sb.append(separator);
            }
            sb.append(formatDate(dateFormat));
        }

        // 添加流水号
        if (sequenceLength > 0) {
            if (sb.length() > 0 && separator != null && !separator.isEmpty()) {
                sb.append(separator);
            }
            long seq = currentSequence.addAndGet(step);
            String seqStr = String.format("%0" + sequenceLength + "d", seq % (long) Math.pow(10, sequenceLength));
            sb.append(seqStr);
        }

        // 添加后缀
        if (suffix != null && !suffix.isEmpty()) {
            sb.append(suffix);
        }

        return sb.toString();
    }

    /**
     * 格式化日期
     *
     * @param format 日期格式
     * @return 格式化后的日期字符串
     */
    private String formatDate(String format) {
        if (format == null || format.isEmpty()) {
            return "";
        }

        DateTimeFormatter formatter;
        // 移除 yyyy-MM-dd HH:mm:ss 中的空格，转换为标准格式
        String normalizedFormat = format.replace(" ", "T");

        try {
            // 尝试解析为日期时间格式
            formatter = DateTimeFormatter.ofPattern(normalizedFormat.replace("HH:mm:ss", "HHmmss")
                    .replace("yyyy-MM-dd", "yyyyMMdd")
                    .replace("yyyy-MM", "yyyyMM")
                    .replace("HH:mm", "HHmm"));
            return LocalDateTime.now().format(formatter);
        } catch (Exception e) {
            // 如果失败，尝试解析为日期格式
            try {
                formatter = DateTimeFormatter.ofPattern(normalizedFormat.replace("yyyy-MM-dd", "yyyyMMdd"));
                return LocalDate.now().format(formatter);
            } catch (Exception ex) {
                log.warn("无法解析日期格式: {}, 使用默认格式", format);
                return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            }
        }
    }

    /**
     * 生成业务编号
     *
     * @param ruleCode        规则编码
     * @param prefix          前缀
     * @param dateFormat      日期格式
     * @param separator       分隔符
     * @param sequenceLength  流水号长度
     * @param currentSequence 当前流水号
     * @param step            步长
     * @param suffix          后缀
     * @return 生成的编号
     */
    public String generateBusinessNumber(String ruleCode, String prefix, String dateFormat,
                                         String separator, int sequenceLength,
                                         AtomicLong currentSequence, int step, String suffix) {
        log.info("为规则 [{}] 生成编号", ruleCode);
        return generate(prefix, dateFormat, separator, sequenceLength, currentSequence, step, suffix);
    }
}
