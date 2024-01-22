package net.travale.model;

public class RefreshTokenRequest {
    private TokenPair token;

    public TokenPair getToken() {
        return this.token;
    }

    public void setToken(TokenPair token){
        this.token = token;
    }
}
