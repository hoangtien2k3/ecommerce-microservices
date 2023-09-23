package com.hoangtien2k3.userservice.config;

import com.hoangtien2k3.userservice.security.jwt.JwtEntryPoint;
import com.hoangtien2k3.userservice.security.jwt.JwtTokenFilter;
import com.hoangtien2k3.userservice.security.userprinciple.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private JwtEntryPoint jwtEntryPoint;

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder());

    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors() // Cross-Origin Resource Sharing (CORS): cho phép các yêu cầu từ các nguồn khác nhau được gửi đến ứng dụng web
                .and()
                .csrf().disable()  // Vô hiệu hóa CSRF (Cross-Site Request Forgery)
                .authorizeRequests().antMatchers("/api/auth/**").permitAll() // Cho phép tất cả các yêu cầu tới /api/auth/** được truy cập mà không cần xác thực.
                .anyRequest().authenticated()   // Yêu cầu tất cả các yêu cầu khác phải được xác thực.
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtEntryPoint) // Xử lý các ngoại lệ liên quan đến xác thực và ủy quyền.
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Thiết lập chính sách quản lý phiên không trạng thái.

        httpSecurity.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // để xác thực và ủy quyền yêu cầu sử dụng JWT (JSON Web Token).

    }

}
