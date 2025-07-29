package com.recargapay.wallet.controller;

import com.recargapay.wallet.model.User;
import com.recargapay.wallet.repository.UserRepository;
import com.recargapay.wallet.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Operations to authenticate users and generate JWT tokens.")
public class AuthController {

    private JwtUtil jwtUtil;

    private final UserRepository userRepository;

    @Operation(
            summary = "Authenticate user and generate JWT token",
            description = "Requires a registered user. Create your account first using the /user endpoint.",
            method = "POST"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public String login(@RequestParam String username) {
        User user;
        try {
            user = userRepository.findByName(username).orElseThrow();
        } catch (Exception e) {
            log.error("Error finding user with username '{}'", username, e);
            throw new IllegalArgumentException("User not found: " + username);
        }
        return jwtUtil.generateToken(user.getName());
    }

}