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

}
