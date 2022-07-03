package com.aplaz.client.controller;

import com.aplaz.client.entity.User;
import com.aplaz.client.entity.VerificationToken;
import com.aplaz.client.events.RegistrationConfirmation;
import com.aplaz.client.model.PasswordModel;
import com.aplaz.client.model.UserModel;
import com.aplaz.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController()
@Slf4j
//@RequestMapping("/api/")
public class RegistrationController extends UserModel{

    @Autowired
    private UserService user_service;

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     *  1)  First Create User
     *  2)  For Enable User send email verification
     *      An event occur that publish a) ```User``` , b) ```URL```
     *      for verification email.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserModel usermodel, final HttpServletRequest request){

        //validate passwords
        int valid_pwd = validate_passwords(usermodel);
        if(valid_pwd == 0){
            return new ResponseEntity<>("Passwords must match", HttpStatus.BAD_REQUEST);
        }

        //1)--create user
        User user = user_service.registerUser(usermodel);

        //2)--send email verification

        publisher.publishEvent(new RegistrationConfirmation(user,applicationUrl(request)));

        return new ResponseEntity<>("user created", HttpStatus.CREATED);
    }

    /************** VERIFY REGISTRATION *****************
     *
     *  when user created then an event occur and a verification email sends
     *  to the user. { Have to create email logic. We just prin the result in terminal }
     *
     */

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String receive_token){
        /*
            Get token from db and compare with receive_token.
            If token == receive_token then enable user account
         */
        String token = user_service.validateVerificationToken(receive_token);
        if(token.equalsIgnoreCase("valid")){
            return "User Verify Successfully";
        }

        return "Bad User";
    }

    /************** RESEND REGISTRATION EMAIL *****************
     *
     *  If the user forgot see email for confirmation or
     *  if token changed user can resend email
     *  1) Generate a new token if user press the button
     */

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String old_token, HttpServletRequest request){
        //create a new token
        VerificationToken verificationToken = user_service.generateNewVerificationToken(old_token);

        //Get user by token
        User user = verificationToken.getUser();

        //send Email again
        resendVerificationTokenEmail(user,applicationUrl(request),verificationToken);
        return "Verification Link Resend";
    }
    /************** RESET PASSWORD BY EMAIL *****************
     *
     *  if the User want reset password. Then
     *  1) Send email to that user with a token that it is for this user
     *  2) If user click in Link at email then reset his password with a new one
     *
     */

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request){

        //first we get email and try find if there is a user with this email
        User user = user_service.findUserByEmail(passwordModel.getEmail());
        String url ="";
        //create a token for that user and save it for password reset
        if(user!=null){
            String token = UUID.randomUUID().toString();
            user_service.createPasswordResetTokenForUser(user,token);

            //after save the User creates the URL for reset Password and send email
            url = passwordResetTokenEmail(user,applicationUrl(request),token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel){
        //first Verify that token is our
        String result = user_service.validatePasswordResetToken(token);

        //if token is valid get new password and save it in database
        if(!result.equalsIgnoreCase("valid")){
            return "Invalid token";
        }

        Optional<User> user = user_service.getUserByPasswordResetToken(token);
        if(user.isPresent()){
            //change password
            user_service.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password Reset Successfully";
        }else {
            return "Invalid User";
        }

    }

    /************** CHANGE PASSWORD *****************
     *
     *  if the User want change password. Then
     *  1) find User by email
     *  2) check if old_password is it from the database.
     *      if old_password == user.password then user is valid and after
     *  3) Save new_password in database
     *
     */

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        //find user by email
        User user = user_service.findUserByEmail(passwordModel.getEmail());
        if (user == null){
            return "There is not user with this Email";
        }

        //check if given password is valid with password in database
        if(!user_service.checkIfValidOldPassword(user, passwordModel.getOldPassword())){
            return "Invalid Old Password";
        }

        //save new password
        user_service.changePassword(user, passwordModel.getNewPassword());
        return "Password Changed Successfully";

    }

    private String passwordResetTokenEmail(User user, String applicationUrl, String token) {
        String url = applicationUrl +"/savePassword?token=" +token;

        log.info("Click here to reset your Password: {}",url);
        return url;
    }

    private void resendVerificationTokenEmail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl +"/verifyRegistration?token="+verificationToken.getToken();

        log.info("Click here to verify your account: {}",url);
    }


    private String applicationUrl(HttpServletRequest request){
        return "http://" + request.getServerName() + ":" +request.getServerPort() + request.getContextPath();
    }


}
