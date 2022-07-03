package com.aplaz.client.service;

import com.aplaz.client.entity.PasswordResetToken;
import com.aplaz.client.entity.User;
import com.aplaz.client.entity.VerificationToken;
import com.aplaz.client.model.UserModel;
import com.aplaz.client.repository.PasswordResetRepository;
import com.aplaz.client.repository.UserRepository;
import com.aplaz.client.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository repo;

    @Autowired
    private VerificationTokenRepository repoToken;
    @Autowired
    private PasswordResetRepository repoPassword;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerUser(UserModel usermodel) {

        // First Create Object User
        User user = new User();

        // Set values of Model
        user.setEmail(usermodel.getEmail());
        user.setFirstname(usermodel.getFirstname());
        user.setLastname(usermodel.getLastname());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(usermodel.getPassword()));

        repo.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(user,token);
        repoToken.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = repoToken.findByToken(token);

        if(verificationToken == null){
            return "invalid";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <=0){
            repoToken.delete(verificationToken);
            return "Token expired";
        }

        user.setEnabled(true);
        repo.save(user);
        return "valid";
    }

    /***        Generate a new token
            find old token and replace it with a new
     */
    @Override
    public VerificationToken generateNewVerificationToken(String old_token) {

        VerificationToken verificationToken = repoToken.findByToken(old_token);
        verificationToken.setToken(UUID.randomUUID().toString());

        repoToken.save(verificationToken);

        return verificationToken;
    }

    @Override
    public User findUserByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user,token);
        //save it in database
        repoPassword.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {

        //Find model of PasswordResetToken and check for validate Token
        PasswordResetToken passwordResetToken = repoPassword.findByToken(token);

        if(passwordResetToken == null){
            return "invalid";
        }

        //User user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();

        if(passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime() <=0 ){
            repoPassword.delete(passwordResetToken);
            return "Token expired";
        }

        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(repoPassword.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        repo.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }


}
