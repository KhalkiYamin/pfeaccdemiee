package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.AuthResponse;
import com.pfe.pfeaccdemie.dto.LoginRequest;
import com.pfe.pfeaccdemie.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String verifyEmail(String token);

    String approveCoach(Long coachId);
    void sendResetCode(String email);
    void verifyResetCode(String email, String code);
    void resetPassword(String email, String code, String newPassword);
}