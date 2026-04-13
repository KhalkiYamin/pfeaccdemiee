package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.ChatResponseDto;
import com.pfe.pfeaccdemie.entities.ReservationSeance;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.ReservationSeanceRepository;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChatbotAthleteService {

    private final UserRepository userRepository;
    private final ReservationSeanceRepository reservationSeanceRepository;
    private final SeanceRepository seanceRepository;
    private final GroqChatService groqChatService;

    public ChatbotAthleteService(UserRepository userRepository,
                                 ReservationSeanceRepository reservationSeanceRepository,
                                 SeanceRepository seanceRepository,
                                 GroqChatService groqChatService) {
        this.userRepository = userRepository;
        this.reservationSeanceRepository = reservationSeanceRepository;
        this.seanceRepository = seanceRepository;
        this.groqChatService = groqChatService;
    }

    public ChatResponseDto handleAthleteMessage(String message, String email) {
        User athlete = userRepository.findByEmail(email).orElse(null);

        if (athlete == null) {
            return new ChatResponseDto("Athlete not found.");
        }

        String lowerMessage = message.toLowerCase().trim();

        if (lowerMessage.contains("recommend")
                || lowerMessage.contains("recommended")
                || lowerMessage.contains("suggest")
                || lowerMessage.contains("session for me")) {
            return handleRecommendations(athlete, message);
        }

        if (lowerMessage.contains("coach")) {
            return handleCoaches();
        }

        if (lowerMessage.contains("next session")
                || lowerMessage.contains("upcoming")
                || lowerMessage.contains("my sessions")) {
            return handleNextSessions(email, athlete);
        }

        if (lowerMessage.contains("reservation")
                || lowerMessage.contains("booking")) {
            return handleReservations(email, athlete);
        }

        return new ChatResponseDto(
                "Hello " + athlete.getNom() + ", I can help you with your reservations, upcoming sessions, coaches, and recommended sessions."
        );
    }

    private ChatResponseDto handleReservations(String email, User athlete) {
        List<ReservationSeance> reservations = reservationSeanceRepository.findByAthleteEmail(email);

        List<ReservationSeance> filteredReservations = reservations.stream()
                .filter(r -> r.getStatut() != null &&
                        (r.getStatut().name().equals("ACCEPTEE") || r.getStatut().name().equals("EN_ATTENTE")))
                .toList();

        if (filteredReservations.isEmpty()) {
            return new ChatResponseDto("You do not have any active reservations yet.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Hello ")
                .append(athlete.getNom())
                .append(", here are your active reservations:\n");

        for (ReservationSeance reservation : filteredReservations) {
            sb.append("- Session: ")
                    .append(reservation.getSeance().getTheme())
                    .append(" | Status: ")
                    .append(reservation.getStatut());

            if (reservation.getSeance().getDateSeance() != null) {
                sb.append(" | Date: ").append(reservation.getSeance().getDateSeance());
            }

            if (reservation.getSeance().getHeureSeance() != null) {
                sb.append(" | Time: ").append(reservation.getSeance().getHeureSeance());
            }

            sb.append("\n");
        }

        return new ChatResponseDto(sb.toString());
    }

    private ChatResponseDto handleNextSessions(String email, User athlete) {
        List<ReservationSeance> reservations = reservationSeanceRepository.findByAthleteEmail(email);
        LocalDate today = LocalDate.now();

        List<ReservationSeance> upcomingSessions = reservations.stream()
                .filter(r -> r.getStatut() != null && r.getStatut().name().equals("ACCEPTEE"))
                .filter(r -> r.getSeance() != null && r.getSeance().getDateSeance() != null)
                .filter(r -> !r.getSeance().getDateSeance().isBefore(today))
                .toList();

        if (upcomingSessions.isEmpty()) {
            return new ChatResponseDto("You do not have any upcoming accepted sessions.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Hello ")
                .append(athlete.getNom())
                .append(", here are your upcoming sessions:\n");

        for (ReservationSeance reservation : upcomingSessions) {
            sb.append("- Session: ")
                    .append(reservation.getSeance().getTheme());

            if (reservation.getSeance().getDateSeance() != null) {
                sb.append(" | Date: ").append(reservation.getSeance().getDateSeance());
            }

            if (reservation.getSeance().getHeureSeance() != null) {
                sb.append(" | Time: ").append(reservation.getSeance().getHeureSeance());
            }

            sb.append("\n");
        }

        return new ChatResponseDto(sb.toString());
    }

    private ChatResponseDto handleCoaches() {
        List<User> coaches = userRepository.findByRole(Role.COACH);

        if (coaches.isEmpty()) {
            return new ChatResponseDto("No coaches are available at the moment.");
        }

        StringBuilder sb = new StringBuilder("Here is the list of coaches:\n");

        for (User coach : coaches) {
            sb.append("- ")
                    .append(coach.getNom())
                    .append(" ")
                    .append(coach.getPrenom());

            if (coach.getEmail() != null) {
                sb.append(" | Email: ").append(coach.getEmail());
            }

            sb.append("\n");
        }

        return new ChatResponseDto(sb.toString());
    }

    private ChatResponseDto handleRecommendations(User athlete, String message) {
        if (athlete.getSport() == null || athlete.getNiveau() == null) {
            return new ChatResponseDto("Your profile does not have enough information yet for session recommendations.");
        }

        List<Seance> seances = seanceRepository.findBySportIdAndNiveau(
                athlete.getSport().getId(),
                athlete.getNiveau()
        );

        LocalDate today = LocalDate.now();

        List<Seance> recommendedSeances = seances.stream()
                .filter(s -> s.getDateSeance() != null)
                .filter(s -> !s.getDateSeance().isBefore(today))
                .limit(5)
                .toList();

        if (recommendedSeances.isEmpty()) {
            return new ChatResponseDto("No recommended sessions are available for your level and sport right now.");
        }

        StringBuilder data = new StringBuilder();
        data.append("Athlete name: ").append(athlete.getNom()).append(" ").append(athlete.getPrenom()).append("\n");
        data.append("Sport: ").append(athlete.getSport().getTitle()).append("\n");
        data.append("Level: ").append(athlete.getNiveau()).append("\n");
        data.append("Available sessions:\n");

        for (Seance seance : recommendedSeances) {
            data.append("- ")
                    .append(seance.getTheme())
                    .append(" | Date: ").append(seance.getDateSeance());

            if (seance.getHeureSeance() != null) {
                data.append(" | Time: ").append(seance.getHeureSeance());
            }

            if (seance.getLieu() != null) {
                data.append(" | Location: ").append(seance.getLieu());
            }

            data.append("\n");
        }

        String prompt = """
                You are a sports academy assistant.
                Answer the athlete in a clear and friendly way using only the data below.
                Recommend the most suitable sessions for the athlete.

                Athlete question:
                %s

                Data:
                %s
                """.formatted(message, data.toString());

        return groqChatService.askChatbot(prompt);
    }
}