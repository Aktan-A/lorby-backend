package com.neobis.lorby.security;

import com.neobis.lorby.exception.InvalidRefreshTokenException;
import com.neobis.lorby.exception.ResourceNotFoundException;
import com.neobis.lorby.model.RefreshToken;
import com.neobis.lorby.model.User;
import com.neobis.lorby.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.refresh-token-expire-minutes}")
    private Integer refreshTokenExpirationMinutes;

    public RefreshToken createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token,
                LocalDateTime.now().plusMinutes(refreshTokenExpirationMinutes),
                user
        );
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshTokenByToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        
        if (refreshToken.isEmpty()) {
            throw new ResourceNotFoundException("Refresh token not found.");
        }
        
        RefreshToken refreshTokenModel = refreshToken.get();
        
        if (LocalDateTime.now().isAfter(refreshTokenModel.getExpiresAt())) {
            refreshTokenRepository.delete(refreshTokenModel);
            throw new InvalidRefreshTokenException("Refresh token has expired.");
        }
        
        return refreshTokenModel;
    }
}
