package link.xuchen.testlog.controller;

import link.xuchen.testlog.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create with IDEA
 * Ludan
 * Data:2019/4/9 5:09 PM
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private LogService logService;

    @RequestMapping("/log")
    public String testLog() {
        logService.log();
        return "ok";
    }

    @RequestMapping("/log2")
    public String testLog2(String name) {
        logService.log2(name);
        return "ok";
    }

    @RequestMapping("/log3")
    public String testLog3(String name, int age) {
        logService.log3(name, age);
        return "ok";
    }

    @RequestMapping("/log4")
    public String testLog4() {
        logService.log4();
        return "ok";
    }

}
