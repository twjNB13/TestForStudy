package me.hmhb.mapper;

import me.hmhb.entity.dto.User;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface TestMapper extends BaseMapper<User> {
}
