package io.github.kongweiguang.core.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具
 *
 * @author kongweiguang
 */
public class Dates {

    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String dateStr) {
        // 常见日期格式

        for (String pattern : DateFormat.allFormat()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
                return Date.from(dateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant());
            } catch (Exception ignored) {
                // 尝试下一个格式
            }
        }

        try {
            // 尝试解析为LocalDate
            LocalDate date = LocalDate.parse(dateStr);
            return Date.from(date.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse date: " + dateStr);
        }
    }
}
