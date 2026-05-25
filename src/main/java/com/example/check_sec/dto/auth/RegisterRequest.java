package com.example.check_sec.dto.auth;

import com.example.check_sec.common.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotNull(message = "注册角色不能为空")
    private RoleType role;

    @NotBlank(message = "单位/机构名称不能为空")
    private String orgName;

    @NotBlank(message = "统一社会信用代码不能为空")
    private String creditCode;

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度6-32位")
    private String password;

    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    @NotBlank(message = "联系地址不能为空")
    private String address;

    private String email;
    private Long qualificationFileId;

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
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
}
