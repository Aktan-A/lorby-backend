package com.neobis.lorby.service;

import com.neobis.lorby.exception.InvalidRequestException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final UserRepository userRepository;

    @Value("${application.base-url}")
    private String baseUrl;

    @Value("${application.confirm-email-daily-limit}")
    private Integer confirmEmailDailyLimit;

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

        if (emailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Email confirmation token has expired.");
        }

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

    @Override
    public void resendConfirmationEmail(String email, String username) {
        int tokenCount = emailConfirmationTokenRepository.countTokensByEmailBetweenDates(
                LocalDate.now().atTime(LocalTime.MIN),
                LocalDate.now().atTime(LocalTime.MAX),
                email
        );

        if (tokenCount >= confirmEmailDailyLimit) {
            throw new InvalidRequestException(
                    String.format("Email verification limit for the email %s has been reached.", email));
        }

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException(
                    String.format("User with username %s was not found.", username));
        }

        User userModel = user.get();

        if (userModel.getVerified()) {
            throw new InvalidRequestException("User has already verified their email.");
        }

        String token = UUID.randomUUID().toString();
        EmailConfirmationToken confirmationToken = saveConfirmationToken(
                new EmailConfirmationToken(
                        token,
                        LocalDateTime.now().plusMinutes(15),
                        userModel
                )
        );
        send(email, confirmationToken.getToken());
    }
}
