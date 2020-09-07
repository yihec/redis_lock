package com.redis.lock.dao;


import com.redis.lock.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserDao {
    @Update("UPDATE  user SET age = #{age} where id = #{id}")
    int updatetData(User demo);

    @Select("select * from user where id = #{id}")
    User queryDataById(int name);
}
