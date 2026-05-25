package com.example.check_sec.controller;

import com.example.check_sec.common.enums.FilingStatus;
import com.example.check_sec.common.enums.SubmitterType;
import com.example.check_sec.common.response.ApiResponse;
import com.example.check_sec.dto.common.PageResult;
import com.example.check_sec.dto.filing.*;
import com.example.check_sec.service.FilingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filings")
public class FilingController {

    private final FilingService filingService;

    public FilingController(FilingService filingService) {
        this.filingService = filingService;
    }

    @GetMapping
    public ApiResponse<PageResult<FilingResponse>> list(
            @RequestParam(required = false) String filingUnitName,
            @RequestParam(required = false) SubmitterType submitterType,
            @RequestParam(required = false) FilingStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(filingService.list(filingUnitName, submitterType, status, page, size));
    }

    @PostMapping
    public ApiResponse<FilingResponse> create(@Valid @RequestBody FilingRequest request) {
        return ApiResponse.ok(filingService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<FilingDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(filingService.detail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<FilingResponse> update(@PathVariable Long id, @Valid @RequestBody FilingRequest request) {
        return ApiResponse.ok(filingService.update(id, request));
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<FilingResponse> submit(@PathVariable Long id) {
        return ApiResponse.ok(filingService.submit(id));
    }

    @PostMapping("/{id}/resubmit")
    public ApiResponse<FilingResponse> resubmit(@PathVariable Long id, @Valid @RequestBody FilingRequest request) {
        return ApiResponse.ok(filingService.resubmit(id, request));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<FilingResponse> approve(@PathVariable Long id, @RequestBody(required = false) ReviewRequest request) {
        if (request == null) request = new ReviewRequest();
        return ApiResponse.ok(filingService.approve(id, request));
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<FilingResponse> reject(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return ApiResponse.ok(filingService.reject(id, request));
    }

    @PostMapping("/{id}/admin-comment-file")
    public ApiResponse<FilingResponse> adminCommentFile(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return ApiResponse.ok(filingService.uploadAdminComment(id, request));
    }
}
