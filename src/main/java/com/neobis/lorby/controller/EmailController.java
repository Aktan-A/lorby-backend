package com.neobis.lorby.controller;

import com.neobis.lorby.dto.ResendConfirmEmailRequestDto;
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
            description = "Verifies the users email address using the token which was sent during registration." +
                    " Meant to be accessed by the user directly from the email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully confirmed"),
            @ApiResponse(responseCode = "400", description = "Email confirmation token has expired"),
            @ApiResponse(responseCode = "404", description = "Email confirmation token not found")
    })
    @GetMapping("/confirm/{token}")
    public ResponseEntity<String> confirmEmail(@PathVariable("token") String token) {
        emailService.confirmEmailByToken(token);
        return ResponseEntity.ok("Email successfully confirmed.");
    }

    @Operation(
            summary = "Resend confirmation email",
            description = "Validates the request and sends the user another confirmation email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Confirmation email has been resent"),
            @ApiResponse(responseCode = "400", description = "Daily email limit has been reached. " +
                    "User has already verified their email."),
            @ApiResponse(responseCode = "404", description = "User was not found")
    })
    @PostMapping("/confirm/resend")
    public ResponseEntity<String> resendConfirmationEmail(
            @RequestBody ResendConfirmEmailRequestDto resendConfirmEmailRequestDto) {
        emailService.resendConfirmationEmail(
                resendConfirmEmailRequestDto.getEmail(),
                resendConfirmEmailRequestDto.getUsername());
        return ResponseEntity.ok("Confirmation email has been resent.");
    }

}
