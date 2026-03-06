package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.AuthResponse;
import com.pfe.pfeaccdemie.dto.LoginRequest;
import com.pfe.pfeaccdemie.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String verifyEmail(String token);

    String approveCoach(Long coachId);
}