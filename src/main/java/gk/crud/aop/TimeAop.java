package gk.crud.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimeAop {

    @Around("execution(* gk.crud.controller..*(..))")
    public Object timeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.info("method={}, start", joinPoint.getSignature());
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long mlis = endTime - startTime;
        log.info("method={}, 걸린 시간={}", joinPoint.getSignature(), mlis);
        return result;
    }



}
