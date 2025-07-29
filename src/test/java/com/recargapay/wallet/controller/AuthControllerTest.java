package com.recargapay.wallet.controller;

import com.recargapay.wallet.model.User;
import com.recargapay.wallet.repository.UserRepository;
import com.recargapay.wallet.security.JwtUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Test
    void login() {

        // Given
        UserRepository userRepository = mock(UserRepository.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        String username = "usuarioTeste";
        User user = new User();
        user.setName(username);
        AuthController controller = new AuthController(jwtUtil, userRepository);

        // When
        when(userRepository.findByName(username))
                .thenReturn(java.util.Optional.of(user));
        when(jwtUtil.generateToken(username))
                .thenReturn("fake-jwt-token");

        // Then
        String token = controller.login(username);
        assertEquals("fake-jwt-token", token);

    }

}