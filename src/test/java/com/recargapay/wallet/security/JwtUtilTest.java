package com.recargapay.wallet.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    void testGenerateAndValidateToken() {
        JwtUtil jwtUtil = new JwtUtil();

        // secret must be at least 32 chars for HMAC SHA
        String secret = "super-secret-key-must-be-32-chars!";
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationSeconds", 300L);

        String token = jwtUtil.generateToken("user");
        assertNotNull(token);

        String username = jwtUtil.getUsernameFromToken(token);
        assertEquals("user", username);

        assertTrue(jwtUtil.validateToken(token));
    }
}
