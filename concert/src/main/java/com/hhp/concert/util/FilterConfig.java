package com.hhp.concert.util;

import com.hhp.concert.util.filter.JwtWaitingAuthFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    @Bean
    public JwtWaitingAuthFilter jwtWaitingAuthFilter(JwtUtil jwtUtil) {
        return new JwtWaitingAuthFilter(jwtUtil);
    }

    @Bean
    public FilterRegistrationBean<JwtWaitingAuthFilter> jwtAuthenticationFilterRegistration(JwtWaitingAuthFilter jwtWaitingAuthFilter) {
        FilterRegistrationBean<JwtWaitingAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtWaitingAuthFilter);
        registrationBean.addUrlPatterns("/concert/*/reservation", "/concert/*/payment");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
