package com.msl;

import com.msl.utils.LdapUtils;
import com.msl.utils.Person;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;

/**
 * 启动类
 *
 * @author Run Jiao
 * @date 2023-04-21 15:55
 **/
@SpringBootApplication
public class LdapApplication {

    public static void main(String[] args) throws NamingException, IOException {
        SpringApplication.run(LdapApplication.class, args);
        List<Person> personList = LdapUtils.loadUsers();
        System.out.println(personList);
    }
}
