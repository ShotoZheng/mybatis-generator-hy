package com.example.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private String userName;

    /**
     * 
     */
    private Integer age;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;
}