package com.aplaz.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/***********************    @EnableWebSecurity    ********************************************
 *
 *  The @EnableWebSecurity is a marker annotation. It allows Spring to find
 *  (it's a @Configuration and, therefore, @Component) and automatically apply
 *  the class to the global WebSecurity.
 *
 */

@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] WHITE_LIST_URLS = {
            "/test",
            "/register",
            "/resetPassword",
            "/savePassword",
            "/verifyRegistration",
            "/resendVerifyToken"
    };

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .antMatchers(WHITE_LIST_URLS).permitAll()  //other URLS have to authenticate
                .antMatchers("/api/**").authenticated()   //These URLS must authenticated
                .and()
                //This is the login Page that should redirect
                .oauth2Login(oauth2login ->
                        oauth2login.loginPage("/oauth2/authorization/api-client-oidc"))
                .oauth2Client(Customizer.withDefaults()); //default configuration

        return http.build();
    }

    public static class WebClientConfiguration {
    }
}
