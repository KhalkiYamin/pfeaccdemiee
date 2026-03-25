package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.PresenceRequest;
import com.pfe.pfeaccdemie.dto.PresenceResponse;
import com.pfe.pfeaccdemie.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presences")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/seance/{seanceId}")
    public List<PresenceResponse> getPresencesBySeance(@PathVariable Long seanceId) {
        return presenceService.getPresencesBySeance(seanceId);
    }

    @PutMapping("/seance/{seanceId}/athlete/{athleteId}")
    public PresenceResponse updatePresence(
            @PathVariable Long seanceId,
            @PathVariable Long athleteId,
            @RequestBody PresenceRequest request
    ) {
        return presenceService.updatePresence(seanceId, athleteId, request);
    }
}