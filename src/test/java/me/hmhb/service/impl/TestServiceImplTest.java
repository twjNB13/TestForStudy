package me.hmhb.service.impl;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestServiceImplTest {

    @Resource
    private TestServiceImpl testService;

    @Test
    void getUsers() throws InterruptedException {
        new Thread(() -> assertEquals(1000, testService.getUsers().size())).start();
        assertEquals(1000, testService.getUsers().size());
        Thread.sleep(1000);
    }
}