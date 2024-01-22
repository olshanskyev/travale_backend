package net.travale.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenPair {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;


    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public TokenPair setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public TokenPair setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
