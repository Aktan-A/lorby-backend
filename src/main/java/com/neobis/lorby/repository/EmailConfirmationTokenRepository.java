package com.neobis.lorby.repository;

import com.neobis.lorby.model.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

    Optional<EmailConfirmationToken> findByToken(String token);

    @Query("SELECT COUNT(ect) FROM EmailConfirmationToken ect WHERE ect.user.email = :email AND ect.createdAt BETWEEN :startDate AND :endDate")
    int countByEmailBetweenDates(LocalDateTime startDate, LocalDateTime endDate, String email);

    @Query("SELECT ect FROM EmailConfirmationToken ect WHERE ect.user.email = :email ORDER BY ect.createdAt DESC LIMIT 1")
    Optional<EmailConfirmationToken> findLatestByEmail(String email);

}
