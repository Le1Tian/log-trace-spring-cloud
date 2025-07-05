package com.purgerteam.log.trace.starter.filter;

import com.purgerteam.log.trace.starter.TraceContentFactory;
import com.purgerteam.log.trace.starter.TraceLogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求拦截器 初始化 Trace 内容
 *
 * @author <a href="mailto:yaoonlyi@gmail.com">purgeyao</a>
 * @since 1.0.0
 */
public class TraceFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TraceFilter.class);

    private TraceLogProperties traceLogProperties;

    public TraceFilter(TraceLogProperties traceLogProperties) {
        this.traceLogProperties = traceLogProperties;
    }


    /**
     * 过滤器核心方法：拦截HTTP请求，解析并存储追踪信息到日志上下文（MDC）
     * @param servletRequest  请求对象
     * @param servletResponse 响应对象
     * @param filterChain     过滤器链（用于继续执行后续过滤器）
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        // 1. 类型转换：将通用ServletRequest转为HttpServletRequest（便于获取HTTP特有信息）
        HttpServletRequest request = ((HttpServletRequest) servletRequest);

        // 2. 初始化存储容器：用于存放从请求头提取的追踪信息
        Map<String, String> formatMap = new HashMap<>(16);

        // 3. 获取配置的追踪字段：从traceLogProperties读取需要捕获的HTTP头字段（例如X-B3-TraceId）
        Set<String> expandFormat = traceLogProperties.getFormat();

        // 4. 遍历所有配置字段，从请求头提取值
        for (String k : expandFormat) {
            // 4.1 获取请求头值
            String v = request.getHeader(k);

            // 4.2 如果值非空，进行URL解码后存储（防止含特殊字符）
            if (!StringUtils.isEmpty(v)) {
                formatMap.put(k, URLDecoder.decode(v, "UTF-8"));
            }
        }

        // 5. 将追踪信息写入MDC（Mapped Diagnostic Context，日志上下文）
        //    后续日志输出时可通过%X{key}引用（例如logback配置）
        TraceContentFactory.storageMDC(formatMap);

        // 6. 放行请求（执行后续过滤器链和业务逻辑）
        filterChain.doFilter(servletRequest, servletResponse);

        // 7. 清除MDC（防止线程池复用导致信息污染）
        MDC.clear();
    }

}
