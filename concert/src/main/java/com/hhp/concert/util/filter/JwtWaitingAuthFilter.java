package com.hhp.concert.util.filter;

import com.hhp.concert.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtWaitingAuthFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(JwtWaitingAuthFilter.class);
    private final JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Initializing JwtWaitingAuthFilter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        logger.info("Request URI: {}", httpRequest.getRequestURI());

        String header = httpRequest.getHeader(JwtUtil.AUTHORIZATION_HEADER);
        String token = null;
        String username = null;

        logger.info("Authorization Header: {}", header);

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            username = jwtUtil.extractSign(token);

            logger.info("Token: {}", token);
            logger.info("Username: {}", username);
        }

        if (username != null && jwtUtil.validateToken(token)) {
            logger.info("Token is valid, proceeding with the request.");
            chain.doFilter(request, response);
        } else {
            logger.info("Token is invalid or not present, returning unauthorized status.");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    public void destroy() {
        logger.info("Destroying JwtWaitingAuthFilter");
    }
}
