//package com.hoangtien2k3.orderservice.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        super.configure(web);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors() // Cross-Origin Resource Sharing (CORS)
//                .and()
//                .csrf().disable()  // CSRF (Cross-Site Request Forgery)
//                .authorizeRequests()
//                .antMatchers("/api/orders").hasAuthority("USER")
//                .antMatchers("/api/carts").hasAnyAuthority("ADMIN", "USER")
//                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling();
//    }
//}

