package com.aplaz.oauthserver.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String email;

    @Column(length = 60)    //until 60 characters
    private String password;
    private String role;
    private boolean enabled = false;
}
