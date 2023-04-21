package com.msl.utils;

import lombok.Data;

import javax.management.relation.Role;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户信息 返回实体
 *
 * @author Run Jiao
 * @date 2023-04-21 14:02
 **/
@Data
public class Person {

    /**
     * 用户名
     */
    private String givenName;

    /**
     * 邮箱
     */
    private String mail;
}
