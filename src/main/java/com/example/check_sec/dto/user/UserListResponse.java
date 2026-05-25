package com.example.check_sec.dto.user;

import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.enums.UserStatus;
import com.example.check_sec.entity.SysUser;

import java.time.LocalDateTime;

public class UserListResponse {

    private Long id;
    private String orgName;
    private RoleType role;
    private String roleLabel;
    private String phone;
    private String creditCode;
    private String contactPerson;
    private UserStatus status;
    private String statusLabel;
    private LocalDateTime createdAt;

    public static UserListResponse from(SysUser user) {
        UserListResponse r = new UserListResponse();
        r.id = user.getId();
        r.orgName = user.getOrgName();
        r.role = user.getRole();
        r.roleLabel = roleLabel(user.getRole());
        r.phone = user.getPhone();
        r.creditCode = user.getCreditCode();
        r.contactPerson = user.getContactPerson();
        r.status = user.getStatus();
        r.statusLabel = user.getStatus() == UserStatus.ENABLED ? "启用" : "禁用";
        r.createdAt = user.getCreatedAt();
        return r;
    }

    private static String roleLabel(RoleType role) {
        return switch (role) {
            case ADMIN -> "管理员";
            case FILING_UNIT -> "备案单位";
            case TESTING_AGENCY -> "测评机构";
        };
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
    public String getRoleLabel() { return roleLabel; }
    public void setRoleLabel(String roleLabel) { this.roleLabel = roleLabel; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCreditCode() { return creditCode; }
    public void setCreditCode(String creditCode) { this.creditCode = creditCode; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
