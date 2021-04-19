package org.example.logs;

import java.lang.annotation.*;

/**
 * 打印参数注解，如果添加在类上面，则该类中的所有方法都执行
 *
 * @author 李昆城
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ShowParam {

    /**
     * 显示参数
     *
     * @return true：打印参数 false：不打印参数
     */
    boolean param() default true;

    /**
     * 打印返回值
     *
     * @return true：打印返回值 false：不打印返回值
     */
    boolean result() default true;

    /**
     * 是否需要持久化到硬盘，默认为false
     *
     * @return true：需要持久化 false：不持久化
     */
    boolean dataPersistence() default false;

    /**
     * 持久化的地址
     *
     * @return 默认没有地址
     */
    String path() default "";

    /**
     * 是否需要在打印方法执行时间
     *
     * @return true：开始打印时间 false：关闭打印执行时间
     */
    boolean timestamp() default true;

}
