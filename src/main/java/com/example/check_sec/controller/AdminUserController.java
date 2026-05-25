package com.example.check_sec.controller;

import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.enums.UserStatus;
import com.example.check_sec.common.response.ApiResponse;
import com.example.check_sec.dto.auth.UserInfoResponse;
import com.example.check_sec.dto.common.PageResult;
import com.example.check_sec.dto.user.UserAdminRequest;
import com.example.check_sec.dto.user.UserListResponse;
import com.example.check_sec.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<PageResult<UserListResponse>> list(
            @RequestParam(required = false) RoleType role,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(userService.list(role, keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserInfoResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(userService.detail(id));
    }

    @PostMapping
    public ApiResponse<UserInfoResponse> create(@Valid @RequestBody UserAdminRequest request) {
        return ApiResponse.ok(userService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserInfoResponse> update(@PathVariable Long id, @Valid @RequestBody UserAdminRequest request) {
        return ApiResponse.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok("操作成功", null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        UserStatus status = UserStatus.valueOf(body.get("status"));
        userService.updateStatus(id, status);
        return ApiResponse.ok("状态更新成功", null);
    }

    @PutMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        userService.resetPassword(id, body != null ? body : Map.of());
        return ApiResponse.ok("密码重置成功", null);
    }
}
