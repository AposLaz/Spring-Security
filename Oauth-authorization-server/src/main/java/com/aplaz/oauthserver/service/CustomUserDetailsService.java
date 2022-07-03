package com.aplaz.oauthserver.service;

import com.aplaz.oauthserver.entity.User;
import com.aplaz.oauthserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/********************   @Transactional  *********************************
 *
 * @Transactional , Spring dynamically creates a proxy that
 * implements the same interface(s) as the class you're annotating.
 * And when clients make calls into your object, the calls are
 * intercepted and the behaviors injected via the proxy mechanism
 *
 */

/**
 *  Now we have defined the custom user detail service.
 *  THIS IS THE SERVICE FOR VALIDATE THE USER
 *  we define the authorization server config like how the authorization server should work
 */

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    /******************************* loadUserByUsername
     *
     *  This dependency is so usefull because with it we know about the users that have in the database.
     *  The UserDetailsService interface is used to retrieve user-related data. It has one method named
     *  loadUserByUsername() which can be overridden to customize the process of finding the user.
     *  It is used by the DaoAuthenticationProvider to load details about the user during authentication.
     *
     */

    @Autowired
    private UserRepository repo;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // first find user in db by email
         User user = repo.findByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException("Not User Found");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                getAuthorities(List.of(user.getRole()))
        );
    }

    /**********         Collection<? extends T>
     *  With the Collection<? extends GrantedAuthority>
     *  extends you can allow any subclasses of GrantedAuthority
     *
     */
    private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role: roles){
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }


}
