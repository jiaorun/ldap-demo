package com.msl.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.io.IOException;
import java.util.*;

/**
 * LDAP 工具类
 *
 * @author Run Jiao
 * @date 2023-04-21 14:12
 **/
@Slf4j
@Component
public class LdapUtils {

    private static String LDAP_URLS = "ldap://10.8.5.155:389/";

    private static String LDAP_BASE = "dc=maslong,dc=com";

    private static String LDAP_USERNAME = "uid=xiaozz,ou=users";

    private static String LDAP_PASSWORD = "123456";

    /**
     * 捞出所有的用户
     *
     * @param
     * @return List<Person>
     * @author Run Jiao
     * @date 2023/4/21 14:20
     */ 
    public static List<Person> loadUsers() throws NamingException, IOException {
        LdapContext ctx = initialLdapContext();
        ctx.setRequestControls(new Control[]{new PagedResultsControl(1000, Control.NONCRITICAL)});

        SearchControls searchCtls = new SearchControls();
        searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        // 注释此行,返回全量属性
        searchCtls.setReturningAttributes(new String[]{"givenName", "mail"});
        List<Person> allUsers = new ArrayList<>();
        byte[] cookie;
        do {
            NamingEnumeration<SearchResult> answer = ctx.search(
                    "ou=users,dc=maslong,dc=com",
                    "(&(objectClass=posixAccount))",
                    searchCtls);
            while (answer != null && answer.hasMoreElements()) {
                Attributes ats = answer.next().getAttributes();
                Person person = new Person();
                getAttribute(ats, "givenName").ifPresent(person::setGivenName);
                getAttribute(ats, "mail").ifPresent(person::setMail);
                allUsers.add(person);
            }

            cookie = Optional.ofNullable(ctx.getResponseControls())
                    .map(arr -> arr[0])
                    .map(PagedResultsResponseControl.class::cast)
                    .map(PagedResultsResponseControl::getCookie)
                    .orElse(null);
            ctx.setRequestControls(new Control[]{new PagedResultsControl(100, cookie, Control.CRITICAL)});
        } while (cookie != null && cookie.length != 0);
        ctx.close();
        return allUsers;
    }

    /**
     * 初始化LDAP上下文环境context
     *
     * @param
     * @return LdapContext
     * @author Run Jiao
     * @date 2023/4/21 14:14
     */
    private static LdapContext initialLdapContext() throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); // LDAP 工厂
        env.put(Context.SECURITY_AUTHENTICATION, "simple"); // LDAP访问安全级别
        env.put(Context.PROVIDER_URL, LDAP_URLS);
        env.put(Context.SECURITY_PRINCIPAL, LDAP_USERNAME + "," + LDAP_BASE); //  填DN
        env.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD); // AD Password
        env.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
        return new InitialLdapContext(env, null);
    }

    /**
     * 属性赋值
     *
     * @param ats 属性集合
     * @param name key
     * @return Optional<String>
     * @author Run Jiao
     * @date 2023/4/21 15:13
     */ 
    private static Optional<String> getAttribute(Attributes ats, String name) {
        return Optional.ofNullable(ats).map(attributes -> attributes.get(name)).map(attribute -> {
            try {
                return attribute.get();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }).map(Object::toString);
    }

    public static void main(String[] args) throws NamingException, IOException {
        List<Person> personList = loadUsers();
        System.out.println(personList);
    }
}
