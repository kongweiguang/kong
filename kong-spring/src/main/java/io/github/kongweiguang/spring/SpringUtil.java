package io.github.kongweiguang.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static java.util.Optional.ofNullable;

@SuppressWarnings("all")
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext context;

//    @Override
//    public void initialize(final ConfigurableApplicationContext applicationContext) {
//        SpringUtil.context = applicationContext;
//    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        SpringUtil.context = applicationContext;
    }

    public static ApplicationContext context() {
        return context;
    }


    public static <T> T getBean(final Class<T> clazz) {
        return ofNullable(context).map(c -> c.getBean(clazz)).orElse(null);
    }

    public static <T> T getBean(final String name, final Class<T> clazz) {
        return ofNullable(context()).map(c -> c.getBean(name, clazz)).orElse(null);
    }


    public static <T> T getBean(final String name) {
        return (T) ofNullable(context).map(c -> c.getBean(name)).orElse(null);
    }

    public static String env() {
        final String[] envs = envs();

        return ofNullable(envs).isPresent() ? envs[0] : null;
    }

    public static String[] envs() {
        return ofNullable(context).map(c -> c.getEnvironment()).map(c -> c.getActiveProfiles()).orElse(new String[0]);
    }

    public static String appName() {
        return getProperty("spring.application.name");
    }

    public static String getProperty(final String name) {
        return ofNullable(context).map(c -> c.getEnvironment()).map(e -> e.getProperty(name)).orElse(null);
    }

    public static void publish(Object obj) {
        ofNullable(context).ifPresent(c -> c.publishEvent(obj));
    }

}
