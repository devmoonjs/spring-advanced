package org.example.expert.domain.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Slf4j
@Aspect
public class AdminLogAop {

    private final HttpServletRequest servletRequest;

    public AdminLogAop(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    @Pointcut("execution(* org.example.expert.domain.comment.service.CommentAdminService.deleteComment(..)) ||" +
              "execution(* org.example.expert.domain.user.service.UserAdminService.changeUserRole(..))")
    private void serviceLayer() {
    }

    @Around("serviceLayer()")
    public void aroundAdminLog(ProceedingJoinPoint joinPoint) throws Throwable {

        Long userId = (Long) servletRequest.getAttribute("userId");
        LocalDateTime accessTime = LocalDateTime.now();
        String requestUrl = servletRequest.getRequestURI();

        log.info("\n::: User ID : {} :::\n::: Access Time : {}:::\n::: URL : {} :::", userId, accessTime, requestUrl);
        joinPoint.proceed();
    }
}
