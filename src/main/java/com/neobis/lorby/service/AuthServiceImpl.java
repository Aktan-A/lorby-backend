package com.neobis.lorby.service;

import com.neobis.lorby.dto.LoginRequestDto;
import com.neobis.lorby.dto.LoginResponseDto;
import com.neobis.lorby.dto.RegisterRequestDto;
import com.neobis.lorby.dto.RegisterResponseDto;
import com.neobis.lorby.enums.UserRole;
import com.neobis.lorby.exception.ResourceExistsException;
import com.neobis.lorby.exception.ResourceNotFoundException;
import com.neobis.lorby.model.User;
import com.neobis.lorby.repository.UserRepository;
import com.neobis.lorby.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        boolean usernameExists = userRepository.existsByUsername(registerRequestDto.getUsername());
        if (!usernameExists) {
            throw new ResourceExistsException(
                    "User with username " + registerRequestDto.getUsername() + " already exists.");
        }

        User user = new User();
        user.setUsername(registerRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setEmail(registerRequestDto.getEmail());
        user.setRole(UserRole.USER_ROLE);
        userRepository.save(user);
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
        String accessToken = jwtService.generateToken(userModel);
        return LoginResponseDto.builder().accessToken(accessToken).build();
    }
}
