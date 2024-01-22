package net.travale.model;


public class ResetPasswordRequest {

    private String password;
    private String confirmPassword;
    private String token;

    public String getPassword() {
        return password;
    }

    public ResetPasswordRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public ResetPasswordRequest setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }

    public String getToken() {
        return token;
    }

    public ResetPasswordRequest setToken(String token) {
        this.token = token;
        return this;
    }
}