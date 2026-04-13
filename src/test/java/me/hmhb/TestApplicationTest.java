package me.hmhb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TestApplicationTest {

    @Test
    public void contextLoads() {
        System.out.println(UUID.randomUUID());
    }

}
