package com.example.check_sec.dto.auth;

import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.enums.UserStatus;
import com.example.check_sec.entity.SysUser;

import java.time.LocalDateTime;

public class UserInfoResponse {

    private Long id;
    private String orgName;
    private String creditCode;
    private String phone;
    private String contactPerson;
    private String address;
    private String email;
    private Long qualificationFileId;
    private RoleType role;
    private UserStatus status;
    private Boolean mustChangePassword;
    private LocalDateTime createdAt;

    public static UserInfoResponse from(SysUser user) {
        UserInfoResponse r = new UserInfoResponse();
        r.id = user.getId();
        r.orgName = user.getOrgName();
        r.creditCode = user.getCreditCode();
        r.phone = user.getPhone();
        r.contactPerson = user.getContactPerson();
        r.address = user.getAddress();
        r.email = user.getEmail();
        r.qualificationFileId = user.getQualificationFileId();
        r.role = user.getRole();
        r.status = user.getStatus();
        r.mustChangePassword = user.getMustChangePassword();
        r.createdAt = user.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getCreditCode() { return creditCode; }
    public void setCreditCode(String creditCode) { this.creditCode = creditCode; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getQualificationFileId() { return qualificationFileId; }
    public void setQualificationFileId(Long qualificationFileId) { this.qualificationFileId = qualificationFileId; }
    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Boolean getMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(Boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
