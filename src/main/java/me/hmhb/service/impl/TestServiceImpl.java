package me.hmhb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.hmhb.entity.dto.User;
import me.hmhb.mapper.TestMapper;
import me.hmhb.service.TestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, User> implements TestService {

    @Override
    public List<User> getUsers() {
        return baseMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUser(User user) {
        throw new RuntimeException("insert user error");
    }
}
