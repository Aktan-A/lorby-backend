package com.neobis.lorby.dto;

import com.neobis.lorby.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private UserRole role;
    private Boolean verified;
    private Boolean deleted;
    private LocalDateTime createdAt;
}
