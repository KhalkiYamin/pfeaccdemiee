package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.AdminUserDto;
import com.pfe.pfeaccdemie.dto.DashboardStatsDto;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.PresenceRepository;
import com.pfe.pfeaccdemie.repositories.ReservationSeanceRepository;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.AdminService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final SeanceRepository seanceRepository;
    private final ReservationSeanceRepository reservationSeanceRepository;
    private final PresenceRepository presenceRepository;

    @Override
    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminUserDto> getAthletes() {
        return userRepository.findByRole(Role.ATHLETE)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminUserDto> getCoaches() {
        return userRepository.findByRole(Role.COACH)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminUserDto> getPendingCoaches() {
        return userRepository.findByRoleAndAdminApproved(Role.COACH, false)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminUserDto approveCoach(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        if (user.getRole() != Role.COACH) {
            throw new IllegalArgumentException("Cet utilisateur n'est pas un coach");
        }

        user.setAdminApproved(true);
        userRepository.save(user);

        return mapToDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        if (user.getRole() == Role.ATHLETE) {
            reservationSeanceRepository.deleteByAthleteId(id);
            presenceRepository.deleteByAthleteId(id);
        }

        if (user.getRole() == Role.COACH) {
            List<Seance> seancesCoach = seanceRepository.findByCoachId(id);

            for (Seance seance : seancesCoach) {
                presenceRepository.deleteBySeanceId(seance.getId());
                reservationSeanceRepository.deleteBySeanceId(seance.getId());
                seanceRepository.delete(seance);
            }
        }

        userRepository.delete(user);
    }

    @Override
    public DashboardStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalAthletes = userRepository.countByRole(Role.ATHLETE);
        long totalCoaches = userRepository.countByRole(Role.COACH);
        long pendingCoaches = userRepository.countByRoleAndAdminApproved(Role.COACH, false);

        return DashboardStatsDto.builder()
                .totalUsers(totalUsers)
                .totalAthletes(totalAthletes)
                .totalCoaches(totalCoaches)
                .pendingCoaches(pendingCoaches)
                .build();
    }

    private AdminUserDto mapToDto(User user) {
        return AdminUserDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole().name())
                .specialite(user.getSpecialite() != null ? user.getSpecialite().getTitle() : null)
                .sport(user.getSport() != null ? user.getSport().getTitle() : null)
                .statut(user.getRole() == Role.COACH
                        ? (user.isAdminApproved() ? "VALIDÉ" : "EN ATTENTE")
                        : "VALIDÉ")
                .enabled(user.isEnabled())
                .build();
    }
}