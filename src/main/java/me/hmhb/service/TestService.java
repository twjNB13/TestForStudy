package me.hmhb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.hmhb.entity.dto.User;

import java.util.List;
import java.util.Map;

public interface TestService extends IService<User> {
    List<User> getUsers();
}
