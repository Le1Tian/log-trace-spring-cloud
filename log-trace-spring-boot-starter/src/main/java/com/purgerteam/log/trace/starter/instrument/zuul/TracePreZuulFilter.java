package com.purgerteam.log.trace.starter.instrument.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.purgerteam.log.trace.starter.Constants;
import com.purgerteam.log.trace.starter.TraceContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

/**
 * @author purgeyao
 * @since 1.0
 */
public class TracePreZuulFilter extends ZuulFilter {

    private static final Logger log = LoggerFactory.getLogger(TracePreZuulFilter.class);

    private TraceContentFactory traceContentFactory;

    public TracePreZuulFilter(TraceContentFactory traceContentFactory){
        this.traceContentFactory = traceContentFactory;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * filterOrder=0 只是指在Zuul的pre类型过滤器中的优先级，不是整个应用所有过滤器的优先级。
     *
     * Spring Boot的Servlet Filter（如TraceFilter）在DispatcherServlet之前执行，属于标准Servlet规范。
     * Zuul的Filter（如TracePreZuulFilter）是在Servlet Filter之后、Zuul内部处理请求时才会执行。
     * Zuul的官方文档明确说明：Servlet Filter优先于Zuul Filter。
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        Map<String, String> copyOfContextMap = traceContentFactory.assemblyTraceContent();  // 从当前环境上下文中获取服务变量名称
        for (Map.Entry<String, String> copyOfContext : copyOfContextMap.entrySet()) {
            context.addZuulRequestHeader(copyOfContext.getKey(), copyOfContext.getValue());  // 加到转发请求头里面去
        }
        log.debug("zuul traceid {}", MDC.get(Constants.LEGACY_TRACE_ID_NAME));
        return null;
    }
}
