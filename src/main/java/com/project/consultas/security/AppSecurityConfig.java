package com.project.consultas.security;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/users/login", "/users/addStudent", "/subjects/images/{\\d+}", "/users/images/profileImages/{\\d+}").permitAll()
            .antMatchers("/users/logout", "users/getUser", "users/images/upload", "users/getAllProfessors", "/subjects/findAll").hasAnyRole("STUDENT", "PROFESSOR", "ADMIN")
            .antMatchers("/users/modify", "/clases/findAll/{\\d+}", "/subjects/findClasses/{\\d+}", "/findProfessorClasses", "/users/images/upload").hasAnyRole("STUDENT", "PROFESSOR")
            .antMatchers("/clases/subscribe", "/clases/unsubscribe", "/subjects/followSubject", "/subjects/unfollowSubject").hasAnyRole("STUDENT")
            .antMatchers("/clases/add", "/clases/cancelClass", "/clases/startClass/{[\\d\\w]+}", "/clases/addComment").hasRole("PROFESSOR")
            .antMatchers("/subjects/add", "/subjects/modify", "/subjects/modifySubjectInfo/{\\d+}", "/subjects/deleteSubject").hasRole("ADMIN")
            .and()
            .formLogin()
            .defaultSuccessUrl("/subjects/findAll")
            .and()
            .csrf().disable()
            .httpBasic()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}