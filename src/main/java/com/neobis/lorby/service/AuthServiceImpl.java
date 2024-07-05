package com.neobis.lorby.service;

import com.neobis.lorby.dto.LoginRequestDto;
import com.neobis.lorby.dto.LoginResponseDto;
import com.neobis.lorby.dto.RegisterRequestDto;
import com.neobis.lorby.dto.RegisterResponseDto;
import com.neobis.lorby.enums.UserRole;
import com.neobis.lorby.exception.InvalidRequestException;
import com.neobis.lorby.exception.ResourceExistsException;
import com.neobis.lorby.exception.ResourceNotFoundException;
import com.neobis.lorby.model.EmailConfirmationToken;
import com.neobis.lorby.model.User;
import com.neobis.lorby.repository.UserRepository;
import com.neobis.lorby.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${application.confirm-email-expiration-minutes}")
    private Integer confirmEmailExpirationMinutes;

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        boolean usernameExists = userRepository.existsByUsername(registerRequestDto.getUsername());
        if (usernameExists) {
            throw new ResourceExistsException(
                    "User with username " + registerRequestDto.getUsername() + " already exists.");
        }

        validateUsername(registerRequestDto.getUsername());
        validatePassword(registerRequestDto.getPassword());

        User user = new User();
        user.setUsername(registerRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setEmail(registerRequestDto.getEmail());
        user.setRole(UserRole.USER_ROLE);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        EmailConfirmationToken confirmationToken = emailService.saveConfirmationToken(
                new EmailConfirmationToken(
                        token,
                        LocalDateTime.now().plusMinutes(confirmEmailExpirationMinutes),
                        user
                )
        );
        emailService.send(registerRequestDto.getEmail(), confirmationToken.getToken());

        String accessToken = jwtService.generateToken(user);
        return RegisterResponseDto.builder().accessToken(accessToken).build();
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );
        Optional<User> user = userRepository.findByUsername(loginRequestDto.getUsername());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User with username " + loginRequestDto.getUsername() + "does not exist.");
        }

        User userModel = user.get();

        if (!userModel.getVerified()) {
            throw new ResourceNotFoundException("User did not verify their email.");
        }

        String accessToken = jwtService.generateToken(userModel);
        return LoginResponseDto.builder().accessToken(accessToken).build();
    }

    @Override
    public void validateUsername(String username) {
        for (int i = 0; i < username.length(); i++) {
            char value = username.charAt(i);
            if (!Character.isLetter(value)) {
                throw new InvalidRequestException("Username can only consist of uppercase and lowercase letters.");
            }
        }
    }

    @Override
    public void validatePassword(String password) {
        if (password.length() < 8) {
            throw new InvalidRequestException("Password is too short. Must be at least 8 characters.");
        }
        if (password.length() > 15) {
            throw new InvalidRequestException("Password is too long. Must be less than 15 characters.");
        }

        boolean containsUppercase = false;
        boolean containsLowercase = false;
        boolean containsNumber = false;
        boolean containsSpecialCharacter = false;
        for (int i = 0; i < password.length(); i++) {
            char value = password.charAt(i);

            if (!containsUppercase && Character.isUpperCase(value)) {
                containsUppercase = true;
            } else if (!containsLowercase && Character.isLowerCase(value)) {
                containsLowercase = true;
            } else if (!containsNumber && Character.isDigit(value)) {
                containsNumber = true;
            } else if (!containsSpecialCharacter && !Character.isLetterOrDigit(value)) {
                containsSpecialCharacter = true;
            }

        }

        if (!containsUppercase) {
            throw new InvalidRequestException("Password must contain an uppercase letter.");
        }

        if (!containsLowercase) {
            throw new InvalidRequestException("Password must contain a lowercase letter.");
        }

        if (!containsNumber) {
            throw new InvalidRequestException("Password must contain a number.");
        }

        if (!containsSpecialCharacter) {
            throw new InvalidRequestException("Password must contain a special character.");
        }

    }
}
