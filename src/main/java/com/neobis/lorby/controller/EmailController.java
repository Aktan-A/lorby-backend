package com.neobis.lorby.controller;

import com.neobis.lorby.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/confirm/{token}")
    public ResponseEntity<String> confirmEmail(@PathVariable("token") String token) {
        emailService.confirmEmailByToken(token);
        return ResponseEntity.ok("Email successfully confirmed.");
    }

}
