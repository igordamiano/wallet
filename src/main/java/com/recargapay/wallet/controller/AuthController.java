package com.recargapay.wallet.controller;

import com.recargapay.wallet.model.User;
import com.recargapay.wallet.model.dto.LoginRequest;
import com.recargapay.wallet.repository.UserRepository;
import com.recargapay.wallet.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Log4j2
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations to authenticate users and generate JWT tokens.")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Operation(
            summary = "Authenticate user and generate JWT token",
            description = "Requires a registered user. Create your account first using the /user endpoint.",
            method = "POST"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT token generated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Valid LoginRequest request) {
        User user = userRepository.findByName(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user"));

        String token = jwtUtil.generateToken(user.getName());
        return Map.of("token", token);
    }
}
