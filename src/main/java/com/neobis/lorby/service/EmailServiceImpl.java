package com.neobis.lorby.service;

import com.neobis.lorby.exception.ResourceNotFoundException;
import com.neobis.lorby.model.EmailConfirmationToken;
import com.neobis.lorby.model.User;
import com.neobis.lorby.repository.EmailConfirmationTokenRepository;
import com.neobis.lorby.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final UserRepository userRepository;

    @Value("${application.base-url}")
    private String baseUrl;

    @Override
    public EmailConfirmationToken saveConfirmationToken(EmailConfirmationToken emailConfirmationToken) {
        return emailConfirmationTokenRepository.save(emailConfirmationToken);
    }

    @Override
    @Transactional
    public void confirmEmailByToken(String token) {
        Optional<EmailConfirmationToken> emailConfirmationToken = emailConfirmationTokenRepository.findByToken(token);
        if (emailConfirmationToken.isEmpty()) {
            throw new ResourceNotFoundException("Email confirmation token not found.");
        }

        EmailConfirmationToken emailToken = emailConfirmationToken.get();
        emailToken.setConfirmedAt(LocalDateTime.now());
        emailConfirmationTokenRepository.save(emailToken);

        User user = emailToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
    }

    @Override
    @Async
    public void send(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirm your email");
        String confirmationLink = baseUrl + "/api/v1/email/confirm/" + token;
        String text = "Please confirm your email using the following link:\n" + confirmationLink;
        message.setText(text);
        mailSender.send(message);
    }
}
