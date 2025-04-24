package com.mybatis;

import com.alibaba.fastjson.JSON;
import com.example.dao.UserMapper;
import com.example.entity.User;
import com.example.entity.UserExample;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class LimitTest extends MybatisBaseTest {

    @Test
    public void test() throws IOException {
        execute(this::testSelectSQL);
    }

    private void testSelectSQL(SqlSession session) {
        UserMapper userMapper = session.getMapper(UserMapper.class);
        testselectByExample(userMapper);
        testselectByExampleByPage(userMapper);
    }

    private void testselectByExample(UserMapper userMapper) {
        UserExample example = new UserExample();
        example.createCriteria().andPropertyEqualTo(UserExample.AGE,23);
        List<User> users = userMapper.selectByExample(example);
        System.out.println(JSON.toJSONString(users));
        System.out.println("=========================================>>>");
    }

    private void testselectByExampleByPage(UserMapper userMapper) {
        int pageSize = 2;
        UserExample example = new UserExample();
        example.createCriteria().andPropertyEqualTo(UserExample.AGE,23);
        long totalCount = userMapper.countByExample(example);
        // 分页数
        int pageCount = (int) Math.ceil(((double) totalCount / (double) pageSize));
        example.setOrderByClause("ID DESC");
        for (int i = 0; i < pageCount; i++) {
            example.page(i + 1, pageSize);
            List<User> users = userMapper.selectByExample(example);
            System.out.println(JSON.toJSONString(users));
        }
    }
}
