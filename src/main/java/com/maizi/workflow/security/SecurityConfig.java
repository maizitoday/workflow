/*
 * @Descripttion: 
 * @version: v1.0
 * @Author: yubo
 * @Date: 2022-03-13 13:04:14
 * @LastEditors: yubo
 * @LastEditTime: 2022-03-13 16:31:31
 */
package com.maizi.workflow.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    private Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * @Description: 方法说明....
     * @Date: 2022-03-13 13:05:00
     * @param {*}
     * @return {*}
     * @LastEditors: Do not edit
     */
    @Bean
    public UserDetailsService myUserDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        String[][] usersGroupsAndRoles = { { "jack", "123456", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam" },
                { "rose", "123456", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam" },
                { "tom", "123456", "ROLE_ACTIVITI_USER", "GROUP_activitiTeam" },
                { "other", "123456", "ROLE_ACTIVITI_USER", "GROUP_otherTeam" },
                { "system", "123456", "ROLE_ACTIVITI_USER" },
                { "admin", "123456", "ROLE_ACTIVITI_ADMIN" } };
        for (String[] user : usersGroupsAndRoles) {
            List<String> authoritiesStrings = Arrays.asList(Arrays.copyOfRange(user, 2, user.length));
            logger.info("> Registering new user: " + user[0] + " with the following Authorities[" + authoritiesStrings
                    + "]");
            inMemoryUserDetailsManager.createUser(new User(user[0], passwordEncoder().encode(user[1]),
                    authoritiesStrings.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList())));
        }
        return inMemoryUserDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
