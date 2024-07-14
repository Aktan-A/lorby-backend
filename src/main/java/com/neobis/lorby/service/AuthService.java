package com.neobis.lorby.service;


import com.neobis.lorby.dto.*;

public interface AuthService {
    void register(RegisterRequestDto registerRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    void validateRegisterDetails(RegisterRequestDto registerRequestDto);

    void validateUsername(String username);

    void validatePassword(String password);

    RefreshTokenResponseDto refreshAccessToken(RefreshTokenRequestDto refreshTokenRequestDto);
}
