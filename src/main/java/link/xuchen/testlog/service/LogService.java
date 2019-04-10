package link.xuchen.testlog.service;

import link.xuchen.testlog.annotation.LogMask;
import link.xuchen.testlog.annotation.LogTypeEnum;
import link.xuchen.testlog.annotation.MyLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Create with IDEA
 * Ludan
 * Data:2019/4/8 5:19 PM
 * Description: 日志测试
 */
@Service
@Slf4j
public class LogService {

    /**
     * 不同的日志级别打印优先级
     */
    @MyLog(value = LogTypeEnum.RESULT)
    public void log() {
        log.debug(" debug ==============================================");
        log.info(" info ==============================================");
        log.warn(" warn ==============================================");
    }

    /**
     * 不同的日志级别打印优先级
     */
    @MyLog(value = LogTypeEnum.ALL)
    public void log2(@LogMask(paramName = "name") String name) {
        log.debug(" debug ==============================================" + name);
        log.info(" info ==============================================" + name);
        log.warn(" warn ==============================================" + name);
    }

    /**
     * 不同的日志级别打印优先级
     */
    public void log3(@LogMask(paramName = "name") String name, int age) {
        log.debug(" debug ==============================================" + name + age);
        log.info(" info ==============================================" + name + age);
        log.warn(" warn ==============================================" + name + age);
    }

    /**
     * 不同的日志级别打印优先级
     */
    public void log4() {
        log.debug(" debug ==============================================");
        log.info(" info ==============================================");
        log.warn(" warn ==============================================");
    }

}
