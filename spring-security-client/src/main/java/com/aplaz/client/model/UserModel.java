package com.aplaz.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/********************* NoArgsConstructor && AllArgsConstructor ***************************
 *
 *  1)  No args Constructor Generates no arguments constructor for us.
 *      Above @NoArgsAnnotation will generate constructor
 *
 *              public UserModel(){}
 *
 *  2) All args Constructor is the opposite of No args constructor
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @NotNull(message = "You must give firstname")
    private String firstname;

    @NotNull(message = "You must give lastname")
    private String lastname;

    @NotNull(message = "You must give email")
    @Email(message = "Invalid Email address", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    private String email;

    @NotNull(message = "You must give password")
    private String password;

    @NotNull(message = "You must confirm your password")
    private String confirm_password;

    public int validate_passwords(UserModel usermodel){
        if(!(usermodel.getPassword()).equals(usermodel.getConfirm_password())){
            return 0;
        }
        return 1;
    }
}
