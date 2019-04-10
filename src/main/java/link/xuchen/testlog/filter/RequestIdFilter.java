package link.xuchen.testlog.filter;

import org.springframework.stereotype.Component;
import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

/**
 * Create with IDEA
 * Ludan
 * Data:2019/4/9 4:55 PM
 * Description: controller过滤器,为请求添加requestId
 */
@Component
public class RequestIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String requestId = UUID.randomUUID().toString().replace("-", "");
        servletRequest.setAttribute("requestId", requestId);
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
