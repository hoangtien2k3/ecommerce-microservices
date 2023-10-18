//package com.hoangtien2k3.orderservice.config.security;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/api/orders/**").hasAuthority("ADMIN") // Chi phép truy cập dựa trên quyền "ADMIN"
//                .antMatchers("/api/carts/**").authenticated() // Yêu cầu xác thực cho tất cả người dùng
//                .and()
//                .httpBasic()
//                .and()
//                .csrf().disable();
//    }
//
//}
