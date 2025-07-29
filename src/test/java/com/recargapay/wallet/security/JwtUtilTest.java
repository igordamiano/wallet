package com.recargapay.wallet.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

   @Test
    void testGenerateAndValidateToken() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "segredo-teste");

        String token = jwtUtil.generateToken("usuario");
        assertNotNull(token);

        String username = jwtUtil.getUsernameFromToken(token);
        assertEquals("usuario", username);

        assertTrue(jwtUtil.validateToken(token));
    }

}