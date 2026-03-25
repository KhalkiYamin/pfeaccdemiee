package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.ReservationSeanceDto;

import java.util.List;

public interface ReservationSeanceService {

    List<ReservationSeanceDto> getSeancesDisponiblesPourAthlete(String email);

    ReservationSeanceDto reserverSeance(Long seanceId, String email);

    List<ReservationSeanceDto> getReservationsBySeance(Long seanceId);

    ReservationSeanceDto accepterReservation(Long reservationId);

    ReservationSeanceDto refuserReservation(Long reservationId);

    List<ReservationSeanceDto> getMesReservations(String email);
}