package io.github.kongweiguang.core.convert;

import io.github.kongweiguang.core.date.Dates;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.kongweiguang.core.lang.Objs.isNull;

/**
 * 类型转换工具类，提供各种类型之间的转换功能
 *
 * @author kongweiguang
 */
public class Cvts {

    private static final Map<Class<?>, Converter<?>> TYPE_CONVERTERS = new HashMap<>();

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
                    return Dates.parseDate((String) value);
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
     * 转换值为指定类型
     *
     * @param value 原始值
     * @param type  目标类型
     * @param <T>   泛型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value, Class<T> type) {
        if (value == null) {
            return null;
        }

        // 如果类型已经匹配，直接返回
        if (type.isInstance(value)) {
            return (T) value;
        }

        // 使用注册的转换器
        Converter<?> converter = TYPE_CONVERTERS.get(type);
        if (converter != null) {
            return (T) converter.convert(value);
        }

        // 处理枚举类型
        if (type.isEnum()) {
            return (T) toEnum(value, (Class<? extends Enum<?>>) type);
        }

        // 处理数组类型
        if (type.isArray()) {
            return (T) toAry(value, type.getComponentType());
        }

        // 处理集合类型
        if (Collection.class.isAssignableFrom(type)) {
            return (T) toColl(value, type);
        }

        // 处理Map类型
        if (Map.class.isAssignableFrom(type)) {
            return (T) toMap(value, type);
        }

        // 如果没有合适的转换器，尝试使用toString方法
        return (T) value.toString();
    }

    /**
     * 转换为枚举类型
     *
     * @param value     原始值
     * @param enumClass 枚举类
     * @return 枚举值
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Enum<?> toEnum(Object value, Class<? extends Enum<?>> enumClass) {
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
     * 转换为数组类型
     *
     * @param value         原始值
     * @param componentType 数组元素类型
     * @return 数组
     */
    private static Object toAry0(Object value, Class<?> componentType) {
        if (value == null) {
            return null;
        }

        // 如果已经是数组类型且元素类型匹配，直接返回
        if (value.getClass().isArray() && value.getClass().getComponentType() == componentType) {
            return value;
        }

        // 如果是集合类型，转换为数组
        if (value instanceof Collection<?> collection) {
            Object array = Array.newInstance(componentType, collection.size());
            int i = 0;
            for (Object item : collection) {
                Array.set(array, i++, convert(item, componentType));
            }
            return array;
        }

        // 如果是单个值，创建只有一个元素的数组
        Object array = Array.newInstance(componentType, 1);
        Array.set(array, 0, convert(value, componentType));
        return array;
    }

    /**
     * 转换为集合类型
     *
     * @param value          原始值
     * @param collectionType 集合类型
     * @return 集合
     */
    @SuppressWarnings("unchecked")
    private static Collection<?> toColl(Object value, Class<?> collectionType) {
        if (value == null) {
            return null;
        }

        Collection<Object> result;

        // 创建合适的集合实例
        if (collectionType.isInterface()) {
            if (List.class.isAssignableFrom(collectionType)) {
                result = new ArrayList<>();
            } else if (Set.class.isAssignableFrom(collectionType)) {
                result = new HashSet<>();
            } else if (Queue.class.isAssignableFrom(collectionType)) {
                result = new LinkedList<>();
            } else {
                throw new IllegalArgumentException("Unsupported collection type: " + collectionType.getName());
            }
        } else {
            try {
                result = (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot instantiate collection type: " + collectionType.getName(), e);
            }
        }

        // 填充集合
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                result.add(Array.get(value, i));
            }
        } else if (value instanceof Collection<?> collection) {
            result.addAll(collection);
        } else if (value instanceof Map<?, ?> map) {
            result.addAll(map.values());
        } else {
            // 单个值转换为只有一个元素的集合
            result.add(value);
        }

        return result;
    }

    /**
     * 转换为Map类型
     *
     * @param value   原始值
     * @param mapType Map类型
     * @return Map
     */
    @SuppressWarnings("unchecked")
    private static Map<?, ?> toMap(Object value, Class<?> mapType) {
        if (isNull(value)) {
            return null;
        }

        Map<Object, Object> result;

        // 创建合适的Map实例
        if (mapType.isInterface()) {
            if (SortedMap.class.isAssignableFrom(mapType)) {
                result = new TreeMap<>();
            } else {
                result = new HashMap<>();
            }
        } else {
            try {
                result = (Map<Object, Object>) mapType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot instantiate map type: " + mapType.getName(), e);
            }
        }

        // 填充Map
        if (value instanceof Map<?, ?> map) {
            result.putAll(map);
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                result.put(i, Array.get(value, i));
            }
        } else if (value instanceof Collection<?> collection) {
            int i = 0;
            for (Object item : collection) {
                result.put(i++, item);
            }
        } else {
            // 单个值转换为只有一个元素的Map，使用0作为键
            result.put(0, value);
        }

        return result;
    }

    /**
     * 注册类型转换器
     *
     * @param type      类型
     * @param converter 转换器
     * @param <T>       泛型
     */
    public static <T> void registerConverter(Class<T> type, Converter<T> converter) {
        TYPE_CONVERTERS.put(type, converter);
    }

    /**
     * 转换字符串为指定类型
     *
     * @param str   字符串
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 转换后的对象
     */
    public static <T> T toStr(String str, Class<T> clazz) {
        return convert(str, clazz);
    }

    /**
     * 转换对象为字符串
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String toStr(Object obj) {
        return convert(obj, String.class);
    }

    /**
     * 转换对象为整数
     *
     * @param obj 对象
     * @return 整数
     */
    public static Integer toInt(Object obj) {
        return convert(obj, Integer.class);
    }

    /**
     * 转换对象为长整数
     *
     * @param obj 对象
     * @return 长整数
     */
    public static Long toLong(Object obj) {
        return convert(obj, Long.class);
    }

    /**
     * 转换对象为双精度浮点数
     *
     * @param obj 对象
     * @return 双精度浮点数
     */
    public static Double toDouble(Object obj) {
        return convert(obj, Double.class);
    }

    /**
     * 转换对象为布尔值
     *
     * @param obj 对象
     * @return 布尔值
     */
    public static Boolean toBool(Object obj) {
        return convert(obj, Boolean.class);
    }

    /**
     * 转换对象为日期
     *
     * @param obj 对象
     * @return 日期
     */
    public static Date toDate(Object obj) {
        return convert(obj, Date.class);
    }

    /**
     * 转换对象为本地日期
     *
     * @param obj 对象
     * @return 本地日期
     */
    public static LocalDate toLocalDate(Object obj) {
        return convert(obj, LocalDate.class);
    }

    /**
     * 转换对象为本地时间
     *
     * @param obj 对象
     * @return 本地时间
     */
    public static LocalTime toLocalTime(Object obj) {
        return convert(obj, LocalTime.class);
    }

    /**
     * 转换对象为本地日期时间
     *
     * @param obj 对象
     * @return 本地日期时间
     */
    public static LocalDateTime toLocalDateTime(Object obj) {
        return convert(obj, LocalDateTime.class);
    }

    /**
     * 转换对象为大整数
     *
     * @param obj 对象
     * @return 大整数
     */
    public static BigInteger toBigInt(Object obj) {
        return convert(obj, BigInteger.class);
    }

    /**
     * 转换对象为大小数
     *
     * @param obj 对象
     * @return 大小数
     */
    public static BigDecimal toBigDecimal(Object obj) {
        return convert(obj, BigDecimal.class);
    }

    /**
     * 转换对象为列表
     *
     * @param obj           对象
     * @param componentType 列表元素类型
     * @param <T>           泛型
     * @return 列表
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(Object obj, Class<T> componentType) {
        if (isNull(obj)) {
            return new ArrayList<>();
        }

        Collection<?> collection = (Collection<?>) convert(obj, List.class);
        return collection.stream()
                .map(item -> convert(item, componentType))
                .collect(Collectors.toList());
    }

    /**
     * 转换对象为集合
     *
     * @param obj           对象
     * @param componentType 集合元素类型
     * @param <T>           泛型
     * @return 集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> toSet(Object obj, Class<T> componentType) {
        if (isNull(obj)) {
            return new HashSet<>();
        }

        Collection<?> collection = (Collection<?>) convert(obj, Set.class);
        return collection.stream()
                .map(item -> convert(item, componentType))
                .collect(Collectors.toSet());
    }

    /**
     * 转换对象为数组
     *
     * @param obj           对象
     * @param componentType 数组元素类型
     * @param <T>           泛型
     * @return 数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toAry(Object obj, Class<T> componentType) {
        if (isNull(obj)) {
            return (T[]) Array.newInstance(componentType, 0);
        }

        return (T[]) toAry0(obj, componentType);
    }

    /**
     * 转换对象为Map
     *
     * @param obj 对象
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        if (isNull(obj)) {
            return new HashMap<>();
        }

        return (Map<String, Object>) convert(obj, Map.class);
    }
}