package com.aplaz.client.model;

import lombok.Data;

@Data
public class PasswordModel {

    private String email;
    private String newPassword;
    private String oldPassword;
}
