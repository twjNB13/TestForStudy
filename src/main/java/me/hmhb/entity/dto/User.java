package me.hmhb.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("db_user")
public class User {
    @TableId
    Integer id;
    String name;
    int age;
    //String desc;
}
