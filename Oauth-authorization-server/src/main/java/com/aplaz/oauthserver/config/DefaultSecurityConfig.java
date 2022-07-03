package com.aplaz.oauthserver.config;

import com.aplaz.oauthserver.service.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 *  Define how your basic security should work like all the requests should be validated
 */

@EnableWebSecurity
public class DefaultSecurityConfig {

    /** Bind Authentication Provider
     *
     */

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    /** ALL REQUESTS THAT COME HERE SHOULD BE AUTHENTICATED
     *
     *
     */

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests(authorizeRequests ->
                authorizeRequests.anyRequest().authenticated() //   any request has to be authenticated
        ).formLogin(Customizer.withDefaults());                           //   with default form login for the
                                                               //   authorization server

        return http.build();
    }

    /**
     *  Bind our CustomAuthenticationProvider to AuthenticationManagerBuilder
     */

    public void bindAuthenticationProvider(AuthenticationManagerBuilder authenticationManagerBuilder){
        authenticationManagerBuilder
                .authenticationProvider(customAuthenticationProvider);
    }
}
