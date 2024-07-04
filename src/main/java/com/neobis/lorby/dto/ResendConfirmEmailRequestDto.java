package com.neobis.lorby.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResendConfirmEmailRequestDto {

    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Username is required")
    private String username;

}
