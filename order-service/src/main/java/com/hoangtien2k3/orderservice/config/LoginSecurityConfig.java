package com.hoangtien2k3.orderservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// config authorize in USER and ADMIN (test)
@EnableWebSecurity
@Configuration
public class LoginSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        authenticationMgr.inMemoryAuthentication()
                .withUser("hoangtien2k3").password("123456").authorities("USER")
                .and()
                .withUser("tien12345").password("123456").authorities("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/api/orders").access("hasAuthority('USER') or hasAuthority('ADMIN')")
                .antMatchers("/api/carts").access("hasAuthority('USER')")
                .antMatchers("/api/orders").access("hasAuthority('ADMIN')")
                .and()
                .formLogin().loginPage("/api/carts")
                .defaultSuccessUrl("/api/orders")
                .failureUrl("/api/orders?error")
                .usernameParameter("username").passwordParameter("password")
                .and()
                .logout().logoutSuccessUrl("/api/orders?logout");

    }

}
