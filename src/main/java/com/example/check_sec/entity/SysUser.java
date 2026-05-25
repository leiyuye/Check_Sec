package com.example.check_sec.entity;

import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.enums.UserStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_user", indexes = {
        @Index(name = "idx_user_phone", columnList = "phone", unique = true),
        @Index(name = "idx_user_credit_code", columnList = "creditCode", unique = true)
})
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String orgName;

    @Column(nullable = false, length = 32, unique = true)
    private String creditCode;

    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String contactPerson;

    @Column(nullable = false, length = 300)
    private String address;

    @Column(length = 100)
    private String email;

    private Long qualificationFileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ENABLED;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getCreditCode() { return creditCode; }
    public void setCreditCode(String creditCode) { this.creditCode = creditCode; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
