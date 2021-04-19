package org.example.logs;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 参数打印增强aop
 *
 * @author 李昆城
 */
@Slf4j
@Aspect
@Component
public class ShowParamEnhance {

    /**
     * 切入点加上该注解的类和所有加上该注解的方法
     *  &&@annotation(org.example.logs.ShowParam) or
     */
    @Pointcut("@annotation(org.example.logs.ShowParam) || @within(org.example.logs.ShowParam)")
    public void section() {
    }

    /**
     * 环绕增强
     *
     * @param point 方法执行的代理对象
     * @return 执行结果
     */
    @Around("section()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object target = point.getTarget();
        Class<?> targetClass = target.getClass();
        //获取到方法签名对象
        MethodSignature signature = (MethodSignature) point.getSignature();
        //执行的方法名称
        String methodName = signature.getName();
        //该方法的所有参数列表
        Class<?>[] parameterTypes = signature.getParameterTypes();
        //如果该方法上面没注解，则在类上面获取注解，方法上面的注解优先局大于类上面的注解
        ShowParam annotation = findAnnotation(targetClass, methodName, parameterTypes);
        //执行方法的完整方法地址
        String fullMethodPath = targetClass.getName() + "." + methodName;
        Object[] args = point.getArgs();
        log.info("{} 方法开始执行...", fullMethodPath);
        long startTime = System.currentTimeMillis();
        boolean param = annotation.param();
        //打印参数
        if (param) {
            showParam(signature, args);
        }
        //执行该方法
        Object proceed = point.proceed();
        boolean result = annotation.result();
        //打印执行结果
        if (result) {
            log.info("执行结果：{}", JSON.toJSONString(proceed));
        }
        log.info("{} 方法执行结束...", fullMethodPath);
        long endTime = System.currentTimeMillis();
        //打印执行时间
        showTime(annotation.timestamp(), startTime, endTime);
        return proceed;
    }

    /**
     * 获取类上面的注解
     *
     * @return 注解对象`
     */
    private ShowParam findAnnotationByClass(Class<?> targetClass) {
        //获取类上面的注解
        return targetClass.getAnnotation(ShowParam.class);
    }

    /**
     * 获取到方法上面的注解
     *
     * @param targetClass    被代理的目标对象
     * @param methodName     执行的方法名称
     * @param parameterTypes 执行的参数列表
     * @return 方法上面的注解
     * @throws NoSuchMethodException 没有获取到该方法
     */
    private ShowParam findAnnotationByMethod(Class<?> targetClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = Objects.nonNull(parameterTypes) && parameterTypes.length >= 1 ? targetClass.getMethod(methodName, parameterTypes) :
                targetClass.getMethod(methodName);
        return method.getAnnotation(ShowParam.class);
    }

    /**
     * 获取需要执行的注解
     *
     * @param targetClass    目标代理对象class
     * @param methodName     需要执行的方法名称
     * @param parameterTypes 需要执行的方法参数类型列表
     * @return 需要执行的注解
     * @throws NoSuchMethodException 没找到方法对象
     */
    private ShowParam findAnnotation(Class<?> targetClass, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        //查询方法上面的注解
        ShowParam annotation = findAnnotationByMethod(targetClass, methodName, parameterTypes);
        //如果该方法上面没注解，则在类上面获取注解，方法上面的注解优先局大于类上面的注解
        return Objects.isNull(annotation) ? findAnnotationByClass(targetClass) : annotation;
    }

    /**
     * 打印参数
     *
     * @param signature 代理方法
     * @param args      参数列表
     */
    private void showParam(MethodSignature signature, Object[] args) {
        if (Objects.isNull(args) || args.length <= 0) {
            log.info("暂无参数");
            return;
        }
        String[] parameterNames = signature.getParameterNames();
        Map<String, Object> params = new HashMap<>(args.length);
        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            String parameterName = parameterNames[i];
            params.put(parameterName, value);
        }
        String paramsJson = JSON.toJSONString(params);
        log.info("params：{}", paramsJson);

    }

    /**
     * 打印执行时间
     *
     * @param timestamp true：需要打印
     * @param start     开始时间
     * @param end       截止时间
     */
    private void showTime(boolean timestamp, long start, long end) {
        if (timestamp) {
            log.info("方法执行用时（毫秒数）：{}", end - start);
        }
    }


}
