package com.aplaz.client.service;

import com.aplaz.client.entity.User;
import com.aplaz.client.entity.VerificationToken;
import com.aplaz.client.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel usermodel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String old_token);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);
}
