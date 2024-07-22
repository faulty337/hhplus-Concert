package com.hhp.concert;

import com.hhp.concert.util.exception.CustomException;
import com.hhp.concert.util.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {
    private static final Logger logger = LogManager.getLogger(ApiControllerAdvice.class);

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResponse> ExceptionHandler(CustomException e, HttpServletRequest request) {
        logger.error("Request URL: {}, ExceptionMessage: {}, StatusCode: {}", request.getRequestURL().toString(), e.getMessage(), e.getStatusCode());
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponse(e.getStatusCode(), e.getMsg()));
    }
}
