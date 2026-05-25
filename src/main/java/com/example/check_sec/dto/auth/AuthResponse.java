package com.example.check_sec.dto.auth;

public class AuthResponse {

    private String token;
    private UserInfoResponse user;
    private boolean suggestChangePassword;

    public AuthResponse(String token, UserInfoResponse user, boolean suggestChangePassword) {
        this.token = token;
        this.user = user;
        this.suggestChangePassword = suggestChangePassword;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserInfoResponse getUser() { return user; }
    public void setUser(UserInfoResponse user) { this.user = user; }
    public boolean isSuggestChangePassword() { return suggestChangePassword; }
    public void setSuggestChangePassword(boolean suggestChangePassword) { this.suggestChangePassword = suggestChangePassword; }
}
