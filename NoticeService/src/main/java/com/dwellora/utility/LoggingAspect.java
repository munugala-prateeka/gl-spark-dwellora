package com.dwellora.utility;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.dwellora.service.impl.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Executing Method : {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.dwellora.service.impl.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Completed Method : {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "execution(* com.dwellora.service.impl.*.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Exception error) {
        logger.error("Exception in {} : {}", joinPoint.getSignature().getName(), error.getMessage());
    }
}