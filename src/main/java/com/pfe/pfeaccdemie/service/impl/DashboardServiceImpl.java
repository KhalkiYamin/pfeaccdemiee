package com.pfe.pfeaccdemie.service.impl;

import org.springframework.stereotype.Service;

import com.pfe.pfeaccdemie.dto.DashboardDto;
import com.pfe.pfeaccdemie.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public DashboardDto getDashboardStats() {
        DashboardDto dto = new DashboardDto();
        dto.setTotalCoachs(38L);
        dto.setTotalAthletes(210L);
        dto.setTotalPayments(7240.0);
        dto.setTotalResources(64L);
        dto.setActivityRate(89);
        dto.setActiveSubscriptions(172L);
        dto.setPlannedSessions(46L);
        dto.setGlobalSatisfaction(96);
        return dto;
    }
}