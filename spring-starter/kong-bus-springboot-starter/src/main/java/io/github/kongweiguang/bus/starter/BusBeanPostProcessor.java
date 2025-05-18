package io.github.kongweiguang.bus.starter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

import static io.github.kongweiguang.bus.Bus.hub;


/**
 * 注册销毁oper
 *
 * @author kongweiguang
 */
public class BusBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        hub().removeClass(bean);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        hub().pullClass(bean);
        return bean;
    }
}
