package com.neobis.lorby.controller;

import com.neobis.lorby.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @Operation(
            summary = "Confirm email",
            description = "Verifies the users email address using the token which was sent during registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully confirmed"),
            @ApiResponse(responseCode = "400", description = "Email confirmation token not found")
    })
    @GetMapping("/confirm/{token}")
    public ResponseEntity<String> confirmEmail(@PathVariable("token") String token) {
        emailService.confirmEmailByToken(token);
        return ResponseEntity.ok("Email successfully confirmed.");
    }

}
