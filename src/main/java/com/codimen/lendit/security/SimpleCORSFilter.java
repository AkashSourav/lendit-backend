package com.codimen.lendit.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class SimpleCORSFilter implements Filter {

    @Value("${api.access.control.origin.header.value}")
    private String crossOriginHeaderValue;

    //allowed origins
    private String [] allowedDomains;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        log.info("<====== Started doFilter(ServletRequest, ServletResponse, FilterChain) ======>");
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        if(allowedDomains == null){
            allowedDomains = crossOriginHeaderValue.split(",");
        }
        String x_real_ip = "test";//request.getHeader("x-real-ip");
        String requestMethod = request.getMethod();
        String requestOrigin = "http://localhost:4200";//request.getHeader("origin");
        String requestURI = request.getRequestURI();
        String requestURL = request.getRequestURL().toString();
        log.info("<====== Request Origin    :: " + requestOrigin +" ======>");
        log.info("<====== Request x-real-ip :: " + x_real_ip +" ======>");
        log.info("<====== Request Method    :: "+ requestMethod +" ======>");
        log.info("<====== Request URL       :: "+ requestURL +" ======>");
        log.info("<====== Request URI       :: "+ requestURI +" ======>");

        //postman_token not equal to null, means request came from postman
//        String postman_token = request.getHeader("postman-token");
//        if(postman_token != null){
//            log.info("<====== Request Origin :: postman-token :: " + postman_token);
//            response.sendError(401, "Unauthorized Request from Postman");
//            log.warn("Postman Request Origin Blocked :: " + requestOrigin);
//            return;
//        }

        if (requestOrigin != null) {
            boolean isHeaderMatched = false;
            for (String domain : allowedDomains) {
                if (requestOrigin.equals(domain)) {
                    response.setHeader("Access-Control-Allow-Origin", requestOrigin);
                    isHeaderMatched = true;
                    log.info("Request Origin Passed :: " + requestOrigin);
                    break;
                }
            }
            if (!isHeaderMatched) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
                log.warn("Request Origin Blocked :: " + requestOrigin);
                return;
            }
        } else if (!(requestURI.contains("configuration/") || requestURI.contains("swagger")
                || requestURI.contains("v2")|| requestURI.contains("download-vulnerability-file"))) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized request without origin");
            log.warn("Null Request Origin Blocked :: " + requestOrigin);
            return;
        }


        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");

        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, X-Requested-With, X-XSRF-TOKEN");
        response.addHeader("Access-Control-Expose-Headers", "X-XSRF-TOKEN");

        // Session filter Starts
        log.info("<====== Started doFilter(ServletRequest, ServletResponse, FilterChain) ======>");
        HttpServletRequest httpServletRequest = (HttpServletRequest)req;
        HttpServletResponse httpServletResponse = (HttpServletResponse)res;

        log.info("<====== User IP x-real-ip :: "+x_real_ip+" ======>");

        if(x_real_ip == null){
            log.error(" User x-real-ip not Found ");
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
            return;
        }

        // Storing Context Data
        ContextData contextData = new ContextData();
        contextData.setOrigin(httpServletRequest.getHeader("origin"));
        contextData.setX_real_ip(x_real_ip);
        ContextStorage.set(contextData);
        log.info("<====== End doFilter(ServletRequest, ServletResponse, FilterChain) ======>");

        // Session filter Ends

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            try {
                chain.doFilter(req, res);
            } catch (RuntimeException e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig){}
    @Override
    public void destroy(){}
}
