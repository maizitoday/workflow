/*
 * @Descripttion: 
 * @version: v1.0
 * @Author: yubo
 * @Date: 2022-03-13 12:53:04
 * @LastEditors: yubo
 * @LastEditTime: 2022-03-13 16:48:19
 */
package com.maizi.workflow.security;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityUtil {

    private Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    @Autowired
    @Qualifier("myUserDetailsService")
    private UserDetailsService userDetailsService;

    public void logInAs(String username) {

        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (user == null) {
            throw new IllegalStateException("User " + username + " doesn't exist, please provide a valid user");
        }
        logger.info("> Logged in as: " + username);

        Authentication authentication = new Authentication() {

            @Override
            public String getName() {

                return user.getUsername();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {

                return user.getAuthorities();
            }

            @Override
            public Object getCredentials() {

                return user.getPassword();
            }

            @Override
            public Object getDetails() {

                return user;
            }

            @Override
            public Object getPrincipal() {

                return user;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

        };
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        org.activiti.engine.impl.identity.Authentication.setAuthenticatedUserId(username);
    }

}
