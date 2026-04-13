package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.CoachProfileRequest;
import com.pfe.pfeaccdemie.dto.CoachProfileResponse;

public interface CoachProfileService {
    CoachProfileResponse getMyProfile(String email);
    CoachProfileResponse getCoachProfileById(Long coachId);
    CoachProfileResponse updateMyProfile(String email, CoachProfileRequest request);
}