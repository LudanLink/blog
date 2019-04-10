package link.xuchen.testlog.annotation;

import java.lang.annotation.*;

/**
 * 自定义日志注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyLog {

    /**
     * 打印日志类型，{@link LogTypeEnum}
     *
     * @return
     */
    LogTypeEnum value() default LogTypeEnum.ALL;

    /**
     * 日志输出前缀（建议配置接口名称）
     *
     * @return
     */
    String prefix() default "";

}
