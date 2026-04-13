package io.github.kongweiguang.core.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具类
 *
 * @author kongweiguang
 */
public class Reflections {

    /**
     * 获取方法的参数泛型
     *
     * @param m 方法
     * @return 参数泛型列表
     */
    public static List<String> generics(Method m) {
        List<String> fr = new ArrayList<>(2);
        Type[] genericParameterTypes = m.getGenericParameterTypes();

        for (Type type : genericParameterTypes) {
            if (type instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();

                for (Type actualType : actualTypeArguments) {
                    fr.add(actualType.getTypeName());
                }

            }
        }

        return fr;
    }
}
