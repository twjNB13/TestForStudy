package me.hmhb.controller;

import jakarta.annotation.Resource;
import me.hmhb.entity.dto.User;
import me.hmhb.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/getUsers")
    public List<User> getUsers() {
        return testService.getUsers();
    }
}
