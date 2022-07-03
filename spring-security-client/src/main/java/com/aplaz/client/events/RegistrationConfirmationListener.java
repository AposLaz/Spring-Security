package com.aplaz.client.events;

import com.aplaz.client.entity.User;
import com.aplaz.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationConfirmationListener implements ApplicationListener<RegistrationConfirmation> {

    @Autowired
    private UserService service;

    @Override
    public void onApplicationEvent(RegistrationConfirmation event) {
        //Create the Verification Token for the User with Link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.saveVerificationTokenForUser(token,user);

        //Send Email to User
        String url = event.getApplicationURL() + "/verifyRegistration?token=" +token ;

        //SendVerificationEmail()
        log.info("Click here to verify your account: {}",url);
    }
}
