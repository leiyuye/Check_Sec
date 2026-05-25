package com.example.check_sec.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {

    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^(admin|1[3-9]\\d{9})$", message = "账号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
