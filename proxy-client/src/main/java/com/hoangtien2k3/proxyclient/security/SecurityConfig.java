package com.hoangtien2k3.proxyclient.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hoangtien2k3.proxyclient.business.user.model.RoleBasedAuthority;
import com.hoangtien2k3.proxyclient.config.filter.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService)
                .passwordEncoder(this.passwordEncoder);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/", "index", "**/css/**", "**/js/**").permitAll()
                .antMatchers("/api/authenticate/**").permitAll()
                .antMatchers("/api/categories/**").permitAll()
                .antMatchers("/api/products/**").permitAll()
                .antMatchers("/api/**")
                .hasAnyRole(RoleBasedAuthority.ROLE_USER.getRole(),
                        RoleBasedAuthority.ROLE_ADMIN.getRole())
                .antMatchers("/actuator/health/**", "/actuator/info/**")
                .permitAll()
                .antMatchers("/actuator/**")
                .hasAnyRole(RoleBasedAuthority.ROLE_ADMIN.getRole())
                .anyRequest().authenticated()
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(this.jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}