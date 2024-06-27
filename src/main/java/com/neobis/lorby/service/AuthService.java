package com.neobis.lorby.service;


import com.neobis.lorby.dto.LoginRequestDto;
import com.neobis.lorby.dto.LoginResponseDto;
import com.neobis.lorby.dto.RegisterRequestDto;
import com.neobis.lorby.dto.RegisterResponseDto;

public interface AuthService {
    RegisterResponseDto register(RegisterRequestDto registerRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);
}
