package io.github.kongweiguang.core.map;

import io.github.kongweiguang.core.exception.BeanConversionException;
import io.github.kongweiguang.core.lang.Assert;
import io.github.kongweiguang.core.lang.Strs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * map工具类
 *
 * @author kongweiguang
 */
public class Maps {

    private static final Map<Class<?>, TypeConverter<?>> TYPE_CONVERTERS = new HashMap<>();

    static {
        // 基本类型转换器
        registerConverter(String.class, value -> value instanceof String ? (String) value : String.valueOf(value));
        registerConverter(Integer.class, value -> value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(String.valueOf(value)));
        registerConverter(int.class, value -> value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(String.valueOf(value)));
        registerConverter(Long.class, value -> value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value)));
        registerConverter(long.class, value -> value instanceof Number ? ((Number) value).longValue() : Long.parseLong(String.valueOf(value)));
        registerConverter(Double.class, value -> value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(String.valueOf(value)));
        registerConverter(double.class, value -> value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(String.valueOf(value)));
        registerConverter(Float.class, value -> value instanceof Number ? ((Number) value).floatValue() : Float.parseFloat(String.valueOf(value)));
        registerConverter(float.class, value -> value instanceof Number ? ((Number) value).floatValue() : Float.parseFloat(String.valueOf(value)));
        registerConverter(Boolean.class, value -> value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(String.valueOf(value)));
        registerConverter(boolean.class, value -> value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(String.valueOf(value)));
        registerConverter(Byte.class, value -> value instanceof Number ? ((Number) value).byteValue() : Byte.parseByte(String.valueOf(value)));
        registerConverter(byte.class, value -> value instanceof Number ? ((Number) value).byteValue() : Byte.parseByte(String.valueOf(value)));
        registerConverter(Short.class, value -> value instanceof Number ? ((Number) value).shortValue() : Short.parseShort(String.valueOf(value)));
        registerConverter(short.class, value -> value instanceof Number ? ((Number) value).shortValue() : Short.parseShort(String.valueOf(value)));
        registerConverter(Character.class, value -> value instanceof Character ? (Character) value : String.valueOf(value).charAt(0));
        registerConverter(char.class, value -> value instanceof Character ? (Character) value : String.valueOf(value).charAt(0));
        registerConverter(BigDecimal.class, value -> value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(String.valueOf(value)));
        registerConverter(BigInteger.class, value -> value instanceof BigInteger ? (BigInteger) value : new BigInteger(String.valueOf(value)));

        // 日期时间类型转换器
        registerConverter(Date.class, value -> {
            if (value instanceof Date) {
                return (Date) value;
            } else if (value instanceof LocalDateTime) {
                return Date.from(((LocalDateTime) value).atZone(TimeZone.getDefault().toZoneId()).toInstant());
            } else if (value instanceof LocalDate) {
                return Date.from(((LocalDate) value).atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant());
            } else if (value instanceof Number) {
                return new Date(((Number) value).longValue());
            } else if (value instanceof String) {
                try {
                    return new Date(Long.parseLong((String) value));
                } catch (NumberFormatException e) {
                    // 尝试解析日期字符串
                    return parseDate((String) value);
                }
            }
            throw new IllegalArgumentException("Cannot convert to Date: " + value);
        });

        registerConverter(java.sql.Date.class, value -> {
            if (value instanceof java.sql.Date) {
                return (java.sql.Date) value;
            } else if (value instanceof Date) {
                return new java.sql.Date(((Date) value).getTime());
            } else if (value instanceof Number) {
                return new java.sql.Date(((Number) value).longValue());
            } else if (value instanceof String) {
                try {
                    return new java.sql.Date(Long.parseLong((String) value));
                } catch (NumberFormatException e) {
                    // 尝试解析日期字符串
                    return java.sql.Date.valueOf(LocalDate.parse((String) value));
                }
            }
            throw new IllegalArgumentException("Cannot convert to java.sql.Date: " + value);
        });

        registerConverter(Timestamp.class, value -> {
            if (value instanceof Timestamp) {
                return (Timestamp) value;
            } else if (value instanceof Date) {
                return new Timestamp(((Date) value).getTime());
            } else if (value instanceof Number) {
                return new Timestamp(((Number) value).longValue());
            } else if (value instanceof String) {
                try {
                    return new Timestamp(Long.parseLong((String) value));
                } catch (NumberFormatException e) {
                    // 尝试解析日期时间字符串
                    return Timestamp.valueOf((String) value);
                }
            }
            throw new IllegalArgumentException("Cannot convert to Timestamp: " + value);
        });

        registerConverter(LocalDate.class, value -> {
            if (value instanceof LocalDate) {
                return (LocalDate) value;
            } else if (value instanceof String) {
                return LocalDate.parse((String) value);
            } else if (value instanceof Date) {
                return ((Date) value).toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
            }
            throw new IllegalArgumentException("Cannot convert to LocalDate: " + value);
        });

        registerConverter(LocalTime.class, value -> {
            if (value instanceof LocalTime) {
                return (LocalTime) value;
            } else if (value instanceof String) {
                return LocalTime.parse((String) value);
            } else if (value instanceof Date) {
                return ((Date) value).toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalTime();
            }
            throw new IllegalArgumentException("Cannot convert to LocalTime: " + value);
        });

        registerConverter(LocalDateTime.class, value -> {
            if (value instanceof LocalDateTime) {
                return (LocalDateTime) value;
            } else if (value instanceof String) {
                return LocalDateTime.parse((String) value);
            } else if (value instanceof Date) {
                return ((Date) value).toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
            }
            throw new IllegalArgumentException("Cannot convert to LocalDateTime: " + value);
        });
    }

    /**
     * 将map中的key转换为驼峰命名
     *
     * @param map 原map
     * @return 转换后的map
     */
    public static Map<String, Object> key2CamelCase(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        map.forEach((k, v) -> result.put(Strs.toCamelCase(k), v));
        return result;
    }

    /**
     * 将Map转换为指定类型的对象
     *
     * @param map   数据源Map
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 转换后的对象
     */
    public static <T> T toBean(Map<String, Object> map, Class<T> clazz) {
        Assert.notNull(map, "Map must not be null");
        Assert.notNull(clazz, "Class must not be null");

        try {
            T instance = createInstance(clazz);
            return populateBean(map, instance);
        } catch (Exception e) {
            throw new BeanConversionException("Failed to convert map to bean of type " + clazz.getName(), e);
        }
    }

    /**
     * 将Map列表转换为指定类型的对象列表
     *
     * @param mapList 数据源Map列表
     * @param clazz   目标类型
     * @param <T>     泛型
     * @return 转换后的对象列表
     */
    public static <T> List<T> toBeanList(List<Map<String, Object>> mapList, Class<T> clazz) {
        Assert.notNull(mapList, "Map list must not be null");
        Assert.notNull(clazz, "Class must not be null");

        List<T> resultList = new ArrayList<>(mapList.size());
        for (Map<String, Object> map : mapList) {
            resultList.add(toBean(map, clazz));
        }
        return resultList;
    }

    /**
     * 创建实例
     *
     * @param clazz 类型
     * @param <T>   泛型
     * @return 实例
     * @throws Exception 异常
     */
    private static <T> T createInstance(Class<T> clazz) throws Exception {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new BeanConversionException("No default constructor found for " + clazz.getName(), e);
        }
    }

    /**
     * 填充Bean属性
     *
     * @param map      数据源Map
     * @param instance 实例
     * @param <T>      泛型
     * @return 填充后的实例
     * @throws Exception 异常
     */
    private static <T> T populateBean(Map<String, Object> map, T instance) throws Exception {
        Class<?> clazz = instance.getClass();
        Map<String, Field> fieldMap = getAllFields(clazz);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            // 尝试直接匹配字段名
            Field field = fieldMap.get(key);

            // 尝试驼峰命名转换（如 user_name -> userName）
            if (field == null && key.contains("_")) {
                String camelKey = Strs.toCamelCase(key);
                field = fieldMap.get(camelKey);
            }

            if (field != null) {
                setFieldValue(instance, field, value);
            }
        }

        return instance;
    }

    /**
     * 获取类的所有字段（包括父类）
     *
     * @param clazz 类
     * @return 字段Map
     */
    private static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                fieldMap.putIfAbsent(field.getName(), field);
            }
            currentClass = currentClass.getSuperclass();
        }

        return fieldMap;
    }

    /**
     * 设置字段值
     *
     * @param instance 实例
     * @param field    字段
     * @param value    值
     * @throws Exception 异常
     */
    private static void setFieldValue(Object instance, Field field, Object value) throws Exception {
        Class<?> fieldType = field.getType();

        // 如果值为null或者类型已经匹配，直接设置
        if (value == null || fieldType.isInstance(value)) {
            field.setAccessible(true);
            field.set(instance, value);
            return;
        }

        // 尝试使用setter方法
        String setterName = "set" + capitalize(field.getName());
        try {
            Method setter = instance.getClass().getMethod(setterName, fieldType);
            setter.invoke(instance, convertValue(value, fieldType));
            return;
        } catch (NoSuchMethodException e) {
            // 没有setter方法，继续使用反射设置字段
        }

        // 使用类型转换器转换值
        field.setAccessible(true);
        field.set(instance, convertValue(value, fieldType));
    }

    /**
     * 转换值为指定类型
     *
     * @param value 原始值
     * @param type  目标类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    private static Object convertValue(Object value, Class<?> type) {
        if (value == null) {
            return null;
        }

        // 如果类型已经匹配，直接返回
        if (type.isInstance(value)) {
            return value;
        }

        // 使用注册的转换器
        TypeConverter<?> converter = TYPE_CONVERTERS.get(type);
        if (converter != null) {
            return converter.convert(value);
        }

        // 处理枚举类型
        if (type.isEnum()) {
            return convertToEnum(value, (Class<? extends Enum<?>>) type);
        }

        // 处理数组类型
        if (type.isArray()) {
            // 数组类型转换逻辑...
            throw new UnsupportedOperationException("Array conversion not implemented yet");
        }

        // 处理集合类型
        if (Collection.class.isAssignableFrom(type)) {
            // 集合类型转换逻辑...
            throw new UnsupportedOperationException("Collection conversion not implemented yet");
        }

        // 如果没有合适的转换器，尝试使用toString方法
        return value.toString();
    }

    /**
     * 转换为枚举类型
     *
     * @param value     原始值
     * @param enumClass 枚举类
     * @return 枚举值
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Enum<?> convertToEnum(Object value, Class<? extends Enum<?>> enumClass) {
        if (value instanceof Number) {
            // 如果是数字，按照序号转换
            int ordinal = ((Number) value).intValue();
            Enum<?>[] constants = enumClass.getEnumConstants();
            if (ordinal >= 0 && ordinal < constants.length) {
                return constants[ordinal];
            }
        } else {
            // 如果是字符串，按照名称转换
            String name = value.toString();
            try {
                // 使用反射安全地调用valueOf方法，避免不安全的类型转换
                Method valueOfMethod = enumClass.getMethod("valueOf", String.class);
                return (Enum<?>) valueOfMethod.invoke(null, name);
            } catch (Exception e) {
                // 尝试忽略大小写
                for (Enum<?> constant : enumClass.getEnumConstants()) {
                    if (constant.name().equalsIgnoreCase(name)) {
                        return constant;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Cannot convert " + value + " to enum " + enumClass.getName());
    }

    /**
     * 注册类型转换器
     *
     * @param type      类型
     * @param converter 转换器
     * @param <T>       泛型
     */
    private static <T> void registerConverter(Class<T> type, TypeConverter<T> converter) {
        TYPE_CONVERTERS.put(type, converter);
    }

    /**
     * 首字母大写
     *
     * @param str 字符串
     * @return 首字母大写的字符串
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    private static Date parseDate(String dateStr) {
        // 常见日期格式
        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd"
        };

        for (String pattern : patterns) {
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

    /**
     * 将对象转换为Map
     *
     * @param obj 源对象
     * @return 转换后的Map
     */
    public static Map<String, Object> toMap(Object obj) {
        Assert.notNull(obj, "Source object must not be null");

        try {
            Map<String, Object> result = new HashMap<>();
            Map<String, Field> fieldMap = getAllFields(obj.getClass());

            for (Field field : fieldMap.values()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(obj);

                // 尝试使用getter方法
                String getterName = "get" + capitalize(fieldName);
                try {
                    Method getter = obj.getClass().getMethod(getterName);
                    value = getter.invoke(obj);
                } catch (NoSuchMethodException e) {
                    // 如果没有getter方法，继续使用字段值
                }

                // 处理特殊类型的值
                if (value != null) {
                    switch (value) {
                        case Enum<?> anEnum -> value = anEnum.name();
                        case Date date -> value = date.getTime();
                        case LocalDateTime localDateTime ->
                                value = Date.from(localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant()).getTime();
                        case LocalDate localDate ->
                                value = Date.from(localDate.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant()).getTime();
                        case LocalTime localTime -> value = value.toString();
                        default -> value.toString();
                    }
                }

                result.put(fieldName, value);
            }

            return result;
        } catch (Exception e) {
            throw new BeanConversionException("Failed to convert object to map", e);
        }
    }

    /**
     * 将对象列表转换为Map列表
     *
     * @param objList 对象列表
     * @return 转换后的Map列表
     */
    public static List<Map<String, Object>> toMapList(List<?> objList) {
        Assert.notNull(objList, "Object list must not be null");

        List<Map<String, Object>> resultList = new ArrayList<>(objList.size());
        for (Object obj : objList) {
            resultList.add(toMap(obj));
        }
        return resultList;
    }

}
