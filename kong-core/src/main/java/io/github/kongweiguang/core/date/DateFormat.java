package io.github.kongweiguang.core.date;

import java.util.List;

/**
 * 日期格式
 *
 * @author kongweiguang
 */
public class DateFormat {

    public static final String YYYY_MM_DD_HH_MM_SS_DASH = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_DOT = "yyyy.MM.dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_CN = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String YYYY_MM_DD_DASH = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_SLASH = "yyyy/MM/dd";
    public static final String YYYY_MM_DD_DOT = "yyyy.MM.dd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String HH_MM_SS_CN = "HH时mm分ss秒";
    public static final String YYYY_MM_DD_HH_MM_DASH = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS_DASH = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYY_MM_DD_HH_MM_SS_SSSSSS_DASH = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String EEE_DD_MMM_YYYY_HH_MM_SS_Z = "EEE, dd MMM yyyy HH:mm:ss z";
    public static final String EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String ISO_OFFSET_DATE_TIME_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String ISO_OFFSET_DATE_TIME_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ISO_OFFSET_DATE_TIME_OFFSET = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String ISO_OFFSET_DATE_TIME_SSS_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * 所有日期格式
     *
     * @return 所有日期格式
     */
    public static List<String> allFormat() {
        return List.of(YYYY_MM_DD_HH_MM_SS_DASH,
                YYYY_MM_DD_HH_MM_SS_SLASH,
                YYYY_MM_DD_HH_MM_SS_DOT,
                YYYY_MM_DD_HH_MM_SS_CN,
                YYYY_MM_DD_DASH,
                YYYY_MM_DD_SLASH,
                YYYY_MM_DD_DOT,
                HH_MM_SS,
                HH_MM_SS_CN,
                YYYY_MM_DD_HH_MM_DASH,
                YYYY_MM_DD_HH_MM_SS_SSS_DASH,
                YYYY_MM_DD_HH_MM_SS_SSSSSS_DASH,
                YYYYMMDDHHMMSS,
                YYYYMMDDHHMMSSSSS,
                YYYYMMDD,
                EEE_DD_MMM_YYYY_HH_MM_SS_Z,
                EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY,
                ISO_OFFSET_DATE_TIME_Z,
                ISO_OFFSET_DATE_TIME_SSS_Z,
                ISO_OFFSET_DATE_TIME_OFFSET,
                ISO_OFFSET_DATE_TIME_SSS_OFFSET
        );
    }
}
