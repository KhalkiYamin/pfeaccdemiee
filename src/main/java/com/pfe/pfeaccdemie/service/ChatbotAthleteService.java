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
            return new ChatResponseDto("لم يتم العثور على بيانات الرياضي.");
        }

        String lowerMessage = message.toLowerCase().trim();

        if (containsAny(lowerMessage,
                "recommend", "recommended", "suggest", "session for me",
                "اقترح", "اقتراح", "أنصح", "تنصحني", "حصة مناسبة", "الحصة المناسبة")) {
            return handleRecommendations(athlete, message);
        }

        if (containsAny(lowerMessage,
                "coach", "coaches",
                "مدرب", "مدربين", "المدربين", "قائمة المدربين")) {
            return handleCoaches();
        }

        if (containsAny(lowerMessage,
                "next session", "upcoming", "my sessions",
                "حصصي", "الحصص القادمة", "الحصص الجاية", "حصصي القادمة")) {
            return handleNextSessions(email, athlete);
        }

        if (containsAny(lowerMessage,
                "reservation", "booking", "reservations",
                "حجز", "حجوزات", "حجزي", "حجوزاتي")) {
            return handleReservations(email, athlete);
        }

        return new ChatResponseDto(
                "مرحبًا " + athlete.getNom() + "، يمكنني مساعدتك في الحجوزات، الحصص القادمة، المدربين، واقتراح الحصص المناسبة لك."
        );
    }

    private boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private ChatResponseDto handleReservations(String email, User athlete) {
        List<ReservationSeance> reservations = reservationSeanceRepository.findByAthleteEmail(email);

        List<ReservationSeance> filteredReservations = reservations.stream()
                .filter(r -> r.getStatut() != null &&
                        (r.getStatut().name().equals("ACCEPTEE") || r.getStatut().name().equals("EN_ATTENTE")))
                .toList();

        if (filteredReservations.isEmpty()) {
            return new ChatResponseDto("ليس لديك أي حجوزات نشطة حاليًا.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("مرحبًا ")
                .append(athlete.getNom())
                .append("، هذه هي حجوزاتك النشطة:\n");

        for (ReservationSeance reservation : filteredReservations) {
            sb.append("- الحصة: ")
                    .append(reservation.getSeance().getTheme())
                    .append(" | الحالة: ")
                    .append(reservation.getStatut());

            if (reservation.getSeance().getDateSeance() != null) {
                sb.append(" | التاريخ: ").append(reservation.getSeance().getDateSeance());
            }

            if (reservation.getSeance().getHeureSeance() != null) {
                sb.append(" | الوقت: ").append(reservation.getSeance().getHeureSeance());
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
            return new ChatResponseDto("ليس لديك أي حصص مقبولة قادمة حاليًا.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("مرحبًا ")
                .append(athlete.getNom())
                .append("، هذه هي حصصك القادمة:\n");

        for (ReservationSeance reservation : upcomingSessions) {
            sb.append("- الحصة: ")
                    .append(reservation.getSeance().getTheme());

            if (reservation.getSeance().getDateSeance() != null) {
                sb.append(" | التاريخ: ").append(reservation.getSeance().getDateSeance());
            }

            if (reservation.getSeance().getHeureSeance() != null) {
                sb.append(" | الوقت: ").append(reservation.getSeance().getHeureSeance());
            }

            sb.append("\n");
        }

        return new ChatResponseDto(sb.toString());
    }

    private ChatResponseDto handleCoaches() {
        List<User> coaches = userRepository.findByRole(Role.COACH);

        if (coaches.isEmpty()) {
            return new ChatResponseDto("لا يوجد مدربون متاحون في الوقت الحالي.");
        }

        StringBuilder sb = new StringBuilder("هذه قائمة المدربين:\n");

        for (User coach : coaches) {
            sb.append("- ")
                    .append(coach.getNom())
                    .append(" ")
                    .append(coach.getPrenom());

            if (coach.getEmail() != null) {
                sb.append(" | البريد الإلكتروني: ").append(coach.getEmail());
            }

            sb.append("\n");
        }

        return new ChatResponseDto(sb.toString());
    }

    private ChatResponseDto handleRecommendations(User athlete, String message) {
        if (athlete.getSport() == null || athlete.getNiveau() == null) {
            return new ChatResponseDto("ملفك الرياضي لا يحتوي بعد على معلومات كافية لاقتراح الحصص المناسبة.");
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
            return new ChatResponseDto("لا توجد حصص مقترحة متاحة حاليًا حسب مستواك ونوع الرياضة.");
        }

        StringBuilder data = new StringBuilder();
        data.append("اسم الرياضي: ").append(athlete.getNom()).append(" ").append(athlete.getPrenom()).append("\n");
        data.append("الرياضة: ").append(athlete.getSport().getTitle()).append("\n");
        data.append("المستوى: ").append(athlete.getNiveau()).append("\n");
        data.append("الحصص المتاحة:\n");

        for (Seance seance : recommendedSeances) {
            data.append("- ")
                    .append(seance.getTheme())
                    .append(" | التاريخ: ").append(seance.getDateSeance());

            if (seance.getHeureSeance() != null) {
                data.append(" | الوقت: ").append(seance.getHeureSeance());
            }

            if (seance.getLieu() != null) {
                data.append(" | المكان: ").append(seance.getLieu());
            }

            data.append("\n");
        }

        String prompt = """
                أنت مساعد ذكي خاص بأكاديمية رياضية.
                أجب دائمًا باللغة العربية بطريقة واضحة وودية.
                استعمل فقط البيانات التالية.
                اقترح أفضل الحصص المناسبة لهذا الرياضي.

                سؤال الرياضي:
                %s

                البيانات:
                %s
                """.formatted(message, data.toString());

        return groqChatService.askChatbot(prompt);
    }
}