package com.example.entity;

import com.ty.criteria.BaseExample;

import java.util.ArrayList;

public class UserExample extends BaseExample {

    public static final String ID = "id";

    public static final String USER_NAME = "userName";

    public static final String AGE = "age";

    public static final String CREATE_TIME = "createTime";

    public static final String UPDATE_TIME = "updateTime";

    public UserExample() {
        oredCriteria = new ArrayList<>();
    }

}