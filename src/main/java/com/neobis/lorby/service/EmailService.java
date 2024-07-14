package com.neobis.lorby.service;

import com.neobis.lorby.model.EmailConfirmationToken;

public interface EmailService {

    EmailConfirmationToken saveConfirmationToken(EmailConfirmationToken emailConfirmationToken);
    void confirmEmailByToken(String token);
    void sendConfirmationEmail(String to, String token);
    void resendConfirmationEmail(String email, String username);

}
