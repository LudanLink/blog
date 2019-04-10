package link.xuchen.testlog.aspect;

import com.alibaba.fastjson.JSON;
import link.xuchen.testlog.annotation.LogMask;
import link.xuchen.testlog.annotation.LogTypeEnum;
import link.xuchen.testlog.annotation.MdcConstant;
import link.xuchen.testlog.annotation.MyLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Create with IDEA
 * Ludan
 * Data:2019/4/9 3:22 PM
 * Description: 日志切面
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 切入点
     */
    @Pointcut("@annotation(link.xuchen.testlog.annotation.MyLog)")
    public void pointcut() {
    }

    /**
     * 环绕通知
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("pointcut()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = this.getSpecificmethod(joinPoint);

        // 获取sessionId
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String sessionId = requestAttributes.getSessionId();
        String requestId = (String)requestAttributes.getAttribute("requestId", 0);
        MyLog myLog = this.getMethodAnnotations(method, MyLog.class);
        if (Objects.isNull(myLog)) {
            myLog = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), MyLog.class);
        }

        boolean isSuccess = setMdc(sessionId, requestId);
        try {
            // 获取日志输出前缀
            String prefix = getPrefix(myLog, method);
            // 执行方法前输出日志
            logBefore(myLog, prefix, method, joinPoint.getArgs());
            // 执行方法，并获取返回值
            Object result = joinPoint.proceed();
            // 执行方法后输出日志
            logAfter(myLog, prefix, result);
            return null;
        } catch (Throwable t) {
            throw t;
        } finally {
            try {
                if (isSuccess) {
                    MDC.remove(MdcConstant.SESSION_KEY);
                    MDC.remove(MdcConstant.REQUEST_KEY);
                }
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * 为每个请求设置唯一标示到MDC容器中
     *
     * @return
     */
    private boolean setMdc(String sessionId, String requestId) {
        try {
            // 设置SessionId
            if (StringUtils.isEmpty(MDC.get(MdcConstant.SESSION_KEY))) {
                MDC.put(MdcConstant.SESSION_KEY, sessionId);
                MDC.put(MdcConstant.REQUEST_KEY, requestId);
                return true;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 输出方法执行结果
     *
     * @param myLog    log注解
     * @param prefix 输出前缀
     * @param result 方法执行结果
     */
    private void logAfter(MyLog myLog, String prefix, Object result) {
        // 判断是否是方法之前输出日志，不是就输出参数日志
        if (!LogTypeEnum.PARAMETER.equals(myLog.value())) {
            log.info("【返回参数 {}】：{}", prefix, JSON.toJSON(result));
        }
    }

    /**
     * 输出方法调用参数
     *
     * @param myLog    log注解
     * @param prefix 输出日志前缀
     * @param method 代理方法
     * @param args   方法参数
     */
    private void logBefore(MyLog myLog, String prefix, Method method, Object[] args) {
        // 判断是否是方法之后输出日志，不是就输出参数日志
        if (!LogTypeEnum.RESULT.equals(myLog.value())) {
            Map<String, Object> paramMap = new LinkedHashMap<>();
            // 获取参数注解
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] parameterAnnotation = parameterAnnotations[i];
                // 找到参数上面的注解，并根据注解获取脱敏的类型
                LogMask logMask = getLogMask(parameterAnnotation);
                String paramName = "args" + (i + 1);
                if (logMask != null) {
                    paramName = StringUtils.isEmpty(logMask.paramName()) ? paramName : logMask.paramName();
                }

                // 忽略这些类型参数的输出
                if (args[i] instanceof ServletResponse || args[i] instanceof ServletRequest
                        || args[i] instanceof HttpSession || args[i] instanceof Model) {

                    break;
                }

                paramMap.put(paramName, args[i]);
            }
            log.info("【请求参数 {}】：{}", prefix, JSON.toJSON(paramMap));
        }
    }

    /**
     * 获取日志前缀对象
     *
     * @param myLog       日志注解对象
     * @param method    注解日志的方法对象
     * @return
     */
    private String getPrefix(MyLog myLog, Method method) {
        // 日志格式：流水号 + 注解的日志前缀 + 方法的全类名
        StringBuilder sb = new StringBuilder();
        sb.append(myLog.prefix());
        sb.append(":");
        sb.append(method.getDeclaringClass().getName());
        sb.append(".");
        sb.append(method.getName());
        sb.append("() ");

        return sb.toString();
    }

    /**
     * 获取参数上的LogMask注解
     *
     * @param parameterAnnotation
     * @return
     */
    private LogMask getLogMask(Annotation[] parameterAnnotation) {
        for (Annotation annotation : parameterAnnotation) {
            // 检查参数是否需要脱敏
            if (annotation instanceof LogMask) {
                return (LogMask) annotation;
            }
        }
        return null;
    }

    private <T extends Annotation> T getMethodAnnotations(AnnotatedElement ae, Class<T> annotationType) {
        // look for raw annotation
        T ann = ae.getAnnotation(annotationType);
        return ann;
    }

    private Method getSpecificmethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        // The method may be on an interface, but we need attributes from the
        // target class. If the target class is null, the method will be
        // unchanged.
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(pjp.getTarget());
        if (targetClass == null && pjp.getTarget() != null) {
            targetClass = pjp.getTarget().getClass();
        }
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the
        // original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        return specificMethod;
    }
}