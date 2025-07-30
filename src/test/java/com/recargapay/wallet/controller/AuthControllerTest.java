package com.recargapay.wallet.controller;

import com.recargapay.wallet.model.User;
import com.recargapay.wallet.model.dto.LoginRequest;
import com.recargapay.wallet.repository.UserRepository;
import com.recargapay.wallet.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Test
    void login() {
        UserRepository userRepository = mock(UserRepository.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);

        String username = "userTest";
        User user = new User();
        user.setName(username);

        LoginRequest request = new LoginRequest();
        request.setUsername(username);

        AuthController controller = new AuthController(jwtUtil, userRepository);

        when(userRepository.findByName(username)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(username)).thenReturn("fake-token");

        Map<String, String> result = controller.login(request);
        assertEquals("fake-token", result.get("token"));
    }


    @Test
    void login_ResponseStatusException_whenUserNotFound() {
        UserRepository userRepository = mock(UserRepository.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);

        LoginRequest request = new LoginRequest();
        request.setUsername("inexistente");

        when(userRepository.findByName("inexistente")).thenReturn(Optional.empty());

        AuthController controller = new AuthController(jwtUtil, userRepository);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.login(request));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Invalid user", ex.getReason());


    }


}