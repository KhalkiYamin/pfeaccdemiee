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
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reservationSeanceService.reserverSeance(seanceId, authentication.getName())
        );
    }

    @GetMapping("/seance/{seanceId}")
    public ResponseEntity<List<ReservationSeanceDto>> getReservationsBySeance(@PathVariable Long seanceId) {
        return ResponseEntity.ok(
                reservationSeanceService.getReservationsBySeance(seanceId)
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