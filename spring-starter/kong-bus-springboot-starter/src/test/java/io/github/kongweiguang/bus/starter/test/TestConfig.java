package io.github.kongweiguang.bus.starter.test;

import io.github.kongweiguang.bus.starter.BusAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot 3 测试配置类
 * 用于提供测试环境所需的配置
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("io.github.kongweiguang.bus.starter.test")
@Import(BusAutoConfiguration.class)
public class TestConfig {
    // 空配置类，仅用于Spring Boot上下文配置
}
