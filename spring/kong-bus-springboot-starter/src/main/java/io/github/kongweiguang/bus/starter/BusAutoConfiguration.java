package io.github.kongweiguang.bus.starter;

import io.github.kongweiguang.bus.Bus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * 自动装配
 *
 * @author kongweiguang
 */
@Component
@ConditionalOnClass(Bus.class)
@Import(BusBeanPostProcessor.class)
public class BusAutoConfiguration {
}
