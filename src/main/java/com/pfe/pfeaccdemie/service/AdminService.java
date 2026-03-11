package com.pfe.pfeaccdemie.service;

import java.util.List;

import com.pfe.pfeaccdemie.dto.AdminUserDto;
import com.pfe.pfeaccdemie.dto.DashboardStatsDto;

public interface AdminService {

    List<AdminUserDto> getAllUsers();

    List<AdminUserDto> getAthletes();

    List<AdminUserDto> getCoaches();

    List<AdminUserDto> getPendingCoaches();

    AdminUserDto approveCoach(Long id);

    void deleteUser(Long id);

    DashboardStatsDto getDashboardStats();
}