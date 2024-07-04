package com.neobis.lorby.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "email_confirmation_tokens")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"user"})
public class EmailConfirmationToken extends BaseEntity {

    @Column(nullable = false)
    private String token;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public EmailConfirmationToken(String token, LocalDateTime expiresAt, User user) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailConfirmationToken that = (EmailConfirmationToken) o;
        return Objects.equals(token, that.token) && Objects.equals(expiresAt, that.expiresAt) && Objects.equals(confirmedAt, that.confirmedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, expiresAt, confirmedAt);
    }
}
