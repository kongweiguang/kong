package io.github.kongweiguang.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * spring工具
 *
 * @author kongweiguang
 */
@SuppressWarnings("all")
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        context(applicationContext);
    }

    /**
     * 获取应用的上下文
     *
     * @return {@link ApplicationContext}
     */
    public static ApplicationContext context() {
        return context;
    }

    /**
     * 设置应用的上下文
     *
     * @param c {@link ApplicationContext}
     */
    public static void context(ApplicationContext c) {
        SpringUtil.context = c;
    }

    /**
     * 根据类型获得bean
     *
     * @param clazz 类型
     * @param <T>
     * @return bean
     */
    public static <T> T getBean(final Class<T> clazz) {
        return ofNullable(context()).map(c -> c.getBean(clazz)).orElse(null);
    }

    /**
     * 根据昵称和类型获取bean
     *
     * @param name  昵称
     * @param clazz 类型
     * @param <T>
     * @return
     */
    public static <T> T getBean(final String name, final Class<T> clazz) {
        return ofNullable(context()).map(c -> c.getBean(name, clazz)).orElse(null);
    }

    /**
     * 根据昵称获取bean
     *
     * @param name 昵称
     * @param <T>
     * @return bean
     */
    public static <T> T getBean(final String name) {
        return (T) ofNullable(context()).map(c -> c.getBean(name)).orElse(null);
    }

    /**
     * 获取当前的环境
     *
     * @return 环境
     */
    public static String env() {
        final String[] envs = envs();

        return nonNull(envs) ? envs[0] : null;
    }

    /**
     * 获取当前激活的所有环境
     *
     * @return 环境数组
     */
    public static String[] envs() {
        return ofNullable(context()).map(c -> c.getEnvironment()).map(c -> c.getActiveProfiles()).orElse(null);
    }

    /**
     * 获取应用的昵称
     *
     * @return 昵称
     */
    public static String appName() {
        return getProperty("spring.application.name");
    }

    /**
     * 获取配置文件的值
     *
     * @param name
     * @return
     */
    public static String getProperty(final String name) {
        return ofNullable(context()).map(c -> c.getEnvironment()).map(e -> e.getProperty(name)).orElse(null);
    }

    /**
     * 发布事件
     *
     * @param evnet 事件对象
     */
    public static void publish(final Object evnet) {
        ofNullable(context()).ifPresent(c -> c.publishEvent(evnet));
    }

}
