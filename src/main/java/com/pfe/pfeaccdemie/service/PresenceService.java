package com.pfe.pfeaccdemie.service;
import com.pfe.pfeaccdemie.dto.PresenceRequest;
import com.pfe.pfeaccdemie.dto.PresenceResponse;

import java.util.List;
public interface PresenceService {
    List<PresenceResponse> getPresencesBySeance(Long seanceId);
    PresenceResponse updatePresence(Long seanceId, Long athleteId, PresenceRequest request);
}
