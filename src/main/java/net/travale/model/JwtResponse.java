package net.travale.model;

public class JwtResponse {

    private TokenPair token;

    public JwtResponse(TokenPair tokenPair) {
        this.token = tokenPair;
    }
    public JwtResponse() {}
    public void setToken(TokenPair token) {
        this.token = token;
    }
    public TokenPair getToken() {
        return this.token;
    }

}