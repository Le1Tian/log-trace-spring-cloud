package com.purgerteam.log.trace.starter;

import com.purgerteam.log.trace.starter.filter.TraceFilter;
import com.purgerteam.log.trace.starter.handlers.DefaultTraceMetaObjectHandler;
import com.purgerteam.log.trace.starter.handlers.TraceMetaObjectHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author purgeyao
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(TraceLogProperties.class)    // TraceLogProperties 类需用 @ConfigurationProperties 注解标记，用于绑定配置文件（如 application.yml）中的属性
public class TraceAutoConfiguration {

    @Bean
    public TraceFilter traceFilter(TraceLogProperties traceLogProperties) {
        return new TraceFilter(traceLogProperties);
    }

    @Bean
    public TraceContentFactory traceContentFactory(Map<String, TraceMetaObjectHandler> traceMetaObjectHandlerMap) {
        return new TraceContentFactory(traceMetaObjectHandlerMap);
    }

    @Bean
    public DefaultTraceMetaObjectHandler defaultTraceMetaObjectHandler() {
        return new DefaultTraceMetaObjectHandler();
    }
}
