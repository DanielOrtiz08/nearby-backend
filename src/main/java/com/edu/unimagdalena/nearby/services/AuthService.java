package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.CuentaUsuario;

import java.util.Map;

public interface AuthService {
    CuentaUsuario registerStudent(Map<String, Object> payload);
    CuentaUsuario registerOwner(Map<String, Object> payload);
    Map<String, Object> login(Map<String, Object> credentials);
    void logout(String authHeader, Map<String, Object> payload);
    boolean verifyEmail(Map<String, Object> payload);
    boolean resendOtp(Map<String, Object> payload);
    boolean forgotPassword(Map<String, Object> payload);
    boolean resetPassword(Map<String, Object> payload);
    CuentaUsuario me(String authHeader);
    Map<String, Object> refreshToken(Map<String, Object> payload);
}
