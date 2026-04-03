package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.ReservationSeanceDto;
import com.pfe.pfeaccdemie.service.ReservationSeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationSeanceController {

    private final ReservationSeanceService reservationSeanceService;

    @GetMapping("/athlete/disponibles")
    public ResponseEntity<List<ReservationSeanceDto>> getSeancesDisponiblesPourAthlete(Authentication authentication) {
        return ResponseEntity.ok(
                reservationSeanceService.getSeancesDisponiblesPourAthlete(authentication.getName())
        );
    }

    @PostMapping("/seance/{seanceId}")
    public ResponseEntity<ReservationSeanceDto> reserverSeance(
            @PathVariable Long seanceId,
            @RequestParam Long coachId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reservationSeanceService.reserverSeance(seanceId, coachId, authentication.getName())
        );
    }

    @GetMapping("/seance/{seanceId}")
    public ResponseEntity<List<ReservationSeanceDto>> getReservationsBySeance(@PathVariable Long seanceId) {
        return ResponseEntity.ok(
                reservationSeanceService.getReservationsBySeance(seanceId)
        );
    }

    @GetMapping("/seance/{seanceId}/coach/{coachId}")
    public ResponseEntity<List<ReservationSeanceDto>> getReservationsByCoachAndSeance(
            @PathVariable Long seanceId,
            @PathVariable Long coachId
    ) {
        return ResponseEntity.ok(
                reservationSeanceService.getReservationsByCoachAndSeance(coachId, seanceId)
        );
    }

    @GetMapping("/seance/{seanceId}/coach/{coachId}/count")
    public ResponseEntity<Long> countReservationsByCoachAndSeance(
            @PathVariable Long seanceId,
            @PathVariable Long coachId
    ) {
        return ResponseEntity.ok(
                reservationSeanceService.countReservationsByCoachAndSeance(coachId, seanceId)
        );
    }

    @GetMapping("/seance/{seanceId}/coach/{coachId}/complete")
    public ResponseEntity<Boolean> isSeanceCompleteForCoach(
            @PathVariable Long seanceId,
            @PathVariable Long coachId
    ) {
        return ResponseEntity.ok(
                reservationSeanceService.isSeanceCompleteForCoach(coachId, seanceId)
        );
    }

    @PutMapping("/{reservationId}/accepter")
    public ResponseEntity<ReservationSeanceDto> accepterReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(
                reservationSeanceService.accepterReservation(reservationId)
        );
    }

    @PutMapping("/{reservationId}/refuser")
    public ResponseEntity<ReservationSeanceDto> refuserReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(
                reservationSeanceService.refuserReservation(reservationId)
        );
    }

    @GetMapping("/athlete/me")
    public ResponseEntity<List<ReservationSeanceDto>> getMesReservations(Authentication authentication) {
        return ResponseEntity.ok(
                reservationSeanceService.getMesReservations(authentication.getName())
        );
    }
}