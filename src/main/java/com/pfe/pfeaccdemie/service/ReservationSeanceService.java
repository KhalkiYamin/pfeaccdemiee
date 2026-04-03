package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.ReservationSeanceDto;

import java.util.List;

public interface ReservationSeanceService {

    List<ReservationSeanceDto> getSeancesDisponiblesPourAthlete(String email);

    ReservationSeanceDto reserverSeance(Long seanceId, Long coachId, String email);

    List<ReservationSeanceDto> getReservationsBySeance(Long seanceId);

    List<ReservationSeanceDto> getReservationsByCoachAndSeance(Long coachId, Long seanceId);

    long countReservationsByCoachAndSeance(Long coachId, Long seanceId);

    boolean isSeanceCompleteForCoach(Long coachId, Long seanceId);

    ReservationSeanceDto accepterReservation(Long reservationId);

    ReservationSeanceDto refuserReservation(Long reservationId);

    List<ReservationSeanceDto> getMesReservations(String email);
}