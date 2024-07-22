package com.hhp.concert.util.config;

import com.hhp.concert.util.JwtUtil;
import com.hhp.concert.util.filter.JwtWaitingAuthFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    private static final Logger logger = LogManager.getLogger(FilterConfig.class);

    @Bean
    public JwtWaitingAuthFilter jwtWaitingAuthFilter(JwtUtil jwtUtil) {
        return new JwtWaitingAuthFilter(jwtUtil);
    }

    @Bean
    public FilterRegistrationBean<JwtWaitingAuthFilter> jwtAuthenticationFilterRegistration(JwtWaitingAuthFilter jwtWaitingAuthFilter) {
        FilterRegistrationBean<JwtWaitingAuthFilter> registrationBean = new FilterRegistrationBean<>();
        logger.info("filter registrationBean");
        registrationBean.setFilter(jwtWaitingAuthFilter);
        registrationBean.addUrlPatterns("/concert/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

}
