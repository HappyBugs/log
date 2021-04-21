package org.example.logs;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.logs.observice.LogEvent;
import org.example.logs.observice.LogEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

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
     * 线程名称后缀
     */
    private static int threadInitNumber;

    /**
     * 获取线程后缀名称
     *
     * @return 线程后缀
     */
    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    /**
     * 核心线程数量
     */
    int corePoolSize = 2;
    /**
     * 最大线程数量
     */
    int maximumPoolSize = 4;
    /**
     * 空闲存活时间
     */
    long keepAliveTime = 0;

    /**
     * 执行日志收集的线程池
     */
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
            keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
            r -> new Thread(new ThreadGroup("showParamThreadGroup"), r, "show_param:" + nextThreadNum()));

    private final LogEventPublisher logEventPublisher;

    @Autowired
    public ShowParamEnhance(LogEventPublisher logEventPublisher) {
        this.logEventPublisher = logEventPublisher;
    }

    /**
     * 切入点加上该注解的类和所有加上该注解的方法
     * &&@annotation(org.example.logs.ShowParam) or
     */
    @Pointcut("@annotation(org.example.logs.ShowParam) || @within(org.example.logs.ShowParam)")
    public void section() {
    }

    /**
     * 环绕增强，实现日志收集与持久化
     *
     * @param point 方法执行的代理对象
     * @return 执行结果
     */
    @Around("section()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        log.info("111");
        long startTime = System.currentTimeMillis();
        Throwable throwable = null;
        //目标方法执行结果
        Object proceed = null;
        try {
            proceed = point.proceed();
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            long endTime = System.currentTimeMillis();
            threadPoolExecutor.execute(enhance(point, proceed, throwable, startTime, endTime));
        }
        //新开一个线程执行收参数收集信息
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
     * @return 需要打印的参数信息
     */
    private String showParam(MethodSignature signature, Object[] args) {
        if (Objects.isNull(args) || args.length <= 0) {
            return "";
        }
        String[] parameterNames = signature.getParameterNames();
        Map<String, Object> params = new HashMap<>(args.length);
        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            String parameterName = parameterNames[i];
            params.put(parameterName, value);
        }
        return JSON.toJSONString(params);

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

    /**
     * 当发生错误的时候
     *
     * @param throwable 错误对象
     */
    private String errorLocation(Throwable throwable) {
        //错误追踪列表
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (Objects.nonNull(stackTrace) && stackTrace.length >= 1) {
            StackTraceElement stackTraceElement = stackTrace[0];
            //错误发生地质
            return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
        }
        return "";
    }


    /**
     * 开启另外一个线程进行方法日志收集
     *
     * @param point     代理对象
     * @param resultObj 返回值 可以为null
     * @param throwable 异常信息 可以为null
     * @param startTime 开始执行时间
     * @param endTime   截止执行时间
     */
    @Async
    public Runnable enhance(ProceedingJoinPoint point, Object resultObj, Throwable throwable, Long startTime, Long endTime) {
        return () -> {
            Object target = point.getTarget();
            Class<?> targetClass = target.getClass();
            //获取到方法签名对象
            MethodSignature signature = (MethodSignature) point.getSignature();
            //执行的方法名称
            String methodName = signature.getName();
            //该方法的所有参数列表
            Class<?>[] parameterTypes = signature.getParameterTypes();
            //如果该方法上面没注解，则在类上面获取注解，方法上面的注解优先局大于类上面的注解
            ShowParam annotation;
            try {
                annotation = findAnnotation(targetClass, methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                log.error("无法获取到目标方法");
                return;
            }
            //执行方法的完整方法地址
            String fullMethodPath = targetClass.getName() + "." + methodName;
            Object[] args = point.getArgs();
            //打印内容的头部和尾部
            log.info("{} 方法开始执行...", fullMethodPath);
            //参数信息
            String paramString = Objects.isNull(args) || args.length < 1 ? "" : showParam(signature, args);
            if (annotation.param()) {
                log.info("参数信息：{}", paramString);
            }
            String resultString = "";
            String errorMsg = "";
            String errorLocation = "";
            //如果执行方法发生了错误
            if (Objects.nonNull(throwable)) {
                //错误消息
                errorMsg = throwable.getMessage();
                //错误位置
                errorLocation = errorLocation(throwable);
                log.info("错误消息：{}", errorMsg);
                log.info("部分错误位置：{}", errorLocation);
            } else if (Objects.nonNull(resultObj)) {
                //返回值
                resultString = JSON.toJSONString(resultObj);
                //打印执行结果
                if (annotation.result()) {
                    log.info("执行结果：{}", resultString);
                }
            }
            log.info("{} 方法执行结束...", fullMethodPath);
            //打印执行时间
            showTime(annotation.timestamp(), startTime, endTime);
            //如果不需要持久化，则返回
            if (!annotation.dataPersistence()) {
                return;
            }
            //进行持久化操作
            LogEvent logEvent = new LogEvent(annotation).targetAddress(fullMethodPath).param(paramString)
                    .result(resultString).startTime(startTime).endTime(endTime).errorMsg(errorMsg).errorLocation(errorLocation);
            logEventPublisher.publishEvent(logEvent);
        };
    }
}
