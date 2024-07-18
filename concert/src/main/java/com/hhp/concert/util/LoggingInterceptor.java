package com.hhp.concert.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LogManager.getLogger(LoggingInterceptor.class);

    //interceptor 이용 로깅 처리

    //요청 시작
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        logger.info("Request URL: {}, Method: {}, Start Time: {}",
                request.getRequestURL(), request.getMethod(), startTime);

        return true;
    }


    //요청 끝
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;


        logger.info("Request URL: {}, Method: {}, Status: {}, End Time: {}, Duration: {} ms",
                request.getRequestURL(), request.getMethod(), response.getStatus(), endTime, duration);
    }
}