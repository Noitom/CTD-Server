package com.snf.dsds.common.aop;

import com.snf.dsds.bean.LogBean;
import com.snf.dsds.dao.LogDao;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


/**
 * @program: dsds
 * @description:
 * @author: zhouyuj
 * @create: 2021-12-29 17:07
 **/
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    LogDao logDao;

    @Pointcut("execution(public * com.snf.dsds.controller.*.*(..))")
    public void webLog(){}

    @Before("webLog()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        log.debug("URL : " + request.getRequestURL().toString());
        log.debug("HTTP_METHOD : " + request.getMethod());
        log.debug("IP : " + request.getRemoteAddr());
        log.debug("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.debug("ARGS : " + Arrays.toString(joinPoint.getArgs()));

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        log.debug("方法的返回值 : " + ret);
    }

    //后置异常通知
    @AfterThrowing("webLog()")
    public void throwss(JoinPoint jp){
        log.debug("方法异常时执行.....");

    }

    //后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
    @After("webLog()")
    public void after(JoinPoint jp){
        log.debug("方法最后执行.....");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LogBean logBean = new LogBean();
        logBean.setUsername(authentication.getName());
        logBean.setRequestUrl(request.getRequestURL().toString());
        logBean.setRequestParam(Arrays.toString(jp.getArgs()).replaceAll("password=[^null].*?,","password=*********,"));
        logDao.addLog(logBean);
    }

    //环绕通知,环绕增强，相当于MethodInterceptor
    @Around("webLog()")
    public Object arround(ProceedingJoinPoint pjp) {
        log.debug("方法环绕start.....");
        try {
            Object o =  pjp.proceed();
            log.debug("方法环绕proceed，结果是 :" + o);
            return o;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

}
