package io.github.kongweiguang.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        SpringUtil.context = applicationContext;
    }

    public static ApplicationContext context() {
        return context;
    }


    public static <T> T getBean(final Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(final String name, final Class<T> clazz) {
        return context.getBean(name, clazz);
    }


    public static <T> T getBean(final String name) {
        return (T) context.getBean(name);
    }

    public static String env() {
        return context.getEnvironment().getActiveProfiles()[0];
    }

    public static String[] envs(String key) {
        return context.getEnvironment().getActiveProfiles();
    }

    public static String appName() {
        return context.getEnvironment().getProperty("spring.application.name");
    }

}
