package me.hmhb;

import com.demo.MyService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TestApplicationTest {

    @Resource
    private MyService myService;

    @Test
    public void contextLoads() {
        myService.sayHello();
    }

}
