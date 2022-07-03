package com.aplaz.client.events;

import com.aplaz.client.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationConfirmation extends ApplicationEvent {

    private User user;
    private String applicationURL;

    public RegistrationConfirmation(User user,String applicationURL){
        super(user);
        this.user = user;
        this.applicationURL = applicationURL;
    }

}
