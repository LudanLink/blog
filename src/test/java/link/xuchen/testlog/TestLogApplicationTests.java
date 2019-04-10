package link.xuchen.testlog;

import link.xuchen.testlog.annotation.LogTypeEnum;
import link.xuchen.testlog.annotation.MyLog;
import link.xuchen.testlog.service.LogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLogApplicationTests {

    @Autowired
    private LogService logService;

    @Test
    public void contextLoads() {
        logService.log();
    }



}
