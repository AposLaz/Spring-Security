package com.aplaz.oauthserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *  Authentication Provider
 *  How you should be managing your authentication
 */

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

    /****** Info
     *
     *  We need custom user details service
     *
     */

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     *
     * This is the authentication method in which we have to customize
     */

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        //first get username or email and password from login request
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        //load from authorization server the user with this email
        UserDetails user = customUserDetailsService.loadUserByUsername(username);

        //check if password from authorization server and login is the same and
        // if then return an authentication token
        //Token will Store Email, Password, Authorities (Email = Username_
        return checkPassword(user, password);

    }

    private Authentication checkPassword(UserDetails user, String rawPassword) {
        if(passwordEncoder.matches(rawPassword, user.getPassword())){
            return new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities());
        }
        else {
            throw new BadCredentialsException("Bad Credentials");
        }
    }

    /** The Authentication has benn handled over here

     */

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
