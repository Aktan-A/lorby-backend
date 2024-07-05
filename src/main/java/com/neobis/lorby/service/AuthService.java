package com.neobis.lorby.service;


import com.neobis.lorby.dto.LoginRequestDto;
import com.neobis.lorby.dto.LoginResponseDto;
import com.neobis.lorby.dto.RegisterRequestDto;

public interface AuthService {
    void register(RegisterRequestDto registerRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    void validateUsername(String username);

    void validatePassword(String password);
}
