package com.example.check_sec.controller;

import com.example.check_sec.common.response.ApiResponse;
import com.example.check_sec.dto.dashboard.DashboardStatsResponse;
import com.example.check_sec.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsResponse> stats() {
        return ApiResponse.ok(dashboardService.stats());
    }
}
